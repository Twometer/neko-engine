package de.twometer.orion.gui.core;

public interface IPropertyDecoder<T> {

    T decode(String str, Class<?> requiredType);

}
