package old.asynchrony.waterbuffalo;


public class FillIn {
	private final Browser browser;
	private final String fieldSelector;

	public FillIn(Browser browser, String fieldSelector) {
		this.browser = browser;
		this.fieldSelector = fieldSelector;
	}

	public void with(String value) {
		browser.findField(fieldSelector).sendKeys(value);
	}
}
