package uk.co.tfn;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.awt.AWTException;
import java.io.IOException;
import java.util.List;

public class StepMethods {
    private final HelperMethods helpers;
    private final WebDriver driver;

    public StepMethods(HelperMethods helpers, WebDriver driver) {
        this.helpers = helpers;
        this.driver = driver;
    }

    public void compeleteServiceAndJourneyDirectionPages() {
        this.helpers.selectRandomOptionFromDropdownById("service");
        this.helpers.continueButtonClick();
        this.helpers.clickElementById("direction-journey-pattern");
        Select directionDropdown = new Select(this.driver.findElement(By.id("direction-journey-pattern")));
        List<WebElement> directionDropdownOptions = directionDropdown.getOptions();
        directionDropdownOptions.get(1).click();
        this.helpers.continueButtonClick();
    }

    public void stepsToSingleTicketInputMethod() {
        this.helpers.startPageButtonClick();
        this.helpers.clickElementById("fare-type-single");
        this.helpers.continueButtonClick();
        this.helpers.randomlyDetermineUserType();
        this.helpers.randomlyDecideTimeRestrictions();
        helpers.continueButtonClick();
        compeleteServiceAndJourneyDirectionPages();
    }

    public void stepsToPeriodPage() {
        this.helpers.startPageButtonClick();
        this.helpers.clickElementById("fare-type-period");
        this.helpers.continueButtonClick();
        this.helpers.randomlyDetermineUserType();
        this.helpers.randomlyDecideTimeRestrictions();
        helpers.continueButtonClick();
    }

    public void stepsToMultiOperatorPage() {
        this.helpers.startPageButtonClick();
        this.helpers.clickElementById("fare-type-multiOperator");
        this.helpers.continueButtonClick();
        this.helpers.randomlyDetermineUserType();
        this.helpers.randomlyDecideTimeRestrictions();
        helpers.continueButtonClick();
    }

    public void stepsToSelectFlatFareServiceSelection() {
        this.helpers.startPageButtonClick();
        this.helpers.clickElementById("fare-type-flatFare");
        this.helpers.continueButtonClick();
        this.helpers.randomlyDetermineUserType();
        this.helpers.randomlyDecideTimeRestrictions();
        this.helpers.continueButtonClick();
    }

    public void stepsToReturnTicketInputMethod() {
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

    public void stepsToSchoolFareTypePage() {
        this.helpers.startPageButtonClick();
        this.helpers.clickElementById("fare-type-schoolService");
        this.helpers.continueButtonClick();
        this.helpers.completeSchoolPupilDefinePassengerTypePage();
        this.helpers.selectTermTime();
    }

    public void completeSingleCsvUpload () throws IOException, AWTException {
        helpers.clickElementById("csv-upload");
        helpers.continueButtonClick();
        helpers.uploadFaresTriangleCsvFile();
        helpers.submitButtonClick();
        helpers.fillInFareStageOptions(8);
        helpers.submitButtonClick();
        helpers.continueButtonClick();
    }

    public void completeSingleManualTriangle() {
        helpers.clickElementById("manual-entry");
        helpers.continueButtonClick();
        helpers.clickElementById("less-than-20-fare-stages");
        helpers.continueButtonClick();
        helpers.waitForElement("fare-stages");
        int numberOfFareStages = 5;
        helpers.sendKeysById("fare-stages", String.valueOf(numberOfFareStages));
        helpers.continueButtonClick();
        helpers.fillInManualFareStagesNames(numberOfFareStages);
        helpers.continueButtonClick();
        helpers.continueButtonClick();
        helpers.fillInFareStageTriangle(numberOfFareStages);
        helpers.continueButtonClick();
        helpers.fillInFareStageOptions(numberOfFareStages);
        helpers.submitButtonClick();
        helpers.continueButtonClick();
    } 

    public void completeSingleSalesOfferPackageAndProductDatingPages() {
        helpers.selectSalesOfferPackages("product");
        helpers.continueButtonClick();
        helpers.completeProductDateInformationPage();
        helpers.continueButtonClick();
    }

    public void completeMultiServicePeriodPagesWithSingleProduct(String productName, boolean schoolService) {
        if (!schoolService) {
            helpers.clickElementById("set-of-services");
            helpers.continueButtonClick();
        }
        helpers.randomlyChooseAndSelectServices();
        helpers.continueButtonClick();
        helpers.sendKeysById("number-of-products", "1");
        helpers.continueButtonClick();
        helpers.sendKeysById("product-details-name", productName);
        helpers.sendKeysById("product-details-price", "10.50");
        helpers.continueButtonClick();
        helpers.sendKeysById("validity", "1");
        helpers.selectRandomOptionFromDropdownById("validity-units");
        helpers.continueButtonClick();

        String endOfCalendarOption = "period-end-calendar";
        String endOfTwentyFourHoursOption = "period-twenty-four-hours";

        String chosenSelector;
        chosenSelector = HelperMethods.makeRandomDecisionBetweenTwoChoices(endOfCalendarOption, endOfTwentyFourHoursOption);

        helpers.clickElementById(chosenSelector);
        helpers.continueButtonClick();
        helpers.waitForPageToLoad();
        helpers.continueButtonClick();
    }

    public int completeMultiServicePeriodPagesWithMultiProducts() {
        helpers.clickElementById("set-of-services");
        helpers.continueButtonClick();
        helpers.randomlyChooseAndSelectServices();
        helpers.continueButtonClick();
        int numberOfProducts = HelperMethods.randomNumberBetweenOneAnd(8) + 2;
        helpers.sendKeysById("number-of-products", String.valueOf(numberOfProducts));
        helpers.continueButtonClick();
        helpers.enterDetailsAndSelectValidityForMultipleProducts(numberOfProducts);
        helpers.continueButtonClick();
        return numberOfProducts;
    }

    public void completeFlatFarePages(String productName) {
        helpers.randomlyChooseAndSelectServices();
        helpers.continueButtonClick();
        helpers.sendKeysById("product-details-name", productName);
        helpers.sendKeysById("product-details-price", "50.50");
        helpers.continueButtonClick();
        helpers.waitForPageToLoad();
        helpers.continueButtonClick();
    }
}
