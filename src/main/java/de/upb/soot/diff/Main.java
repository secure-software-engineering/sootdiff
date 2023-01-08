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
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Andreas Dann created on 06.12.18
 */
public class Main {

    static final String referenceFilesOpt = "reffile";
    static final String otherFilesOpt = "otherfile";
    static final String qnameOpt = "qname";

    static final String qnameOpt2 = "qname2";

    static final String useTLSH = "useTLSH";

    private SootClass referenceClass;
    private SootClass otherClass;

    public Main(String referenceFolder, String otherFileFolder, String qname, String qname2) {

        setUpTheClassesToCompare(referenceFolder, otherFileFolder, qname, qname2);
    }

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

        Option qname =
                Option.builder(qnameOpt)
                        .argName(qnameOpt)
                        .hasArg()
                        .desc("qname Method/Class")
                        .required(true)
                        .build();

        Option qname2 =
                Option.builder(qnameOpt2)
                        .argName(qnameOpt2)
                        .hasArg()
                        .desc("qname Method/Class")
                        .required(false)
                        .build();

        Option tlsh =
                Option.builder(useTLSH)
                        .hasArg(false)
                        .desc("useTLSH")
                        .optionalArg(true)
                        .required(false)
                        .build();

        // required options
        options.addOption(modulePath);
        options.addOption(otherFiles);
        options.addOption(qname);
        options.addOption(qname2);
        options.addOption(tlsh);

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

    public static void main(String[] args) throws IOException {

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
        final String qname2 = cmdLine.getOptionValue(qnameOpt2);

        Main main = new Main(referenceFolder, otherFileFolder, qname, qname2);


        DiffResult result = null;
        if (qname.contains("(")) {
            // compare methods
            result = main.compareMethod(qname);
        } else if (qname.endsWith("INIT")) {
            String nename = qname.substring(0, qname.length() - "INIT".length());
            nename = nename + "<clinit>";
            result = main.compareMethod(nename);
        } else {
            result = main.compareClasses();
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

    // FIXME: only load the classes to compare
    // FIXME: more classes in the classpath can influence the jimple result
    // FIXME: all inner classes, must be present

    private void setUpTheClassesToCompare(
            String referenceFolder, String otherFile, String qname, String qname2) {

        // qname to classname
        String className = Utils.className(qname);

        SootDiff sootDiff = new SootDiff(Collections.singletonList(referenceFolder), null, false);

        sootDiff.runSootDiff();
        this.referenceClass = Scene.v().forceResolve(className, SootClass.BODIES);

        // clear the Scene
        if (qname2 != null && !qname.isEmpty()) {
            className = Utils.className(qname2);
        }

        sootDiff = new SootDiff(Collections.singletonList(otherFile), null, false);
        sootDiff.runSootDiff();

        this.otherClass = Scene.v().forceResolve(className, SootClass.BODIES);
    }


    public DiffResult compareClasses() {
        DiffBuilder builder =
                new SootClassDiffBuilder(referenceClass, otherClass, ToStringStyle.JSON_STYLE);
        return ((SootClassDiffBuilder) builder)
                .append(referenceClass.getName(), referenceClass, otherClass)
                .build();
    }

    public DiffResult compareMethod(final String qnameMethod) {
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
            return compareClasses();
        }
        SootMethod methodToCompare = null;
        try {

            methodToCompare = otherClass.getMethod(referenceMethod.getSubSignature());
        } catch (RuntimeException e) {
            // method not contained in fixed class // has been removed
            // fall back to class compare
            return compareClasses();
        }

        // compare
        DiffBuilder builder =
                new SootMethodDiffBuilder(referenceMethod, methodToCompare, ToStringStyle.JSON_STYLE);
        res = builder.build();

        return res;
    }

    public SootClass getReferenceClass() {
        return referenceClass;
    }

    public SootClass getOtherClass() {
        return otherClass;
    }
}
