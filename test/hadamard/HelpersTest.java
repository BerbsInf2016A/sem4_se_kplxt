package hadamard;

import org.junit.Assert;
import org.junit.Test;

public class HelpersTest {
    @Test
    public void HelpersTest_IsIdentity() {
        int[][] testMatrix = {{4, 0, 0, 0}, {0, 4, 0, 0}, {0, 0, 4, 0}, {0, 0, 0, 4}};
        Assert.assertTrue("Should be true.", Helpers.isIdentity(testMatrix));

        testMatrix = new int[][]{{4, 0, 4, 0}, {0, 4, 0, 0}, {0, 0, 4, 0}, {0, 0, 0, 4}};
        Assert.assertFalse("Should be false.", Helpers.isIdentity(testMatrix));

        testMatrix = new int[][]{{4, 0, 0, 0}, {0, 4, 0, 0}, {0, 0, 2, 0}, {0, 0, 0, 4}};
        Assert.assertFalse("Should be false.", Helpers.isIdentity(testMatrix));

        testMatrix = new int[][]{{4, 0, 4, 0}, {0, 4, 0, 1}, {0, 0, 4, 0}, {0, 0, 0, 1}};
        Assert.assertFalse("Should be false.", Helpers.isIdentity(testMatrix));
    }
}
