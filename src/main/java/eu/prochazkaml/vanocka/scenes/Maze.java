package eu.prochazkaml.vanocka.scenes;

import eu.prochazkaml.vanocka.FrameBuffer;
import eu.prochazkaml.vanocka.JoystickThread;
import eu.prochazkaml.vanocka.ScreenFader;
import eu.prochazkaml.vanocka.RayCaster;
import eu.prochazkaml.vanocka.RayCasterColorMap;
import eu.prochazkaml.vanocka.RayCasterMap;

public class Maze {
	public static void run(FrameBuffer fb, JoystickThread joystick, boolean debugOutput, boolean renderSingleFrame) {
		//RayCasterMap testMap = new RayCasterMap(10, 10, 6.5, 3.5, -Math.PI / 4, new char[][] {
		RayCasterMap testMap = new RayCasterMap(36, 40, 18, -2.5, Math.PI / 2, new char[][] {
			"....................................".toCharArray(),
			".################..################.".toCharArray(),
			".#...........A...........A........#.".toCharArray(),
			".#...........A...........A........#.".toCharArray(),
			".#..A..BBBB..A..B..B..AAAA..BBBB..#.".toCharArray(),
			".#..A.....B.....B..B..A.....B.....#.".toCharArray(),
			".#..A.....B.....B..B..A.....B.....#.".toCharArray(),
			".#..AAAA..BBBBBBBBBB..A..BBBB..A..#.".toCharArray(),
			".#..A.....B..A...........B..B..A..#.".toCharArray(),
			".#..A.....B..A...........B..B..A..#.".toCharArray(),
			".#..A..AAAB..A..BBBBBBB..B..B..A..#.".toCharArray(),
			".#..A..A..B..A..B.....B.....B..A..#.".toCharArray(),
			".#..A..A..B..A..B.....B.....B..A..#.".toCharArray(),
			".#..A..A..B..A..B..BBBBBBB..B..A..#.".toCharArray(),
			".#..A..A..B..A.....B........B..A..#.".toCharArray(),
			".#..A..A..B..A.....B........B..A..#.".toCharArray(),
			".#..AAAA..B..A..BBBB..AAAAAAB..A..#.".toCharArray(),
			".#..A.....B.....B.....A.....B..A..#.".toCharArray(),
			".#..A.....B.....B.....A.....B..A..#.".toCharArray(),
			".#..A..BBBBBBBBBB..AAAAAAA..BAAA..#.".toCharArray(),
			".#..A..B.....B..B..A........B.....#.".toCharArray(),
			".#..A..B.....B..B..A........B.....#.".toCharArray(),
			".#..A..B..A..B..B..A..BBBBBBB..AAA#.".toCharArray(),
			".#.....B..A........A........B.....#.".toCharArray(),
			".#.....B..A........A........B.....#.".toCharArray(),
			".#..BBBB..AAAAAAAAAAAAAAAA..BBBB..#.".toCharArray(),
			".#..B.....A..............A.....B..#.".toCharArray(),
			".#..B.....A..............A.....B..#.".toCharArray(),
			".#..B..AAAAAAAAAAAAAAAA..A..BBBB..#.".toCharArray(),
			".#..B..............A##A...........#.".toCharArray(),
			".#..B..............A##A...........#.".toCharArray(),
			".#..BBBBBBBBBBBBB..AAAA..AAAAAAAAA#.".toCharArray(),
			".#........B..AB......AA........A..#.".toCharArray(),
			".#........B..AB......AA........A..#.".toCharArray(),
			".#..BBBBBBB..AB..??..AA..AAAAAAA..#.".toCharArray(),
			".#...........AB..??..AA...........#.".toCharArray(),
			".#...........AB......AA...........#.".toCharArray(),
			".#AAAAAAAAAAAAB......A#BBBBBBBBBBB#.".toCharArray(),
			".##################################.".toCharArray(),
			"....................................".toCharArray(),
		}, 0x8080FF, 0xE0E0E0, new RayCasterColorMap[] {
			new RayCasterColorMap('#', 0x008000, 0x006000),
			new RayCasterColorMap('A', 0xFF0000, 0xE00000),
			new RayCasterColorMap('B', 0xFFC0C0, 0xE0A0A0),
			new RayCasterColorMap('?', 0xE06000, 0xFF8000)
		});

		RayCaster testCaster = new RayCaster(fb, testMap, fb.w, fb.h, Math.PI / 3, .3, debugOutput);

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

		while(true) {
			testCaster.render();
			fader.process();

			fb.updateLimited();

			testCaster.handleMovement(joystick);

			if(testCaster.playerX >= 17.f && testCaster.playerX < 19.f && testCaster.playerY >= 33.f && testCaster.playerY < 34.f)
				break;

			System.out.println();
			System.out.printf("angle: %f | x: %f | y: %f               ", testCaster.playerAngle, testCaster.playerX, testCaster.playerY);
		}
	}
}
