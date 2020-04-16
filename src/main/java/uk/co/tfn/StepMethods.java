package uk.co.tfn;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

import static uk.co.tfn.HelperMethods.continueButtonClick;
import static uk.co.tfn.HelperMethods.startPageButtonClick;
import static uk.co.tfn.HelperMethods.waitForElement;

public class StepMethods {

    public static void stepsToInputMethod(WebDriver driver) {
        startPageButtonClick(driver);

        driver.findElement((By.id("operator-name0"))).click();

        continueButtonClick(driver);

        waitForElement(driver, "fareType-single");

        driver.findElement(By.id("fareType-single")).click();

        continueButtonClick(driver);

        driver.findElement(By.id("service")).click();

        waitForElement(driver, "service");

        Select serviceDropdown = new Select(driver.findElement(By.id("service")));

        serviceDropdown.selectByVisibleText("11 - Start date 02/01/2020");

        continueButtonClick(driver);

        driver.findElement(By.id("journeyPattern")).click();
        Select directionDropdown = new Select(driver.findElement(By.id("journeyPattern")));

        List<WebElement> directionDropdownOptions = directionDropdown.getOptions();

        directionDropdownOptions.get(1).click();

        continueButtonClick(driver);
    }

    public static void stepsToPeriodPage(WebDriver driver) {
        startPageButtonClick(driver);

        driver.findElement((By.id("operator-name0"))).click();

        continueButtonClick(driver);

        driver.findElement(By.id("fareType-period")).click();

        continueButtonClick(driver);
    }

    public static void fillInManualFareStages(WebDriver driver) {
        String stageArray[] = new String[] { "1", "2", "3", "4", "5", "6", "7" };

        int i;
        String x;
        for (i = 0; i < stageArray.length; i++) {
            x = stageArray[i];
            WebElement fareStage = driver.findElement(By.id("fareStageName" + x));
            fareStage.sendKeys("test" + x);
        }
    }

    public static void fillInFareStageTriangle(WebDriver driver) {
        String columnArray[] = new String[] { "100", "100", "50", "100", "250", "300", "450" };

        for (int row = 1; row < 7; row++) {
            for (int column = 0; column < row; column++) {
                WebElement fareStage = driver.findElement(By.id("cell-" + row + "-" + column));
                fareStage.sendKeys(columnArray[row]);
            }

        }
    }
}
