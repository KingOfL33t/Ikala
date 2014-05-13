package com.ikalagaming.core;

import static org.junit.Assert.*;

import java.util.Locale;

import org.junit.Test;

/**
 * Tests the functionality of the localization class.
 * @author Ches Burks
 *
 */
public class LocalizationTest {

	/**
	 * Tests that the default locale is valid
	 */
	@Test
	public void testDefaultLocale() {
		assertNotNull(Localization.getLocale());
	}
	
	/**
	 * Fetches the default locale, changes it, and ensures 
	 * that the two values are different
	 */
	@Test
	public void testLocaleChange(){
		Locale old = Localization.getLocale();
		assertNotNull(old);
		Localization.setLocale(new Locale("fr", "FR"));
		Locale updated = Localization.getLocale();
		assertNotNull(updated);
		assertNotEquals("The locale did not change", old, updated);
		
	}
	
	

}
