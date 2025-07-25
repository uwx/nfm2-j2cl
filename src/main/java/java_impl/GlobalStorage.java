package java_impl;

import elemental2.webstorage.Storage;
import jsinterop.annotations.JsType;

@JsType(
        isNative = true,
        name = "goog.global",
        namespace = "<global>"
)
public class GlobalStorage {
    public static Storage localStorage;
    public static Storage sessionStorage;
}
