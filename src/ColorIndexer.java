import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.HashMap;


public class ColorIndexer {


    int height = 288;
    int width = 352;
    int numFrames = 720;
	public static void main(String[] args){
		ColorIndexer ci = new ColorIndexer();
	}
	
	public ColorIndexer(){
		String[] filenames = new String[12];
		filenames[0] = "C:/Users/Cauchy/Documents/CSCI576/Project/vdo1/vdo1.rgb";
		filenames[1] = "C:/Users/Cauchy/Documents/CSCI576/Project/vdo2/vdo2.rgb";
		filenames[2] = "C:/Users/Cauchy/Documents/CSCI576/Project/vdo3/vdo3.rgb";
		filenames[3] = "C:/Users/Cauchy/Documents/CSCI576/Project/vdo4/vdo4.rgb";
		filenames[4] = "C:/Users/Cauchy/Documents/CSCI576/project/vdo5/vdo5.rgb";
		filenames[5] = "C:/Users/Cauchy/Documents/CSCI576/project/vdo6/vdo6.rgb";
		filenames[6] = "C:/Users/Cauchy/Documents/CSCI576/Project/vdo7/vdo7.rgb";
		filenames[7] = "C:/Users/Cauchy/Documents/CSCI576/Project/vdo8/vdo8.rgb";
		filenames[8] = "C:/Users/Cauchy/Documents/CSCI576/Project/vdo9/vdo9.rgb";
		filenames[9] = "C:/Users/Cauchy/Documents/CSCI576/Project/vdo10/vdo10.rgb";
		filenames[10] = "C:/Users/Cauchy/Documents/CSCI576/Project/vdo11/vdo11.rgb";
		filenames[11] = "C:/Users/Cauchy/Documents/CSCI576/Project/vdo12/vdo12.rgb";
		
		try{
			FileWriter fw = new FileWriter("colorindex.txt");
			BufferedWriter bw = new BufferedWriter(fw);
			
			for (int j = 0; j < 12; j++){
				File file = new File(filenames[j]);
				String[] colorindex = new String[numFrames];
				bw.write("" + j + " ");
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
	
				    HashMap<String, Integer> histogram = new HashMap<String, Integer>();
					for(int y = 0; y < height; y++){
				
						for(int x = 0; x < width; x++){
					 
							int r = (int) bytes[ind];
							int g = (int) bytes[ind+height*width];
							int b = (int) bytes[ind+height*width*2]; 
							if (r < 0){
								r+=256;
							}
							if (g < 0){
								g+=256;
							}
							
							if (b < 0){
								b+=256;
							}
							
							MyHSV hsv = new MyHSV(r, g, b);
							if (histogram.get(hsv.toString()) == null){
								histogram.put(hsv.toString(), 0);
							}else{
								histogram.put(hsv.toString(), histogram.get(hsv.toString()) + 1);
							}
							
							
							ind++;
						}
					}
					
					String maxHSV = "";
					for (String s : histogram.keySet()	){
						if (maxHSV.equals("")){
							maxHSV = s;
						}else{
							if (histogram.get(s) > histogram.get(maxHSV)){
								maxHSV = s;
							}
						}
					}
					colorindex[i] = maxHSV;
					//System.out.println("colorindex[" + i + "] = " + colorindex[i]);
					bw.write("" + maxHSV + " ");
					ind = (ind * 3) / (i + 1);
		    	}
		    	bw.write("\n");
		    	System.out.println("Finished " + j + "th video");
	    	
			}
			
			bw.close();
			System.out.println("Done calculating");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	

	
	public class MyHSV{
		int h, s, v;
		
		/*
		 * H is normalized to 16 levels:  0-15. -1 = undefined
		 * S is normalized to 4 levels: 0-3
		 * V is normalized to 4 levels: 0-3
		 */
		
		
		public MyHSV(int ri, int gi, int bi){
			float r = (float) (ri / 256.0);
			float g = (float) (gi / 256.0);
			float b = (float) (bi / 256.0);
			float  min, max, c;
			float vtemp, stemp, htemp;
			max = Math.max(r, Math.max(g, b));
			min = Math.min(r, Math.min(g, b));
			c = max - min;
			vtemp = max;
			
			if (max != 0){
				stemp = (int) Math.round((float) c / (float) max);
			}else{
				stemp = 0;
				htemp = -1;
			}
			
			if( r == max ){
				htemp = ( g - b ) / c;
			}else if( g == max ){
				htemp = 2 + ( b - r ) / c;
			}else{
				htemp = 4 + ( r - g ) / c;
			}
			htemp = htemp *  60;
			if( htemp < 0 )
				htemp += 360;
			htemp = htemp / (float)360.0;
			
			//System.out.println("htemp: " + htemp);
			// now normalize the values
			if (htemp != -1){
				h = (int) Math.round(htemp * 16);
			}
			s = (int) Math.round(stemp * 8);
			v = (int) Math.round(vtemp * 8);

		}
		
		public String toString(){
			if (h < 10){
				return "0" + h + "" + s + "" + v;
			}
			return "" + h + "" + s + "" + v;
		}
	} // end MyHSV
	
}
