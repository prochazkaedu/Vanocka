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
//		for(int x = 0; x < 20; x++) {
		for(int x = 0; x < vw; x++) {
			double relangle = playerFOV * (double)x / (double)vw - playerFOV / 2;
			double angle = playerAngle + relangle;
			double px = playerX, py = playerY;

			double distance = 0;

			if(angle == 0) angle += Double.MIN_VALUE; // Prevents NaN
			double tmptan = Math.tan(angle);

			double horizUnitDeltaY = tmptan * Math.signum(Math.cos(angle));
			double vertUnitDeltaX = 1 / tmptan * Math.signum(Math.sin(angle));

			System.err.printf("%d: %f %f/%f\n", x, angle, vertUnitDeltaX, horizUnitDeltaY);

			int wallColor = 0xFFFFFF;

			if(Math.abs(horizUnitDeltaY) < Math.abs(vertUnitDeltaX)) {
				// The fired ray will mainly go left/right

				double dx = Math.signum(vertUnitDeltaX), dy = horizUnitDeltaY;
				System.err.printf("- Incrementing X by %f, Y by %f.\n", dx, dy);

				double toTravelX, toTravelY;

				// Figure out how much to "travel" in the X direction initally

				// TODO - finish the other quadrants

				if(dx > 0) { // Right
					// TODO - CHECK IF THE MATH CHECKS OUT

					toTravelX = Math.floor(px + 1) - px;

					toTravelY = dy * toTravelX / dx;

					System.err.printf(" - First increment X by %f, Y by %f.\n", toTravelX, toTravelY);

					while(true) {
						// TODO - move this block of code further

						if(getBlock((int)px, (int)py) != '.') {
							System.err.printf(" - Reached vertical wall at %f/%f, distance %f\n", px, py, distance);
							break;
						}

						if((int)py != (int)(py + toTravelY)) {
							System.err.printf(" - CROSSED HORIZONTAL BOUNDARY (%d/%d)\n", (int)px, (int)(py + toTravelY));

							double ratio = 0;

							if(toTravelY > 0) {
								ratio = (((int)(py + toTravelY)) - py) / toTravelY;
							} else {
								ratio = (((int)(py) - py)) / toTravelY;
							}

							System.err.printf(" - *** %f (%f / %d)\n", ratio, py, (int)(py));

							if(getBlock((int)px, (int)(py + toTravelY)) != '.') {
								distance += ratio * Math.hypot(toTravelX, toTravelY);
								System.err.printf(" - Reached horizontal wall at %f/%f (block %d/%d), distance %f\n", px + toTravelX * ratio, py + toTravelY * ratio, (int)px, (int)(py + toTravelY), distance);
								wallColor = 0xE0E0E0;
								break;
							}
						}

						System.err.printf("- %f/%f -> %f/%f\n", px, py, px + toTravelX, py + toTravelY);

						px += toTravelX;
						py += toTravelY;

						toTravelX = dx;
						toTravelY = dy;

						distance += Math.hypot(dx, dy);

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
			}

			// Perform fish-eye correction

			distance *= Math.cos(relangle);

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
