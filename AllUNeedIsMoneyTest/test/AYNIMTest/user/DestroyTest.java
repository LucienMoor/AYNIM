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
public class DestroyTest {
    
    private static WebDriver driver;
    private static String baseUrl;
    private static boolean acceptNextAlert = true;
    private static StringBuffer verificationErrors = new StringBuffer();
    
    public DestroyTest() {
       
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
    public void testDestroy() throws Exception {
        
        login("test","qwertz");
        
        driver.get(baseUrl);
        Thread.sleep(1000);
        
        driver.findElement(By.id("headerFormProfil:profilLink")).click();
        Thread.sleep(1000);
        driver.findElement(By.id("profilForm:destroyButton")).click();
        Thread.sleep(1000);
        assertEquals("User was successfully deleted.", driver.findElement(By.id("javax_faces_developmentstage_messages")).getText());
    }
    
    private void login(String username, String password) throws Exception {
        driver.get(baseUrl + "login.xhtml");
        Thread.sleep(1000);
        driver.findElement(By.name("j_username")).clear();
        driver.findElement(By.name("j_username")).sendKeys(username);
        driver.findElement(By.name("j_password")).clear();
        driver.findElement(By.name("j_password")).sendKeys(password);
        Thread.sleep(100);
        driver.findElement(By.id("login")).click();
        Thread.sleep(1000);
        assertTrue(baseUrl+"j_security_check" != driver.getCurrentUrl());
    }

    private void logout() throws Exception {
        driver.get(baseUrl);
        Thread.sleep(1000);
        //Check if link for log out appear --> user log in 
        if(isElementPresent(By.id("headerFormLogout:logoutLink")))
        {
            driver.findElement(By.id("headerFormLogout:logoutLink")).click();
            Thread.sleep(1000);
        }
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
