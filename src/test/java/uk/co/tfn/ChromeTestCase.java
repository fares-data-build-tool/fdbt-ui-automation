package uk.co.tfn;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.Select;

import static uk.co.tfn.HelperMethods.continueButtonClick;
import static uk.co.tfn.HelperMethods.explicitWait;
import static uk.co.tfn.HelperMethods.setDriverService;
import static uk.co.tfn.HelperMethods.startPageButtonClick;
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

        startPageButtonClick(driver);

        driver.findElement((By.id("operator-name0"))).click();

        continueButtonClick(driver);

        driver.findElement(By.id("faretype")).click();

        continueButtonClick(driver);

        driver.findElement(By.id("service")).click();

        explicitWait(2000);

        Select dropdown = new Select(driver.findElement(By.id("service")));

        dropdown.selectByVisibleText("13 - Start date 04/12/2019");

        continueButtonClick(driver);

        driver.findElement(By.id("csv-upload")).click();

        continueButtonClick(driver);

        WebElement upload = driver.findElement(By.id("file-upload-1"));

        ((RemoteWebElement) upload ).setFileDetector(new LocalFileDetector());

        upload.sendKeys("/Users/robcatton/Downloads/testCsv.csv");

        explicitWait(10000);

    }

    @After
    public void tearDown() {

        driver.quit();
    }

}
