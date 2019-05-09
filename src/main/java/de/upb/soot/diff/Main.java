package de.upb.soot.diff;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.builder.Diff;
import org.apache.commons.lang3.builder.DiffBuilder;
import org.apache.commons.lang3.builder.DiffResult;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.G;
import soot.PackManager;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** @author Andreas Dann created on 06.12.18 */
public class Main {

  static final String referenceFilesOpt = "reffile";
  static final String otherFilesOpt = "otherfile";
  static final String qnameOpt = "qname";

  static final Logger logger = LoggerFactory.getLogger(Main.class);
  private SootClass referenceClass;
  private SootClass otherClass;

  private static Options createOptions() {
    Options options = new Options();

    Option modulePath =
        Option.builder(referenceFilesOpt)
            .argName(referenceFilesOpt)
            .hasArg()
            .desc("location of reference files")
            .required(true)
            .build();

    Option otherFiles =
        Option.builder(otherFilesOpt)
            .argName(otherFilesOpt)
            .hasArg()
            .desc("files to compare")
            .required(true)
            .build();

    Option logfile =
        Option.builder(qnameOpt)
            .argName(qnameOpt)
            .hasArg()
            .desc("qname Method/Class")
            .required(true)
            .build();

    // required options
    options.addOption(modulePath);
    options.addOption(otherFiles);
    options.addOption(logfile);

    Option help = Option.builder("h").longOpt("help").desc("print this message").build();
    options.addOption(help);
    return options;
  }

  private static void showHelpMessage(String[] args, Options options) {
    Options helpOptions = new Options();
    Option help = Option.builder("h").longOpt("help").desc("print this message").build();

    helpOptions.addOption(help);
    try {
      CommandLine helpLine = new BasicParser().parse(helpOptions, args, true);
      if (helpLine.hasOption("help") || args.length == 0) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("DoopModuleAnalysis", options);
        System.exit(0);
      }
    } catch (ParseException ex) {
      System.err.println("Parsing failed.  Reason: " + ex.getMessage());
      System.exit(1);
    }
  }

  private static void escapeElements(ArrayList arrayList) {
    for (int i = 0; i < ((ArrayList) arrayList).size(); i++) {
      String escaped = "\"" + arrayList.get(i).toString() + "\"";
      arrayList.set(i, escaped);
    }
  }

  public static void main(String[] args) {

    Options options = createOptions();

    showHelpMessage(args, options);

    CommandLineParser parser = new BasicParser();
    CommandLine cmdLine = null;
    HelpFormatter formatter = new HelpFormatter();
    try {
      // parse the command line arguments
      cmdLine = parser.parse(options, args);

    } catch (ParseException exp) {
      // oops, something went wrong
      System.err.println("Parsing failed.  Reason: " + exp.getMessage() + "\n");
      formatter.printHelp(Main.class.getCanonicalName(), options);
      System.exit(1);
    }

    final String referenceFolder = cmdLine.getOptionValue(referenceFilesOpt);
    final String otherFileFolder = cmdLine.getOptionValue(otherFilesOpt);
    final String qname = cmdLine.getOptionValue(qnameOpt);

    Main main = new Main(referenceFolder, otherFileFolder, qname);
    DiffResult result = null;
    if (qname.contains("(")) {
      // compare methods
      result = main.compareMethod2(qname);
    } else if (qname.endsWith("INIT")) {
      String nename = qname.substring(0, qname.length() - "INIT".length());
      nename = nename + "<clinit>";
      result = main.compareMethod2(nename);
    } else {
      result = main.compareClasses2();
    }

    for (Diff d : result.getDiffs()) {

      if (d.getKey() instanceof ArrayList) {
        escapeElements((ArrayList) d.getKey());
      }
      if (d.getValue() instanceof ArrayList) {
        escapeElements((ArrayList) d.getValue());
      }
    }

    String resultString = result.toString().trim();

    if (resultString.contains("differs from")) {
      String[] resultStrings = resultString.split("differs from");
      String lhs = "\"lhs\" : " + resultStrings[0].trim();
      String rhs = "\"rhs\" : " + resultStrings[1].trim();

      resultString = "{" + lhs + "," + rhs + "}";
    }
    System.out.print(resultString);
  }

  public Main(String referenceFolder, String otherFileFolder, String qname) {

    setUpTheClassesToCompare(referenceFolder, otherFileFolder, qname);
  }

  private void setUpTheClassesToCompare(String referenceFolder, String otherFile, String qname) {

    // qname to classname
    String className = this.className(qname);

    this.setUpSoot();
    soot.options.Options.v().set_process_dir(Collections.singletonList(referenceFolder));

    Scene.v().loadNecessaryClasses();

    // load the reference files
    // this.loadClasses(referenceFolder);
    PackManager.v().runPacks();

    this.referenceClass = Scene.v().forceResolve(className, SootClass.BODIES);

    // clear the Scene
    this.setUpSoot();
    soot.options.Options.v().set_process_dir(Collections.singletonList(otherFile));

    Scene.v().loadNecessaryClasses();

    // load the other files
    PackManager.v().runPacks();

    this.otherClass = Scene.v().forceResolve(className, SootClass.BODIES);
  }

  public DiffResult compareClasses() {

    DiffResult res = new DiffSootClass(referenceClass).diff(new DiffSootClass(otherClass));
    return res;
  }

  public DiffResult compareClasses2() {
    DiffBuilder builder =
        new SootClassDiffBuilder(referenceClass, otherClass, ToStringStyle.JSON_STYLE);
    DiffResult res =
        ((SootClassDiffBuilder) builder)
            .append(referenceClass.getName(), referenceClass, otherClass)
            .build();
    return res;
  }

  public DiffResult compareMethod(String qnameMethod) {
    DiffResult res = null;

    if (qnameMethod.contains("(")) {

      // get the method based on the qname
      String methodName = qnameMethod.substring(qnameMethod.lastIndexOf("."), qnameMethod.length());
      String[] parameterList =
          methodName.substring(methodName.indexOf("("), methodName.indexOf(")")).split(",");

      SootMethod referenceMethod = null;
      for (SootMethod sootMethod : referenceClass.getMethods()) {
        if (sootMethod.getName().equals(methodName)) {
          List<Type> parameterTypes = sootMethod.getParameterTypes();
          // check if parameter name matches
          if (parameterTypes.size() == parameterList.length) {

            for (int i = 0; i < parameterList.length; i++) {
              if (!parameterTypes.get(i).getEscapedName().contains(parameterList[i])) {
                // go on
                break;
              }
            }
            referenceMethod = sootMethod;
          }
        }
      }

      if (referenceMethod == null) {
        throw new RuntimeException("Could not find the qname");
      }
      SootMethod methodToCompare = otherClass.getMethod(referenceMethod.getSignature());

      // compare

      res = new DiffSootMethod(referenceMethod).diff(new DiffSootMethod(methodToCompare));
    }
    return res;
  }

  public DiffResult compareMethod2(final String qnameMethod) {
    DiffResult res = null;
    String[] parameterList = new String[0];
    String methodName = qnameMethod;
    methodName = methodName.substring(methodName.lastIndexOf(".") + 1, methodName.length());

    if (methodName.contains("(")) {

      // get the method based on the qname
      parameterList =
          methodName.substring(methodName.indexOf("(") + 1, methodName.indexOf(")")).split(",");

      methodName = methodName.substring(0, methodName.indexOf("("));

      // check if the method is a constructor
      if (Character.isUpperCase(methodName.charAt(0))) {
        methodName = "<init>";
      }
    }
    SootMethod referenceMethod = null;
    for (SootMethod sootMethod : referenceClass.getMethods()) {
      if (sootMethod.getName().equals(methodName)) {
        List<Type> parameterTypes = sootMethod.getParameterTypes();
        // check if parameter name matches
        if (parameterTypes.size() == parameterList.length) {

          for (int i = 0; i < parameterList.length; i++) {
            if (!parameterTypes.get(i).getEscapedName().contains(parameterList[i])) {
              // go on
              break;
            }
          }
          referenceMethod = sootMethod;
          break;
        }
      }
    }

    if (referenceMethod == null) {
      // method is not present in vulnerable code // reference code
      // it was added in commit
      // throw new RuntimeException("Could not find the qname");

      // fall back to class compare
      return compareClasses2();
    }
    SootMethod methodToCompare = null;
    try {

      methodToCompare = otherClass.getMethod(referenceMethod.getSubSignature());
    } catch (RuntimeException e) {
      // method not contained in fixed class // has been removed
      // fall back to class compare
      return compareClasses2();
    }

    // compare
    DiffBuilder builder =
        new SootMethodDiffBuilder(referenceMethod, methodToCompare, ToStringStyle.JSON_STYLE);
    res = builder.build();

    return res;
  }

  private String className(final String qname) {
    String className = qname;
    if (qname.contains("(")) {
      // we have a method

      className = className.substring(0, className.indexOf("("));

      // check if we have a constructro --> then constname == classname
      if (Character.isUpperCase(className.charAt(className.lastIndexOf(".") + 1))) {
        className = className;
      } else {
        // cut of the method name
        className = className.substring(0, className.lastIndexOf("."));
      }
    }
    if (qname.endsWith("INIT")) {
      className = className.substring(0, className.lastIndexOf("."));
    }

    // FIXME: case if qname is a field

    return className;
  }

  protected void setUpSoot() {
    G.reset();
    logger.info("[Soot] Setting Up Soot ... \n");
    soot.options.Options.v().set_debug(false);
    soot.options.Options.v().set_debug_resolver(true);
    soot.options.Options.v().set_prepend_classpath(true);
    soot.options.Options.v().set_keep_line_number(true);
    soot.options.Options.v().set_output_format(soot.options.Options.output_format_none);

    //    soot.options.Options.v().set_no_bodies_for_excluded(true);
    soot.options.Options.v().set_allow_phantom_refs(true);


    soot.options.Options.v().setPhaseOption("jb.ule", "enabled:" + true);
    soot.options.Options.v().setPhaseOption("jb.dae", "enabled:" + true);

    soot.options.Options.v().setPhaseOption("jb.cp-ule", "enabled:" + true);

    // FIXME: Jimple Soot optimization does not have any effect on the diff
    // activate optimization
    soot.options.Options.v().setPhaseOption("jop", "enabled:" + true);
    soot.options.Options.v().setPhaseOption("jop.cp", "enabled:" + true);
    soot.options.Options.v().setPhaseOption("jop.cpf", "enabled:" + true);
    soot.options.Options.v().setPhaseOption("jop.dae", "enabled:" + true);
    soot.options.Options.v().setPhaseOption("jop.ubf1", "enabled:" + true);
    soot.options.Options.v().setPhaseOption("jop.ubf2", "enabled:" + true);

    soot.options.Options.v().setPhaseOption("jop.ule", "enabled:" + true);

    // add the String optimizer
    StringOptimizeBodyTransformer.addToSootConfig();
  }

  /*private void loadClasses(String modulePath) {
    // first we have to load all module-info files in the modulepath to build up
    // the module graph, which we use to resolve dependencies/references
    //  Scene.v().loadModuleInformation(modulePath);

    // now we can load the classes of the module as application classes
    List<String> map = SourceLocator.v().getClassesUnder(modulePath);
    for (String klass : map) {
      logger.info("Loaded Class: " + klass + "\n");
      SootClass c = Scene.v().loadClassAndSupport(klass);
      c.setApplicationClass();
    }

    // this must be called after all classes are resolved; because it sets doneResolving in Scene
    Scene.v().loadNecessaryClasses();
  }*/
}
