package old.asynchrony.waterbuffalo;

import org.openqa.selenium.WebElement;

public abstract class Filter {
	public static Filter containingText(String text) {
		return new ContainingTextFilter(text);
	}

	public abstract boolean matches(WebElement element);

	public abstract String description();
}
