import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * The scene consists a camera, light and objects
 * @author Christoph Murauer - a1127084
 *
 */
public class Scene {

	private float r;
	private float g;
	private float b;
	private Color backgroundColor;
	private Camera cam;
	private List<Light> lightList;
	private List<Shape> objectList;
	
	
	
	Scene(){
		r=0;
		g=0;
		b=0;
		backgroundColor = new Color(0,0,0);
		setObjectList(new ArrayList<Shape>());
	}
	
	
	Scene(Camera cam, Light light){
		r=0;
		g=0;
		b=0;
		backgroundColor = new Color(0,0,0);
		this.cam = cam;
		this.lightList = new ArrayList<Light>();
		setObjectList(new ArrayList<Shape>());
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public Camera getCam() {
		return cam;
	}
	public void setCam(Camera cam) {
		this.cam = cam;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}


	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}


	public List<Shape> getObjectList() {
		return objectList;
	}


	public void setObjectList(List<Shape> objectList) {
		this.objectList = objectList;
	}


	public List<Light> getLightList() {
		return lightList;
	}


	public void setLightList(List<Light> lightList) {
		this.lightList = lightList;
	}


	public float getR() {
		return r;
	}


	public void setR(float r) {
		this.r = r;
	}


	public float getG() {
		return g;
	}


	public void setG(float g) {
		this.g = g;
	}


	public float getB() {
		return b;
	}


	public void setB(float b) {
		this.b = b;
	}
	
	
	
	
	
	
	
	
}
