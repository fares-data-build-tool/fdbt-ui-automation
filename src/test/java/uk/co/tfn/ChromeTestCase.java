package uk.co.tfn;

import org.apache.tools.ant.types.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterAll;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.*;
import java.net.URL;

import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertTrue;
import static uk.co.tfn.HelperMethods.*;
import static uk.co.tfn.StepMethods.*;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.devicefarm.*;
import software.amazon.awssdk.services.devicefarm.model.*;
import software.amazon.awssdk.services.devicefarm.DeviceFarmClient;
import software.amazon.awssdk.services.devicefarm.model.CreateTestGridUrlRequest;
import software.amazon.awssdk.services.devicefarm.model.CreateTestGridUrlResponse;


public class ChromeTestCase {

    private static RemoteWebDriver driver;

    @Before
    public void chromeSetup() throws IOException {


        File file = new File("src/test/properties/env.properties");
        FileInputStream fileInput = new FileInputStream(file);
        Properties properties = new Properties();
        properties.load(fileInput);
        fileInput.close();
        String browser = properties.getProperty("browser");
        String host = properties.getProperty("host");
        System.out.println(browser + host);

        if (host.equals("local")) {
            DesiredCapabilities caps = setCapabilities();
        ChromeDriverService service = setDriverService();
        ChromeOptions options = setOptions();

        options.merge(caps);

        driver = new ChromeDriver(service, options);

        } else {
            String myProjectARN = "arn:aws:devicefarm:us-west-2:442445088537:testgrid-project:eaf5a5fe-6e13-493e-8d07-c083c0ee65ee";
            DeviceFarmClient client  = DeviceFarmClient.builder().region(Region.US_WEST_2) //Device farm is in US_WEST_2
                    .build();
            CreateTestGridUrlRequest request = CreateTestGridUrlRequest.builder()
                    .expiresInSeconds(300)        // 5 minutes
                    .projectArn(myProjectARN)
                    .build();
            URL testGridUrl = null;
            try {
                CreateTestGridUrlResponse response = client.createTestGridUrl(request);
                testGridUrl = new URL(response.url());
            } catch (Exception e) {
                e.printStackTrace();
            }
            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setBrowserName(browser);
            driver = new RemoteWebDriver(testGridUrl, capabilities);
        }
    }

    @Test
    public void chromeUploadCSVTest() throws IOException {

        getHomePage(driver);

        waitForPageToLoad(driver);

        stepsToInputMethod(driver);

        driver.findElement(By.id("csv-upload")).click();

        continueButtonClick(driver);

        uploadCsvFile(driver);

        submitButtonClick(driver);

        fillInFareStageOptions(driver, 8);

        submitButtonClick(driver);

        assertTrue(isUuidStringValid(driver));

    }

    @Test
    public void chromeManualTriangle() {

        getHomePage(driver);
        waitForPageToLoad(driver);
        stepsToInputMethod(driver);
        driver.findElement(By.id("manual-entry")).click();
        continueButtonClick(driver);
        driver.findElement(By.id("lessThan20FareStages")).click();
        continueButtonClick(driver);
        WebElement fareStages = driver.findElement(By.id("fareStages"));
        fareStages.sendKeys("7");
        continueButtonClick(driver);
        fillInManualFareStages(driver);
        continueButtonClick(driver);
        fillInFareStageTriangle(driver);
        continueButtonClick(driver);
        fillInFareStageOptions(driver, 6);
        submitButtonClick(driver);
        assertTrue(isUuidStringValid(driver));
    }

    @AfterAll
    public static void tearDown() {
        driver.quit();
    }

}
