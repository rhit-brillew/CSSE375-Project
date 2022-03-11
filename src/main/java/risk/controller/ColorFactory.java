package risk.controller;

import java.awt.*;
import java.util.Locale;

public class ColorFactory {

	public static Color createColor(String color) {
		color = color.toLowerCase(Locale.ROOT);
		color = color.replaceAll(" ", "");
		switch(color) {
			case "player1":
				return Color.RED;
			case "player2":
				return new Color(0, 150, 0);
			case "player3":
				return Color.BLUE;
			case "player4":
				return Color.MAGENTA;
			case "player5":
				return new Color(0, 150, 150);
			default:
				throw new IllegalArgumentException("This color does not exist");
		}
	}
}
