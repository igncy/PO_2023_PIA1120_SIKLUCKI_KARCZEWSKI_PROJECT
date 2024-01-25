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
    @FXML private TextField grassGrowthField;
    @FXML private TextField animalCountField;
    @FXML private TextField animalEnergyField;
    @FXML private TextField animalSatietyField;
    @FXML private TextField animalBreedingEnergyField;
    @FXML private TextField mutationMinField;
    @FXML private TextField mutationMaxField;
    @FXML private TextField genomeLengthField;

    @FXML private ComboBox<String> grassSelect;
    @FXML private ComboBox<String> mutationSelect;
    @FXML private ComboBox<String> configSelect;

    @FXML private ScrollPane logPane;
    @FXML private VBox logPaneBox;

    private int grassType = 0;
    private int mutationType = 0;
    private int config = 0;

    private final ExecutorService executorService = Executors.newFixedThreadPool(4);
    private int simulationCount = 1;
    private final Gson gson = new Gson();
    private final String configPath = getClass().getClassLoader().getResource("config").getPath();

    private final List<InputField> inputFields = new ArrayList<>();
    private InputField mapWidthInput;
    private InputField mapHeightInput;
    private InputField grassCountInput;
    private InputField grassEnergyInput;
    private InputField grassGrowthInput;
    private InputField animalCountInput;
    private InputField animalEnergyInput;
    private InputField animalSatietyInput;
    private InputField animalBreedingEnergyInput;
    private InputField mutationMinInput;
    private InputField mutationMaxInput;
    private InputField genomeLengthInput;

    @FXML
    public void initialize() {
        mapWidthInput = new InputField(mapWidthField, 500);
        mapHeightInput = new InputField(mapHeightField, 500);
        grassCountInput = new InputField(grassCountField, 20);
        grassEnergyInput = new InputField(grassEnergyField, 10);
        grassGrowthInput = new InputField(grassGrowthField, 10);
        animalCountInput = new InputField(animalCountField, 20);
        animalEnergyInput = new InputField(animalEnergyField, 50);
        animalSatietyInput = new InputField(animalSatietyField, 75);
        animalBreedingEnergyInput = new InputField(animalBreedingEnergyField, 50);
        mutationMinInput = new InputField(mutationMinField, 0);
        mutationMaxInput = new InputField(mutationMaxField, 5);
        genomeLengthInput = new InputField(genomeLengthField, 5);

        inputFields.add(mapWidthInput);
        inputFields.add(mapHeightInput);
        inputFields.add(grassCountInput);
        inputFields.add(grassEnergyInput);
        inputFields.add(grassGrowthInput);
        inputFields.add(animalCountInput);
        inputFields.add(animalEnergyInput);
        inputFields.add(animalSatietyInput);
        inputFields.add(animalBreedingEnergyInput);
        inputFields.add(mutationMinInput);
        inputFields.add(mutationMaxInput);
        inputFields.add(genomeLengthInput);

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
            if (!file.getName().equals("default.json"))
                updateConfigSelect(file.getName().replaceFirst(".json", ""));
        }
    }

    public void runSimulation() throws IOException {
        log(grassSelect.getValue());
        log(mutationSelect.getValue());
        log(configSelect.getValue());
        WorldSettings settings = loadConfig();

        WorldMap map = new GrassField(10, settings);

        SimulationController controller = new SimulationController();
        map.addObserver(controller);
        controller.setWorldMap(map);

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

        Simulation simulation = new Simulation(map, settings);
        executorService.submit(simulation);
        log("simulation #" + simulationCount++ + " started");
        controller.drawMap("");
    }

    private WorldSettings getConfig() {
        return new WorldSettings(
                mapWidthInput.getValue(),
                mapHeightInput.getValue(),
                grassType,
                grassCountInput.getValue(),
                grassEnergyInput.getValue(),
                grassGrowthInput.getValue(),
                animalCountInput.getValue(),
                animalEnergyInput.getValue(),
                animalSatietyInput.getValue(),
                animalBreedingEnergyInput.getValue(),
                mutationType,
                mutationMinInput.getValue(),
                mutationMaxInput.getValue(),
                genomeLengthInput.getValue()
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
            System.out.println(data);
            return gson.fromJson(data.toString(), WorldSettings.class);
        } catch (FileNotFoundException e) {
            log("error while loading config");
            return getConfig();
        }
    }

    private void updateConfigSelect(String value) {
        if (!configSelect.getItems().contains(value))
            configSelect.getItems().add(value);
    }

    private void log(String message) {
        logPaneBox.getChildren().add(new Label(message));
        logPane.setContent(logPaneBox);
    }
}