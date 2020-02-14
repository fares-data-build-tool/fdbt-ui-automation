package uk.co.tfn;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import static uk.co.tfn.HelperMethods.setDriverService;
import static uk.co.tfn.PageElements.startPageStartButton;
import static uk.co.tfn.HelperMethods.getHomePage;
import static uk.co.tfn.HelperMethods.makePageFullScreen;
import static uk.co.tfn.HelperMethods.setCapabilities;
import static uk.co.tfn.HelperMethods.setOptions;
import static uk.co.tfn.HelperMethods.waitForPageToLoad;

public class ChromeTestCase {

    private ChromeDriver driver;

    @Before
    public void chromeSetup() {

        DesiredCapabilities caps = setCapabilities();
        ChromeDriverService service = setDriverService();
        ChromeOptions options = setOptions();

        options.merge(caps);

        driver = new ChromeDriver(service, options);

        getHomePage(driver);
        waitForPageToLoad(driver);
        makePageFullScreen(driver);
    }

    @Test
    public void chromeTest() {

        startPageStartButton(driver).click();

        waitForPageToLoad(driver);

        driver.findElement((By.id("operator-name1"))).click();

        driver.findElement(By.id("continue-button")).click();

        waitForPageToLoad(driver);

        driver.findElement(By.id("faretype")).click();

        driver.findElement(By.id("continue-button")).click();

        assert(driver.findElement(By.id("page-heading")).isDisplayed());

        assert (driver.findElement(By.id("page-heading")).getText().equals("Please select your bus service"));

    }

    @After
    public void tearDown() {

        driver.quit();
    }

}
