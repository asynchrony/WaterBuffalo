package old.asynchrony.waterbuffalo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.*;
import org.openqa.selenium.support.ui.Select;

@SuppressWarnings("unused")
public class Element {

	private final Browser browser; // The Browser that this element is in.
	private final WebElement element; // The Selenium WebElement that we're wrapping.
	private String innerHtml;
	
	Element(Browser browser, WebElement element) {
		this.browser = browser;
		this.element = element;
	}
	
	public boolean equals(Element other) {
		// TODO: Need some way to determine equivalence for elements without IDs. Probably have to get its XPath, possibly by traversing its hierarchy.
		//       Or just upgrade to the version of Selenium that includes this patch: http://groups.google.com/group/selenium-developer-activity/browse_thread/thread/5347e05e5e9cc826
		//		 Then we can just do: return this.element.equals(other.element);
		return this.element.getAttribute("id").equals(other.element.getAttribute("id"));
	}

	
	public List<Element> findAllByClass(String selector) {
		return findAll(By.className(selector));
	}
	
	public List<Element> findAll(By locator) {
		List<Element> elements = new ArrayList<Element>();
		List<WebElement> webElements = element.findElements(locator);
		for (WebElement webElement : webElements) {
			elements.add(new Element(browser, webElement));
		}
		return elements;
	}
	// Finders
	public Element find(String selector, Filter... filters) {
		return find(By.cssSelector(selector), filters);
	}

	public Element find(By by, Filter... filters) {
		// TODO: Extracting Element.find() and Browser.find() to a common class.
		if ( filters.length == 0 ) {
			return new Element(browser, element.findElement(by));
		} else {
			for ( WebElement potentialMatch : element.findElements(by) ) {
				if ( filters[0].matches(potentialMatch) ) {
					return new Element(browser, potentialMatch);
				}
			}
			throw new NoSuchElementException("Could not find " + by.toString() + " " + filters[0].description());
		}
	}


	// Actions
	public void click() {
		element.click();
	}

	// REMINDER: CharSequence can be a String or a Key.
	public void sendKeys(CharSequence... text) {
		element.sendKeys(text);
	}

	// Check element if it's a checkable element, or throw an exception.
	public void check() {
		throw new RuntimeException("Not Yet Implemented");
	}

	// Uncheck element if it's a checkable element, or throw an exception.
	public void uncheck() {
		throw new RuntimeException("Not Yet Implemented");
	}

	// Output / Info / Details / Getters
	public String getText() {
		return element.getText();
	}
	public String text() {
		return element.getText();
	}

	public String getAttribute(String attrName) {
		return element.getAttribute(attrName);
	}

	public Boolean hasAttribute(String attrName) {
		return element.getAttribute(attrName) != null;
	}

	public String getTagName() {
		return element.getTagName();
	}

	public Element getParent() {
		return new Element(browser, element.findElement(By.xpath("..")));
	}

	public String getValue() {
		return element.getAttribute("value");
	}

	public List<List<String>> getTableCellContents() {
		if ( !getTagName().equalsIgnoreCase("TABLE") && !getTagName().equalsIgnoreCase("TBODY") ) {
			throw new RuntimeException("getTableCells can only be called for a TABLE or TBODY.");
		}
		List<List<String>> results = new ArrayList<List<String>>();
		Element[] rows = all("TR");
		for (Element row : rows) {
			List<String> rowResult = new ArrayList<String>();
			Element[] cells = row.all("TD");
			for (Element cell : cells) {
				rowResult.add(cell.getText());
			}
			if ( cells.length != 0 ) {
				results.add(rowResult);
			}
		}
		return results;
	}

	public String innerHTML() {
		if (innerHtml == null) {
			innerHtml = (String) browser.evaluateJavaScript("return arguments[0].innerHTML", this);
		}
		return innerHtml;
	}
	
	public void clear() {
		element.clear();
	}
	
	protected WebElement getSeleniumWebElement() {
		return element;
	}

	public void removeText() {
		element.sendKeys(Keys.CONTROL+"a");
		element.sendKeys(Keys.DELETE);
		
	}

	// Select ALL text within the element.
	public void selectText() {
		selectText(getText());
	}
	// Select the given text within the element.
	public void selectText(String text) {
		// This is based on http://stackoverflow.com/questions/4473887/cucumber-capybara-selenium-selecting-text and http://stackoverflow.com/questions/4183401/can-you-set-and-or-change-the-users-text-selection-in-javascript
		String JS_FOR_NON_IE = 	"var selection = window.getSelection();" + 
								"var range = window.document.createRange();" +
								"range.setStart(element, START_POSITION);" +
								"range.setEnd(element, END_POSITION);" +
								"selection.removeAllRanges();" +
								"selection.addRange(range);";
		String JS_FOR_IE = 		"var textRange = document.body.createTextRange();" +
								"textRange.moveToElementText(element);" + 				// Select the entire element.
								"textRange.collapse();" + 								// Remove the current selection.
								"textRange.moveStart('character', START_POSITION);" +
								"textRange.moveEnd('character', END_POSITION);" +
								"textRange.select();";
		String JS = "var element = arguments[0];" +
					"if ( window.getSelection && document.createRange ) {" +
						JS_FOR_NON_IE + 
					"} else {" +
						JS_FOR_IE +
					"}";

		String elementText = this.getText();
		int startPosition = elementText.indexOf(text);
		if ( startPosition == -1 ) {
			throw new RuntimeException("Could not find text \"" + text + "\" within element: " + this);
		}
		int endPosition = startPosition + text.length();

		JS = JS.replaceAll("START_POSITION", "" + startPosition);
		JS = JS.replaceAll("END_POSITION", "" + endPosition);

		browser.executeJavaScript(JS, this);
	}
	
	// Select an OPTION from a SELECT element. The OPTION is specified by the text it displays on screen.
	// TODO: Allow OPTION to be specified by value. (We'll have to cycle through select.getOptions() ourselves, then choose whether to use select.selectByValue() or select.selectByVisibleText().
	// TODO: Consider also allowing OPTION to be specified by index within the SELECT. (Would have a second method signature that takes an integer instead of a locator.)
	public void select(String optionLocator) {
		if ( !getTagName().equals("select") ) {
			throw new RuntimeException("Can only select() on SELECT elements.");
		}
		Select select = new Select(this.element);
		select.selectByVisibleText(optionLocator);
	}

	public int selectNumberOfOptions() {
		if ( !getTagName().equals("select") ) {
			throw new RuntimeException("Can only selectLength() on SELECT elements.");
		}
		Select select = new Select(this.element);
		return select.getOptions().size();
	}

	public Element[] all(String string) {
		List<WebElement> webElements = element.findElements(By.cssSelector(string));
		if (webElements!=null) {
			Element[] elements = new Element[webElements.size()];
			for (int i = 0; i < webElements.size(); i++) {
				elements[i] = new Element(browser, webElements.get(i));			
			}
			return elements;
		} else {
			return null;
		}
	}

	public boolean isEmpty() {
		String innerHtml = innerHTML().trim();
		return innerHtml.equals("");
	}

}
