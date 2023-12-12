package eu.prochazkaml.vanocka;

public class vanocka {
	public static void main(String[] args) {
		FrameBuffer fb = new FrameBuffer(80, 24, 20);

		RayCasterMap testMap = new RayCasterMap(10, 10, 4.7, 4.1, -Math.PI / 4, new char[][] {
			{ '#', '#', '#', '#', '#', '#', '#', '#', '#', '#' },
			{ '#', '.', '.', '.', '.', '.', '.', '.', '.', '#' },
			{ '#', '.', '#', '.', '.', '.', '.', '#', '.', '#' },
			{ '#', '.', '.', '.', '.', '.', '.', '.', '.', '#' },
			{ '#', '.', '.', '.', '.', '.', '.', '.', '.', '#' },
			{ '#', '.', '.', '.', '.', '.', '.', '#', '.', '#' },
			{ '#', '.', '.', '.', '.', '.', '.', '.', '.', '#' },
			{ '#', '.', '.', '.', '.', '.', '.', '#', '.', '#' },
			{ '#', '#', '.', '.', '.', '.', '.', '.', '.', '#' },
			{ '#', '#', '#', '#', '#', '#', '#', '#', '#', '#' }
		});

		boolean debugOutput = false;

		RayCaster testCaster = new RayCaster(fb, testMap, fb.w, fb.h, Math.PI / 3.f, debugOutput);

		if(debugOutput) {
			testCaster.render();
		} else while(true) {
			testCaster.render();
			testCaster.playerAngle += .01;

			fb.updateLimited();
		}
	}
}
