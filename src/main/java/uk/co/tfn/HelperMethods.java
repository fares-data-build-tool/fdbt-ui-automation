package uk.co.tfn;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;


public class HelperMethods {

    public static void waitForPageToLoad(ChromeDriver driver) {
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(
                webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
    }

    public static void getHomePage(WebDriver driver) {
        driver.get("https://tfn-test.infinityworks.com/");
    }

    public static DesiredCapabilities setCapabilities() {

        DesiredCapabilities caps = new DesiredCapabilities();

        // any capabilities we want to set

        return caps;
    }

    public static ChromeOptions setOptions(){
        ChromeOptions options = new ChromeOptions();

        options.setPageLoadStrategy(PageLoadStrategy.NONE);
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-extensions");

        return options;
    }

    public static ChromeDriverService setDriverService(){
        return new ChromeDriverService.Builder()
                .usingDriverExecutable(new File("/usr/local/chromedriver"))
                .usingAnyFreePort()
                .build();
    }

    public static void continueButtonClick(ChromeDriver driver){
        driver.findElement(By.id("continue-button")).click();
        waitForPageToLoad(driver);
    }

    public static void submitButtonClick(ChromeDriver driver){
        driver.findElement(By.id("submit-button")).click();
        waitForPageToLoad(driver);
    }

    public static void startPageButtonClick(ChromeDriver driver){
        driver.findElement((By.id("start-now-button"))).click();
        waitForPageToLoad(driver);
    }

    public static void waitForElement(ChromeDriver driver, String elementId){
        FluentWait<ChromeDriver> fluentWait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(30))
                .pollingEvery(Duration.ofMillis(200))
                .ignoring(NoSuchElementException.class);

        fluentWait.until((Function<WebDriver, WebElement>) driver1 -> driver1.findElement(By.id(elementId)));
    }

    public static void waitForElementToBeClickable(ChromeDriver driver, String elementId){
        FluentWait<ChromeDriver> fluentWait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(30))
                .pollingEvery(Duration.ofMillis(200))
                .ignoring(NoSuchElementException.class);

        fluentWait.until(ExpectedConditions.elementToBeClickable(By.id(elementId)));
    }

    public static void fillInFareStageOptions(ChromeDriver driver, int range){

        List<WebElement> dropdowns = driver.findElements(By.className("farestage-select-wrapper"));

        final AtomicInteger counter = new AtomicInteger(0);

        dropdowns.forEach(dropdown -> {
            WebElement chosenDropdown = driver.findElement(By.id(String.format("option%s", counter.getAndIncrement())));

            chosenDropdown.click();

            Select select = new Select(chosenDropdown);

            List<WebElement> dropdownOptions = select.getOptions();

            Random random = new Random();

            dropdownOptions.get(random.nextInt(range)).click();

            return;
        });

    }

    public static void uploadCsvFile(ChromeDriver driver, String filepath){
        waitForElementToBeClickable(driver, "csv-upload");

        WebElement upload = driver.findElement(By.id("csv-upload"));

        ((RemoteWebElement) upload ).setFileDetector(new LocalFileDetector());

        upload.sendKeys(filepath);
    }

    public static boolean isUuidStringValid(ChromeDriver driver){
        waitForElement(driver, "uuid-ref-number");

        String rawUuid = driver.findElement(By.id("uuid-ref-number")).getText();

        String uuid = rawUuid.replace("Your reference number\n", "");

        return uuid.matches("[0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12}");

    }
}
