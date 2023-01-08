package de.upb.soot.diff;

import com.github.difflib.DiffUtils;
import com.github.difflib.algorithm.DiffException;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.DeltaType;
import com.github.difflib.patch.Patch;
import org.apache.commons.lang3.builder.DiffBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import soot.Body;
import soot.SootMethod;
import soot.Type;
import soot.Unit;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Andreas Dann created on 07.01.19
 */
public class SootMethodDiffBuilder extends DiffBuilder {

    public SootMethodDiffBuilder(SootMethod lhs, SootMethod rhs, ToStringStyle style) {
        super(lhs, rhs, style);

        if (lhs == null) {
            this.append(lhs.getName(), lhs, null);
            return;
        }
        if (rhs == null) {
            this.append(rhs.getName(), null, rhs);
            return;
        }

        this.append("name", lhs.getName(), rhs.getName());
        this.append("returnType", lhs.getReturnType().toString(), rhs.getReturnType().toString());

        List<String> lhsParameter =
                lhs.getParameterTypes().stream().map(Type::toString).collect(Collectors.toList());
        List<String> rhsParameter =
                rhs.getParameterTypes().stream().map(Type::toString).collect(Collectors.toList());

        this.append("parameters", lhsParameter, rhsParameter);

        if (lhs.hasActiveBody()) {
            createDiffBuilderBody(lhs, rhs);
        } else {

            if (rhs.hasActiveBody()) {
                this.append("body", null, rhs.getActiveBody().toString());
            } else {
                this.append("body", (Object) null, null);
            }
        }
    }

    private void createDiffBuilderBody(SootMethod sootMethod, SootMethod rhs) {
        if (sootMethod.hasActiveBody()) {

            if (rhs.hasActiveBody()) {
                // lhs and rhs have an active body..
                //  use Myer's diff to compute diff...., largest common subsquences

                Body lhsBody = sootMethod.getActiveBody();
                Body rhsBody = rhs.getActiveBody();

                Unit[] lhsBodyArray = new Unit[lhsBody.getUnits().size()];
                lhsBodyArray = lhsBody.getUnits().toArray(lhsBodyArray);
                Unit[] rhsBodyArray = new Unit[rhsBody.getUnits().size()];
                rhsBodyArray = rhsBody.getUnits().toArray(rhsBodyArray);

                // lhs.size == rhs.size
                if (lhsBodyArray.length == rhsBodyArray.length) {

                    // compare statement wise
                    for (int positionInBody = 0;
                         positionInBody < lhsBody.getUnits().size();
                         positionInBody++) {
                        this.append(
                                Integer.toString(positionInBody),
                                lhsBodyArray[positionInBody].toString(),
                                rhsBodyArray[positionInBody].toString());
                    }
                }

                // FIXME: check me, maybe it makes sense to merge all cases
                // lhs < rhs (something has been added to rhs?)
                else if (lhsBodyArray.length < rhsBodyArray.length) {

                    try {
                        // FIXME: for now, compare the statements based on string
                        List<String> lhsBodyStatements =
                                Arrays.stream(lhsBodyArray).map(Object::toString).collect(Collectors.toList());
                        List<String> rhsBodyStatements =
                                Arrays.stream(rhsBodyArray).map(Object::toString).collect(Collectors.toList());
                        Patch<String> patch = null;

                        patch = DiffUtils.diff(lhsBodyStatements, rhsBodyStatements);
                        List<AbstractDelta<String>> deltas = patch.getDeltas();
                        for (AbstractDelta<String> delta : deltas) {

                            // FIXME: maybe report as diff Result
                            // Thus wrapper class for Delta with type DiffResult
                            if (delta.getType() == DeltaType.INSERT) {
                                this.append(
                                        Integer.toString(delta.getSource().getPosition()),
                                        (Object) null,
                                        delta.getTarget().getLines());
                            }

                            if (delta.getType() == DeltaType.CHANGE) {
                                this.append(
                                        Integer.toString(delta.getSource().getPosition()),
                                        delta.getSource().getLines(),
                                        delta.getTarget().getLines());
                            }

                            if (delta.getType() == DeltaType.DELETE) {
                                this.append(
                                        Integer.toString(delta.getSource().getPosition()),
                                        delta.getSource().getLines(),
                                        (Object) null);
                            }
                        }

                    } catch (DiffException e) {
                        e.printStackTrace();
                    }

                }
                // lhs > rhs (something has been removed from rhs)
                else {

                    try {
                        // FIXME: for now, compare the statements based on string
                        List<String> lhsBodyStatements =
                                Arrays.stream(lhsBodyArray).map(Object::toString).collect(Collectors.toList());
                        List<String> rhsBodyStatements =
                                Arrays.stream(rhsBodyArray).map(Object::toString).collect(Collectors.toList());
                        Patch<String> patch = null;

                        patch = DiffUtils.diff(lhsBodyStatements, rhsBodyStatements);
                        List<AbstractDelta<String>> deltas = patch.getDeltas();
                        // FIXME: add directly to set diff and create wrapper around delta using Diff class
                        for (AbstractDelta<String> delta : deltas) {
                            if (delta.getType() == DeltaType.INSERT) {
                                this.append(
                                        Integer.toString(delta.getSource().getPosition()),
                                        (Object) null,
                                        delta.getTarget().getLines());
                            }

                            if (delta.getType() == DeltaType.CHANGE) {
                                this.append(
                                        Integer.toString(delta.getSource().getPosition()),
                                        delta.getSource().getLines(),
                                        delta.getTarget().getLines());
                            }

                            if (delta.getType() == DeltaType.DELETE) {
                                this.append(
                                        Integer.toString(delta.getSource().getPosition()),
                                        delta.getSource().getLines(),
                                        (Object) null);
                            }
                        }

                    } catch (DiffException e) {
                        e.printStackTrace();
                    }
                }

            } else {

                // rhs does not have an active body...
                Body lhsBody = sootMethod.getActiveBody();
                int positionInLoop = 0;
                for (Iterator<Unit> i = lhsBody.getUnits().iterator(); i.hasNext(); ) {
                    Unit unit = i.next();
                    this.append(Integer.toString(positionInLoop++), unit.toString(), (Object) null);
                }
            }

        } else {

            if (rhs.hasActiveBody()) {
                // lhs does not have an active body...
                Body rhsBody = rhs.getActiveBody();
                int positionInLoop = 0;
                for (Iterator<Unit> i = rhsBody.getUnits().iterator(); i.hasNext(); ) {
                    Unit unit = i.next();
                    this.append(Integer.toString(positionInLoop++), (Object) null, unit.toString());
                }
            } else {
                this.append("body", (Object) null, null);
            }
        }
    }
}
