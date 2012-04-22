import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;


public class VideoIndexer {
	
	BufferedImage vdo[] = new BufferedImage[720];
	MyPanel imgPanel = new MyPanel();
	int videoFrame;
	Timer videoTimer;
	JButton playButton, pauseButton, stopButton, searchButton;
	MyListener listener;

    int height = 288;
    int width = 352;
    int numFrames = 720;
	
	public static void main(String[] args){
			
			VideoIndexer vi = new VideoIndexer();
		
		
	}
	public VideoIndexer(){


		
		try{
			File file = new File("C:/Users/edeng/VideoIndexer/vdo1/vdo1.rgb"); // TODO change this path to your own
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
	    		vdo[i] = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
				for(int y = 0; y < height; y++){
			
					for(int x = 0; x < width; x++){
				 
						byte a = 0;
						byte r = bytes[ind];
						byte g = bytes[ind+height*width];
						byte b = bytes[ind+height*width*2]; 
						
						int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
						//int pix = ((a << 24) + (r << 16) + (g << 8) + b);
						//System.out.println("x: " + x + "; y: "+ y + "; pix " + pix + "; ind: " + ind);
						vdo[i].setRGB(x,y,pix);
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
	    
	    bottomPanel.add(new JLabel("image strip of frames will go here"));
	    
	    container.add(topPanel);
	    container.add(bottomPanel);
	    
	    frame.getContentPane().add(container, BorderLayout.CENTER);

	    frame.pack();
	    frame.setVisible(true); 	

		videoFrame = 0;
	    imgPanel.img = vdo[videoFrame];
	}

	
	
	public class MyListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == playButton){
				System.out.println("play pressed;");

				videoTimer = new Timer();
				videoTimer.schedule(new PlayVideo(), 0, 42); // 41.66				
			}else if (e.getSource() == pauseButton){
				System.out.println("pause pressed;");
				videoTimer.cancel();
			}else if (e.getSource() == stopButton){
				System.out.println("stop pressed;");
				videoTimer.cancel();
				videoFrame = 0;
			    imgPanel.img = vdo[videoFrame];
			    imgPanel.repaint();
			}else if (e.getSource() == searchButton){
				System.out.println("search pressed;");
				// TODO nothing for now
			}			
		}
		
	} // end of MyListener class
	
	public class PlayVideo extends TimerTask{
		public void run(){
			videoFrame = (videoFrame + 1) % vdo.length;
			imgPanel.img = vdo[videoFrame];
			imgPanel.repaint();
		}
	} // end of PlayVideo Timer class

	public class MyPanel extends JPanel{
		public BufferedImage img;
		
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
		

	} // end MyPanel class
}