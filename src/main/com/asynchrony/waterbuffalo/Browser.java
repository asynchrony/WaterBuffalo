package com.asynchrony.waterbuffalo;


import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.firefox.*;
import org.openqa.selenium.ie.*;
import org.openqa.selenium.interactions.*;
import org.openqa.selenium.remote.RemoteWebDriver;


@SuppressWarnings("unused")
public class Browser {

	private final WebDriver driver;		// The Selenium WebDriver that we're using.
	private final JavascriptExecutor js;	// Handle we can use to run JavaScript.
//	private final Actions actions;		// Handle we can use to run low-level Selenium (keyboard and mouse) interactions. See http://selenium.googlecode.com/svn/trunk/docs/api/java/org/openqa/selenium/interactions/Actions.html
	private final boolean isRemote;
	private final String serverIP;		// IP of the system that the web server is running on.
	private final String clientIP;		// IP of the system that the web browser is running on.

	private String currentURL = "http://localhost:8560/prodo/"; // TODO: Make the constructor and theBrowser() pass this in.

    public Browser() {
		this("Firefox", "localhost", "localhost");    	
    }

	Browser(WebDriver driver) {
		this.driver = driver;
		this.isRemote = (driver instanceof RemoteWebDriver);
		this.js = (JavascriptExecutor) driver;
//		this.actions = (Actions) driver;
		this.clientIP = ""; // TODO: FIXME!
		this.serverIP = ""; // TODO: FIXME!
	}

	Browser(String browserType) {
		this(browserType, "localhost", "localhost");
	}
	Browser(String browserType, String serverIP) {
		this(browserType, serverIP, "localhost");
	}
	Browser(String browserType, String serverIP, String clientIP) {
		this(new FirefoxDriver()); // TODO: FIXME, using a static factory method.
	}
	
	// Finders
	public Element find(By by) {
		return new Element(this, driver.findElement(by));
	}
	public Element find(String selector) {
		return new Element(this, driver.findElement(By.cssSelector(selector)));
	}

	// Actions

	/**
	 * Commands the browser go to the specified URL.
	 *
	 * @param url  fully specified URL, or a relative URL (relative to the current URL)
	 */
	public void visit(String url) {
		currentURL = fullySpecifiedURL(url);
		driver.get(currentURL);
	}

	private String fullySpecifiedURL(String url) {
		URI baseUrl;
		URI absoluteUrl;
		try {
			baseUrl = new URI(currentURL);
			absoluteUrl = baseUrl.resolve(url);
			return absoluteUrl.toString();
		} catch (URISyntaxException e) {
			return currentURL;
		}
	}

	public void close() {
		driver.close();
	}
	public void quit() {
		driver.close();
	}
	
	public void sendKeys(String text) {
//		actions.sendKeys(text);
	}
	public void sendKeys(Keys keys) {
//		actions.sendKeys(keys);
	}

	// General Info
	public boolean isRemote() { // TRUE if we're hitting Selenium RC.
		return isRemote;
	}
	public boolean isLocal() {	// TRUE if we're hitting a local browser via WebDriver.
		return !isRemote;
	}

	// Page Info / Output / Details / Getters
	public String getTitle() {
		return driver.getTitle();
	}

	// Windows and Frames
	public void switchToFrame() {
		driver.switchTo().defaultContent();
	}
	public void switchToFrame(String frame) {}
	public void switchToWindow() {
		driver.switchTo().defaultContent();
	}
	public void switchToWindow(String window) {}
	public void switchToActiveElement() {
		driver.switchTo().activeElement();
	}
	public void switchToAlert() {
		driver.switchTo().alert();
	}

	// JavaScript
	public void executeJavaScript(String script) {	// Execute some JavaScript, with no return value.
		js.executeScript(script); // TODO: handle optional receiver.
	}
	public Object evaluateJavaScript(String script) { // Execute some JavaScript, with a return value.
		return js.executeScript(script); // TODO: Add a return statement to the script if it doesn't have one.
	}
}
