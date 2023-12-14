package eu.prochazkaml.vanocka;

public class RayCasterMap {
	public int w, h;
	public double playerSpawnX, playerSpawnY, playerAngle;
	public char[][] map;
	public RayCasterColorMap[] colorMap;

	public RayCasterMap(int _w, int _h, double _playerSpawnX, double _playerSpawnY, double _playerAngle, char[][] _map, RayCasterColorMap[] _colorMap) {
		w = _w;
		h = _h;
		map = _map;
		playerSpawnX = _playerSpawnX;
		playerSpawnY = _playerSpawnY;
		playerAngle = _playerAngle;
		colorMap = _colorMap;
	}
}
