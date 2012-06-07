import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.io.File;
import org.junit.Before;
import org.junit.Test;
import com.asynchrony.waterbuffalo.Browser;


public class BrowserPageTest {

	private static final Browser browser = new Browser();

	@Before
	public void setUp() {
		String BIN_PATH = ClassLoader.getSystemClassLoader().getResource(".").getPath();
		String __PATH__ = (new File(BIN_PATH)).getParent();
		String FIXTURE_PATH = __PATH__ + "/test/fixtures";
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				browser.quit();
			}
		});
		browser.visit("file://" + FIXTURE_PATH + "/page1.html"); // FIXME: Probably won't work in Windows.
	}

	@Test
	public void testPageTitle() {
		assertThat(browser.getTitle(), is("Page 1 Title"));
	}
}
