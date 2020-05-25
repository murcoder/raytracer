
public class Mesh extends Shape{

	
	private String textureName;
	
	
	
	@Override
	public boolean RayIntersect(Ray ray) {
		// TODO Auto-generated method stub
		return false;
	}



	public String getTextureName() {
		return textureName;
	}



	public void setTextureName(String textureName) {
		this.textureName = textureName;
	}

}
