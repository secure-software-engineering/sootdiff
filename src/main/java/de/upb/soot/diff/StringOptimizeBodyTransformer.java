package de.upb.soot.diff;

import soot.Body;
import soot.BodyTransformer;
import soot.Local;
import soot.PackManager;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethodRef;
import soot.Transform;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.Expr;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.NewExpr;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.VirtualInvokeExpr;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.toolkits.scalar.LocalNameStandardizer;
import soot.options.Options;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.Orderer;
import soot.toolkits.graph.PseudoTopologicalOrderer;
import soot.toolkits.scalar.LocalDefs;
import soot.toolkits.scalar.LocalUses;
import soot.toolkits.scalar.UnitValueBoxPair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Andreas Dann created on 15.02.19
 */
public class StringOptimizeBodyTransformer extends BodyTransformer {

    private static String STRINGBUILDER_INIT_SIGNATURE = "<java.lang.StringBuilder: void <init>()>";
    private static String STRINGBUILDER_INIT_INIT_SIGNATURE =
            "<java.lang.StringBuilder: void <init>(java.lang.String)>";
    private static String STRING_BUILDER_APPEND_SIGNATURE =
            "<java.lang.StringBuilder: java.lang.StringBuilder append(java.lang.String)>";

    public static void addToSootConfig() {
        PackManager.v()
                .getPack("jtp")
                .add(new Transform("jtp.myTransformString", new StringOptimizeBodyTransformer()));
        Options.v().set_verbose(true);
        soot.options.Options.v().setPhaseOption("jtp", "enabled:" + true);
        soot.options.Options.v().setPhaseOption("jtp.myTransformString", "enabled:" + true);
    }

    @Override
    protected void internalTransform(Body body, String phaseName, Map<String, String> options) {
        ArrayList<Unit> stringBuilderCallsToOptimize = new ArrayList<>();

        ExceptionalUnitGraph exceptionalUnitGraph = new ExceptionalUnitGraph(body);

        LocalDefs localDefs = LocalDefs.Factory.newLocalDefs(exceptionalUnitGraph);
        LocalUses localUses = LocalUses.Factory.newLocalUses(exceptionalUnitGraph);

        boolean renameLocals = false;

        // Perform a constant/local propagation pass.
        Orderer<Unit> orderer = new PseudoTopologicalOrderer<Unit>();

        // go through each use box in each statement
        for (Unit u : orderer.newList(exceptionalUnitGraph, false)) {

            // get the code where the StringBuilder is assigned or created
            if (!(u instanceof AssignStmt)) {
                continue;
            }
            AssignStmt s = (AssignStmt) u;

            Value lhs = s.getLeftOp();
            if (!(lhs instanceof Local)) {
                continue;
            }
            Value rhs = s.getRightOp();

            if (rhs instanceof InvokeExpr) {
                renameLocals = isUselessCall(body, localUses, u, lhs, (InvokeExpr) rhs);
                if (renameLocals) {
                    LocalNameStandardizer.v().transform(body);
                }
            }

            if (!(rhs instanceof NewExpr)) {
                continue;
            }

            // check if it the string builder
            NewExpr newExpr = (NewExpr) rhs;
            if (!newExpr.getType().toQuotedString().equals("java.lang.StringBuilder")) {
                continue;
            }

            // find the uses of this expr
            List<UnitValueBoxPair> usesOf = localUses.getUsesOf(u);
            // should be 2 uses; one specialIvoke <init>, and one append call
            if (usesOf.size() != 2) {
                continue;
            }

            // check if the first init call is already optimized
            UnitValueBoxPair unitValueBoxPair = usesOf.get(0);
            Unit firstUseUnit = unitValueBoxPair.getUnit();

            if (!(firstUseUnit instanceof InvokeStmt)) {
                continue;
            }

            Expr firstExpr = ((InvokeStmt) firstUseUnit).getInvokeExpr();

            if (!(firstExpr instanceof SpecialInvokeExpr)) {
                // not the init call, we ware looking for
                continue;
            }

            SpecialInvokeExpr stringBuilderInitCall = (SpecialInvokeExpr) firstExpr;
            // check if it is a stringbuilder calls
            boolean isStringBuilderInitSignature =
                    stringBuilderInitCall.getMethodRef().getSignature().equals(STRINGBUILDER_INIT_SIGNATURE);
            if (!isStringBuilderInitSignature) {
                // we don't have to call  the StringBuilder's <init>(String) constructor if it already has
                // an arg
                continue;
            }

            if (!(stringBuilderInitCall.getArgCount() == 0)) {
                continue;
            }

            // check if the next call is the append call
            Unit secondUseUnit = usesOf.get(1).getUnit();

            if (!(secondUseUnit instanceof AssignStmt)) {
                continue;
            }

            Expr secondExpr = (Expr) ((AssignStmt) secondUseUnit).getRightOp();

            if (!(secondExpr instanceof VirtualInvokeExpr)) {
                continue;
            }

            VirtualInvokeExpr stringBuilderAppendCall = ((VirtualInvokeExpr) secondExpr);

            boolean isAppendCallSig =
                    stringBuilderAppendCall
                            .getMethodRef()
                            .getSignature()
                            .equals(STRING_BUILDER_APPEND_SIGNATURE);
            if (!isAppendCallSig) {
                continue;
            }

            if (stringBuilderAppendCall.getArgCount() != 1) {
                continue;
            }

            // we have an empty StringBuilder init call, and the next call is append, thus it is worth
            // optimizing
            // get the units that use the string builder
            // System.out.println("Worth optimizing");

            // 1. exchange the init call with the initialize init call -> new StringBuilder(local)
            // 1.1 get the loc for initialisation
            Value initArg = stringBuilderAppendCall.getArg(0);

            // TODO: 1.1.1 Check if the arg is actually a String

            // 1.2 get methodRef to Init call
            Value baseValue = stringBuilderInitCall.getBase();
            SootClass stringBuilderClass = Scene.v().getSootClass("java.lang.StringBuilder");
            SootMethodRef methodRef =
                    Scene.v()
                            .makeConstructorRef(
                                    stringBuilderClass, Collections.singletonList(RefType.v("java.lang.String")));

            SpecialInvokeExpr stringBuilderInit =
                    Jimple.v().newSpecialInvokeExpr((Local) baseValue, methodRef, initArg);

            // 1.3 rewrite the first unit
            ((InvokeStmt) firstUseUnit).setInvokeExpr(stringBuilderInit);

            // 2. Clean up the append Call

            // 2.1 get the local to which the result of the append call is assigned
            Value valueToRemove = ((JAssignStmt) secondUseUnit).getLeftOp();
            if (!(valueToRemove instanceof Local)) {
                System.out.println("Something is wrong!");
                continue;
            }
            Local localToRemove = (Local) valueToRemove;

            // 2.1 check where the local to remove is used
            List<UnitValueBoxPair> usesOf1 = localUses.getUsesOf(secondUseUnit);
            // redirect to the new optimized local
            for (UnitValueBoxPair valueBoxPair : usesOf1) {
                Unit unit = valueBoxPair.getUnit();
                InvokeExpr toModify = null;
                if (unit instanceof AssignStmt) {
                    Value rightOp = ((AssignStmt) unit).getRightOp();
                    if (rightOp instanceof InvokeExpr) {
                        toModify = (InvokeExpr) rightOp;
                    }
                } else if (unit instanceof InvokeStmt) {
                    toModify = ((InvokeStmt) unit).getInvokeExpr();
                }

                if (toModify != null) {
                    ((InstanceInvokeExpr) toModify).setBase(baseValue);

                    body.getLocals().remove(localToRemove);

                    body.getUnits().remove(firstUseUnit);
                    body.getUnits().insertBefore(firstUseUnit, secondUseUnit);
                    body.getUnits().remove(secondUseUnit);

                    LocalNameStandardizer.v().transform(body);

                } else {
                    System.out.println("Strange no expression found");
                }
            }
        }
    }

    /**
     * remove useless: $r4 = staticinvoke <java.lang.String: java.lang.String //
     * valueOf(java.lang.Object)>($r3);
     *
     * @param body
     * @param localUses
     * @param u
     * @param lhs
     * @param rhs
     * @return
     */
    private boolean isUselessCall(Body body, LocalUses localUses, Unit u, Value lhs, InvokeExpr rhs) {
        boolean renameLocals = false;
        boolean equals =
                rhs.getMethodRef()
                        .getSignature()
                        .equals("<java.lang.String: java.lang.String valueOf(java.lang.Object)>");
        if (equals) {
            Value argument = rhs.getArg(0);
            boolean equals1 = argument.getType().toQuotedString().equals("java.lang.String");
            if (equals1) {
                // we have a call String valueOf(Object) with a String argument, thus this is useless
                // code..

                // FIXME: clean up the mess, especially the duplicate code

                // rewrite the use-units (of the lhs) to use the argument directly
                List<UnitValueBoxPair> usesOf = localUses.getUsesOf(u);
                for (UnitValueBoxPair unitValueBoxPair : usesOf) {
                    Unit unit = unitValueBoxPair.getUnit();
                    if (unit instanceof AssignStmt) {
                        AssignStmt assignStmt = (AssignStmt) unit;
                        Value toDelete = ((AssignStmt) unit).getLeftOp();
                        Value assignemntExpr = ((AssignStmt) unit).getRightOp();
                        if (assignemntExpr instanceof InvokeExpr) {
                            int indexOf = ((InvokeExpr) assignemntExpr).getArgs().indexOf(lhs);
                            if (indexOf > -1) {
                                ((InvokeExpr) unit).setArg(indexOf, argument);
                            }
                        }

                        // remove the local
                        body.getLocals().remove(toDelete);
                    } else if (unit instanceof InvokeStmt) {
                        InvokeExpr assignemntExpr = ((InvokeStmt) unit).getInvokeExpr();
                        int indexOf = assignemntExpr.getArgs().indexOf(lhs);
                        if (indexOf > -1) {
                            assignemntExpr.setArg(indexOf, argument);
                        }
                    }
                }
            }
        }
        // remove this statement
        body.getUnits().remove(u);

        renameLocals = true;
        return renameLocals;
    }
}
