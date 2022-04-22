package risk.view;

import static org.junit.Assert.assertEquals;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import org.junit.Test;

/**
 * BVA: changeImageToColor
 * CASE                             | EXPECTED
 * 1x1 pixel image, magenta         | image is now magenta
 * 1x50 pixel image, magenta        | image is now magenta
 * 50x1 pixel image, magenta        | image is now magenta
 * 50x50 pixel image, magenta       | image is now magenta
 * 1x1 clear pixel image, magenta   | image is still clear
 * 1x50 clear pixel image, magenta  | image is still clear
 * 50x1 clear pixel image, magenta  | image is still clear
 * 50x50clear pixel image, magenta  | image is still clear
 * Max int was not used because of memory limits.
 */

public class TestGameView {
	@Test
	public void testChangeImageToColor1x1Pixel() {
		BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		GameView.changeImageToColor(image, Color.MAGENTA);
		assertEquals(1, image.getWidth());
		assertEquals(1, image.getHeight());
		assertImageIsMagenta(image);
	}

	private void assertImageIsMagenta(BufferedImage image) {
		for(int x = 0; x < image.getWidth(); x++) {
			for(int y = 0; y < image.getHeight(); y++) {
				assertEquals(0xFF00FF, image.getRGB(x, y));
			}
		}
	}

	@Test
	public void testChangeImageToColor1x50Pixel() {
		BufferedImage image = new BufferedImage(1, 50, BufferedImage.TYPE_INT_ARGB);
		GameView.changeImageToColor(image, Color.MAGENTA);
		assertEquals(1, image.getWidth());
		assertEquals(50, image.getHeight());
		assertImageIsMagenta(image);
	}

	@Test
	public void testChangeImageToColor50x1Pixel() {
		BufferedImage image = new BufferedImage(50, 1, BufferedImage.TYPE_INT_ARGB);
		GameView.changeImageToColor(image, Color.MAGENTA);
		assertEquals(50, image.getWidth());
		assertEquals(1, image.getHeight());
		assertImageIsMagenta(image);
	}

	@Test
	public void testChangeImageToColor50x50Pixel() {
		BufferedImage image = new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB);
		GameView.changeImageToColor(image, Color.MAGENTA);
		assertEquals(50, image.getWidth());
		assertEquals(50, image.getHeight());
		assertImageIsMagenta(image);
	}

	@Test
	public void testChangeImageToColor1x1ClearPixel() {
		BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		makeImageClear(image);
		GameView.changeImageToColor(image, Color.MAGENTA);
		assertEquals(1, image.getWidth());
		assertEquals(1, image.getHeight());
		assertImageIsClear(image);
	}

	private void makeImageClear(BufferedImage image) {
		WritableRaster raster = image.getRaster();
		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				raster.setPixel(x, y, new int[]{255, 255, 255, 0});
			}
		}
	}

	private void assertImageIsClear(BufferedImage image) {
		WritableRaster raster = image.getRaster();
		for(int x = 0; x < image.getWidth(); x++) {
			for(int y = 0; y < image.getHeight(); y++) {
				int[] pixelRGBA = raster.getPixel(x, y, (int[]) null);
				assertEquals(0, pixelRGBA[3]);
			}
		}
	}

	@Test
	public void testChangeImageToColor1x50ClearPixel() {
		BufferedImage image = new BufferedImage(1, 50, BufferedImage.TYPE_INT_ARGB);
		makeImageClear(image);
		GameView.changeImageToColor(image, Color.MAGENTA);
		assertEquals(1, image.getWidth());
		assertEquals(50, image.getHeight());
		assertImageIsClear(image);
	}

	@Test
	public void testChangeImageToColor50x1ClearPixel() {
		BufferedImage image = new BufferedImage(50, 1, BufferedImage.TYPE_INT_ARGB);
		makeImageClear(image);
		GameView.changeImageToColor(image, Color.MAGENTA);
		assertEquals(50, image.getWidth());
		assertEquals(1, image.getHeight());
		assertImageIsClear(image);
	}

	@Test
	public void testChangeImageToColor50x50ClearPixel() {
		BufferedImage image = new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB);
		makeImageClear(image);
		GameView.changeImageToColor(image, Color.MAGENTA);
		assertEquals(50, image.getWidth());
		assertEquals(50, image.getHeight());
		assertImageIsClear(image);
	}
}
