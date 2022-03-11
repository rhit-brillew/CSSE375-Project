package risk.controller;

import static org.junit.Assert.*;

import java.awt.*;
import org.junit.Test;
import risk.controller.ColorFactory;

/**
 * BVA:
 * CASE                 | Expected
 * Not included Color   | IllegalArgumentException
 * Lowercase string     | correct color
 * Uppercase string     | correct color
 * Full caps string     | correct color
 * Lowercase space      | correct color
 * Uppercase space      | correct color
 * Full caps space      | correct color
 * All Colors           | all correct colors
 */
public class TestColorFactory {
	@Test
	public void testNotIncludedColor() {
		try {
			ColorFactory.createColor("player6");
			fail();
		} catch (IllegalArgumentException e) {
			assertEquals("This color does not exist", e.getMessage());
		}
	}

	@Test
	public void testIncludedColorLowercase() {
		Color red = Color.RED;
		assertEquals(red, ColorFactory.createColor("player1"));
	}

	@Test
	public void testIncludedColorUppercase() {
		Color red = Color.RED;
		assertEquals(red, ColorFactory.createColor("Player1"));
	}

	@Test
	public void testIncludedColorFullCaps() {
		Color red = Color.RED;
		assertEquals(red, ColorFactory.createColor("PLAYER1"));
	}

	@Test
	public void testIncludedColorLowercaseSpace() {
		Color red = Color.RED;
		assertEquals(red, ColorFactory.createColor("player 1"));
	}

	@Test
	public void testIncludedColorUppercaseSpace() {
		Color red = Color.RED;
		assertEquals(red, ColorFactory.createColor("Player 1"));
	}

	@Test
	public void testIncludedColorFullCapsSpace() {
		Color red = Color.RED;
		assertEquals(red, ColorFactory.createColor("PLAYER 1"));
	}

	@Test
	public void testAllIncludedColors() {
		Color red = Color.RED;
		assertEquals(red, ColorFactory.createColor("player1"));
		assertEquals(new Color(0, 150, 0), ColorFactory.createColor("player2"));
		Color blue = Color.BLUE;
		assertEquals(blue, ColorFactory.createColor("player3"));
		Color magenta = Color.MAGENTA;
		assertEquals(magenta, ColorFactory.createColor("player4"));
		assertEquals(new Color(0, 150, 150), ColorFactory.createColor("player5"));
	}
}
