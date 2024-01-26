package simulation;

import javafx.scene.control.TextField;

public class InputField {
    private final TextField field;
    private final int defaultValue;

    public InputField(TextField field, int defaultValue) {
        this.field = field;
        this.defaultValue = defaultValue;
        field.setPromptText(String.format("%d", defaultValue));
    }

    public int getValue() {
        if ("".equals(field.getText())) return defaultValue;
        else return Integer.parseInt(field.getText());
    }

    public int getDefault() {
        return defaultValue;
    }

    public TextField getField() {
        return field;
    }

    public String getName() {
        return field.getId().replaceFirst("Field", "");
    }
}
