import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;


public class VideoIndexer {
	
	private final int videoCount = 1;
	public enum VideoStatus {PLAYING, PAUSED, STOPPED, SEARCHING, START;};
	BufferedImage vdo[][] = new BufferedImage[videoCount][720];
	MyPanel imgPanel = new MyPanel();
	int videoFrame;
	ScheduledThreadPoolExecutor videoTimer;
	ScheduledThreadPoolExecutor audioTimer;
	ScheduledFuture audioStopper;
	JButton playButton, pauseButton, stopButton, searchButton;
	MyListener listener;
	VideoStatus status = VideoStatus.START;
    int height = 288;
    int width = 352;
    int numFrames = 720;

    AudioInputStream audioInputStream;
    InputStream waveStream;
    Info info;
    AudioFormat audioFormat;
    SourceDataLine dataLine;
    
	int readBytes = 0;
	byte[] audioBuffer; // = new byte[EXTERNAL_BUFFER_SIZE];;
	int buffersize;
	int currentVideo = 0;
	File audio;
	
	public static void main(String[] args){
			VideoIndexer vi = new VideoIndexer();		
	}
	
	public VideoIndexer(){


		try{
			File file = new File("C:/Users/Cauchy/Documents/CSCI576/Project/vdo4/vdo4.rgb"); // TODO change this path to your own
			audio = new File("C:/Users/Cauchy/Documents/CSCI576/project/vdo4/vdo4.wav"); // TODO change this path to you own

			waveStream = new FileInputStream(audio);
			int audiolen = (int) audio.length();
			System.out.println("audiolen: " + audiolen);
			buffersize = (int) Math.round((double) audiolen * 42.0 / 30000.0);
			System.out.println("buffersize: " + buffersize);
			audioBuffer = new byte[buffersize];
		    InputStream is = new FileInputStream(file);
		    long len = file.length();
		    byte[] bytes = new byte[(int)len];
		    
		    
		    int offset = 0;
	        int numRead = 0;
	        
	        while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
	            offset += numRead;
	        }
	        int ind;
	    	int maxInd = height * width;
	    	for(int i = 0; i < numFrames; i++){
	    		ind = maxInd * i * 3;
	    		//System.out.println("ind in outer loop: " + ind);
	    		vdo[currentVideo][i] = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
				for(int y = 0; y < height; y++){
			
					for(int x = 0; x < width; x++){
				 
						byte a = 0;
						byte r = bytes[ind];
						byte g = bytes[ind+height*width];
						byte b = bytes[ind+height*width*2]; 
						
						int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
						//int pix = ((a << 24) + (r << 16) + (g << 8) + b);
						//System.out.println("x: " + x + "; y: "+ y + "; pix " + pix + "; ind: " + ind);
						vdo[currentVideo][i].setRGB(x,y,pix);
						ind++;
					}
				}

				ind = (ind * 3) / (i + 1);
	    	}
		    
		}catch(Exception e){
			System.out.println(e);
		}
		
		
		listener = new MyListener();
		
		JFrame frame = new JFrame();
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	
	    JPanel container = new JPanel();
	    container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

	    JScrollPane scrollContainer = new JScrollPane(container);
	    
	    JPanel topPanel = new JPanel();
	    topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
	    JPanel bottomPanel = new JPanel();
	    bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
	    bottomPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
	    
	    //imgPanel.setBounds(0, 0, width, height);
	    imgPanel.setPreferredSize(new Dimension(width, height));
	    imgPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
	    
	    JPanel buttonPanel = new JPanel();
	    buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
	    playButton = new JButton("Play");
	    playButton.setAlignmentX(Component.CENTER_ALIGNMENT);
	    playButton.addActionListener(listener);
	    pauseButton = new JButton("Pause");
	    pauseButton.setAlignmentX(Component.CENTER_ALIGNMENT);
	    pauseButton.addActionListener(listener);
	    stopButton = new JButton("Stop");
	    stopButton.setAlignmentX(Component.CENTER_ALIGNMENT);
	    stopButton.addActionListener(listener);
	    searchButton = new JButton("Search");
	    searchButton.setAlignmentX(Component.CENTER_ALIGNMENT);
	    searchButton.addActionListener(listener);
	    buttonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
	    buttonPanel.add(playButton);
	    buttonPanel.add(pauseButton);
	    buttonPanel.add(stopButton);
	    buttonPanel.add(searchButton);
	    
	    topPanel.add(imgPanel);
	    topPanel.add(buttonPanel);
	    
	    for (int i = 0; i < videoCount; i++){
	    	ImageStripPanel temp = new ImageStripPanel(i);
	    	bottomPanel.add(temp);
	    	temp.setAlignmentX(Component.CENTER_ALIGNMENT);
	    }

	    bottomPanel.add(new JLabel("image strip of frames will go here"));
	    container.add(topPanel);
	    container.add(bottomPanel);
	    
	    frame.getContentPane().add(scrollContainer, BorderLayout.CENTER);
	    frame.pack();
	    frame.setVisible(true); 	

		videoFrame = 0;
	    imgPanel.img = vdo[currentVideo][videoFrame];

		audioInputStream = null;
		try {
			InputStream bufferedIn = new BufferedInputStream(waveStream);
		    audioInputStream = AudioSystem.getAudioInputStream(bufferedIn);
		} catch (UnsupportedAudioFileException e1) {
			System.out.println(e1);
		    //throw new PlayWaveException(e1);
		} catch (IOException e1) {
			System.out.println(e1);
		    //throw new PlayWaveException(e1);
		}
	
		// Obtain the information about the AudioInputStream
		audioFormat = audioInputStream.getFormat();
		System.out.println("audio frame rate: " + audioFormat.getFrameRate());
		System.out.println("audio frame size: " + audioFormat.getFrameSize());
		
		info = new Info(SourceDataLine.class, audioFormat);
	
		// opens the audio channel
		dataLine = null;
		try {
		    dataLine = (SourceDataLine) AudioSystem.getLine(info);
		    dataLine.open(audioFormat, buffersize);
		} catch (LineUnavailableException e1) {
			System.out.println(e1);
		    //throw new PlayWaveException(e1);
		}
	}

	
	public void play(){
		if (status != VideoStatus.PLAYING){
			status = VideoStatus.PLAYING;
					
			videoTimer = new ScheduledThreadPoolExecutor(5);
			videoTimer.scheduleAtFixedRate(new PlayVideo(), 0, 41666666, TimeUnit.NANOSECONDS); // 41.66	
			dataLine.start();
			audioTimer = new ScheduledThreadPoolExecutor(5);
			audioTimer.scheduleAtFixedRate(new PlayAudio(), 0, 41666666, TimeUnit.NANOSECONDS);
		}
	}
	
	public void pause(){
		if (status == VideoStatus.PLAYING){
			status = VideoStatus.PAUSED;
			videoTimer.shutdownNow();
			audioTimer.shutdownNow();
			dataLine.stop();	
		}
	}
	
	public void stop(){
		status = VideoStatus.STOPPED;

		videoTimer.shutdownNow();
		videoFrame = 0;
	    imgPanel.img = vdo[currentVideo][videoFrame];
	    imgPanel.repaint();	    

		audioTimer.shutdownNow();
		dataLine.stop();
		

		try {
			waveStream = new FileInputStream(audio);
		} catch (FileNotFoundException e) {
			System.out.println(e);
			
		}
	    audioInputStream = null;
		try {
			InputStream bufferedIn = new BufferedInputStream(waveStream);
		    audioInputStream = AudioSystem.getAudioInputStream(bufferedIn);
		} catch (UnsupportedAudioFileException e1) {
			System.out.println(e1);
		    //throw new PlayWaveException(e1);
		} catch (IOException e1) {
			System.out.println(e1);
		    //throw new PlayWaveException(e1);
		}
	
		// Obtain the information about the AudioInputStream
		audioFormat = audioInputStream.getFormat();
		info = new Info(SourceDataLine.class, audioFormat);
	
		// opens the audio channel
		dataLine = null;
		try {
		    dataLine = (SourceDataLine) AudioSystem.getLine(info);
		    dataLine.open(audioFormat, buffersize);
		} catch (LineUnavailableException e1) {
			System.out.println(e1);
		    //throw new PlayWaveException(e1);
		}
		
	}
	
	public void search(){
		status = VideoStatus.SEARCHING;
		//	TODO
	}
	
	public class MyListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == playButton){
				System.out.println("play pressed");				
				play();
			}else if (e.getSource() == pauseButton){
				System.out.println("pause pressed");
				pause();
			}else if (e.getSource() == stopButton){
				System.out.println("stop pressed");
				stop();
			}else if (e.getSource() == searchButton){
				System.out.println("search pressed");
				search();
			}			
		}
		
	} // end of MyListener class
	
	public class PlayVideo implements Runnable {
		public void run(){
			videoFrame++;
			if (videoFrame >= vdo[currentVideo].length){
				videoFrame = 0;
				videoTimer.shutdownNow();
				return;
			}
			imgPanel.img = vdo[currentVideo][videoFrame];
			imgPanel.repaint();
		}
	} // end of PlayVideo Timer class

	
	public class PlayAudio implements Runnable {
		public void run(){
			try {
				readBytes = audioInputStream.read(audioBuffer, 0, audioBuffer.length);
				if (readBytes >= 0){
				    dataLine.write(audioBuffer, 0, readBytes);
				}else{
					dataLine.drain();
				    dataLine.close();
					audioTimer.shutdownNow();
					return;
				}
				
			} catch (IOException e) {
				System.out.println();
			}
		}
	}
	
	public class MyPanel extends JPanel{
		public BufferedImage img;
		int imgFrame = -1;
		
		MyPanel(BufferedImage i){
			super();
			img = i;
		}
		
		MyPanel(){
			super();
		}
		
		public void paintComponent(Graphics g){
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;
			if (img != null){
				g2d.drawImage(img, null, 0,0);
			}
		}
		
		public void paint(Graphics g){
			super.paint(g);
			Graphics2D g2d = (Graphics2D) g;
			if (img != null){
				g2d.drawImage(img, null, 0,0);
			}
		}
		
		public String toString(){
			return("imgFrame: " + imgFrame);
		}
		

	} // end MyPanel class
	
	public class ImageStripPanel extends JPanel{
		int numPanelFrames = 10;
		int newHeight = 50;
		int newWidth = 61;
		MyPanel panels[] = new MyPanel[numPanelFrames];
		MyMouseListener mml;
		
		public ImageStripPanel(int videoIndex){
			super();
			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
			JScrollPane scrollpane = new JScrollPane(panel);
			this.add(scrollpane);
			this.setPreferredSize(new Dimension(350, 70));
			scrollpane.setPreferredSize(new Dimension(350, 70));
			
			this.setPreferredSize(new Dimension(350, 50));
			mml = new MyMouseListener();
			
			for (int i = 0; i < numPanelFrames; i++){
				BufferedImage tempImg = vdo[videoIndex][numFrames * i / numPanelFrames];
				
				BufferedImage newImg = new BufferedImage(newWidth, newHeight, tempImg.getType());  
		        Graphics2D g2d = newImg.createGraphics();  
		        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);  
		        g2d.drawImage(tempImg, 0, 0, newWidth, newHeight, 0, 0, width, height, null);  
		        g2d.dispose();  
		        
		        panels[i] = new MyPanel(newImg);
		        panels[i].imgFrame = numFrames * i / numPanelFrames;
		        panels[i].setPreferredSize(new Dimension(newWidth + 2, newHeight));
		        panels[i].addMouseListener(mml);
		        panel.add(panels[i]);
			}
			
		}
		
		public ImageStripPanel(){
			super();
		}
	} // end ImageStripPanel
	
	
	public class MyMouseListener implements MouseListener{

		@Override
		public void mouseClicked(MouseEvent arg0) {
			//System.out.println(arg0.getSource().toString());
			if (status == VideoStatus.PLAYING){
				pause();
			}
			int temp = ((MyPanel)arg0.getSource()).imgFrame;

			videoFrame = temp;
			imgPanel.img = vdo[currentVideo][videoFrame];
			imgPanel.repaint();
			
			
			
			try {
				waveStream = new FileInputStream(audio);
			} catch (FileNotFoundException e) {
				System.out.println(e);
			}
			
		    audioInputStream = null;
			try {
				InputStream bufferedIn = new BufferedInputStream(waveStream);
				audioInputStream = AudioSystem.getAudioInputStream(bufferedIn);
			    //audioInputStream.skip(temp * buffersize);
			} catch (UnsupportedAudioFileException e1) {
				System.out.println(e1);
			    //throw new PlayWaveException(e1);
			} catch (IOException e1) {
				System.out.println(e1);
				e1.printStackTrace();
			    //throw new PlayWaveException(e1);
			}
		
			// Obtain the information about the AudioInputStream
			audioFormat = audioInputStream.getFormat();
			info = new Info(SourceDataLine.class, audioFormat);

		      
			// opens the audio channel
			dataLine = null;
			try {
				audioInputStream.skip(temp * 30 * (int) audioFormat.getFrameRate() * audioFormat.getFrameSize() / numFrames);
			    dataLine = (SourceDataLine) AudioSystem.getLine(info);
			    dataLine.open(audioFormat, buffersize);
			} catch (LineUnavailableException e1) {
				System.out.println(e1);
			    //throw new PlayWaveException(e1);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
