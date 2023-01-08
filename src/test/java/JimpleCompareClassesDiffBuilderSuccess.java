import de.upb.soot.diff.Main;
import org.apache.commons.lang3.builder.Diff;
import org.apache.commons.lang3.builder.DiffResult;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Andreas Dann created on 10.12.18
 */
@Ignore
@RunWith(Parameterized.class)
public class JimpleCompareClassesDiffBuilderSuccess {

    private final String referenceFolder;
    private final String otherFolder;
    private final File filename;

    public JimpleCompareClassesDiffBuilderSuccess(
            String referenceFolder, String otherFolder, File filename) {
        this.referenceFolder = referenceFolder;
        this.otherFolder = otherFolder;
        this.filename = filename;
    }

    /**
     * THe simple jimple comparison has to fail on some classes, expected (AD)
     *
     * @return
     */
    @Parameterized.Parameters
    public static Collection<Object[]> generateParams() {
        List<Object[]> params = new ArrayList<Object[]>();

        params.addAll(createParaList("1.5"));
        params.addAll(createParaList("1.6"));
        params.addAll(createParaList("1.7"));

        return params;
    }

    public static List<Object[]> createParaList(String cmpFolder) {
        List<Object[]> params = new ArrayList<Object[]>();

        URL url = JimpleCompareClassesDiffBuilderSuccess.class.getResource("/" + "reference");
        File refClass = new File(url.getFile());

        // the other class
        URL otherurl =
                JimpleCompareClassesDiffBuilderSuccess.class.getClass().getResource("/" + cmpFolder);
        String otherFolder = new File(otherurl.getFile()).toString();

        File[] listOfFiles = refClass.listFiles();

        for (File filename : listOfFiles) {

            params.add(new Object[]{refClass.toString(), otherFolder, filename});
        }
        return params;
    }

    @Test
    public void test() {
        String qname = filename.getName().substring(0, filename.getName().indexOf("."));

        System.out.println(
                "Compare " + referenceFolder + " against " + otherFolder + " using class " + qname);

        System.out.println("");

        Main main = new Main(referenceFolder, otherFolder, qname, qname);
        DiffResult res = main.compareClasses();
        // AD: printout the differences for debugging
        for (Diff d : res.getDiffs()) {
            System.out.println(d.toString());
        }

        int numberOfDiffs = res.getNumberOfDiffs();

        boolean condition = numberOfDiffs == 0;
        System.out.println(
                "Latex: "
                        + filename
                        + " "
                        + otherFolder.substring(otherFolder.lastIndexOf("/", otherFolder.length() - 1))
                        + " : "
                        + condition);

        Assert.assertEquals(0, numberOfDiffs);
    }
}
