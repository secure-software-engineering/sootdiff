/**
 * Small driver class to test Point2d
 *
 * @author Robert H. sloan
 */
class PointTester {
    public static void main(String[] args) {
        Point2d pt1 = new Point2d();

        System.out.println("pt1 = " + pt1);

        Point2d pt2 = new Point2d(4.0, 3.0);

        System.out.println("pt2 = " + pt2);

        pt1.setDebug(true); // turning on debugging
        // statements for pt1

        System.out.println("Distance from " + pt1 + " to " + pt2 + " is " + pt1.distanceFrom(pt2));

        System.out.println("Distance from " + pt2 + " to " + pt1 + " is " + pt2.distanceFrom(pt1));

        System.out.println(
                "Distance from " + pt1 + " to the origin (0, 0) is " + pt1.distanceFromOrigin());

        System.out.println(
                "Distance from " + pt2 + " to the origin (0, 0) is " + pt2.distanceFromOrigin());

        pt1.setXY(3, 5);
        System.out.println("pt1 = " + pt1);

        pt2.setXY(-3, -5);
        System.out.println("pt2 = " + pt2);

        System.out.println("Distance from " + pt1 + " to " + pt2 + " is " + pt1.distanceFrom(pt2));

        System.out.println("Distance from " + pt2 + " to " + pt1 + " is " + pt2.distanceFrom(pt1));

        pt1.setDebug(false); // turning off debugging
        // statements for pt1

        System.out.println(
                "Distance from " + pt1 + " to the origin (0, 0) is " + pt1.distanceFromOrigin());

        System.out.println(
                "Distance from " + pt2 + " to the origin (0, 0) is " + pt2.distanceFromOrigin());
    }

    public static class Point2d {
        /* The X and Y coordinates of the point--instance variables */
        private double x;
        private double y;
        private boolean debug; // A trick to help with debugging

        public Point2d(double px, double py) { // Constructor
            x = px;
            y = py;

            debug = false; // turn off debugging
        }

        public Point2d() { // Default constructor
            this(0.0, 0.0); // Invokes 2 parameter Point2D constructor
        }
        // Note that a this() invocation must be the BEGINNING of
        // statement body of constructor

        public Point2d(Point2d pt) { // Another consructor
            x = pt.getX();
            y = pt.getY();

            // a better method would be to replace the above code with
            //    this (pt.getX(), pt.getY());
            // especially since the above code does not initialize the
            // variable debug.  This way we are reusing code that is already
            // working.
        }

        public void dprint(String s) {
            // print the debugging string only if the "debug"
            // data member is true
            if (debug) System.out.println("Debug: " + s);
        }

        public void setDebug(boolean b) {
            debug = b;
        }

        public double getX() {
            return x;
        }

        public void setX(double px) {
            dprint("setX(): Changing value of X from " + x + " to " + px);
            x = px;
        }

        public double getY() {
            return y;
        }

        public void setY(double py) {
            dprint("setY(): Changing value of Y from " + y + " to " + py);
            y = py;
        }

        public void setXY(double px, double py) {
            setX(px);
            setY(py);
        }

        public double distanceFrom(Point2d pt) {
            double dx = Math.abs(x - pt.getX());
            double dy = Math.abs(y - pt.getY());

            // check out the use of dprint()
            dprint("distanceFrom(): deltaX = " + dx);
            dprint("distanceFrom(): deltaY = " + dy);

            return Math.sqrt((dx * dx) + (dy * dy));
        }

        public double distanceFromOrigin() {
            return distanceFrom(new Point2d());
        }

        public String toStringForXY() {
            String str = "(" + x + ", " + y;
            return str;
        }

        public String toString() {
            String str = toStringForXY() + ")";
            return str;
        }
    }

    public static class Point3d extends Point2d {
        private double z;

        public Point3d(int px, int py, int pz) {
            super(px, py);

            z = pz;
        }

        public Point3d() {
            super();
            z = 0;

            // perhaps a better method would be to replace the above code with
            //     this (0, 0, 0);
        }

        public Point3d(Point3d pt) {
            super((Point2d) pt);
            z = pt.getZ();

            // perhaps a better method would be to replace the above code with
            //     this (pt.getX(), pt.getY(), pt.getZ());
        }

        public static void main(String[] args) {
            Point3d pt1 = new Point3d();
            System.out.println("pt1 = " + pt1);

            Point3d pt2 = new Point3d(1, 2, 3);
            System.out.println("pt2 = " + pt2);

            pt1.setDebug(true); // turn on debugging statements
            // for pt1
            System.out.println("Distance from " + pt2 + " to " + pt1 + " is " + pt2.distanceFrom(pt1));

            System.out.println(
                    "Distance from " + pt1 + " to the origin (0, 0) is " + pt1.distanceFromOrigin());

            System.out.println(
                    "Distance from " + pt2 + " to the origin (0, 0) is " + pt2.distanceFromOrigin());

            pt1.setXYZ(3, 5, 7);
            System.out.println("pt1 = " + pt1);

            pt2.setXYZ(-3, -5, -7);
            System.out.println("pt2 = " + pt2);

            System.out.println("Distance from " + pt1 + " to " + pt2 + " is " + pt1.distanceFrom(pt2));

            System.out.println("Distance from " + pt2 + " to " + pt1 + " is " + pt2.distanceFrom(pt1));

            pt1.setDebug(false); // turning off debugging
            // statements for pt1

            System.out.println(
                    "Distance from " + pt1 + " to the origin (0, 0) is " + pt1.distanceFromOrigin());

            System.out.println(
                    "Distance from " + pt2 + " to the origin (0, 0) is " + pt2.distanceFromOrigin());
        }

        public double getZ() {
            return z;
        }

        public void setZ(double pz) {
            dprint("setZ(): Changing value of z from " + z + " to " + pz);
            z = pz;
        }

        public void setXYZ(double px, double py, double pz) {
            setXY(px, py);
            setZ(pz);
        }

        public double distanceFrom(Point3d pt) {
            double xyDistance = super.distanceFrom((Point2d) pt);
            double dz = Math.abs(z - pt.getZ());
            dprint("distanceFrom(): deltaZ = " + dz);

            return Math.sqrt((xyDistance * xyDistance) + (dz * dz));
        }

        public double distanceFromOrigin() {
            return distanceFrom(new Point3d());
        }

        public String toStringForZ() {
            String str = ", " + z;
            return str;
        }

        public String toString() {
            String str = toStringForXY() + toStringForZ() + ")";
            return str;
        }
    }
}
