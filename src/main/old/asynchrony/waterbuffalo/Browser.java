package old.asynchrony.waterbuffalo;

import static old.asynchrony.waterbuffalo.Filter.containingText;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.asynchrony.waterbuffalo.RemoteDriverBuilder;

public class Browser {

	private static final int TIMEOUT_IN_SECONDS = 5;

	private static Browser theBrowser;

	private WebDriver driver; // The Selenium WebDriver that we're using.
	private JavascriptExecutor js; // Handle we can use to run JavaScript.

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

	


	Browser() {
		RemoteDriverBuilder builder = new RemoteDriverBuilder();
		driver = builder.build();

		driver.manage().timeouts().implicitlyWait(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
		driver.manage().timeouts().setScriptTimeout(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);

		this.js = (JavascriptExecutor) driver;
	}






	public Capabilities getDriverCapabilities() {
		return ((HasCapabilities) driver).getCapabilities();
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
		throw new NoSuchElementException("Could not find " + by.toString() + " " + filters[0].description());
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

		By[] byList = { By.id(fieldSelector), By.name(fieldSelector), By.cssSelector(fieldSelector) };

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
		throw new NoSuchElementException("Could not find label with text " + labelText);
	}

	// WARNING: Labels must have a FOR attribute. Does not yet support fields
	// within labels.
	public Element findFieldForLabel(Element label) {
		String forText = label.getAttribute("for");

		if (forText == null || forText.equals("")) {
			throw new NoSuchElementException("Could not find field associated with label " + label.getText());
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
		return getDriverCapabilities().getBrowserName();
	}

	public String getPlatform() {
		return getDriverCapabilities().getPlatform().name().equalsIgnoreCase("MAC") ? "Mac OS X" : "Windows";
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
