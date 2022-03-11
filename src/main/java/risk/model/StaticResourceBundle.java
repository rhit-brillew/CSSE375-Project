package risk.model;

import java.util.Locale;
import java.util.ResourceBundle;

public class StaticResourceBundle {
	
	private static ResourceBundle resourceBundle;
	
	private StaticResourceBundle(Locale locale) {
		resourceBundle = ResourceBundle.getBundle("messages", locale);
	}
	
	public static void createResourceBundle(Locale locale) {
		new StaticResourceBundle(locale);
	}
	
	public static ResourceBundle getResourceBundle() {
		return resourceBundle;
	}
}
