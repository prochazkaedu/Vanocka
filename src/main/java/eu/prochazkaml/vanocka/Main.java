package eu.prochazkaml.vanocka;

import eu.prochazkaml.vanocka.scenes.JoystickSetup;
import eu.prochazkaml.vanocka.scenes.StoryIntro;
import eu.prochazkaml.vanocka.scenes.Maze;
import eu.prochazkaml.vanocka.scenes.StoryOutro;

import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);

		System.out.print("Vítejte v mé vánoční hře!\n\n");
		System.out.print("Tato hra potřebuje vědět, jak velký terminál máte. Minimální velikost je 80x24 znaků (= 80x48 pixelů).\n\n");
		
		int width = 80, height = 24;

		System.out.print("Zadejte šířku terminálu: ");

		try {
			width = sc.nextInt();
		} catch(Exception e) {
			System.out.println("Neplatný vstup!");
			System.exit(1);
		}

		if(width < 80) {
			System.out.println("Zadaná šířka je příliš malá, zvětšuji na 80 znaků.");
			width = 80;
		}

		System.out.print("Zadejte výšku terminálu: ");

		try {
			height = sc.nextInt();
		} catch(Exception e) {
			System.out.println("Neplatný vstup!");
			System.exit(1);
		}

		if(height < 24) {
			System.out.println("Zadaná výška je příliš malá, zvětšuji na 24 znaků.");
			height = 24;
		}

		sc.close();

		FrameBuffer fb = new FrameBuffer(width, height, 20);

		// ========== FOR TESTING PURPOSES ==========

		boolean debugOutput = false;
		boolean renderSingleFrame = false;

		if(debugOutput || renderSingleFrame) {
			Maze.run(fb, null, debugOutput, renderSingleFrame);
			return;
		}

		// ==========================================

		JoystickThread joystick = new JoystickThread();
		joystick.start();

		JoystickSetup.run(fb, joystick);

		MusicPlayer mp = new MusicPlayer("assets/jbells.mid");
		mp.start();

		StoryIntro.run(fb, joystick);

		Maze.run(fb, joystick, debugOutput, renderSingleFrame);

		StoryOutro.run(fb, joystick);

		mp.interrupt();
		joystick.interrupt();

		System.out.println();
		System.out.println("Děkujeme Vám za hraní této hry! ");

		System.exit(0);
	}
}
