package de.twometer.orion.gui.core;

public class DefaultPropertyDecoder implements IPropertyDecoder<Object> {

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Object decode(String str, Class<?> requiredType) {
        if (requiredType == int.class) {
            return Integer.parseInt(str);
        } else if (requiredType == double.class) {
            return Double.parseDouble(str);
        } else if (requiredType == float.class) {
            return Float.parseFloat(str);
        } else if (requiredType == String.class) {
            return str;
        } else if (requiredType == Size.class) {
            var s = str.split(",");
            return new Size(Integer.parseInt(s[0]), Integer.parseInt(s[1]));
        } else if (requiredType == Point.class) {
            var s = str.split(",");
            return new Point(Integer.parseInt(s[0]), Integer.parseInt(s[1]));
        } else if (requiredType.isEnum()) {
            return Enum.valueOf((Class) requiredType, str);
        }
        throw new IllegalArgumentException("Default decoder can't decode " + requiredType + " format.");
    }

}
