package eu.prochazkaml.vanocka;

public class vanocka {
	public static void main(String[] args) throws InterruptedException {
		FrameBuffer fb = new FrameBuffer(80, 24, 20);

		//RayCasterMap testMap = new RayCasterMap(10, 10, 6.5, 3.5, -Math.PI / 4, new char[][] {
		RayCasterMap testMap = new RayCasterMap(10, 10, 5, 2.5, 1, new char[][] {
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
		boolean rendersingleframe = false;

		RayCaster testCaster = new RayCaster(fb, testMap, fb.w, fb.h, Math.PI / 3, debugOutput);

		if(rendersingleframe) {
			testCaster.render();
			fb.update();
			return;
		}

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

		ScreenFader fader = new ScreenFader(fb);

		MusicPlayer mp = new MusicPlayer("assets/e2m1.mid");
		mp.start();

		while(true) {
			testCaster.render();
			fader.process();

			testCaster.playerAngle += joystick.rot / 30.f;

			double dx = joystick.xmove / 25.f, dy = -joystick.ymove / 25.f;

			testCaster.playerX += Math.cos(testCaster.playerAngle) * dy - Math.sin(testCaster.playerAngle) * dx;
			testCaster.playerY += Math.sin(testCaster.playerAngle) * dy + Math.cos(testCaster.playerAngle) * dx;
			
			fb.updateLimited();

			System.out.println();
			System.out.printf("angle: %f | x: %f | y: %f               ", testCaster.playerAngle, testCaster.playerX, testCaster.playerY);
		}
	}
}
