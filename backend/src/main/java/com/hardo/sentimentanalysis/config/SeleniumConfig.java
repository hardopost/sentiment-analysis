package com.hardo.sentimentanalysis.config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SeleniumConfig {

    @Bean
    public WebDriver initializeWebDriver() {
        // Set Chrome options
        WebDriverManager.chromedriver().setup(); // Automatically sets driver path
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");      // Optional: Run in headless mode
        //options.addArguments("--disable-gpu");    // Disable GPU rendering
        //options.addArguments("--no-sandbox");     // Required for Docker or CI/CD
        options.addArguments("--window-size=2560,1440");

        // Return the configured WebDriver instance
        return new ChromeDriver(options);
    }
}

