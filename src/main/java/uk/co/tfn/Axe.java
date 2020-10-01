package uk.co.tfn;

import org.json.JSONObject;
import org.json.JSONArray;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.deque.axe.AXE;

public class Axe {
    private final WebDriver driver;
    private final String host;
    private static final URI axeFileUri = new File((System.getProperty("user.dir") + "/src/test/resources/axe.min.js"))
            .toURI();

    public Axe(WebDriver driver, String host) {
        this.driver = driver;
        this.host = host;
    }

    public void runCheck() {
        JSONArray results = scanPage();
        JSONArray refinedResults = refineViolations(results);
        if (refinedResults.length() > 0) {
            String violationAsString = refinedResults.getJSONObject(0).toString();
            writeToFile(violationAsString);
        }
    }

    private JSONArray scanPage() {
        JSONArray violations = new JSONArray();
        try {
            URL scriptUrl = axeFileUri.toURL();
            AXE.inject(this.driver, scriptUrl);
            JSONObject responseJSON = new AXE.Builder(this.driver, scriptUrl).analyze();
            violations = responseJSON.getJSONArray("violations");
        } catch (MalformedURLException err) {
            System.out.println(String.format("An error occurred, stacktrace: %s", err));
        }
        return violations;
    }

    private JSONArray refineViolations(JSONArray initialViolations) {
        JSONArray desiredViolations = new JSONArray();
        for (int i = 0; i < initialViolations.length(); i++) {
            JSONObject violation = initialViolations.getJSONObject(i);
            JSONArray tags = violation.getJSONArray("tags");
            for (int j = 0; j < tags.length(); j++) {
                if (tags.getString(j).equals("wcag2a") || tags.getString(j).equals("wcag2aa")) {
                    desiredViolations.put(violation);
                }
            }
        }
        return desiredViolations;
    }

    private void writeToFile(String resultsToWrite) {
        BufferedWriter writer = null;
        String urlToSplitOn = "";
        if (host.equals("local")) {
            urlToSplitOn = "http://localhost:5555/";
        } else {
            urlToSplitOn = "https://tfn-test.infinityworks.com/";
        }
        String currentUrl = this.driver.getCurrentUrl();
        String page = currentUrl.split(urlToSplitOn)[1].replace("?", "");
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        
        try {
            writer = new BufferedWriter(
                    new FileWriter(String.format(System.getProperty("user.dir") + "/src/test/resources/results/%s_%s.json", page, sdf.format(cal.getTime())), true));
            writer.write(resultsToWrite);
        } catch (IOException e) {
            // error case
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                // error case
            }
        }
    }
}