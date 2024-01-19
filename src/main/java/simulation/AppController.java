package simulation;//import simulation.Simulation;
//import simulation.SimulationEngine;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import model.*;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//public class AppController implements MapChangeListener {
public class AppController {
//    private WorldMap map;
    @FXML private TextField animalCountField;
    @FXML private TextField grassCountField;
    private final ExecutorService executorService;

    public AppController() {
        this.executorService = Executors.newFixedThreadPool(4);
    }

    private boolean validAnimalCount(int value) {
        return 1<=value && value<=1000;
    }

    @FXML
    public void initialize() {
        animalCountField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
        animalCountField.textProperty().addListener(((observableValue, oldValue, newValue) -> {
            try {
                animalCountField.getTextFormatter().getValueConverter().fromString(newValue);
                if (!validAnimalCount((int) animalCountField.getTextFormatter().getValue())) throw new NumberFormatException();
                animalCountField.setBorder(null);
            } catch (NumberFormatException e) {
                animalCountField.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, new CornerRadii(3), new BorderWidths(2), new Insets(-2))));
            }
        }));
    }

//    public void setWorldMap(WorldMap map) {
//        this.map = map;
//    }

//    @Override
//    public void mapChanged(WorldMap worldMap, String message) {
//        Platform.runLater(() -> {
//            drawMap(message);
//        });
//    }

    public void runSimulation() throws IOException {
        List<Vector2d> positions = List.of(new Vector2d(2, 2), new Vector2d(3, 4));
//        WorldMap map = new GrassField(10);
//        map.addObserver(new ConsoleMapDisplay());

        SimulationController controller = new SimulationController();
//        map.addObserver(controller);
//        controller.setWorldMap(map);

        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        loader.setController(controller);
        loader.setLocation(getClass().getClassLoader().getResource("simulation.fxml"));
        BorderPane viewRoot = loader.load();

        var scene = new Scene(viewRoot);
        stage.setScene(scene);
//        stage.setTitle("Simulation " + map.getId());
        stage.minWidthProperty().bind(viewRoot.minWidthProperty());
        stage.minHeightProperty().bind(viewRoot.minHeightProperty());
        stage.show();

//        Simulation simulation = new Simulation(positions, map);
        Simulation simulation = new Simulation(positions);
        executorService.submit(simulation);
    }
}