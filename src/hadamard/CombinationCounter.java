package implementation;
// TODO Can be removed if not needed anymore.
public class CombinationCounter {
    public static void main(String[] args) {
        System.out.println("Count: " + getCountOfPossibleColumns(8));
    }

    private static long getCountOfValidMatrix(short [][] matrix, int position, int n) {
        if(position == n - 1) {
            long counter = 0;
            for (Integer i = 0; i < Math.pow(2, n-1); i++) {
                for (int y = 0; y < n - 1; y++)
                    matrix[position][y] = (((i >> y) & 1) == 0 ? (short) 1 : (short) -1);
                if(isOrthogonal(matrix[position-1], matrix[position], n))
                    counter++;
            }
            return counter;
        }

        long counter = 0;

        for (Integer i = 0; i < Math.pow(2, n-1); i++) {
            for (int y = 0; y < n - 1; y++)
                matrix[position][y] = (((i >> y) & 1) == 0 ? (short) 1 : (short) -1);
            if(isOrthogonal(matrix[position - 1], matrix[position], n))
                counter += getCountOfValidMatrix(matrix, position+1, n);
        }

        return counter;
    }

    private static long getCountOfPossibleColumns(int n) {
        short[][] matrix = new short[n][n];

        for (Integer i = 0; i < n; i++)
            matrix[0][i] = 1;

        for (Integer i = 0; i < n; i++)
            matrix[i][n-1] = 1;

        return getCountOfValidMatrix(matrix, 1, n);
    }

    private static boolean isOrthogonal(short[] column1, short[] column2, int n) {
        int sum = 0;
        for (int i = 0; i < n; i++)
            sum += column1[i] * column2[i];
        return sum == 0;
    }
}