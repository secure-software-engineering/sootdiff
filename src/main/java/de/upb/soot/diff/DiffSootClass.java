package de.upb.soot.diff;

import org.apache.commons.lang3.builder.DiffBuilder;
import org.apache.commons.lang3.builder.DiffResult;
import org.apache.commons.lang3.builder.Diffable;
import org.apache.commons.lang3.builder.ToStringStyle;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


//FIXME: remove the old version
/** @author Andreas Dann created on 06.12.18 */
public class DiffSootClass implements Diffable<DiffSootClass> {

  public SootClass sootClass;

  public DiffSootClass(SootClass sootClass) {
    this.sootClass = sootClass;
  }


  //instead of this it may make sense to subclass the diffbuilder....

  public DiffResult diff(DiffSootClass rhs) {
    DiffBuilder diffBuilder = new DiffBuilder(this, rhs, ToStringStyle.JSON_STYLE);
    diffBuilder =
        diffBuilder.append("name", sootClass.getJavaStyleName(), rhs.sootClass.getJavaStyleName());

    diffBuilder =
        diffBuilder.append("modifier", sootClass.getModifiers(), rhs.sootClass.getModifiers());

    // check the implemented interfaces

    List<String> lhsInterFaces =
        sootClass
            .getInterfaces()
            .stream()
            .map(intf -> intf.getJavaStyleName())
            .collect(Collectors.toList());

    List<String> rhsInterfaces =
        rhs.sootClass
            .getInterfaces()
            .stream()
            .map(intf -> intf.getJavaStyleName())
            .collect(Collectors.toList());

    diffBuilder = diffBuilder.append("interfaces", lhsInterFaces, rhsInterfaces);

    // check the super classes

    String lhsSuperClasses = sootClass.getSuperclass().getJavaStyleName();

    String rhsSuperClasses = rhs.sootClass.getSuperclass().getJavaStyleName();

    diffBuilder = diffBuilder.append("superclass", lhsSuperClasses, rhsSuperClasses);

    // check the fields

    List<SootField> rhsFieldsToVisit = new ArrayList<>(rhs.sootClass.getFields());

    // append the diff results of the methods ...
    for (SootField lhsField : this.sootClass.getFields()) {
      try {
        SootField rhsField = rhs.sootClass.getField(lhsField.getSubSignature());
        diffBuilder =
            diffBuilder.append(
                lhsField.getName(), lhsField.getSignature(), rhsField.getSignature());

        // remove from the methods to visit
        rhsFieldsToVisit.remove(rhsField);

      } catch (RuntimeException ex) {
        // no corresponding righthand method found
        // dummy method to compute diff
        diffBuilder = diffBuilder.append(lhsField.getName(), lhsField.getSignature(), null);
      }
    }

    // get the diff for fields that are in the right class but not in the lhs
    for (SootField rhsField : rhsFieldsToVisit) {
      // no lhsMethod exists
      // create dummy lhsMethod
      // SootField lhsField = new SootField(null, null, 0);
      diffBuilder = diffBuilder.append(rhsField.getName(), null, rhsField.getSignature());
    }

    // check the methods
    List<SootMethod> rhsMethodsToVisit = new ArrayList<>(rhs.sootClass.getMethods());

    // append the diff results of the methods ...
    for (SootMethod lhsMethod : this.sootClass.getMethods()) {
      try {
        SootMethod rhsMethod = rhs.sootClass.getMethod(lhsMethod.getSubSignature());
        DiffResult diff = new DiffSootMethod(lhsMethod).diff(new DiffSootMethod(rhsMethod));
        diffBuilder = diffBuilder.append(lhsMethod.getName(), diff);

        // remove from the methods to visit
        rhsMethodsToVisit.remove(rhsMethod);

      } catch (RuntimeException ex) {
        // no corresponding righthand method found
        // dummy method to compute diff
        diffBuilder = diffBuilder.append(lhsMethod.getName(), new DiffSootMethod(lhsMethod), null);
      }
    }

    // get the diff for methods that are in the right class but not in the lhs
    for (SootMethod rhsMethod : rhsMethodsToVisit) {
      // no lhsMethod exists
      // create dummy lhsMethod
      SootMethod lhsMethod = null;
      // DiffResult diff = new DiffSootMethod(lhsMethod).diff(new DiffSootMethod(rhsMethod));
      diffBuilder = diffBuilder.append(rhsMethod.getName(), null, new DiffSootMethod(rhsMethod));
    }

    return diffBuilder.build();
  }
}
