package eu.prochazkaml.vanocka;
//import java.awt.MouseInfo;

public class vanocka {
	public static void main(String[] args) {
		FrameBuffer fb = new FrameBuffer(80, 24, 20);

//		int colors[] = { 0xFF8080, 0xFFC080, 0xFFFF80, 0xC0FF80, 0x80FF80, 0x80FFC0, 0x80FFFF, 0x80C0FF, 0x8080FF, 0xC080FF, 0xFF80FF, 0xFF80C0 };

//		int off = 0;

		RayCasterMap testMap = new RayCasterMap(10, 10, 4, 4, 5.f * Math.PI / 4.f, new char[][] {
			{ '#', '#', '#', '#', '#', '#', '#', '#', '#', '#' },
			{ '#', '.', '.', '.', '.', '.', '.', '.', '.', '#' },
			{ '#', '.', '#', '.', '.', '.', '.', '.', '.', '#' },
			{ '#', '.', '.', '.', '.', '.', '.', '.', '.', '#' },
			{ '#', '.', '.', '.', '.', '.', '.', '.', '.', '#' },
			{ '#', '.', '.', '.', '.', '.', '.', '.', '.', '#' },
			{ '#', '.', '.', '.', '.', '.', '.', '.', '.', '#' },
			{ '#', '.', '.', '.', '.', '.', '.', '.', '.', '#' },
			{ '#', '.', '.', '.', '.', '.', '.', '.', '.', '#' },
			{ '#', '.', '.', '.', '.', '.', '.', '#', '.', '#' },
			{ '#', '.', '.', '.', '.', '.', '.', '.', '.', '#' },
			{ '#', '#', '#', '#', '#', '#', '#', '#', '#', '#' }
		});

		RayCaster testCaster = new RayCaster(fb, testMap, fb.w, fb.h, Math.PI / 3.f);

		while(true) {
/*			for(int x = 0; x < fb.w; x++)
				for(int y = 0; y < fb.h; y++)
					fb.pixels[x][y] = colors[(x - y + off + 10000) % 12];

			off++;*/

			testCaster.render();
			testCaster.playerAngle += .01;

			fb.updateLimited();
		}

/*/		while(true) {
			System.out.println(MouseInfo.getPointerInfo().getLocation());
			try {
				Thread.sleep(16);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}*/
	}
}
