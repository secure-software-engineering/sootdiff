package de.upb.soot.diff.compare;

import de.upb.soot.diff.SootDiff;
import de.upb.soot.diff.Utils;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import soot.Printer;
import soot.Scene;
import soot.SootClass;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Andreas Dann created on 10.12.18
 */
@RunWith(Parameterized.class)
public class JimpleClassesOutputTest {

    private final String referenceFolder;
    private final String otherFolder;
    private final File filename;

    public JimpleClassesOutputTest(String referenceFolder, String otherFolder, File filename) {
        this.referenceFolder = referenceFolder;
        this.otherFolder = otherFolder;
        this.filename = filename;
    }

    /**
     * The jimple representation is not always the same, as expected
     *
     * @return
     */
    @Parameterized.Parameters(name = "{1}:{2}")
    public static Collection<Object[]> generateParams() {
        List<Object[]> params = new ArrayList<Object[]>();

    /* If JDK > 8, compiling to 1.5 and 1.6 is not working anymore and the tests fail.
    params.addAll(createParaList("1.5"));
    params.addAll(createParaList("1.6"));*/
        params.addAll(createParaList("1.7"));
        // params.addAll(createParaList("1.9"));
        return params;
    }

    public static List<Object[]> createParaList(String cmpFolder) {
        List<Object[]> params = new ArrayList<Object[]>();

        URL url = JimpleClassesOutputTest.class.getResource("/" + "reference");
        File refClass = new File(url.getFile());

        // the other class
        URL otherurl = JimpleClassesOutputTest.class.getResource("/" + cmpFolder);
        String otherFolder = new File(otherurl.getFile()).toString();

        File[] listOfFiles = refClass.listFiles();

        for (File filename : listOfFiles) {
            params.add(new Object[]{refClass.toString(), otherFolder, filename});
        }
        return params;
    }

    @Test
    @Ignore
    public void test() {
        String qname = filename.getName().substring(0, filename.getName().indexOf("."));

        System.out.println(
                "Compare " + referenceFolder + " against " + otherFolder + " using class " + qname);
        SootDiff sootDiff = new SootDiff(Collections.singletonList(referenceFolder), null, false);
        sootDiff.runSootDiff();

        StringWriter s1 = new StringWriter();
        PrintWriter fileWriter = new PrintWriter(s1);
        SootClass a = Scene.v().getSootClass(Utils.className(qname));
        Printer.v().printTo(a, fileWriter);
        // the other class
        URL otherurl = JimpleClassesOutputTest.class.getResource("/1.9");
        String otherFolder = new File(otherurl.getFile()).toString();

        SootDiff sootDiff2 = new SootDiff(Collections.singletonList(otherFolder), null, false);
        sootDiff2.runSootDiff();

        SootClass b = Scene.v().getSootClass(Utils.className(qname));

        StringWriter s2 = new StringWriter();
        PrintWriter fileWriter2 = new PrintWriter(s2);
        Printer.v().printTo(b, fileWriter2);

        assertEquals(s1.toString(), s2.toString());
    }
}
