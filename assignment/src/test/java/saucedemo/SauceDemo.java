package saucedemo;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;

interface TestData{
	public static final String URL = "https://www.saucedemo.com/";
	public static final String Firstname="Basavaraj";
	public static final String Lastname="Kichadi";
	public static final String Postalcode="591344";
}
interface Locators{
	public static final By username = By.xpath("//input[@placeholder='Username']"); 
	public static final By password = By.xpath("//input[@placeholder='Password']");
	public static final By loginBtn = By.xpath("//input[@type='submit']");
	public static final By inventoryPage = By.xpath("//span[text()='Products']");
	public static final By lockedErrorMsg = By.xpath("//h3[contains(text(), \"Sorry, this user has been locked out.\")]");
	public static final By productImages = By.xpath("//div[@id='inventory_container']//img");
	public static final By singleItem = By.xpath("//button[@id='add-to-cart-sauce-labs-backpack']");
	public static final By cartBadgeCount = By.xpath("//span[@class='shopping_cart_badge']");
	public static final By removeBtn = By.xpath("//button[text()='Remove']");
	public static final By afterRemoving = By.xpath("//div[@class='cart_list']/div[contains(@class, 'cart_item')]");
	public static final By checkoutbutton=By.xpath("//*[@id='checkout']");
	public static final By firstname=By.xpath("//*[@id='first-name']");
	public static final By lastname=By.xpath("//*[@id='last-name']");
	public static final By postalcode=By.xpath("//*[@id='postal-code']");
	public static final By continuebutton=By.xpath("//input[@id='continue']");
	public static final By checkouttext=By.xpath("//span[text()='Checkout: Your Information']");
	public static final By invalidusererror=By.xpath("//h3[contains(text(),'Epic sadface: Username and password do not match any user in this service')]");

}

public class SauceDemo implements Locators,TestData {

	public WebDriver driver;

	@DataProvider(name ="LoginCredentials")
	public Object[][] login()
	{
		return new Object[][] {{"standard_user", "secret_sauce"},
			{"locked_out_user", "secret_sauce"},
			{"problem_user", "secret_sauce"},
			{"performance_glitch_user", "secret_sauce"},
			{"Basavaraj", "kichadi"}};
	}

	public void LaunchBrowser() {
		WebDriverManager.chromedriver().setup();
		driver = new ChromeDriver();
		driver.manage().window().maximize();
		driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(20));
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));
	}


	@Test(dataProvider = "LoginCredentials", priority = 1)
	public void LoginToApplication(String UserName, String Password) throws Exception {
		try {
			LaunchBrowser();
			driver.navigate().to(URL);
			driver.findElement(username).sendKeys(UserName);
			driver.findElement(password).sendKeys(Password);


			if(UserName.contains("standard")) { 
				driver.findElement(loginBtn).click();
				Thread.sleep(3000);
				Assert.assertTrue(driver.findElement(inventoryPage).isDisplayed(),"Inventory Page is not displayed");
				Assert.assertTrue(driver.getCurrentUrl().contains("inventory"), "Url doesnt contains Inventory End Point");
				System.out.println("Verified Standard Credentials Successfully");
			}

			else if(UserName.contains("locked")) { 
				driver.findElement(loginBtn).click();
				Thread.sleep(3000);
				Assert.assertTrue(driver.findElement(lockedErrorMsg).isDisplayed(),"Locked Error Message is not displayed");
				System.out.println("Verified Locked Credentials Successfully");
			}

			else if(UserName.contains("problem")) { 
				driver.findElement(loginBtn).click();
				Thread.sleep(3000);

				List<WebElement> allItems_Images = driver.findElements(productImages);
				for(WebElement ele : allItems_Images) {
					String imageSrc = ele.getAttribute("src");
					Assert.assertTrue(imageSrc.contains("static/media/sl-404.168b1cce.jpg"), "Different Image is displayed");
				}
				System.out.println("Verified Problem Credentials Successfully");
			}

			else if(UserName.contains("performance")) {
				driver.findElement(loginBtn).click();
				long startTime = System.currentTimeMillis();
				driver.findElement(inventoryPage);
				long endTime = System.currentTimeMillis();

				long performance = endTime-startTime;

				System.out.println("Verified Performance Credentials Successfully in millis : "+performance);
			}
			else
			{
				driver.findElement(loginBtn).click();
				
				Assert.assertTrue(driver.findElement(invalidusererror).isDisplayed(),"invalid Error Message is not displayed");
				System.out.println("Verified invalid  Credentials Successfully");
			}
			

			driver.close();
			driver.quit();
		}catch(Exception e) {
			throw new Exception("Exception occured "+e);
		}finally {
			driver = null;
		}
	}

	@Test(priority = 2)
	public void VerifyAddToCartFunctionality() throws Exception
	{
		try
		{
			LaunchBrowser();
			driver.navigate().to(URL);
			driver.findElement(username).sendKeys("standard_user");
			driver.findElement(password).sendKeys("secret_sauce");
			driver.findElement(loginBtn).click();
			Thread.sleep(3000);

			driver.findElement(singleItem).click();

			Assert.assertEquals(driver.findElement(cartBadgeCount).getText(), "1", "Mismatch in Cart Badge Count");
			System.out.println("Verified Add To Cart Functionality");

			driver.close();
			driver.quit();
		}catch(Exception e)
		{
			throw new Exception("Exception occured "+e);
		}finally
		{
			driver = null;
		}

	}

	@Test(priority = 3)
	public void VerifyRemoveFunctionality() throws Exception
	{
		try
		{
			LaunchBrowser();
			driver.navigate().to(URL);
			driver.findElement(username).sendKeys("standard_user");
			driver.findElement(password).sendKeys("secret_sauce");
			driver.findElement(loginBtn).click();
			Thread.sleep(3000);

			driver.findElement(singleItem).click();

			driver.findElement(cartBadgeCount).click();
			Thread.sleep(2000);
			driver.findElement(removeBtn).click();
			Thread.sleep(2000);
			String classAttr = driver.findElement(afterRemoving).getAttribute("class");
			Assert.assertTrue(classAttr.contains("removed"), "Item is not removed");
			System.out.println("Verified Remove Functionality");


			driver.close();
			driver.quit();
		}catch(Exception e)
		{
			throw new Exception("Exception occured "+e);
		}finally
		{
			driver = null;
		}
	}
	@Test(priority=4)
	public void verifyCheckoutFormFunctionality()
	{
		try
		{		
			LaunchBrowser();
			driver.navigate().to(URL);
			driver.findElement(username).sendKeys("standard_user");
			driver.findElement(password).sendKeys("secret_sauce");
			driver.findElement(loginBtn).click();
			Thread.sleep(3000);

			driver.findElement(singleItem).click();

			driver.findElement(cartBadgeCount).click();
			Thread.sleep(2000);
			
			driver.findElement(checkoutbutton).click();
			
			Assert.assertTrue(driver.findElement(checkouttext).isDisplayed(), "checkout functionality is not working");
			
			
			driver.findElement(firstname).sendKeys(Firstname);
			driver.findElement(lastname).sendKeys(Lastname);
			driver.findElement(postalcode).sendKeys(Postalcode);
			driver.findElement(continuebutton).click();
			
			System.out.println("Verified CheckoutForm Functionality");
			
			driver.close();
			driver.quit();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			driver=null;
		}
	}

}



