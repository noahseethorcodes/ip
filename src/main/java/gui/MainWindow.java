package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.application.Platform;

import logos.Logos;
/**
 * Controller for the main GUI.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Logos logos;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/User.png"));
    private Image logosImage = new Image(this.getClass().getResourceAsStream("/images/Logos.png"));

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /** Injects the Duke instance */
    public void setLogos(Logos logos) {
        this.logos = logos;
    }

    @FXML
    public void welcome() {
        dialogContainer.getChildren().add(
                DialogBox.getDukeDialog(logos.getWelcome(), logosImage)
        );
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Duke's reply and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = logos.getResponse(input);

        // Exit chat if logos is no longer set as active (i.e. the bye command was given)
        if (!logos.isActive()) {
            Platform.exit();
        }
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getDukeDialog(response, logosImage)
        );
        userInput.clear();
    }
}
