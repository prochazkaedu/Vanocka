package eu.prochazkaml.vanocka.scenes;

import eu.prochazkaml.vanocka.FrameBuffer;
import eu.prochazkaml.vanocka.JoystickThread;
import eu.prochazkaml.vanocka.ScreenFader;
import eu.prochazkaml.vanocka.MusicPlayer;

public class StoryIntro {
	private static String[] storyText = {
		"Potkali jste sněhuláka. Sněhulák je ale smutný.",
		"",
		"Proč? Okradli ho totiž. Přišel mrkvový bandit",
		"a když sněhulák spal, ukradl mu mrkev.",
		"",
		"Sněhulák tě snažně prosí, ať mu tu jeho mrkev",
		"najdeš. Banditovo mrkvové doupě se nachází",
		"uprostřed bludiště, před kterým právě stojíš.",
		"",
		"Pokud sněhulákovi pomůžeš a najdeš mu mrkev",
		"bude šťastný a možná ti dá dárek. :)",
		"",
		"Stiskem libovolného tlačitka na ovladači",
		"se přesunete do bludiště..."
	};

	private static int[] snowmanPixels = {
		0x00, 0x00, 0x00, 0xff, 0x01, 0x00, 0x01, 0xff, 0x80, 0x81, 0x81, 0xff, 0x80, 0x80, 0x80, 0xff, 0x81, 0x80, 0x80, 0xff, 0x80, 0x80, 0x80, 0xff, 0x81, 0x80, 0x81, 0xff, 0x80, 0x80, 0x80, 0xff, 0x80, 0x80, 0x80, 0xff, 0x00, 0x00, 0x00, 0xff, 0x00, 0x01, 0x01, 0xff, 
		0x01, 0x00, 0x00, 0xff, 0x01, 0x01, 0x01, 0xff, 0x81, 0x80, 0x80, 0xff, 0x81, 0x81, 0x80, 0xff, 0x80, 0x81, 0x80, 0xff, 0x81, 0x80, 0x80, 0xff, 0x80, 0x80, 0x80, 0xff, 0x81, 0x81, 0x80, 0xff, 0x81, 0x81, 0x80, 0xff, 0x00, 0x01, 0x00, 0xff, 0x01, 0x00, 0x01, 0xff, 
		0x00, 0x00, 0x00, 0xff, 0x80, 0x80, 0x80, 0xff, 0x81, 0x80, 0x80, 0xff, 0x80, 0x80, 0x80, 0xff, 0x80, 0x80, 0x80, 0xff, 0x81, 0x80, 0x81, 0xff, 0x81, 0x80, 0x81, 0xff, 0x81, 0x80, 0x80, 0xff, 0x80, 0x80, 0x80, 0xff, 0x81, 0x80, 0x81, 0xff, 0x00, 0x00, 0x00, 0xff, 
		0x80, 0x80, 0x80, 0xff, 0x00, 0x00, 0x01, 0xff, 0x80, 0x80, 0x80, 0xff, 0x80, 0x80, 0x80, 0xff, 0x80, 0x80, 0x80, 0xff, 0x80, 0x80, 0x80, 0xff, 0x81, 0x81, 0x80, 0xff, 0x80, 0x81, 0x81, 0xff, 0x81, 0x80, 0x80, 0xff, 0x01, 0x00, 0x01, 0xff, 0x80, 0x80, 0x80, 0xff, 
		0x00, 0x01, 0x00, 0xff, 0x80, 0x81, 0x80, 0xff, 0x80, 0x80, 0x80, 0xff, 0x80, 0x81, 0x80, 0xff, 0x80, 0x80, 0x80, 0xff, 0x80, 0x80, 0x80, 0xff, 0x80, 0x80, 0x80, 0xff, 0x81, 0x80, 0x81, 0xff, 0x80, 0x80, 0x80, 0xff, 0x80, 0x80, 0x81, 0xff, 0x00, 0x00, 0x01, 0xff, 
		0x01, 0x00, 0x00, 0xff, 0x01, 0x01, 0x00, 0xff, 0x81, 0x81, 0x80, 0xff, 0x80, 0x81, 0x80, 0xff, 0x80, 0x80, 0x81, 0xff, 0x81, 0x81, 0x80, 0xff, 0x80, 0x80, 0x80, 0xff, 0x80, 0x80, 0x81, 0xff, 0x81, 0x80, 0x81, 0xff, 0x00, 0x00, 0x00, 0xff, 0x00, 0x01, 0x00, 0xff, 
		0x00, 0x01, 0x00, 0xff, 0x00, 0x00, 0x00, 0xff, 0x14, 0x14, 0x15, 0xff, 0xce, 0xce, 0xce, 0xff, 0xff, 0xff, 0xfe, 0xff, 0xff, 0xff, 0xff, 0xff, 0xfe, 0xfe, 0xff, 0xff, 0xeb, 0xeb, 0xeb, 0xff, 0x36, 0x37, 0x37, 0xff, 0x00, 0x00, 0x01, 0xff, 0x00, 0x01, 0x00, 0xff, 
		0x00, 0x01, 0x01, 0xff, 0x00, 0x01, 0x00, 0xff, 0xae, 0xae, 0xae, 0xff, 0xff, 0xfe, 0xff, 0xff, 0xff, 0xff, 0xfe, 0xff, 0xff, 0xff, 0xfe, 0xff, 0xff, 0xff, 0xfe, 0xff, 0xff, 0xff, 0xfe, 0xff, 0xe3, 0xe3, 0xe3, 0xff, 0x0b, 0x0b, 0x0b, 0xff, 0x00, 0x01, 0x00, 0xff, 
		0x00, 0x00, 0x00, 0xff, 0x21, 0x21, 0x20, 0xff, 0xfe, 0xff, 0xfe, 0xff, 0xff, 0xff, 0xff, 0xff, 0x00, 0x00, 0x00, 0xff, 0xfe, 0xff, 0xff, 0xff, 0x01, 0x00, 0x00, 0xff, 0xfe, 0xff, 0xff, 0xff, 0xfe, 0xff, 0xff, 0xff, 0x60, 0x60, 0x60, 0xff, 0x01, 0x01, 0x00, 0xff, 
		0x00, 0x00, 0x00, 0xff, 0x55, 0x54, 0x55, 0xff, 0xff, 0xfe, 0xff, 0xff, 0xff, 0xfe, 0xfe, 0xff, 0xff, 0xff, 0xff, 0xff, 0xfe, 0xfe, 0xff, 0xff, 0xff, 0xfe, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0x95, 0x95, 0x95, 0xff, 0x00, 0x00, 0x00, 0xff, 
		0x00, 0x00, 0x00, 0xff, 0x5a, 0x5a, 0x5a, 0xff, 0xfe, 0xff, 0xff, 0xff, 0xfe, 0xfe, 0xff, 0xff, 0xfe, 0xfe, 0xff, 0xff, 0x00, 0x00, 0x00, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xfe, 0xff, 0xff, 0xff, 0xff, 0xff, 0x99, 0x99, 0x99, 0xff, 0x00, 0x00, 0x00, 0xff, 
		0x00, 0x00, 0x01, 0xff, 0x2e, 0x2e, 0x2e, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0x00, 0x00, 0x00, 0xff, 0xff, 0xff, 0xfe, 0xff, 0x00, 0x01, 0x00, 0xff, 0xfe, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0x6f, 0x6e, 0x6e, 0xff, 0x00, 0x01, 0x00, 0xff, 
		0x00, 0x00, 0x00, 0xff, 0x00, 0x01, 0x00, 0xff, 0xc9, 0xc9, 0xc9, 0xff, 0xfe, 0xff, 0xfe, 0xff, 0xff, 0xff, 0xfe, 0xff, 0xff, 0xff, 0xfe, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xfe, 0xff, 0xf2, 0xf2, 0xf3, 0xff, 0x16, 0x16, 0x16, 0xff, 0x01, 0x00, 0x01, 0xff, 
		0x00, 0x00, 0x01, 0xff, 0x13, 0x12, 0x13, 0xff, 0xbe, 0xbe, 0xbe, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xfe, 0xff, 0xff, 0xff, 0xfe, 0xff, 0xff, 0xff, 0xff, 0xfe, 0xff, 0xfe, 0xff, 0xff, 0xff, 0xcc, 0xcc, 0xcc, 0xff, 0x13, 0x13, 0x12, 0xff, 0x01, 0x00, 0x01, 0xff, 
		0x09, 0x09, 0x08, 0xff, 0xce, 0xce, 0xce, 0xff, 0xff, 0xfe, 0xfe, 0xff, 0xfe, 0xff, 0xff, 0xff, 0xff, 0xfe, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xfe, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xfe, 0xff, 0xfe, 0xff, 0xce, 0xcf, 0xce, 0xff, 0x09, 0x09, 0x08, 0xff, 
		0x7b, 0x7b, 0x7b, 0xff, 0xff, 0xff, 0xfe, 0xff, 0xff, 0xff, 0xfe, 0xff, 0xfe, 0xff, 0xfe, 0xff, 0xff, 0xfe, 0xff, 0xff, 0x00, 0x01, 0x00, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xfe, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xfe, 0xff, 0xff, 0x7a, 0x7b, 0x7a, 0xff, 
		0xd7, 0xd7, 0xd7, 0xff, 0xfe, 0xfe, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xfe, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xd7, 0xd7, 0xd7, 0xff, 
		0xfb, 0xfb, 0xfa, 0xff, 0xfe, 0xff, 0xff, 0xff, 0xfe, 0xfe, 0xff, 0xff, 0xff, 0xfe, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0x00, 0x01, 0x00, 0xff, 0xff, 0xff, 0xfe, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xfe, 0xff, 0xff, 0xff, 0xfe, 0xff, 0xff, 0xfc, 0xfc, 0xfd, 0xff, 
		0xed, 0xed, 0xec, 0xff, 0xfe, 0xfe, 0xff, 0xff, 0xfe, 0xff, 0xfe, 0xff, 0xff, 0xff, 0xff, 0xff, 0xfe, 0xfe, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xfe, 0xff, 0xff, 0xff, 0xfe, 0xfe, 0xff, 0xff, 0xff, 0xfe, 0xff, 0xff, 0xfe, 0xff, 0xff, 0xed, 0xed, 0xec, 0xff, 
		0xab, 0xab, 0xab, 0xff, 0xff, 0xfe, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xfe, 0xff, 0x00, 0x00, 0x00, 0xff, 0xfe, 0xfe, 0xfe, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xfe, 0xff, 0xfe, 0xff, 0xff, 0xff, 0xab, 0xab, 0xab, 0xff, 
		0x2e, 0x2f, 0x2f, 0xff, 0xf8, 0xf9, 0xf9, 0xff, 0xff, 0xff, 0xfe, 0xff, 0xff, 0xff, 0xff, 0xff, 0xfe, 0xff, 0xff, 0xff, 0xfe, 0xff, 0xff, 0xff, 0xff, 0xfe, 0xff, 0xff, 0xfe, 0xfe, 0xff, 0xff, 0xfe, 0xff, 0xff, 0xff, 0xf9, 0xf9, 0xf8, 0xff, 0x2f, 0x2f, 0x2f, 0xff, 
		0x01, 0x01, 0x01, 0xff, 0x58, 0x58, 0x58, 0xff, 0xf9, 0xf8, 0xf9, 0xff, 0xfe, 0xfe, 0xfe, 0xff, 0xff, 0xfe, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xfe, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xf8, 0xf9, 0xf8, 0xff, 0x58, 0x59, 0x58, 0xff, 0x00, 0x00, 0x00, 0xff, 
		0x01, 0x00, 0x00, 0xff, 0x00, 0x01, 0x00, 0xff, 0x29, 0x29, 0x29, 0xff, 0xa1, 0xa1, 0xa1, 0xff, 0xe7, 0xe7, 0xe7, 0xff, 0xfe, 0xff, 0xfe, 0xff, 0xe7, 0xe7, 0xe7, 0xff, 0xa1, 0xa1, 0xa1, 0xff, 0x29, 0x29, 0x29, 0xff, 0x01, 0x00, 0x01, 0xff, 0x00, 0x00, 0x00, 0xff, 	  
	};

	public static void run(FrameBuffer fb, JoystickThread joystick) {
		joystick.buttonsPressed = 0;

		ScreenFader fader = new ScreenFader(fb);

		MusicPlayer mp = new MusicPlayer("assets/jbells.mid");
		mp.start();

		while(joystick.buttonsPressed == 0) {
			fb.removeText();

			for(int x = 0; x < fb.w; x++) {
				for(int y = 0; y < fb.h; y++) {
					fb.pixels[x][y] = 0x000000;
				}
			}
	
			for(int x = 0; x < 11; x++) {
				for(int y = 0; y < 23; y++) {
					fb.pixels[fb.w - 12 + x][(fb.h - 23) / 2 + y] =
						(snowmanPixels[(x + y * 11) * 4 + 0] << 16) +
						(snowmanPixels[(x + y * 11) * 4 + 1] << 8) +
						(snowmanPixels[(x + y * 11) * 4 + 2] << 0);
				}
			}

			fb.textBackgroundColor = 0x000000;
			fb.textForegroundColor = 0xFFFFFF;
	
			for(int i = 0; i < storyText.length; i++) {
				fb.textCursorX = 1;
				fb.textCursorY = (fb.physicalHeight - storyText.length) / 2 + i;
				fb.writeText(storyText[i]);
			}
	
			fader.process();

			fb.updateLimited();
		}

		mp.interrupt();
		fb.removeText();
	}
}
