package vanoce.scenes;
import vanoce.FrameBuffer;
import vanoce.JoystickThread;
import vanoce.TestScreen;

public class JoystickSetup {
	public static void run(FrameBuffer fb, JoystickThread joystick) {
		TestScreen testScreen = new TestScreen(fb, "");

		try { Thread.sleep(500); } catch (InterruptedException e) { }

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
	}
}
