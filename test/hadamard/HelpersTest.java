package hadamard;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.BitSet;

public class HelpersTest {
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

    @Test
    public void Helpers_ConcatenateSets() {
        BitSet firstPart = new BitSet();
        for (int i = 0; i < 8; i += 2){
            firstPart.set(i);
        }
        BitSet secondPart = new BitSet();
        for (int i = 0; i < 8; i++){
            secondPart.set(i);
        }

        BitSet combinedBitSet = Helpers.concatenateSets(firstPart, secondPart, 8);

        BitSet comparingBitSet = new BitSet();
        for(int i = 0; i < 8; i+=2)
            comparingBitSet.set(i);
        for (int i = 8; i < 16; i++)
            comparingBitSet.set(i);

        Assert.assertTrue("Should be equal.", combinedBitSet.equals(comparingBitSet));
    }
}
