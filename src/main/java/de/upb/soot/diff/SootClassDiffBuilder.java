package de.upb.soot.diff;

import org.apache.commons.lang3.builder.DiffBuilder;
import org.apache.commons.lang3.builder.DiffResult;
import org.apache.commons.lang3.builder.ToStringStyle;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Andreas Dann created on 11.12.18
 */
public class SootClassDiffBuilder extends DiffBuilder {

    private ToStringStyle stringStyle;

    public SootClassDiffBuilder(Object lhs, Object rhs, ToStringStyle style) {
        super(lhs, rhs, style);
        this.stringStyle = style;
    }

    public DiffBuilder append(
            final String className, final SootClass sootClass, final SootClass rhs) {

        this.append("name", sootClass.getJavaStyleName(), rhs.getJavaStyleName());

        this.append("modifier", sootClass.getModifiers(), rhs.getModifiers());

        // check the implemented interfaces

        List<String> lhsInterFaces =
                sootClass.getInterfaces().stream()
                        .map(SootClass::getJavaStyleName)
                        .collect(Collectors.toList());

        List<String> rhsInterfaces =
                rhs.getInterfaces().stream().map(SootClass::getJavaStyleName).collect(Collectors.toList());

        this.append("interfaces", lhsInterFaces, rhsInterfaces);

        // check the super classes

        String lhsSuperClasses = sootClass.getSuperclass().getJavaStyleName();

        String rhsSuperClasses = rhs.getSuperclass().getJavaStyleName();

        this.append("superclass", lhsSuperClasses, rhsSuperClasses);

        // check the fields

        List<SootField> rhsFieldsToVisit = new ArrayList<>(rhs.getFields());

        // append the diff results of the methods ...
        for (SootField lhsField : sootClass.getFields()) {
            try {
                SootField rhsField = rhs.getField(lhsField.getSubSignature());
                this.append(lhsField.getName(), lhsField.getSignature(), rhsField.getSignature());

                // remove from the methods to visit
                rhsFieldsToVisit.remove(rhsField);

            } catch (RuntimeException ex) {
                // no corresponding righthand method found
                // dummy method to compute diff
                this.append(lhsField.getName(), lhsField.getSignature(), null);
            }
        }

        // get the diff for fields that are in the right class but not in the lhs
        for (SootField rhsField : rhsFieldsToVisit) {
            // no lhsMethod exists
            // create dummy lhsMethod
            // SootField lhsField = new SootField(null, null, 0);
            this.append(rhsField.getName(), null, rhsField.getSignature());
        }

        // check the methods
        List<SootMethod> rhsMethodsToVisit = new ArrayList<>(rhs.getMethods());

        // append the diff results of the methods ...
        for (SootMethod lhsMethod : sootClass.getMethods()) {
            try {
                SootMethod rhsMethod = rhs.getMethod(lhsMethod.getSubSignature());
                this.append(lhsMethod.getName(), createDiffBuilderBody(lhsMethod, rhsMethod));

                // remove from the methods to visit
                rhsMethodsToVisit.remove(rhsMethod);

            } catch (RuntimeException ex) {
                // no corresponding righthand method found
                // dummy method to compute diff
                this.append(lhsMethod.getName(), lhsMethod, null);
            }
        }

        // get the diff for methods that are in the right class but not in the lhs
        for (SootMethod rhsMethod : rhsMethodsToVisit) {
            // no lhsMethod exists
            // create dummy lhsMethod
            SootMethod lhsMethod = null;
            // DiffResult diff = new DiffSootMethod(lhsMethod).diff(new DiffSootMethod(rhsMethod));
            this.append(rhsMethod.getName(), null, rhsMethod);
        }

        return this;
    }

    //  public DiffBuilder append(final String methodName, final SootMethod lhs, final SootMethod rhs)
    // {
    //
    //    if (lhs == null) {
    //      return this.append(rhs.getName(), null, rhs);
    //    }
    //    if (rhs == null) {
    //      return this.append(lhs.getName(), lhs, null);
    //    }
    //
    //    return this.append(methodName, createDiffBuilderBody(lhs, rhs));
    //  }

    private DiffResult createDiffBuilderBody(SootMethod sootMethod, SootMethod rhs) {
        return new SootMethodDiffBuilder(sootMethod, rhs, this.stringStyle).build();
    }
}
