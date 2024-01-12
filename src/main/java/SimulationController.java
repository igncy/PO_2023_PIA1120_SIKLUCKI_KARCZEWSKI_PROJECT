import Simulation;
import SimulationEngine;
import model.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

import java.util.ArrayList;
import java.util.List;

public class SimulationController implements MapChangeListener {
    private WorldMap map;
    @FXML private Label infoLabel;
    @FXML private TextField textField;
    @FXML private GridPane mapGrid;
    @FXML private Label moveInfo;
    private int updateCount = 1;

    public void setWorldMap(WorldMap map) {
        this.map = map;
    }

    public void drawMap(String message) {
        infoLabel.setText(String.format("update #%d", updateCount++));
        moveInfo.setText(message);
        clearGrid();

        int CELL_WIDTH = 30, CELL_HEIGHT = 30;
        Boundary boundary = map.getCurrentBounds();
        int minX = boundary.lowerLeft().getX();
        int maxX = boundary.upperRight().getX();
        int maxY = boundary.upperRight().getY();
        int minY = boundary.lowerLeft().getY();

        addCell(0, 0, "y\\x");
        mapGrid.getColumnConstraints().add(new ColumnConstraints(CELL_WIDTH));
        mapGrid.getRowConstraints().add(new RowConstraints(CELL_HEIGHT));

        for (int x=minX; x<=maxX; x++) {
            addCell(x-minX+1, 0, String.format("%d", x));
            mapGrid.getColumnConstraints().add(new ColumnConstraints(CELL_WIDTH));
        }
        for (int y=maxY; y>=minY; y--) {
            addCell(0, maxY-y+1, String.format("%d", y));
            mapGrid.getRowConstraints().add(new RowConstraints(CELL_HEIGHT));
        }

        for (WorldElement element: map.getElements()) {
            addCell(element.getPosition().getX()-minX+1, maxY-element.getPosition().getY()+1, element.toString());
        }
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

    @Override
    public void mapChanged(WorldMap worldMap, String message) {
        Platform.runLater(() -> {
            drawMap(message);
        });
    }

    public void onSimulationStartClicked() {
        ArrayList<Simulation> simulations = new ArrayList<>();
        List<MoveDirection> directions = OptionsParser.parse(textField.getText().split(" "));
        List<Vector2d> positions = List.of(new Vector2d(2, 2), new Vector2d(3, 4));

        WorldMap map = new GrassField(10);
        map.addObserver(new ConsoleMapDisplay());
        map.addObserver(this);
        setWorldMap(map);

        simulations.add(new Simulation(positions, directions, map));

        SimulationEngine engine = new SimulationEngine(simulations);
        engine.runAsync();
//        engine.runAsyncInThreadPool();
    }
}