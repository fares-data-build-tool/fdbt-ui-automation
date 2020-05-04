# UI Test Automation

Selenium automation framework project, to prove key user journeys work.

## Getting Started

### Selenium

- Open Chrome (or install it if you dont have it) and check you have the latest version.
- Go here: https://chromedriver.chromium.org/downloads and download the latest chromedriver.
- Put the chromedriver you download (and unzip) into your /usr/local directory

- On MacOS:
    - When you first run the UI tests on your machine, you might receive an error telling you that '“chromedriver” cannot be opened because the developer cannot be verified'. If you receive this error, open System Preferences > Security & Privacy > General and select 'Allow Anyway' alongside the messaege about chromdriver.
    - The next time you receive an error window, click through to allow chromedriver to run.

### Maven
Download Maven from here: https://maven.apache.org/download.cgi

### Java
Download and install OpenJDK 13. For Mac:
```
curl https://download.java.net/java/GA/jdk13.0.2/d4173c853231432d94f001e99d882ca7/8/GPL/openjdk-13.0.2_osx-x64_bin.tar.gz | tar -xz
mv jdk-13.0.2.jdk/ /Library/Java/JavaVirtualMachines/      (this might need sudo infront if you get permission denied)
# Update JAVA_HOME environment variable. Assuming your .bash_profile has export JAVA_HOME=$(/usr/libexec/java_home) just do:
source ~/.bash_profile
```

You're all set!


## Running the tests locally

To run the tests, either click the green arrow to the left of the @Test annotation (if using intelliJ), or run command 'mvn test -P local-chrome'.

To change to hit your local version of the site, change the homepage URL to your localhost URL.

This will run the tests on your local machine


## Running the tests remotely

To run the tests on AWS device farm you must put the awstfn-mfa script from the tfn-aws repo into usr/local/bin. 

From your IDE terminal run 'source awstfn-mfa tfn-test <AWS_USERNAME>' and login with your MFA code

The pack can then be run remotely using 'mvn test -P remote-chrome' or by editing the host within /src/test/properties/env.properties to remote

 
### Aim

We are proving that the full user journeys, from end to end, work. We are using Chrome as the chosen browser, but this might change in the future to accommodate other browsers or versions.


## Built With

- Selenium test framework
- Maven dependency orchestration
- Java 11

## Code Standards

