
/**
 * Consists different kinds of light 
 * @author Christoph Murauer - a1127084
 *
 */
public class Light {

	 public static final int AMBIENT = 0;
	 public static final int DIRECTIONAL = 1;
	 public static final int POINT = 2;

	 public int lightType;
	 private Vec3 pos;           // the position of a point light or
	                             // the direction to a directional light
	 private float ir, ig, ib;    // intensity of the light source

    public Light(int type, Vec3 v, float r, float g, float b) {
        lightType = type;
        setIr(r);
        setIg(g);
        setIb(b);
        if (type != AMBIENT) {
            setPos(v);
            if (type == DIRECTIONAL) {
                getPos().normalize();
            }
        }
    }

	public float getIr() {
		return ir;
	}

	public void setIr(float ir) {
		this.ir = ir;
	}

	public float getIg() {
		return ig;
	}

	public void setIg(float ig) {
		this.ig = ig;
	}

	public float getIb() {
		return ib;
	}

	public void setIb(float ib) {
		this.ib = ib;
	}

	public Vec3 getPos() {
		return pos;
	}

	public void setPos(Vec3 pos) {
		this.pos = pos;
	}
	
	
	
	
	
}
