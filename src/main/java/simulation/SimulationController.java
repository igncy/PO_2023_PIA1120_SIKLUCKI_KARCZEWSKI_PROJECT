package simulation;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import model.Animal;
import model.Boundary;
import model.WorldElement;
import model.WorldMap;
import util.MapChangeListener;
import util.WorldSettings;
import util.WorldStats;

import java.util.List;

public class SimulationController implements MapChangeListener {
    @FXML private Label infoLabel;
    @FXML private GridPane mapGrid;
    @FXML private Label moveInfo;
    @FXML private Label animalCountLabel;

    private WorldMap map;
    private int updateCount = 1;
    private final WorldSettings settings;
    private WorldStats stats;

    public SimulationController(WorldSettings settings) {
        this.settings = settings;
    }

    public void drawMap(String message) {
        infoLabel.setText(String.format("update #%d", updateCount++));
        moveInfo.setText(message);
        clearGrid();

        int CELL_WIDTH = 20, CELL_HEIGHT = 20;
        Boundary boundary = map.getCurrentBonds();
        int minX = boundary.lowerLeft().getX();
        int maxX = boundary.upperRight().getX();
        int maxY = boundary.upperRight().getY();
        int minY = boundary.lowerLeft().getY();

//        addCell(0, 0, "y\\x");
        mapGrid.getColumnConstraints().add(new ColumnConstraints(CELL_WIDTH));
        mapGrid.getRowConstraints().add(new RowConstraints(CELL_HEIGHT));
        for (int x=minX; x<=maxX; x++) {
//            addCell(x-minX+1, 0, String.format("%d", x));
            mapGrid.getColumnConstraints().add(new ColumnConstraints(CELL_WIDTH));
        }
        for (int y=maxY; y>=minY; y--) {
//            addCell(0, maxY-y+1, String.format("%d", y));
            mapGrid.getRowConstraints().add(new RowConstraints(CELL_HEIGHT));
        }

        for (WorldElement element: map.getGrass().values()) {
            addCell(element.getPosition().getX()-minX+1, maxY-element.getPosition().getY()+1, "___", Color.GREEN);
        }
        for (List<Animal> list: map.getAnimals().values()) {
            if (list.isEmpty()) continue;
            Animal element = list.get(0);
            addCell(element.getPosition().getX()-minX+1, maxY-element.getPosition().getY()+1, "_"+element+"_", Color.RED, element.getHealth());
        }


        animalCountLabel.setText(String.format("%d", stats.animalCount()));
    }

    private void clearGrid() {
        mapGrid.getChildren().retainAll(mapGrid.getChildren().get(0));
        mapGrid.getColumnConstraints().clear();
        mapGrid.getRowConstraints().clear();
    }

    private void addCell(int x, int y, String stringValue) {
        Label label = new Label(stringValue);
        mapGrid.add(label, x, y);
        GridPane.setHalignment(label, HPos.CENTER);
    }
    private void addCell(int x, int y, String stringValue, Color color) {
        Label label = new Label(stringValue);
        label.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
        mapGrid.add(label, x, y);
        GridPane.setHalignment(label, HPos.CENTER);
    }
    private void addCell(int x, int y, String stringValue, Color color, double opacity) {
        Label label = new Label(stringValue);
        label.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
        label.setOpacity(opacity);
        mapGrid.add(label, x, y);
        GridPane.setHalignment(label, HPos.CENTER);
    }

        public void setWorldMap(WorldMap map) {
        this.map = map;
    }

    @Override
    public void mapChanged(WorldMap worldMap, String message) {
        Platform.runLater(() -> {
            drawMap(message);
        });
    }

    public void pause() {
    }

    public void click(MouseEvent event) {
        Node clickedNode = event.getPickResult().getIntersectedNode();
        if (clickedNode != mapGrid) {
            Node parent = clickedNode.getParent();
            while (parent != mapGrid) {
                clickedNode = parent;
                parent = clickedNode.getParent();
            }
            Integer colIndex = GridPane.getColumnIndex(clickedNode);
            Integer rowIndex = GridPane.getRowIndex(clickedNode);
            System.out.println("col: " + colIndex + " row: " + rowIndex);
        }
    }

    public void setStats(WorldStats stats) {
        this.stats = stats;
    }
}
