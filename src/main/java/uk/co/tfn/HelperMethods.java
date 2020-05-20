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
    private final WebDriver driver;
    private final String browser;
    private final String host;

    public static DesiredCapabilities setCapabilities() {

        DesiredCapabilities caps = new DesiredCapabilities();

        // any capabilities we want to set

        return caps;
    }

    public static ChromeOptions setChromeOptions() {
        ChromeOptions options = new ChromeOptions();

        options.setPageLoadStrategy(PageLoadStrategy.EAGER);
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-extensions");

        return options;
    }

    public static ChromeDriverService setChromeDriverService() {
        return new ChromeDriverService.Builder().usingDriverExecutable(new File("/usr/local/chromedriver"))
                .usingAnyFreePort().build();
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

    public HelperMethods(WebDriver driver, String browser, String host) {
        this.driver = driver;
        this.browser = browser;
        this.host = host;
    }

    public void waitForPageToLoad() {
        new WebDriverWait(this.driver, Duration.ofSeconds(10)).until(webDriver -> ((JavascriptExecutor) webDriver)
                .executeScript("return document.readyState").equals("complete"));
    }

    public void getHomePage() {
        this.driver.get("https://tfn-test.infinityworks.com/");
    }

    public void continueButtonClick() {
        this.clickElementById("continue-button");
        this.waitForPageToLoad();
    }

    public void submitButtonClick() {
        this.clickElementById("submit-button");
        this.waitForPageToLoad();
    }

    public void startPageButtonClick() {
        this.clickElementById("start-now-button");
        this.waitForPageToLoad();
    }

    public WebElement waitForElement(String elementId) {

        FluentWait<WebDriver> fluentWait = new FluentWait<WebDriver>(this.driver).withTimeout(Duration.ofSeconds(30))
                .pollingEvery(Duration.ofMillis(200)).ignoring(NoSuchElementException.class);

        return fluentWait.until(new Function<WebDriver, WebElement>() {
            public WebElement apply(WebDriver driver) {
                return driver.findElement(By.id(elementId));
            }
        });
    }

    public void fillInFareStageOptions(int numberOfFareStages) {

        List<WebElement> dropdowns = this.driver.findElements(By.className("farestage-select-wrapper"));

        final AtomicInteger dropdownCounter = new AtomicInteger(0);
        final AtomicInteger fareStageCounter = new AtomicInteger(0);

        dropdowns.forEach(dropdown -> {
            WebElement chosenDropdown = this.driver
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

    public void uploadFaresTriangleCsvFile() throws IOException, AWTException {
        URL url = new URL("https://fdbt-test-upload.s3.eu-west-2.amazonaws.com/Fares-Triangle-Example.csv");

        File csvFile = new File("Fares-Triangle-Example.csv");

        if (!csvFile.exists()) {
            csvFile.createNewFile();
        }

        FileUtils.copyURLToFile(url, csvFile);

        if (this.browser.equals("chrome") || this.browser.equals("ie")) {
            this.waitForElement("csv-upload");

            WebElement upload = this.driver.findElement(By.id("csv-upload"));

            if (this.host.equals("local")) {
                ((RemoteWebElement) upload).setFileDetector(new LocalFileDetector());
            }

            upload.sendKeys("Fares-Triangle-Example.csv");

        } else if (this.browser.equals("firefox")) {

            this.waitForElement("csv-upload");

            this.driver.findElement(By.xpath("//input[@type='file']")).sendKeys(csvFile.getAbsolutePath());
        }

    }

    public void uploadFareZoneCsvFile() throws IOException {
        URL url = new URL("https://fdbt-test-upload.s3.eu-west-2.amazonaws.com/Fare-Zone-Example.csv");

        File csvFile = new File("Fare-Zone-Example.csv");

        if (!csvFile.exists()) {
            csvFile.createNewFile();
        }

        FileUtils.copyURLToFile(url, csvFile);

        if (this.browser.equals("chrome") || this.browser.equals("ie")) {
            this.waitForElement("csv-upload");

            WebElement upload = this.driver.findElement(By.id("csv-upload"));

            if (this.host.equals("local")) {
                ((RemoteWebElement) upload).setFileDetector(new LocalFileDetector());
            }

            upload.sendKeys("Fare-Zone-Example.csv");

        } else if (this.browser.equals("firefox")) {
            this.waitForElement("csv-upload");

            this.driver.findElement(By.xpath("//input[@type='file']")).sendKeys(csvFile.getAbsolutePath());
        }

    }

    public void clickSelectedNumberOfCheckboxes(boolean selectAll) {

        List<WebElement> checkboxes = this.driver.findElements(By.className("govuk-checkboxes__item"));
        int numberOfCheckboxes = checkboxes.size();

        for (int i = 0; i < numberOfCheckboxes; i++) {
            WebElement chosenCheckbox = this.driver.findElement(By.id(String.format("checkbox-%s", i)));

            if (this.browser.equals("ie")) {
                JavascriptExecutor executor = (JavascriptExecutor) this.driver;
                executor.executeScript("arguments[0].click();", chosenCheckbox);
            } else {
                chosenCheckbox.click();
            }

            if (selectAll == false) {
                Random random = new Random();
                int iterator = random.nextInt((numberOfCheckboxes - i)) + 1;
                i += iterator;
            }
        }
        return;
    }

    public void randomlyChooseAndSelectServices() {
        Random random = new Random();
        int randomSelector = random.nextInt(4) + 1;
        switch (randomSelector) {
            case 1:
                // 1. Click Select All button and continue
                this.clickElementById("select-all-button");
                this.waitForPageToLoad();
                break;
            case 2:
                // 2. Loop through checkboxes and click all, then continue
                boolean selectAll = true;
                this.clickSelectedNumberOfCheckboxes(selectAll);
                break;
            case 3:
                // 3. Loop through checkboxes and click random ones, then continue.
                selectAll = false;
                this.clickSelectedNumberOfCheckboxes(selectAll);
                break;
            case 4:
                // 4. Click Select All button and then click random checkboxes to deselect, then
                // continue
                this.clickElementById("select-all-button");
                this.waitForPageToLoad();
                selectAll = false;
                this.clickSelectedNumberOfCheckboxes(selectAll);
                break;
            case 5:
                // 5. Loop through checkboxes and click all and then click random checkboxes to
                // deselect, then continue.
                selectAll = true;
                this.clickSelectedNumberOfCheckboxes(selectAll);
                selectAll = false;
                this.clickSelectedNumberOfCheckboxes(selectAll);
                break;
        }

    }

    public boolean isUuidStringValid() {
        this.waitForElement("uuid-ref-number");
        String rawUuid = this.driver.findElement(By.id("uuid-ref-number")).getText();
        String uuid = rawUuid.replace("Your reference number\n", "");
        return uuid.matches("[0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12}");
    }

    public void fillInManualFareStages() {
        String stageArray[] = new String[] { "1", "2", "3", "4", "5", "6", "7" };

        int i;
        String x;
        for (i = 0; i < stageArray.length; i++) {
            x = stageArray[i];
            WebElement fareStage = this.driver.findElement(By.id("fareStageName" + x));
            fareStage.sendKeys("test" + x);
        }
    }

    public void fillInFareStageTriangle() {
        String columnArray[] = new String[] { "100", "100", "50", "100", "250", "300", "450" };

        for (int row = 1; row < 7; row++) {
            for (int column = 0; column < row; column++) {
                WebElement fareStage = this.driver.findElement(By.id("cell-" + row + "-" + column));
                fareStage.sendKeys(columnArray[row]);
            }

        }
    }

    public void enterDetailsAndSelectValidityForMultipleProducts(int numberOfProducts) {
        for (int i = 0; i < numberOfProducts; i++) {
            this.driver.findElement(By.id(String.format("multipleProductName%s", i)))
                    .sendKeys(String.format("Product %s", i));
            this.driver.findElement(By.id(String.format("multipleProductPrice%s", i))).sendKeys("3.67");
            this.driver.findElement(By.id(String.format("multipleProductDuration%s", i))).sendKeys("7");
        }
        this.continueButtonClick();

        for (int i = 0; i < numberOfProducts; i++) {
            String twentyFourHourId = String.format("twenty-four-hours-row%s", i);
            String calendayDayId = String.format("calendar-day-row%s", i);
            String validitySelectionId = makeRandomDecisionBetweenTwoChoices(twentyFourHourId, calendayDayId);
            this.clickElementById(validitySelectionId);
        }

        this.continueButtonClick();
    }

    public void clickElementById(String id) {
        try {
            WebElement element = this.waitForElement(id);

            if (this.browser.equals("ie")) {
                JavascriptExecutor executor = (JavascriptExecutor) this.driver;
                executor.executeScript("arguments[0].click();", element);
            } else {
                element.click();
            }
        } catch (NoSuchElementException err) {
            // Refreshing page in case the fluent wait library fails.
            // If we fix the waitForElement method properly, we can remove this.
            System.out.println("----Refresh page method called----");
            this.driver.get(this.driver.getCurrentUrl());
            this.waitForPageToLoad();
        }
    }
}
