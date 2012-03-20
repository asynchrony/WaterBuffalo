package com.asynchrony.waterbuffalo;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.*;


@SuppressWarnings("unused")
public class Element {

	private final Browser browser;		// The Browser that this element is in.
	private final WebElement element;	// The Selenium WebElement that we're wrapping.

	Element(Browser browser, WebElement element) {
		this.browser = browser;
		this.element = element;
	}

	// Finders
	public Element find(By by) {
		return new Element(browser, element.findElement(by));
	}
	public Element find(String selector) {
		return new Element(browser, element.findElement(By.cssSelector(selector)));		
	}

	// Actions
	public void click() {
		element.click();
	}
	public void sendKeys(String text) {
		element.sendKeys(text);
	}
	public void sendKeys(Keys keys) {
		element.sendKeys(keys);
	}
	public void check() {}				// Check if it's a checkable element, or throw an exception.
	public void uncheck() {}			// Uncheck if it's a checkable element, or throw an exception.

	// Output / Info / Details / Getters
	public String text() {
		return element.getText();
	}
	public String innerHTML() {
		// TODO: Use JavaScript to get this.
		return "";
	}
}
