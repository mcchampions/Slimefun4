package city.norain.slimefun4.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ClassUtil {
    public String getCallerClass() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        if (stackTrace.length > 3) {
            return stackTrace[3].getClassName();
        } else {
            return null;
        }
    }
}
