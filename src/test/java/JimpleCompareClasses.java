import de.upb.soot.diff.Main;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.commons.lang3.builder.DiffResult;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/** @author Andreas Dann created on 10.12.18 */
@Ignore
@RunWith(Parameterized.class)
public class JimpleCompareClasses {

  private final String referenceFolder;
  private final String otherFolder;
  private final File filename;

  /**
   * The jimple representation is not always the same, as expected
   *
   * @return
   */
  @Parameterized.Parameters(name = "{1}:{2}")
  public static Collection<Object[]> generateParams() {
    List<Object[]> params = new ArrayList<Object[]>();

    params.addAll(createParaList("1.5"));
    params.addAll(createParaList("1.6"));
    params.addAll(createParaList("1.7"));

    params.addAll(createParaList("ecj1.5"));
    params.addAll(createParaList("ecj1.6"));
    params.addAll(createParaList("ecj1.7"));
    params.addAll(createParaList("ecj1.8"));

    return params;
  }

  public static List<Object[]> createParaList(String cmpFolder) {
    List<Object[]> params = new ArrayList<Object[]>();

    URL url = JimpleCompareClasses.class.getResource("/" + "reference");
    File refClass = new File(url.getFile());

    // the other class
    URL otherurl = JimpleCompareClasses.class.getResource("/" + cmpFolder);
    String otherFolder = new File(otherurl.getFile()).toString();

    File[] listOfFiles = refClass.listFiles();

    for (File filename : listOfFiles) {

      params.add(new Object[] {refClass.toString(), otherFolder, filename});
    }
    return params;
  }

  public JimpleCompareClasses(String referenceFolder, String otherFolder, File filename) {
    this.referenceFolder = referenceFolder;
    this.otherFolder = otherFolder;
    this.filename = filename;
  }

  @Test
  public void test() {
    String qname = filename.getName().substring(0, filename.getName().indexOf("."));

    System.out.println(
        "Compare " + referenceFolder + " against " + otherFolder + " using class " + qname);

    Main main = new Main(referenceFolder, otherFolder, qname, qname);
    DiffResult res = main.compareClasses();

    Assert.assertEquals(0, res.getNumberOfDiffs());
  }
}
