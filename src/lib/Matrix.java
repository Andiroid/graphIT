package lib;

import java.util.ArrayList;

public class Matrix implements Cloneable{

    private int vertices;
    private int[][] matrix;
    private int[] eccentricities;
    private int radius;
    private int diameter;
    private String center = "";
    private ArrayList endVertices;
    private ArrayList endVerticesChar;
    private ArrayList cutVertices;
    private ArrayList cutVerticesChar;
    private ArrayList bridges;

    public Matrix(int vertices){
        this.vertices = vertices;
        this.matrix = new int[vertices][vertices];
        this.eccentricities = new int[vertices];
        this.endVertices = new ArrayList();
        this.endVerticesChar = new ArrayList();
        this.cutVertices = new ArrayList();
        this.cutVerticesChar = new ArrayList();
        this.bridges = new ArrayList();
    }

    public Matrix (Matrix p) {
        this.vertices = p.vertices;
        this.matrix = new int[vertices][vertices];
        this.matrix = p.matrix;
        /* Matrix m = adjMatrix; Matrix mCopy = new Matrix(m);*/
    }

    public Object clone()throws CloneNotSupportedException{
        return super.clone();
    }

    public int getVertices(){
        return this.vertices;
    }

    public int getValue(int a, int b){
        return this.matrix[a][b];
    }

    public void setVal(int a, int b, int c){
        this.matrix[a][b] = c;
    }

    public void setMirrorVal(int a, int b, int c){
        this.matrix[b][a] = c;
        this.matrix[a][b] = c;
    }

    public void setFullMatrix(int[][] matrix){
        this.vertices = matrix.length;
        this.matrix = matrix;
    }

    public int[][] getMatrix(){
        return this.matrix;
    }

    public void setEccentricities(int index, int val){
            this.eccentricities[index] = val;
    }

    public void setBridges(String val){
        this.bridges.add(val);
    }

    public ArrayList getBridges(){
        return this.bridges;
    }

    public void clearBridges(){
        this.bridges.clear();
    }

    public void setEndVertex(int val){
        this.endVertices.add(val);
        this.endVerticesChar.add(Character.toString ((char) (65+val)));
    }

    public void setCutVertex(int val){
        this.cutVertices.add(val);
        this.cutVerticesChar.add(Character.toString ((char) (65+val)));
    }

    public void setRadius(int radius){
        this.radius = radius;
    }

    public void setDiameter(int diameter){
        this.diameter = diameter;
    }

    public void setCenter(String center){
        this.center = center;
    }

    public int[] getEccentricities(){
        return this.eccentricities;
    }

    public ArrayList getEndVertices(){
        return this.endVertices;
    }

    public ArrayList getEndVerticesChar(){
        return this.endVerticesChar;
    }

    public ArrayList getCutVertices(){
        return this.cutVertices;
    }

    public String getCutVerticesChar(){
        String out = this.cutVerticesChar.toString();

        return "{"+out.substring(1, out.length() - 1)+"}";
    }

    public void clearCutVertices(){
        this.cutVertices.clear();
    }

    public void clearCutVerticesChar(){
        this.cutVerticesChar.clear();
    }

    public void clearEndVertices(){
        this.endVertices.clear();
    }

    public void clearEndVerticesChar(){
        this.endVerticesChar.clear();
    }

    public int getRadius(){
        return this.radius;
    }

    public int getDiameter(){
        return this.diameter;
    }

    public String getCenter(){
        return this.center;
    }
}
