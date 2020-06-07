package org.pclg.security;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import org.pclg.log.LoggerFactory;

/**
 * @author El Coyote Cojo
 * @since 1/09/19 12:47
 */
public final class PasswordDialogFX extends Application {
    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.makeLog4J();

    public void start(final Stage stage) {
        final double rem = new Text("").getLayoutBounds().getHeight();

        final GridPane pane = new GridPane();
        pane.setHgap(0.8 * rem);
        pane.setVgap(0.8 * rem);
        pane.setPadding(new Insets(0.8 * rem));

        final Label passwordLabel = new Label("Password:");
        final PasswordField password = new PasswordField();
        password.setPromptText("Choose a password");
        final Button okButton = new Button("Ok");
        okButton.setOnAction(event -> LOGGER.warn(password.getText()));

        pane.add(passwordLabel, 0, 1);
        pane.add(password, 1, 1);
        pane.add(okButton, 0, 3, 2, 1);

        GridPane.setHalignment(passwordLabel, HPos.RIGHT);
        GridPane.setHalignment(okButton, HPos.CENTER);

        stage.setScene(new Scene(pane));
        stage.setTitle("TextControlTest");
        stage.show();
    }

    public static void main(final String[] args) {
        launch(args);
    }
}
