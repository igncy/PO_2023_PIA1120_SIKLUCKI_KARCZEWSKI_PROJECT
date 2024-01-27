package simulation;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import model.*;
import util.MapChangeListener;
import util.WorldSettings;
import util.WorldStats;

public class SimulationController implements MapChangeListener {
    @FXML private Label infoLabel;
    @FXML private GridPane mapGrid;
    @FXML private Label animalCountLabel;
    @FXML private Label grassCountLabel;
    @FXML private Label emptyTilesLabel;
    @FXML private Label avgEnergyLabel;
    @FXML private Label avgLifespanLabel;
    @FXML private Label avgChildCountLabel;
    @FXML private Label mostCommonGenomeLabel;
    @FXML private Button pauseButton;

    private final WorldMap map;
    private final WorldSettings settings;
    private final WorldStats stats;
    private Simulation simulation;

    public SimulationController(WorldSettings settings, WorldMap map, WorldStats stats) {
        this.settings = settings;
        this.map = map;
        this.stats = stats;
    }

    public void drawMap() {
        clearGrid();
        int CELL_WIDTH = 20, CELL_HEIGHT = 20;

        mapGrid.getColumnConstraints().add(new ColumnConstraints(CELL_WIDTH));
        mapGrid.getRowConstraints().add(new RowConstraints(CELL_HEIGHT));
        for (int x=0; x<settings.mapWidth()-1; x++) {
            mapGrid.getColumnConstraints().add(new ColumnConstraints(CELL_WIDTH));
        }
        for (int y=0; y<settings.mapHeight()-1; y++) {
            mapGrid.getRowConstraints().add(new RowConstraints(CELL_HEIGHT));
        }

        for (WorldElement element: map.getGrass().values()) {
            addCell(element.getPosition().getX(), element.getPosition().getY(), "___", Color.GREEN, 1);
        }
        for (Animal element: map.getAlive()) {
            addCell(element.getPosition().getX(), element.getPosition().getY(), "_" + element + "_", Color.RED, element.getHealth());
        }
    }

    private void updateStats() {
        animalCountLabel.setText(String.format("%d", stats.animalCount()));
        grassCountLabel.setText(String.format("%d", stats.grassCount()));
        emptyTilesLabel.setText(String.format("%d", stats.emptyTiles()));
        avgEnergyLabel.setText(String.format("%.1f", stats.avgEnergy()));
        avgLifespanLabel.setText(String.format("%.1f days", stats.avgLifespan()));
        avgChildCountLabel.setText(String.format("%.1f", stats.avgChildCount()));
        String mcg = stats.mostCommonGenome();
        mostCommonGenomeLabel.setText(String.format("'%s' (%d animal(s)", mcg, stats.genomeCount(mcg)));
    }

    private void clearGrid() {
        mapGrid.getChildren().retainAll(mapGrid.getChildren().get(0));
        mapGrid.getColumnConstraints().clear();
        mapGrid.getRowConstraints().clear();
    }

    public void setDay(int day) {
        Platform.runLater(() -> infoLabel.setText(String.format("%d", day)));
    }

    private void addCell(int x, int y, String stringValue, Color color, double opacity) {
        Label label = new Label(stringValue);
        label.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
        label.setOpacity(opacity);
        mapGrid.add(label, x, y);
        GridPane.setHalignment(label, HPos.CENTER);
    }

    @Override
    public void mapChanged(WorldMap worldMap) {
        Platform.runLater(() -> {
            drawMap();
            updateStats();
        });
    }

    public void pause() {
        simulation.pause(pauseButton);
    }

    public void stop() {
        simulation.stop();
        stats.closeFile();
    }

    public void click(MouseEvent event) {
        Node clicked = event.getPickResult().getIntersectedNode();
        if (clicked != mapGrid) {
            Node parent = clicked.getParent();
            while (parent != mapGrid) {
                clicked = parent;
                parent = clicked.getParent();
            }
            Integer x = GridPane.getColumnIndex(clicked);
            Integer y = GridPane.getRowIndex(clicked);
            System.out.println(map.getAnimals().get(new Vector2d(x, y)));
        }
    }

    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }
}
