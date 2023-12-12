package eu.prochazkaml.vanocka;
import java.io.*;

// https://www.kernel.org/doc/Documentation/input/joystick-api.txt

public class JoystickEvent {
	enum Type {
		BUTTON,
		AXIS
	}

	public long timestamp;
	public int value;
	public int type;
	public int number;

	public JoystickEvent(DataInputStream in) throws IOException {
		// Because Java's DataInputStream stuck on ancient mainframes and is BIG ENDIAN.

		timestamp = in.readUnsignedByte() | (in.readUnsignedByte() << 8) | (in.readUnsignedByte() << 16) | (in.readUnsignedByte() << 24);
		value = in.readUnsignedByte() | (in.readUnsignedByte() << 8);

		if(value >= 0x8000) {
			value -= 0x10000;
		}

		type = in.readUnsignedByte();
		number = in.readUnsignedByte();
	}
}
