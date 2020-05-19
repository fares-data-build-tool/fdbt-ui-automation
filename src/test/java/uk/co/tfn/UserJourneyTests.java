package uk.co.tfn;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.After;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.devicefarm.DeviceFarmClient;
import software.amazon.awssdk.services.devicefarm.model.CreateTestGridUrlRequest;
import software.amazon.awssdk.services.devicefarm.model.CreateTestGridUrlResponse;

import java.awt.AWTException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import static org.junit.Assert.assertTrue;
import static uk.co.tfn.HelperMethods.continueButtonClick;
import static uk.co.tfn.HelperMethods.fillInFareStageOptions;
import static uk.co.tfn.HelperMethods.getHomePage;
import static uk.co.tfn.HelperMethods.isUuidStringValid;
import static uk.co.tfn.HelperMethods.setCapabilities;
import static uk.co.tfn.HelperMethods.setChromeDriverService;
import static uk.co.tfn.HelperMethods.setChromeOptions;
import static uk.co.tfn.HelperMethods.submitButtonClick;
import static uk.co.tfn.HelperMethods.uploadFareZoneCsvFile;
import static uk.co.tfn.HelperMethods.uploadFaresTriangleCsvFile;
import static uk.co.tfn.HelperMethods.waitForPageToLoad;
import static uk.co.tfn.HelperMethods.clickElementById;
import static uk.co.tfn.StepMethods.fillInFareStageTriangle;
import static uk.co.tfn.StepMethods.fillInManualFareStages;
import static uk.co.tfn.StepMethods.stepsToInputMethod;
import static uk.co.tfn.StepMethods.stepsToPeriodPage;
import static uk.co.tfn.HelperMethods.waitForElement;
import static uk.co.tfn.HelperMethods.makeRandomDecisionBetweenTwoChoices;
import static uk.co.tfn.HelperMethods.randomlyChooseAndSelectServices;
import static uk.co.tfn.StepMethods.enterDetailsAndSelectValidityForMultipleProducts;

public class UserJourneyTests {

    private static RemoteWebDriver driver;
    private static String browserType;

    @BeforeAll
    public static void setup() throws IOException {

        File file = new File("src/test/properties/env.properties");
        FileInputStream fileInput = new FileInputStream(file);
        Properties properties = new Properties();
        properties.load(fileInput);
        fileInput.close();
        String browser = properties.getProperty("browser");
        String host = properties.getProperty("host");
        browserType = browser;

        if (host.equals("local")) {
            DesiredCapabilities caps = setCapabilities();
            if (browser.equals("chrome")) {
                ChromeDriverService service = setChromeDriverService();
                ChromeOptions options = setChromeOptions();
                options.merge(caps);
                driver = new ChromeDriver(service, options);
            } else if (browser.equals("firefox")) {
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                firefoxOptions.setCapability("marionette", true);
                firefoxOptions.setCapability("dom.file.createInChild", true);
                driver = new FirefoxDriver(firefoxOptions);
            }
        } else {
            String aws_secret_access_key = System.getenv("AWS_SECRET_ACCESS_KEY");
            String aws_access_key = System.getenv("AWS_ACCESS_KEY");
            System.setProperty("aws.secretAccessKey", aws_secret_access_key); // if attempting to run on AWS From local
                                                                              // machine, comment these two lines out,
                                                                              // and assume role using awstfn-mfa script
            System.setProperty("aws.accessKeyId", aws_access_key);
            String myProjectARN = "arn:aws:devicefarm:us-west-2:442445088537:testgrid-project:eaf5a5fe-6e13-493e-8d07-c083c0ee65ee";
            DeviceFarmClient client = DeviceFarmClient.builder().region(Region.US_WEST_2) // Device farm is in US_WEST_2
                    .build();
            CreateTestGridUrlRequest request = CreateTestGridUrlRequest.builder().expiresInSeconds(900) // 15 minutes
                    .projectArn(myProjectARN).build();
            URL testGridUrl = null;
            try {
                CreateTestGridUrlResponse response = client.createTestGridUrl(request);
                testGridUrl = new URL(response.url());
            } catch (Exception e) {
                e.printStackTrace();
            }
            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setBrowserName(browser);
            driver = new RemoteWebDriver(testGridUrl, capabilities);
        }
    }

    @Test
    public void singleTicketCsvTest() throws IOException, AWTException {

        getHomePage(driver);
        waitForPageToLoad(driver);
        stepsToInputMethod(driver);
        clickElementById(driver, "csv-upload");
        continueButtonClick(driver);
        uploadFaresTriangleCsvFile(driver, browserType);
        submitButtonClick(driver);
        fillInFareStageOptions(driver, 8);
        submitButtonClick(driver);
        assertTrue(isUuidStringValid(driver));
    }

    @Test
    public void singleTicketManualTriangleTest() {

        getHomePage(driver);
        waitForPageToLoad(driver);
        stepsToInputMethod(driver);
        clickElementById(driver, "manual-entry");
        continueButtonClick(driver);
        clickElementById(driver, "lessThan20FareStages");
        continueButtonClick(driver);
        waitForElement(driver, "fareStages");
        WebElement fareStages = driver.findElement(By.id("fareStages"));
        fareStages.sendKeys("7");
        continueButtonClick(driver);
        fillInManualFareStages(driver);
        continueButtonClick(driver);
        fillInFareStageTriangle(driver);
        continueButtonClick(driver);
        fillInFareStageOptions(driver, 7);
        submitButtonClick(driver);
        assertTrue(isUuidStringValid(driver));
    }

    @Test
    public void periodGeoZoneSingleProductTest() throws IOException, AWTException, InterruptedException {

        getHomePage(driver);
        waitForPageToLoad(driver);
        stepsToPeriodPage(driver);
        clickElementById(driver, "period-type-geo-zone");
        continueButtonClick(driver);
        uploadFareZoneCsvFile(driver, browserType);
        submitButtonClick(driver);
        waitForElement(driver, "numberOfProducts");
        driver.findElement(By.id("numberOfProducts")).sendKeys("1");
        continueButtonClick(driver);
        driver.findElement(By.id("productDetailsName")).sendKeys("Selenium Test Product");
        driver.findElement(By.id("productDetailsPrice")).sendKeys("10.50");
        continueButtonClick(driver);
        driver.findElement(By.id(("validity"))).sendKeys("1");
        continueButtonClick(driver);

        String endOfCalendarOption = "period-end-calendar";
        String endOfTwentyFourHoursOption = "period-twenty-four-hours";

        String chosenSelector;
        chosenSelector = makeRandomDecisionBetweenTwoChoices(endOfCalendarOption, endOfTwentyFourHoursOption);

        clickElementById(driver, chosenSelector);
        continueButtonClick(driver);
        assertTrue(isUuidStringValid(driver));
    }

    @Test
    public void periodMultipleServicesSingleProductTest() throws IOException {

        getHomePage(driver);
        waitForPageToLoad(driver);
        stepsToPeriodPage(driver);
        clickElementById(driver, "period-type-single-set-service");
        continueButtonClick(driver);
        randomlyChooseAndSelectServices(driver);
        continueButtonClick(driver);
        driver.findElement(By.id("numberOfProducts")).sendKeys("1");
        continueButtonClick(driver);
        driver.findElement(By.id("productDetailsName")).sendKeys("Selenium Test Product");
        driver.findElement(By.id("productDetailsPrice")).sendKeys("10.50");
        continueButtonClick(driver);
        driver.findElement(By.id(("validity"))).sendKeys("1");
        continueButtonClick(driver);

        String endOfCalendarOption = "period-end-calendar";
        String endOfTwentyFourHoursOption = "period-twenty-four-hours";

        String chosenSelector;
        chosenSelector = makeRandomDecisionBetweenTwoChoices(endOfCalendarOption, endOfTwentyFourHoursOption);

        clickElementById(driver, chosenSelector);
        continueButtonClick(driver);
        assertTrue(isUuidStringValid(driver));
    }

    @Test
    public void periodMultipleServicesMultipleProducts() throws IOException {

        getHomePage(driver);
        waitForPageToLoad(driver);
        stepsToPeriodPage(driver);
        clickElementById(driver, "period-type-single-set-service");
        continueButtonClick(driver);
        randomlyChooseAndSelectServices(driver);
        continueButtonClick(driver);

        driver.findElement(By.id("numberOfProducts")).sendKeys("4");

        continueButtonClick(driver);
        enterDetailsAndSelectValidityForMultipleProducts(driver, 4);
        assertTrue(isUuidStringValid(driver));
    }

    @After
    public void tearDown() {
        driver.quit();
    }

}