package com.asynchrony.waterbuffalo;


import static com.asynchrony.waterbuffalo.PropertiesLoader.APPSERVER_PATH;
import static com.asynchrony.waterbuffalo.PropertiesLoader.BROWSER;
import static com.asynchrony.waterbuffalo.PropertiesLoader.DEFAULT_CHROME_MAC_PATH;
import static com.asynchrony.waterbuffalo.PropertiesLoader.DEFAULT_CHROME_WINDOWS_PATH;
import static com.asynchrony.waterbuffalo.PropertiesLoader.WEBDRIVER_CHROME_DRIVER;
import static com.asynchrony.waterbuffalo.PropertiesLoader.WEBDRIVER_REMOTE_SERVER;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.RETURNS_DEFAULTS;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Properties;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.asynchrony.waterbuffalo.PropertiesLoader;

@RunWith(PowerMockRunner.class)
@PrepareForTest( { Browser.class})
public class PropertiesLoaderTest {
	
	private Properties mockProperties;
	private PropertiesLoader spy;
	private PropertiesLoader testedObject;

	@Before
	public void setUp() {
		PowerMockito.mockStatic(Browser.class);
	}

	private void setUpMockPropertiesTests() {
		mockProperties = mock(Properties.class);
		testedObject = new PropertiesLoader(mockProperties);
	}
	
	private void setUpPropertiesTests() {
		mockProperties = new Properties();
		PropertiesLoader testObject = new PropertiesLoader(mockProperties);
		spy = spy(testObject);
		doAnswer(RETURNS_DEFAULTS).when(spy).actualLoad(mockProperties);
		
	}
	@Test
	public void validateSetChromeDriver() {
		setUpPropertiesTests();
		mockProperties.setProperty(WEBDRIVER_CHROME_DRIVER, "wcd");
		spy.loadProperties();
		assertThat(System.getProperty(WEBDRIVER_CHROME_DRIVER), is("wcd"));
	}
	
	@Test
	public void validateNullChromeDriverOnMac() {
		setUpPropertiesTests();
		when(Browser.isMac()).thenReturn(true);
		spy.loadProperties();
		assertThat(System.getProperty(WEBDRIVER_CHROME_DRIVER), is(DEFAULT_CHROME_MAC_PATH));
	}
	
	@Test
	public void validateNullChromeDriverOnWindows() {
		setUpPropertiesTests();
		when(Browser.isWindows()).thenReturn(true);
		spy.loadProperties();
		assertThat(System.getProperty(WEBDRIVER_CHROME_DRIVER), is(DEFAULT_CHROME_WINDOWS_PATH));
	}

	
	@Test
	public void validateRemoteServer() {
		setUpPropertiesTests();
		mockProperties.setProperty(WEBDRIVER_REMOTE_SERVER, "wrs");
		spy.loadProperties();
		assertThat(System.getProperty(WEBDRIVER_REMOTE_SERVER), is("wrs"));
	}
	
	@Test
	public void validateNullRemoteServer() {
		setUpPropertiesTests();
		spy.loadProperties();
		assertThat(System.getProperty(WEBDRIVER_REMOTE_SERVER), is(""));
	}

	@Test
	public void appPathDelegatesProperly() {
		setUpMockPropertiesTests();
		testedObject.getAppPath();
		verify(mockProperties).getProperty(APPSERVER_PATH);
	}
	
	@Test
	public void browserTypeDelegatesProperly() {
		setUpMockPropertiesTests();
		testedObject.getBrowserType();
		verify(mockProperties).getProperty(BROWSER);
	}
	
	@Test
	public void isRemoteWhenPropertyNotNull() {
		setUpMockPropertiesTests();
		when(mockProperties.getProperty(WEBDRIVER_REMOTE_SERVER)).thenReturn("foo");
		assertTrue(testedObject.isRemote());
	}
	
	@Test
	public void isRemoteWhenPropertyNull() {
		setUpMockPropertiesTests();
		when(mockProperties.getProperty(WEBDRIVER_REMOTE_SERVER)).thenReturn(null);
		assertFalse(testedObject.isRemote());
	}

}
