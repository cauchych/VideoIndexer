
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.DataLine.Info;

/**
 * 
 * <Replace this with a short description of the class.>
 * 
 * @author Giulio
 */
public class PlaySound {

    private InputStream waveStream;

    private final int EXTERNAL_BUFFER_SIZE = 524288; // 128Kb

    /**
     * CONSTRUCTOR
     */
    public PlaySound(InputStream waveStream) {
	this.waveStream = waveStream;
    }

    public void play() throws PlayWaveException {

		AudioInputStream audioInputStream = null;
		try {
		    audioInputStream = AudioSystem.getAudioInputStream(this.waveStream);
		} catch (UnsupportedAudioFileException e1) {
		    throw new PlayWaveException(e1);
		} catch (IOException e1) {
		    throw new PlayWaveException(e1);
		}
	
		// Obtain the information about the AudioInputStream
		AudioFormat audioFormat = audioInputStream.getFormat();
		Info info = new Info(SourceDataLine.class, audioFormat);
	
		// opens the audio channel
		SourceDataLine dataLine = null;
		try {
		    dataLine = (SourceDataLine) AudioSystem.getLine(info);
		    dataLine.open(audioFormat, this.EXTERNAL_BUFFER_SIZE);
		} catch (LineUnavailableException e1) {
		    throw new PlayWaveException(e1);
		}
	
		// Starts the music :P
		dataLine.start();
	
		int readBytes = 0;
		byte[] audioBuffer = new byte[this.EXTERNAL_BUFFER_SIZE];
		int count = 0;
		try {
		    while (readBytes != -1) {
				readBytes = audioInputStream.read(audioBuffer, 0, audioBuffer.length);
				if (readBytes >= 0){
				    dataLine.write(audioBuffer, 0, readBytes);
				}
				System.out.println("Iteration " + count);
				System.out.println("readBytes: " + readBytes + "\n");
				count++;
		    }
		} catch (IOException e1) {
		    throw new PlayWaveException(e1);
		} finally {
		    // plays what's left and and closes the audioChannel
		    dataLine.drain();
		    dataLine.close();
		}

    }
}
