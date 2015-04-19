/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AYNIMTest.message;

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
public class CreateTest {
    
    private static WebDriver driver;
    private static String baseUrl;
    private static boolean acceptNextAlert = true;
    private static StringBuffer verificationErrors = new StringBuffer();

    public CreateTest() {

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
    
    @Test
    public void testCreate1() throws Exception {
        
        String dest = "joe";
        String content = "Salut joé, ton profil me plait";
        baseCreate(dest, content);

        assertEquals("Message send!",driver.findElement(By.id("messageForm:messagePanel")).getText());
    }
    
    @Test
    public void testCreate2() throws Exception {
        String dest = "";
        String content = "Salut, ton profil me plait";
        baseCreate(dest, content);
        
        assertEquals("You must choose someone",driver.findElement(By.id("messagePanel")).getText());
    }
    
    private boolean isElementPresent(By by) {
        try {
            driver.findElement(by);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }
    
    private void login() throws Exception {
        driver.get(baseUrl + "login.xhtml");
        Thread.sleep(1000);
        driver.findElement(By.name("j_username")).clear();
        driver.findElement(By.name("j_username")).sendKeys("test");
        Thread.sleep(100);
        driver.findElement(By.name("j_password")).clear();
        driver.findElement(By.name("j_password")).sendKeys("123456789");
        Thread.sleep(100);
        driver.findElement(By.id("login")).click();
        Thread.sleep(1000);
        assertTrue(baseUrl+"j_security_check" != driver.getCurrentUrl());
    }
    
    private void baseCreate(String dest, String content)throws Exception
    {
        login();
        driver.get(baseUrl+"poorSecure/message/List.xhtml");
        Thread.sleep(1000);
        driver.findElement(By.id("messageForm:newMessageLink")).click();
        Thread.sleep(1000);
        driver.findElement(By.id("messageFormCreate:destField")).clear();
        driver.findElement(By.id("messageFormCreate:destField")).sendKeys(dest);
        Thread.sleep(100);
        driver.findElement(By.id("messageFormCreate:content")).clear();
        driver.findElement(By.id("messageFormCreate:content")).sendKeys(content);        
        Thread.sleep(100);
        driver.findElement(By.id("messageFormCreate:createMessageLink")).click();
        Thread.sleep(1000);
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
