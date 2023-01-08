package de.upb.soot.diff;

import soot.CompilationDeathException;
import soot.Printer;
import soot.SootClass;
import soot.options.Options;
import soot.toolkits.scalar.ConstantValueToInitializerTransformer;
import soot.util.EscapedWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class Utils {

    public static byte[] sootClassToJimple(SootClass sootClass) {
        if (Options.v().output_format() == Options.output_format_jimple) {
            if (!sootClass.isPhantom()) {
                ConstantValueToInitializerTransformer.v().transformClass(sootClass);
            }
        }

        //        String fileName = SourceLocator.v().getFileNameFor(sootClass,
        // Options.output_format_jimple);
        //
        //
        //        new File(fileName).getParentFile().mkdirs();
        //        streamOut = new FileOutputStream(fileName);
        ByteArrayOutputStream streamOut = null;
        OutputStreamWriter fos = null;
        EscapedWriter out = null;
        PrintWriter writerOut = null;
        try {
            streamOut = new ByteArrayOutputStream();
            fos = new OutputStreamWriter(streamOut);
            out = new EscapedWriter(fos);
            writerOut = new PrintWriter(out);
            Printer.v().printTo(sootClass, writerOut);
            writerOut.flush();
            final byte[] bytes = (streamOut).toByteArray();
            return bytes;
        } finally {
            try {

                if (streamOut != null) {
                    streamOut.close();
                }
                if (writerOut != null) {
                    writerOut.close();
                }
                if (fos != null) {

                    fos.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                throw new CompilationDeathException("Cannot close output stream ");
            }
        }
    }

    public static String className(final String qname) {
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
}
