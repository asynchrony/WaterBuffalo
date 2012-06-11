package com.asynchrony.waterbuffalo;

import static org.junit.Assert.*;

import static org.hamcrest.CoreMatchers.is;
import org.junit.Test;
import org.mockito.Mock;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.asynchrony.waterbuffalo.CapabilitiesBuilder;
import com.asynchrony.waterbuffalo.PropertiesLoader;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;

public class CapabilitiesBuilderTest {

	@Mock
	private PropertiesLoader mockPropertiesLoader;

	@Test
	public void internetExplorerLoaded() {
		initMocks(this);
		CapabilitiesBuilder testObject = new CapabilitiesBuilder(mockPropertiesLoader);
		when(mockPropertiesLoader.getBrowserType()).thenReturn("Internet Explorer");
		when(mockPropertiesLoader.isRemote()).thenReturn(true);
		when(mockPropertiesLoader.getAppPath()).thenReturn("foo");
		DesiredCapabilities actualCapabilities = testObject.buildCapabilities();
		
		String expectedBrowserName = DesiredCapabilities.internetExplorer().getBrowserName();
		String actualBrowserName = actualCapabilities.getBrowserName();
		assertThat(actualBrowserName, is(expectedBrowserName));
		String ieFlag = InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS;
		assertTrue((Boolean) actualCapabilities.getCapability(ieFlag));
		assertTrue((Boolean) actualCapabilities.getCapability("isRemote"));
		assertThat((String)actualCapabilities.getCapability("appPath"), is("foo"));
	}

	@Test
	public void chromeLoaded() {
		initMocks(this);
		CapabilitiesBuilder testObject = new CapabilitiesBuilder(mockPropertiesLoader);
		when(mockPropertiesLoader.getBrowserType()).thenReturn("Chrome");
		DesiredCapabilities actualCapabilities = testObject.buildCapabilities();
		String expectedBrowserName = DesiredCapabilities.chrome().getBrowserName();
		String actualBrowserName = actualCapabilities.getBrowserName();
		assertThat(actualBrowserName, is(expectedBrowserName));
	}

	@Test
	public void safariLoaded() {
		initMocks(this);
		CapabilitiesBuilder testObject = new CapabilitiesBuilder(mockPropertiesLoader);
		when(mockPropertiesLoader.getBrowserType()).thenReturn("Safari");
		DesiredCapabilities actualCapabilities = testObject.buildCapabilities();
		String expectedBrowserName = DesiredCapabilities.safari().getBrowserName();
		String actualBrowserName = actualCapabilities.getBrowserName();
		assertThat(actualBrowserName, is(expectedBrowserName));
	}

	@Test
	public void defaultFirefoxLoaded() {
		initMocks(this);
		CapabilitiesBuilder testObject = new CapabilitiesBuilder(mockPropertiesLoader);
		when(mockPropertiesLoader.getBrowserType()).thenReturn("foo");
		DesiredCapabilities actualCapabilities = testObject.buildCapabilities();
		String expectedBrowserName = DesiredCapabilities.firefox().getBrowserName();
		String actualBrowserName = actualCapabilities.getBrowserName();
		assertThat(actualBrowserName, is(expectedBrowserName));
	}
}
