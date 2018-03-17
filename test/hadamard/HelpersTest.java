package hadamard;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.BitSet;

/**
 * Class for the helpers tests.
 */
public class HelpersTest {

    /**
     * Tests the helpers function comparing two BitSet for their Orthogonality.
     */
    @Test
    public void Helpers_IsOrthogonal() {
        BitSet firstColumn = Helpers.convertTo(BigInteger.valueOf(9));
        BitSet secondColumn = Helpers.convertTo(BigInteger.valueOf(12));

        Assert.assertTrue("Should be true.", Helpers.isOrthogonal(firstColumn, secondColumn, 2));

        firstColumn = Helpers.convertTo(BigInteger.valueOf(14));
        secondColumn = Helpers.convertTo(BigInteger.valueOf(15));

        Assert.assertFalse("Should be false.", Helpers.isOrthogonal(firstColumn, secondColumn, 2));
    }

    /**
     * Tests the helpers function for converting a BigInteger value to a BitSet representation.
     */
    @Test
    public void Helpers_ConvertTo() {
        BitSet bitset = Helpers.convertTo(BigInteger.valueOf(9));

        Assert.assertEquals("Should be the same.", true, bitset.get(0));
        Assert.assertEquals("Should be the same.", false, bitset.get(1));
        Assert.assertEquals("Should be the same.", false, bitset.get(2));
        Assert.assertEquals("Should be the same.", true, bitset.get(3));
    }

    /**
     * Tests the helpers function for checking if a matrix is an identity matrix.
     */
    @Test
    public void Helpers_IsIdentity() {
        int[][] testMatrix = {{4, 0, 0, 0}, {0, 4, 0, 0}, {0, 0, 4, 0}, {0, 0, 0, 4}};
        Assert.assertTrue("Should be true.", Helpers.isIdentity(testMatrix));

        testMatrix = new int[][]{{4, 0, 4, 0}, {0, 4, 0, 0}, {0, 0, 4, 0}, {0, 0, 0, 4}};
        Assert.assertFalse("Should be false.", Helpers.isIdentity(testMatrix));

        testMatrix = new int[][]{{4, 0, 0, 0}, {0, 4, 0, 0}, {0, 0, 2, 0}, {0, 0, 0, 4}};
        Assert.assertFalse("Should be false.", Helpers.isIdentity(testMatrix));

        testMatrix = new int[][]{{4, 0, 4, 0}, {0, 4, 0, 1}, {0, 0, 4, 0}, {0, 0, 0, 1}};
        Assert.assertFalse("Should be false.", Helpers.isIdentity(testMatrix));
    }

    /**
     * Tests the helpers function for concatenating two BitSets into one.
     */
    @Test
    public void Helpers_ConcatenateSets() {
        BitSet firstPart = new BitSet();
        for (int i = 0; i < 8; i += 2) {
            firstPart.set(i);
        }
        BitSet secondPart = new BitSet();
        for (int i = 0; i < 8; i++) {
            secondPart.set(i);
        }

        BitSet combinedBitSet = Helpers.concatenateSets(firstPart, secondPart, 8);

        BitSet comparingBitSet = new BitSet();
        for (int i = 0; i < 8; i += 2)
            comparingBitSet.set(i);
        for (int i = 8; i < 16; i++)
            comparingBitSet.set(i);

        Assert.assertTrue("Should be equal.", combinedBitSet.equals(comparingBitSet));
    }
}
