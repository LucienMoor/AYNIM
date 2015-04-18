package AYNIMTest.user;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

/**
 *
 * @author joe.butikofe
 */
@RunWith(Parameterized.class)
public class ReadTest {
    
    private static WebDriver driver;
    private static String baseUrl;
    private static boolean acceptNextAlert = true;
    private static StringBuffer verificationErrors = new StringBuffer();
    private static String nickname;
    private static String email;
    private static String birthday;
    private static String password;
    private static String country;
    private static String city;
    private static String description;
    
    private static String result;
    
    public ReadTest(String nickname, String email, String password, String country, String city, String description, String result) {
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.country = country;
        this.city = city;
        this.description = description;
        this.result = result;
    }
    
    @BeforeClass
    public static void setUpClass() {
        System.setProperty("webdriver.chrome.driver", "c:/temp/chromedriver.exe");
        driver = new ChromeDriver();
        baseUrl = "http://localhost:20628/AllUNeedIsMoney/";
        driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
    }
    
    @AfterClass
    public static void tearDownClass() {
        driver.close();
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

	
    @Parameterized.Parameters
    public static Iterable<Object[]> data1() {
        return Arrays.asList(new Object[][]{
            {"test", "test@test.com", "123456789","Testland","Test Town","It’s a test","User was successfully created."}
        });
    }
    
    @Test
    public void testRead() throws Exception {

        //Search user
        driver.get(baseUrl+"user/List.xhtml");
        Thread.sleep(1000);
        driver.findElement(By.name("j_idt11:j_idt13")).clear();
        driver.findElement(By.name("j_idt11:j_idt13")).sendKeys("test");
        Thread.sleep(1000);
        driver.findElement(By.id("j_idt11:searchButton")).click();
        Thread.sleep(1000);
        driver.findElement(By.linkText("test")).click();
        Thread.sleep(1000);
        
        //Check profil
        assertEquals(nickname,driver.findElement(By.id("nickname")).getText());
        assertEquals(country,driver.findElement(By.id("country")).getText());
        assertEquals(city,driver.findElement(By.id("city")).getText());
        assertEquals(description, driver.findElement(By.id("description")).getText());
        Thread.sleep(100);
    }
    
    private boolean isElementPresent(By by) {
        try {
            driver.findElement(by);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    private boolean isAlertPresent() {
        try {
            driver.switchTo().alert();
            return true;
        } catch (NoAlertPresentException e) {
            return false;
        }
    }

    private String closeAlertAndGetItsText() {
        try {
            Alert alert = driver.switchTo().alert();
            String alertText = alert.getText();
            if (acceptNextAlert) {
                alert.accept();
            } else {
                alert.dismiss();
            }
            return alertText;
        } finally {
            acceptNextAlert = true;
        }
    }

}
