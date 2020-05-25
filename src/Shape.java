import java.awt.Color;

/**
 * Defines a geometry shape
 * @author Christoph Murauer - a1127084
 *
 */
public abstract class Shape {

    protected String name;
	protected float r;
	protected float g;
	protected float b;
	protected Color color;
	protected float ka, kd, ks, exp;    // constants for phong model
	protected float reflectance;
	protected float transmittance;
	protected float refraction;
	private double t;
	
	public abstract boolean RayIntersect( Ray ray );
	
	Shape(){
		
	}


	
	public void print(){
		System.out.println("---------- " + name + " ----------");
		System.out.println("Color: " + color.toString());
		System.out.println("phongExp: " + exp + "; Reflectance: " + reflectance + "; Transmittance: "
		+ transmittance + "; Refraction: " + refraction);
		System.out.println("----------------------------" + "\n");
	}
	
	
	


	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}



	public float getReflectance() {
		return reflectance;
	}

	public void setReflectance(float reflectance) {
		this.reflectance = reflectance;
	}

	public float getTransmittance() {
		return transmittance;
	}

	public void setTransmittance(float transmittance) {
		this.transmittance = transmittance;
	}

	public float getRefraction() {
		return refraction;
	}

	public void setRefraction(float refraction) {
		this.refraction = refraction;
	}
	public float getKa() {
		return ka;
	}
	public void setKa(float ka) {
		this.ka = ka;
	}
	public float getKd() {
		return kd;
	}
	public void setKd(float kd) {
		this.kd = kd;
	}
	public float getKs() {
		return ks;
	}
	public void setKs(float ks) {
		this.ks = ks;
	}
	public float getExp() {
		return exp;
	}
	public void setExp(float exp) {
		this.exp = exp;
	}

	public double getT() {
		return t;
	}

	public void setT(double t) {
		this.t = t;
	}
	
	
	
}
