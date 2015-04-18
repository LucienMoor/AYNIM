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
import org.junit.experimental.runners.Enclosed;
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
@RunWith(Enclosed.class)
public class EditTest {

    private static WebDriver driver;
    private static String baseUrl;

    private static boolean acceptNextAlert = true;
    private static StringBuffer verificationErrors = new StringBuffer();

    public EditTest() {

    }

    @RunWith(Parameterized.class)
    public static class ComponentParamTests {

        private static String email;
        private static String birthday;
        private static String password;
        private static String newPassword;
        private static String country;
        private static String city;
        private static String description;
        private static String result;

        public ComponentParamTests(String email, String birthday, String password, String newPassword, String country, String city, String description, String result) {
            this.email = email;
            this.birthday = birthday;
            this.newPassword = newPassword;
            this.password = password;
            this.country = country;
            this.city = city;
            this.description = description;
            this.result = result;
        }

        @Parameterized.Parameters
        public static Iterable<Object[]> data1() {
            return Arrays.asList(new Object[][]{
                {"test@modified.com", "02.01.2015",  "123456789", "qwertz", "Editland", "Edit Town", "New description", "User was successfully updated."},
                {"testtest.com", "", "qwertz", "", "", "", "", "Invalid email format"
                    + "\nThe Password field is required."
                    + "\nThe Description field is required."}
            });
        }

        @Test
        public void testEdit() throws Exception {
    
            login("test",password);
            //Search user
            /*driver.get(baseUrl + "user/List.xhtml");
            Thread.sleep(1000);
            driver.findElement(By.name("j_idt11:j_idt13")).clear();
            driver.findElement(By.name("j_idt11:j_idt13")).sendKeys("test");
            Thread.sleep(100);
            driver.findElement(By.id("j_idt11:searchButton")).click();
            Thread.sleep(1000);
            driver.findElement(By.linkText("Edit")).click();
            Thread.sleep(1000);*/
            
            driver.get(baseUrl);
            Thread.sleep(1000);
            driver.findElement(By.id("j_idt18:profilLink")).click();
            Thread.sleep(1000);
            driver.findElement(By.id("j_idt43:editButton")).click();
            Thread.sleep(1000);

            driver.findElement(By.name("form:email")).clear();
            driver.findElement(By.name("form:email")).sendKeys(email);

            driver.findElement(By.name("form:birthday")).sendKeys(birthday);

            driver.findElement(By.name("form:password")).clear();
            driver.findElement(By.name("form:password")).sendKeys(newPassword);

            driver.findElement(By.name("form:country")).clear();
            driver.findElement(By.name("form:country")).sendKeys(country);

            driver.findElement(By.name("form:city")).clear();
            driver.findElement(By.name("form:city")).sendKeys(city);

            driver.findElement(By.name("form:description")).clear();
            driver.findElement(By.name("form:description")).sendKeys(description);

            driver.findElement(By.id("form:editLink")).click();
            Thread.sleep(1000);
            
            //check if we are still in the edit form (error) or not (success) 
            if (isElementPresent(By.name("form:email"))) 
                assertEquals(result,driver.findElement(By.id("messagePanel")).getText());
            else
                assertEquals(result, driver.findElement(By.id("javax_faces_developmentstage_messages")).getText());
        }
    }

    public static class ComponentSingleTests {

        @Test
        public void testEditNotAuthenticated() throws Exception {
        logout();
        driver.get(baseUrl + "poorSecure/user/Edit.xhtml");
        Thread.sleep(1000);
        //Check if the application redirect the user on the login page
        assertTrue(isElementPresent(By.id("login")));
        }
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

    private static void login(String username, String password) throws Exception {
        driver.get(baseUrl + "login.xhtml");
        Thread.sleep(1000);
        driver.findElement(By.name("j_username")).clear();
        driver.findElement(By.name("j_username")).sendKeys(username);
        Thread.sleep(100);
        driver.findElement(By.name("j_password")).clear();
        driver.findElement(By.name("j_password")).sendKeys(password);
        Thread.sleep(100);
        driver.findElement(By.id("login")).click();
        Thread.sleep(1000);
        assertTrue(baseUrl+"j_security_check" != driver.getCurrentUrl());
    }

    private static void logout() throws Exception {
        driver.get(baseUrl);
        Thread.sleep(1000);
        //Check if link for log out appear --> user log in 
        if(isElementPresent(By.id("j_idt21:logoutLink")))
        {
            driver.findElement(By.id("j_idt21:logoutLink")).click();
            Thread.sleep(1000);
        }
    }

    private static boolean isElementPresent(By by) {
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
