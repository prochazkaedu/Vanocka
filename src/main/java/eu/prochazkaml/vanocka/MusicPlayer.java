package eu.prochazkaml.vanocka;

import javax.sound.midi.*;
import java.io.*;

// thx, https://gist.github.com/indy/360540

public class MusicPlayer extends Thread {
	File midiFile;
	boolean allowplayback = true;

	public MusicPlayer(String file) {
		super();

		midiFile = new File(file);
        
		if(!midiFile.exists() || midiFile.isDirectory() || !midiFile.canRead()) {
			allowplayback = false;
        }
	}

	public void run() {
		if(!allowplayback) return;

		try {
            Sequencer sequencer = MidiSystem.getSequencer();
            sequencer.setSequence(MidiSystem.getSequence(midiFile));
            sequencer.open();
            sequencer.start();
            sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);

            while(!Thread.currentThread().isInterrupted()) {
                if(sequencer.isRunning()) {
                    try {
                        Thread.sleep(100); // Check every second
                    } catch(InterruptedException ignore) {
                        break;
                    }
                } else {
                    break;
                }
            }

			sequencer.stop();
            sequencer.close();
        } catch(Exception e) {
			e.printStackTrace();
		} 
	}
}
