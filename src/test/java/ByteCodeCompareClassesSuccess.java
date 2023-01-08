import com.github.difflib.DiffUtils;
import com.github.difflib.algorithm.DiffException;
import com.github.difflib.patch.Patch;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Andreas Dann created on 10.12.18
 */
@Ignore
@RunWith(Parameterized.class)
public class ByteCodeCompareClassesSuccess {

    private final String referenceFolder;
    private final String otherFolder;
    private final File filename;

    public ByteCodeCompareClassesSuccess(String referenceFolder, String otherFolder, File filename) {
        this.referenceFolder = referenceFolder;
        this.otherFolder = otherFolder;
        this.filename = filename;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> generateParams() {
        List<Object[]> params = new ArrayList<Object[]>();

        params.addAll(createParaList("reference", "1.5"));
        params.addAll(createParaList("reference", "1.6"));
        params.addAll(createParaList("reference", "1.7"));

        return params;
    }

    public static List<Object[]> createParaList(String refeFolder, String cmpFolder) {
        List<Object[]> params = new ArrayList<Object[]>();

        URL url = ByteCodeCompareClassesSuccess.class.getResource("/" + refeFolder);
        File refClass = new File(url.getFile());

        // the other class
        URL otherurl = ByteCodeCompareClassesSuccess.class.getResource("/" + cmpFolder);
        String otherFolder = new File(otherurl.getFile()).toString();

        File[] listOfFiles = refClass.listFiles();

        for (File filename : listOfFiles) {

            params.add(new Object[]{refClass.toString(), otherFolder, filename});
        }
        return params;
    }

    @Test
    public void test() throws IOException, DiffException {
        String qname = filename.getName().substring(0, filename.getName().indexOf("."));

        System.out.println(
                "Compare " + referenceFolder + " against " + otherFolder + " using class " + qname);

        ClassReader cr = new ClassReader(new FileInputStream(filename));
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        cr.accept(
                new TraceClassVisitor(new PrintWriter(byteArrayOutputStream)), ClassReader.SKIP_DEBUG);

        String filename2 = otherFolder + "/" + qname + ".class";
        ClassReader cr2 = new ClassReader(new FileInputStream(filename2));
        ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();

        cr2.accept(
                new TraceClassVisitor(new PrintWriter(byteArrayOutputStream2)), ClassReader.SKIP_DEBUG);

        Patch<String> diff =
                DiffUtils.diff(byteArrayOutputStream.toString(), byteArrayOutputStream2.toString(), null);
        System.out.println("Number of diffs: " + diff.getDeltas().size());
        System.out.println(diff);

        boolean condition = diff.getDeltas().size() <= 1;

        System.out.println(
                "Latex: "
                        + filename
                        + " "
                        + otherFolder.substring(otherFolder.lastIndexOf("/", otherFolder.length() - 1))
                        + " : "
                        + condition);

        Assert.assertTrue(condition);
    }
}
