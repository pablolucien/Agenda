package org.pclg.media.catalog;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.pclg.dbutil.DbManager;
import org.pclg.tools.PropertiesHelper;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class VideoCatalog extends Application {
    private Connection dbConnection;

    @Override
    public void start(Stage primaryStage) throws Exception {
        final Properties properties = new Properties();
        PropertiesHelper.loadProperties(properties, "VideoCatalog");
        openDatabase(properties);
        Parent root = FXMLLoader.load(getClass().getResource("VideoCatalog.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }

    private void openDatabase(Properties properties) {
        try {
            DbManager dbManager = new DbManager("VideoCatalog");
            final String password = properties.getProperty("VideoCatalog.password");
            final String connectString = properties.getProperty("VideoCatalog.url");
            dbConnection = dbManager.getConnection(properties.getProperty("VideoCatalog.driver"),
                    connectString, properties.getProperty("VideoCatalog.user"), password);
            dbManager.checkAndCreateTables(properties);
        } catch (SQLException | ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        launch(args);
    }
}
