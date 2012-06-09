import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.io.File;
import org.junit.Before;
import org.junit.Test;
import com.asynchrony.waterbuffalo.Browser;


public class BrowserPageTest {

	private static final Browser browser = new Browser();
	private static final String __PATH__ = (new File(ClassLoader.getSystemClassLoader().getResource(".").getPath())).getParent();
	private static final String FIXTURE_PATH = __PATH__ + "/test/fixtures";

	@Before
	public void setUp() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				browser.quit();
			}
		});
		browser.visit("file://" + FIXTURE_PATH + "/page1.html"); // FIXME: Probably won't work in Windows.
	}

	@Test
	public void canGetPageTitle() {
		assertThat(browser.getTitle(), is("Page 1 Title"));
	}

	@Test
	public void canVisitRelativeUrl() {
		browser.visit("page2.html");
		assertThat(browser.getTitle(), is("Page 2 Title"));
	}

	@Test
	public void canVisitRelativeUrlWithDot() {
		browser.visit("./page2.html");
		assertThat(browser.getTitle(), is("Page 2 Title"));
	}

	@Test
	public void canVisitRelativeUrlWithTwoDots() {
		browser.visit("subfolder/../page2.html");
		assertThat(browser.getTitle(), is("Page 2 Title"));
	}

	@Test
	public void canVisitRelativeUrlWithPathFromRoot() {
		// This is a little harder to test with file: URLs, but should be sufficient if we ensure it starts with "/".
		assertTrue(FIXTURE_PATH.startsWith("/"));
		browser.visit(FIXTURE_PATH + "/page2.html");
		assertThat(browser.getTitle(), is("Page 2 Title"));
	}

	@Test
	public void canVisitRelativeUrlFromSubfolder() {
		browser.visit(FIXTURE_PATH + "/subfolder/page3.html");
		browser.visit("page4.html");
		assertThat(browser.getTitle(), is("Page 4 Title"));
	}

	@Test
	public void canVisitRelativeUrlWithDotFromSubfolder() {
		browser.visit(FIXTURE_PATH + "/subfolder/page3.html");
		browser.visit("./page4.html");
		assertThat(browser.getTitle(), is("Page 4 Title"));
	}

	@Test
	public void canVisitRelativeUrlWithTwoDotsFromSubfolder() {
		browser.visit(FIXTURE_PATH + "/subfolder/page3.html");
		browser.visit("../page2.html");
		assertThat(browser.getTitle(), is("Page 2 Title"));
	}
}
