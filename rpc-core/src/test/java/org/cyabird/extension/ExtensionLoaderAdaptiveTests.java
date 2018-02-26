package org.cyabird.extension;

import org.cyabird.extension.adaptive.HasAdaptiveExt;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ExtensionLoaderAdaptiveTests {

    @Test
    public void testUseAdaptiveClass() throws Exception {
        ExtensionLoader<HasAdaptiveExt> loader = ExtensionLoader.getExtensionLoader(HasAdaptiveExt.class);
        HasAdaptiveExt ext = loader.getAdaptiveExtension();
        assertTrue(ext instanceof HasAdaptiveExt_ManualAdaptive);
    }

}
