package lib;

public class GT {

    public static void printMatrix(Matrix matrix){
        int vertices = matrix.getVertices();
        int[][] thisMatrix = matrix.getMatrix();

        for (int i = 0; i < vertices; i++) {
            for (int j = 0; j < vertices; j++) {
                System.out.print(thisMatrix[i][j]);
            }
            System.out.print("\n");
        }
    }

    public static Matrix powMatrices(Matrix matrixA,Matrix matrixB){
        int[][] thisMatrixA = matrixA.getMatrix();
        int thisVerticesA = matrixA.getVertices();
        int[][] thisMatrixB = matrixB.getMatrix();
        int thisVerticesB = matrixB.getVertices();
        int[][] matrixOUT = new int[thisVerticesA][thisVerticesA];

        for (int i = 0; i < thisVerticesA; i++) {
            // for each row of MatrixA
            for (int j = 0; j < thisVerticesA; j++) {
                // for each field right in MatrixOUT, one column right in MatrixB
                // if next row, start from left again
                for (int k = 0; k < thisVerticesA; k++) {
                    // for each field right in MatrixA, one field down in MatrixB
                    matrixOUT[i][j] += thisMatrixA[i][k] * thisMatrixB[k][j];
                }
            }
        }

        Matrix powMatrix = new Matrix(thisVerticesA);
        powMatrix.setFullMatrix(matrixOUT);
        return powMatrix;
    }

    public static Matrix powMatrix(Matrix matrixIN){
        int[][] thisMatrix = matrixIN.getMatrix();
        int thisVertices = matrixIN.getVertices();

        int[][] matrixOUT = new int[thisVertices][thisVertices];

        for (int i = 0; i < thisVertices; i++) {
            // for each row of MatrixA
            for (int j = 0; j < thisVertices; j++) {
                // for each field right in MatrixOUT, one column right in MatrixB
                // if next row, start from left again
                for (int k = 0; k < thisVertices; k++) {
                    // for each field right in MatrixA, one field down in MatrixB
                    matrixOUT[i][j] += thisMatrix[i][k] * thisMatrix[k][j];
                }
            }
        }

        Matrix powMatrix = new Matrix(thisVertices);
        powMatrix.setFullMatrix(matrixOUT);
        return powMatrix;
    }

    public Matrix powMatrixByVal(Matrix matrixIN, int pow){

        int thisVertices = matrixIN.getVertices();
        Matrix matrixOUT = new Matrix(thisVertices);

        for (int h = 0; h < pow; h++) {
            // raise to the power of pow
            for (int i = 0; i < thisVertices; i++) {
                // for each row of MatrixA
                for (int j = 0; j < thisVertices; j++) {
                    // for each field right in MatrixOUT, one column right in MatrixB
                    // if next row, start from left again
                    for (int k = 0; k < thisVertices; k++) {
                        // for each field right in MatrixA, one field down in MatrixB
                        int thisValue = matrixOUT.getValue(i,j) + matrixIN.getValue(i,k) * matrixIN.getValue(k,j);
                        matrixOUT.setVal(i,j, thisValue);
                    }
                }
            }
        }
        return matrixOUT;
    }

    public void testPowMatrix(){
        //System.out.print(thisMatrix.getMatrix()[1][2]);
        int testV = 3;
        Matrix testM = new Matrix(testV);

        for (int i = 0; i < testV; i++) {
            for (int j = 0; j < testV; j++) {
                testM.setVal(i,j,i+1);
            }
        }

        for (int i = 0; i < testV; i++) {
            for (int j = 0; j < testV; j++) {
                System.out.print(testM.getMatrix()[i][j]);
            }
            System.out.print("  ");
            for (int j = 0; j < testV; j++) {
                System.out.print(testM.getMatrix()[i][j]);
            }
            System.out.print("\n");
        }

        // i     i     i     i     i     i      i
        // 0-0 * 0-0 + 0-1 * 1-0 + 0-2 * 2-0 -> 0-0
        // 0-0 * 0-1 + 0-1 * 1-1 + 0-2 * 2-1 -> 0-1
        // 0-0 * 0-2 + 0-1 * 2-1 + 0-2 * 2-2 -> 0-2

        // 1-0 * 1-0 + 1-1 * 1-0 + 1-2 * 2-0 -> 1-0
        // 1-0 * 1-1 + 1-1 * 1-1 + 1-2 * 2-1 -> 1-1
        // 1-0 * 1-2 + 1-1 * 2-1 + 1-2 * 2-2 -> 1-2

        // 2-0 * 2-0 + 2-1 * 1-0 + 2-2 * 2-0 -> 2-0
        // 2-0 * 2-1 + 2-1 * 1-1 + 2-2 * 2-1 -> 2-1
        // 2-0 * 2-2 + 2-1 * 2-1 + 2-2 * 2-2 -> 2-2

        Matrix powM = new Matrix(testV);
        int[][] matrixOUT = new int[testV][testV];
        for (int i = 0; i < testV; i++) {
            // for each row of MatrixA
            for (int j = 0; j < testV; j++) {
                // for each field right in MatrixOUT, one column right in MatrixB
                // if next row, start from left again
                for (int k = 0; k < testV; k++) {
                    // for each field right in MatrixA, one field down in MatrixB
                    powM.setVal(i,j,1);
                    System.out.println(i+"-"+k+"-"+j);
                    // FOR I = 0
                        // FOR J = 0
                            // FOR  k = 0
                            //      i  j             i  k            k  j
                            // powM[0][0] += MatrixA[0][0] * MatrixB[0][0]
                            // FOR  k = 1
                            //      i  j             i  k            k  j
                            // powM[0][0] += MatrixA[0][1] * MatrixB[1][0]
                            // FOR  k = 2
                            //      i  j             i  k            k  j
                            // powM[0][0] += MatrixA[0][2] * MatrixB[2][0]
                    // FOR J = 1
                            //      i  j             i  k            k  j
                            // powM[0][1] += MatrixA[0][0] * MatrixB[0][1]
                            // powM[0][1] += MatrixA[0][1] * MatrixB[1][1]
                            // powM[0][1] += MatrixA[0][2] * MatrixB[2][1]
                    // FOR J = 2
                            //      i  j             i  k            k  j
                            // powM[0][2] += MatrixA[0][0] * MatrixB[0][2]
                            // powM[0][2] += MatrixA[0][1] * MatrixB[1][2]
                            // powM[0][2] += MatrixA[0][2] * MatrixB[2][2]

                    matrixOUT[i][j] += testM.getMatrix()[i][k] * testM.getMatrix()[k][j];
                }
            }
        }

        for (int i = 0; i < testV; i++) {
            for (int j = 0; j < testV; j++) {
                System.out.print(matrixOUT[i][j]);
            }
            System.out.print("\n");
        }
    }

}
