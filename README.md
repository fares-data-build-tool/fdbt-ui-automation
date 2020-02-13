# UI Test Automation

Selenium automation framework project, to prove key user journeys work.

## Getting Started

 ###Selenium

- Open Chrome (or install it if you dont have it) and check you have the latest version.
- Go here: https://chromedriver.chromium.org/downloads and download the latest chromedriver.
- Put the chromedriver you download (and unzip) into your /usr/local directory

 ###Maven
- Download Maven from here: https://maven.apache.org/download.cgi

 ###Java
 - Download Java 11 here : https://www.oracle.com/java/technologies/javase-jdk11-downloads.html
 
- You're all set!


## Running the tests

To run the tests, either click the green arrow to the left of the @Test annotation (if using intelliJ), or run command 'mvn clean verify'.


### Aim

We are proving that the full user journeys, from end to end, work. We are using Chrome as the chosen browser, but this might change in the future to accomodate other browsers or versions.


## Built With

- Selenium test framework
- Maven dependency orchestration
- Java 11