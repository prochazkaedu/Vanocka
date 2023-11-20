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

	private char getBlock(int x, int y) {
		//System.out.printf("Read block %d/%d %f\n", x, y, playerX);
		if(x >= map.w || y >= map.h || x < 0 || y < 0) return '0';

		return map.map[y][x];
	}

	public void render() {
//		for(int x = 38; x < 43; x++) {
		for(int x = 0; x < vw; x++) {
			double relangle = playerFOV * (double)x / (double)vw - playerFOV / 2;
			double angle = playerAngle + relangle;
			double px = playerX, py = playerY, oldpx, oldpy;

			double distance = 0;

			if(angle == 0) angle += Double.MIN_VALUE; // Prevents NaN
			double tmptan = Math.tan(angle);

			double horizUnitDeltaY = tmptan * Math.signum(Math.cos(angle));
			double vertUnitDeltaX = 1 / tmptan * Math.signum(Math.sin(angle));

			System.err.printf("%d: %f %f/%f\n", x, angle, vertUnitDeltaX, horizUnitDeltaY);

			int wallColor = 0xFFFFFF;

			double toTravelX, toTravelY;

			if(Math.abs(horizUnitDeltaY) < Math.abs(vertUnitDeltaX)) {
				// The fired ray will mainly go left/right

				double dx = Math.signum(vertUnitDeltaX), dy = horizUnitDeltaY;
				System.err.printf("- Incrementing X by %f, Y by %f.\n", dx, dy);

				// Figure out how much to "travel" in the X direction initally

				// TODO - finish the other quadrants

				if(dx > 0) { // Right
					System.err.printf(" - RAY GOING RIGHT\n");

					toTravelX = Math.floor(px + 1) - px;

					toTravelY = dy * toTravelX / dx;

					System.err.printf(" - First increment X by %f, Y by %f.\n", toTravelX, toTravelY);

					while(true) {
						oldpx = px;
						oldpy = py;

						px += toTravelX;
						py += toTravelY;

						System.err.printf("- %f/%f + %f/%f = %f/%f\n", oldpx, oldpy, toTravelX, toTravelY, px, py);

						if((int)oldpy != (int)py) {
							System.err.printf(" - CROSSED HORIZONTAL BOUNDARY (%d/%d)\n", (int)oldpx, (int)py);

							double ratio = 0;

							if(toTravelY > 0) {
								ratio = (((int)py) - oldpy) / toTravelY;
							} else {
								ratio = (((int)oldpy) - oldpy) / toTravelY;
							}

							System.err.printf(" - *** %f (%f / %d)\n", ratio, oldpy, (int)(oldpy));

							if(getBlock((int)oldpx, (int)py) != '.') {
								px = oldpx + toTravelX * ratio;
								py = oldpy + toTravelY * ratio;
								System.err.printf(" - Reached horizontal wall at %f/%f (block %d/%d)\n", px, py, (int)oldpx, (int)py);
								wallColor = 0xE0E0E0;
								break;
							}
						}

						if(getBlock((int)px, (int)py) != '.') {
							System.err.printf(" - Reached vertical wall at %f/%f (block %d/%d)\n", px, py, (int)px, (int)py);
							break;
						}

						toTravelX = dx;
						toTravelY = dy;

						System.err.printf(" - Moved to: %f/%f\n", px, py);
					}
				} else if(dx < 0) { // Left
					toTravelX = Math.floor(px) - px;
				} else {
					continue; // Should never happen, but Java screams otherwise
				}
			} else {
				// The fired ray will mainly go up/down

				double dx = vertUnitDeltaX, dy = Math.signum(horizUnitDeltaY);
				System.err.printf("- Incrementing X by %f, Y by %f.\n", dx, dy);

				if(dy > 0) { // Down
					System.err.printf(" - RAY GOING DOWN\n");

					toTravelY = Math.floor(py + 1) - py;

					toTravelX = dx * toTravelY / dy;

					System.err.printf(" - First increment X by %f, Y by %f.\n", toTravelX, toTravelY);

					while(true) {
						oldpx = px;
						oldpy = py;

						px += toTravelX;
						py += toTravelY;

						System.err.printf("- %f/%f + %f/%f = %f/%f\n", oldpx, oldpy, toTravelX, toTravelY, px, py);

						if((int)oldpx != (int)px) {
							System.err.printf(" - CROSSED VERTICAL BOUNDARY (%d/%d)\n", (int)px, (int)oldpy);

							double ratio = 0;

							if(toTravelX > 0) {
								ratio = (((int)px) - oldpx) / toTravelX;
							} else {
								ratio = (((int)oldpx) - oldpx) / toTravelX;
							}

							System.err.printf(" - *** %f (%f / %d)\n", ratio, oldpx, (int)(oldpx));

							if(getBlock((int)px, (int)oldpy) != '.') {
								px = oldpx + toTravelX * ratio;
								py = oldpy + toTravelY * ratio;
								System.err.printf(" - Reached vertical wall at %f/%f (block %d/%d)\n", oldpx + toTravelX * ratio, oldpy + toTravelY * ratio, (int)px, (int)oldpy);
								break;
							}
						}

						if(getBlock((int)px, (int)py) != '.') {
							System.err.printf(" - Reached horizontal wall at %f/%f (block %d/%d)\n", px, py, (int)px, (int)py);
							wallColor = 0xE0E0E0;
							break;
						}

						toTravelX = dx;
						toTravelY = dy;

						System.err.printf(" - Moved to: %f/%f\n", px, py);
					}
				} else if(dy < 0) {

				} else {
					continue; // Should never happen, but Java screams otherwise
				}
			}

			// Perform fish-eye correction

			distance = Math.hypot(playerX - px, playerY - py) * Math.cos(relangle);

			// Render this column of pixels

			this.drawColumn(x, (1-1.f / distance) * (double)(fb.h / 2), 0x808080, 0x404040, wallColor);

			// System.err.printf("%d: %.02f %f\n", x, angle, distance);
		}
	}

	void drawColumn(int x, double val, int colourCeiling, int colourFloor, int colourWall) {
		int intval = (int)val;
		
		colourWall = interpolateColours(colourWall, 0x808080, Math.min(val / 100, .5));

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
