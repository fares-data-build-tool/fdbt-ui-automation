package uk.co.tfn;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;


public class HelperMethods {

    public static void waitForPageToLoad(WebDriver driver) {
        new WebDriverWait(driver, 10).until(
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

        // any options we want to use

        return options;
    }

    public static ChromeDriverService setDriverService(){
        return new ChromeDriverService.Builder()
                .usingDriverExecutable(new File("/usr/local/chromedriver"))
                .usingAnyFreePort()
                .build();
    }
}
