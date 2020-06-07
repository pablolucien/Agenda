package org.pclg.runtime;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import org.pclg.Globals;
import org.pclg.log.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

/**
 * @author El Coyote Cojo
 * @since 1/09/19 12:47
 */
public final class JavaShell extends Application {
    /** Logger for this class. */
    private static final Logger LOGGER = LoggerFactory.makeLog4J();
    private PrintWriter writer;
    private InputStream processInputStream;
    private InputStream processErrorStream;

    public void start(final Stage stage) {
        LOGGER.debug(LoggerFactory.ENTER_METHOD);
        setUpShellProcess();
        setUpShellGUI(stage);
        stage.show();
        LOGGER.debug(LoggerFactory.EXIT_METHOD);
    }

    private void setUpShellGUI(final Stage stage) {
        LOGGER.debug(LoggerFactory.ENTER_METHOD);
        final double rem = new Text("").getLayoutBounds().getHeight();

        final TextArea output = createTextArea();
        final Label commandLabel = new Label("Command:");
        final TextField command = createCommandField();

        final Button byeButton = new Button("Bye!");
        byeButton.setOnAction(JavaShell::exitOrderly);

        final Button pieButton = new Button("Pie!");
        pieButton.setOnAction(JavaShell::showPieChart);

        final GridPane pane = createMainPane(rem);

        pane.add(output,       0, 0, 2, 1);
        pane.add(commandLabel, 0, 2);
        pane.add(command,      1, 2);
        pane.add(pieButton,    0, 3);
        pane.add(byeButton,    1, 3);

        GridPane.setHalignment(commandLabel, HPos.RIGHT);
        GridPane.setHalignment(pieButton, HPos.RIGHT);
        GridPane.setHalignment(byeButton, HPos.RIGHT);

        stage.setScene(new Scene(pane));
        stage.setTitle("JavaShellFX");

        stage.setOnCloseRequest(JavaShell::exitOrderly);

        command.requestFocus();

        new Thread(() -> capture("stdout", processInputStream, output)).start();
        new Thread(() -> capture("stderr", processErrorStream, output)).start();
        //Platform.runLater(() -> capture(processInputStream, output));
        //Platform.runLater(() -> capture(processErrorStream, output));
        LOGGER.debug(LoggerFactory.EXIT_METHOD);
    }

    private static Stage pieChartStage;
    private static void showPieChart(ActionEvent actionEvent) {
        if (pieChartStage == null) {
            PieChart chart = new PieChart();
            chart.getData().addAll(
                    new PieChart.Data("Asia", 4298723000.0),
                    new PieChart.Data("North America", 355361000.0),
                    new PieChart.Data("South America", 616644000.0),
                    new PieChart.Data("Europe", 742452000.0),
                    new PieChart.Data("Africa", 1110635000.0),
                    new PieChart.Data("Oceania", 38304000.0));
            chart.setTitle("Population of the Continents");

            pieChartStage = new Stage();
            pieChartStage.setWidth(500);
            pieChartStage.setHeight(500);
//pieChartStage.setX(stage.getX() + stage.getWidth());
//pieChartStage.setY(stage.getY());
            pieChartStage.setScene(new Scene(chart));
        }
        pieChartStage.show();
    }

    private static void exitOrderly(final Event event) {
        final Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure?");
        alert.setHeaderText("Exit this great application?");
        final Optional<ButtonType> typeOptional = alert.showAndWait();
        if (typeOptional.isPresent() && typeOptional.get() == ButtonType.OK) {
            //Platform.exit();
            System.exit(Globals.THE_ANSWER_TO_THE_ULTIMATE_QUESTION_OF_LIFE_THE_UNIVERSE_AND_EVERYTHING);
        } else {
            event.consume();
        }
    }

    private static GridPane createMainPane(final double rem) {
        final GridPane pane = new GridPane();
        pane.setHgap(0.8 * rem);
        pane.setVgap(0.8 * rem);
        pane.setPadding(new Insets(0.8 * rem));
        pane.setPrefSize(800, 600);
        return pane;
    }

    private TextField createCommandField() {
        final TextField command = new TextField();
        command.setPromptText("Enter command");
        command.setOnAction(event -> {
            final String input = command.getText();
            writer.println(input + '\n');
            writer.flush();
            command.clear();
        });
        return command;
    }

    private static TextArea createTextArea() {
        final TextArea output = new TextArea();
        output.setPrefRowCount(500);
        output.setPrefColumnCount(100);
        output.setWrapText(false);
        output.setEditable(false);
        output.setFont(Font.font("Monospaced", FontWeight.BOLD, FontPosture.REGULAR, 12));
        output.setStyle("background-color: #0F0");
        return output;
    }

    private void capture(final String name, final InputStream stream, final TextArea output) {
        try (final BufferedReader errReader = new BufferedReader(new InputStreamReader(stream))) {
            String line;
            while ((line = errReader.readLine()) != null) {
                final String text = line;
                Platform.runLater(() -> {
                    output.appendText(text);
                    output.appendText("\n");
                    LOGGER.error(name + " -> " + text);
                });
            }
        } catch (final IOException ex) {
            LOGGER.error(LoggerFactory.ERROR_TAG, ex);
            Platform.runLater(() -> {
                output.appendText("\n\n");
                output.appendText(getClass().getName());
                output.appendText(ex.toString());
            });
        } catch (final Exception ex) {
            Platform.runLater(() ->
                    showErrorMessage(ex, "Error in capture()"));
        }
    }

    private void setUpShellProcess() {
        LOGGER.debug(LoggerFactory.ENTER_METHOD);
        try {
            // I know that this is non portable, but this is exactly the point of this class
            // noinspection CallToRuntimeExec
            final Process process = Runtime.getRuntime().exec("cmd");
            final OutputStream outputStream = process.getOutputStream();
            writer = new PrintWriter(outputStream);
            processInputStream = process.getInputStream();
            processErrorStream = process.getErrorStream();
        } catch (final IOException ex) {
            LOGGER.error(ex);
            //JOptionPane.showMessageDialog(null, ex.getMessage(), "Error in setUpShellProcess()", JOptionPane.ERROR_MESSAGE);
            showErrorMessage(ex, "Error in setUpShellProcess()");
        }
        LOGGER.debug(LoggerFactory.EXIT_METHOD);
    }

    private static void showErrorMessage(final Throwable throwable, final String headerText) {
        final Alert alert = new Alert(Alert.AlertType.ERROR, throwable.getMessage());
        alert.setHeaderText(headerText);
//alert.setGraphic(new ImageView("dialogs/bomb.png"));
        final TextArea stackTrace = new TextArea();
        final StringWriter out = new StringWriter();
        throwable.printStackTrace(new PrintWriter(out));
        stackTrace.setText(out.toString());
        alert.getDialogPane().setExpandableContent(stackTrace);
        alert.showAndWait();
    }

    public static void main(final String[] args) {
        launch(args);
    }
}
