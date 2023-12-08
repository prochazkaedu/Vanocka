package eu.prochazkaml.vanocka;

public class RayCaster {
	public FrameBuffer fb;
	public final RayCasterMap map;
	
	public double playerX, playerY, playerAngle;
	public final double playerFOV;
	public final int vw, vh;
	public final boolean debugOutput;

	public RayCaster(FrameBuffer _fb, RayCasterMap _map, int _viewportWidth, int _viewportHeight, double _playerFOV, boolean _debugOutput) {
		fb = _fb;
		map = _map;
		vw = _viewportWidth;
		vh = _viewportHeight;
		debugOutput = _debugOutput;

		playerX = map.playerSpawnX;
		playerY = map.playerSpawnY;
		playerAngle = map.playerAngle;
		playerFOV = _playerFOV;
	}

	public RayCaster(FrameBuffer _fb, RayCasterMap _map, int _viewportWidth, int _viewportHeight, double _playerFOV) {
		this(_fb, _map, _viewportWidth, _viewportHeight, _playerFOV, false);
	}

	private char getBlock(int x, int y) {
		//System.out.printf("Read block %d/%d %f\n", x, y, playerX);
		if(x >= map.w || y >= map.h || x < 0 || y < 0) return '0';

		return map.map[y][x];
	}

	private char getBlockRelative(int x, int y, int rot) {
		int px = (int)playerX, py = (int)playerY, tmp;

		// Translate the requested world coordinates to the player's origin

		x -= px;
		y -= py;

		// Rotate the world coordinates around the player

		switch(rot) {
			case 0: // 0 deg
				// Do nothing
				break;

			case 1: // 90 deg
				tmp = y;

				y = x;
				x = -tmp;
				break;

			case 2: // 180 deg
				x *= -1;
				y *= -1;
				break;

			case 3: // 270 deg
				tmp = y;

				y = -x;
				x = tmp;
				break;
		}

		// Translate the rotated coordinates back to world coordinates

		x += px;
		y += py;

		return getBlock(x, y);
	}

	private void calculateRayDistance(RayCasterResult retval, double deltaX, double deltaY, int rot) {
		double posX = 0, posY = 0, oldPosX, oldPosY;
		double toTravelX = 0, toTravelY = 0;

		// TODO - rotate the player inside the square they are currently in

		retval.wallColor = 0xFFFFFF;
		retval.distance = 0;

		if(debugOutput) System.err.printf("- Incrementing X by %f, Y by %f.\n", deltaX, deltaY);

		posX = playerX;
		posY = playerY;

		toTravelX = Math.floor(playerX + 1) - playerX;
		toTravelY = deltaY * toTravelX / deltaX;

		if(debugOutput) System.err.printf(" - First increment X by %f, Y by %f.\n", toTravelX, toTravelY);

		while(true) {
			oldPosX = posX;
			oldPosY = posY;

			posX += toTravelX;
			posY += toTravelY;

			if(debugOutput) System.err.printf("- %f/%f + %f/%f = %f/%f\n",
				oldPosX, oldPosY,
				toTravelX, toTravelY,
				posX, posY);

			if((int)oldPosY != (int)posY) {
				if(debugOutput) System.err.printf(" - CROSSED HORIZONTAL BOUNDARY (%d/%d)\n", (int)oldPosX, (int)posY);

				double ratio = 0;

				if(toTravelY > 0) {
					ratio = (((int)posY) - oldPosY) / toTravelY;
				} else {
					ratio = (((int)oldPosY) - oldPosY) / toTravelY;
				}

				if(debugOutput) System.err.printf("   - ratio = %f (%f / %d)\n", ratio, oldPosY, (int)(oldPosY));

				if(getBlockRelative((int)oldPosX, (int)posY, rot) != '.') {
					if(debugOutput) System.err.printf(" - Reached horizontal wall at %f/%f (block %d/%d)\n",
						posX, posY, (int)oldPosX, (int)posY);
					
					posX = oldPosX + toTravelX * ratio;
					posY = oldPosY + toTravelY * ratio;
					
					if(rot % 2 == 0) retval.wallColor = 0xE0E0E0;
					break;
				}
			}

			if(getBlockRelative((int)posX, (int)posY, rot) != '.') {
				if(debugOutput) System.err.printf(" - Reached vertical wall at %f/%f (block %d/%d)\n",
					posX, posY, (int)posX, (int)posY);

				if(rot % 2 == 1) retval.wallColor = 0xE0E0E0;
				break;
			}

			toTravelX = deltaX;
			toTravelY = deltaY;
		}

		retval.distance = Math.hypot(playerX - posX, playerY - posY);

		if(debugOutput) System.err.printf(" *** RESULT DISTANCE = %f, COLOR = 0x%06X\n", retval.distance, retval.wallColor);
	}

	public void render() {
		RayCasterResult rayResult = new RayCasterResult();

//		for(int x = 38; x < 42; x++) {
		for(int x = 0; x < vw; x++) {
			double relangle = playerFOV * (double)x / (double)vw - playerFOV / 2;
			double angle = playerAngle + relangle;

			// Limit the angle to [-π/4; π/4], keep track of the quadrant

			int quadrant = 0;

			while(angle < Math.PI / 4) {
				angle += Math.PI / 2;
				quadrant--;
			}

			while(angle > Math.PI / 4) {
				angle -= Math.PI / 2;
				quadrant++;
			}

			// Force the quadrant integer to be non-negative (otherwise we would get negative array indices)

			while(quadrant < 0) quadrant += 4;

			double horizUnitDeltaY = Math.tan(angle);

			if(debugOutput) System.err.printf("%d: angle %f, quadrant %d (%d), Y delta %f\n", x, angle, quadrant, quadrant % 4, horizUnitDeltaY);

			calculateRayDistance(rayResult, 1, horizUnitDeltaY, quadrant % 4);

			// Perform fish-eye correction

			double distance = rayResult.distance * Math.cos(relangle);

			// Render this column of pixels

			this.drawColumn(x, (1-1.f / distance) * (double)(fb.h / 2), 0x808080, 0x404040, rayResult.wallColor);

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
