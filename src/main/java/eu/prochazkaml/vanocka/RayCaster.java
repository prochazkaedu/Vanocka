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

	private char getBlockRelative(int x, int y, boolean swapXY, boolean mainAxisNegative) {
		if(!swapXY) {
			return getBlock(x, y);
		} else {
			return getBlock(y, x);
		}
	}

	private void calculateRayDistance(RayCasterResult retval, double dx, double dy, boolean swapXY, boolean mainAxisNegative) {
		double posPrimary = 0, posSecondary = 0, oldPosPrimary, oldPosSecondary;
		double toTravelPrimary = 0, toTravelSecondary = 0;
		double deltaPrimary = 0, deltaSecondary = 0;

		retval.wallColor = 0xFFFFFF;
		retval.distance = 0;

		if(debugOutput) System.err.printf("- Incrementing X by %f, Y by %f.\n", dx, dy);

		if(!swapXY) {
			if(debugOutput) System.err.printf(" - RAY GOING %s: X = primary, Y = secondary\n", mainAxisNegative ? "LEFT" : "RIGHT");

			posPrimary = playerX;
			posSecondary = playerY;

			deltaPrimary = dx;
			deltaSecondary = dy;

			toTravelPrimary = Math.floor(playerX + 1) - playerX;
			toTravelSecondary = deltaSecondary * toTravelPrimary / deltaPrimary;
		} else {
			if(debugOutput) System.err.printf(" - RAY GOING %s: X = secondary, Y = primary\n", mainAxisNegative ? "UP" : "DOWN");

			posPrimary = playerY;
			posSecondary = playerX;

			deltaPrimary = dy;
			deltaSecondary = dx;

			toTravelPrimary = Math.floor(playerY + 1) - playerY;
			toTravelSecondary = deltaSecondary * toTravelPrimary / deltaPrimary;
		}

		if(debugOutput) System.err.printf(" - First increment primary by %f, secondary by %f.\n", toTravelPrimary, toTravelSecondary);

		while(true) {
			oldPosPrimary = posPrimary;
			oldPosSecondary = posSecondary;

			posPrimary += toTravelPrimary;
			posSecondary += toTravelSecondary;

			if(debugOutput) System.err.printf("- %f/%f + %f/%f = %f/%f\n",
				oldPosPrimary, oldPosSecondary,
				toTravelPrimary, toTravelSecondary,
				posPrimary, posSecondary);

			if((int)oldPosSecondary != (int)posSecondary) {
				if(debugOutput) System.err.printf(" - CROSSED SECONDARY BOUNDARY (%d/%d)\n", (int)oldPosPrimary, (int)posSecondary);

				double ratio = 0;

				if(toTravelSecondary > 0) {
					ratio = (((int)posSecondary) - oldPosSecondary) / toTravelSecondary;
				} else {
					ratio = (((int)oldPosSecondary) - oldPosSecondary) / toTravelSecondary;
				}

				if(debugOutput) System.err.printf("   - ratio = %f (%f / %d)\n", ratio, oldPosSecondary, (int)(oldPosSecondary));

				if(getBlockRelative((int)oldPosPrimary, (int)posSecondary, swapXY, mainAxisNegative) != '.') {
					if(debugOutput) System.err.printf(" - Reached secondary wall at %f/%f (block %d/%d)\n",
						posPrimary, posSecondary, (int)oldPosPrimary, (int)posSecondary);
					
					posPrimary = oldPosPrimary + toTravelPrimary * ratio;
					posSecondary = oldPosSecondary + toTravelSecondary * ratio;
					
					if(!swapXY) retval.wallColor = 0xE0E0E0;
					
					break;
				}
			}

			if(getBlockRelative((int)posPrimary, (int)posSecondary, swapXY, mainAxisNegative) != '.') {
				if(debugOutput) System.err.printf(" - Reached primary wall at %f/%f (block %d/%d)\n",
					posPrimary, posSecondary, (int)posPrimary, (int)posSecondary);

				if(swapXY) retval.wallColor = 0xE0E0E0;

				break;
			}

			toTravelPrimary = deltaPrimary;
			toTravelSecondary = deltaSecondary;
		}

		if(!swapXY) {
			retval.distance = Math.hypot(playerX - posPrimary, playerY - posSecondary);
		} else {
			retval.distance = Math.hypot(playerX - posSecondary, playerY - posPrimary);
		}

		if(debugOutput) System.err.printf(" *** RESULT DISTANCE = %f, COLOR = 0x%06X\n", retval.distance, retval.wallColor);
	}

	public void render() {
		RayCasterResult rayResult = new RayCasterResult();

//		for(int x = 38; x < 42; x++) {
		for(int x = 0; x < vw; x++) {
			double relangle = playerFOV * (double)x / (double)vw - playerFOV / 2;
			double angle = playerAngle + relangle;

			if(angle == 0) angle += Double.MIN_VALUE; // Prevents NaN
			double tmptan = Math.tan(angle);

			double horizUnitDeltaY = tmptan * Math.signum(Math.cos(angle));
			double vertUnitDeltaX = 1 / tmptan * Math.signum(Math.sin(angle));

			if(debugOutput) System.err.printf("%d: %f %f/%f\n", x, angle, vertUnitDeltaX, horizUnitDeltaY);

			if(Math.abs(horizUnitDeltaY) < Math.abs(vertUnitDeltaX)) {
				// The fired ray will mainly go left/right

				double dx = Math.signum(vertUnitDeltaX), dy = horizUnitDeltaY;

				// dx = ±1
				// dy = anywhere from -1 to 1

				// TODO - finish the other quadrants

				if(dx > 0) { // Right
					calculateRayDistance(rayResult, dx, dy, false, false);
				} else if(dx < 0) { // Left
					//calculateRayDistance(rayResult, dx, dy, false, true);
				}
			} else {
				// The fired ray will mainly go up/down

				double dx = vertUnitDeltaX, dy = Math.signum(horizUnitDeltaY);

				// dx = anywhere from -1 to 1
				// dy = ±1

				if(dy > 0) { // Down
					calculateRayDistance(rayResult, dx, dy, true, false);
				} else if(dy < 0) {
					//calculateRayDistance(rayResult, dx, dy, true, true);
				}
			}

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
