

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * The background Image of the Scene
 * @author Christoph Murauer - a1127084
 *
 */
public class Image {

	
	private BufferedImage image; 
	private int width;
	private int height;
	private File outputFile;
	private String filename;
	private int imageSize;
	private Color color;
	
	

	/**
	 * Constructor
	 * @param width
	 * @param height
	 */
	Image(int width, int height){
		
		setWidth(width);
		setHeight(height);
		setImageSize(width * height);
		setOutputFile(null);
		setColor(null);
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

	}

	
	public Color getColor() {
		return color;
	}


	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * Fill the Image with color
	 * @param r
	 * @param g
	 * @param b
	 */
	public void fillImage(int r, int g, int b){
		
		color = new Color(r, g, b);
		int rgb = color.getRGB();
		
			for (int y = 0; y < height; y++) {
		        for (int x = 0; x < width; x++) {
		        	image.setRGB(y, x, rgb);
		        }
		    } 
	}
	
	
	/**
	 * Convert coordinates of a Ray (r,g,b) and set as color [0-255] for the pixel on position (x,y)
	 * @param x
	 * @param y
	 * @param r
	 * @param g
	 * @param b
	 */
	public void setColorToPixel(int u, int v, double r, double g, double b){
		
		//Remove normalization on height and width
		double origR = (double) (r/(double) 512); 
		double origG = (double) (g/(double) 512);

		//Normalize with factor 255
		int tmpR = (int) Math.round(origR*255); 
		int tmpG = (int) Math.round(origG*255);
		int tmpB = (int) Math.round(b);
		
		color = new Color(tmpR, tmpG, tmpB);
		int rgb = color.getRGB();
		image.setRGB(u, v, rgb);
		        
	}

	
	/**
	 * Set the coordinates of a Ray (r,g,b) as color of the pixel on position (x,y)
	 * @param x
	 * @param y
	 * @param r
	 * @param g
	 * @param b
	 */
	public void setColorToPixelInt(int u, int v, int r, int g, int b){

		
		color = new Color(r, g, b);
		int rgb = color.getRGB();
		image.setRGB(u, v, rgb);
		        
	}
	
	
	/**
	 * Calculates RGB Values from float format to int format and returns an Color object
	 * @param r
	 * @param g
	 * @param b
	 * @return
	 */
	public static Color floatInRGB(float r, float g, float b){
		int tmpR = (int) Math.round(r*255); 
		int tmpG = (int) Math.round(g*255);
		int tmpB = (int) Math.round(b*255);
		Color color = new Color(tmpR, tmpG, tmpB);
		
		return color;
	}
	
	/**
	 * Save the image as 'filename'.png
	 */
	public void saveImage(){
		try{
			outputFile = new File(filename);
		    ImageIO.write(image, "png", outputFile);
		}
		catch (IOException e) {
			System.out.println("Problem in saveImage(): " + e);
		}
		System.out.println("Image saved in " +  filename  + "! " +  "Resolution: " + getWidth() + "x" + getHeight());
	}


	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}






	public int getWidth() {
		return width;
	}






	public void setWidth(int width) {
		this.width = width;
	}






	public int getHeight() {
		return height;
	}






	public void setHeight(int height) {
		this.height = height;
	}






	public int getImageSize() {
		return imageSize;
	}






	public void setImageSize(int imageSize) {
		this.imageSize = imageSize;
	}



	public File getOutputFile() {
		return outputFile;
	}



	public void setOutputFile(File outputFile) {
		this.outputFile = outputFile;
	}



	public String getFilename() {
		return filename;
	}



	public void setFilename(String filename) {
		this.filename = filename;
	}


	
}
