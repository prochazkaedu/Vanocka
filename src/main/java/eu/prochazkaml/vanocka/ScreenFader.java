package eu.prochazkaml.vanocka;

// Provides a cool DOOM-like screen melt effect.
// The constructor creates a copy of the current framebuffer contents, you just need to call process() after each rendered frame.

public class ScreenFader {
	public FrameBuffer fb;

	private int[][] fadePixels;
	private double[] fadePos;
	private double[] fadeSpeed;
	private double[] fadeAccel;

	public ScreenFader(FrameBuffer _fb) {
		fb = _fb;
		
		fadePixels = new int[fb.pixels.length][];
		fadePos = new double[fadePixels.length];
		fadeSpeed = new double[fadePixels.length];
		fadeAccel = new double[fadePixels.length];

		for(int i = 0; i < fadePixels.length; i++) {
			fadePixels[i] = fb.pixels[i].clone();
			fadePos[i] = 0;
			fadeSpeed[i] = 1;
			fadeAccel[i] = Math.random() / 20 + .02;
		}
	}

	public void process() {
		for(int i = 0; i < fadePixels.length; i++) {
			if(fadePos[i] > fb.h) continue;

			int dest = (int)fadePos[i];
			if(dest < 0) dest = 0;

			for(int j = 0; j < fadePixels[i].length; j++) {
				if(j + dest >= fb.h) break;

				fb.pixels[i][j + dest] = fadePixels[i][j];
			}

			fadePos[i] += fadeSpeed[i];
			fadeSpeed[i] += fadeAccel[i];
		}
	}
}
