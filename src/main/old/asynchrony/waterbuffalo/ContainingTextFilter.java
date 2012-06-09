package old.asynchrony.waterbuffalo;

import org.openqa.selenium.WebElement;

public class ContainingTextFilter extends Filter {
	private final String text;

	ContainingTextFilter(String text) {
		this.text = text;
	}

	public boolean matches(WebElement element) {
		return element.getText().contains(text);
	}

	public String description() {
		return "containing text " + "\"" + text + "\"";
	}
}
