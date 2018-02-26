package hadamard;

public class Helpers {
    public static boolean isIdentity(int[][] matrix){
        if(matrix.length == 0)
            throw new RuntimeException("Illegal matrix dimensions.");
        if(matrix.length != matrix[0].length)
            throw new RuntimeException("Illegal matrix dimensions.");

        int identityValue = matrix[0][0];

        /**
         * Source: http://www.penguincoders.net/2015/05/check-if-matrix-is-identity-matrix-in-java.html
         */
        boolean flag = true;
        for(int i=0;i<matrix.length;i++) {
            for(int j=0;j<matrix.length;j++)
                if((i==j && matrix[i][j] != identityValue) || (i!=j && matrix[i][j] != 0)) {
                    flag = false;
                    break;
                }
            if(!flag)
                return false;
        }
        return true;
    }
}
