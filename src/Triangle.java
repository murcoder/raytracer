
/**
 * 
 * @author Christoph Murauer - 1127084
 *
 */
public class Triangle extends Shape {

	private final static double EPSILON = 0.00001;
	private final Vec3 v1, v2, v3;
	private final Vec3 vn1, vn2, vn3;
	private final Vec3 vt1, vt2, vt3;
	private final Plane plane;
	double u;
	private double v;
	private Vec3 uVec, vVec;
	private Vec3 planeNormal;
	private Ray hitray;
	private String textureName;
	private double t;
	
	//variables for calculating intersection point
	private double D;
	private double distance;
	
	
	Triangle(){
		v1=null;v2=null;v3=null;
		vn1=null;vn2=null;vn3=null;
		vt1=null;vt2=null;vt3=null;
		u=0;v=0;
		hitray=null;
		textureName="";
		t=0;
		plane = null;
		setPlaneNormal(null); uVec=null; vVec=null;
		D = 0;
		setDistance(0);
	}
	
	Triangle(Vec3 v1, Vec3 v2, Vec3 v3, Vec3 vn1, Vec3 vn2, Vec3 vn3, Vec3 vt1, Vec3 vt2, Vec3 vt3) {
		//Set vertices
		this.v1 = v1;
		this.v2 = v2;
		this.v3 = v3;

		//Set normal vertices
		this.vn1 = vn1;
		this.vn2 = vn2;
		this.vn3 = vn3;
		
		//Set texture vertices
		this.vt1 = vt1;
		this.vt2 = vt2;
		this.vt3 = vt3;
		hitray = null;
		
		//Determine distances between the vertices and get the edges
		this.uVec = Vec3.subVec(v2, v1);
		this.vVec = Vec3.subVec(v3, v1);
		
		//Set a plane
		this.setPlaneNormal(Vec3.crossProduct(uVec, vVec)); //normal = cross(edge1,edge2)
		planeNormal.normalize();
		double a = planeNormal.getX();
		double b = planeNormal.getY();
		double c = planeNormal.getZ();
		double d = v1.getX() * planeNormal.getX() + v1.getY() * planeNormal.getY() + v1.getZ() * planeNormal.getZ();
		this.plane = new Plane(a, b, c, -d);
		
		D = Vec3.dotProduct(planeNormal, v1); 
		setT(0);
		setDistance(0);
	}
	

//	public boolean RayIntersect(Ray ray){
//
//		
//		Vec3 origin = new Vec3(0,0,0);
//		Vec3 v1 = Vec3.subVec(this.v1, ray.getOrigin());
//		Vec3 v2 = Vec3.subVec(this.v2, ray.getOrigin());
//		Vec3 v3 = Vec3.subVec(this.v3, ray.getOrigin());
//		
//			    Vec3 v1v2 = Vec3.subVec(v2,v1); 
//			    Vec3 v1v3 = Vec3.subVec(v3,v1); 
//			    Vec3 pvec = Vec3.crossProduct(ray.getDirection(), v1v2); 
//			    double det =  Vec3.dotProduct(v1v2,pvec); 
//
//			    // if the determinant is negative the triangle is backfacing
//			    // if the determinant is close to 0, the ray misses the triangle
//			    if (det < EPSILON) return false; 
//			    else 
//			    // ray and triangle are parallel if det is close to 0
//			    if (Math.abs(det) < EPSILON) return false; 
//
//			    double invDet = 1.0 / det; 
//			 
//			    Vec3 tvec = Vec3.subVec(origin,v1); 
//			    u = Vec3.dotProduct(tvec,pvec);
//			    u *= invDet;
//			    if (u < 0 || u > 1) return false; 
//			 
//			    Vec3 qvec = Vec3.crossProduct(tvec,v1v2); 
//			    v = Vec3.dotProduct(ray.getDirection(),qvec);
//			    v *= invDet;
//			    if (v < 0 || u + v > 1) return false; 
//
//
//			    t = Vec3.dotProduct(v1v3,qvec);
//			    t *= invDet;
//			 
//			    return true; 
//}

	
	
	/**
	 * Check for triangle intersections
	 * Moeller-Trumbore Algorithm: https://en.wikipedia.org/wiki/M%C3%B6ller%E2%80%93Trumbore_intersection_algorithm
	 * nX = nV1;  X=P+t*dir
	 * @param v1
	 * @param v2
	 * @param v3
	 * @param orig
	 * @param dir
	 * @param out
	 * @return
	 */
	public boolean RayIntersect(Ray ray){
				Vec3 P, Q, T;
				float det, detInvers;
				double t;
				
				
//				Vec3 v1 = Vec3.subVec(this.v1, ray.getOrigin());
//				Vec3 v2 = Vec3.subVec(this.v2, ray.getOrigin());
//				Vec3 v3 = Vec3.subVec(this.v3, ray.getOrigin());
				
				//Begin calculating determinant - also used to calculate u parameter
				//uVec = edge of (v2,v1), vVec = edge of (v3,v1)
				P = Vec3.crossProduct(ray.getDirection(), vVec);
				
				
				//if determinant is near zero, ray lies in plane of triangle
				det = (float) Vec3.dotProduct(uVec, P);
				//NOT CULLING
				if(det > 1-EPSILON && det < EPSILON) 
					return false;
				
				detInvers = 1.f / det;
				
				//Vec3 offset = new Vec3(ray.getOrigin().getX()*EPSILON, ray.getOrigin().getY()*EPSILON, ray.getOrigin().getZ()*EPSILON);
				//calculate distance from ORIGIN to V1
				T = Vec3.subVec(ray.getOrigin(), v1);
				
				//Calculate u parameter and test bound
				u = (float) (Vec3.dotProduct(T, P) * detInvers);
				//The intersection lies outside of the triangle
				if(u < 0.f || u > 1.f) 
					return false;
				
				//Prepare to test v parameter
				Q = Vec3.crossProduct(T, uVec);
				
				//Calculate V parameter and test bound
				v = (float) (Vec3.dotProduct(ray.getDirection(), Q) * detInvers);
				//The intersection lies outside of the triangle
				if(v < 0.f || u + v  > 1.f) 
					return false;
				
				//Combine plane equation with ray equation: t = (N(A,B,C)*O*D) / N(A,B,C)*R
				t = (Vec3.dotProduct(planeNormal, ray.getOrigin()) + D) / (Vec3.dotProduct(planeNormal, ray.getDirection()));
				//Alternative: t = Vec3.dotProduct(vVec, Q) * detInvers;
				this.t = t;
				
			
				if(t > EPSILON) {
					//Intersect! t > 0
					return true;
				}
				
				return false;
	}
	
	
	
	public Vec3 getV2() {
		return v2;
	}

	public Vec3 getV3() {
		return v3;
	}

	public Ray getHitray() {
		return hitray;
	}

	public void setHitray(Ray hitray) {
		this.hitray = hitray;
	}

	public String getTextureName() {
		return textureName;
	}

	public void setTextureName(String textureName) {
		this.textureName = textureName;
	}

	public double getT() {
		return t;
	}

	public void setT(double t) {
		this.t = t;
	}

	public Vec3 getV1() {
		return v1;
	}

	public Vec3 getVn1() {
		return vn1;
	}


	public Vec3 getVn2() {
		return vn2;
	}


	public Vec3 getVn3() {
		return vn3;
	}

	public Vec3 getVt1() {
		return vt1;
	}

	public Vec3 getVt2() {
		return vt2;
	}

	public Vec3 getVt3() {
		return vt3;
	}

	public Vec3 getuVec() {
		return uVec;
	}

	public void setuVec(Vec3 uVec) {
		this.uVec = uVec;
	}

	public Vec3 getvVec() {
		return vVec;
	}

	public void setvVec(Vec3 vVec) {
		this.vVec = vVec;
	}

	public Plane getPlane() {
		return plane;
	}



	public Vec3 getPlaneNormal() {
		return planeNormal;
	}

	public void setPlaneNormal(Vec3 planeNormal) {
		this.planeNormal = planeNormal;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}



	
	
	

}
