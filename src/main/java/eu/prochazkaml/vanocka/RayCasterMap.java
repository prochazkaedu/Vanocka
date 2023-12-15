package eu.prochazkaml.vanocka;

public class RayCasterMap {
	public int w, h;
	public double playerSpawnX, playerSpawnY, playerAngle;
	public char[][] map;
	public RayCasterColorMap[] colorMap;
	public int colorCeiling, colorFloor;

	public RayCasterMap(int _w, int _h, double _playerSpawnX, double _playerSpawnY, double _playerAngle, char[][] _map, int _colorCeiling, int _colorFloor, RayCasterColorMap[] _colorMap) {
		w = _w;
		h = _h;
		map = _map;
		playerSpawnX = _playerSpawnX;
		playerSpawnY = _playerSpawnY;
		playerAngle = _playerAngle;
		colorMap = _colorMap;
		colorCeiling = _colorCeiling;
		colorFloor = _colorFloor;
	}
}
