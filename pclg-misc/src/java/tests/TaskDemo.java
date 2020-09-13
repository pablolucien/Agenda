package tests;

//import javafx.application.Application;
//import javafx.application.Platform;
//import javafx.concurrent.Task;
//import javafx.geometry.Insets;
//import javafx.scene.Scene;
//import javafx.scene.control.Button;
//import javafx.scene.control.Label;
//import javafx.scene.control.TextArea;
//import javafx.scene.layout.HBox;
//import javafx.scene.layout.VBox;
//import javafx.stage.FileChooser;
//import javafx.stage.Stage;
//import org.apache.log4j.Logger;
//import org.pclg.log.LoggerFactory;
//
//import java.io.File;
//import java.io.IOException;
//import java.io.UncheckedIOException;
//import java.util.Scanner;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;

/**
 * @author El Coyote Cojo
 * @since 5/09/19 14:06
 */
public final class TaskDemo {//extends Application {
//
//    /**
//     * Logger for this class.
//     */
//    private static final Logger LOGGER = LoggerFactory.makeLog4J();
//    private TextArea content = new TextArea("");
//    private Label status = new Label();
//    private ExecutorService executor = Executors.newCachedThreadPool();
//    private Task<Integer> task;
//    private Button open = new Button("Open");
//    private Button cancel = new Button("Cancel");
//
//    public void start(final Stage stage) {
//        open.setOnAction(event -> read(stage));
//        cancel.setOnAction(event ->
//        {
//            if (task != null) {
//                task.cancel();
//            }
//        });
//        cancel.setDisable(true);
//        stage.setOnCloseRequest(event ->
//        {
//            if (task != null) {
//                task.cancel();
//            }
//            executor.shutdown();
//            Platform.exit();
//        });
//
//        final HBox box = new HBox(10, open, cancel);
//        final VBox pane = new VBox(10, content, box, status);
//        pane.setPadding(new Insets(10));
//        stage.setScene(new Scene(pane));
//        stage.setTitle("TaskDemo");
//        stage.show();
//    }
//
//    private void read(final Stage stage) {
//        if (task != null) {
//            return;
//        }
//        final FileChooser chooser = new FileChooser();
//        chooser.setInitialDirectory(new File(".."));
//        final File file = chooser.showOpenDialog(stage);
//        if (file == null) {
//            return;
//        }
//        content.setText("");
//        task = new Task<Integer>() {
//            public Integer call() {
//                int lines = 0;
//                try (Scanner in = new Scanner(file/*, StandardCharsets.UTF_8*/)) {
//                    while (!isCancelled() && in.hasNextLine()) {
//                        Thread.sleep(10); // simulate work
//                        final String line = in.nextLine();
//                        Platform.runLater(() ->
//                                content.appendText(line + "\n"));
//                        lines++;
//                        updateMessage(lines + " lines read");
//                    }
//                } catch (final InterruptedException e) {
//// task was canceled in sleep
//                } catch (final IOException e) {
//                    throw new UncheckedIOException(null, e);
//                }
//                return lines;
//            }
//        };
//        executor.execute(task);
//        task.setOnScheduled(event ->
//        {
//            cancel.setDisable(false);
//            open.setDisable(true);
//        });
//        task.setOnRunning(event ->
//        {
//            status.setText("Running");
//            status.textProperty().bind(task.messageProperty());
//        });
//        task.setOnFailed(event ->
//        {
//            cancel.setDisable(true);
//            status.textProperty().unbind();
//            status.setText("Failed due to " + task.getException());
//            task = null;
//            open.setDisable(false);
//        });
//        task.setOnCancelled(event ->
//        {
//            cancel.setDisable(true);
//            status.textProperty().unbind();
//            status.setText("Canceled");
//            task = null;
//            open.setDisable(false);
//        });
//        task.setOnSucceeded(event ->
//        {
//            cancel.setDisable(true);
//            status.textProperty().unbind();
//            status.setText("Done reading " + task.getValue() + " lines");
//            task = null;
//            open.setDisable(false);
//        });
//    }
//    public static void main(final String[] args) {
//         launch(args);
//     }
}
