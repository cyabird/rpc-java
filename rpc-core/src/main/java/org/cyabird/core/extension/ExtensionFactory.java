package org.cyabird.core.extension;

@SPI
public interface ExtensionFactory {

    <T> T getExtension(Class<T> type, String name);

}
