package uk.co.tfn;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

import static uk.co.tfn.HelperMethods.continueButtonClick;
import static uk.co.tfn.HelperMethods.startPageButtonClick;
import static uk.co.tfn.HelperMethods.waitForElement;
import static uk.co.tfn.HelperMethods.makeRandomDecisionBetweenTwoChoices;

public class StepMethods {

    public static void stepsToInputMethod(WebDriver driver) {
        startPageButtonClick(driver);

        driver.findElement((By.id("operator-name0"))).click();

        continueButtonClick(driver);

        waitForElement(driver, "fare-type-single");

        driver.findElement(By.id("fare-type-single")).click();

        continueButtonClick(driver);

        driver.findElement(By.id("service")).click();

        waitForElement(driver, "service");

        Select serviceDropdown = new Select(driver.findElement(By.id("service")));

        serviceDropdown.selectByVisibleText("11 - Start date 05/04/2020");

        continueButtonClick(driver);

        driver.findElement(By.id("directionJourneyPattern")).click();
        Select directionDropdown = new Select(driver.findElement(By.id("directionJourneyPattern")));

        List<WebElement> directionDropdownOptions = directionDropdown.getOptions();

        directionDropdownOptions.get(1).click();

        continueButtonClick(driver);
    }

    public static void stepsToPeriodPage(WebDriver driver) {
        startPageButtonClick(driver);

        driver.findElement((By.id("operator-name0"))).click();

        continueButtonClick(driver);

        driver.findElement(By.id("fare-type-period")).click();

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

    public static void enterDetailsAndSelectValidityForMultipleProducts(WebDriver driver, int numberOfProducts) {
        for (int i = 0; i < numberOfProducts; i++) {
            driver.findElement(By.id(String.format("multipleProductName%s", i)))
                    .sendKeys(String.format("Product %s", i));
            driver.findElement(By.id(String.format("multipleProductPrice%s", i))).sendKeys("3.67");
            driver.findElement(By.id(String.format("multipleProductDuration%s", i))).sendKeys("7");
        }
        continueButtonClick(driver);

        for (int i = 0; i < numberOfProducts; i++) {
            String twentyFourHourId = String.format("twenty-four-hours-row%s", i);
            String calendayDayId = String.format("calendar-day-row%s", i);
            String validitySelectionId = makeRandomDecisionBetweenTwoChoices(twentyFourHourId, calendayDayId);
            driver.findElement(By.id(validitySelectionId)).click();
        }

        continueButtonClick(driver);
    }
}
