package eu.prochazkaml.vanocka;

import eu.prochazkaml.vanocka.scenes.JoystickSetup;
import eu.prochazkaml.vanocka.scenes.StoryIntro;
import eu.prochazkaml.vanocka.scenes.Maze;

public class vanocka {
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

		StoryIntro.run(fb, joystick);

		Maze.run(fb, joystick, debugOutput, renderSingleFrame);
	}
}
