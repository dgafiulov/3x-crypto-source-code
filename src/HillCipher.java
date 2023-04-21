import java.util.Arrays;
import java.util.Objects;

public class HillCipher {

    public void testFunc() {

        int[][] matr = keyToMatrix("doggy");
        int[][] m = inverseMatrix(matr);
        System.out.println(Arrays.deepToString(m));

    }

    //main functions
    public byte[] encode(byte[] byteOrig, String key) {
        int[] orig = byteToIntArray(byteOrig);
        int[][] keyMatrix = keyToMatrix(key);
        int[] encoded = bytesEncode(orig, keyMatrix);
        return intToByteArray(encoded);
    }

    private int[] bytesEncode(int[] bytes, int[][] key) {
        int[] multiplyRes;
        int[] finalRes = new int[bytes.length];
        for (int i = 0; i < bytes.length; i = i + 2) {
            multiplyRes = matrixMultiply(new int[]{bytes[i], bytes[i + 1]}, key);
            finalRes[i] = multiplyRes[0];
            finalRes[i + 1] = multiplyRes[1];
        }
        return finalRes;
    }

    /*
    public int[] decode(byte[] encoded, String key) {
        int[][] keyMatrix = keyToMatrix(key);
        int[][] inverseKeyMatrix = inverseMatrix(keyMatrix);

        return bytesDecode(encoded, inverseKeyMatrix);
    }

    private double[] bytesDecode(byte[] bytes, double[][] key) {
        double[] multiplyRes;
        double[] finalRes = new double[bytes.length];
        for (int i = 0; i < bytes.length; i = i + 2) {
            multiplyRes = matrixMultiply(new double[]{bytes[i], bytes[i + 1]}, key);
            finalRes[i] = multiplyRes[0];
            finalRes[i + 1] = multiplyRes[1];
        }
        return finalRes;
    }
     */

    //general matrix operations
    private int[][] byteToIntMatrix(byte[][] orig) {
        int[][] matrix = new int[orig.length][orig[0].length];

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                matrix[i][j] = orig[i][j] + 128;
            }
        }

        return matrix;
    }

    private int[] byteToIntArray(byte[] orig) {
        int[] matrix = new int[orig.length];

        for (int i = 0; i < matrix.length; i++) {
            matrix[i] = orig[i] + 128;
        }

        return matrix;
    }

    private byte[] intToByteArray(int[] orig) {
        byte[] matrix = new byte[orig.length];
        for (int i = 0; i < matrix.length; i++) {
            matrix[i] = (byte) (orig[i] - 128);
        }
        return matrix;
    }

    private int[][] keyToMatrix(String key) {
        if (Objects.equals(key, "")) {
            key = "key";
        }

        int[] bytes = new int[key.getBytes().length];
        int[][] keyMatrix = new int[2][2];

        int plus00 = 0;
        int plus01 = 0;
        int plus11 = 0;

        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (key.charAt(i));
        }

        while (true) {
            keyMatrix[0][0] = bytes[0 % bytes.length] + plus00;
            keyMatrix[0][1] = bytes[1 % bytes.length] + plus01;
            keyMatrix[1][0] = bytes[2 % bytes.length];
            keyMatrix[1][1] = bytes[3 % bytes.length] + plus11;
            if (determinantCheck(matrixDeterminant(keyMatrix))) {
                System.out.println("good matrix " + Arrays.deepToString(keyMatrix) + " with det = " + matrixDeterminant(keyMatrix));
                break;
            } else {
                System.out.println("bad matrix " + Arrays.deepToString(keyMatrix) + " with det = " + matrixDeterminant(keyMatrix));
                if (keyMatrix[0][0] % 2 == 0) {
                    plus00++;
                }
                if (keyMatrix[1][1] % 2 == 0) {
                    plus11++;
                }
                if (keyMatrix[0][0] % 2 != 0 & keyMatrix[0][0] % 2 != 0) {
                    plus01++;
                }
            }
        }
        return keyMatrix;
    }

    private int[] matrixMultiply(int[] a, int[][] b) {
        int[] c = new int[2];
        c[0] = (a[0] * b[0][0] + a[1] * b[1][0]) % 256;
        c[1] = (a[0] * b[0][1] + a[1] * b[1][1]) % 256;
        return c;
    }

    private int[][] matrixMultiply(int a, int[][] b) {
        for (int i = 0; i < b.length; i++) {
            for (int j = 0; j < b[0].length; j++) {
                b[i][j] *= a;
            }
        }
        return b;
    }

    private boolean determinantCheck(int det) {
        return !((det == 0) | (det == 1) | (det == -1) | (det % 2 == 0));
    }

    //special decoding operations
    private int matrixDeterminant(int[][] matrix) {
        return (matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0]);
    }

    private int inverseDet(int det, int x) {
        int res = x;

        if (det < 0 & x > 0 ) {
            res = x;
        } else if (det > 0 & x < 0) {
            res = x + 256;
        } else if (det > 0 & x > 0) {
            res = x;
        } else if (det < 0 & x < 0) {
            res = x * -1;
        }
        return res;
    }

    private int[][] inverseMatrix(int[][] key) {
        int determinant = ring(matrixDeterminant(key), 256);
        int x = gcd(determinant, 256)[0];
        int inverseDeterminant = ring(x, 256);
        int[][] matrix = matrixOfAlgebraicAdditions(key);
        //matrix = modulo(matrix, 256);
        matrix = matrixMultiply(inverseDeterminant, matrix);
        //matrix = modulo(matrix, 256);
        matrix = transposeMatrix(matrix);
        matrix = makePositive(matrix, 256);
        return matrix;
    }

    private int[][] modulo(int[][] key, int num) {
        for (int i = 0; i < key.length; i++) {
            for (int j = 0; j < key[0].length; j++) {
                key[i][j] = key[i][j] % num;
            }
        }
        return key;
    }

    private int[][] matrixOfAlgebraicAdditions(int[][] key) {
        int[][] m = new int[key.length][key[0].length];
        m[0][0] = key[1][1];
        m[0][1] = key[1][0] * -1;
        m[1][0] = key[0][1] * -1;
        m[1][1] = key[0][0];

        return m;
    }

    private int[][] makePositive(int[][] key, int size) {
        for (int i = 0; i < key.length; i++) {
            for (int j = 0; j < key[i].length; j++) {
                key[i][j] = ring(key[i][j], size);
            }
        }
        return key;
    }

    private int[][] transposeMatrix(int[][] key) {
        int size = key.length;
        for (int i = 0; i < size - 1; i++) {
            for (int j = (i + 1); j < size; j++) {
                int temp = key[i][j];
                key[i][j] = key[j][i];
                key[j][i] = temp;
            }
        }
        return key;
    }

    private int ring(int a, int size) {
        while(a < 0) {
            a += size;
        }
        return a % size;
    }

    private int[] gcd(int a, int b) {
        if (b == 0) {
            return new int[]{1, 0, a};
        }
        int[] res = gcd(b, a % b);
        int y = res[0];
        int x = res[1];
        int g = res[2];
        return new int[]{x, y - (a / b) * x, g};
    }
}