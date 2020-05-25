/**
 * 
 * @author Christoph Murauer - 1127084
 *
 */
public class Plane extends Shape {
	private final double a, b, c, d;
	private final Vec3 normal;
	private Ray hitRay;
	private double t;

	public Plane(double a, double b, double c, double d) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
		this.normal = new Vec3(a, b, c);
		normal.normalize();
		hitRay = null;
		setT(0);
	}

	/**
	 * Check intersection of the ray with the plane
	 */
	@Override
	public boolean RayIntersect(Ray ray) {
		double denominator = (a * ray.getDirection().getX() + b * ray.getDirection().getY() + c * ray.getDirection().getZ());
		
		if(denominator == 0.0) 
			return false;

		double t = - (a * ray.getOrigin().getX() + b * ray.getOrigin().getY() + c * ray.getOrigin().getZ() + d) / denominator;

		if(t < 0)
			return false;
		else{
			//Remember ray and solution t
			this.hitRay = ray;
			this.setT(t);
			return true;
		}
	}


	public Ray getHitRay() {
		return hitRay;
	}


	public void setHitRay(Ray hitRay) {
		this.hitRay = hitRay;
	}

	public double getT() {
		return t;
	}

	public void setT(double t) {
		this.t = t;
	}

}
