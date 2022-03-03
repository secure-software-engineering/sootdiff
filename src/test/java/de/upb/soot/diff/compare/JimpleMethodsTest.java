package de.upb.soot.diff.compare;

import de.upb.soot.diff.SootDiff;
import org.junit.Test;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import targets.StringBufferConstructor;
import targets.StringBuilderAndBuffer1;
import targets.StringBuilderAndBuffer2;
import targets.StringBuilderConstructor;
import targets.StringValueOf;

import java.io.File;
import java.net.URL;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

/**
 * @author Andreas Dann created on 10.12.18
 */
public class JimpleMethodsTest {

    @Test
    public void stringBuilderAndBuffer1() {
        runTest(StringBuilderAndBuffer1.class);
    }

    @Test
    public void stringBuilderAndBuffer2() {
        runTest(StringBuilderAndBuffer2.class);
    }

    @Test
    public void stringBufferConstructor() {
        runTest(StringBuilderConstructor.class);
    }

    @Test
    public void stringBuilderConstructor() {
        runTest(StringBufferConstructor.class);
    }

    @Test
    public void stringValueOf() {
        runTest(StringValueOf.class);
    }

    private void runTest(Class<?> clz) {

        URL url = JimpleMethodsTest.class.getResource("/");
        File refClass = new File(url.getFile());
        SootDiff sootDiff = new SootDiff(Collections.singletonList(refClass.toString()), null, false);

        sootDiff.runSootDiff();

        SootClass sc = Scene.v().getSootClass(clz.getName());
        SootMethod sm1 = sc.getMethod("void targetMethod1()");
        SootMethod sm2 = sc.getMethod("void targetMethod2()");

        assertEquals(
                String.format(
                        "Method Bodies in Jimple format not equal \n%s\n %s",
                        sm1.getActiveBody(), sm2.getActiveBody()),
                sm1.getActiveBody().toString(),
                sm2.getActiveBody().toString().replace("void targetMethod2()", "void targetMethod1()"));
    }
}
