package simulation;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import model.*;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppController {//implements MapChangeListener {
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

    public AppController() {
    }

    @FXML
    public void initialize() {
        animalCountField.setPromptText(String.format("%d", animalCount));
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

    private boolean validAnimalCount(int value) {
        return 1<=value && value<=1000;
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
        stage.setTitle("Simulation #" + simulationCount++);
        stage.minWidthProperty().bind(viewRoot.minWidthProperty());
        stage.minHeightProperty().bind(viewRoot.minHeightProperty());
        stage.show();

        Simulation simulation = new Simulation(positions);//, map);
        executorService.submit(simulation);


        System.out.println((int) animalCountField.getTextFormatter().getValue());
        System.out.println(animalCountField.getTextFormatter().getValueConverter().fromString(animalCountField.getText()));
    }

    public void saveConfig() {
    }
}