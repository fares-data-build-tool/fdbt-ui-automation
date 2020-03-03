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
import org.openqa.selenium.support.ui.Select;

import java.util.List;

import static org.junit.Assert.assertTrue;
import static uk.co.tfn.HelperMethods.*;

public class ChromeTestCase {

    private ChromeDriver driver;
    String filepath = System.getProperty("user.dir")+"/src/test/testData/testcsv.csv";

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

        driver.findElement(By.id("faretype-single")).click();

        continueButtonClick(driver);

        driver.findElement(By.id("service")).click();

        Select serviceDropdown = new Select(driver.findElement(By.id("service")));

        List<WebElement> serviceDropdownOptions = serviceDropdown.getOptions();

        serviceDropdownOptions.get(1).click();

        continueButtonClick(driver);

        driver.findElement(By.id("journeyPattern")).click();

        Select directionDropdown = new Select(driver.findElement(By.id("journeyPattern")));

        List<WebElement> directionDropdownOptions = directionDropdown.getOptions();

        directionDropdownOptions.get(1).click();

        continueButtonClick(driver);

        driver.findElement(By.id("csv-upload")).click();

        continueButtonClick(driver);

        uploadCsvFile(driver, filepath);

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
