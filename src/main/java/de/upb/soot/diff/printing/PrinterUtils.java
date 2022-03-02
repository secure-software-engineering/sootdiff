package de.upb.soot.diff.printing;

import com.google.common.base.Strings;
import soot.ArrayType;
import soot.PrimType;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.VoidType;

import java.util.List;

public class PrinterUtils {
    public static String simplifyTypeName(Type type) {
        String typeName = type.toQuotedString();
        if (type instanceof RefType) {
            return simplifyClassName(((RefType) type).getSootClass());
        } else if (type instanceof ArrayType) {
            return simplifyTypeName(((ArrayType) type).baseType)
                    + Strings.repeat("[]", ((ArrayType) type).numDimensions);
        } else if (type instanceof PrimType) {
            return typeName;
        } else if (type instanceof VoidType) {
            return typeName;
        }

        throw new IllegalStateException("Unhandled Soot type " + type);
    }

    public static String simplifyClassName(SootClass cl) {
        if (Scene.v().isExcluded(cl)) {
            return (Scene.v().quotedNameOf(cl.getName()));
        } else {
            return (Scene.v().quotedNameOf(cl.getShortName()));
        }
    }

    public static String getSignature(SootClass cl, String subSignature) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("<");
        buffer.append(simplifyClassName(cl));
        buffer.append(": ");
        buffer.append(subSignature);
        buffer.append(">");
        return buffer.toString();
    }

    public static String getSubSignature(String name, List<Type> params, Type returnType) {
        StringBuilder buffer = new StringBuilder();
        buffer.append(simplifyTypeName(returnType));
        buffer.append(" ");
        buffer.append(Scene.v().quotedNameOf(name));
        buffer.append("(");
        if (params != null) {
            for (int i = 0; i < params.size(); ++i) {
                buffer.append(simplifyTypeName((Type) params.get(i)));
                if (i < params.size() - 1) {
                    buffer.append(",");
                }
            }
        }

        buffer.append(")");
        return buffer.toString();
    }

    public static String getMethodSignature(SootMethod method) {
        return getSubSignature(method.getName(), method.getParameterTypes(), method.getReturnType());
    }
}
