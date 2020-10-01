package uk.co.tfn;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
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
    private static Axe axe;

    private static final String USERNAME = System.getenv("BROWSERSTACK_USERNAME");
    private static final String AUTOMATE_KEY = System.getenv("BROWSERSTACK_KEY");
    private static final String BUILD_NAME = System.getenv("CIRCLE_BUILD_NUM") != null
            ? System.getenv("CIRCLE_BUILD_NUM")
            : "Local Build";
    private static final String URL = "https://" + USERNAME + ":" + AUTOMATE_KEY + "@hub-cloud.browserstack.com/wd/hub";

    @BeforeClass
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
            caps.setCapability("project", "FDBT Site");
            caps.setCapability("build", BUILD_NAME);

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

        axe = new Axe(driver, host);
        helpers = new HelperMethods(driver, browser, host, axe);
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
        helpers.continueButtonClick();
        helpers.selectSalesOfferPackages("product");
        helpers.continueButtonClick();
        helpers.completeProductDateInformationPage();
        helpers.continueButtonClick();
        assertTrue(helpers.isUuidStringValid());
    }

    @Test
    public void singleTicketManualTriangleTest() throws InterruptedException {
        helpers.getHomePage();
        helpers.waitForPageToLoad();
        stepMethods.stepsToSingleTicketInputMethod();
        helpers.clickElementById("manual-entry");
        helpers.continueButtonClick();
        helpers.clickElementById("less-than-20-fare-stages");
        helpers.continueButtonClick();
        helpers.waitForElement("fare-stages");
        int numberOfFareStages = 5;
        helpers.sendKeysById("fare-stages", String.valueOf(numberOfFareStages));
        helpers.continueButtonClick();
        helpers.fillInManualFareStagesNames(numberOfFareStages);
        helpers.continueButtonClick();
        helpers.continueButtonClick();
        helpers.fillInFareStageTriangle(numberOfFareStages);
        helpers.continueButtonClick();
        helpers.fillInFareStageOptions(numberOfFareStages);
        helpers.submitButtonClick();
        helpers.continueButtonClick();
        helpers.selectSalesOfferPackages("product");
        helpers.continueButtonClick();
        helpers.completeProductDateInformationPage();
        helpers.continueButtonClick();
        assertTrue(helpers.isUuidStringValid());
    }

    @Test
    public void periodGeoZoneSingleProductTest() throws IOException, InterruptedException {
        String productName = "Selenium Test Product";
        helpers.getHomePage();
        helpers.waitForPageToLoad();
        stepMethods.stepsToPeriodPage();
        helpers.clickElementById("geo-zone");
        helpers.continueButtonClick();
        helpers.uploadFareZoneCsvFile();
        helpers.submitButtonClick();
        helpers.waitForElement("number-of-products");
        helpers.sendKeysById("number-of-products", "1");
        helpers.continueButtonClick();
        helpers.sendKeysById("product-details-name", productName);
        helpers.sendKeysById("product-details-price", "10.50");
        helpers.continueButtonClick();
        helpers.sendKeysById("validity", "1");
        helpers.continueButtonClick();

        String endOfCalendarOption = "period-end-calendar";
        String endOfTwentyFourHoursOption = "period-twenty-four-hours";

        String chosenSelector;
        chosenSelector = makeRandomDecisionBetweenTwoChoices(endOfCalendarOption, endOfTwentyFourHoursOption);

        helpers.clickElementById(chosenSelector);
        helpers.continueButtonClick();
        helpers.selectSalesOfferPackages(productName);
        helpers.continueButtonClick();
        helpers.completeProductDateInformationPage();
        helpers.continueButtonClick();
        assertTrue(helpers.isUuidStringValid());
    }

    @Test
    public void periodMultipleServicesSingleProductTest() throws IOException {
        String productName = "Selenium Test Product";
        helpers.getHomePage();
        helpers.waitForPageToLoad();
        stepMethods.stepsToPeriodPage();
        helpers.clickElementById("set-of-services");
        helpers.continueButtonClick();
        helpers.randomlyChooseAndSelectServices();
        helpers.continueButtonClick();
        helpers.sendKeysById("number-of-products", "1");
        helpers.continueButtonClick();
        helpers.sendKeysById("product-details-name", productName);
        helpers.sendKeysById("product-details-price", "10.50");
        helpers.continueButtonClick();
        helpers.sendKeysById("validity", "1");
        helpers.continueButtonClick();

        String endOfCalendarOption = "period-end-calendar";
        String endOfTwentyFourHoursOption = "period-twenty-four-hours";

        String chosenSelector;
        chosenSelector = makeRandomDecisionBetweenTwoChoices(endOfCalendarOption, endOfTwentyFourHoursOption);

        helpers.clickElementById(chosenSelector);
        helpers.continueButtonClick();
        helpers.continueButtonClick();
        helpers.selectSalesOfferPackages(productName);
        helpers.continueButtonClick();
        helpers.completeProductDateInformationPage();
        helpers.continueButtonClick();
        assertTrue(helpers.isUuidStringValid());
    }

    @Test
    public void periodMultipleServicesMultipleProducts() throws IOException {
        helpers.getHomePage();
        helpers.waitForPageToLoad();
        stepMethods.stepsToPeriodPage();
        helpers.clickElementById("set-of-services");
        helpers.continueButtonClick();
        helpers.randomlyChooseAndSelectServices();
        helpers.continueButtonClick();
        int numberOfProducts = HelperMethods.randomNumberBetweenOneAnd(8) + 2;
        helpers.sendKeysById("number-of-products", String.valueOf(numberOfProducts));
        helpers.continueButtonClick();
        helpers.enterDetailsAndSelectValidityForMultipleProducts(numberOfProducts);
        helpers.selectSalesOfferPackagesForMultipleProducts(numberOfProducts);
        helpers.continueButtonClick();
        helpers.completeProductDateInformationPage();
        helpers.continueButtonClick();
        assertTrue(helpers.isUuidStringValid());
    }

    @Test
    public void flatFareMultipleServicesSingleProduct() throws IOException {
        String productName = "Flat Fare Test Product";
        helpers.getHomePage();
        helpers.waitForPageToLoad();
        stepMethods.stepsToSelectFlatFareServiceSelection();
        helpers.randomlyChooseAndSelectServices();
        helpers.continueButtonClick();
        helpers.sendKeysById("product-details-name", productName);
        helpers.sendKeysById("product-details-price", "50.50");
        helpers.continueButtonClick();
        helpers.continueButtonClick();
        helpers.selectSalesOfferPackages(productName);
        helpers.continueButtonClick();
        helpers.completeProductDateInformationPage();
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
        helpers.enterReturnTicketValidity();
        helpers.continueButtonClick();
        helpers.continueButtonClick();
        helpers.selectSalesOfferPackages("product");
        helpers.continueButtonClick();
        helpers.completeProductDateInformationPage();
        helpers.continueButtonClick();
        assertTrue(helpers.isUuidStringValid());
    }

    @Test
    public void returnTicketCircularAndNonCircularManualUpload()
            throws IOException, AWTException, InterruptedException {
        helpers.getHomePage();
        helpers.waitForPageToLoad();
        stepMethods.stepsToReturnTicketInputMethod();
        helpers.clickElementById("manual-entry");
        helpers.continueButtonClick();
        helpers.clickElementById("less-than-20-fare-stages");
        helpers.continueButtonClick();
        helpers.waitForElement("fare-stages");
        int numberOfFareStages = HelperMethods.randomNumberBetweenOneAnd(4) + 3;
        helpers.sendKeysById("fare-stages", String.valueOf(numberOfFareStages));
        helpers.continueButtonClick();
        helpers.fillInManualFareStagesNames(numberOfFareStages);
        helpers.continueButtonClick();
        helpers.continueButtonClick();
        helpers.fillInFareStageTriangle(numberOfFareStages);
        helpers.continueButtonClick();
        helpers.fillInFareStageOptions(numberOfFareStages);
        helpers.submitButtonClick();
        helpers.fillInFareStageOptions(numberOfFareStages);
        helpers.submitButtonClick();
        helpers.enterReturnTicketValidity();
        helpers.continueButtonClick();
        helpers.continueButtonClick();
        helpers.selectSalesOfferPackages("product");
        helpers.continueButtonClick();
        helpers.completeProductDateInformationPage();
        helpers.continueButtonClick();
        assertTrue(helpers.isUuidStringValid());
    }

    @AfterClass
    public static void tearDown() {
        driver.quit();
    }

}
