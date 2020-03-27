package uk.co.tfn;

import org.apache.commons.io.FileUtils;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;


public class HelperMethods {

    public static void waitForPageToLoad(WebDriver driver) {
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(
                webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
    }

    public static void getHomePage(WebDriver driver) {
        driver.get("https://tfn-test.infinityworks.com/");
    }

    public static void makePageFullScreen(WebDriver driver) {
        driver.manage().window().fullscreen();
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

    public static void continueButtonClick(WebDriver driver){
        driver.findElement(By.id("continue-button")).click();
        waitForPageToLoad(driver);
    }

    public static void submitButtonClick(WebDriver driver){
        driver.findElement(By.id("submit-button")).click();
        waitForPageToLoad(driver);
    }

    public static void startPageButtonClick(WebDriver driver){
        driver.findElement((By.id("start-now-button"))).click();
        waitForPageToLoad(driver);
    }

    public static void waitForElement(WebDriver driver, String elementId){
        FluentWait<WebDriver> fluentWait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(30))
                .pollingEvery(Duration.ofMillis(200))
                .ignoring(NoSuchElementException.class);

        fluentWait.until((Function<WebDriver, WebElement>) driver1 -> driver1.findElement(By.id(elementId)));
    }

    public static void waitForElementToBeClickable(WebDriver driver, String elementId){
        FluentWait<WebDriver> fluentWait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(30))
                .pollingEvery(Duration.ofMillis(200))
                .ignoring(NoSuchElementException.class);

        fluentWait.until(ExpectedConditions.elementToBeClickable(By.id(elementId)));
    }

    public static void fillInFareStageOptions(WebDriver driver, int range){

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

    public static void uploadCsvFile(WebDriver driver) throws IOException {
        URL url = new URL("https://fdbt-test-upload.s3.eu-west-2.amazonaws.com/Fares-Triangle-Example.csv");

        File a = new File("../Fares-Triangle-Example.csv");

        FileUtils.copyURLToFile(url, a);

        waitForElementToBeClickable(driver, "csv-upload");

        WebElement upload = driver.findElement(By.id("csv-upload"));

        ((RemoteWebElement) upload ).setFileDetector(new LocalFileDetector());

        upload.sendKeys("../Fares-Triangle-Example.csv");

    }

    public static boolean isUuidStringValid(WebDriver driver){
        waitForElement(driver, "uuid-ref-number");

        String rawUuid = driver.findElement(By.id("uuid-ref-number")).getText();

        String uuid = rawUuid.replace("Your reference number\n", "");

        return uuid.matches("[0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12}");

    }
}
