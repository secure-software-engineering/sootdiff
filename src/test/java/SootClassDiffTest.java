import de.upb.soot.diff.DiffSootClass;
import org.apache.commons.lang3.builder.DiffResult;
import org.junit.Assert;
import org.junit.Test;
import soot.G;
import soot.Scene;
import soot.SootClass;
import soot.SootField;

/** @author Andreas Dann created on 06.12.18 */
public class SootClassDiffTest {

  @Test
  public void simpleTest() {

    G.reset();

    Scene.v().loadNecessaryClasses();

    SootClass objectClass = Scene.v().getObjectType().getSootClass();
    SootClass lhs = new SootClass("A");
    lhs.setSuperclass(objectClass);

    Scene.v().addClass(lhs);

    lhs.addField(new SootField("field1", objectClass.getType()));

    DiffResult res = new DiffSootClass(lhs).diff(new DiffSootClass(lhs));
    Assert.assertNotNull(res);
    Assert.assertEquals(res.getNumberOfDiffs(), 0);
  }

  @Test
  public void simpleTest2() {

    G.reset();

    Scene.v().loadNecessaryClasses();

    SootClass objectClass = Scene.v().getObjectType().getSootClass();
    SootClass lhs = new SootClass("A");
    lhs.setSuperclass(objectClass);
    lhs.addField(new SootField("field1", objectClass.getType()));

    Scene.v().addClass(lhs);

    SootClass rhs = new SootClass("A");
    rhs.setSuperclass(objectClass);
    rhs.addField(new SootField("field1", objectClass.getType()));

    Scene.v().addClass(rhs);

    DiffResult res = new DiffSootClass(lhs).diff(new DiffSootClass(rhs));
    Assert.assertNotNull(res);
    Assert.assertEquals(res.getNumberOfDiffs(), 0);
  }

  @Test
  public void simpleTest3() {

    G.reset();

    Scene.v().loadNecessaryClasses();

    SootClass objectClass = Scene.v().getObjectType().getSootClass();
    SootClass lhs = new SootClass("A");
    lhs.setSuperclass(objectClass);
    lhs.addField(new SootField("field0", objectClass.getType()));
    lhs.addField(new SootField("field1", objectClass.getType()));

    Scene.v().addClass(lhs);

    SootClass rhs = new SootClass("A");
    rhs.setSuperclass(objectClass);
    rhs.addField(new SootField("field2", objectClass.getType()));
    rhs.addField(new SootField("field1", objectClass.getType()));
    rhs.addField(new SootField("field3", objectClass.getType()));

    Scene.v().addClass(rhs);

    DiffResult res = new DiffSootClass(lhs).diff(new DiffSootClass(rhs));
    Assert.assertNotNull(res);
    Assert.assertEquals(res.getNumberOfDiffs(), 3);
    System.out.println(res.toString());
  }



  @Test
  public void simpleTest4() {

    G.reset();

    Scene.v().loadNecessaryClasses();

    SootClass objectClass = Scene.v().getObjectType().getSootClass();
    SootClass lhs = new SootClass("A");
    lhs.setSuperclass(objectClass);

    Scene.v().addClass(lhs);

    SootClass rhs = new SootClass("A");
    rhs.setSuperclass(lhs);

    Scene.v().addClass(rhs);

    DiffResult res = new DiffSootClass(lhs).diff(new DiffSootClass(rhs));
    Assert.assertNotNull(res);
    Assert.assertEquals(res.getNumberOfDiffs(), 1);
    System.out.println(res.toString());
  }
}
