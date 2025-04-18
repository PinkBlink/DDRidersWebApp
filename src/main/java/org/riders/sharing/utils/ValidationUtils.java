package org.riders.sharing.utils;

import java.util.function.Supplier;

public class ValidationUtils {
    public static void checkThat(boolean expression, Supplier<? extends RuntimeException> supplier) {
        if (!expression) {
            throw supplier.get();
        }
    }
}
