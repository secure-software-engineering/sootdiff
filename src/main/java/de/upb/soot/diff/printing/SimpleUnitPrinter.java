package de.upb.soot.diff.printing;

import soot.Body;
import soot.NormalUnitPrinter;
import soot.RefType;
import soot.Scene;
import soot.SootFieldRef;
import soot.SootMethodRef;
import soot.Type;
import soot.jimple.IdentityRef;

/**
 * Omits the package name of classes that are not in the JDK. Useful for minhashing for example.
 */
public class SimpleUnitPrinter extends NormalUnitPrinter {
    public SimpleUnitPrinter(Body body) {
        super(body);
    }

    public static String getSimpleMethodRef(SootMethodRef methodRef) {
        String subSignature =
                PrinterUtils.getSubSignature(
                        methodRef.getName(), methodRef.getParameterTypes(), methodRef.getReturnType());
        return PrinterUtils.getSignature(methodRef.getDeclaringClass(), subSignature);
    }

    public static String getSimpleFieldRef(SootFieldRef f) {
        return PrinterUtils.getSignature(f.declaringClass(), getSubSignature(f.name(), f.type()));
    }

    public static String getSubSignature(String name, Type type) {
        StringBuilder buffer = new StringBuilder();
        buffer.append(PrinterUtils.simplifyTypeName(type) + " " + Scene.v().quotedNameOf(name));
        return buffer.toString();
    }

    @Override
    public void type(Type t) {
        handleIndent();
        String s = t == null ? "<null>" : PrinterUtils.simplifyTypeName(t);
        output.append(s);
    }

    @Override
    public void methodRef(SootMethodRef m) {
        handleIndent();
        output.append(getSimpleMethodRef(m));
    }

    @Override
    public void fieldRef(SootFieldRef f) {
        handleIndent();
        output.append(getSimpleFieldRef(f));
    }

    @Override
    public void identityRef(IdentityRef r) {
        Type type = r.getType();

        if (type instanceof RefType) {
            handleIndent();
            output.append(PrinterUtils.simplifyClassName(((RefType) type).getSootClass()));
        } else {
            super.identityRef(r);
        }
    }
}
