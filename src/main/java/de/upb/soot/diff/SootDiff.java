package de.upb.soot.diff;

import de.upb.soot.diff.printing.PrinterUtils;
import de.upb.soot.diff.printing.SimpleUnitPrinter;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.G;
import soot.PackManager;
import soot.Printer;
import soot.Scene;
import soot.options.Options;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class SootDiff {
    static final Logger logger = LoggerFactory.getLogger(Utils.class);
    private final List<String> processDirs;
    private final Path outputDir;
    private boolean writeJimple;

    public SootDiff(List<String> processDirs, String outputDir, boolean writeJimple) {
        this.processDirs = processDirs;
        this.writeJimple = writeJimple;
        if (outputDir == null || outputDir.isEmpty()) {
            this.outputDir = Paths.get("sootOutput");
        } else {
            this.outputDir = Paths.get(outputDir);
        }
    }

    public List<String> getProcessDirs() {
        return processDirs;
    }

    public Path getOutputDir() {
        return outputDir;
    }

    private void setUpSootDiff() {
        G.reset();
        G.v().resetSpark();
        logger.info("[Soot] Setting Up Soot ... \n");
        soot.options.Options.v().set_debug(false);
        soot.options.Options.v().set_debug_resolver(false);
        soot.options.Options.v().set_prepend_classpath(true);
        soot.options.Options.v().set_ignore_resolution_errors(true);
        soot.options.Options.v().set_ignore_resolving_levels(true);
        soot.options.Options.v().set_ignore_classpath_errors(true);
        // soot.options.Options.v().set_keep_line_number(true);
        if (writeJimple) {
            soot.options.Options.v().set_output_format(soot.options.Options.output_format_jimple);
        } else {
            soot.options.Options.v().set_output_format(Options.output_format_none);
        }

        soot.options.Options.v().set_src_prec(Options.src_prec_only_class);

        soot.options.Options.v().set_no_bodies_for_excluded(true);
        soot.options.Options.v().set_allow_phantom_refs(true);

        soot.options.Options.v().set_wrong_staticness(Options.wrong_staticness_ignore);

        soot.options.Options.v().setPhaseOption("jb.lns", "enabled:" + true);

        // !!! Important to avoid different local names!!!
        soot.options.Options.v().setPhaseOption("jb", "stabilize-local-names:" + true);
        soot.options.Options.v().setPhaseOption("jb.lns", "sort-locals:" + true);

        soot.options.Options.v().setPhaseOption("jb.ule", "enabled:" + true);
        soot.options.Options.v().setPhaseOption("jb.dae", "enabled:" + true);

        soot.options.Options.v().setPhaseOption("jb.cp-ule", "enabled:" + true);

        // use Jimple Soot optimization
        // activate optimization
        soot.options.Options.v().setPhaseOption("jop", "enabled:" + true);
        soot.options.Options.v().setPhaseOption("jop.cp", "enabled:" + true);
        soot.options.Options.v().setPhaseOption("jop.cpf", "enabled:" + true);
        soot.options.Options.v().setPhaseOption("jop.dae", "enabled:" + true);
        soot.options.Options.v().setPhaseOption("jop.ubf1", "enabled:" + true);
        soot.options.Options.v().setPhaseOption("jop.ubf2", "enabled:" + true);

        soot.options.Options.v().setPhaseOption("jop.ule", "enabled:" + true);

        // TODO improve in the future
        StringOptimizeBodyTransformer.addToSootConfig();

        // set the custom UnitPrinter
        Printer.v().setCustomUnitPrinter(SimpleUnitPrinter::new);
        Printer.v().setCustomClassSignaturePrinter(PrinterUtils::simplifyClassName);
        Printer.v().setCustomMethodSignaturePrinter(PrinterUtils::getMethodSignature);

        soot.options.Options.v().set_output_dir(this.outputDir.toAbsolutePath().toString());

        soot.options.Options.v().set_process_dir(this.processDirs);

        soot.options.Options.v().set_no_writeout_body_releasing(true);
    }

    private void loadTheClasses() {

        this.setUpSootDiff();
        Scene.v().loadNecessaryClasses();

        // load the reference files
        PackManager.v().runPacks();
        try {
            if (Files.exists(outputDir)) {
                FileUtils.deleteDirectory(outputDir.toFile());
            }
            if (writeJimple) {
                PackManager.v().writeOutput();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void runSootDiff() {
        this.loadTheClasses();
    }
}
