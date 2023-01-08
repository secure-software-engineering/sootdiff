class Point2d {
    /* The X and Y coordinates of the point--instance variables */
    private double x;
    private double y;
    private boolean debug;    // A trick to help with debugging

    public Point2d(double px, double py) { // Constructor
        x = px;
        y = py;

        debug = false;        // turn off debugging
    }

    public Point2d() {        // Default constructor
        this(0.0, 0.0);        // Invokes 2 parameter Point2D constructor
    }
    // Note that a this() invocation must be the BEGINNING of
    // statement body of constructor

    public Point2d(Point2d pt) {    // Another consructor
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
        if (debug)
            System.out.println("Debug: " + s);
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

