package com.mycompany.utils;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebDriverUtils {

	private static final Logger LOG = LoggerFactory.getLogger(WebDriverUtils.class);

	private WebDriverUtils() {
	}

	private static final String NO_MORE_RESULTS_ID =
			"#islmp > div > div > div > div > div.gBPM8 > div.qvfT1 > div.DwpMZe > div.OuJzKb.Bqq24e";

	public static void showAllResults(WebDriver driver) {
		while (thereAreResults(driver)) {
			scrollDown(driver);
			try {
				clickMoreResults(driver);
			} catch (NoSuchElementException e) {
				LOG.info("Se llego a cargar hasta el final de la página");
			}
		}
	}

	private static boolean thereAreResults(WebDriver driver) {
		boolean theAre = true;
		WebElement webElem;
		try {
			webElem = driver.findElement(By.cssSelector(NO_MORE_RESULTS_ID));
			theAre = webElem.getText().equals("Espera mientras se carga más contenido");
		} catch (NoSuchElementException e) {
			theAre = false;
		}
		return theAre;
	}

	private static void scrollDown(WebDriver driver) {
		JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
		boolean scrollComplete = false;
		int pageHeight = ((Number) jsExecutor.executeScript("return document.body.scrollHeight")).intValue();
		int scrollPosition = 0;
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
		while (!scrollComplete) {
			scrollPosition += 1000;
			if (scrollPosition >= pageHeight) {
				scrollPosition = pageHeight;
				scrollComplete = true;
			}
			jsExecutor.executeScript("window.scrollTo(0, " + scrollPosition + ");");
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("body")));
		}
	}

	private static void clickMoreResults(WebDriver driver) {
		var findElementsBy = By.className("mye4qd");
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
		wait.until(ExpectedConditions.presenceOfElementLocated(findElementsBy));
		WebElement searchButton = driver.findElement(findElementsBy);
		JavascriptExecutor executor = (JavascriptExecutor) driver;
		executor.executeScript("arguments[0].click();", searchButton);
	}
}
