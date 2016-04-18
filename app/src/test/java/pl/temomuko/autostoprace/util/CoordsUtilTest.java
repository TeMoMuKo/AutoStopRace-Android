package pl.temomuko.autostoprace.util;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Created by Szymon Kozak on 2016-04-17.
 */
public class CoordsUtilTest {

    @Test
    public void testGetDmsTextFromDecimalDegrees() throws Exception {
        String actual = CoordsUtil.getDmsTextFromDecimalDegrees(40.7127836, -74.00594111);
        String expected = "N 40°42’46”, W 74°0’21”";
        assertEquals(expected, actual);

        actual = CoordsUtil.getDmsTextFromDecimalDegrees(51.1078852, 17.03853760000004);
        expected = "N 51°6’28”, E 17°2’19”";
        assertEquals(expected, actual);
    }
}