import de.upb.soot.diff.SootMethodDiffBuilder;
import org.apache.commons.lang3.builder.DiffResult;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.Assert;
import org.junit.Test;
import soot.G;
import soot.Local;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.VoidType;
import soot.jimple.AssignStmt;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.NullConstant;
import soot.jimple.ReturnVoidStmt;

import java.util.Collections;

/**
 * @author Andreas Dann created on 06.12.18
 */
public class SootMethodDiffTest {

    @Test
    public void simpleMethod() {

        G.reset();

        Scene.v().loadNecessaryClasses();

        SootClass objectClass = Scene.v().getObjectType().getSootClass();

        SootMethod lhsMethod = new SootMethod("foo", Collections.emptyList(), VoidType.v());

        DiffResult res =
                new SootMethodDiffBuilder(lhsMethod, lhsMethod, ToStringStyle.JSON_STYLE).build();
        Assert.assertNotNull(res);
        Assert.assertEquals(0, res.getNumberOfDiffs());
    }

    @Test
    public void simpleMethod2() {

        G.reset();

        Scene.v().loadNecessaryClasses();

        SootClass objectClass = Scene.v().getObjectType().getSootClass();

        SootMethod lhsMethod = new SootMethod("foo", Collections.emptyList(), VoidType.v());

        SootMethod rhsMethod =
                new SootMethod("foo", Collections.singletonList(objectClass.getType()), VoidType.v());

        DiffResult res =
                new SootMethodDiffBuilder(lhsMethod, rhsMethod, ToStringStyle.JSON_STYLE).build();
        Assert.assertNotNull(res);
        Assert.assertEquals(1, res.getNumberOfDiffs());
        System.out.println(res.toString());
    }

    @Test
    public void simpleMethodBody() {

        G.reset();

        Scene.v().loadNecessaryClasses();

        SootClass objectClass = Scene.v().getObjectType().getSootClass();

        SootMethod lhsMethod = new SootMethod("foo", Collections.emptyList(), VoidType.v());

        JimpleBody jimpleBody = new JimpleBody();

        ReturnVoidStmt returnVoidStmt = Jimple.v().newReturnVoidStmt();
        jimpleBody.getUnits().add(returnVoidStmt);

        jimpleBody.setMethod(lhsMethod);
        lhsMethod.setActiveBody(jimpleBody);

        SootMethod rhsMethod = new SootMethod("foo", Collections.emptyList(), VoidType.v());

        DiffResult res =
                new SootMethodDiffBuilder(lhsMethod, lhsMethod, ToStringStyle.JSON_STYLE).build();
        Assert.assertNotNull(res);
        Assert.assertEquals(0, res.getNumberOfDiffs());
        System.out.println(res.toString());
    }

    @Test
    public void simpleMethodBody2() {

        G.reset();

        Scene.v().loadNecessaryClasses();

        SootClass objectClass = Scene.v().getObjectType().getSootClass();

        SootMethod lhsMethod = new SootMethod("foo", Collections.emptyList(), VoidType.v());

        JimpleBody jimpleBody = new JimpleBody();

        ReturnVoidStmt returnVoidStmt = Jimple.v().newReturnVoidStmt();
        jimpleBody.getUnits().add(returnVoidStmt);

        jimpleBody.setMethod(lhsMethod);
        lhsMethod.setActiveBody(jimpleBody);

        SootMethod rhsMethod = new SootMethod("foo", Collections.emptyList(), VoidType.v());

        JimpleBody jimpleBody2 = new JimpleBody();

        ReturnVoidStmt returnVoidStmt2 = Jimple.v().newReturnVoidStmt();
        jimpleBody2.getUnits().add(returnVoidStmt2);

        jimpleBody2.setMethod(rhsMethod);
        rhsMethod.setActiveBody(jimpleBody2);

        DiffResult res =
                new SootMethodDiffBuilder(lhsMethod, rhsMethod, ToStringStyle.JSON_STYLE).build();
        Assert.assertNotNull(res);
        Assert.assertEquals(0, res.getNumberOfDiffs());
        System.out.println(res.toString());
    }

    @Test
    public void simpleMethodBody3() {

        G.reset();

        Scene.v().loadNecessaryClasses();

        SootClass objectClass = Scene.v().getObjectType().getSootClass();

        SootMethod lhsMethod = new SootMethod("foo", Collections.emptyList(), VoidType.v());

        JimpleBody jimpleBody = new JimpleBody();

        ReturnVoidStmt returnVoidStmt = Jimple.v().newReturnVoidStmt();
        jimpleBody.getUnits().add(returnVoidStmt);

        jimpleBody.setMethod(lhsMethod);
        lhsMethod.setActiveBody(jimpleBody);

        SootMethod rhsMethod = new SootMethod("foo", Collections.emptyList(), VoidType.v());

        JimpleBody jimpleBody2 = new JimpleBody();

        Local var1 = Jimple.v().newLocal("var1", objectClass.getType());
        jimpleBody2.getLocals().add(var1);

        AssignStmt assignStmt = Jimple.v().newAssignStmt(var1, NullConstant.v());

        jimpleBody2.getUnits().add(assignStmt);

        ReturnVoidStmt returnVoidStmt2 = Jimple.v().newReturnVoidStmt();
        jimpleBody2.getUnits().add(returnVoidStmt2);

        jimpleBody2.setMethod(rhsMethod);
        rhsMethod.setActiveBody(jimpleBody2);

        DiffResult res =
                new SootMethodDiffBuilder(lhsMethod, rhsMethod, ToStringStyle.JSON_STYLE).build();
        Assert.assertNotNull(res);
        Assert.assertEquals(1, res.getNumberOfDiffs());
        System.out.println(res.toString());
    }
}
