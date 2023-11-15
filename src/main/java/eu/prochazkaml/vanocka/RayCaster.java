package eu.prochazkaml.vanocka;

public class RayCaster {
	public FrameBuffer fb;
	public final RayCasterMap map;
	
	public double playerX, playerY, playerAngle;
	public final double playerFOV;
	public final int vw, vh;

	public RayCaster(FrameBuffer _fb, RayCasterMap _map, int _viewportWidth, int _viewportHeight, double _playerFOV) {
		fb = _fb;
		map = _map;
		vw = _viewportWidth;
		vh = _viewportHeight;

		playerX = map.playerSpawnX;
		playerY = map.playerSpawnY;
		playerAngle = map.playerAngle;
		playerFOV = _playerFOV;
	}

	private char getBlock(int x, int y) throws Exception {
		//System.out.printf("Read block %d/%d %f\n", x, y, playerX);
		return map.map[y][x];		
	}

	public void render() {
		final double step = .01;

		for(int x = 0; x < vw; x++) {
			double absangle = playerFOV * (double)x / (double)vw - playerFOV / 2;
			double angle = playerAngle + absangle;
			double dx = Math.cos(angle) * step, dy = Math.sin(angle) * step;

			// Who cares about optimization anyways.

			double px = playerX, py = playerY;

			double distance = 0;

			char c;
			
			try {
				while(true) {
					c = getBlock((int)px, (int)py);

					if(c != '.') {
						// System.out.printf("%d: %f (%f/%f %f/%f %d/%d %f)\n", x, distance, px, py, px - dx, py - dy, (int)px - (int)(px - dx), (int)py - (int)(py - dy), Math.hypot(dx, dy));
						break;
					}

					px += dx;
					py += dy;
					distance += step;
				}

				distance *= Math.cos(absangle);

				double ix = px - Math.floor(px), iy = py - Math.floor(py);

				int colourIfTrue = 0xFFFFFF;

				if(ix - iy >= 0) colourIfTrue -= 0x101010;

				if(1 - ix - iy >= 0) colourIfTrue -= 0x202020;

				this.drawColumn(x, (1-1.f / distance) * (double)(fb.h / 2), 0x808080, 0x404040, colourIfTrue);

				// System.out.printf("%d: %.02f %f\n", x, angle, distance);
			} catch(Exception e) {
				// System.out.printf("%d: could not draw, too bad\n", x);
			}
		}
	}

	void drawColumn(int x, double val, int colourCeiling, int colourFloor, int colourWall) {
		int intval = (int)val;
		
		for(int y = 0; y < fb.h / 2; y++) {
			if(y == intval)
				fb.pixels[x][y] = interpolateColours(colourWall, colourCeiling, val);
			else if(y < intval)
				fb.pixels[x][y] = colourCeiling;
			else
				fb.pixels[x][y] = colourWall;

//			fb.pixels[x][y] = ((double)y < val) ? colourCeiling : colourWall;
		}

		for(int y = fb.h / 2; y < fb.h; y++) {
			if((fb.h - y) == intval)
				fb.pixels[x][y] = interpolateColours(colourWall, colourFloor, val);
			else if((fb.h - y) < intval)
				fb.pixels[x][y] = colourFloor;
			else
				fb.pixels[x][y] = colourWall;

//			fb.pixels[x][y] = ((double)(fb.h - y) < val) ? colourFloor : colourWall;
		}
	}

	int interpolateColours(int col1, int col2, double frac) {
		int r1 = col1 >> 16, g1 = (col1 >> 8) & 0xFF, b1 = col1 & 0xFF;
		int r2 = col2 >> 16, g2 = (col2 >> 8) & 0xFF, b2 = col2 & 0xFF;

		frac -= Math.floor(frac);

		int intfrac = (int)(frac * 256.f);
		int revintfrac = 256 - intfrac;

		return
			((r1 * revintfrac + r2 * intfrac) & 0xFF00) << 8 |
			((g1 * revintfrac + g2 * intfrac) & 0xFF00) |
			((b1 * revintfrac + b2 * intfrac) & 0xFF00) >> 8;
	}
}
