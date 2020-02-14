package uk.co.tfn;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class PageElements {

    public static WebElement startPageStartButton(ChromeDriver driver){
        return driver.findElement((By.id("start-now-button")));
    }



}
