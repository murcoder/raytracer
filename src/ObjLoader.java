import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Read each string line of an obj file and parse it into a model object
 * v........ Vertex line: List of geometric vertices, with (x,y,z[,w]) coordinates
 * vn....... Vertex normal: List of vertex normals in (x,y,z) form; normals might not be unit vectors.
 * vt....... Texture index: List of texture coordinates, in (u, v [,w]) coordinates, these will vary between 0 and 1
 * f........ Face line:  Vertex Normal Indices - normal indices can be used to specify normal vectors for vertices when defining a face 
 * f........ v1/vt1/vn1 v2/vt2/vn2 v3/vt3/vn3 ....
 * @author Christoph Murauer - a1127084
 *
 */
public class ObjLoader {

	
	public ObjLoader(){
		
	}
	
	public Model loadModel(File f) throws FileNotFoundException, IOException{
		BufferedReader reader = new BufferedReader(new FileReader(f));
		Model m = new Model();
		String line;
		while((line = reader.readLine()) != null){
			if (line.startsWith("v ")){
				double x = Double.valueOf(line.split(" ")[1]);
				double y = Double.valueOf(line.split(" ")[2]);
				double z = Double.valueOf(line.split(" ")[3]);
				m.getVertices().add(new Vec3(x,y,z));
			}else if(line.startsWith("vn ")){
				double x = Double.valueOf(line.split(" ")[1]);
				double y = Double.valueOf(line.split(" ")[2]);
				double z = Double.valueOf(line.split(" ")[3]);
				m.getNormals().add(new Vec3(x,y,z));
			}else if(line.startsWith("vt ")){
				double x = Double.valueOf(line.split(" ")[1]);
				double y = Double.valueOf(line.split(" ")[2]);
				m.getTextures().add(new Vec3(x,y,0));
			}else if (line.startsWith("f ")){
				//Cut the String: vertex/texture/normal
				Vec3 vertexIndices = new Vec3(Float.valueOf(line.split(" ")[1].split("/")[0]),
						Double.valueOf(line.split(" ")[2].split("/")[0]),
						Double.valueOf(line.split(" ")[3].split("/")[0]));
				Vec3 textureIndices = new Vec3(Float.valueOf(line.split(" ")[1].split("/")[1]),
						Double.valueOf(line.split(" ")[2].split("/")[1]),
						Double.valueOf(line.split(" ")[3].split("/")[1]));
				Vec3 normalIndices = new Vec3(Float.valueOf(line.split(" ")[1].split("/")[2]),
						Double.valueOf(line.split(" ")[2].split("/")[2]),
						Double.valueOf(line.split(" ")[3].split("/")[2]));
				m.getFaces().add(new Face(vertexIndices, textureIndices, normalIndices));
			}
		}
		reader.close();
		return m;
	}
	
	
}
