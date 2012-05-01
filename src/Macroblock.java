import java.awt.image.BufferedImage;

/**
 * @author Ernest Wozniak, Lukasz Wiktor
 */
public class Macroblock {
    
    //coordinates of the macroblock
    private int x;
    private int y;
    private int size;
    BufferedImage bImage;
    
    public static final int SIZE = 16;
    public static final double MAX_DIFFERENCE = SIZE * 255; // sqrt(SIZE*SIZE*255*255);
    
    public Macroblock(BufferedImage bImage, int x, int y){
        this.x = x;
        this.y = y;
        this.bImage = bImage;
        size = SIZE;
    }
    
    public Macroblock(BufferedImage bImage, int x, int y, int size) {
        this.x = x;
        this.y = y;
        this.bImage = bImage;
        this.size = size;
    }
    
    public int getX(){
        return this.x;
    }
    
    public int getY(){
        return this.y;
    }
    
    /**
     * @param macroblock
     * @return 0.0 if two macroblocks are equal;
     *         MAX_DIFFERENCE if two macroblocks are completly different
     */
    public double compareTo(Macroblock macroblock){
        double result = 0;
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                int rgb1, rgb2;
                double lum1, lum2;
                if(this.x + i >= this.bImage.getWidth() || this.x + i < 0 ||
                   this.y + j >= this.bImage.getHeight() || this.y + j < 0){
                    rgb1 = 0;
                } else {
                    rgb1 = this.bImage.getRGB(this.x + i, this.y + j);
                }
                if(macroblock.x + i >= macroblock.bImage.getWidth() || macroblock.x + i < 0 ||
                   macroblock.y + j >= macroblock.bImage.getHeight() || macroblock.y + j < 0){
                    rgb2 = 0;
                }else{
                    rgb2 = macroblock.bImage.getRGB(macroblock.x + i, macroblock.y + j);
                }
                lum1 = ColorSpaceConverter.getLuminanceFromRGB(rgb1);
                lum2 = ColorSpaceConverter.getLuminanceFromRGB(rgb2);
                result += (lum2-lum1)*(lum2-lum1);
            }
        }
        return Math.sqrt(result);
    }
    
    /**
     * @param macroblock
     * @return 0.0 if two macroblocks are equal;
     *         MAX_DIFFERENCE if two macroblocks are completly different
     */
    public double getPercentageMatch(Macroblock macroblock) {        
        return (1.0 - (compareTo(macroblock)/(size*255))) * 100;
    }
}
