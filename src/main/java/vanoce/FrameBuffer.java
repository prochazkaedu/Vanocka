package vanoce;

public class FrameBuffer {
	public int physicalWidth, physicalHeight;
	public int w, h;
	public int pixels[][];

	private char[] renderstring, renderstringbackup;
	private long start = 0;
	public long msPerFrame = 40;
	public boolean printDebug = true;

	public int textCursorX = 0, textCursorY = 0;
	public int textForegroundColor = 0xFFFFFF, textBackgroundColor = 0x000000;

	public FrameBuffer(int _w, int _h, long _msPerFrame, boolean _printDebug) {
		this(_w, _h);

		msPerFrame = _msPerFrame;
		printDebug = _printDebug;
	}

	public FrameBuffer(int _w, int _h, long _msPerFrame) {
		this(_w, _h);

		msPerFrame = _msPerFrame;
	}

	public FrameBuffer(int _w, int _h) {
		physicalWidth = _w;
		physicalHeight = _h;

		w = _w;
		h = _h * 2;

		pixels = new int[w][h];

		String output = "\033[?25l";

		for(int y = 0; y < physicalHeight; y++) {
			output += String.format("\033[%03d;1H", y + 1);

			for(int x = 0; x < physicalWidth; x++) {
				output += "\033[38;2;000;000;000m";
				output += "\033[48;2;000;000;000m";
				output += "▄";
			}
		}

		output += "\033[0m\033[?25h";

		renderstring = output.toCharArray();
		renderstringbackup = output.toCharArray();

		start = System.currentTimeMillis();
	}

	public void writeText(char c) {
		int ptr = 21 + 31 + textCursorY * (physicalWidth * 39 + 8) + textCursorX * 39;

		renderstring[ptr] = c;
		pixels[textCursorX][textCursorY * 2] = textBackgroundColor;
		pixels[textCursorX][textCursorY * 2 + 1] = textForegroundColor;

		textCursorX++;

		if(textCursorX >= physicalWidth) {
			textCursorX = 0;
			textCursorY++;

			if(textCursorY >= physicalHeight) {
				textCursorY = 0;
			}
		}
	}

	public void writeText(String str) {
		for(int i = 0; i < str.length(); i++) {
			writeText(str.charAt(i));
		}
	}

	public void removeText() {
		System.arraycopy(renderstringbackup, 0, renderstring, 0, renderstringbackup.length);
	}

	public void update() {
		int hpixel, lpixel;

		for(int y = 0; y < physicalHeight; y++) {
			int lineptr = 21 + y * (physicalWidth * 39 + 8);

			for(int x = 0; x < physicalWidth; x++) {
				hpixel = pixels[x][y * 2];
				lpixel = pixels[x][y * 2 + 1];

				renderstring[lineptr] = (char) (48 + ((lpixel >> 16) & 0xFF) / 100);
				renderstring[lineptr + 1] = (char) (48 + ((lpixel >> 16) & 0xFF) / 10 % 10);
				renderstring[lineptr + 2] = (char) (48 + ((lpixel >> 16) & 0xFF) % 10);

				renderstring[lineptr + 4] = (char) (48 + ((lpixel >> 8) & 0xFF) / 100);
				renderstring[lineptr + 5] = (char) (48 + ((lpixel >> 8) & 0xFF) / 10 % 10);
				renderstring[lineptr + 6] = (char) (48 + ((lpixel >> 8) & 0xFF) % 10);

				renderstring[lineptr + 8] = (char) (48 + ((lpixel) & 0xFF) / 100);
				renderstring[lineptr + 9] = (char) (48 + ((lpixel) & 0xFF) / 10 % 10);
				renderstring[lineptr + 10] = (char) (48 + ((lpixel) & 0xFF) % 10);

				renderstring[lineptr + 19] = (char) (48 + ((hpixel >> 16) & 0xFF) / 100);
				renderstring[lineptr + 20] = (char) (48 + ((hpixel >> 16) & 0xFF) / 10 % 10);
				renderstring[lineptr + 21] = (char) (48 + ((hpixel >> 16) & 0xFF) % 10);

				renderstring[lineptr + 23] = (char) (48 + ((hpixel >> 8) & 0xFF) / 100);
				renderstring[lineptr + 24] = (char) (48 + ((hpixel >> 8) & 0xFF) / 10 % 10);
				renderstring[lineptr + 25] = (char) (48 + ((hpixel >> 8) & 0xFF) % 10);

				renderstring[lineptr + 27] = (char) (48 + ((hpixel) & 0xFF) / 100);
				renderstring[lineptr + 28] = (char) (48 + ((hpixel) & 0xFF) / 10 % 10);
				renderstring[lineptr + 29] = (char) (48 + ((hpixel) & 0xFF) % 10);
				
				lineptr += 39;
			}
		}

		System.out.println(renderstring);
	}

	public void updateLimited() {
		this.update();

		long towait = msPerFrame - (System.currentTimeMillis() - start);

		try {
			Thread.sleep(towait);
		} catch(Exception e) {
			
		}

		if(printDebug) {
			System.out.printf((System.currentTimeMillis() - start) + " ms (%.2f fps, %.2f%% usage)", 1000.0 / (System.currentTimeMillis() - start), (double)(msPerFrame - towait) / (double)msPerFrame * 100.0);
		}

		start = System.currentTimeMillis();
	}
}
