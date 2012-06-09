package old.asynchrony.waterbuffalo;

import static old.asynchrony.waterbuffalo.Filter.containingText;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;


public class Browser {

	private static final int TIMEOUT_IN_SECONDS = 5;

	private static Browser theBrowser;

	private final WebDriver driver; // The Selenium WebDriver that we're using.
	private final JavascriptExecutor js; // Handle we can use to run JavaScript.
	private final Actions actions; // Handle we can use to run low-level
									// Selenium (keyboard and mouse)
									// interactions. See
									// http://selenium.googlecode.com/svn/trunk/docs/api/java/org/openqa/selenium/interactions/Actions.html
	private final WebDriverWait wait; // Use wait.until() to do explicit
										// waiting/timeouts.

	private final boolean isRemote;
	private final String type; // "Firefox", "Chrome", or "Internet Explorer"
	private final String platform; // "Windows" or "Mac OS X"

	private String currentURL = "http://localhost:8560/prodo/"; // TODO: Make
																// the
																// constructor
																// and singleton
																// pass this in.

	// TODO: Rename?
	public static Browser theBrowser() {
		if (theBrowser == null) {
			theBrowser = new Browser();

			// Close the browser when the JVM completes execution. See
			// https://github.com/cucumber/cucumber-jvm/pull/295#issuecomment-5267340
			// for details.
			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					theBrowser.quit();
				}
			});
		}
		return theBrowser;
	}

	Browser(WebDriver driver) {
		this.driver = driver;
		this.isRemote = (driver instanceof RemoteWebDriver);
		this.js = (JavascriptExecutor) driver;
		this.actions = (Actions) driver;
		this.type = "Chrome"; // TODO: FIXME!
		this.platform = "Windows"; // TODO: FIXME!
		this.wait = new WebDriverWait(driver, TIMEOUT_IN_SECONDS);
	}

	Browser() {
		// TODO: Use System.getProperty() to set these defaults.
		this("Internet Explorer", "127.0.0.1", "127.0.0.1");
	}

	Browser(String browserType, String serverIP, String browserIP) {
		this.isRemote = false; // FIXME: determine whether browserIP is my own
								// IP or localhost.

		Properties environmentProperties = new Properties();
		InputStream environmentPropertiesFile = this.getClass()
				.getResourceAsStream(
						"/com/asynchrony/waterbuffalo/environment.properties");
		try {
			environmentProperties.load(environmentPropertiesFile);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		this.type = environmentProperties.getProperty("browser");
		// this.type = browserType;
		DesiredCapabilities capabilities;
		// CommandExecutor executor;
		// Selenium selenium;

		if (isRemote) {
			String remoteSeleniumDriverURL = "http://" + browserIP
					+ ":4444/wd/hub";

			if (type.equals("Firefox")) {
				capabilities = DesiredCapabilities.firefox();
			} else if (type.equals("Chrome")) {
				// FIXME: The following line assumes the remote system is
				// running Mac OS X.
				System.setProperty("webdriver.chrome.driver",
						"/Applications/Google Chrome.app/Contents/MacOS/Google Chrome");
				capabilities = DesiredCapabilities.chrome();
				// } else if ( browserType == "Safari" ) {
				// // WebDriver does not yet support Safari, so this does not
				// really work for us.
				// // Per
				// http://groups.google.com/group/webdriver/msg/68d385ecc940a03f?dmode=source
				// capabilities = new DesiredCapabilities();
				// selenium = new DefaultSelenium(remoteIP, 4444, "*safari",
				// baseURL);
				// executor = new SeleneseCommandExecutor(selenium);
				// driver = new RemoteWebDriver(executor, capabilities);
			} else {
				throw new UnsupportedOperationException(
						"Please set browserType.");
			}

			try {
				driver = new RemoteWebDriver(new URL(remoteSeleniumDriverURL),
						capabilities);
			} catch (MalformedURLException e) {
				throw new RuntimeException(
						"Could not open link to remote Selenium driver at "
								+ browserIP);
			}
		} else {
			if (type.equals("Firefox")) {
				driver = new FirefoxDriver();
			} else if (type.equals("Internet Explorer")) {
				capabilities = DesiredCapabilities.internetExplorer();
				capabilities
						.setCapability(
								InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,
								true);
				driver = new InternetExplorerDriver(capabilities);
			} else if (type.equals("Chrome")) {
				System.setProperty("webdriver.chrome.driver",
						environmentProperties
								.getProperty("webdriver.chrome.driver"));
				driver = new ChromeDriver();
				// } else if ( browserType == "Safari" ) {
				// // WebDriver does not yet support Safari, so this does not
				// really work for us.
				// selenium = new DefaultSelenium("localhost", 4444,
				// "*safariproxy", baseUrl);
				// executor = new SeleneseCommandExecutor(selenium);
				// driver = new RemoteWebDriver(executor, capabilities);
			} else {
				throw new UnsupportedOperationException(
						"Please set valid browserType. Browser =" + type);
			}
		}

		// Always wait for 5 seconds before failing due to not finding an
		// element on the page, or running a script.
		driver.manage().timeouts()
				.implicitlyWait(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
		driver.manage().timeouts()
				.setScriptTimeout(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);

		this.js = (JavascriptExecutor) driver;
		// FIXME: Following line throws java.lang.ClassCastException:
		// org.openqa.selenium.firefox.FirefoxDriver cannot be cast to
		// org.openqa.selenium.interactions.Actions
		this.actions = null; // (Actions) driver;

		// TODO: Can't figure out why this first line of JS runs, but the second
		// throws an exception.
		// Long two = (Long)js.executeScript("return 1 + 1;");
		// assertEquals((long)2, two.longValue());

		String platform = ((HasCapabilities) driver).getCapabilities()
				.getPlatform().name();
		this.platform = platform.equalsIgnoreCase("MAC") ? "Mac OS X"
				: "Windows";
		this.wait = new WebDriverWait(driver, TIMEOUT_IN_SECONDS);
	}

	Browser(String browserType) {
		throw new RuntimeException("Not Yet Implemented");
	}

	Browser(String browserType, String serverIP) {
		this(browserType, serverIP, "localhost");
	}

	// Finders
	public Element find(String selector, Filter... filters) {
		return find(By.cssSelector(selector), filters);
	}

	public Element find(By by, Filter... filters) {
		if (filters.length == 0) {
			return new Element(this, driver.findElement(by));
		}
		for (WebElement potentialMatch : driver.findElements(by)) {
			if (filters[0].matches(potentialMatch)) {
				return new Element(this, potentialMatch);
			}
		}
		throw new NoSuchElementException("Could not find " + by.toString()
				+ " " + filters[0].description());
	}
	
	public boolean has(By locator) {
		return findAll(locator).size() != 0;
	}

	public List<Element> findAll(By locator) {
		List<Element> elements = new ArrayList<Element>();
		List<WebElement> webElements = driver.findElements(locator);
		for (WebElement webElement : webElements) {
			elements.add(new Element(this, webElement));
		}
		return elements;
	}

	public Element findSubmitButton(String buttonSelector) {
		// TODO: Need to look for ID and value. Also need to look for
		// type=button.
		return find("input[type=submit]");
	}

	public Element findButtonById(String buttonSelector) {
		return find("input[type=button][id=" + buttonSelector + "]");
	}

	// Find a field by CSS selector, ID, name, or label text.
	// TODO: Memoize the results.
	// TODO: Revert back to Capybara way of doing things (the order listed
	// above).
	// TODO: Add explicit timeouts to find
	public Element findField(final String fieldSelector) {
		try {
			Element el = findFieldForLabel(findByLabel(fieldSelector));
			return el;
		} catch (NoSuchElementException e1) {
			// Not an error. Keep searching
		}
		
		By[] byList = { 
				By.id(fieldSelector), 
				By.name(fieldSelector),
				By.cssSelector(fieldSelector) 
				};

		for (int i = 0; i < byList.length - 1; i++) {
			try {
				Element el = find(byList[i]);
				return el;
			} catch (NoSuchElementException e2) {
				continue;
			}
		}

		return find(byList[byList.length - 1]);
	}

	// Find a label with the given text (case insensitive). Throws
	// NoSuchElementException if there is no label with the given text.
	public Element findByLabel(String labelText) {
		List<WebElement> allLabels = driver.findElements(By.tagName("label"));

		// TODO: getText() seems to be quite slow. Is there another way we can
		// do this?
		for (WebElement labelWebElement : allLabels) {
			if (labelWebElement.getText().equalsIgnoreCase(labelText)) {
				return new Element(this, labelWebElement);
			}
		}
		throw new NoSuchElementException("Could not find label with text "
				+ labelText);
	}

	// WARNING: Labels must have a FOR attribute. Does not yet support fields
	// within labels.
	public Element findFieldForLabel(Element label) {
		String forText = label.getAttribute("for");

		if (forText == null || forText.equals("")) {
			throw new NoSuchElementException(
					"Could not find field associated with label "
							+ label.getText());
		}

		return new Element(this, driver.findElement(By.id(forText)));
	}

	// Find a field by CSS selector, ID, or link text.
	// TODO: Memoize the results.
	public Element findLink(String linkSelector) {
		try {
			return find(By.cssSelector(linkSelector));
		} catch (NoSuchElementException e1) {
			try {
				return find(By.id(linkSelector));
			} catch (NoSuchElementException e2) {
				// Allow NoSuchElementException to get thrown if this one fails.
				return find(By.linkText(linkSelector));
			}
		}
	}

	// Actions
	public void visit(String url) {
		// NOTE: Allows relative URLs, based off current URL.
		currentURL = fullySpecifiedURL(url);
		driver.get(currentURL);
	}

	// FIXME: If currentURL is "http://localhost:8560/prodo/",
	// "searchAuditView.do" should return
	// "http://localhost:8560/prodo/searchAuditView.do", not
	// "http://localhost:8560/searchAuditView.do".
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

	public void clickLink(String linkSelector) {
		clickLink(findLink(linkSelector));
	}

	public void clickLink(Element link) {
		link.click();
		driver.findElement(By.tagName("body")); // Wait until the body shows up
												// on the newly loaded page.
	}

	public void clickSubmitButton(String buttonSelector) {
		clickButton(findSubmitButton(buttonSelector));
	}

	public void clickButtonById(String selector) {
		findButtonById(selector).click();
	}

	public void clickButton(Element button) {
		button.click();
	}

	public void mouseOver(String linkSelector) {
		mouseOver(findLink(linkSelector));
	}

	public void mouseOver(Element link) {
		driver.findElement(By.tagName("body")); // Wait until the body shows up
												// on the newly loaded page.
		Actions builder = new Actions(driver);
		Actions hover = builder.moveToElement(link.getSeleniumWebElement());
		hover.perform();
	}

	public FillIn fillIn(String fieldSelector) {
		return new FillIn(this, fieldSelector);
	}

	public void close() {
		driver.close();
	}

	public void quit() {
		driver.close();
	}

	// REMINDER: CharSequence can be a String or a Key.
	public void sendKeys(CharSequence... text) {
		getFocusedElement().sendKeys(text);
	}

	public Actions keyDown(Keys keys) {
		// return actions.keyDown(keys);
		throw new RuntimeException("Not Yet Implemented");
	}

	// General Info
	public String type() {
		return type;
	}

	public String getPlatform() {
		return platform;
	}

	public boolean isRemote() { // TRUE if we're hitting Selenium RC.
		return isRemote;
	}

	public boolean isLocal() { // TRUE if we're hitting a local browser via
								// WebDriver.
		return !isRemote;
	}

	// Page Info / Output / Details / Getters
	public String getTitle() {
		return driver.getTitle();
	}

	public Element getFocusedElement() {
		return new Element(this, driver.switchTo().activeElement());
	}

	// Windows and Frames
	public void switchToFrame() {
		driver.switchTo().defaultContent();
	}

	public void switchToFrame(String frame) {
		driver.switchTo().frame(frame);
	}

	public void switchToFrame(Element frame) {
		driver.switchTo().frame(frame.getSeleniumWebElement());
	}

	public void switchToWindow() {
		driver.switchTo().defaultContent();
	}

	public void switchToWindow(String window) {
		throw new RuntimeException("Not Yet Implemented");
	}

	public void switchToActiveElement() {
		driver.switchTo().activeElement();
	}

	public void switchToAlert() {
		driver.switchTo().alert();
	}

	// JavaScript
	public void executeJavaScript(String script) { // Execute some JavaScript,
													// with no return value.
		js.executeScript(script);
	}

	public void executeJavaScript(String script, Element element) {
		js.executeScript(script, element.getSeleniumWebElement());
	}

	public Object evaluateJavaScript(String script) { // Execute some
														// JavaScript, WITH a
														// return value.
		return js.executeScript(script); // TODO: Add a return statement to the
											// script if it doesn't have one.
	}

	public Object evaluateJavaScript(String script, Element element) {
		return js.executeScript(script, element.getSeleniumWebElement());
	}

	// Cut, copy, paste.
	public void cut() {
		sendKeys(Keys.CONTROL, "x");
	}

	public void copy() {
		sendKeys(Keys.CONTROL, "c");
	}

	public void paste() {
		sendKeys(Keys.CONTROL, "v");
	}

	public void clickOnOptionInSelect(Element element, String selectedElement) {
		findOptionInSelect(element, selectedElement).click();
	}
	public void clickOnOptionInSelect(Element element, int index) {
		findOptionInSelect(element, index).click();
	}

	public Element findOptionInSelect(Element element, String selectedElement) {
		return element.find("option", containingText(selectedElement));
	}
	public Element findOptionInSelect(Element element, int index) {
		return element.all("option")[index];
	}

}
