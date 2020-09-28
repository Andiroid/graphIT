import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import lib.GT;
import lib.Matrix;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class Main extends Application {

    private Scene scene;
    private final int DEFAULT_SIZE = 10;
    private Matrix adjMatrix;
    private Matrix disMatrix;
    private Matrix pathMatrix;
    private Button[][] adjMatrixBtn;
    private Button[][] disMatrixBtn;
    private Button[] disMatrixSideBtn;
    private Button[][] pathMatrixBtn;
    private Button[] pathMatrixSideBtn;
    private ArrayList components = new ArrayList();
    private ArrayList componentsMirror = new ArrayList();
    private ArrayList componentsEdges = new ArrayList();

    public void initMain(int size, Stage window){

        int labelMatrixSize = size+2;
        int phM = size+1;
        int maxSize = 17*36;
        double tileSize = maxSize/(size+2);
        int fontSize = (int)tileSize/2-5;
        adjMatrix = new Matrix(phM-1);
        FlowPane adjMatrixFrame = new FlowPane();

        adjMatrixFrame.setMaxHeight(maxSize);
        adjMatrixFrame.setMaxWidth(maxSize);

        adjMatrixFrame.setStyle("-fx-background-color:rgba(0,0,0,0);");
        adjMatrixBtn = new Button[labelMatrixSize][labelMatrixSize];

        for (int i = 0; i < adjMatrixBtn.length; i++) {
            for (int j = 0; j < adjMatrixBtn.length; j++) {

                if((i == 0) || (j == 0) || (i == phM) || (j == phM)){
                    String thisLabelChar = "";
                    if(!((i == 0) && (j == 0)) && !((i == 0 ) && (j == phM)) && !((i == phM) && (j == 0)) && !((i == phM ) && (j == phM))){
                        int c = (i == 0) || (i == phM) ? 64+j : 64+i;
                        thisLabelChar += Character.toString ((char) c);
                    }
                    adjMatrixBtn[i][j] = new Button(thisLabelChar);
                    adjMatrixBtn[i][j].setId(i+"-"+j);
                    adjMatrixBtn[i][j].setStyle("-fx-font-size: "+fontSize+"px;-fx-text-fill:#8d8d8e;");
                    adjMatrixFrame.getChildren().add(adjMatrixBtn[i][j]);
                    adjMatrixBtn[i][j].getStyleClass().add("matrixLabels");
                    adjMatrixBtn[i][j].setPrefHeight(tileSize);
                    adjMatrixBtn[i][j].setPrefWidth(tileSize);
                } else {
                    adjMatrixBtn[i][j] = new Button("0");

                    if(i == j){
                        adjMatrixBtn[i][j].getStyleClass().add("loopVertex");
                        adjMatrixBtn[i][j].setStyle("-fx-text-fill: #6f6f6f;-fx-background-color: #9cb0ce;-fx-font-size: "+fontSize+"px;");
                        adjMatrixBtn[i][j].setOnMouseEntered(t -> {
                            Button thisBtn = (Button) t.getSource();
                            hoverBtn(thisBtn.getId(), "#f27d7d", "#fff", phM, true);
                        });
                        adjMatrixBtn[i][j].setOnMouseExited(t -> {
                            Button thisBtn = (Button) t.getSource();
                            hoverBtn(thisBtn.getId(), "#9cb0ce", "#8d8d8e", phM, true);
                        });
                    } else {
                        adjMatrixBtn[i][j].getStyleClass().add("regVertex");
                        adjMatrixBtn[i][j].setStyle("-fx-text-fill: #6f6f6f;-fx-background-color: #fff;-fx-font-size: "+fontSize+"px;");
                        int thisI = i-1;
                        int thisJ = j-1;
                        adjMatrixBtn[i][j].setOnMouseClicked(t -> {
                            Button thisBtn = (Button) t.getSource();
                            toggleBtn(thisBtn.getId());
                            int toggleBtnVal = Integer.parseInt(thisBtn.getText());
                            adjMatrix.setMirrorVal(thisI,thisJ,toggleBtnVal);
                            disMatrix.setMirrorVal(thisI,thisJ,toggleBtnVal);
                            calculateGraph();
                        });
                        adjMatrixBtn[i][j].setOnMouseEntered(t -> {
                            Button thisBtn = (Button) t.getSource();
                            hoverBtn(thisBtn.getId(), "#cff2c9", "#fff", phM, false);
                        });
                        adjMatrixBtn[i][j].setOnMouseExited(t -> {
                            Button thisBtn = (Button) t.getSource();
                            hoverBtn(thisBtn.getId(), "#fff", "#8d8d8e", phM, false);
                        });
                    }
                    adjMatrixBtn[i][j].setId(i+"-"+j);
                    adjMatrixBtn[i][j].setPrefHeight(tileSize);
                    adjMatrixBtn[i][j].setPrefWidth(tileSize);
                    adjMatrixFrame.getChildren().add(adjMatrixBtn[i][j]);
                }
            }
        }
        VBox topFrame = new VBox();
        topFrame.setSpacing(10);
        ChoiceBox choiceBoxSize = new ChoiceBox();
        Text choiceBoxSizeTitle = new Text("Graph IT");
        try {
            final Font f = Font.loadFont(new FileInputStream(new File("src/fonts/ag.ttf")), 12);
            choiceBoxSizeTitle.setFont(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        choiceBoxSizeTitle.setId("choiceBoxTitle");
        topFrame.setMargin(choiceBoxSizeTitle, new Insets(15, 5, 10, 5));
        topFrame.setMargin(choiceBoxSize, new Insets(15, 5, 5, 5));
        topFrame.setAlignment(Pos.BASELINE_CENTER);
        ObservableList list = topFrame.getChildren();
        list.addAll(choiceBoxSizeTitle, choiceBoxSize);
        choiceBoxSize.setId("choiceBoxSize");
        choiceBoxSize.getItems().addAll("3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15");
        choiceBoxSize.getSelectionModel().select(size-3);
        choiceBoxSize.getSelectionModel().selectedIndexProperty().addListener((observableValue, number, number2) -> {
            // System.out.println(choiceBoxSize.getItems().get((Integer) number2));
            int chosenSize = (Integer) number2;
            initMain(chosenSize+3,window);
        });
        int subMaxSize = 15*20;
        double subTileSize = subMaxSize/(size);
        int subFontSize = size > 9 ? 14 : (int)subTileSize/2-5;
        VBox leftFrame = new VBox();
        Text leftFrameTitle = new Text("Wegmatrix");
        leftFrameTitle.getStyleClass().add("subTitle");
        pathMatrix = new Matrix(size);
        FlowPane pathMatrixFrame = new FlowPane();
        leftFrame.setMargin(leftFrameTitle, new Insets(5, 0, 10, 5+subTileSize));
        leftFrame.setMargin(pathMatrixFrame, new Insets(0, 5, 10, 5));
        pathMatrixFrame.setMaxHeight(subMaxSize+subTileSize);
        pathMatrixFrame.setMaxWidth(subMaxSize+subTileSize);
        pathMatrixFrame.setStyle("-fx-background-color:rgba(0,0,0,0);");
        pathMatrixBtn = new Button[size][size];
        pathMatrixSideBtn = new Button[size];
        for (int i = 0; i < pathMatrixBtn.length; i++) {
            pathMatrixSideBtn[i] = new Button("");
            pathMatrixSideBtn[i].getStyleClass().add("sideBtn");
            pathMatrixSideBtn[i].setStyle("-fx-text-fill: #fff;-fx-font-size: "+subFontSize+"px;");
            pathMatrixSideBtn[i].setPadding(Insets.EMPTY);
            pathMatrixSideBtn[i].setPrefHeight(subTileSize);
            pathMatrixSideBtn[i].setPrefWidth(subTileSize);
            pathMatrixFrame.getChildren().add(pathMatrixSideBtn[i]);
            for (int j = 0; j < pathMatrixBtn.length; j++) {
                pathMatrixBtn[i][j] = new Button("0");
                pathMatrixBtn[i][j].setId(i+"-"+j+"-p");
                pathMatrix.setMirrorVal(i,j,0);
                pathMatrixBtn[i][j].setStyle("-fx-text-fill: #6f6f6f;-fx-background-color: #fff;-fx-font-size: "+subFontSize+"px;");
                pathMatrixBtn[i][j].setPadding(Insets.EMPTY);
                pathMatrixBtn[i][j].setPrefHeight(subTileSize);
                pathMatrixBtn[i][j].setPrefWidth(subTileSize);
                if(i == j){
                    pathMatrixBtn[i][j].setText("1");
                    pathMatrix.setMirrorVal(i,j,1);
                    pathMatrixBtn[i][j].setStyle("-fx-text-fill: #000000;-fx-font-size: "+subFontSize+"px;");
                    pathMatrixBtn[i][j].getStyleClass().add("loopVertex");
                } else {
                    pathMatrixBtn[i][j].getStyleClass().add("regVertex");
                }
                pathMatrixFrame.getChildren().add(pathMatrixBtn[i][j]);
            }
        }
        ObservableList leftList = leftFrame.getChildren();
        getComponents(); // TODO fix when load CSV file
        Label components = new Label(componentsToString());
        components.setWrapText(true);
        components.getStyleClass().add("assets");
        components.setId("components");
        components.setWrapText(true);
        components.setMaxWidth(290);
        Label bridges = new Label("Brücken: {}");
        bridges.setWrapText(true);
        bridges.setMaxWidth(290);
        bridges.getStyleClass().add("assets");
        bridges.setId("bridges");
        Label articulations = new Label("Artikulationen: {}");
        articulations.setWrapText(true);
        articulations.setMaxWidth(290);
        articulations.getStyleClass().add("assets");
        articulations.setId("articulations");
        leftFrame.setMargin(components, new Insets(15, 0, 10, 15+subTileSize));
        leftFrame.setMargin(bridges, new Insets(15, 0, 10, 15+subTileSize));
        leftFrame.setMargin(articulations, new Insets(15, 0, 10, 15+subTileSize));
        leftList.addAll(leftFrameTitle,
                pathMatrixFrame,
                articulations,
                bridges,
                components
        );
        VBox rightFrame = new VBox();
        Text rightFrameTitle = new Text("Distanzmatrix");
        rightFrameTitle.getStyleClass().add("subTitle");
        disMatrix = new Matrix(size);
        FlowPane disMatrixFrame = new FlowPane();
        rightFrame.setMargin(rightFrameTitle, new Insets(5, 0, 10, 0));
        rightFrame.setMargin(disMatrixFrame, new Insets(0, 5, 10, 0));
        disMatrixFrame.setMaxHeight(subMaxSize+subTileSize);
        disMatrixFrame.setMaxWidth(subMaxSize+subTileSize);
        disMatrixFrame.setStyle("-fx-background-color:rgba(0,0,0,0);");
        disMatrixBtn = new Button[size][size];
        disMatrixSideBtn = new Button[size];
        for (int i = 0; i < disMatrixBtn.length; i++) {
            for (int j = 0; j < disMatrixBtn.length; j++) {
                disMatrixBtn[i][j] = new Button("∞");
                disMatrixBtn[i][j].setStyle("-fx-text-fill: #6f6f6f;-fx-background-color: #fff;-fx-font-size: "+subFontSize+"px;");
                disMatrixBtn[i][j].setPadding(Insets.EMPTY);
                disMatrixBtn[i][j].setPrefHeight(subTileSize);
                disMatrixBtn[i][j].setPrefWidth(subTileSize);
                if(i == j){
                    disMatrixBtn[i][j].setText("0");
                    disMatrixBtn[i][j].setStyle("-fx-text-fill: #000;-fx-font-size: "+subFontSize+"px;");
                    disMatrixBtn[i][j].getStyleClass().add("loopVertex");
                } else {
                    disMatrixBtn[i][j].getStyleClass().add("regVertex");
                }
                disMatrix.setVal(i,j,0);
                disMatrixFrame.getChildren().add(disMatrixBtn[i][j]);
            }
            disMatrixSideBtn[i] = new Button("");
            disMatrixSideBtn[i].getStyleClass().add("sideBtn");
            disMatrixSideBtn[i].setStyle("-fx-text-fill: #fff;-fx-font-size: "+subFontSize+"px;");
            disMatrixSideBtn[i].setPadding(Insets.EMPTY);
            disMatrixSideBtn[i].setPrefHeight(subTileSize);
            disMatrixSideBtn[i].setPrefWidth(subTileSize);
            disMatrixFrame.getChildren().add(disMatrixSideBtn[i]);
        }
        ObservableList rightList = rightFrame.getChildren();
        //leftFrame.setAlignment(Pos.CENTER_LEFT);
        //leftFrame.setAlignment(Pos.BASELINE_CENTER);
        Label radius = new Label("Radius: n/a");
        radius.getStyleClass().add("assets");
        radius.setId("radius");
        Label diam = new Label("Durchmesser: n/a");
        diam.getStyleClass().add("assets");
        diam.setId("diam");
        Label center = new Label("Zentrum: n/a");
        center.getStyleClass().add("assets");
        center.setId("center");

        VBox btnFrame = new VBox();
        ObservableList btnFrameList = btnFrame.getChildren();

        Button loadBtn = new Button("Load");
        loadBtn.getStyleClass().add("btn");

        loadBtn.setOnMouseClicked(t -> {
            FileChooser loadFile = new FileChooser();
            File recordsDir = new File(System.getProperty("user.home"), "Desktop");
            loadFile.setInitialDirectory(recordsDir);
            loadFile.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV Files", "*.csv", "*.CSV"));
            loadFile.setTitle("Load Matrix from CSV file");
            File selectedFile = loadFile.showOpenDialog(window);

            if (selectedFile != null) {
                //System.out.println(selectedFile.getAbsolutePath());
                try (BufferedReader reader = new BufferedReader(new FileReader(new File(selectedFile.getAbsolutePath())))) {
                    String line;
                    String firstLine[] = reader.readLine().split(";");
                    int newMatrix[][] = new int[firstLine.length][firstLine.length];
                    BufferedReader thisReader = new BufferedReader(new FileReader(new File(selectedFile.getAbsolutePath())));
                    int counter = 0;
                    while ((line = thisReader.readLine()) != null) {
                        int pos = 0;
                        for (String s : line.split(";")) {
                            newMatrix[counter][pos] = Integer.parseInt(s);
                            pos++;
                        }
                        counter++;
                        //System.out.println(line);
                    }
                    if(counter == firstLine.length){
                        initMain(firstLine.length,window);
                        adjMatrix.setFullMatrix(newMatrix);
                        calculateGraph();
                        populateFromFile();
                        //System.out.println("valid");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Button saveBtn = new Button("Save");
        saveBtn.getStyleClass().add("btn");
        btnFrame.setAlignment(Pos.BASELINE_CENTER);

        saveBtn.setOnMouseClicked(t -> {
            FileChooser saveFile = new FileChooser();
            File recordsDir = new File(System.getProperty("user.home"), "Desktop");
            saveFile.setInitialDirectory(recordsDir);
            saveFile.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV Files", "*.csv", "*.CSV"));
            saveFile.setTitle("Save Matrix to CSV file");
            File newFile = saveFile.showSaveDialog(window);
            if (newFile != null) {
                try {
                    String filePath = newFile.toPath().toString();
                    if(!filePath.toUpperCase().endsWith(".CSV") ) {
                        //newFile = new File(filePath + ".csv");
                        filePath += ".csv";
                    }
                    String fileContent = adjMatrixToString();

                    //System.out.println(fileContent);
                    BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
                    writer.write(fileContent);
                    writer.close();
                    //System.out.println(newFile.toPath());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        rightFrame.setMargin(loadBtn, new Insets(5, 30, 10, 0));
        rightFrame.setMargin(saveBtn, new Insets(25, 30, 10, 0));

        btnFrameList.addAll(loadBtn, saveBtn);
        //btnFrameList.addAll(loadBtn);

        rightFrame.setMargin(radius, new Insets(15, 0, 10, 5));
        rightFrame.setMargin(diam, new Insets(15, 0, 10, 5));
        rightFrame.setMargin(center, new Insets(15, 0, 10, 5));

        rightList.addAll(rightFrameTitle,
                disMatrixFrame,
                radius,
                diam,
                center,
                btnFrame
        );
        Text footer = new Text("");
        BorderPane.setAlignment(footer,Pos.BOTTOM_CENTER);
        /*
         *  SET ROOT
         */
        BorderPane root = new BorderPane(adjMatrixFrame, topFrame, rightFrame, footer, leftFrame);
        // root.setPrefSize(1280, 720);
        window.setMaximized(true);
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        root.setPrefSize(screenBounds.getWidth(), screenBounds.getHeight());
        root.getStyleClass().add("root");
        // window.setFullScreen(true);
        scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("css/generic.css").toExternalForm());
        window.setScene(scene);
    }

    public String adjMatrixToString(){
        String out = "";
        int[][] thisMatrix = adjMatrix.getMatrix();
        for (int i = 0; i < thisMatrix.length; i++) {
            for (int j = 0; j < thisMatrix.length; j++) {
                out += thisMatrix[i][j]+";";
            }
            out = out.substring(0,out.length()-1);
            out += "\n";
        }
        return out;
    }

    public void populateFromFile(){
        int[][] thisMatrix = adjMatrix.getMatrix();
        for (int i = 0; i < thisMatrix.length; i++) {
            for (int j = 0; j < thisMatrix.length; j++) {
                adjMatrixBtn[i+1][j+1].setText(""+thisMatrix[i][j]);
                if(thisMatrix[i][j] == 1) {
                    String activeBtnColor = adjMatrixBtn[i+1][j+1].getStyle().replace("#6f6f6f", "#000"); // font color
                    activeBtnColor = activeBtnColor.replace("#fff", "#d8d8d8"); // bg color
                    adjMatrixBtn[i+1][j+1].setStyle(activeBtnColor);
                }
            }
        }
    }

    @Override
    public void start(Stage window) throws Exception {
        initMain(DEFAULT_SIZE,window);
        window.setTitle("GraphIT");
        window.show();
        window.setMinWidth(window.getWidth());
        window.setMinHeight(window.getHeight());
    }

    public static void main(String[] args) { launch(args); }

    public void hoverBtn(String id, String color, String colorLabel, int size, boolean disabledBtn){
        String[] data = id.split("-");
        int valA = Integer.parseInt(data[0]);
        int valB = Integer.parseInt(data[1]);
        Button labelBtn = adjMatrixBtn[valB][0];
        String thisStyle = colorLabel.equals("#8d8d8e") ? labelBtn.getStyle().replace("#fff", "#8d8d8e"): labelBtn.getStyle().replace("#8d8d8e", "#fff");
        adjMatrixBtn[valB][0].setStyle(thisStyle);
        adjMatrixBtn[0][valB].setStyle(thisStyle);
        adjMatrixBtn[0][valA].setStyle(thisStyle);
        adjMatrixBtn[valA][0].setStyle(thisStyle);
        adjMatrixBtn[valB][size].setStyle(thisStyle);
        adjMatrixBtn[size][valB].setStyle(thisStyle);
        adjMatrixBtn[size][valA].setStyle(thisStyle);
        adjMatrixBtn[valA][size].setStyle(thisStyle);
        if(!disabledBtn){
            thisStyle = color.equals("#fff") ? adjMatrixBtn[valB][valA].getStyle().replace("#cff2c9", "#fff"):adjMatrixBtn[valB][valA].getStyle().replace("#fff", "#cff2c9");
            adjMatrixBtn[valB][valA].setStyle(thisStyle);
            adjMatrixBtn[valA][valB].setStyle(thisStyle);
        } else {
            thisStyle = color.equals("#9cb0ce") ? adjMatrixBtn[valA][valB].getStyle().replace("#f27d7d", "#9cb0ce"):adjMatrixBtn[valA][valB].getStyle().replace("#9cb0ce", "#f27d7d");
            adjMatrixBtn[valA][valB].setStyle(thisStyle);
        }
    }

    public void toggleBtn(String id){
        String[] data = id.split("-");
        int valA = Integer.parseInt(data[0]);
        int valB = Integer.parseInt(data[1]);
        Button toggleBtn = (Button) scene.lookup("#"+id);
        int toggleBtnVal = Integer.parseInt(toggleBtn.getText());
        toggleBtnVal ^= 1;
        String activeBtnColor;
        if(toggleBtnVal == 1){
            activeBtnColor = toggleBtn.getStyle().replace("#6f6f6f", "#000"); // font color
            activeBtnColor = activeBtnColor.replace("#cff2c9", "#d8d8d8"); // bg color
        } else {
            activeBtnColor = toggleBtn.getStyle().replace("#000", "#6f6f6f");
            activeBtnColor = activeBtnColor.replace("#d8d8d8", "#cff2c9");
        }
        toggleBtn.setStyle(activeBtnColor);
        toggleBtn.setText(""+toggleBtnVal+"");
        toggleBtn = (Button) scene.lookup("#"+valB+"-"+valA);
        toggleBtn.setStyle(activeBtnColor);
        toggleBtn.setText(""+toggleBtnVal+"");
    }

    public void getComponents(){
        this.components.clear();
        this.componentsMirror.clear();
        this.componentsEdges.clear();
        int[][] thisAdjMatrix = this.adjMatrix.getMatrix();
        boolean[] checked = new boolean[thisAdjMatrix.length];
        Queue<Integer> queue = new LinkedList<>();
        for (int o = 0; o < checked.length; o++) {
            if(!checked[o]){
                queue.add(o);
                int g = 65+o;
                String thisComponent = Character.toString ((char) g);
                ArrayList<Integer> thisMirrorComponent = new ArrayList<>();
                ArrayList<Integer> thisEdgesComponent = new ArrayList<>();
                thisMirrorComponent.add(o);
                thisEdgesComponent.add(o);
                while (!queue.isEmpty()) {
                    int current = queue.peek(); // get first element of queue
                    queue.remove(current);
                    checked[current] = true;
                    // check matrix row given by current(popped from queue)
                    for (int i = 0; i < thisAdjMatrix.length; i++) {
                        if((thisAdjMatrix[current][i] == 1) && (!checked[i])){
                            int c = 65+i;
                            thisComponent += ", "+Character.toString ((char) c);
                            thisMirrorComponent.add(i);
                            thisEdgesComponent.add(i);
                            checked[i] = true;
                            queue.add(i);
                        }
                    }
                }
                this.components.add(thisComponent);
                this.componentsMirror.add(thisMirrorComponent);

                // populate component & edges display string
                String out = "({"+thisComponent+"}, {";
                for (int i = 0; i < thisEdgesComponent.size(); i++) {

                    for (int j = 0; j < thisEdgesComponent.size(); j++) {
                        if(thisAdjMatrix[thisEdgesComponent.get(i)][thisEdgesComponent.get(j)] == 1){
                            char thisJ = (char)(65+thisEdgesComponent.get(j));
                            char thisI = (char)(65+thisEdgesComponent.get(i));
                            if(out.indexOf(thisJ+"-"+thisI) == -1){
                                out += "["+thisI+"-"+thisJ+"], ";
                            }
                        }
                    }
                    //thisEdgesComponent.remove(i);
                }
                if(thisEdgesComponent.size()>1){
                    out = out.substring(0,out.length() - 2);
                }

                out += "})\n";
                componentsEdges.add(out);
            }
        }
    }

    public String componentsToString(){
        String out = "";
        for (int i = 0; i < this.componentsEdges.size(); i++) {
            out += "K"+(i+1)+": "+componentsEdges.get(i);
        }
        return out;
    }

    public void updatePathMatrix(){
        clearPathMatrix();
        for (int p = 0; p < this.componentsMirror.size(); p++) {
            ArrayList<Integer> list = (ArrayList<Integer>) this.componentsMirror.get(p);
            for (int i = 0; i < list.toArray().length; i++) {
                for (int j = 0; j < list.toArray().length; j++) {
                    int thisI = (int) list.toArray()[i];
                    int thisJ = (int) list.toArray()[j];
                    pathMatrix.setVal(thisI,thisJ,1);
                    pathMatrixBtn[thisI][thisJ].setText("1");
                    pathMatrixBtn[thisI][thisJ].setStyle(pathMatrixBtn[thisI][thisJ].getStyle().replace("#6f6f6f", "#000"));
                    pathMatrixBtn[thisI][thisJ].setStyle(pathMatrixBtn[thisI][thisJ].getStyle().replace("#fff", "#d8d8d8"));
                }
            }
        }
    }

    public void printComponents(){
        System.out.println(this.components.size());
        for (int i = 0; i < this.components.size(); i++) {
            System.out.println(this.components.get(i));
        }
    }

    public void clearPathMatrix(){
        for (int i = 0; i < pathMatrix.getMatrix().length; i++) {
            for (int j = 0; j < pathMatrix.getMatrix().length; j++) {
                if(i != j){
                    pathMatrix.setVal(i,j,0);
                    pathMatrixBtn[i][j].setText("0");
                    pathMatrixBtn[i][j].setStyle(pathMatrixBtn[i][j].getStyle().replace("#000", "#6f6f6f"));
                    pathMatrixBtn[i][j].setStyle(pathMatrixBtn[i][j].getStyle().replace("#d8d8d8", "#fff"));
                }
            }
        }
    }

    public void clearDisMatrix(){
        for (int i = 0; i < disMatrix.getMatrix().length; i++) {
            for (int j = 0; j < disMatrix.getMatrix().length; j++) {
                if(i != j){
                    disMatrixBtn[i][j].setText("∞");
                    disMatrixBtn[i][j].setStyle(disMatrixBtn[i][j].getStyle().replace("#000", "#6f6f6f"));
                    disMatrixBtn[i][j].setStyle(disMatrixBtn[i][j].getStyle().replace("#d8d8d8", "#fff"));
                } else {
                    disMatrixBtn[i][j].setText("0");
                }
                disMatrix.setVal(i,j,0);
            }
        }
    }

    public void printMatrix(Matrix matrix){
        int[][] thisMatrix = matrix.getMatrix();
        for (int i = 0; i < thisMatrix.length; i++) {
            for (int j = 0; j < thisMatrix.length; j++) {
                System.out.print(thisMatrix[i][j]+" ");
            }
            System.out.print("\n");
        }
        System.out.print("\n");
    }

    public void updateDisMatrix(){
        clearDisMatrix();
        int[][] thisDisMatrix = disMatrix.getMatrix();
        Matrix tmpMatrix = adjMatrix;
        int[][] thisTmpMatrix = tmpMatrix.getMatrix();
        int[][] thisPathMatrix = pathMatrix.getMatrix();
        boolean nothingChanged = true;
        int pow = 1;
        while(true){
            for (int i = 0; i < thisTmpMatrix.length; i++) {
                for (int j = 0; j < thisTmpMatrix.length; j++) {
                    if ((thisDisMatrix[i][j] == 0) && (thisTmpMatrix[i][j] > 0) && (thisPathMatrix[i][j] == 1) && (i != j)) {
                        thisDisMatrix[i][j] = pow;
                        disMatrixBtn[i][j].setText(""+pow+"");
                        disMatrixBtn[i][j].setStyle(disMatrixBtn[i][j].getStyle().replace("#6f6f6f", "#000"));
                        disMatrixBtn[i][j].setStyle(disMatrixBtn[i][j].getStyle().replace("#fff", "#d8d8d8"));
                        nothingChanged = false;
                    }
                }
            }
            if(nothingChanged){
                break;
            } else {
                tmpMatrix = GT.powMatrices(tmpMatrix,adjMatrix);
                thisTmpMatrix = tmpMatrix.getMatrix();
                nothingChanged = true;
                pow++;
            }
        }
    }

    public void getGraphEccentricities(){
        int[][] thisDisMatrix = disMatrix.getMatrix();
        for (int i = 0; i < thisDisMatrix.length; i++) {
            int rowEcc = 0;
            for (int j = 0; j < thisDisMatrix.length; j++) {
                if(thisDisMatrix[i][j] > rowEcc){
                    rowEcc = thisDisMatrix[i][j];
                }
            }
            adjMatrix.setEccentricities(i,rowEcc);
        }
    }

    public void updateGraphAssets(){
        int[] eccList = adjMatrix.getEccentricities();
        int radius = eccList[0];
        int diam = eccList[0];
        for (int i = 1; i < eccList.length; i++) {
            if(eccList[i] < radius){
                radius = eccList[i];
            }
            if(eccList[i] > diam){
                diam = eccList[i];
            }
        }
        adjMatrix.setRadius(radius);
        adjMatrix.setDiameter(diam);
        String center = "";
        for (int i = 0; i < eccList.length; i++) {
            if(eccList[i] == radius){
                int c = 65+i;
                center += Character.toString ((char) c)+", ";
            }
        }
        adjMatrix.setCenter(center.substring(0, center.length() - 2));
    }

    public void calcBridges(){
        this.adjMatrix.clearBridges();
        Matrix CopyMatrix = new Matrix(adjMatrix.getVertices());
        CopyMatrix.setFullMatrix(copyMatrix(adjMatrix.getMatrix()));
        int[][] thisAdjCopy = CopyMatrix.getMatrix();
        int componentCount = this.components.size();

        for (int i = 0; i < thisAdjCopy.length; i++) {
            for (int j = 0; j < thisAdjCopy.length; j++) {
                if(thisAdjCopy[i][j] == 1){
                    thisAdjCopy[i][j] = 0;
                    //thisAdjCopy[j][i] = 0;
                    int newComponentCount = getComponentCount(thisAdjCopy);
                    if(newComponentCount-componentCount > 0){
                        this.adjMatrix.setBridges("["+ ((char) (65+i))+","+ ((char) (65+j))+"]");
                    }
                    thisAdjCopy[i][j] = 1;
                    //thisAdjCopy[j][i] = 1;
                }
            }
        }
    }

    public void calculateGraph(){
        getComponents();
        updatePathMatrix();
        updateDisMatrix();
        Label compText = (Label) scene.lookup("#components");
        compText.setText(componentsToString());
        if(this.components.size() == 1){
            getGraphEccentricities();
            int[] ecc = this.adjMatrix.getEccentricities();
            for (int i = 0; i < disMatrixSideBtn.length; i++) {
                disMatrixSideBtn[i].setText(""+ecc[i]+"");
            }
            updateGraphAssets();
            Label radius = (Label) scene.lookup("#radius");
            radius.setText("Radius: "+adjMatrix.getRadius());
            Label diam = (Label) scene.lookup("#diam");
            diam.setText("Durchmesser: "+adjMatrix.getDiameter());
            Label center = (Label) scene.lookup("#center");
            center.setText("Zentrum: {"+adjMatrix.getCenter()+"}");
        } else {
            Label radius = (Label) scene.lookup("#radius");
            radius.setText("Radius: n/a");
            Label diam = (Label) scene.lookup("#diam");
            diam.setText("Durchmesser: n/a");
            Label center = (Label) scene.lookup("#center");
            center.setText("Zentrum: n/a");
            for (int i = 0; i < disMatrixSideBtn.length; i++) {
                disMatrixSideBtn[i].setText("");
            }
        }
        calcCutVertices();
        Label articulations = (Label) scene.lookup("#articulations");
        articulations.setText("Artikulationen: "+this.adjMatrix.getCutVerticesChar());
        calcBridges();
        String thisBridges = this.adjMatrix.getBridges().toString();
        Label bridges = (Label) scene.lookup("#bridges");
        bridges.setText("Brücken: {"+thisBridges.substring(1, thisBridges.length() - 1)+"}");
    }

    public int[][] copyMatrix(int[][] matrix){
        int[][] outMatrix = new int[matrix.length][matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                outMatrix[i][j] = matrix[i][j];
            }
        }
        return outMatrix;
    }

    public void calcCutVertices(){
        this.adjMatrix.clearCutVertices();
        this.adjMatrix.clearCutVerticesChar();
        this.adjMatrix.clearEndVertices();
        this.adjMatrix.clearEndVerticesChar();
        Matrix CopyMatrix = new Matrix(adjMatrix.getVertices());
        CopyMatrix.setFullMatrix(copyMatrix(adjMatrix.getMatrix()));
        int[][] thisAdjCopy = CopyMatrix.getMatrix();
        int componentCount = this.components.size();
        int edgeCount = 0;
        for (int i = 0; i < thisAdjCopy.length; i++) {
            for (int j = 0; j < thisAdjCopy.length; j++) {
                if(thisAdjCopy[i][j] == 1){
                    edgeCount++;
                }
            }
            // for each row
            if(edgeCount == 1){
                this.adjMatrix.setEndVertex(i);
            } else if(edgeCount > 1){
                // set row to 0, detach Vertex
                for (int q = 0; q < thisAdjCopy.length; q++) {
                    thisAdjCopy[i][q] = 0;
                    thisAdjCopy[q][i] = 0;
                }
                int newComponentCount = getComponentCount(thisAdjCopy);
                if(newComponentCount-componentCount > 1){
                    this.adjMatrix.setCutVertex(i);
                }
                // set row to adjMatrix value, repair Vertex
                for (int q = 0; q < thisAdjCopy.length; q++) {
                    thisAdjCopy[i][q] = adjMatrix.getValue(i,q);
                    thisAdjCopy[q][i] = adjMatrix.getValue(i,q);
                }
            }
            edgeCount = 0;
        }
    }

    public int getComponentCount(int[][] thisMatrix){
        ArrayList outerComponentList = new ArrayList();
        int[][] thisWorkingMatrix = thisMatrix;
        boolean[] checked = new boolean[thisWorkingMatrix.length];
        Queue<Integer> queue = new LinkedList<>();
        for (int o = 0; o < checked.length; o++) {
            if(!checked[o]){
                queue.add(o);
                ArrayList<Integer> innerComponentList = new ArrayList<>();
                innerComponentList.add(o);
                while (!queue.isEmpty()) {
                    int current = queue.peek(); // get first element of queue
                    queue.remove(current);
                    checked[current] = true;
                    // check matrix row given by current(popped from queue)
                    for (int i = 0; i < thisWorkingMatrix.length; i++) {
                        if((thisWorkingMatrix[current][i] == 1) && (!checked[i])){
                            innerComponentList.add(i);
                            checked[i] = true;
                            queue.add(i);
                        }
                    }
                }
                outerComponentList.add(innerComponentList);
            }
        }
        return outerComponentList.size();
    }

}
