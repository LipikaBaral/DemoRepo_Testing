package Test.Util;

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import com.twilio.Twilio;
import com.twilio.base.ResourceSet;
import com.twilio.rest.api.v2010.account.Message;

public class AmazonOTPVerification {
	
	//Provide your SID and Token from Twilio
	public static final String ACCOUNT_SID = "AC5a30b4e7ad263b050fcecace2c765149";
	public static final String AUTH_TOKEN = "26411aa9bdc80f09f514ac6d82f21cc9";

	public static void main(String[] args) {
		
		/*Now the scenario is as below::
		 * 1.Click on Sign In
		 * 2.Click on Create your Amazon account(amazon.in)
		 * 3.Provide all the details(Name,the trial num created in Twilio etc)
		 * 4.
		 * 
		 * 
		 */
		
		System.setProperty("webdriver.chrome.driver", "/Users/Lipika/Desktop/Selenium/jars/chromedriver");
		WebDriver driver= new ChromeDriver();
		
		driver.manage().window().maximize();
		driver.manage().deleteAllCookies();
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		driver.manage().timeouts().pageLoadTimeout(15, TimeUnit.SECONDS);
		
		driver.get("https://www.amazon.in/");
		
		driver.findElement(By.xpath("//span[contains(text(),'Hello. Sign in')]")).click();
		driver.findElement(By.id("createAccountSubmit")).click();
		
		driver.findElement(By.name("customerName")).sendKeys("LipikaTest");
		driver.findElement(By.xpath("//span[@class='a-button-text a-declarative']")).click();
		
		driver.findElement(By.xpath("//a[contains(text(),'United States +1')]")).click();
		/*Now enter the phone number generated in Twilio except the country code i.e +1 as we
		 * have already selected it.
		 */
		
		driver.findElement(By.xpath("//input[@id='ap_phone_number']")).sendKeys("6029752672");
		driver.findElement(By.xpath("//input[@id='ap_password']")).sendKeys("Lipikaautomation@12");
		
		driver.findElement(By.id("continue")).click();
	
		//Now we are in OTP verification page
		
		Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
		String smsBody = getMessage();
		System.out.println(smsBody);//smsbody displays the entire string
		String otp = smsBody.replaceAll("[^0-9]", "");
		System.out.println(otp);
		driver.findElement(By.id("auth-pv-enter-code")).sendKeys(otp);
		
		
			

	}

	//******************************OTP VERIFICATION*********************************************** 
	//The below methods are written by Naveen AutomationLabs,just understand the basics and use it
	
	
	/*So here in this method,we have provided the phone num and from the phone num 
	it will go to that particular twilio account and get the message body where we have the OTP and it 
	it returns the string.
	*/
	public static String getMessage() {
		return getMessages().filter(m -> m.getDirection().compareTo(Message.Direction.INBOUND) == 0)
				            .filter(m -> m.getTo().equals("+16029752672")).map(Message::getBody).findFirst()
				            .orElseThrow(IllegalStateException::new);
	}
	
	private static Stream<Message> getMessages(){
		ResourceSet<Message> messages = Message.reader(ACCOUNT_SID).read();
		return StreamSupport.stream(messages.spliterator(),false);
	}
	
}
