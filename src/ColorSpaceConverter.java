import java.awt.image.BufferedImage;

/**
 *
 * @author Michal Wasniowski, Lukasz Wiktor
 */
public class ColorSpaceConverter {
    
    public static int getLuminanceFromBufferedImage(BufferedImage image, int x, int y) {
       
       if(x < 0 || y < 0 || x >= image.getWidth() || y >= image.getHeight() ) {
           return 0;
       }
       
       return getLuminanceFromRGB(image.getRGB(x, y));
    }
    
    public static int getLuminanceFromRGB(int rgb) {
        int red = getRedFromRGB(rgb); // czerwony
        int green = getGreenFromRGB(rgb); // zielony
        int blue = getBlueFromRGB(rgb); // niebieski
        return getLuminanceFromRGB(red, green, blue);
    }

    public static int getRGBFromLuminance(int lum) {
        int rgb = 0;
        
        //set red
        if (lum < 0) {
            rgb += (-lum << 16);
        }
            
        //set green
        //rgb += (0 << 8);
        
        //set blue
        if (lum > 0) {
            rgb += lum;
        }
        
        return rgb;
    }

    public static int getLuminanceFromRGB(int red, int green, int blue)
    {
        // acording to formula from lecture
        // Y = 0,299R + 0,587G + 0,114B
        double result = 0.299* red + 0.587*green + 0.114*blue;
        return (int)result;
    }
    
    public static int getRedFromRGB(int rgb) {
        return (rgb & 0x00ff0000) >> 16;
    }
    
    public static int getGreenFromRGB(int rgb) {
        return (rgb & 0x0000ff00) >> 8;
    }
    
    public static int getBlueFromRGB(int rgb) {
        return (rgb & 0x000000ff);
    }
    
    public static int getRGBFromRedGreenBlue(int red, int green, int blue) {
        int rgb = 0xff000000;
        rgb += red << 16;
        rgb += green << 8;
        rgb += blue;
                
        return rgb;
    }
    
    public static int getMeanRed(int point00, int point01, int point10, int point11) {
        int red = getRedFromRGB(point00);
        red += getRedFromRGB(point01);
        red += getRedFromRGB(point10);
        red += getRedFromRGB(point11);
        return red /= 4;
    }

    public static int getMeanGreen(int point00, int point01, int point10, int point11) {
        int green = getGreenFromRGB(point00);
        green += getGreenFromRGB(point01);
        green += getGreenFromRGB(point10);
        green += getGreenFromRGB(point11);
        return green /= 4;
    }

    public static int getMeanBlue(int point00, int point01, int point10, int point11) {
        int blue = getBlueFromRGB(point00);
        blue += getBlueFromRGB(point01);
        blue += getBlueFromRGB(point10);
        blue += getBlueFromRGB(point11);
        return blue /= 4;
    }
    
    public static int getMeanRGB(int rgb1, int rgb2, int rgb3, int rgb4) {
        int green = getMeanGreen(rgb1, rgb2, rgb3, rgb4);
        int red = getMeanRed(rgb1, rgb2, rgb3, rgb4);
        int blue = getMeanBlue(rgb1, rgb2, rgb3, rgb4);
        int rgb = ColorSpaceConverter.getRGBFromRedGreenBlue(red, green, blue);
        return rgb;
    }
}