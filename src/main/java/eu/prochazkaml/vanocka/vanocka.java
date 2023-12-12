package eu.prochazkaml.vanocka;

public class vanocka {
	public static void main(String[] args) throws InterruptedException {
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
			return;
		}

		TestScreen testScreen = new TestScreen(fb, "");

		JoystickThread joystick = new JoystickThread();

		joystick.start();

		Thread.sleep(500);

		String[] bindings = {
			"pohyb dopředu",
			"pohyb dozadu",
			"pohyb doleva",
			"pohyb doprava",
			"pohyb otočení doleva",
			"pohyb otočení doprava",
		};

		for(int i = 0; i < 6; i++) {
			testScreen.msg = String.format("Stiskněte prosím tlačítko na %s.", bindings[i]);
			fb.removeText();

			joystick.setupStep = i;
			joystick.setupStepRunning = true;

			while(joystick.setupStepRunning) {
				testScreen.render();
				fb.updateLimited();
			}
		}

		joystick.setupStep = -1;

		// Cool fade effect

		int[][] fadePixels = new int[fb.pixels.length][];
		double[] fadePos = new double[fadePixels.length];
		double[] fadeSpeed = new double[fadePixels.length];
		double[] fadeAccel = new double[fadePixels.length];

		for(int i = 0; i < fadePixels.length; i++) {
			fadePixels[i] = fb.pixels[i].clone();
			fadePos[i] = 0;
			fadeSpeed[i] = 1;
			fadeAccel[i] = Math.random() / 20 + .02;
		}

		while(true) {
			testCaster.render();

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

			testCaster.playerAngle += joystick.rot / 30.f;

			double dx = joystick.xmove / 25.f, dy = -joystick.ymove / 25.f;

			testCaster.playerX += Math.cos(testCaster.playerAngle) * dy - Math.sin(testCaster.playerAngle) * dx;
			testCaster.playerY += Math.sin(testCaster.playerAngle) * dy + Math.cos(testCaster.playerAngle) * dx;
			
			fb.updateLimited();

			System.out.println();
			System.out.printf("%f %f %f", testCaster.playerAngle, testCaster.playerX, testCaster.playerY);
		}
	}
}
