package eu.prochazkaml.vanocka;

public class RayCaster {
	public FrameBuffer fb;
	public final RayCasterMap map;
	
	public double playerX, playerY, playerAngle;
	public final double playerFOV;
	public final int vw, vh;
	public final boolean debugOutput;
	public final double playerRadius;

	public RayCaster(FrameBuffer _fb, RayCasterMap _map, int _viewportWidth, int _viewportHeight, double _playerFOV, double _playerRadius, boolean _debugOutput) {
		fb = _fb;
		map = _map;
		vw = _viewportWidth;
		vh = _viewportHeight;
		debugOutput = _debugOutput;

		playerX = map.playerSpawnX;
		playerY = map.playerSpawnY;
		playerAngle = map.playerAngle;
		playerFOV = _playerFOV;
		playerRadius = _playerRadius;
	}

	public RayCaster(FrameBuffer _fb, RayCasterMap _map, int _viewportWidth, int _viewportHeight, double _playerFOV, double _playerRadius) {
		this(_fb, _map, _viewportWidth, _viewportHeight, _playerFOV, _playerRadius, false);
	}

	private char getBlock(int x, int y) {
		//System.out.printf("Read block %d/%d %f\n", x, y, playerX);
		if(x >= map.w || y >= map.h || x < 0 || y < 0) return '.';

		return map.map[y][x];
	}

	private int mapBlockToColor(char block, boolean primary) {
		for(int i = 0; i < map.colorMap.length; i++) {
			if(block == map.colorMap[i].id)
				return primary ? map.colorMap[i].color1 : map.colorMap[i].color2;
		}

		return 0xFF0000;
	}

	private void calculateRayDistance(RayCasterResult retval, double angle) {
		// https://en.wikipedia.org/wiki/Line%E2%80%93line_intersection

		double x1 = playerX;
		double y1 = playerY;
		double x2 = playerX + Math.cos(angle) * map.w * 2;
		double y2 = playerY + Math.sin(angle) * map.h * 2;
		double t, u, dx, dy, tmpdist;

		char block;

		retval.wallColor = map.colorFloor;
		retval.distance = Double.POSITIVE_INFINITY;

		// Find grid intersections in the X direction

		for(double x3 = 0; x3 <= map.w; x3++) {
			t = ((x1 - x3) * map.h) / ((x1 - x2) * map.h);
			u = (y1 * (x1 - x2) - (x1 - x3) * (y1 - y2)) / ((x1 - x2) * map.h);

			if(t >= 0 && t <= 1 && u >= 0 && u <= 1) {
				// Intersection valid, calculate point and check if block is there

				dx = t * (x2 - x1);
				dy = t * (y2 - y1);

				tmpdist = Math.hypot(dx, dy);

				if(tmpdist > retval.distance) continue;

				if((block = getBlock((int)Math.round(x1 + dx) - ((dx < 0) ? 1 : 0), (int)(y1 + dy))) != '.') {
					retval.distance = tmpdist;
					retval.wallColor = mapBlockToColor(block, true);
				}
			}
		}

		// Find grid intersections in the Y direction

		for(double y3 = 0; y3 <= map.h; y3++) {
			t = ((y1 - y3) * map.w) / ((y1 - y2) * map.w);
			u = (x1 * (y1 - y2) - (y1 - y3) * (x1 - x2)) / ((y1 - y2) * map.w);

			if(t >= 0 && t <= 1 && u >= 0 && u <= 1) {
				// Intersection valid, calculate point and check if block is there

				dx = t * (x2 - x1);
				dy = t * (y2 - y1);

				tmpdist = Math.hypot(dx, dy);

				if(tmpdist > retval.distance) continue;

				if((block = getBlock((int)(x1 + dx), (int)Math.round(y1 + dy) - ((dy < 0) ? 1 : 0))) != '.') {
					retval.distance = tmpdist;
					retval.wallColor = mapBlockToColor(block, false);
				}
			}
		}
	}

	public void render() {
		RayCasterResult rayResult = new RayCasterResult();

//		for(int x = 38; x < 42; x++) {
		for(int x = 0; x < vw; x++) {
			double relangle = playerFOV * (double)x / (double)vw - playerFOV / 2;
			double angle = playerAngle + relangle;

			if(debugOutput) System.err.printf("%d: angle %f (rel %f)\n", x, angle, relangle);

			calculateRayDistance(rayResult, angle);

			// Perform fish-eye correction

			double distance = rayResult.distance * Math.cos(relangle);

			// Render this column of pixels

			this.drawColumn(x, (1-1.f / distance) * (double)(fb.h / 2), map.colorCeiling, map.colorFloor, rayResult.wallColor);

			// System.err.printf("%d: %.02f %f\n", x, angle, distance);
		}
	}

	private boolean inRightSideBorder() {
		return playerX - Math.floor(playerX) > (1 - playerRadius);
	}

	private boolean inLeftSideBorder() {
		return playerX - Math.floor(playerX) < playerRadius;
	}

	private boolean inUpperBorder() {
		return playerY - Math.floor(playerY) < playerRadius;
	}

	private boolean inLowerBorder() {
		return playerY - Math.floor(playerY) > (1 - playerRadius);
	}

	private boolean isNearbyWall(int dx, int dy) {
		return getBlock((int)playerX + dx, (int)playerY + dy) != '.';
	}

	public void handleMovement(JoystickThread joystick) {
		// Move a specific amount in the given direction

		playerAngle += joystick.rot / 30.f;

		double dx = joystick.xmove / 25.f, dy = -joystick.ymove / 25.f;

		playerX += Math.cos(playerAngle) * dy - Math.sin(playerAngle) * dx;
		playerY += Math.sin(playerAngle) * dy + Math.cos(playerAngle) * dx;

		// Handle collision detection

		boolean clipUpper = false, clipLower = false, clipLeftSide = false, clipRightSide = false;

		if(inUpperBorder() && inRightSideBorder() && isNearbyWall(1, -1)) {
			if((Math.ceil(playerY) - playerY) < (playerX - Math.floor(playerX)))
				clipUpper = true;
			else
				clipRightSide = true;
		}

		if(inUpperBorder() && inLeftSideBorder() && isNearbyWall(-1, -1)) {
			if((Math.ceil(playerY) - playerY) < (Math.ceil(playerX) - playerX))
				clipUpper = true;
			else
				clipLeftSide = true;
		}

		if(inLowerBorder() && inRightSideBorder() && isNearbyWall(1, 1)) {
			if((playerY - Math.floor(playerY)) < (playerX - Math.floor(playerX)))
				clipLower = true;
			else
				clipRightSide = true;
		}

		if(inLowerBorder() && inLeftSideBorder() && isNearbyWall(-1, 1)) {
			if((playerY - Math.floor(playerY)) < (Math.ceil(playerX) - playerX))
				clipLower = true;
			else
				clipLeftSide = true;			
		}

		if(clipRightSide || (inRightSideBorder() && isNearbyWall(1, 0))) {
			playerX = Math.ceil(playerX) - playerRadius;
		}

		if(clipLeftSide || (inLeftSideBorder() && isNearbyWall(-1, 0))) {
			playerX = Math.floor(playerX) + playerRadius;
		}

		if(clipUpper || (inUpperBorder() && isNearbyWall(0, -1))) {
			playerY = Math.floor(playerY) + playerRadius;
		}

		if(clipLower || (inLowerBorder() && isNearbyWall(0, 1))) {
			playerY = Math.ceil(playerY) - playerRadius;
		}
	}

	private void drawColumn(int x, double val, int colourCeiling, int colourFloor, int colourWall) {
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

	private int interpolateColours(int col1, int col2, double frac) {
		if(frac < 0.f) frac = 0.f;

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
