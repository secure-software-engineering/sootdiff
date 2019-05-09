package de.upb.soot.diff;

import com.github.difflib.DiffUtils;
import com.github.difflib.algorithm.DiffException;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.DeltaType;
import com.github.difflib.patch.Patch;
import org.apache.commons.lang3.builder.DiffBuilder;
import org.apache.commons.lang3.builder.DiffResult;
import org.apache.commons.lang3.builder.Diffable;
import org.apache.commons.lang3.builder.ToStringStyle;
import soot.Body;
import soot.SootMethod;
import soot.Unit;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/** @author Andreas Dann created on 06.12.18 */
public class DiffSootMethod implements Diffable<DiffSootMethod> {

  public SootMethod sootMethod;

  public DiffSootMethod(SootMethod sootMethod) {
    this.sootMethod = sootMethod;
  }

  //FIXME: maye more reasonable to subclass diffbuilder...

  // FIXME: work on strings because there are no equal method

  public DiffResult diff(DiffSootMethod rhs) {

    DiffBuilder diffBuilder = new DiffBuilder(this, rhs, ToStringStyle.JSON_STYLE);

    diffBuilder = diffBuilder.append("name", this.sootMethod.getName(), rhs.sootMethod.getName());
    diffBuilder =
        diffBuilder.append(
            "returnType",
            this.sootMethod.getReturnType().toString(),
            rhs.sootMethod.getReturnType().toString());

    List<String> lhsParameter =
        this.sootMethod
            .getParameterTypes()
            .stream()
            .map(p -> p.toString())
            .collect(Collectors.toList());
    List<String> rhsParameter =
        rhs.sootMethod
            .getParameterTypes()
            .stream()
            .map(p -> p.toString())
            .collect(Collectors.toList());

    diffBuilder = diffBuilder.append("parameters", lhsParameter, rhsParameter);

    // check for active body
    if (sootMethod.hasActiveBody()) {

      diffBuilder = createDiffBuilderBody(diffBuilder, rhs.sootMethod);

    } else {

      if (rhs.sootMethod.hasActiveBody()) {
        diffBuilder = diffBuilder.append("body", null, rhs.sootMethod.getActiveBody().toString());
      } else {
        diffBuilder = diffBuilder.append("body", (Object) null, null);
      }
    }

    return diffBuilder.build();
  }

  private DiffBuilder createDiffBuilderBody(DiffBuilder diffBuilder, SootMethod rhs) {
    SootMethod lhs = this.sootMethod;
    if (lhs.hasActiveBody()) {

      if (rhs.hasActiveBody()) {
        // lhs and rhs have an active body..
        // FIXME: use Myer's diff to compute diff....,
        // Brenda S. Baker: A Program for Identifying Duplicated Code
        // therefore separate into common subsequences

        Body lhsBody = this.sootMethod.getActiveBody();
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
            diffBuilder =
                diffBuilder.append(
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
                Arrays.stream(lhsBodyArray).map(stm ->"\""+ stm.toString()+"\"").collect(Collectors.toList());
            List<String> rhsBodyStatements =
                Arrays.stream(rhsBodyArray).map(stm -> "\""+ stm.toString()+"\"").collect(Collectors.toList());
            Patch<String> patch = null;

            patch = DiffUtils.diff(lhsBodyStatements, rhsBodyStatements);
            List<AbstractDelta<String>> deltas = patch.getDeltas();
            for (AbstractDelta<String> delta : deltas) {

              //FIXME: maybe report as diff Result
              //Thus wrapper class for Delta with type DiffResult
              if (delta.getType() == DeltaType.INSERT) {
                diffBuilder =
                    diffBuilder.append(
                        Integer.toString(delta.getSource().getPosition()),
                        (Object) null,
                        delta.getTarget().getLines());
              }

              if (delta.getType() == DeltaType.CHANGE) {
                diffBuilder =
                    diffBuilder.append(
                        Integer.toString(delta.getSource().getPosition()),
                        delta.getSource().getLines(),
                        delta.getTarget().getLines());
              }

              if (delta.getType() == DeltaType.DELETE) {
                diffBuilder =
                    diffBuilder.append(
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
                Arrays.stream(lhsBodyArray).map(stm -> "\""+ stm.toString()+"\"").collect(Collectors.toList());
            List<String> rhsBodyStatements =
                Arrays.stream(rhsBodyArray).map(stm -> "\""+stm.toString()+"\"").collect(Collectors.toList());
            Patch<String> patch = null;

            patch = DiffUtils.diff(lhsBodyStatements, rhsBodyStatements);
            List<AbstractDelta<String>> deltas = patch.getDeltas();
            for (AbstractDelta<String> delta : deltas) {
              System.out.println(delta);
              if (delta.getType() == DeltaType.INSERT) {
                diffBuilder =
                    diffBuilder.append(
                        Integer.toString(delta.getSource().getPosition()),
                        (Object) null,
                        delta.getTarget().getLines());
              }

              if (delta.getType() == DeltaType.CHANGE) {
                diffBuilder =
                    diffBuilder.append(
                        Integer.toString(delta.getSource().getPosition()),
                        delta.getSource().getLines(),
                        delta.getTarget().getLines());
              }

              if (delta.getType() == DeltaType.DELETE) {
                diffBuilder =
                    diffBuilder.append(
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
        Body lhsBody = this.sootMethod.getActiveBody();
        int positionInLoop = 0;
        for (Iterator<Unit> i = lhsBody.getUnits().iterator(); i.hasNext(); ) {
          Unit unit = i.next();
          diffBuilder =
              diffBuilder.append(
                  Integer.toString(positionInLoop++),"\""+  unit.toString() +"\"", (Object) null);
        }
      }

    } else {

      if (rhs.hasActiveBody()) {
        // lhs does not have an active body...
        Body rhsBody = rhs.getActiveBody();
        int positionInLoop = 0;
        for (Iterator<Unit> i = rhsBody.getUnits().iterator(); i.hasNext(); ) {
          Unit unit = i.next();
          diffBuilder =
              diffBuilder.append(
                  Integer.toString(positionInLoop++), (Object) null, "\""+ unit.toString()+"\"");
        }
      } else {
        diffBuilder = diffBuilder.append("body", (Object) null, null);
      }
    }

    return diffBuilder;
  }
}
