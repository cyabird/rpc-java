package org.cyabird.extension;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ExtensionLoaderAdaptiveTest {

    @Test
    public void test_useAdaptiveClass() throws Exception {
        ExtensionLoader<HasAdaptiveExt> loader = ExtensionLoader.getExtensionLoader(HasAdaptiveExt.class);
        HasAdaptiveExt ext = loader.getAdaptiveExtension();
        assertTrue(ext instanceof HasAdaptiveExt_ManualAdaptive);
    }

}
