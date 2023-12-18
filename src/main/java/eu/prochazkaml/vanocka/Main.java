package eu.prochazkaml.vanocka;

import eu.prochazkaml.vanocka.scenes.JoystickSetup;
import eu.prochazkaml.vanocka.scenes.StoryIntro;
import eu.prochazkaml.vanocka.scenes.Maze;
import eu.prochazkaml.vanocka.scenes.StoryOutro;

public class Main {
	public static void main(String[] args) {
		FrameBuffer fb = new FrameBuffer(80, 24, 20);

		// ========== FOR TESTING PURPOSES ==========

		boolean debugOutput = false;
		boolean renderSingleFrame = false;

		if(debugOutput || renderSingleFrame) {
			Maze.run(fb, null, debugOutput, renderSingleFrame);
			return;
		}

		// ==========================================

		JoystickThread joystick = new JoystickThread();
		joystick.start();

		JoystickSetup.run(fb, joystick);

		MusicPlayer mp = new MusicPlayer("assets/jbells.mid");
		mp.start();

		StoryIntro.run(fb, joystick);

		Maze.run(fb, joystick, debugOutput, renderSingleFrame);

		StoryOutro.run(fb, joystick);

		mp.interrupt();
		joystick.interrupt();

		System.out.println();
		System.out.println("Děkujeme Vám za hraní této hry! ");

		System.exit(0);
	}
}
