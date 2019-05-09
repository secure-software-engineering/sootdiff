class Point3d extends Point2d {
 private double z;

 public Point3d( int px, int py, int pz) {
    super (px, py);

    z = pz;
   }

 public Point3d () {
    super ();
    z = 0;

    // perhaps a better method would be to replace the above code with
    //     this (0, 0, 0);
   }

 public Point3d(Point3d pt) {
    super ((Point2d) pt);
    z = pt.getZ();

    // perhaps a better method would be to replace the above code with
    //     this (pt.getX(), pt.getY(), pt.getZ());
   }

 public void setZ(double pz) {
    dprint("setZ(): Changing value of z from " + z + " to " + pz);
    z = pz;
   }

 public double getZ () {
    return z;
   }

 public void setXYZ(double px, double py, double pz) {
    setXY(px, py);
    setZ(pz);
   }

 public double distanceFrom (Point3d pt) {
    double xyDistance = super.distanceFrom ((Point2d) pt);
    double dz = Math.abs (z - pt.getZ()); 
    dprint ("distanceFrom(): deltaZ = " + dz);

    return Math.sqrt((xyDistance * xyDistance) + (dz * dz));
   }

 public double distanceFromOrigin () {
    return distanceFrom (new Point3d ( ));
   }

 public String toStringForZ() {
    String str =  ", " + z;
    return str;
   }

 public String toString() {
    String str = toStringForXY() + toStringForZ() + ")";
    return str;
   }

 
 public static void main (String[] args) {
    Point3d pt1 = new Point3d ();
    System.out.println ("pt1 = " + pt1);

    Point3d pt2 = new Point3d (1,2,3);
    System.out.println ("pt2 = " + pt2);

    pt1.setDebug(true);		// turn on debugging statements
				// for pt1
    System.out.println ("Distance from " + pt2 + " to " + pt1 +
			" is " + pt2.distanceFrom(pt1));

    System.out.println ("Distance from " + pt1 + " to the origin (0, 0) is " +
			pt1.distanceFromOrigin());

    System.out.println ("Distance from " + pt2 + " to the origin (0, 0) is " +
			pt2.distanceFromOrigin());

    pt1.setXYZ(3, 5, 7);
    System.out.println ("pt1 = " + pt1);

    pt2.setXYZ(-3, -5, -7);
    System.out.println ("pt2 = " + pt2);

    System.out.println ("Distance from " + pt1 + " to " + pt2 +
			" is " + pt1.distanceFrom(pt2));

    System.out.println ("Distance from " + pt2 + " to " + pt1 +
			" is " + pt2.distanceFrom(pt1));

    pt1.setDebug(false);   	// turning off debugging 
			   	// statements for pt1

    System.out.println ("Distance from " + pt1 + " to the origin (0, 0) is " +
			pt1.distanceFromOrigin());

    System.out.println ("Distance from " + pt2 + " to the origin (0, 0) is " +
			pt2.distanceFromOrigin());


   }
}
