package uk.co.tfn;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class HelperMethods {
    private final WebDriver driver;
    private final String browser;
    private final String host;
    private final JavascriptExecutor executor;

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
        this.executor = (JavascriptExecutor) driver;
    }

    public void waitForPageToLoad() {
        new WebDriverWait(this.driver, 10).until(webDriver -> ((JavascriptExecutor) webDriver)
                .executeScript("return document.readyState").equals("complete"));

    }

    public void javascriptClick(WebElement element) {
        executor.executeScript("arguments[0].click();", element);
    }

    public void getHomePage() {
        this.driver.manage().deleteAllCookies();
        this.driver.get("https://tfn-test.infinityworks.com/?disableAuth=true");
        this.driver.manage().window().maximize();
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
        WebDriverWait wait = new WebDriverWait(this.driver, 10);
        return wait.until(ExpectedConditions.presenceOfElementLocated(By.id(elementId)));
    }

    public void fillInFareStageOptions(int numberOfFareStages) {

        List<WebElement> dropdowns = this.driver.findElements(By.className("farestage-select"));

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
                javascriptClick(chosenCheckbox);
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
        WebDriverWait wait = new WebDriverWait(this.driver, 10);
        wait.until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                WebElement firstCell = driver.findElement(By.className("govuk-input"));
                if (firstCell.isDisplayed() && firstCell.isEnabled()) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        List<WebElement> fareTriangleCells = driver.findElements(By.className("govuk-input"));
        fareTriangleCells.forEach((cell) -> cell.sendKeys(String.valueOf(randomNumberBetweenOneAnd(10) * 100)));
    }

    public void enterDetailsAndSelectValidityForMultipleProducts(int numberOfProducts) {
        for (int i = 0; i < numberOfProducts; i++) {
            this.driver.findElement(By.id(String.format("multiple-product-name-%s", i)))
                    .sendKeys(String.format("Product %s", i));
            this.driver.findElement(By.id(String.format("multiple-product-price-%s", i))).sendKeys("3.67");
            this.driver.findElement(By.id(String.format("multiple-product-duration-%s", i))).sendKeys("7");
            this.selectRandomOptionFromDropdownById(String.format("multiple-product-duration-units-%s", i));
        }
        this.continueButtonClick();

        for (int i = 0; i < numberOfProducts; i++) {
            selectRandomOptionFromDropdownById("validity-option-0");
        }

        List<WebElement> endTimeInputBoxes = driver.findElements(By.className("govuk-input"));
        
        for (int i = 0; i < endTimeInputBoxes.size(); i++) {
            endTimeInputBoxes.get(i).sendKeys("0900");
        }

        this.continueButtonClick();
    }

    public void clickElementById(String id) {

        WebElement element = this.waitForElement(id);

        if (this.browser.equals("ie")) {
            javascriptClick(element);
        } else {
            element.click();
        }

    }

    public void sendKeysById(String id, String input) {
        WebElement element = this.waitForElement(id);

        if (this.browser.equals("ie")) {
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

    public void completeUserDetailsPage(boolean group, int maxNumber, boolean adult) {
        int randomSelector = randomNumberBetweenOneAnd(4);
        int secondRandomSelector = randomNumberBetweenOneAnd(2);
        if (group) {
            this.driver.findElement(By.id("min-number-of-passengers")).sendKeys("1");
            this.driver.findElement(By.id("max-number-of-passengers")).sendKeys(String.valueOf(maxNumber));
        }
        if (!adult) {
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
        } else {
            switch (secondRandomSelector) {
                case 1:
                    // 1. No to age range
                    this.clickElementById("age-range-not-required");
                    this.continueButtonClick();
                    break;
                case 2:
                    // 2. Yes to age range
                    this.clickElementById("age-range-required");
                    this.randomlyChooseAgeLimits();
                    this.continueButtonClick();
                    break;
            }
        }
    }

    public void completeSchoolPupilDefinePassengerTypePage() {
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

    public void selectTermTime() {
        int randomSelector = randomNumberBetweenOneAnd(2);

        if (randomSelector == 1) {
            this.clickElementById("term-time-yes");
        } else {
            this.clickElementById("term-time-no");
        }
        this.continueButtonClick();
    }

    public void randomlyDetermineUserType() {
        waitForPageToLoad();
        int randomSelector = randomNumberBetweenOneAnd(3);

        if (randomSelector == 1) {
            // Click Any and continue
            WebElement element = this.waitForElement("passenger-type-anyone");
            if (this.browser.equals("ie")) {
                javascriptClick(element);
            } else {
                element.click();
            }
            this.continueButtonClick();
            this.waitForPageToLoad();
        } else if (randomSelector == 2) {
            // Click Group, complete following pages, and continue
            WebElement element = this.waitForElement("passenger-type-group");
            if (this.browser.equals("ie")) {
                javascriptClick(element);
            } else {
                element.click();
            }
            this.continueButtonClick();
            this.waitForPageToLoad();
            this.completeGroupPassengerDetailsPages();
        } else {
            // Click a non-Any non-Group, complete the next page, and continue
            List<WebElement> otherPassengerTypes = new ArrayList<WebElement>();

            otherPassengerTypes.add(this.driver.findElement(By.id("passenger-type-adult")));
            otherPassengerTypes.add(this.driver.findElement(By.id("passenger-type-child")));
            otherPassengerTypes.add(this.driver.findElement(By.id("passenger-type-infant")));
            otherPassengerTypes.add(this.driver.findElement(By.id("passenger-type-senior")));
            otherPassengerTypes.add(this.driver.findElement(By.id("passenger-type-student")));
            otherPassengerTypes.add(this.driver.findElement(By.id("passenger-type-youngPerson")));

            WebElement chosenPassenger = otherPassengerTypes.get(randomNumberBetweenOneAnd(6) - 1);
            boolean adult = false;

            if (this.browser.equals("ie")) {
                javascriptClick(chosenPassenger);
            } else {
                chosenPassenger.click();
            }
            this.continueButtonClick();
            this.waitForPageToLoad();
            if (this.driver.findElement(By.id("define-passenger-age-range")).getText().contains("adult")) {
                adult = true;
            }
            if (adult) {
                this.completeUserDetailsPage(false, 0, true);
            } else {
                this.completeUserDetailsPage(false, 0, false);
            }

        }
    }

    public void completeGroupPassengerDetailsPages() {
        int groupSize = completeGroupSizePage();
        completeDefineGroupPassengersPage();

        String header = this.driver.findElement(By.id("number-of-passenger-type-heading")).getText();
        if (header.contains("adult")) {
            this.completeUserDetailsPage(true, groupSize, true);
        } else {
            this.completeUserDetailsPage(true, groupSize, false);
        }
        waitForPageToLoad();
        String secondHeader = this.driver.findElement(By.id("number-of-passenger-type-heading")).getText();
        if (secondHeader.contains("adult")) {
            this.completeUserDetailsPage(true, groupSize, true);
        } else {
            this.completeUserDetailsPage(true, groupSize, false);
        }
    }

    public int completeGroupSizePage() {
        int groupSize = randomNumberBetweenOneAnd(29) + 1;
        this.driver.findElement(By.id("max-group-size")).sendKeys(String.valueOf(groupSize));
        this.continueButtonClick();
        this.waitForPageToLoad();
        return groupSize;
    }

    public void completeDefineGroupPassengersPage() {
        int firstRandomNumber = randomNumberBetweenOneAnd(7) - 1;
        WebElement firstPassengerType = this.driver
                .findElement(By.id(String.format("passenger-type-%s", String.valueOf(firstRandomNumber))));
        if (this.browser.equals("ie")) {
            javascriptClick(firstPassengerType);
        } else {
            firstPassengerType.click();
        }

        int secondRandomNumber = randomNumberBetweenOneAnd(7) - 1;
        while (firstRandomNumber == secondRandomNumber) {
            secondRandomNumber = randomNumberBetweenOneAnd(7) - 1;
        }
        WebElement secondPassengerType = this.driver
                .findElement(By.id(String.format("passenger-type-%s", String.valueOf(secondRandomNumber))));
        if (this.browser.equals("ie")) {
            javascriptClick(secondPassengerType);
        } else {
            secondPassengerType.click();
        }

        this.continueButtonClick();
        this.waitForPageToLoad();
    }

    public void selectRandomOptionFromDropdownById(String id) {
        WebElement chosenDropdown = driver.findElement(By.id(id));
        chosenDropdown.click();
        Select serviceDropdown = new Select(chosenDropdown);
        List<WebElement> serviceElements = serviceDropdown.getOptions();
        int randomSelector = HelperMethods.randomNumberBetweenOneAnd(serviceElements.size()) - 1;
        if (randomSelector == 0) {
            randomSelector = randomSelector += 1;
        }
        serviceElements.get(randomSelector).click();
    }

    public void selectInboundAndOutboundDirections() {
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
        switch (randomNumber) {
            case 1:
                if (this.browser.equals("ie")) {
                    javascriptClick(salesOfferPackages.get(0));
                } else {
                    salesOfferPackages.get(0).click();
                }
                break;
            case 2:
                if (this.browser.equals("ie")) {
                    javascriptClick(salesOfferPackages.get(0));
                    javascriptClick(salesOfferPackages.get(1));
                } else {
                    salesOfferPackages.get(0).click();
                    salesOfferPackages.get(1).click();
                }
                break;
            case 3:
                if (this.browser.equals("ie")) {
                    javascriptClick(salesOfferPackages.get(0));
                    javascriptClick(salesOfferPackages.get(1));
                    javascriptClick(salesOfferPackages.get(2));
                } else {
                    salesOfferPackages.get(0).click();
                    salesOfferPackages.get(1).click();
                    salesOfferPackages.get(2).click();
                }
                break;
            case 4:
                if (this.browser.equals("ie")) {
                    javascriptClick(salesOfferPackages.get(0));
                    javascriptClick(salesOfferPackages.get(1));
                    javascriptClick(salesOfferPackages.get(2));
                    javascriptClick(salesOfferPackages.get(3));
                } else {
                    salesOfferPackages.get(0).click();
                    salesOfferPackages.get(1).click();
                    salesOfferPackages.get(2).click();
                    salesOfferPackages.get(3).click();
                }
                break;
        }
    }

    public void selectSalesOfferPackages(String productName) {
        String productNameNoSpaces = productName.replaceAll("\\s+", "");
        List<WebElement> salesOfferPackages = new ArrayList<WebElement>();
        salesOfferPackages.add(this.driver.findElement(By.id(String.format("%s-checkbox-0", productNameNoSpaces))));
        salesOfferPackages.add(this.driver.findElement(By.id(String.format("%s-checkbox-1", productNameNoSpaces))));
        salesOfferPackages.add(this.driver.findElement(By.id(String.format("%s-checkbox-2", productNameNoSpaces))));
        salesOfferPackages.add(this.driver.findElement(By.id(String.format("%s-checkbox-3", productNameNoSpaces))));
        int randomNumber = randomNumberBetweenOneAnd(4);
        clickRandomSalesOfferPackages(salesOfferPackages, randomNumber);
    }

    public void selectSalesOfferPackagesForMultipleProducts(int numberOfProducts) {
        for (int i = 0; i < numberOfProducts; i++) {
            List<WebElement> productSalesOfferPackages = new ArrayList<WebElement>();
            productSalesOfferPackages.add(this.driver.findElement(By.id(String.format("Product%s-checkbox-0", i))));
            productSalesOfferPackages.add(this.driver.findElement(By.id(String.format("Product%s-checkbox-1", i))));
            productSalesOfferPackages.add(this.driver.findElement(By.id(String.format("Product%s-checkbox-2", i))));
            productSalesOfferPackages.add(this.driver.findElement(By.id(String.format("Product%s-checkbox-3", i))));
            int randomNumber = randomNumberBetweenOneAnd(4);
            clickRandomSalesOfferPackages(productSalesOfferPackages, randomNumber);
        }
    }

    public void selectNoToTimeRestrictions() {
        this.clickElementById("valid-days-not-required");
        this.continueButtonClick();
    }

    public void selectYesToTimeRestrictions() {
        this.clickElementById("valid-days-required");

        List<String> checkboxIds = Arrays.asList("monday","tuesday","wednesday","thursday","friday","saturday","sunday","bankHoliday");
        int randomNumber = randomNumberBetweenOneAnd(8);

        for (int i = 0; i < randomNumber; i++) {
            WebElement chosenCheckbox = this.driver.findElement(By.id(checkboxIds.get(i)));

            if (this.browser.equals("ie")) {
                javascriptClick(chosenCheckbox);
            } else {
                chosenCheckbox.click();
            }
        }
        this.continueButtonClick();

    }

    public void randomlyDecideTimeRestrictions() {
        if (randomNumberBetweenOneAnd(2) == 1) {
            selectNoToTimeRestrictions();
        } else {
            selectYesToTimeRestrictions();

            List<WebElement> inputs = this.driver.findElements(By.className("govuk-input"));
            List<String> timesAsStrings = Arrays.asList("0900", "0000", "2359", "0459", "1750", "1420", "");
            for (int i = 0; i < inputs.size(); i++) {
                String keysToSend = timesAsStrings.get(randomNumberBetweenOneAnd(7) - 1);
                if (this.browser.equals("ie")) {
                    executor.executeScript("arguments[0].setAttribute('value', arguments[1])", inputs.get(i), keysToSend);
                } else {
                    inputs.get(i).sendKeys(keysToSend);
                }
            }
        }
        this.continueButtonClick();
    }

    public void enterReturnTicketValidity() {
        if (randomNumberBetweenOneAnd(2) == 1) {
            this.clickElementById("return-validity-not-defined");
        } else {
            this.clickElementById("return-validity-defined");
            this.sendKeysById("return-validity-amount", "5");

            WebElement dropdown = this.driver.findElement(By.id("return-validity-units"));
            dropdown.click();

            Select select = new Select(dropdown);
            List<WebElement> dropdownOptions = select.getOptions();

            int separatorNumber;
            separatorNumber = dropdownOptions.size() - 1;
            // this cannot be zero, as Select One is the disabled 0th option
            int chosenOption = dropdownOptions.size() - randomNumberBetweenOneAnd(separatorNumber);
            dropdownOptions.get(chosenOption).click();
        }
    }

    public void completeProductDateInformationPage() {
        if (randomNumberBetweenOneAnd(2) == 1) {
            this.clickElementById("product-dates-information-not-required");
        } else {
            this.clickElementById("product-dates-required");
            int randomiser = randomNumberBetweenOneAnd(3);
            if (randomiser == 1) {
                this.sendKeysById("start-date-day", "13");
                this.sendKeysById("start-date-month", "10");
                this.sendKeysById("start-date-year", "2010");
            } else if (randomiser == 2) {
                this.sendKeysById("start-date-day", "13");
                this.sendKeysById("start-date-month", "10");
                this.sendKeysById("start-date-year", "2010");
                this.sendKeysById("end-date-day", "7");
                this.sendKeysById("end-date-month", "12");
                this.sendKeysById("end-date-year", "2025");
            } else {
                this.sendKeysById("end-date-day", "4");
                this.sendKeysById("end-date-month", "4");
                this.sendKeysById("end-date-year", "2030");
            }
        }
        this.continueButtonClick();
    }

    public int searchForOperators() {
        this.sendKeysById("search-input", "bus");
        this.clickElementById("search-button");
        this.waitForPageToLoad();
        List<WebElement> operatorCheckboxes = this.driver.findElements(By.className("govuk-checkboxes__input"));
        int numberOfCheckboxes = operatorCheckboxes.size();
        int randomNumberOfCheckboxesToClick = randomNumberBetweenOneAnd(numberOfCheckboxes);

        for (int i = 0; i < randomNumberOfCheckboxesToClick; i++) {
            if (this.browser.equals("ie")) {
                javascriptClick(operatorCheckboxes.get(i));
            } else {
                operatorCheckboxes.get(i).click();
            }
        }
        this.clickElementById("add-operator-button");
        this.waitForPageToLoad();
        if (randomNumberOfCheckboxesToClick > 1 && randomNumberBetweenOneAnd(2) == 1) {
            if (this.browser.equals("ie")) {
                javascriptClick(driver.findElement(By.id("remove-operator-checkbox-0")));
            } else {
                this.clickElementById("remove-operator-checkbox-0");
            }
            this.clickElementById("remove-operators-button");
            this.waitForPageToLoad();
            return randomNumberOfCheckboxesToClick - 1;
        }
        return randomNumberOfCheckboxesToClick;
    }
}
