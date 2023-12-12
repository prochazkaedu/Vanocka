package eu.prochazkaml.vanocka;
import java.io.*;

public class JoystickThread extends Thread {
	public boolean joystickActive = false;
	
	public double xmove = 0, ymove = 0, rot = 0;

	/*
	 * -1 = setup has finished
	 * 0 = setup for forward movement
	 * 1 = setup for backward movement
	 * 2 = setup for left strafe movement
	 * 3 = setup for right strafe movement
	 * 4 = setup for rotating to the left
	 * 5 = setup for rotating to the right
	 * 
	 * Why don't I use an enum for this? Because I cannot easily increment an enum in Java (unlike C).
	 * And Java does not support preprocessor defines. Too bad.
	 */

	public int setupStep = 0;
	public boolean setupStepRunning = false;

	private SetupData[] setupData;

	private DataInputStream in;

	public JoystickThread() {
		super();
		
		setupData = new SetupData[6];

		for(int i = 0; i < 6; i++) {
			setupData[i] = new SetupData();
		}

		try {
			in = new DataInputStream(new BufferedInputStream(new FileInputStream("/dev/input/js0")));
		} catch(FileNotFoundException e) {
			System.out.println("Ovladač nenalezen.");
		}

		joystickActive = true;
	}
	
	public void run() {
		while(true) {
			JoystickEvent ev;

			try {
				ev = new JoystickEvent(in);
			} catch (IOException e) {
				try { in.close(); } catch (IOException e1) {}
				
				System.out.println("Chyba komunikace s ovladačem.");
				
				joystickActive = false;
				
				return;
			}

			if(Thread.currentThread().isInterrupted()) {
				return;
			}
			
			if(setupStepRunning) {
				// Joystick setup is running

//				System.out.printf("Setup step %d (type %d, num %d, val %d)\n", setupStep, ev.type, ev.number, ev.value);

				switch(ev.type) {
					case 1: // Button
						if(ev.value == 1) {
							setupData[setupStep].type = 0;
							setupData[setupStep].id = ev.number;
							setupStepRunning = false;
						}

						break;

					case 2: // Axis
						if(ev.value < -5000) { // A generous deadzone
							setupData[setupStep].type = 1;
							setupData[setupStep].id = ev.number;
							setupStepRunning = false;
						} else if(ev.value > 5000) {
							setupData[setupStep].type = 2;
							setupData[setupStep].id = ev.number;
							setupStepRunning = false;
						}

						break;
				}
			} else if(setupStep == -1) {
				// Parse joystick event

				for(int i = 0; i < 6; i++) {
					if(setupData[i].type == 0) {
						// Button

						if(ev.type != 1) continue;
						if(ev.number != setupData[i].id) continue;
					} else {
						// Axis

						if(ev.type != 2) continue;
						if(ev.number != setupData[i].id) continue;
					}

					// Found the correct button mapping, calculate the value

					double val = 0;

					switch(setupData[i].type) {
						case 0:
							val = (ev.value != 0) ? 1.f : 0.f;
							break;

						case 1:
							val = -(double)(ev.value) / 32768.f;
							break;

						case 2:
							val = (double)(ev.value) / 32768.f;
							break;
					}

					// Found the correct button mapping, execute the command

					switch(i) {
						case 0: // Move forward
							ymove = -val;
							break;

						case 1: // Move backward
							ymove = val;
							break;

						case 2: // Strafe left
							xmove = -val;
							break;

						case 3: // Strafe right
							xmove = val;
							break;

						case 4: // Rotate left
							rot = -val;
							break;

						case 5: // Rotate right
							rot = val;
							break;
					}

					break;
				}
			}

//			System.out.printf("%d %d %d %d\n", ev.timestamp, ev.value, ev.type, ev.number);
		}
	}
}

class SetupData {
	int type; // 0 = button, 1 = axis left, 2 = axis right
	int id; // button id or axis id, depends on the type
}
