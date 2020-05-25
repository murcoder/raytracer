/**
 * Representation of an three dimensional Vector
 * including the operations dot- and cross product
 * @author Christoph Murauer - a1127084 (Source: http://r3dux.org/2012/12/vec3-a-simple-vector-class-in-c/)
 *
 */
public class Vec3 {

	
	
	private double x,y,z;

	
	
	
	
	// -----------   Constructors -------------	
	Vec3(){
		setX(0); setY(0); setZ(0);
	}
	
	
	Vec3(double x, double y, double z)
    {
        this.setX(x);
        this.setY(y);
        this.setZ(z);
    }

	Vec3(Vertex v) {
		this(v.getX(), v.getY(), v.getZ());
	}
	
	
	/**
	 * Create a new vector from point1 to point2.
	 * @param from
	 * @param to
	 */
	Vec3(Vertex from, Vertex to) {
		this(to.getX() - from.getX(), to.getY() - from.getY(), to.getZ() - from.getZ());
	}
	
	
	
	// -----------  Operations  -------------
	
	/**
	 * Calculate the magnitude of the current vector ||v||
	 * @return the magnitude
	 */
	public double getMagnitude(){
		
		return (double) Math.sqrt((x*x) + (y*y) + (z*z));
	}
	
	public void negate(){
		multiplyScalarThis(-1);
	}
	
	
	/**
	 * Normalize the current vector
	 */
	public void normalize() {
		
        // Calculate the magnitude of our vector
        double magnitude = getMagnitude();

        // As long as the magnitude isn't zero, divide each element by the magnitude
        // to get the normalised value between -1 and +1
        if (magnitude != 0)
        {
            x /= magnitude;
            y /= magnitude;
            z /= magnitude;
        }
    } 
	
	public Vec3 inverse(){
		Vec3 result = multiplyScalar(this, -1);
		return result;
	}
	

	
    /** Non-static method to calculate and return the scalar dot product of this vector and another vector
    /*
     * Usage example: double foo = vectorA.dotProduct(vectorB);
     */ 
    public double dotProduct(Vec3 vec){
    	
        return ((x * vec.x) + (y * vec.y) + (z * vec.z));
    }
	
    /** Non-static method to calculate and return the scalar dot product of this vector with itself
    /*
     * Usage example: double foo = vectorA.dotProduct(vectorB);
     */ 
    public double dotDot(){
    	
        return ((x * x) + (y * y) + (z * z));
    }
    
    
    
    /**
     * Additions
     */
    public void addX(double value) { x += value; }
    public void addY(double value) { y += value; }
    public void addZ(double value) { z += value; }
    
    
    /**
     *  Print the vector in console
     */
    void print(){
        System.out.println(" [" + x + "," + y + "," + z + "] ");
    }

    
    // -------------- Static vector operations -------------
    
    public static Vec3 copy(Vec3 orig){
    	Vec3 copy = new Vec3(orig.getX(), orig.getY(), orig.getZ());
    	return copy;
    }
    
	public static Vec3 inverseVec(Vec3 v){
		return new Vec3(-1*v.getX(),-1*v.getY(),-1*v.getZ());
	}
    
    
    
	/** Static method to calculate and return the scalar dot product of two vectors
    *
    * Note: The dot product of two vectors tell us things about the angle between
    * the vectors. That is, it tells us if they are pointing in the same direction
    * (i.e. are they parallel? If so, the dot product will be 1), or if they're
    * perpendicular (i.e. at 90 degrees to each other) the dot product will be 0,
    * or if they're pointing in opposite directions then the dot product will be -1.
    *
    * Usage example: double foo = Vec3<double>::dotProduct(vectorA, vectorB);
    * @return
    */
   public static double dotProduct(Vec3 vec1, Vec3 vec2){
   	
       return ( (vec1.x * vec2.x) + (vec1.y * vec2.y) + (vec1.z * vec2.z));
   }
    
    
    /** Static method to calculate and return a vector which is the cross product of two vectors
    *
    * Note: The cross product is simply a vector which is perpendicular to the plane formed by
    * the first two vectors. Think of a desk like the one your laptop or keyboard is sitting on.
    * If you put one pencil pointing directly away from you, and then another pencil pointing to the
    * right so they form a "L" shape, the vector perpendicular to the plane made by these two pencils
    * points directly upwards.
    *
    * Whether the vector is perpendicularly pointing "up" or "down" depends on the "handedness" of the
    * coordinate system that you're using.
    *
    * Further reading: http://en.wikipedia.org/wiki/Cross_product
    *
    * Usage example: Vec3<double> crossVect = Vec3<double>::crossProduct(vectorA, vectorB);
    */ 
    static public Vec3 crossProduct(Vec3 vec1, Vec3 vec2)
    {
    	Vec3 result = new Vec3( (vec1.y * vec2.z - vec1.z * vec2.y), (vec1.z * vec2.x - vec1.x * vec2.z), (vec1.x * vec2.y - vec1.y * vec2.x));
        return result;
    }
    
    
    
    /**
     *  Method to return the distance between two vectors in 3D space
     * @param v1
     * @param v2
     * @return
     */
    public static double getDistance(Vec3 v1, Vec3 v2) {
        double dx = v2.x - v1.x;
        double dy = v2.y - v1.y;
        double dz = v2.z - v1.z;

        return (double) Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
    
    
    
    /**
     *  Static vector addition operation to add Vec3s together
     */
    public static Vec3 addVec(Vec3 v1, Vec3 v2){
    	Vec3 result = new Vec3((v1.x + v2.x), (v1.y + v2.y), (v1.z + v2.z));
        return result;
    }


    /**
     *  Static vector subtraction operation to subtract a Vec3 from another Vec3
     */
    public static Vec3 subVec(Vec3 v1, Vec3 v2){
    	Vec3 result = new Vec3((v1.x - v2.x), (v1.y - v2.y), (v1.z - v2.z));
        return result;
    }


    /**
     *  Static vector multiplication operation to multiply two Vec3s together
     */
    public static Vec3 multiplyVec(Vec3 v1, Vec3 v2){
    	Vec3 result = new Vec3((v1.x * v2.x), (v1.y * v2.y), (v1.z * v2.z));
        return result;
    }

    /**
     *  Static scalar multiplication operation to multiply a vector by a scalar
     */
    public static Vec3 multiplyScalar(Vec3 vec, double value) {
    	Vec3 result = new Vec3((vec.x * value), (vec.y * value), (vec.z * value));
        return result;
    }

    /**
     *  Static vector division operation to divide a vector by a scalar
     */
    public static Vec3 divideVec(Vec3 vec, double value) {
    	Vec3 result = new Vec3((vec.x / value), (vec.y / value), (vec.z / value));
        return result;
    }


    
 // -------------- Nonstatic vector operations -------------
    /**
     *  Static vector addition operation to add Vec3s together
     */
    public void add(Vec3 vec){
    	this.x += vec.x;
    	this.y += vec.y;
    	this.z += vec.z;
    }


    /**
     *  Static vector subtraction operation to subtract a Vec3 from another Vec3
     */
    public void sub(Vec3 vec){
    	this.x -= vec.x;
    	this.y -= vec.y;
    	this.z -= vec.z;
    }


    /**
     *  Static vector multiplication operation to multiply two Vec3s together
     */
    public void multiplyVectorThis(Vec3 vec){
    	this.x *= vec.x;
    	this.y *= vec.y;
    	this.z *= vec.z;
    }

    /**
     *  Static scalar multiplication operation to multiply a vector by a scalar
     */
    public void multiplyScalarThis(double value) {
    	this.x *= value;
    	this.y *= value;
    	this.z *= value;
    }

    /**
     *  Static vector division operation to divide a vector by a scalar
     */
    public void divide(double value) {
    	this.x /= value;
    	this.y /= value;
    	this.z /= value;
    }
    
    
    
	
	// -----------   Getter and Setter -------------
	
	
	public void setVec3(double x, double y, double z){
        this.setX(x);
        this.setY(y);
        this.setZ(z);
	}
	
	public String toString(){
		String result = "[" + x + "," + y + "," + z + "]";
		return result;
		
		
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
