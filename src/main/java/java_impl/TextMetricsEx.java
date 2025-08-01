package java_impl;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "TextMetrics")
public class TextMetricsEx {
    public double width;
    public double actualBoundingBoxLeft;
    public double actualBoundingBoxRight;
    public double fontBoundingBoxAscent;
    public double fontBoundingBoxDescent;
    public double actualBoundingBoxAscent;
    public double actualBoundingBoxDescent;
    public double emHeightAscent;
    public double emHeightDescent;
    public double hangingBaseline;
    public double alphabeticBaseline;
    public double ideographicBaseline;
}
