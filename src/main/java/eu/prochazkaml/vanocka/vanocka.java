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

		TestScreen testScreen = new TestScreen(fb);

		JoystickThread joystick = new JoystickThread();

		joystick.start();

		Thread.sleep(500);

		for(int i = 0; i < 6; i++) {
			joystick.setupStep = i;
			joystick.setupStepRunning = true;

			System.out.printf("Namapujte prosím %d.\n", i);

			while(joystick.setupStepRunning) Thread.sleep(100);
		}

		joystick.setupStep = -1;

		if(debugOutput) {
			testCaster.render();
		} else while(true) {
			testCaster.render();

			testCaster.playerAngle += joystick.rot / 30.f;
			testCaster.playerX += joystick.xmove / 25.f;
			testCaster.playerY += joystick.ymove / 25.f;
			
			fb.updateLimited();

			System.out.println();
			System.out.printf("%f %f %f", testCaster.playerAngle, testCaster.playerX, testCaster.playerY);
		}
	}
}
