/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AYNIMTest.user;

import java.util.Arrays;
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
import org.openqa.selenium.chrome.ChromeDriver;

/**
 *
 * @author joe.butikofe
 */
@RunWith(Parameterized.class)
public class CreateTest {
    
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
    
    public CreateTest(String nickname, String email, String birthday, String password, String country, String city, String description, String result) {
        this.nickname = nickname;
        this.email = email;
        this.birthday = birthday;
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
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
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
            {"test", "test@test.com","01.01.2015","123456789","Testland","Test Town","It’s a test","User was successfully created."},
            {"t", "testtest.com","","","","","","The nickname must be between 2 and 10 character long"+
            "\nInvalid email format"+
            "\nThe Password field is required."+
            "\nThe Description field is required."},
            {"test", "test@test.com","01.01.2015","123456789","Testland","Test Town","It’s a test","Transaction aborted"}
        });
    }
    
    @Test
    public void testCreate() throws Exception {
        driver.get(baseUrl+"user/Create.xhtml");
        Thread.sleep(1000);
        driver.findElement(By.name("form:nickname")).clear();
        driver.findElement(By.name("form:nickname")).sendKeys(nickname);
        driver.findElement(By.name("form:email")).clear();
        driver.findElement(By.name("form:email")).sendKeys(email);
        driver.findElement(By.name("form:birthday")).sendKeys(birthday);
        driver.findElement(By.name("form:password")).clear();
        driver.findElement(By.name("form:password")).sendKeys(password);
        driver.findElement(By.name("form:country")).clear();
        driver.findElement(By.name("form:country")).sendKeys(country);
        driver.findElement(By.name("form:city")).clear();
        driver.findElement(By.name("form:city")).sendKeys(city);
        driver.findElement(By.name("form:description")).clear();
        driver.findElement(By.name("form:description")).sendKeys(description);
        driver.findElement(By.linkText("Save")).click();
        Thread.sleep(1000);
        assertEquals(result,driver.findElement(By.id("messagePanel")).getText());
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
