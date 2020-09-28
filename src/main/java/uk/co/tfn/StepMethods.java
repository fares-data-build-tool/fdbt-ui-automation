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

    public void stepsToSingleTicketInputMethod() throws InterruptedException {
        this.helpers.startPageButtonClick();
        this.helpers.clickElementById("fare-type-single");
        this.helpers.continueButtonClick();
        this.helpers.randomlyDetermineUserType();
        this.helpers.randomlyDecideTimeRestrictions();
        helpers.continueButtonClick();
        this.helpers.selectRandomOptionFromDropdownById("service");
        this.helpers.continueButtonClick();
        this.helpers.clickElementById("direction-journey-pattern");
        Select directionDropdown = new Select(this.driver.findElement(By.id("direction-journey-pattern")));
        List<WebElement> directionDropdownOptions = directionDropdown.getOptions();
        directionDropdownOptions.get(1).click();
        this.helpers.continueButtonClick();
    }

    public void stepsToPeriodPage() {
        this.helpers.startPageButtonClick();
        this.helpers.clickElementById("fare-type-period");
        this.helpers.continueButtonClick();
        this.helpers.randomlyDetermineUserType();
        this.helpers.randomlyDecideTimeRestrictions();
        helpers.continueButtonClick();
    }

    public void stepsToSelectFlatFareServiceSelection() {
        this.helpers.startPageButtonClick();
        this.helpers.clickElementById("fare-type-flat-fare");
        this.helpers.continueButtonClick();
        this.helpers.randomlyDetermineUserType();
        this.helpers.randomlyDecideTimeRestrictions();
        this.helpers.continueButtonClick();
    }

    public void stepsToReturnTicketInputMethod() throws InterruptedException {
        this.helpers.startPageButtonClick();
        this.helpers.clickElementById("fare-type-return");
        this.helpers.continueButtonClick();
        this.helpers.randomlyDetermineUserType();
        this.helpers.randomlyDecideTimeRestrictions();
        this.helpers.continueButtonClick();
        this.helpers.selectRandomOptionFromDropdownById("service");
        this.helpers.continueButtonClick();
        this.helpers.selectInboundAndOutboundDirections();
        this.helpers.continueButtonClick();
    }
}
