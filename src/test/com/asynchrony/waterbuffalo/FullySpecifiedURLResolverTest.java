package com.asynchrony.waterbuffalo;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class FullySpecifiedURLResolverTest {
	
	private FullySpecifiedURLResolver testObject;

	@Before
	public void setup() {
		testObject = new FullySpecifiedURLResolver();
		
	}

	@Test
	public void resolveRelative() {
		testObject.resolve("http://www.foo.com");
		String resolve = testObject.resolve("bar.html");
		assertThat(resolve,is("http://www.foo.com/bar.html"));
	}
	
	@Test
	public void resolveTwoAbsolute() {
		testObject.resolve("http://www.foo.com");
		String resolve = testObject.resolve("http://www.bar.com");
		assertThat(resolve,is("http://www.bar.com"));
	}
	
	@Test(expected=RuntimeException.class)
	public void cantResolveBad() {
		testObject.resolve("bar.html");
	}

}
