package fallk.logmaster;

import static elemental2.dom.DomGlobal.console;

public final class HLogger {
    public static void info(Object... args) {
        console.info(args);
    }

    public static void error(Object... args) {
        console.error(args);
    }

    public static void warn(Object... args) {
        console.warn(args);
    }
}
