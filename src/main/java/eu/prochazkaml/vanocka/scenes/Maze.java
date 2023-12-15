package eu.prochazkaml.vanocka.scenes;

import eu.prochazkaml.vanocka.FrameBuffer;
import eu.prochazkaml.vanocka.JoystickThread;
import eu.prochazkaml.vanocka.ScreenFader;
import eu.prochazkaml.vanocka.MusicPlayer;
import eu.prochazkaml.vanocka.RayCaster;
import eu.prochazkaml.vanocka.RayCasterColorMap;
import eu.prochazkaml.vanocka.RayCasterMap;

public class Maze {
	public static void run(FrameBuffer fb, JoystickThread joystick, boolean debugOutput, boolean renderSingleFrame) {
		//RayCasterMap testMap = new RayCasterMap(10, 10, 6.5, 3.5, -Math.PI / 4, new char[][] {
		RayCasterMap testMap = new RayCasterMap(10, 10, 5, 2.5, 1, new char[][] {
			{ '#', '#', '#', '#', '#', '#', '#', '#', '#', '#' },
			{ '#', '.', '.', '.', '.', '.', '.', '.', '.', '#' },
			{ '#', '.', '#', '.', '.', '.', '.', '?', '.', '#' },
			{ '#', '.', '.', '.', '.', '.', '.', '.', '.', '#' },
			{ '#', '.', '.', '.', '.', '.', '.', '.', '.', '#' },
			{ '#', '.', '.', '.', '.', '.', '.', '#', '.', '#' },
			{ '#', '.', '.', '.', '.', '.', '.', '.', '.', '#' },
			{ '#', '.', '.', '.', '.', '.', '.', '#', '.', '#' },
			{ '#', '#', '.', '.', '.', '.', '.', '.', '.', '#' },
			{ '#', '#', '#', '#', '#', '#', '#', '#', '#', '#' }
		}, 0x8080FF, 0xE0E0E0, new RayCasterColorMap[] {
			new RayCasterColorMap('#', 0x008000, 0x006000),
			new RayCasterColorMap('?', 0xFF8000, 0xE06000)
		});

		RayCaster testCaster = new RayCaster(fb, testMap, fb.w, fb.h, Math.PI / 3, debugOutput);

		if(renderSingleFrame) {
			testCaster.render();
			fb.update();
			return;
		}

		if(debugOutput) {
			testCaster.render();
			return;
		}

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
