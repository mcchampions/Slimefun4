package me.qscbm.slimefun4.utils;

import java.lang.invoke.*;
import java.lang.reflect.Method;

public class QsReflectionUtils {
    public static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    public static final MethodType GETTER_METHOD_TYPE = MethodType.methodType(Object.class, Object.class);

    public static ReflectionGetterMethodFunction createGetterFunction(Method method) {
        try {
            MethodHandle methodHandle = LOOKUP.unreflect(method);
            CallSite callSite = LambdaMetafactory.metafactory(
                    LOOKUP,
                    "invoke",
                    MethodType.methodType(ReflectionGetterMethodFunction.class),
                    GETTER_METHOD_TYPE,
                    methodHandle,
                    methodHandle.type()
            );
            return (ReflectionGetterMethodFunction) callSite.getTarget().invoke();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
