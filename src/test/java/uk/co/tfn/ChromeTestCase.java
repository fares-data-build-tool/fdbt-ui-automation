package uk.co.tfn;

import org.junit.After;
import org.junit.Assert;
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static uk.co.tfn.HelperMethods.*;

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

        waitForElement(driver,"service");

        Select serviceDropdown = new Select(driver.findElement(By.id("service")));

        serviceDropdown.selectByValue("1#02/01/2020");

        continueButtonClick(driver);

        driver.findElement(By.id("journeyPattern")).click();

        waitForElement(driver,"journeyPattern");

        Select directionDropdown = new Select(driver.findElement(By.id("journeyPattern")));

        directionDropdown.selectByValue("2500BTA01076#2590B1003");

        continueButtonClick(driver);

        waitForElement(driver, "csv-upload");

        driver.findElement(By.id("csv-upload")).click();

        continueButtonClick(driver);

        uploadCsvFile(driver);

        submitButtonClick(driver);

        fillInFareStageOptions(driver);

        submitButtonClick(driver);

        assertTrue(isUuidStringValid(driver));

    }

    @After
    public void tearDown() {

        driver.quit();
    }

}
