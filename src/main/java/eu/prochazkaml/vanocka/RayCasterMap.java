package eu.prochazkaml.vanocka;

public class RayCasterMap {
	public int w, h;
	public double playerSpawnX, playerSpawnY, playerAngle;
	public char[][] map;

	public RayCasterMap(int _w, int _h, double _playerSpawnX, double _playerSpawnY, double _playerAngle, char[][] _map) {
		w = _w;
		h = _h;
		map = _map;
		playerSpawnX = _playerSpawnX;
		playerSpawnY = _playerSpawnY;
		playerAngle = _playerAngle;
	}
}
