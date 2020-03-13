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

import java.awt.desktop.SystemSleepEvent;
import java.io.File;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static uk.co.tfn.HelperMethods.*;

public class StepMethods {

    public static void stepsToInputMethod(ChromeDriver driver) {
        startPageButtonClick(driver);

        driver.findElement((By.id("operator-name0"))).click();

        continueButtonClick(driver);

        driver.findElement(By.id("faretype-single")).click();

        continueButtonClick(driver);

        driver.findElement(By.id("service")).click();

        waitForElement(driver,"service");

        Select serviceDropdown = new Select(driver.findElement(By.id("service")));

        serviceDropdown.selectByVisibleText("11 - Start date 02/01/2020");

        continueButtonClick(driver);

        driver.findElement(By.id("journeyPattern")).click();
        Select directionDropdown = new Select(driver.findElement(By.id("journeyPattern")));

        List<WebElement> directionDropdownOptions = directionDropdown.getOptions();

        directionDropdownOptions.get(1).click();

        continueButtonClick(driver);
    }

    public static void fillInManualFareStages(ChromeDriver driver) {
        String stageArray[] = new String[]{"1", "2", "3", "4", "5", "6", "7"};

        int i;
        String x;
        for (i = 0; i < stageArray.length; i++) {
            x = stageArray[i];
            WebElement fareStage = driver.findElement(By.id("fareStageName" + x));
            fareStage.sendKeys("test" + x);
        }
    }

    public static void fillInFareStageTriangle(ChromeDriver driver) {
        String columnArray[] = new String[]{"100", "100", "50", "100", "250", "300", "450"};


        String x;
        for (int row = 1; row < 7; row++) {
            for (int column = 0; column < row; column++) {
                WebElement fareStage = driver.findElement(By.id("cell-" + row + "-" + column));
                fareStage.sendKeys(columnArray[row]);
            }

        }
    }
}
