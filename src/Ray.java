
/**
 * 
 * @author Christoph Murauer - a1127084
 *
 */
public class Ray {

	public static final double MAX_T = Double.MAX_VALUE;
	private double t;
	private Vec3 origin;
	private Vec3 direction;
	
	
	
	Ray(){
		origin = new Vec3(0,0,0);
		direction = new Vec3(0,0,0);
	}
	
	Ray(Vec3 o, Vec3 d){
		this.origin = new Vec3(o.getX(), o.getY(), o.getZ());
		this.direction = new Vec3(d.getX(), d.getY(), d.getZ());
	}
	
	/**
	 * Calculate the point p on the ray depending on variable t
	 * p = o + t*d
	 * @param t
	 * @return shorter or longer vector on the ray
	 */
	public Vec3 getPointOnRay(double t){
		
		Vec3 tmpO = Vec3.multiplyScalar(getDirection(), t);
		Vec3 result = Vec3.addVec(tmpO, getOrigin());
		
		return result;
	}
	
	/**
	 * Returns a copy of the current instance of Ray
	 * @return
	 */
	public Ray copy(){
			Ray copy = new Ray();
			copy.direction.setX(this.direction.getX());
			copy.direction.setY(this.direction.getY());
			copy.direction.setZ(this.direction.getZ());
			copy.origin.setX(this.origin.getX());
			copy.origin.setY(this.origin.getY());
			copy.origin.setZ(this.origin.getZ());
			
			return copy;
	}
	
	
	/**
	 * Print the attributes in the console
	 */
	public void print(){
		
		System.out.println("Origin: " + "[" + getOrigin().getX() + "," + getOrigin().getY() + "," + getOrigin().getZ() + 
				"]; " + "\n" + "Direction: " + "[" + getDirection().getX() + "," + getDirection().getY() + "," + getDirection().getZ() + "]" + "\n");
		
	}

	public String toString(){
		return "Origin: " + "[" + getOrigin().getX() + "," + getOrigin().getY() + "," + getOrigin().getZ() + 
				"]; " + "\n" +  "Direction: " + "[" + getDirection().getX() + "," + getDirection().getY() + "," + getDirection().getZ() + "]" + "\n";
	}
	
	public Vec3 getEnd(double t) {
		Vec3 result = Vec3.multiplyScalar(direction, t);
		result.add(origin);
		return result;
	}
	
	
	//-------------GETTER SETTER
	
	public Vec3 getOrigin() {
		return origin;
	}

	public void setOrigin(Vec3 origin) {
		this.origin.setX(origin.getX());
		this.origin.setY(origin.getY());
		this.origin.setZ(origin.getZ());
	}

	public Vec3 getDirection() {
		return direction;
	}

	public void setDirection(Vec3 direction) {
		this.direction.setX(direction.getX());
		this.direction.setY(direction.getY());
		this.direction.setZ(direction.getZ());
	}

	public double getT() {
		return t;
	}

	public void setT(double t) {
		this.t = t;
	}
	
}
