package simulation;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.*;
import javafx.fxml.FXML;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppController {
    @FXML private TextField mapWidthField;
    @FXML private TextField mapHeightField;
    @FXML private TextField grassCountField;
    @FXML private TextField grassEnergyField;
    @FXML private TextField grassGrowthField;
    @FXML private ComboBox grassTypeSelect;
    @FXML private TextField animalCountField;
    @FXML private TextField animalEnergyField;
    @FXML private TextField animalSatietyField;
    @FXML private TextField animalBreedingEnergyField;
    @FXML private TextField mutationMinField;
    @FXML private TextField mutationMaxField;
    @FXML private ComboBox mutationTypeSelect;
    @FXML private TextField genomeLengthField;

    @FXML private ScrollPane logPane;
    @FXML private VBox logPaneBox;

    private int mapWidth = 500;
    private int mapHeight = 500;
    private int grassCount = 20;
    private int grassEnergy = 10;
    private int grassGrowth = 10;
    private int grassType = 0;
    private int animalCount = 20;
    private int animalEnergy = 50;
    private int animalSatiety = 75;
    private int animalBreedingEnergy = 50;
    private int mutationMin = 0;
    private int mutationMax = 5;
    private int mutationType = 0;
    private int genomeLength = 5;

    private final ExecutorService executorService = Executors.newFixedThreadPool(4);
    private int simulationCount = 1;

    @FXML
    public void initialize() {
        TextField[] textFields = {
                mapWidthField,
                mapHeightField,
                grassCountField,
                grassEnergyField,
                grassGrowthField,
                animalCountField,
                animalEnergyField,
                animalSatietyField,
                animalBreedingEnergyField,
                mutationMinField,
                mutationMaxField,
                genomeLengthField,
        };
        for (TextField obj: textFields) {
            obj.textProperty().addListener((observable, oldValue, newValue) -> {
                boolean valid = true;
                int value = 0;
                if (!newValue.matches("\\d*")) valid = false;
                else value = Integer.parseInt(newValue);
                if (!valid || value<0) {
                    obj.setText(oldValue);
                    log(obj.getId().replaceFirst("Field", "") + " must be a non-negative integer");
                }
            });
        }

        logPane.vvalueProperty().bind(logPaneBox.heightProperty());
    }

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
        stage.setTitle("Simulation #" + simulationCount);
        stage.minWidthProperty().bind(viewRoot.minWidthProperty());
        stage.minHeightProperty().bind(viewRoot.minHeightProperty());
        stage.show();

        Simulation simulation = new Simulation(positions);//, map);
        executorService.submit(simulation);

        log("simulation #" + simulationCount + " started");
        simulationCount++;
    }

    public void saveConfig() {
        log("config saved as");
    }

    public void log(String message) {
        logPaneBox.getChildren().add(new Label(message));
        logPane.setContent(logPaneBox);
    }
}