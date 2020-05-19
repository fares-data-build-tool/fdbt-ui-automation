package uk.co.tfn;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.awt.AWTException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class HelperMethods {

    public static void waitForPageToLoad(WebDriver driver) {
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(webDriver -> ((JavascriptExecutor) webDriver)
                .executeScript("return document.readyState").equals("complete"));
    }

    public static void getHomePage(WebDriver driver) {
        driver.get("https://tfn-test.infinityworks.com");
    }

    public static DesiredCapabilities setCapabilities() {

        DesiredCapabilities caps = new DesiredCapabilities();

        // any capabilities we want to set

        return caps;
    }

    public static ChromeOptions setChromeOptions() {
        ChromeOptions options = new ChromeOptions();

        options.setPageLoadStrategy(PageLoadStrategy.NONE);
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-extensions");

        return options;
    }

    public static ChromeDriverService setChromeDriverService() {
        return new ChromeDriverService.Builder().usingDriverExecutable(new File("/usr/local/chromedriver"))
                .usingAnyFreePort().build();
    }

    public static void continueButtonClick(WebDriver driver) {
        clickElementById(driver, "continue-button");
        waitForPageToLoad(driver);
    }

    public static void submitButtonClick(WebDriver driver) {
        clickElementById(driver, "submit-button");
        waitForPageToLoad(driver);
    }

    public static void startPageButtonClick(WebDriver driver) {
        clickElementById(driver, "start-now-button");
        waitForPageToLoad(driver);
    }

    public static void waitForElement(WebDriver driver, String elementId) {

        FluentWait<WebDriver> fluentWait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(30))
                .pollingEvery(Duration.ofMillis(200)).ignoring(NoSuchElementException.class);

        fluentWait.until(new Function<WebDriver, WebElement>() {
            public WebElement apply(WebDriver driver) {
                return driver.findElement(By.id(elementId));
            }
        });
    }

    public static void fillInFareStageOptions(WebDriver driver, int numberOfFareStages) {

        List<WebElement> dropdowns = driver.findElements(By.className("farestage-select-wrapper"));

        final AtomicInteger dropdownCounter = new AtomicInteger(0);
        final AtomicInteger fareStageCounter = new AtomicInteger(0);

        dropdowns.forEach(dropdown -> {
            WebElement chosenDropdown = driver
                    .findElement(By.id(String.format("option%s", dropdownCounter.getAndIncrement())));

            if (dropdownCounter.get() > (numberOfFareStages + 1)) {
                return;
            }

            chosenDropdown.click();

            Select select = new Select(chosenDropdown);

            List<WebElement> dropdownOptions = select.getOptions();

            dropdownOptions.get(fareStageCounter.getAndIncrement()).click();

            if (fareStageCounter.get() == (numberOfFareStages + 1)) {
                fareStageCounter.set(0);
            }

            return;
        });

    }

    public static void uploadFaresTriangleCsvFile(WebDriver driver, String browserType)
            throws IOException, AWTException {
        URL url = new URL("https://fdbt-test-upload.s3.eu-west-2.amazonaws.com/Fares-Triangle-Example.csv");

        File csvFile = new File("../Fares-Triangle-Example.csv");

        FileUtils.copyURLToFile(url, csvFile);

        if (browserType.equals("chrome") || browserType.equals("internet explorer")) {
            waitForElement(driver, "csv-upload");

            WebElement upload = driver.findElement(By.id("csv-upload"));

            ((RemoteWebElement) upload).setFileDetector(new LocalFileDetector());

            upload.sendKeys("../Fares-Triangle-Example.csv");

        } else if (browserType.equals("firefox")) {

            waitForElement(driver, "csv-upload");

            driver.findElement(By.xpath("//input[@type='file']")).sendKeys(csvFile.getAbsolutePath());
        }

    }

    public static void uploadFareZoneCsvFile(WebDriver driver, String browserType) throws IOException {
        URL url = new URL("https://fdbt-test-upload.s3.eu-west-2.amazonaws.com/Fare-Zone-Example.csv");

        File csvFile = new File("../Fare-Zone-Example.csv");

        FileUtils.copyURLToFile(url, csvFile);

        if (browserType.equals("chrome") || browserType.equals("internet explorer")) {
            waitForElement(driver, "csv-upload");

            WebElement upload = driver.findElement(By.id("csv-upload"));

            ((RemoteWebElement) upload).setFileDetector(new LocalFileDetector());

            upload.sendKeys("../Fare-Zone-Example.csv");

        } else if (browserType.equals("firefox")) {
            waitForElement(driver, "csv-upload");

            driver.findElement(By.xpath("//input[@type='file']")).sendKeys(csvFile.getAbsolutePath());
        }

    }

    public static String makeRandomDecisionBetweenTwoChoices(String firstElementId, String secondElementId) {
        Random random = new Random();
        String chosenSelector;
        int number = random.nextInt(2) + 1;
        if (number == 2) {
            chosenSelector = secondElementId;
        } else {
            chosenSelector = firstElementId;
        }
        return chosenSelector;
    }

    public static void clickSelectedNumberOfCheckboxes(WebDriver driver, boolean selectAll) {

        List<WebElement> checkboxes = driver.findElements(By.className("govuk-checkboxes__item"));
        int numberOfCheckboxes = checkboxes.size();

        for (int i = 0; i < numberOfCheckboxes; i++) {
            WebElement chosenCheckbox = driver.findElement(By.id(String.format("checkbox-%s", i)));
            chosenCheckbox.click();
            if (selectAll == false) {
                Random random = new Random();
                int iterator = random.nextInt((numberOfCheckboxes - i)) + 1;
                i += iterator;
            }
        }
        return;
    }

    public static void randomlyChooseAndSelectServices(WebDriver driver) {
        Random random = new Random();
        int randomSelector = random.nextInt(4) + 1;
        switch (randomSelector) {
            case 1:
                // 1. Click Select All button and continue
                clickElementById(driver, "select-all-button");
                waitForPageToLoad(driver);
                break;
            case 2:
                // 2. Loop through checkboxes and click all, then continue
                boolean selectAll = true;
                clickSelectedNumberOfCheckboxes(driver, selectAll);
                break;
            case 3:
                // 3. Loop through checkboxes and click random ones, then continue.
                selectAll = false;
                clickSelectedNumberOfCheckboxes(driver, selectAll);
                break;
            case 4:
                // 4. Click Select All button and then click random checkboxes to deselect, then
                // continue
                clickElementById(driver, "select-all-button");
                waitForPageToLoad(driver);
                selectAll = false;
                clickSelectedNumberOfCheckboxes(driver, selectAll);
                break;
            case 5:
                // 5. Loop through checkboxes and click all and then click random checkboxes to
                // deselect, then continue.
                selectAll = true;
                clickSelectedNumberOfCheckboxes(driver, selectAll);
                selectAll = false;
                clickSelectedNumberOfCheckboxes(driver, selectAll);
                break;
        }

    }

    public static boolean isUuidStringValid(WebDriver driver) {
        waitForElement(driver, "uuid-ref-number");
        String rawUuid = driver.findElement(By.id("uuid-ref-number")).getText();
        String uuid = rawUuid.replace("Your reference number\n", "");
        return uuid.matches("[0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12}");
    }

    public static void clickElementById(WebDriver driver, String id) {
        try {
            waitForElement(driver, id);
        } catch (NoSuchElementException err){
            // Refreshing page in case the fluent wait library fails. 
            // If we fix the waitForElement method properly, we can remove this.
            System.out.println("----Refresh page method called----");
            driver.get(driver.getCurrentUrl());
            waitForPageToLoad(driver);
        }
    
        driver.findElement(By.id(id)).click();
    }
}
