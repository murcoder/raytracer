import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;






public class Raytracer {

	//Input variables
	private String input;
	private Scanner scInt;
	private Scanner scInput;
	boolean ref = false;
	
	//XML variables
	private Document doc;
	private Element root;
	private File inputFile;
	private File outputFile;
	
	//Obj variables
	private List<Model> modelList;
	private List<Mesh> meshList;
	
	//Objects
	private Scene scene;
	private Image img;
	private Ray ray;
	private Camera cam;
	private List<Light> lightList;
	
	//Raytracing
	double fovX;
	double fovY;
	
	//Shading
	protected static final float TINY = 0.001f;
	
	Raytracer(){
		scInt = new Scanner(System.in);
		scInput = new Scanner(System.in);
		input = "";
		scene = null;
		img = null;
		ray = null;
		cam = null;
		lightList = new ArrayList<Light>();
		modelList = new ArrayList<Model>();
		meshList = new ArrayList<Mesh>();
		fovX = 0;
		fovY = 0;
	}
	
	
	
	/**
	 * Parse an xml-file for java and set it as current Document
	 * Create all included object and lights
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public void xmlDomParser() throws ParserConfigurationException, SAXException, IOException, FileNotFoundException{
		
		//Get Documentbuilder
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		
		//Build Document
		this.doc = builder.parse(this.inputFile);
		
		//Normalize the XML Structure
		doc.getDocumentElement().normalize();      
		
		//Set the root node
	    this.root = this.doc.getDocumentElement();
		
	  //Parse the mesh data of the file if there is one
	    if(this.doc.getElementsByTagName("mesh").getLength() != 0){
		   
		    NodeList XMLmeshes = this.doc.getElementsByTagName("mesh");
		    Node nNode = XMLmeshes.item(0);
		    Element XMLmesh = (Element) nNode;
		    String fileName = XMLmesh.getAttribute("name");
		    saveModel (fileName);
	    }
	    
	    //Fill the scene with all XML Attributes
		setScene();
	}
	
	/**
	 * Parse an obj-File into a Model object
	 * @param objString
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public void saveModel(String objString) throws IOException, FileNotFoundException{
		objString = objString; //****** ONLY ON WINDOWS SYSTEMS // LINUX WITHOUT "src/"
		File obj = new File(objString);
		ObjLoader objLoader = new ObjLoader();
		Model model = objLoader.loadModel(obj);
		this.modelList.add(model);
		System.out.println("OBJ-File: " + objString + " parsed and saved!");
		
	}
	
	/**
	 * Constructs Triangles of the facesList and set the attributes of the mesh data
	 */
	public void createTriangles(){
		
		//Iterate throw all models and add the mesh attributes
		for(int me=0; me<meshList.size(); me++){
			Mesh mesh = meshList.get(me);
			
			for(int mo=0; mo<modelList.size(); mo++){
				Model model = modelList.get(mo);
				
				for(int f=0; f<model.getFaces().size(); f++){
					Face face = model.getFaces().get(f);
					
					//Set the vertices on the index, which is indicated of the faces
					//e.g.: face: v1,v4,v3 -> get vertices in model on index 1,4,3
					Vec3 v1 = model.getVertices().get(  (int) (face.getVertex().getX()-1) );
					Vec3 v2 = model.getVertices().get(  (int) (face.getVertex().getY()-1) );
					Vec3 v3 = model.getVertices().get(  (int) (face.getVertex().getZ()-1) );
					
					//Set the normals verices
					Vec3 vn1 =  model.getNormals().get(  (int) (face.getNormal().getX()-1));
					Vec3 vn2 =  model.getNormals().get(  (int) (face.getNormal().getY()-1));
					Vec3 vn3 =  model.getNormals().get(  (int) (face.getNormal().getZ()-1));
					
					//Set the texture verices
					Vec3 vt1 =  model.getTextures().get(  (int) (face.getTexture().getX()-1));
					Vec3 vt2 =  model.getTextures().get(  (int) (face.getTexture().getY()-1));
					Vec3 vt3 =  model.getTextures().get(  (int) (face.getTexture().getZ()-1));
					
					//Create the triangle and set mesh attributes
					Triangle tri = new Triangle(v1,v2,v3,vn1,vn2,vn3,vt1,vt2,vt3);
					tri.name = mesh.name;
					tri.setColor( mesh.getColor() );
					tri.r = mesh.r; tri.g = mesh.g; tri.b = mesh.b;
					tri.setExp( mesh.getExp());
					tri.setKa( mesh.getKa() );
					tri.setKd( mesh.getKd() );
					tri.setKs( mesh.getKs() );
					tri.setReflectance( mesh.getReflectance() );
					tri.setRefraction( mesh.getRefraction() );
					tri.setTransmittance( mesh.getTransmittance() );
					tri.setTextureName( mesh.getTextureName() );
					
					//Add to objectlist
					scene.getObjectList().add(tri);
				}
			}
		}
	}
	
	
	/**
	 * Set up the scene and the camera
	 */
	public void setScene(){
		scene = new Scene();
		
		//Set CAMERA
		setCamera();
		this.scene.setCam(this.cam);
		
		Node XMLbackground = this.doc.getElementsByTagName("background_color").item(0);
		Element background = (Element) XMLbackground;
		
		Float tmpR = Float.parseFloat(background.getAttributes().item(0).getNodeValue());
		Float tmpG = Float.parseFloat(background.getAttributes().item(1).getNodeValue());
		Float tmpB = Float.parseFloat(background.getAttributes().item(2).getNodeValue());
		scene.setR(tmpB);
		scene.setG(tmpG);		
		scene.setB(tmpR);
		Color backgroundColor = Image.floatInRGB(tmpB, tmpG, tmpR);
		scene.setBackgroundColor(backgroundColor);

		//Set LIGHT
		setLight();
		this.scene.setLightList(this.lightList);
		
		//-------------Set all OBJECTS
		
		//Set Spheres
		if(this.doc.getElementsByTagName("sphere").getLength() != 0){
			NodeList XMLspheres = this.doc.getElementsByTagName("sphere");
			
			//Get all Spheres
			for (int i = 0; i < XMLspheres.getLength(); i++) {
				Sphere sphere = new Sphere();
				if(this.doc.getElementsByTagName("material_solid").getLength() != 0){
					Node materialNode = this.doc.getElementsByTagName("material_solid").item(i);
					Node nNode = XMLspheres.item (i);
					if (nNode.getNodeType() == Node.ELEMENT_NODE) {
						Element XMLsphere = (Element) nNode;
						Element material = (Element) materialNode;
						sphere.name = nNode.getNodeName();
						//Set Radius and middlepoint of the sphere
						sphere.setRadius( Double.parseDouble(XMLsphere.getAttribute("radius")) );
						sphere.getMiddlepoint().setX(  Double.parseDouble(XMLsphere.getElementsByTagName("position").item(0).getAttributes().item(0).getNodeValue()) );
						sphere.getMiddlepoint().setY(  Double.parseDouble(XMLsphere.getElementsByTagName("position").item(0).getAttributes().item(1).getNodeValue()) );
						sphere.getMiddlepoint().setZ(  Double.parseDouble(XMLsphere.getElementsByTagName("position").item(0).getAttributes().item(2).getNodeValue()) );
						 
						//Set color of the sphere
						Float r = Float.parseFloat(material.getElementsByTagName("color").item(0).getAttributes().item(0).getNodeValue());
						Float g = Float.parseFloat(material.getElementsByTagName("color").item(0).getAttributes().item(1).getNodeValue());
						Float b = Float.parseFloat(material.getElementsByTagName("color").item(0).getAttributes().item(2).getNodeValue());
						sphere.r = b;
						sphere.g = g;
						sphere.b = r;
							//Convert also float values in rgb colors
						Color color1 = Image.floatInRGB(b, g, r);
						sphere.setColor(color1);
						
						//Set phong attributes of the sphere
						sphere.setExp( Integer.parseInt(material.getElementsByTagName("phong").item(0).getAttributes().item(0).getNodeValue()) );
						sphere.setKa ( Float.parseFloat(material.getElementsByTagName("phong").item(0).getAttributes().item(1).getNodeValue()) );
						sphere.setKd ( Float.parseFloat(material.getElementsByTagName("phong").item(0).getAttributes().item(2).getNodeValue()) );
						sphere.setKs ( Float.parseFloat(material.getElementsByTagName("phong").item(0).getAttributes().item(3).getNodeValue()) );
						
						sphere.setReflectance(Float.parseFloat(material.getElementsByTagName("reflectance").item(0).getAttributes().item(0).getNodeValue()));
						sphere.setTransmittance(Float.parseFloat(material.getElementsByTagName("transmittance").item(0).getAttributes().item(0).getNodeValue()));
						sphere.setRefraction(Float.parseFloat(material.getElementsByTagName("refraction").item(0).getAttributes().item(0).getNodeValue()));
						
						//Add to objectlist
						scene.getObjectList().add(sphere);
					}
				}
			}
		}
			
	
		//Set all Meshes
		if(this.doc.getElementsByTagName("mesh").getLength() != 0){
		NodeList XMLmeshes = this.doc.getElementsByTagName("mesh");
			//Get all meshes
			for (int i = 0; i < XMLmeshes.getLength(); i++) {
				Mesh mesh = new Mesh();
				Node nNode = XMLmeshes.item(i);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element XMLmesh = (Element) nNode;
					mesh.name = XMLmesh.getAttribute("name");
					//System.out.println(XMLmesh.getElementsByTagName("texture").getLength());
					//Set texture of the mesh
					if(XMLmesh.getElementsByTagName("texture").getLength() != 0){
						mesh.setTextureName(XMLmesh.getElementsByTagName("texture").item(0).getAttributes().item(0).getNodeValue());
						float r = (float) 0.50;
						float g = (float) 0.50;
						float b = (float) 0.50;
						mesh.r = r;
						mesh.g = g;
						mesh.b = b;
						Color color1 = Image.floatInRGB(r, g, b);
						mesh.setColor(color1);
					}else
						if(XMLmesh.getElementsByTagName("color").getLength() != 0){
						float r = (float) 0.50;
						float g = (float) 0.50;
						float b = (float) 0.50;
						mesh.r = r;
						mesh.g = g;
						mesh.b = b;
						Color color1 = Image.floatInRGB(r, g, b);
						mesh.setColor(color1);
					}
				
					//Set phong attributes of the mesh
					if(XMLmesh.getElementsByTagName("phong").getLength() != 0){
						mesh.setExp( Integer.parseInt(XMLmesh.getElementsByTagName("phong").item(0).getAttributes().item(0).getNodeValue()) );
						mesh.setKa ( Float.parseFloat(XMLmesh.getElementsByTagName("phong").item(0).getAttributes().item(1).getNodeValue()) );
						mesh.setKd ( Float.parseFloat(XMLmesh.getElementsByTagName("phong").item(0).getAttributes().item(2).getNodeValue()) );
						mesh.setKs ( Float.parseFloat(XMLmesh.getElementsByTagName("phong").item(0).getAttributes().item(3).getNodeValue()) );
					}
					
					mesh.setReflectance(Float.parseFloat(XMLmesh.getElementsByTagName("reflectance").item(0).getAttributes().item(0).getNodeValue()));
					mesh.setTransmittance(Float.parseFloat(XMLmesh.getElementsByTagName("transmittance").item(0).getAttributes().item(0).getNodeValue()));
					mesh.setRefraction(Float.parseFloat(XMLmesh.getElementsByTagName("refraction").item(0).getAttributes().item(0).getNodeValue()));
					
					meshList.add(mesh);
					}
					
				}
			//Creates all Triangle
			createTriangles();
			
		}			
		System.out.println("XML-File: Attributes parsed and saved!");
	}
	
	/**
	 * Fill the camera instance with attributes
	 */
	public void setCamera(){
		cam = new Camera();
		
		Node XMLcamera = this.doc.getElementsByTagName("camera").item(0);
		Element camera = (Element) XMLcamera;
		double tmp[] = new double[3];
		
		//Set resolution
		cam.setWidth(Integer.parseInt( camera.getElementsByTagName("resolution").item(0).getAttributes().item(0).getNodeValue() ));
		cam.setHeight(Integer.parseInt( camera.getElementsByTagName("resolution").item(0).getAttributes().item(1).getNodeValue() ));
		
		//Get position from XLM
		tmp[0] = Double.parseDouble(camera.getElementsByTagName("position").item(0).getAttributes().item(0).getNodeValue());
		tmp[1] = Double.parseDouble(camera.getElementsByTagName("position").item(0).getAttributes().item(1).getNodeValue());
		tmp[2] = Double.parseDouble(camera.getElementsByTagName("position").item(0).getAttributes().item(2).getNodeValue());
		
		//Set position to the middle of the image
//		tmp[0] = tmp[0] + cam.getWidth()/2;
//		tmp[1] = tmp[1] + cam.getHeight()/2;
		Vec3 tmpV = new Vec3(tmp[0], tmp[1], tmp[2]);
		this.cam.setPosition(tmpV);
//		System.out.print(cam.getPosition().getX() + ", ");
//		System.out.print(cam.getPosition().getY()+ ", ");
//		System.out.println(cam.getPosition().getZ());
		
		//Get lookat from XLM
		tmp[0] = Double.parseDouble(camera.getElementsByTagName("lookat").item(0).getAttributes().item(0).getNodeValue());
		tmp[1] = Double.parseDouble(camera.getElementsByTagName("lookat").item(0).getAttributes().item(1).getNodeValue());
		tmp[2] = Double.parseDouble(camera.getElementsByTagName("lookat").item(0).getAttributes().item(2).getNodeValue());
		
		//Normalize lookat
//		tmp[0] = tmp[0] + cam.getWidth()/2;
//		tmp[1] = tmp[1] + cam.getHeight()/2;
		tmpV = new Vec3(tmp[0], tmp[1], tmp[2]);
		this.cam.setLookat(tmpV);	
//		System.out.print(cam.getLookat()[0] + ", ");
//		System.out.print(cam.getLookat()[1] + ", ");
//		System.out.println(cam.getLookat()[2]);

		//Set up
		tmp[0] = Double.parseDouble(camera.getElementsByTagName("up").item(0).getAttributes().item(0).getNodeValue());
		tmp[1] = Double.parseDouble(camera.getElementsByTagName("up").item(0).getAttributes().item(1).getNodeValue());
		tmp[2] = Double.parseDouble(camera.getElementsByTagName("up").item(0).getAttributes().item(2).getNodeValue());
		tmpV = new Vec3(tmp[0], tmp[1], tmp[2]);
		this.cam.setUp(tmpV);		
//		System.out.print(cam.getUp()[0]+ ", ");
//		System.out.print(cam.getUp()[1]+ ", ");
//		System.out.println(cam.getUp()[2]);

		//Set horizontal fov
		cam.setHorizontal_fov(Integer.parseInt( camera.getElementsByTagName("horizontal_fov").item(0).getAttributes().item(0).getNodeValue() ));



		//Set max bounces
		cam.setMax_bounce(Integer.parseInt( camera.getElementsByTagName("max_bounces").item(0).getAttributes().item(0).getNodeValue() ));
//		System.out.println(cam.getMax_bounce());

		fovX = Math.PI/4;
		fovY = (cam.getHeight()/cam.getWidth()) * fovX;
		
	}
	
	
	/**
	 * Fill the Light instance with attributes
	 */
	public void setLight(){
		
		//Check for AMBIENT LIGHT
		Node ambientNode = this.doc.getElementsByTagName("ambient_light").item(0);
		if (ambientNode != null){
			//There is an ambient light; Create new light and add to lightList
			Element ambientLight = (Element) ambientNode;
			float color[] = new float[3];
			color[0] = Float.parseFloat(ambientLight.getElementsByTagName("color").item(0).getAttributes().item(0).getNodeValue());
			color[1] = Float.parseFloat(ambientLight.getElementsByTagName("color").item(0).getAttributes().item(1).getNodeValue());
			color[2] = Float.parseFloat(ambientLight.getElementsByTagName("color").item(0).getAttributes().item(2).getNodeValue());
			Light light = new Light(Light.AMBIENT, null, color[0], color[1], color[2]);
			lightList.add(light);
			//System.out.println("Added AmbientLight!");
		}

		//Check for PARALLEL LIGHT
		Node parallelTest = this.doc.getElementsByTagName("parallel_light").item(0);
		if (parallelTest != null){
			//There is an PARALLEL light; Create new light and add to lightList
			NodeList parallelLights = this.doc.getElementsByTagName("parallel_light");
			for (int i = 0; i < parallelLights.getLength(); i++) {
				Node parallelNode = parallelLights.item (i);
				if (parallelNode.getNodeType() == Node.ELEMENT_NODE) {
					Element parallelLight = (Element) parallelNode;
					
					//Set color and position of the parallel light
					float color[] = new float[3];
					color[0] = Float.parseFloat(parallelLight.getElementsByTagName("color").item(0).getAttributes().item(0).getNodeValue());
					color[1] = Float.parseFloat(parallelLight.getElementsByTagName("color").item(0).getAttributes().item(1).getNodeValue());
					color[2] = Float.parseFloat(parallelLight.getElementsByTagName("color").item(0).getAttributes().item(2).getNodeValue());
					double pos[] = new double[3];
					pos[0] = Double.parseDouble(parallelLight.getElementsByTagName("direction").item(0).getAttributes().item(0).getNodeValue());
					pos[1] = Double.parseDouble(parallelLight.getElementsByTagName("direction").item(0).getAttributes().item(1).getNodeValue());
					pos[2] = Double.parseDouble(parallelLight.getElementsByTagName("direction").item(0).getAttributes().item(2).getNodeValue());
					Vec3 p = new Vec3(pos[0], pos[1], pos[2]);
					Light light = new Light(Light.DIRECTIONAL, p, color[0], color[1], color[2]);
					lightList.add(light);
					//System.out.println("Added ParallelLight!");
				}
			}
		}
		
		//Check for POINT LIGHT
		Node pointTest = this.doc.getElementsByTagName("point_light").item(0);
		if (pointTest != null){
			//There is an POINT light; Create new light and add to lightList
			NodeList pointLights = this.doc.getElementsByTagName("point_light");
			for (int i = 0; i < pointLights.getLength(); i++) {
				Node pointNode = pointLights.item (i);
				if (pointNode.getNodeType() == Node.ELEMENT_NODE) {
					Element pointLight = (Element) pointNode;
					
					//Set color and position of the parallel light
					float color[] = new float[3];
					color[0] = Float.parseFloat(pointLight.getElementsByTagName("color").item(0).getAttributes().item(0).getNodeValue());
					color[1] = Float.parseFloat(pointLight.getElementsByTagName("color").item(0).getAttributes().item(1).getNodeValue());
					color[2] = Float.parseFloat(pointLight.getElementsByTagName("color").item(0).getAttributes().item(2).getNodeValue());
					
					double pos[] = new double[3];
					pos[0] = Double.parseDouble(pointLight.getElementsByTagName("position").item(0).getAttributes().item(0).getNodeValue());
					pos[1] = Double.parseDouble(pointLight.getElementsByTagName("position").item(0).getAttributes().item(1).getNodeValue());
					pos[2] = Double.parseDouble(pointLight.getElementsByTagName("position").item(0).getAttributes().item(2).getNodeValue());
					Vec3 p = new Vec3(pos[0], pos[1], pos[2]);
					
					Light light = new Light(Light.POINT, p, color[0], color[1], color[2]);
					lightList.add(light);
					//System.out.println("Added PointLight!");
				}
			}
		}
		
		
	}
	
	
	
	/**
	 * Creates an Image with the desired background color
	 * @param r
	 * @param g
	 * @param b
	 */
	public void createImage(int r, int g, int b){
		this.img = new Image(cam.getWidth(), cam.getHeight());
		img.setFilename( root.getAttributes().item(0).getNodeValue() );
		img.fillImage(r,g,b);
		img.saveImage();
	}
	
	
	/**
	 * Creates an Image with the desired background color
	 * @param r
	 * @param g
	 * @param b
	 */
	public void createImageByXMLColor(Color background){
		this.img = new Image(cam.getWidth(), cam.getHeight());
		img.setFilename( root.getAttributes().item(0).getNodeValue() );
		//img.fillImage(r,g,b);
		img.saveImage();
	}

	/**
	 * Creates an Image by encoding in Ray coordinates
	 */
	public void raytrace(){
		
		this.img = new Image(cam.getWidth(), cam.getHeight());
		img.setFilename( root.getAttributes().item(0).getNodeValue() );
		Color color;
		float[] tmpColor;
		//----CREATE CAMERA
		//Get the distance lookat - eye Position
		Vec3 viewDirection = Vec3.subVec(cam.getLookat(), cam.getPosition());
        Vec3 right = Vec3.crossProduct(viewDirection, cam.getUp()); 
        //Tilt the up-Vector
        Vec3 upTilt = Vec3.crossProduct(right, viewDirection);  
        right.normalize();
        upTilt.normalize();
        
        
        
        //viewPlaneHalfWidth = tan(fieldofView / 2) * |LookAt - Position|
        double viewPlaneHalfWidth = Math.tan(Math.toRadians(cam.getHorizontal_fov()) ); 
        viewPlaneHalfWidth *= viewDirection.getMagnitude();	//Output: 5.669213166903515
        double aspectRatio = cam.getWidth() / cam.getHeight();
        double viewPlaneHalfHeight = aspectRatio * viewPlaneHalfWidth;
        
        //viewPlaneTopLeftPoint = lookatPoint + V*viewPlaneHalfHeight - U*viewPlaneHalfWidth
        Vec3 tmpHalfHeight = Vec3.multiplyScalar(upTilt,viewPlaneHalfHeight);
        Vec3 tmpHalfWidht = Vec3.multiplyScalar(right,viewPlaneHalfWidth);
        Vec3 viewPlaneTopLeftPoint = Vec3.addVec(cam.getLookat(), tmpHalfHeight);
        viewPlaneTopLeftPoint.sub(tmpHalfWidht);        
        
        //Pixel size
        //double pixelWidth = viewPlaneHalfWidth*2 / (double)cam.getWidth();
        double pixelHeight = viewPlaneHalfHeight*2 / (double) cam.getHeight();
        
        //Reset height
        viewPlaneTopLeftPoint.setY(viewPlaneTopLeftPoint.getY()-pixelHeight);
        
        //Calculate the increments Vectors
        //(U * 2 * halfWidth)  / width;
        Vec3 xIncVector = Vec3.multiplyScalar(right, 2);
        xIncVector.multiplyScalarThis(viewPlaneHalfWidth);
        xIncVector.divide(cam.getWidth());
        
        //(V * 2 * halfHeight) / height;
        Vec3 yIncVector = Vec3.multiplyScalar(upTilt, 2);
        yIncVector.multiplyScalarThis(viewPlaneHalfHeight);
        yIncVector.divide(cam.getHeight());
        
        //------RAYTRACING
		//Run throw bufferedImage
		for (int y=0; y<img.getWidth(); y++){
			for (int x=0; x<img.getHeight(); x++){
				this.ray = shootRay(x,y, xIncVector, yIncVector, viewPlaneTopLeftPoint);
				tmpColor = trace(this.ray, scene.getObjectList(), 1);	
				color = new Color(tmpColor[0], tmpColor[1], tmpColor[2]);
				img.setColorToPixelInt(x,y,color.getRed(), color.getGreen(), color.getBlue());
			}
		}
		
		System.out.println("Raytracing was successfull!");
		img.saveImage();
	}
	
	
	/**
	 * Converts the camera coordinates [x,y,z] into pixel coordinates [pixelX,pixelY,pixelZ]
	 * @param u
	 * @param v
	 * @return the ray with the converted camera coordinates
	 */
	public Ray shootRay(int x, int y, Vec3 xIncVector, Vec3 yIncVector, Vec3 viewPlaneTopLeftPoint){
		Ray result = new Ray();
		result.setOrigin(cam.getPosition());
	
        //viewPlanePoint = viewPlaneBottomLeftPoint + x*xIncVector - y*yIncVector
		Vec3 newXVec = Vec3.multiplyScalar(xIncVector, x);
		Vec3 newYVec = Vec3.multiplyScalar(yIncVector, y);
        Vec3 viewPlanePoint = Vec3.addVec(viewPlaneTopLeftPoint,newXVec);
        viewPlanePoint.sub(newYVec);
      
        Vec3 castRay = Vec3.subVec(viewPlanePoint,cam.getPosition());
        //System.out.println("newX: " + newXVec + ", newY: " + newYVec + "; castRay " + castRay.toString());
        castRay.normalize();
        
        //System.out.println("at " + "[" + x + "," + y + "]: " + castRay.toString());
		result.setDirection(castRay);
	    return result;
		
	}

	
	/**
	 * Find object intersection and shade it 
	 * @param ray
	 * @param objectlist
	 * @return the color depends on ray intersection
	 */
	public float[] trace(Ray ray, List<Shape> objectlist, int depth){
		boolean intersect = false;
		Shape obj = null;
		Sphere sphere = null;
		Triangle tri = null;
		double mindist = Double.MAX_VALUE;
		
		//System.out.println(depth);
		//---INTERSECTION TEST
		for(int s=0; s<objectlist.size(); s++){

			if(objectlist.get(s).RayIntersect(ray)){
				obj = objectlist.get(s);
				
				if(obj instanceof Sphere){
					//Remember the sphere with the closest intersection point
					if(mindist > obj.getT()){
						mindist = obj.getT();
						sphere = (Sphere) obj;
						intersect = true;
					}
				}else if(obj instanceof Triangle){
					tri = (Triangle) obj;
					intersect = true;
				}
			}
		}
		
		
		
		//---LIGHTING AND SHADING
		if(!intersect){
			float[] tmp = {scene.getR(), scene.getG(), scene.getB()};
			return tmp;
		}else{
			float[] overallColor = new float[3];
			Vec3 iP = null;
			Vec3 normal = null;
			Vec3 dirInverse =  Vec3.inverseVec(ray.getDirection());

			//---SET GEOMETRY
			//SPHERE
			if(sphere != null){
				//Intersection Point for Sphere: iP = o + mindist * d
				iP = Vec3.multiplyScalar(ray.getDirection(), sphere.getT());
				iP.add(ray.getOrigin());
				//Normal at iP = intersection point - middlepoint
				normal = Vec3.copy(iP);
				normal.sub(sphere.getMiddlepoint());
				normal.normalize();
				obj = sphere;

		    //TRIANGLE
			} else if(tri != null){
				//Intersection point for Triangle iP on the ray: P = O + t*D
				iP = Vec3.addVec(ray.getOrigin(), Vec3.multiplyScalar( ray.getDirection(), tri.getT() ) );
				normal = Vec3.copy(tri.getPlaneNormal());
				normal.normalize();
				obj = tri;
			}				

			float tmpR=0;
			float tmpG=0;
			float tmpB=0;
			float[] tmpArr = null;
			//Overall lightsources
        	for(int l=0; l<scene.getLightList().size(); l++){
        		Light light = scene.getLightList().get(l);
        		tmpArr = shade(iP, normal, dirInverse, light, obj);
        		tmpR += tmpArr[0];
        		tmpG += tmpArr[1];
        		tmpB += tmpArr[2];
        	}
        	
        	//Normalize Color: make sure the values are not higher than 1.0
        	tmpR = (tmpR > 1f) ? 1f : tmpR;
        	tmpG = (tmpG > 1f) ? 1f : tmpG;
        	tmpB = (tmpB > 1f) ? 1f : tmpB;
        	
        	overallColor[0] = tmpR;
        	overallColor[1] = tmpG;
        	overallColor[2] = tmpB;
        	
        	if(depth >= cam.getMax_bounce())
        		return overallColor;
        	
        	
			//REFLECTION
	        if (obj.getReflectance() > 0) {
	            double t = Vec3.dotProduct(dirInverse, normal);
	            if (t > 0) {
		            t *= 2;
		            //Reflected = N*2*(V*N)-V; V=viewDirection, 
			        double vDotN = Vec3.dotProduct(ray.getDirection(), normal);
			        Vec3 tmp = Vec3.multiplyScalar(normal, 2); 
			        tmp.multiplyScalarThis(vDotN);
			        //Add a tiny offset
			        Vec3 iPoffset = new Vec3(iP.getX() + TINY*tmp.getX(), iP.getY() + TINY*tmp.getY(), iP.getZ() + TINY*tmp.getZ());
		            Vec3 reflectDir = Vec3.subVec(tmp, ray.getDirection());
		            Ray reflectedRay = new Ray(iPoffset,reflectDir);
		            
		            if(hitObject(reflectedRay, 1000)){
			            float[] colorRefl = trace(reflectedRay, scene.getObjectList(), depth+1);
			            tmpR += obj.getReflectance()*colorRefl[0] ;
			            tmpG += obj.getReflectance()*colorRefl[1];
			            tmpB += obj.getReflectance()*colorRefl[2] ;
		            }else{
		            	tmpR += obj.getReflectance()*scene.getBackgroundColor().getRed();
		            	tmpG += obj.getReflectance()*scene.getBackgroundColor().getGreen();
		            	tmpB += obj.getReflectance()*scene.getBackgroundColor().getBlue();
		            }
	            }
	        }
	        
	        
	        if(ref){
			//REFRACTION
	        if (obj.getRefraction() > 0){
	        	boolean inside = false;
	        	float iof = obj.getRefraction();
	        	
	        	if(Vec3.dotProduct(ray.getDirection(), normal) > 0 )
	        		inside = true;
	        	
	        	//Check if inside or outside of object
	        	 double eta = (inside) ? iof : 1 / iof;
	        	
	        	 
	        	//IOF original medium = Air = 1
	        	//float iof = obj.getRefraction();
	        	//float n = 1 / iof;
	        	double c1 = - (Vec3.dotProduct(normal, ray.getDirection())); 
	        	//double c2 = Math.sqrt( 1 - Math.pow(n, 2) * (1 - Math.pow(c1,2)) );
	        	double k = 1 - eta * eta * (1 - c1 * c1); 
	        	
	        	
	        	//Rr = (n * V) + (n * c1 - c2) * N
	        	Vec3 refrdir = Vec3.multiplyScalar(ray.getDirection(), eta);
	        	double tmp2 = eta*c1 - Math.sqrt(k);
	        	Vec3 tmp3 = Vec3.multiplyScalar(normal, tmp2);
	        	refrdir.add(tmp3);
	            //Vec3 refrdir = raydir * eta + nhit * (eta *  c1 - sqrt(k)); 
	            refrdir.normalize();
	            Vec3 iPoffset = new Vec3(iP.getX() - normal.getX()*TINY,
        				iP.getY() - normal.getY()*TINY,
        				iP.getZ() - normal.getZ()*TINY);
	            Ray refractedRay = new Ray(iPoffset,refrdir);
	            float[] colorRefr = trace(refractedRay, scene.getObjectList(), depth+1);
	            tmpR += obj.getTransmittance()*colorRefr[0] ;
	            tmpG += obj.getTransmittance()*colorRefr[1];
	            tmpB += obj.getTransmittance()*colorRefr[2] ;
	        }
	        }
	        
        	//Normalize Color (1f = 1.0)
        	tmpR = (tmpR > 1f) ? 1f : tmpR;
        	tmpG = (tmpG > 1f) ? 1f : tmpG;
        	tmpB = (tmpB > 1f) ? 1f : tmpB;

			tmpArr[0] = tmpR;
			tmpArr[1] = tmpG;
			tmpArr[2] = tmpB;
			
	        return tmpArr;
		        
		}	
	}

	
	public boolean hitObject(Ray ray, double lengthLight){
		Shape obj = null;
		Vec3 iP = null;
		double lengthShadow =0;
		for(int s=0; s<scene.getObjectList().size(); s++){
			obj = scene.getObjectList().get(s);
				if(s != 7){
				if(obj.RayIntersect(ray)){
					if(obj instanceof Sphere){
						//Intersection Point for Sphere: iP = o + mindist * d
						Sphere sphere = (Sphere) obj;
						iP = Vec3.multiplyScalar(ray.getDirection(), sphere.getT());
						iP.add(ray.getOrigin());
					}else if(obj instanceof Triangle){
						Triangle tri = (Triangle) obj;
						//Intersection point for Triangle iP on the ray: P = O + t*D
						iP = Vec3.addVec(ray.getOrigin(), Vec3.multiplyScalar( ray.getDirection(), tri.getT() ) );
						
					}
					lengthShadow = iP.getMagnitude();

					if(lengthLight > lengthShadow)
						return true;
				}
				}
		}
		//No object in the way
		return false;
	}
	
	
	
	/**
	 * Determines the shading including the light sources
	 * @param p
	 * @param n
	 * @param v
	 * @param lights
	 * @param object
	 * @return the color components r,g,b as float[]
	 */
	 public float[] shade(Vec3 iP, Vec3 normal, Vec3 dirInverse, Light light, Shape object) {
		 	boolean shadowed = false;
	        float r = 0;
	        float g = 0;
	        float b = 0;
	        double lengthLight;
	        
	        	//AMBIENT LIGHT
	            if (light.lightType == Light.AMBIENT) {
	                r += ( object.getKa() * object.r * light.getIr()  );
	                g += ( object.getKa() * object.g * light.getIg()  );
	                b += ( object.getKa() * object.b * light.getIb()  );
	            } else {
	            //POINT OR PARALLEL LIGHT
	                Vec3 li = null;
	                if (light.lightType == Light.POINT) {
	                	//vecLight = lightPosition - intersection point
	                	li = Vec3.subVec(light.getPos(), iP);
	                	li.normalize();

	                } else {
	                	//Parallel Light
	                	li = light.getPos().inverse();
	                }

	                
	                //SHADOW
	                //Determine length of light.position - intersection point to make sure, the point isn't behind the light source
                	Vec3 lightVecLength = Vec3.subVec(light.getPos(), iP);
                	lengthLight = lightVecLength.getMagnitude();
                	
	                Vec3 iPoffset = new Vec3(iP.getX() + TINY*li.getX(), iP.getY() + TINY*li.getY(), iP.getZ() + TINY*li.getZ());
	                Ray shadowRay = new Ray(iPoffset, li);
	                //Checks if between the intersection point and the light is an object
	                if(hitObject(shadowRay, lengthLight))
	                	shadowed = true;
	                
             	
	                if(!shadowed || object instanceof Sphere){
		                //PHONG = AMBIENT + DIFFUSE + SPECULAR 
		                double lambert = Vec3.dotProduct(normal,li);
		                if (lambert > 0) {
		                	//Diffuse light
		                    if (object.getKd() > 0) {
		                        double diffuse = object.getKd()*lambert;
		                        r += diffuse*object.r*light.getIr();
		                        g += diffuse*object.g*light.getIg();
		                        b += diffuse*object.b*light.getIb();
		                        //System.out.println(" diffuse: [" + r + "," + g + "," + b + "]" );
		                    }
		                    //Specular light
		                    if (object.getKs() > 0) {
		                        lambert *= 2;
		                        Vec3 tmp = new Vec3(lambert*normal.getX() - li.getX(), lambert*normal.getY() - li.getY(), lambert*normal.getZ() - li.getZ());
		                        double spec = Vec3.dotProduct(tmp, dirInverse);
		                        if (spec > 0) {
		                            spec = (double)object.getKs()*(Math.pow((double) spec, (double) object.getExp()));
		                            r += spec*light.getIr();
		                            g += spec*light.getIg();
		                            b += spec*light.getIb();
		                        }
		                    }
		                }
	                }
	            }


	        float[] col = new float[3];
	        col[0] = r;
	        col[1] = g;
	        col[2] = b;
	        return col;
	    }

	
	
	
	
	/**
	 * Print the current Scene attributes in the console
	 */
	public void printXML(){
		
		System .out.println("++++++++++++++++++++ " + this.root.getNodeName() + " ++++++++++++++++++++++");
		Node XMLbackground = this.doc.getElementsByTagName("background_color").item(0);
		Element background = (Element) XMLbackground;
		System .out.print("Background Color: [" + background.getAttributes().item(0).getNodeValue() + ", ");
		System .out.print(background.getAttributes().item(1).getNodeValue() + ", ");
		System .out.print(background.getAttributes().item(2).getNodeValue() + "]; ");
		
		System.out.println("Output Filename: " + root.getAttributes().item(0).getNodeValue());
		
		//Access to the CAMERA attributes
		Node XMLcamera = this.doc.getElementsByTagName("camera").item(0);
		System.out.println(XMLcamera.getNodeName());
		System .out.println("----------------------------");
		Element camera = (Element) XMLcamera;
			//position [x,y,z]
			System .out.print("Position: [" + camera.getElementsByTagName("position").item(0).getAttributes().item(0).getNodeValue() + ", ");
			System .out.print(camera.getElementsByTagName("position").item(0).getAttributes().item(1).getNodeValue() + ", ");
			System .out.print(camera.getElementsByTagName("position").item(0).getAttributes().item(2).getNodeValue() + "];  ");
			//lookat [x,y,z]
			System .out.print("Look_at: [" + camera.getElementsByTagName("lookat").item(0).getAttributes().item(0).getNodeValue() + ", ");
			System .out.print(camera.getElementsByTagName("lookat").item(0).getAttributes().item(1).getNodeValue() + ", ");
			System .out.println(camera.getElementsByTagName("lookat").item(0).getAttributes().item(2).getNodeValue() + "];  ");
			//horizontal fov
			System .out.print("Horizontal_Fov: " + camera.getElementsByTagName("horizontal_fov").item(0).getAttributes().item(0).getNodeValue() + ";  ");
			System .out.println("Max_Bounces: " + camera.getElementsByTagName("max_bounces").item(0).getAttributes().item(0).getNodeValue());
			//Resolution
			System .out.print("Resolution: " + camera.getElementsByTagName("resolution").item(0).getAttributes().item(0).getNodeValue() + ", ");
			System .out.println(camera.getElementsByTagName("resolution").item(0).getAttributes().item(1).getNodeValue());
			System .out.println("");
			
			
			
		//Access to the LIGHT attributes
		System.out.println("Lights " + scene.getLightList().size() +"x");
		System .out.println("----------------------------");
		for(int i=0; i<scene.getLightList().size(); i++){
			Light light = scene.getLightList().get(i);
			if(light.lightType == Light.AMBIENT)
				System.out.println("Type: Ambient Light");
			if(light.lightType == Light.DIRECTIONAL)
				System.out.println("Type: Parallel Light");
			if(light.lightType == Light.POINT)
				System.out.println("Type: Point Light");
			
			System.out.println("Color: " + "r=" + light.getIr() + ", g=" + light.getIg() + ", b=" + light.getIb());
			
			if(light.lightType == Light.DIRECTIONAL)
				System.out.println("Direction: " + light.getPos().toString());
			if(light.lightType == Light.POINT)
				System.out.println("Position: " + light.getPos().toString());
			
			System.out.println("");
		}
		System.out.println("");
			
			
		//Access to the SURFACES and attributes
		System.out.println("Objects " + scene.getObjectList().size() +"x");
		System .out.println("----------------------------");
		for(int i=0; i<scene.getObjectList().size(); i++){
			Shape obj = scene.getObjectList().get(i);
			System.out.print("Type: " + obj.name);
			if(obj instanceof Triangle)
				System.out.println(", Triangle");
			System.out.println("Color: " + obj.getColor().toString() + "; float: [" + obj.r + "," + obj.g + "," + obj.b + "]");
			
			if(obj instanceof Sphere){
				Sphere s = (Sphere) obj;
				System.out.println("Radius: " + s.getRadius());
				System.out.println("Origin: " + s.getMiddlepoint().toString());
				System.out.println("index of refraction: " + s.getRefraction());
			}else if(obj instanceof Triangle){
				Triangle tri = (Triangle) obj;
				System.out.println("Textur: " + tri.getTextureName());
				System.out.println("Vertices: " + tri.getV1() + "," + tri.getV2() + "," + tri.getV3());
				System.out.println("Texture coord.: " + tri.getVt1() + "," + tri.getVt2() + "," + tri.getVt3());
				System.out.println("Normals: " + tri.getVn1() + "," + tri.getVn2() + "," + tri.getVn3());
				
				
			}
			System.out.println("Phong: ka=" + obj.getKa() + ", kd=" + obj.getKd() + ", ks=" + obj.getKs() + ", exp=" + obj.getExp());
			
			System.out.println("");
		}
		System.out.println("");
		System .out.println("++++++++++++++++++++++++++++++++++++++++++++++++");
		System .out.println("\n");
	}
	
	
	
	
	/**
	 * Provides an user input interface
	 */
	public void inputHandler(){
		
		try {

			while (!input.equals("q")) {

				System.out.println("What you wanna do? ");
				System.out.println("---Hint: 'readme.txt' in project folder---");
				System.out.println("1: Start (Load XML File)");
				System.out.println("2: Print Scene attributes");
				System.out.println("3: RAYTRACE and create image");
				System.out.println("4: RAYTRACE with Refraction (experimental)");
				System.out.println("5: Create a simple background image");
				System.out.println("6: Raycasting Test: Create debug image (r=x,g=y,b=z)");
				System.out.println("7: Exit");
				System.out.println("");
				int menu = (scInt.nextInt());

				switch (menu) {

				//Choose XML File
				case 1:
					lightList.clear();
					scene = null;
					String input2 = "";
					Scanner scInt2 = new Scanner(System.in);
					try {
						while (!input2.equals("q")) {
	
							System.out.println("Choose a file: ");
							System.out.println("1: Example1");
							System.out.println("2: Example2");
							System.out.println("3: Example3");
							System.out.println("4: Example4");
							System.out.println("5: Example5");
							System.out.println("6: Example6");
							System.out.println("7: Back");
							System.out.println("");
							int menu2 = (scInt2.nextInt());
	
							switch (menu2) {
							
							//*******Change directories on Linux System: exampleX.xml; Directories in Windows System: src/exampleX.xml********
							case 1:
								this.inputFile = new File("example1.xml");
								xmlDomParser();
								System.out.println("Example1 selected!\n");
								input2 = "q";
								break;
							case 2:
								this.inputFile = new File("example2.xml");
								xmlDomParser();
								System.out.println("Example2 selected!\n");
								input2 = "q";
								break;
							case 3:
								this.inputFile = new File("example3.xml");
								xmlDomParser();
								System.out.println("Example3 selected!\n");
								input2 = "q";
								break;
							case 4:
								this.inputFile = new File("example4.xml");
								xmlDomParser();
								System.out.println("Example4 selected!\n");
								input2 = "q";
								break;
							case 5:
								this.inputFile = new File("example5.xml");
								xmlDomParser();
								System.out.println("Example5 selected!\n");
								input2 = "q";
								break;
							case 6:
								this.inputFile = new File("example6.xml");
								xmlDomParser();
								System.out.println("Example6 selected!\n");
								input2 = "q";
								break;
							case 7:
								input2 = "q";
								break;
							default:
								System.out.println("Wrong input!");
								break;
							}
						}
					} catch (Exception ex) {
						System.out.println("Wrong Input: " + "\n" + ex);
					}
					
					break;
				case 2:
					if(scene != null)
						printXML();
					else
						System.out.println("Choose an XML first! (1) \n");
					break;
				case 3:
					if(scene != null)
						raytrace();
					else
						System.out.println("Choose an XML first! (1)\n");
					break;
				case 4:
					if(scene != null){
						ref = true;
						raytrace();
						ref = false;
					}else
						System.out.println("Choose an XML first! (1)\n");
					break;
				case 5:
					String input3 = "";
					Scanner scInt3 = new Scanner(System.in);
					if(scene != null){
						try {
							while (!input3.equals("q")) {
		
								System.out.println("Which background color would you like?");
								System.out.println("1: Default from XML File");
								System.out.println("2: Choose an color");
								System.out.println("3: Back");
								System.out.println("");
								int menu3 = (scInt3.nextInt());
								int r = 0;
								int g = 0;
								int b = 0;
								
								switch (menu3) {
								case 1:
									Node XMLbackground = this.doc.getElementsByTagName("background_color").item(0);
									Element background = (Element) XMLbackground;
									r = (int) Double.parseDouble(background.getAttributes().item(0).getNodeValue());
									g = (int) Double.parseDouble(background.getAttributes().item(1).getNodeValue());
									b = (int) Double.parseDouble(background.getAttributes().item(2).getNodeValue());
									createImage(r,g,b);
									input3 = "q";
									break;
								case 2:
									System.out.println("Define red, green and blue (0-255|black=0,0,0): ");
									r = scInput.nextInt();
									g = scInput.nextInt();
									b = scInput.nextInt();
									if(! (r < 0 || g < 0 || b < 0 || r > 255 || g > 255 || b > 255) ) {
										createImage(r,g,b);
									}else{
										System.out.println("Please choose a number between 0 and 255!" + "\n");
										break;
									}
									input3 = "q";
									break;
								case 3:
									input3 = "q";
									break;
								default:
									System.out.println("Wrong input!");
									break;
								}
							}
						} catch (Exception ex) {
							System.out.println("Wrong Input: " + ex);
						}
						
							
					}else
						System.out.println("Choose an XML first! (1)\n");
					break;
				case 6:
					if(scene != null)
						convertXYZInRGB();
					else
						System.out.println("Choose an XML first! (1)\n");
					break;
				case 7:
					System.out.println("Program terminated.");
					return;
				default:
					System.out.println("Wrong input!");
					break;

				}
			}
		} catch (Exception ex) {
			System.out.println("Wrong Input: " + ex);
		}

	}
		
	
	
	
	/**
	 * Program start here
	 * @param args
	 */
	public static void main(String[] args) {
		Raytracer rt = new Raytracer();
		rt.inputHandler();
	}



	public File getOutputFile() {
		return outputFile;
	}



	public void setOutputFile(File outputFile) {
		this.outputFile = outputFile;
	}



	 /** Creates an Ray on the pixel coordinates (r=u,g=v,b=z)
	 * @param u pixel coordinate x
	 * @param v pixel coordinate y
	 * @return
	 */
	public void convertXYZInRGBSphere(){
		Vec3 vec = new Vec3(0,0,-3);
		Sphere s = new Sphere(1,vec);
//		Sphere s = (Sphere) scene.getObjectList().get(0);
//		s.print();
//		Vec3 o = new Vec3(0,0,-1);
//		Vec3 d = new Vec3(-2,0.2,-1);
//		Ray r = new Ray(o,d);

		this.img = new Image(cam.getWidth(), cam.getHeight());
		img.setFilename( root.getAttributes().item(0).getNodeValue() );
		
		for (int v=0; v<cam.getHeight(); v++){
			for (int u=0; u<cam.getWidth(); u++){
				this.ray = shootRayTest(u,v);
				
				
				if(s.RayIntersect(this.ray)){
					System.out.println("Ray: x="+ray.getDirection().getX() + ", y=" +ray.getDirection().getY() + ", z=" +ray.getDirection().getZ());
					System.out.println("Ray: x="+ray.getDirection().getX() + ", y=" +ray.getDirection().getY() + ", z=" +ray.getDirection().getZ());
					img.setColorToPixelInt(u,v,255,255,255);
				}
				else
					img.setColorToPixel(u,v,ray.getDirection().getX(),ray.getDirection().getY(), 1);
				
			}
		}
		
		System.out.println("Ray casting was successfull!");
		img.saveImage();
	}


	
	
	
	public Ray shootRayTest(double u, double v){

		double ratio = (double)cam.getWidth()/(double)cam.getHeight();
		Ray result = new Ray();
		result.setOrigin(cam.getPosition());

		//System.out.println("fovX: " + fovX + ", " + "fovY: " + fovY + "; ratio: " + ratio);

		//---Create coordinates
		double newX =  (double) ratio * (( (double)(2*u- (double)cam.getWidth()) / (double)cam.getWidth() ) * (double)(Math.tan(fovX)));
		double newY =  (double) ratio * (( (double)(2*v- (double)cam.getHeight())/ (double)cam.getHeight()) * (double)(Math.tan(fovY)));
		
		//---Normalize
		//instead of -1 until 1, the values become 0 - 1 (for one quadrant; no quadrant check yet!)
		if(newX < 0)
			newX = 1-Math.abs(newX);
		if(newY < 0)
			newY = 1-Math.abs(newY);
		
		double normX;
		double normY;
		
		//Check if the current pixel (u,v) is on left/right or up/down side
		if(u >= cam.getWidth()/2)
			//pixel is on the right part
			normX = newX * ( (double)cam.getWidth()/(double)2) + (double)cam.getWidth()/(double)2;
		else
			//pixel is on the left part
			normX = newX * ( (double)cam.getHeight()/(double)2);
		
		if(v >= cam.getHeight()/2)
			//pixel is on the lower part 
			normY = newY * ( (double)cam.getHeight()/(double)2) + (double)cam.getHeight()/(double)2;
		else
			//pixel is on the higher part
			normY = newY * ( (double)cam.getHeight()/(double)2);
		
		//Coordinates now from 0 to width or 0 to height (start from topleft)
		
		//result.getDirection().normalize();
		Vec3 direction = new Vec3(normX,normY, -1);
		result.setDirection(direction);
				
	    return result;
		
	}
	
	
	/**
	 * Creates an Ray on the pixel coordinates (r=u,g=v,b=z)
	 * @param u pixel coordinate x
	 * @param v pixel coordinate y
	 * @return
	 */
	public void convertXYZInRGB(){
		this.img = new Image(cam.getWidth(), cam.getHeight());
		img.setFilename( root.getAttributes().item(0).getNodeValue() );
		
		for (int v=0; v<cam.getHeight(); v++){
			for (int u=0; u<cam.getWidth(); u++){
				this.ray = shootRayTest(u,v);
				img.setColorToPixel(u,v,ray.getDirection().getX(),ray.getDirection().getY(), 1);
				
			}
		}
		
		System.out.println("Ray casting was successfull!");
		img.saveImage();
	}
	
	
	
	
	

	public List<Light> getLightList() {
		return lightList;
	}



	public void setLightList(List<Light> lightList) {
		this.lightList = lightList;
	}



	public List<Model> getModelList() {
		return modelList;
	}



	public void setModelList(List<Model> modelList) {
		this.modelList = modelList;
	}



	public List<Mesh> getMeshList() {
		return meshList;
	}



	public void setMeshList(List<Mesh> meshList) {
		this.meshList = meshList;
	}


}

