/**
 * 
 * @author Christoph Murauer - 1127084
 *
 */
public class Vertex {
	private double x, y, z;

	public Vertex(double x, double y, double z) {
		this.setX(x);
		this.setY(y);
		this.setZ(z);
	}

	public Vertex() {
	}

	public double distanceTo(Vertex v) {
		return Math.sqrt((v.getX() - getX())*(v.getX() - getX()) + (v.getY() - getY())*(v.getY() - getY()) + (v.getZ() - getZ())*(v.getZ() - getZ()));
	}

	public Vertex plus(Vec3 v) {
		return new Vertex(getX() + v.getX(), getY() + v.getY(), getZ() + v.getZ());
	}

	public String toString() {
		return "[" + getX() + ", " + getY() + ", " + getZ() + "]";
	}

	
	
	
	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}
}

