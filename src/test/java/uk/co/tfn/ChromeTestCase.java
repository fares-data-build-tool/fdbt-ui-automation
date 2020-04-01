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


import java.util.Random;

import static org.junit.Assert.assertTrue;
import static uk.co.tfn.HelperMethods.*;
import static uk.co.tfn.StepMethods.*;

public class ChromeTestCase {

    private ChromeDriver driver;
    String basicFilepath = System.getProperty("user.dir") + "/src/test/testData/testcsv.csv";
    String periodFilepath = System.getProperty("user.dir") + "/src/test/testData/periodTestCsv.csv";

    @Before
    public void chromeSetup() {

        DesiredCapabilities caps = setCapabilities();
        ChromeDriverService service = setDriverService();
        ChromeOptions options = setOptions();

        options.merge(caps);

        driver = new ChromeDriver(service, options);

        getHomePage(driver);
        waitForPageToLoad(driver);
//        makePageFullScreen(driver);
    }

    @Test
    public void chromeUploadCSVTest() {

        stepsToInputMethod(driver);

        driver.findElement(By.id("csv-upload")).click();

        continueButtonClick(driver);

        uploadCsvFile(driver, basicFilepath);

        submitButtonClick(driver);

        fillInFareStageOptions(driver, 8);

        submitButtonClick(driver);

        assertTrue(isUuidStringValid(driver));

    }

    @Test
    public void chromeManualTriangle() {

        stepsToInputMethod(driver);
        driver.findElement(By.id("manual-entry")).click();
        continueButtonClick(driver);
        driver.findElement(By.id("lessThan20FareStages")).click();
        continueButtonClick(driver);
        WebElement fareStages = driver.findElement(By.id("fareStages"));
        fareStages.sendKeys("7");
        continueButtonClick(driver);
        fillInManualFareStages(driver);
        continueButtonClick(driver);
        fillInFareStageTriangle(driver);
        continueButtonClick(driver);
        fillInFareStageOptions(driver, 6);
        submitButtonClick(driver);
        assertTrue(isUuidStringValid(driver));
    }

    @Test
    public void chromePeriodGeoZone() {

        stepsToPeriodPage(driver);

        driver.findElement(By.id("periodtype-geo-zone")).click();

        continueButtonClick(driver);

        uploadCsvFile(driver, periodFilepath);

        submitButtonClick(driver);

        driver.findElement(By.id("periodProductName")).sendKeys("Selenium Test Product");

        driver.findElement(By.id("periodProductPrice")).sendKeys("10.50");

        continueButtonClick(driver);

        driver.findElement(By.id(("validity"))).sendKeys("1");

        continueButtonClick(driver);

        String endOfCalendarOption = "period-end-calendar";
        String endOfTwentyFourHoursOption = "period-twenty-four-hours";

        Random random = new Random();
        String chosenSelector;
        int number = random.nextInt(2) + 1;

        if (number == 2) {
            chosenSelector = endOfCalendarOption;
        } else {
            chosenSelector = endOfTwentyFourHoursOption;
        }

        driver.findElement(By.id((chosenSelector))).click();

        continueButtonClick(driver);

        assertTrue(isUuidStringValid(driver));
    }

    @After
    public void tearDown() {

        driver.quit();
    }

}
