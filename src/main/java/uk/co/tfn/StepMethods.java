package uk.co.tfn;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

public class StepMethods {
    private final HelperMethods helpers;
    private final WebDriver driver;

    public StepMethods(HelperMethods helpers, WebDriver driver) {
        this.helpers = helpers;
        this.driver = driver;
    }

    public void stepsToInputMethod() {
        this.helpers.startPageButtonClick();

        this.helpers.clickElementById("operator-name0");

        this.helpers.continueButtonClick();

        this.helpers.clickElementById("fare-type-single");

        this.helpers.continueButtonClick();

        this.helpers.clickElementById("passenger-type0");

        this.helpers.continueButtonClick();

        this.helpers.clickElementById("service");

        this.helpers.waitForElement("service");

        Select serviceDropdown = new Select(this.driver.findElement(By.id("service")));

        serviceDropdown.selectByVisibleText("11 - Start date 05/04/2020");

        this.helpers.continueButtonClick();

        this.helpers.clickElementById("directionJourneyPattern");

        Select directionDropdown = new Select(this.driver.findElement(By.id("directionJourneyPattern")));

        List<WebElement> directionDropdownOptions = directionDropdown.getOptions();

        directionDropdownOptions.get(1).click();

        this.helpers.continueButtonClick();
    }

    public void stepsToPeriodPage() {
        this.helpers.startPageButtonClick();

        this.helpers.clickElementById("operator-name0");

        this.helpers.continueButtonClick();

        this.helpers.clickElementById("fare-type-period");

        this.helpers.continueButtonClick();

        this.helpers.clickElementById("passenger-type0");

        this.helpers.continueButtonClick();
    }
}
