package eu.prochazkaml.vanocka;

public class TestScreen {
	public FrameBuffer fb;
	
	private int frame = 0;
	private String msg = "Stiskněte libovolné tlačítko na ovladači.";
	private int fg = 0xFFFFFF, bg = 0x800000;

	public TestScreen(FrameBuffer _fb) {
		fb = _fb;
		fb.textForegroundColor = fg;
		fb.textBackgroundColor = bg;
	}

	public void render() {
		double framediv = 25;

		// Draw background animation

		for(int x = 0; x < fb.w; x++)
			for(int y = 0; y < fb.h; y++)
				fb.pixels[x][y] = 
					(((int)(Math.sin((double)(frame + x - y) / framediv + 2 * Math.PI / 3) * 127) + 128) << 16) +
					(((int)(Math.sin((double)(frame + x - y) / framediv + 4 * Math.PI / 3) * 127) + 128) << 8) +
					(int)(Math.sin((double)(frame + x - y) / framediv) * 127) + 128;

		// Draw window background
		
		int wx = (fb.physicalWidth - msg.length()) / 2;
		int wy = fb.physicalHeight / 2;
		
		for(int x = 0; x < msg.length() + 10; x++) {
			for(int y = 0; y < 10; y++) {
				fb.pixels[x + wx - 5][y + wy * 2 - 4] = bg;
			}
		}

		// Draw window decoration

		for(int x = 0; x < msg.length() + 8; x++) {
			fb.pixels[x + wx - 4][wy * 2 - 3] = fg;
			fb.pixels[x + wx - 4][wy * 2 + 4] = fg;
		}

		for(int y = 0; y < 8; y++) {
			fb.pixels[wx - 4][y + wy * 2 - 3] = fg;
			fb.pixels[wx + msg.length() + 3][y + wy * 2 - 3] = fg;
		}

		// Draw window text

		fb.textCursorX = wx;
		fb.textCursorY = wy;
		fb.writeText(msg);

		frame++;
	}
}
