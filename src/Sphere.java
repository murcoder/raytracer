

/**
 * Defines a sphere which is an child of shape
 * @author Christoph Murauer - a1127084
 *
 */
public class Sphere extends Shape {

	
	private double radius;
	private Vec3 middlepoint;
	
	//Solutions of the quadratic equation; 
	private double solutionMax;
	private double solutionMin;
	private double t;
	
	public Sphere() {
		radius = 0.0;
		setSolutionMax(0.0);
		setSolutionMin(0.0);
		setMiddlepoint(new Vec3());
		t=Double.MAX_VALUE;
	}

	
	public Sphere(double radius, Vec3 middlepoint) {
		this.radius = radius;
		this.middlepoint = middlepoint;
		setSolutionMax(0.0);
		setSolutionMin(0.0);
		t=Double.MAX_VALUE;
	}

	
	/**
	 * Check for intersections with the sphere
	 */
	@Override
	public boolean RayIntersect( Ray ray ){
	    double boundingSquare = radius * radius ;
	    double a, b, c;
	    
	    //a = d^2 - Dot product with itself
	    a = ray.getDirection().dotDot() ;
	    
	    //b = (2*O*d - 2*C*d) C=middlepoint
	    double b1 = 2 * (ray.getOrigin().dotProduct(ray.getDirection()));
	    double b2 = 2 * (this.middlepoint.dotProduct(ray.getDirection()));
	    b = b1 - b2; 
	    //c = (O-C)^2 -r^2
	    Vec3 c1 = Vec3.subVec(ray.getOrigin(), this.middlepoint);
	    double c2 = c1.dotDot();
	    c = c2 - boundingSquare;
	    
	    int solutions = quadraticEquation(a, b, c);
	 
	    if (solutions > 0){
	    	//There is at least one solution
			double hitdist = 0.0;
			if(getSolutionMin() > 0.0)	//solutions by default 0.0
				hitdist = getSolutionMin();
			else 
				hitdist = getSolutionMax();

				this.t = hitdist;
	    	//this.t = getSolutionMin();
	        return true; 
	    }else
	        return false; // no solutions therefore no intersection
	}
	
	
	//------------------------------>INTERSECTION TEST AND QUADRATIC EQUATION
	/**
	 * Look how many solutions for the quadratic equation exists
	 * @param a
	 * @param b
	 * @param c
	 * @return 0=no solutions; 1=one solution; 2=two solutions
	 */
	public int quadraticEquation(double a, double b, double c){
		
		double disc = b*b - 4*a*c;
		
		if(disc < 0.0 ){
			setSolutionMax(-1);
			setSolutionMin(-1);
			return 0;
		}
		if(disc == 0.0){
			solutionMax = quadraticEquationRoot1(a,b,c);	//max
			solutionMin = quadraticEquationRoot2(a,b,c);	//min
			return 1;
		}
		if(disc > 0.0){
			solutionMax = quadraticEquationRoot1(a,b,c);	//max
			solutionMin = quadraticEquationRoot2(a,b,c);	//min
			return 2;
		}
			
		return 0;
	}
	
	
	
	/**
	 * Calculate the Quadratic Equation
	 * @param a
	 * @param b
	 * @param c
	 * @return the max of the two roots, if there are two
	 */
	public static  double quadraticEquationRoot1(double a, double b, double c){    
	    double root1, root2;
	    root1 = (-b + Math.sqrt(b*b - 4*a*c)) / (2*a);
	    root2 = (-b - Math.sqrt(b*b - 4*a*c)) / (2*a);
	    return Math.max(root1, root2);  
	}

	/**
	 * Calculate the Quadratic Equation
	 * @param a
	 * @param b
	 * @param c
	 * @return the min of the two roots, if there are two
	 */
	public static double quadraticEquationRoot2(double a, double b, double c){    
		double root1, root2; 
	    root1 = (-b + Math.sqrt(b*b - 4*a*c)) / (2*a);
	    root2 = (-b - Math.sqrt(b*b - 4*a*c)) / (2*a);
	    return Math.min(root1, root2);
	}
	
	/**
	 * Check for intersections with the sphere
	 */
	public boolean RayIntersectSphereTest( Ray ray )
	{
	    double boundingSquare = radius * radius ;
	    //ray.getDirection().print();
	    double a, b, c;
	    //a = d^2 - Dot product with itself
	    a = ray.getDirection().dotDot() ;
	    
	    //b = (2Od-2Cd)
	    double b1 = 2 * ray.getOrigin().dotProduct(ray.getDirection());
	    double b2 = 2 * this.middlepoint.dotProduct(ray.getDirection());
	    b = b1 - b2; 
	    //c = (O-C)^2 -r^2
	    Vec3 c1 = Vec3.subVec(ray.getOrigin(), this.middlepoint);
	    double c2 = c1.dotDot();
	    c = c2 - boundingSquare;
	    
	    System.out.println("Determine a,b,c for quadratic equation = a:" + a + ", b:" + b + ",c:" + c);
	    int solutions = quadraticEquation(a, b, c);
	 
	    if (solutions > 0)
	        return true; 
	    else
	        return false; // no solutions therefore no intersection
	}
	
	


	
	
	//------------------------------>GETTER AND SETTER
	
	
	public Vec3 getMiddlepoint() {
		return middlepoint;
	}

	public void setMiddlepoint(Vec3 middlepoint) {
		this.middlepoint = middlepoint;
	}
	
	
	public double getRadius() {
		return radius;
	}



	public void setRadius(double radius) {
		this.radius = radius;
	}


	public double getSolutionMax() {
		return solutionMax;
	}

	public void setSolutionMax(double solutionMax) {
		this.solutionMax = solutionMax;
	}

	public double getSolutionMin() {
		return solutionMin;
	}

	public void setSolutionMin(double solutionMin) {
		this.solutionMin = solutionMin;
	}


	public double getT() {
		return t;
	}


	public void setT(double t) {
		this.t = t;
	}




}
