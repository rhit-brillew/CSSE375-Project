package risk.model;

import static org.junit.Assert.assertEquals;

import java.util.Locale;
import org.junit.Test;

/**
 * BVA: creation
 * CASE     | EXPECTED
 * English  | the english resource bundle is loaded
 * Spanish  | the spanish resource bundle is loaded
 */

public class TestStaticResourceBundle {
	@Test
	public void testCreationEnglish() {
		Locale locale = new Locale("en", "US");
		StaticResourceBundle.createResourceBundle(locale);
		assertEquals(locale, StaticResourceBundle.getResourceBundle().getLocale());
	}

	@Test
	public void testCreationSpanish() {
		Locale locale = new Locale("es", "ES");
		StaticResourceBundle.createResourceBundle(locale);
		assertEquals(locale, StaticResourceBundle.getResourceBundle().getLocale());
	}
}
