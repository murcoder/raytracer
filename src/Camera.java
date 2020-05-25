
/**
 * Handles the view
 * @author Christoph Murauer - a1127084
 *
 */
public class Camera {

	
	private Vec3 position;
	private Vec3 lookat;
	private Vec3 up;
	private int horizontal_fov;
	private int width;
	private int height;
	private int max_bounce;
	
	Camera(){
	  this.position = new Vec3();
	  this.lookat = new Vec3();
	  this.up = new Vec3();
	}

	
	

	
	
	
	public Vec3 getPosition() {
		return position;
	}

	public void setPosition(Vec3 position) {
		this.position.setX( position.getX() );
		this.position.setY( position.getY() );
		this.position.setZ( position.getZ() );
	}

	public Vec3 getLookat() {
		return lookat;
	}

	public void setLookat(Vec3 lookat) {
		this.lookat.setX( lookat.getX() );
		this.lookat.setY( lookat.getY() );
		this.lookat.setZ( lookat.getZ() );
	}

	public Vec3 getUp() {
		return up;
	}

	public void setUp(Vec3 up) {
		this.up.setX( up.getX() );
		this.up.setY( up.getY() );
		this.up.setZ( up.getZ() );
	}

	public int getHorizontal_fov() {
		return horizontal_fov;
	}

	public void setHorizontal_fov(int horizontal_fov) {
		this.horizontal_fov = horizontal_fov;
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

	public int getMax_bounce() {
		return max_bounce;
	}

	public void setMax_bounce(int max_bounce) {
		this.max_bounce = max_bounce;
	}
	
	
	
}
