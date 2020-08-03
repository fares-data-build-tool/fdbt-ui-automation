package uk.co.tfn;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.awt.AWTException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

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
        return new ChromeDriverService.Builder().usingAnyFreePort().build();
    }

    public static String makeRandomDecisionBetweenTwoChoices(String firstElementId, String secondElementId) {
        String chosenSelector;
        int number = randomNumberBetweenOneAnd(2);
        if (number == 2) {
            chosenSelector = secondElementId;
        } else {
            chosenSelector = firstElementId;
        }
        return chosenSelector;
    }

    public static int randomNumberBetweenOneAnd(int x) {
        Random random = new Random();
        int result = random.nextInt(x) + 1;
        return result;
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
        this.driver.manage().deleteAllCookies();
        this.driver.get("https://tfn-test.infinityworks.com/?disableAuth=true");
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
        WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
        return wait.until(ExpectedConditions.presenceOfElementLocated(By.id(elementId)));
    }

    public void fillInFareStageOptions(int numberOfFareStages) {

        List<WebElement> dropdowns = this.driver.findElements(By.className("farestage-select-wrapper"));

        final AtomicInteger dropdownCounter = new AtomicInteger(0);
        final AtomicInteger fareStageCounter = new AtomicInteger(0);

        dropdowns.forEach(dropdown -> {
            WebElement chosenDropdown = this.driver
                    .findElement(By.id(String.format("option-%s", dropdownCounter.getAndIncrement())));

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
                int iterator = randomNumberBetweenOneAnd(numberOfCheckboxes);
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
        if (uuid.substring(0, 4).equals("BLAC") && uuid.length() == 12) {
            return true;
        }
        return false;
    }

    public void fillInManualFareStagesNames(int numberOfFareStages) {
        ArrayList<String> stageArray = new ArrayList<String>();

        for (int i = 1; i <= numberOfFareStages; i++) {
            stageArray.add(String.valueOf(i));
        }

        for (int i = 0; i < stageArray.size(); i++) {
            String x = stageArray.get(i);
            WebElement fareStage = this.driver.findElement(By.id("fare-stage-name-" + x));
            fareStage.sendKeys("test" + x);
        }
    }

    public void fillInFareStageTriangle(int numberOfFareStages) {
        WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
        wait.until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                WebElement firstCell = driver.findElement(By.id("cell-1-0"));
                if (firstCell.isDisplayed() && firstCell.isEnabled()) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        ArrayList<String> columnArray = new ArrayList<String>();

        for (int i = 1; i <= numberOfFareStages; i++) {
            columnArray.add(String.valueOf(i * 100));
        }

        for (int row = 1; row < numberOfFareStages; row++) {
            for (int column = 0; column < row; column++) {
                WebElement fareStage = this.driver.findElement(By.id("cell-" + row + "-" + column));
                fareStage.sendKeys(columnArray.get(row));
            }

        }
    }

    public void enterDetailsAndSelectValidityForMultipleProducts(int numberOfProducts) {
        for (int i = 0; i < numberOfProducts; i++) {
            this.driver.findElement(By.id(String.format("multiple-product-name-%s", i)))
                    .sendKeys(String.format("Product %s", i));
            this.driver.findElement(By.id(String.format("multiple-product-price-%s", i))).sendKeys("3.67");
            this.driver.findElement(By.id(String.format("multiple-product-duration-%s", i))).sendKeys("7");
        }
        this.continueButtonClick();

        for (int i = 0; i < numberOfProducts; i++) {
            String twentyFourHourId = String.format("twenty-four-hours-row-%s", i);
            String calendayDayId = String.format("calendar-day-row-%s", i);
            String validitySelectionId = makeRandomDecisionBetweenTwoChoices(twentyFourHourId, calendayDayId);
            this.clickElementById(validitySelectionId);
        }

        this.continueButtonClick();
    }

    public void clickElementById(String id) {

        WebElement element = this.waitForElement(id);

        if (this.browser.equals("ie")) {
            JavascriptExecutor executor = (JavascriptExecutor) this.driver;
            executor.executeScript("arguments[0].click();", element);
        } else {
            element.click();
        }

    }

    public void sendKeysById(String id, String input) {
        WebElement element = this.waitForElement(id);

        if (this.browser.equals("ie")) {
            JavascriptExecutor executor = (JavascriptExecutor) this.driver;
            executor.executeScript("arguments[0].setAttribute('value', arguments[1])", element, input);
        } else {
            element.sendKeys(input);
        }
    }

    public void randomlyChooseAProof() {
        int randomSelector = randomNumberBetweenOneAnd(3);
        switch (randomSelector) {
            case 1:
                // 1. Membership card
                this.clickElementById("membership-card");
                break;
            case 2:
                // 2. Student card
                this.clickElementById("student-card");
                break;
            case 3:
                // 3. Identity Document
                this.clickElementById("identity-document");
                break;
        }
    }

    public void randomlyChooseAgeLimits() {
        int randomSelector = randomNumberBetweenOneAnd(4);
        switch (randomSelector) {
            case 1:
                // 1. Max age, no min age
                this.sendKeysById("age-range-max", "30");
                break;
            case 2:
                // 2. Min age, no max age
                this.sendKeysById("age-range-min", "12");
                break;
            case 3:
                // 3. Max and min age, diff values
                this.sendKeysById("age-range-min", "13");
                this.sendKeysById("age-range-max", "18");
                break;
            case 4:
                // 4. Max and min age, same values
                this.sendKeysById("age-range-min", "50");
                this.sendKeysById("age-range-max", "50");
                break;
        }
    }

    public void completeUserDetailsPage() {
        int randomSelector = randomNumberBetweenOneAnd(4);
        switch (randomSelector) {
            case 1:
                // 1. No to both questions
                this.clickElementById("age-range-not-required");
                this.clickElementById("proof-not-required");
                this.continueButtonClick();
                break;
            case 2:
                // 2. No to age limit, Yes to Proof
                this.clickElementById("age-range-not-required");
                this.clickElementById("proof-required");
                this.randomlyChooseAProof();
                this.continueButtonClick();
                break;
            case 3:
                // 3. Yes to age limit, Yes to Proof
                this.clickElementById("age-range-required");
                this.randomlyChooseAgeLimits();
                this.clickElementById("proof-required");
                this.randomlyChooseAProof();
                this.continueButtonClick();
                break;
            case 4:
                // 4. Yes to age limit, No to Proof
                this.clickElementById("age-range-required");
                this.randomlyChooseAgeLimits();
                this.clickElementById("proof-not-required");
                this.continueButtonClick();
                break;
        }
    }

    public void randomlyDetermineUserType() {
        int randomSelector = randomNumberBetweenOneAnd(2);
        switch (randomSelector) {
            case 1:
                // 1. Click Any and continue
                this.clickElementById("passenger-type-0");
                this.continueButtonClick();
                this.waitForPageToLoad();
                break;
            case 2:
                // 2. Click a non-Any, complete the next page, and continue
                int randomUserType = randomNumberBetweenOneAnd(6);

                WebElement element = this
                        .waitForElement(String.format("passenger-type-%s", String.valueOf(randomUserType)));

                if (this.browser.equals("ie")) {
                    JavascriptExecutor executor = (JavascriptExecutor) this.driver;
                    executor.executeScript("arguments[0].click();", element);
                } else {
                    element.click();
                }

                this.continueButtonClick();
                this.waitForPageToLoad();
                this.completeUserDetailsPage();
                break;
        }
    }

    public void selectRandomOptionFromDropdownById(String id) throws InterruptedException {
        WebElement chosenDropdown = driver.findElement(By.id(id));
        chosenDropdown.click();
        Select serviceDropdown = new Select(chosenDropdown);
        List<WebElement> serviceElements = serviceDropdown.getOptions();
        int randomSelector = HelperMethods.randomNumberBetweenOneAnd(serviceElements.size()) - 1;
        serviceElements.get(randomSelector).click();
    }

    public void selectInboundAndOutboundDirections() throws InterruptedException {
        List<WebElement> dropdowns = this.driver.findElements(By.className("govuk-select"));

        final AtomicInteger dropdownCounter = new AtomicInteger(0);

        dropdowns.forEach(dropdown -> {

            String id = "";
            if (dropdownCounter.get() == 0) {
                id = "outbound-journey";
            } else {
                id = "inbound-journey";
            }
            WebElement chosenDropdown = this.driver.findElement(By.id(id));

            chosenDropdown.click();

            Select select = new Select(chosenDropdown);

            List<WebElement> dropdownOptions = select.getOptions();

            int separatorNumber;

            separatorNumber = dropdownOptions.size() - 1;

            // this cannot be zero, as Select One is the disabled 0th option
            int chosenOption = dropdownOptions.size() - randomNumberBetweenOneAnd(separatorNumber);

            dropdownOptions.get(chosenOption).click();

            dropdownCounter.incrementAndGet();
        });
    }

    public void clickRandomSalesOfferPackages(List<WebElement> salesOfferPackages, int randomNumber) {
        JavascriptExecutor executor = (JavascriptExecutor) this.driver;
        switch (randomNumber) {
            case 1:
            if (this.browser.equals("ie")) {
                executor.executeScript("arguments[0].click();", salesOfferPackages.get(0));
            } else {
                salesOfferPackages.get(0).click();
            }
                break;
            case 2:
            if (this.browser.equals("ie")) {
                executor.executeScript("arguments[0].click();", salesOfferPackages.get(0));
                executor.executeScript("arguments[0].click();", salesOfferPackages.get(1));
            } else {
                salesOfferPackages.get(0).click();
                salesOfferPackages.get(1).click();
            }
            break;
            case 3:
            if (this.browser.equals("ie")) {
                executor.executeScript("arguments[0].click();", salesOfferPackages.get(0));
                executor.executeScript("arguments[0].click();", salesOfferPackages.get(1));
                executor.executeScript("arguments[0].click();", salesOfferPackages.get(2));
            } else {
                salesOfferPackages.get(0).click();
                salesOfferPackages.get(1).click();
                salesOfferPackages.get(2).click();
            }
            break;
            case 4:
            if (this.browser.equals("ie")) {
                executor.executeScript("arguments[0].click();", salesOfferPackages.get(0));
                executor.executeScript("arguments[0].click();", salesOfferPackages.get(1));
                executor.executeScript("arguments[0].click();", salesOfferPackages.get(2));
                executor.executeScript("arguments[0].click();", salesOfferPackages.get(3));
            } else {
                salesOfferPackages.get(0).click();
                salesOfferPackages.get(1).click();
                salesOfferPackages.get(2).click();
                salesOfferPackages.get(3).click();
            }
            break;
        }
    }

    public void selectSalesOfferPackages() {
        List<WebElement> salesOfferPackages = new ArrayList<WebElement>();
        salesOfferPackages.add(this.driver.findElement(By.id("product-0-checkbox-0")));
        salesOfferPackages.add(this.driver.findElement(By.id("product-0-checkbox-1")));
        salesOfferPackages.add(this.driver.findElement(By.id("product-0-checkbox-2")));
        salesOfferPackages.add(this.driver.findElement(By.id("product-0-checkbox-3")));
        int randomNumber = randomNumberBetweenOneAnd(4);
        clickRandomSalesOfferPackages(salesOfferPackages, randomNumber);
    }

    public void selectSalesOfferPackagesForMultipleProducts(int numberOfProducts) {
        for(int i = 0; i < numberOfProducts; i++){
            List<WebElement> productSalesOfferPackages = new ArrayList<WebElement>();
            productSalesOfferPackages.add(this.driver.findElement(By.id(String.format("product-%s-checkbox-0", i))));
            productSalesOfferPackages.add(this.driver.findElement(By.id(String.format("product-%s-checkbox-1", i))));
            productSalesOfferPackages.add(this.driver.findElement(By.id(String.format("product-%s-checkbox-2", i))));
            productSalesOfferPackages.add(this.driver.findElement(By.id(String.format("product-%s-checkbox-3", i))));
            int randomNumber = randomNumberBetweenOneAnd(4);
            clickRandomSalesOfferPackages(productSalesOfferPackages, randomNumber);
        }
    }
}
