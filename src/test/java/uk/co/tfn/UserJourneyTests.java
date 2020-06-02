package uk.co.tfn;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.awt.AWTException;
import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.assertTrue;
import static uk.co.tfn.HelperMethods.setCapabilities;
import static uk.co.tfn.HelperMethods.setChromeDriverService;
import static uk.co.tfn.HelperMethods.setChromeOptions;
import static uk.co.tfn.HelperMethods.makeRandomDecisionBetweenTwoChoices;

public class UserJourneyTests {

    private static RemoteWebDriver driver;
    private static String browser;
    private static String host;
    private static HelperMethods helpers;
    private static StepMethods stepMethods;

    private static final String USERNAME = System.getenv("BROWSERSTACK_USERNAME");
    private static final String AUTOMATE_KEY = System.getenv("BROWSERSTACK_KEY");
    private static final String URL = "https://" + USERNAME + ":" + AUTOMATE_KEY + "@hub-cloud.browserstack.com/wd/hub";

    @BeforeAll
    public static void setup() throws IOException {
        browser = System.getProperty("browser");
        host = System.getProperty("host");

        if (host.equals("local")) {
            DesiredCapabilities caps = setCapabilities();

            if (browser.equals("chrome")) {
                ChromeDriverService service = setChromeDriverService();
                ChromeOptions options = setChromeOptions();
                ChromeOptions merged = options.merge(caps);
                driver = new ChromeDriver(service, merged);
            } else if (browser.equals("firefox")) {
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                firefoxOptions.setCapability("marionette", true);
                firefoxOptions.setCapability("dom.file.createInChild", true);
                driver = new FirefoxDriver(firefoxOptions);
            }
        } else {
            DesiredCapabilities caps = new DesiredCapabilities();

            caps.setCapability("os", "Windows");
            caps.setCapability("os_version", "10");
            caps.setCapability("browserstack.local", "true");

            if (browser.equals("firefox")) {
                caps.setCapability("browserName", "Firefox");

                driver = new RemoteWebDriver(new URL(URL), caps);
            } else if (browser.equals("chrome")) {
                caps.setCapability("browserName", "Chrome");

                ChromeOptions options = setChromeOptions();
                ChromeOptions merged = options.merge(caps);

                driver = new RemoteWebDriver(new URL(URL), merged);
            } else if (browser.equals("ie")) {
                caps.setCapability("browserName", "IE");

                caps.setCapability("ignoreProtectedModeSettings", true);
                caps.setCapability("disable-popup-blocking", true);
                caps.setCapability("enablePersistentHover", true);
                caps.setCapability("ignoreZoomSetting", true);

                driver = new RemoteWebDriver(new URL(URL), caps);
            }

            driver.setFileDetector(new LocalFileDetector());
        }

        helpers = new HelperMethods(driver, browser, host);
        stepMethods = new StepMethods(helpers, driver);
    }

    @Test
    public void singleTicketCsvTest() throws IOException, AWTException, InterruptedException {
    helpers.getHomePage();
    helpers.waitForPageToLoad();
    stepMethods.stepsToSingleTicketInputMethod();
    helpers.clickElementById("csv-upload");
    helpers.continueButtonClick();
    helpers.uploadFaresTriangleCsvFile();
    helpers.submitButtonClick();
    helpers.fillInFareStageOptions(8);
    helpers.submitButtonClick();
    assertTrue(helpers.isUuidStringValid());
    }

    @Test
    public void singleTicketManualTriangleTest() throws InterruptedException {
    helpers.getHomePage();
    helpers.waitForPageToLoad();
    stepMethods.stepsToSingleTicketInputMethod();
    helpers.clickElementById("manual-entry");
    helpers.continueButtonClick();
    helpers.clickElementById("lessThan20FareStages");
    helpers.continueButtonClick();
    helpers.waitForElement("fareStages");
    int numberOfFareStages = 5;
    helpers.sendKeysById("fareStages", String.valueOf(numberOfFareStages));
    helpers.continueButtonClick();
    helpers.fillInManualFareStagesNames(numberOfFareStages);
    helpers.continueButtonClick();
    helpers.fillInFareStageTriangle(numberOfFareStages);
    helpers.continueButtonClick();
    helpers.fillInFareStageOptions(numberOfFareStages);
    helpers.submitButtonClick();
    assertTrue(helpers.isUuidStringValid());
    }

    @Test
    public void periodGeoZoneSingleProductTest() throws IOException,
    InterruptedException {
    helpers.getHomePage();
    helpers.waitForPageToLoad();
    stepMethods.stepsToPeriodPage();
    helpers.clickElementById("period-type-geo-zone");
    helpers.continueButtonClick();
    helpers.uploadFareZoneCsvFile();
    helpers.submitButtonClick();
    helpers.waitForElement("numberOfProducts");
    helpers.sendKeysById("numberOfProducts", "1");
    helpers.continueButtonClick();
    helpers.sendKeysById("productDetailsName", "Selenium Test Product");
    helpers.sendKeysById("productDetailsPrice", "10.50");
    helpers.continueButtonClick();
    helpers.sendKeysById("validity", "1");
    helpers.continueButtonClick();

    String endOfCalendarOption = "period-end-calendar";
    String endOfTwentyFourHoursOption = "period-twenty-four-hours";

    String chosenSelector;
    chosenSelector = makeRandomDecisionBetweenTwoChoices(endOfCalendarOption,
    endOfTwentyFourHoursOption);

    helpers.clickElementById(chosenSelector);
    helpers.continueButtonClick();
    assertTrue(helpers.isUuidStringValid());
    }

    @Test
    public void periodMultipleServicesSingleProductTest() throws IOException {
    helpers.getHomePage();
    helpers.waitForPageToLoad();
    stepMethods.stepsToPeriodPage();
    helpers.clickElementById("period-type-single-set-service");
    helpers.continueButtonClick();
    helpers.randomlyChooseAndSelectServices();
    helpers.continueButtonClick();
    helpers.sendKeysById("numberOfProducts", "1");
    helpers.continueButtonClick();
    helpers.sendKeysById("productDetailsName", "Selenium Test Product");
    helpers.sendKeysById("productDetailsPrice", "10.50");
    helpers.continueButtonClick();
    helpers.sendKeysById("validity", "1");
    helpers.continueButtonClick();

    String endOfCalendarOption = "period-end-calendar";
    String endOfTwentyFourHoursOption = "period-twenty-four-hours";

    String chosenSelector;
    chosenSelector = makeRandomDecisionBetweenTwoChoices(endOfCalendarOption,
    endOfTwentyFourHoursOption);

    helpers.clickElementById(chosenSelector);
    helpers.continueButtonClick();
    assertTrue(helpers.isUuidStringValid());
    }

    @Test
    public void periodMultipleServicesMultipleProducts() throws IOException {
    helpers.getHomePage();
    helpers.waitForPageToLoad();
    stepMethods.stepsToPeriodPage();
    helpers.clickElementById("period-type-single-set-service");
    helpers.continueButtonClick();
    helpers.randomlyChooseAndSelectServices();
    helpers.continueButtonClick();
    int numberOfProducts = HelperMethods.randomNumberBetweenOneAnd(10) + 2;
    helpers.sendKeysById("numberOfProducts", String.valueOf(numberOfProducts));
    helpers.continueButtonClick();
    helpers.enterDetailsAndSelectValidityForMultipleProducts(numberOfProducts);
    assertTrue(helpers.isUuidStringValid());
    }

    @Test
    public void flatFareMultipleServicesSingleProduct() throws IOException {
    helpers.getHomePage();
    helpers.waitForPageToLoad();
    stepMethods.stepsToSelectFlatFareServiceSelection();
    helpers.randomlyChooseAndSelectServices();
    helpers.continueButtonClick();
    helpers.sendKeysById("productDetailsName", "Flat Fare Test Product");
    helpers.sendKeysById("productDetailsPrice", "50.50");
    helpers.continueButtonClick();
    assertTrue(helpers.isUuidStringValid());
    }

    @Test
    public void returnTicketCircularAndNonCircularCsvUpload() throws IOException, AWTException, InterruptedException {
        helpers.getHomePage();
        helpers.waitForPageToLoad();
        stepMethods.stepsToReturnTicketInputMethod();
        helpers.clickElementById("csv-upload");
        helpers.continueButtonClick();
        helpers.uploadFaresTriangleCsvFile();
        helpers.submitButtonClick();
        helpers.fillInFareStageOptions(8);
        helpers.submitButtonClick();
        helpers.fillInFareStageOptions(8);
        helpers.submitButtonClick();

        assertTrue(helpers.isUuidStringValid());
    }

    @Test
    public void returnTicketCircularAndNonCircularManualUpload() throws IOException, AWTException,
            InterruptedException {
        helpers.getHomePage();
        helpers.waitForPageToLoad();
        stepMethods.stepsToReturnTicketInputMethod();
        helpers.clickElementById("manual-entry");
        helpers.continueButtonClick();
        helpers.clickElementById("lessThan20FareStages");
        helpers.continueButtonClick();
        helpers.waitForElement("fareStages");
        int numberOfFareStages = HelperMethods.randomNumberBetweenOneAnd(4) + 3;
        helpers.sendKeysById("fareStages", String.valueOf(numberOfFareStages));
        helpers.continueButtonClick();
        helpers.fillInManualFareStagesNames(numberOfFareStages);
        helpers.continueButtonClick();
        helpers.fillInFareStageTriangle(numberOfFareStages);
        helpers.continueButtonClick();
        helpers.fillInFareStageOptions(numberOfFareStages);
        helpers.submitButtonClick();
        helpers.fillInFareStageOptions(numberOfFareStages);
        helpers.submitButtonClick();

        assertTrue(helpers.isUuidStringValid());

    }

    @AfterAll
    public static void tearDown() {
        driver.quit();
    }

}
