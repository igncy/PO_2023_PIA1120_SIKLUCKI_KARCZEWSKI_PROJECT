package simulation;

import com.google.gson.Gson;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.*;
import javafx.fxml.FXML;
import util.WorldSettings;
import util.WorldStats;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppController {
    @FXML private TextField mapWidthField;
    @FXML private TextField mapHeightField;
    @FXML private TextField grassCountField;
    @FXML private TextField grassEnergyField;
    @FXML private TextField energyLossField;
    @FXML private TextField grassGrowthField;
    @FXML private TextField animalCountField;
    @FXML private TextField animalEnergyField;
    @FXML private TextField animalSatietyField;
    @FXML private TextField animalBreedingEnergyField;
    @FXML private TextField mutationMinField;
    @FXML private TextField mutationMaxField;
    @FXML private TextField genomeLengthField;
    @FXML private TextField sleepTimeField;

    @FXML private ComboBox<String> grassSelect;
    @FXML private ComboBox<String> mutationSelect;
    @FXML private ComboBox<String> configSelect;
    @FXML private CheckBox csvSelect;

    @FXML private ScrollPane logPane;
    @FXML private VBox logPaneBox;

    private int grassType = 0;
    private int mutationType = 0;
    private int config = 0;

    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private int simulationCount = 1;
    private final Gson gson = new Gson();
    private final String configPath = getClass().getClassLoader().getResource("config").getPath();

    private final List<InputField> inputFields = new ArrayList<>();
    private InputField mapWidthInput;
    private InputField mapHeightInput;
    private InputField grassCountInput;
    private InputField grassEnergyInput;
    private InputField energyLossInput;
    private InputField grassGrowthInput;
    private InputField animalCountInput;
    private InputField animalEnergyInput;
    private InputField animalSatietyInput;
    private InputField animalBreedingEnergyInput;
    private InputField mutationMinInput;
    private InputField mutationMaxInput;
    private InputField genomeLengthInput;
    private InputField sleepTimeInput;

    @FXML
    public void initialize() {
        mapWidthInput = new InputField(mapWidthField, 20);
        mapHeightInput = new InputField(mapHeightField, 20);
        grassCountInput = new InputField(grassCountField, 20);
        grassEnergyInput = new InputField(grassEnergyField, 10);
        energyLossInput = new InputField(energyLossField, 2);
        grassGrowthInput = new InputField(grassGrowthField, 5);
        animalCountInput = new InputField(animalCountField, 20);
        animalEnergyInput = new InputField(animalEnergyField, 20);
        animalSatietyInput = new InputField(animalSatietyField, 50);
        animalBreedingEnergyInput = new InputField(animalBreedingEnergyField, 30);
        mutationMinInput = new InputField(mutationMinField, 0);
        mutationMaxInput = new InputField(mutationMaxField, 10);
        genomeLengthInput = new InputField(genomeLengthField, 10);
        sleepTimeInput = new InputField(sleepTimeField, 25);

        inputFields.add(mapWidthInput);
        inputFields.add(mapHeightInput);
        inputFields.add(grassCountInput);
        inputFields.add(grassEnergyInput);
        inputFields.add(energyLossInput);
        inputFields.add(grassGrowthInput);
        inputFields.add(animalCountInput);
        inputFields.add(animalEnergyInput);
        inputFields.add(animalSatietyInput);
        inputFields.add(animalBreedingEnergyInput);
        inputFields.add(mutationMinInput);
        inputFields.add(mutationMaxInput);
        inputFields.add(genomeLengthInput);
        inputFields.add(sleepTimeInput);

        for (InputField obj: inputFields) {
            obj.getField().textProperty().addListener((observable, oldValue, newValue) -> {
                boolean valid = true;
                int value = 0;
                if (!newValue.matches("\\d*")) valid = false;
                else value = Integer.parseInt(newValue);
                if (!valid || value<0) {
                    obj.getField().setText(oldValue);
                    log(obj.getName() + " must be a non-negative integer");
                }
            });
        }

        logPane.vvalueProperty().bind(logPaneBox.heightProperty());

        File[] files = new File(configPath).listFiles();
        for (File file: files) {
            if (!file.getName().equals("default.json") && file.getName().matches(".*\\.json"))
                updateConfigSelect(file.getName().replaceFirst(".json", ""));
        }
    }

    private WorldMap getMap(WorldSettings settings) {
        return switch (grassSelect.getValue()) {
            case "equatorial forests" -> new Globe(simulationCount, settings);
            case "invigorating corpses" -> new Caracass(simulationCount, settings);
            case "grass" -> new GrassField(simulationCount, settings);
            default -> new Globe(simulationCount, settings);
        };
    }

    public void runSimulation() throws IOException {
        WorldSettings settings = loadConfig();
        WorldMap map = getMap(settings);
        WorldStats stats = new WorldStats(map, csvSelect.isSelected());
        SimulationController controller = new SimulationController(settings, map, stats);
        map.addObserver(controller);

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
        stage.setOnHiding(event -> closeSimulation(controller, simulationCount));
        stage.show();

        Simulation simulation = new Simulation(map, settings, controller, stats);
        controller.setSimulation(simulation);
        executorService.submit(simulation);
        log("simulation #" + simulationCount++ + " started");
    }

    private void closeSimulation(SimulationController controller, int id) {
        log("simulation #" + id + " closed");
        controller.stop();
    }

    public void closeApp() {
        executorService.shutdownNow();
    }

    private WorldSettings getConfig() {
        return new WorldSettings(
                mapWidthInput.getValue(),
                mapHeightInput.getValue(),
                grassType,
                grassCountInput.getValue(),
                grassEnergyInput.getValue(),
                grassGrowthInput.getValue(),
                energyLossInput.getValue(),
                animalCountInput.getValue(),
                animalEnergyInput.getValue(),
                animalSatietyInput.getValue(),
                animalBreedingEnergyInput.getValue(),
                mutationType,
                mutationMinInput.getValue(),
                mutationMaxInput.getValue(),
                genomeLengthInput.getValue(),
                sleepTimeInput.getValue()
        );
    }

    public void saveConfig() {
        if (configPath == null) {
            log("error while saving config");
            return;
        }

        String name = new SimpleDateFormat("EEE d MMM HH;mm;ss").format(new java.util.Date());
        String filename = configPath + "/" + name + ".json";

        try {
            File file = new File(filename);
            if (file.createNewFile()) {
                FileWriter fileWriter = new FileWriter(filename);
                fileWriter.write(gson.toJson(getConfig()));
                fileWriter.close();
                updateConfigSelect(name);
                log("config saved as '" + name + "'");
            }
            else throw new IOException();
        } catch (IOException e) {
            log("error while saving config");
        }
    }

    private WorldSettings loadConfig() {
        String name = configSelect.getValue();
        if (name.equals("none"))
            return getConfig();

        if (configPath == null) {
            log("error while loading config");
            return getConfig();
        }

        String filename = configPath + "/" + name + ".json";

        try {
            File file = new File(filename);
            Scanner reader = new Scanner(file);
            StringBuilder data = new StringBuilder();
            while (reader.hasNextLine()) {
                data.append(reader.nextLine());
            }
            reader.close();
            return gson.fromJson(data.toString(), WorldSettings.class);
        } catch (FileNotFoundException e) {
            log("error while loading config");
            return getConfig();
        }
    }

    private void updateConfigSelect(String value) {
        value = value.replaceAll(";", ":");
        if (!configSelect.getItems().contains(value))
            configSelect.getItems().add(value);
    }

    private void log(String message) {
        logPaneBox.getChildren().add(new Label(message));
        logPane.setContent(logPaneBox);
    }
}