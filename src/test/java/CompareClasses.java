import com.github.difflib.DiffUtils;
import com.github.difflib.algorithm.DiffException;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Patch;
import de.upb.soot.diff.Main;
import org.apache.commons.lang3.builder.DiffResult;
import org.junit.Assert;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;

/** @author Andreas Dann created on 10.12.18 */
public class CompareClasses {




  @Test
  public void checkECJ15() {

    URL url = this.getClass().getResource("/" + "1.5");
    File refClass = new File(url.getFile());

    // the other class
    URL otherurl = this.getClass().getResource("/" + "ecj1.5");
    String otherFolder = new File(otherurl.getFile()).toString();

    File[] listOfFiles = refClass.listFiles();
    ArrayList<DiffResult> diffResults = new ArrayList<>();

    for (File filename : listOfFiles) {
      String qname = filename.getName().substring(0, filename.getName().indexOf("."));
      Main main = new Main(refClass.toString(), otherFolder, qname);
      DiffResult res = main.compareClasses();
      diffResults.add(res);
    }

    for (DiffResult res : diffResults) {
      Assert.assertEquals(0, res.getNumberOfDiffs());
    }
  }

  @Test
  public void checkECJ16() {

    URL url = this.getClass().getResource("/" + "1.6");
    File refClass = new File(url.getFile());

    // the other class
    URL otherurl = this.getClass().getResource("/" + "ecj1.6");
    String otherFolder = new File(otherurl.getFile()).toString();

    File[] listOfFiles = refClass.listFiles();
    ArrayList<DiffResult> diffResults = new ArrayList<>();

    for (File filename : listOfFiles) {
      String qname = filename.getName().substring(0, filename.getName().indexOf("."));
      Main main = new Main(refClass.toString(), otherFolder, qname);
      DiffResult res = main.compareClasses();
      diffResults.add(res);
    }

    for (DiffResult res : diffResults) {
      Assert.assertEquals(0, res.getNumberOfDiffs());
    }
  }

  @Test
  public void checkECJ16vsECJ15() {

    URL url = this.getClass().getResource("/" + "ecj1.6");
    File refClass = new File(url.getFile());

    // the other class
    URL otherurl = this.getClass().getResource("/" + "ecj1.5");
    String otherFolder = new File(otherurl.getFile()).toString();

    File[] listOfFiles = refClass.listFiles();
    ArrayList<DiffResult> diffResults = new ArrayList<>();

    for (File filename : listOfFiles) {
      String qname = filename.getName().substring(0, filename.getName().indexOf("."));
      Main main = new Main(refClass.toString(), otherFolder, qname);
      DiffResult res = main.compareClasses();
      diffResults.add(res);
    }

    for (DiffResult res : diffResults) {
      Assert.assertEquals(0, res.getNumberOfDiffs());
    }
  }
}
