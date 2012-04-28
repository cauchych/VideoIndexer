
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * plays a wave file using PlaySound class
 * 
 * @author Giulio
 */
public class PlayWaveFile {

    /**
     * <Replace this with one clearly defined responsibility this method does.>
     * 
     * @param args
     *            the name of the wave file to play
     */
    public static void main(String[] args) {

	// get the command line parameters
	/*
	 * if (args.length < 1) {
	    System.err.println("usage: java -jar PlayWaveFile.jar [filename]");
	    return;
	}
	String filename = args[0];
	*/
	String filename = "C:/Users/edeng/Documents/School/s10/576/project/vdo1/vdo1.wav";
	
	// opens the inputStream
	FileInputStream inputStream;
	try {
	    inputStream = new FileInputStream(filename);
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	    return;
	}

	// initializes the playSound Object
	PlaySound playSound = new PlaySound(inputStream);

	// plays the sound
	try {
	    playSound.play();
	} catch (PlayWaveException e) {
	    e.printStackTrace();
	    return;
	}
    }

}
