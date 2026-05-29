package org.javalabs.decl.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Sudiptasish Chanda
 */
public class CharUtilTest {
    
    @Test
    public void testLowerFirst() {
        String noun = "PanInfo";
        String result = CharUtil.lowerFirst(noun);
        Assertions.assertEquals("panInfo", result);
    }
    
    @Test
    public void testSingular() {
        String noun = "pricing_rules";
        String result = CharUtil.singular(noun);
        Assertions.assertEquals("PricingRule", result);
    }
}
