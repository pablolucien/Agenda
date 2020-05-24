import java.awt.Point;

/**
 * Esta clase representa un punto en el espacio tridimensional N3.
 */
public class Point3D extends Point {
    private static final long serialVersionUID = -1783055265695737801L;
    /** La coordenada Z. */
	private final int z;

	/**
	 * Constructor.
	 */
	private Point3D(final int x, final int y, final int z) {
		super(x, y);
		this.z = z;
	}
	
	/**
	 * Devuelve la proyeccion de este punto sobre el plano XY.
	 */
	final Point projectXY() {
		return new Point(x, y);
	}

	/**
	 * Compara este objeto con otro.
	 */
	public final boolean equals(final Object obj) {
		if (obj == null || obj.getClass() != getClass()) {
			return false;
		}
		
		final Point3D target = (Point3D) obj;
		return target.x == x && target.y == y && target.z == z;
	}
	
	/**
	 *
	 */
	public final int hashCode() {
		return x ^ y ^ z;
	}
	
	/**
	 * Para probar la clase.
	 */
	public static void main(final String[] args) {
		final Point p1 = new Point(0, 0);
		final Point3D p2 = new Point3D(0, 0, 0);
		final Point3D p3 = new Point3D(0, 0, 0);
		System.out.println(p1.equals(p2));				// Should be false, but...
		System.out.println(p2.equals(p1));				// Should be false.
		System.out.println(p2.equals(p3));				// Should be true.
		System.out.println(p2.projectXY().equals(p1));	// Should be true.
	}
}