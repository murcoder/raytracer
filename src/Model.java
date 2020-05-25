import java.util.ArrayList;
import java.util.List;





/**
 * The Model contains all data of an obj-File in seperated Lists
 * @author Christoph Murauer - 1127084
 *
 */
public class Model {
	private List<Vec3> vertices = new ArrayList<Vec3>();
	private List<Vec3> textures = new ArrayList<Vec3>();
	private List<Vec3> normals = new ArrayList<Vec3>();
	private List<Face> faces = new ArrayList<Face>();
	
	
	public Model(){
		
	}


	public List<Vec3> getVertices() {
		return vertices;
	}


	public void setVertices(List<Vec3> vertices) {
		this.vertices = vertices;
	}


	public List<Vec3> getNormals() {
		return normals;
	}


	public void setNormals(List<Vec3> normals) {
		this.normals = normals;
	}


	public List<Face> getFaces() {
		return faces;
	}


	public void setFaces(List<Face> faces) {
		this.faces = faces;
	}


	public List<Vec3> getTextures() {
		return textures;
	}


	public void setTextures(List<Vec3> textures) {
		this.textures = textures;
	}
}
