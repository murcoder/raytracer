
/**
 * The Face contains three vertices, texture coodrinates and normals to construct a triangle
 * f........ v1/vt1/vn1 v2/vt2/vn2 v3/vt3/vn3
 * @author Christoph Murauer - 1127084
 *
 */
public class Face {

	//Contains three vertices(each vertices have x,y,z!) which construct a triangle
	private Vec3 vertex = new Vec3();
	private Vec3 texture = new Vec3();
	private Vec3 normal = new Vec3();
	
	
	
	public Face(Vec3 vertex, Vec3 texture, Vec3 normal){
		this.setVertex(vertex);
		this.setTexture(texture);
		this.setNormal(normal);
	}


	public Vec3 getVertex() {
		return vertex;
	}


	public void setVertex(Vec3 vertex) {
		this.vertex = vertex;
	}


	public Vec3 getNormal() {
		return normal;
	}


	public void setNormal(Vec3 normal) {
		this.normal = normal;
	}


	public Vec3 getTexture() {
		return texture;
	}


	public void setTexture(Vec3 texture) {
		this.texture = texture;
	}
	
}
