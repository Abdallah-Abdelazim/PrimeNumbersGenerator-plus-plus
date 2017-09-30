package prime_gen.ui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import prime_gen.backend.Buffer;
import prime_gen.backend.OutputWriter;
import prime_gen.backend.PrimeGen;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Controller Class for the app view
 */
public class AppController {

    @FXML
    private TextField nField;
    @FXML
    private TextField outputFileField;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private Button startButton;
    @FXML
    private Label waitMessageLabel;

    private Main main;  // reference to the Main class object

    @FXML
    void startGenerating() throws IOException {

        if (nField.getText().equals("") || outputFileField.getText().equals("")) { // ensures use enters N before executing this function
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please Enter N.\nThe prime numbers will be calculated from 0 to N.");
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(
                    new Image("file:Resources/app_icon.png"));
            stage.initOwner(main.getPrimaryStage());
            alert.showAndWait();
            return;
        }

        startButton.setDisable(true);
        progressIndicator.setVisible(true);
        waitMessageLabel.setVisible(true);

        // operations are done on a background thread , freeing up the JavaFX Application Thread for GUI operations.
        // So that the ui don't become unresponsive, hang,.
        new Thread(new Runnable() {
            @Override
            public void run() {
                Buffer buffer = new Buffer();  // default size is 100, you can pass the size you want.
                PrimeGen primeGen = new PrimeGen(Integer.parseInt(nField.getText()), buffer);  // the producer thread
                FileWriter fWriter = null;
                try {
                    fWriter = new FileWriter(new File(outputFileField.getText()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                OutputWriter outputWriter = new OutputWriter(fWriter, buffer);  // the consumer thread

                long startTime = System.currentTimeMillis();

                primeGen.start();
                outputWriter.start();

                try {
                    // Wait for the threads to die (i.e. finish execution)
                    // join() means cause the MainThread to stop until that thread terminates
                    primeGen.join();
                    outputWriter.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                long endTime = System.currentTimeMillis();

                // both threads has finished
                // time to show the results
                try {
                    if (fWriter!=null){
                        fWriter.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("---------------------***************---------------------");
                System.out.println("Number of prime numbers generated:  " + primeGen.getPrimeNumbersCount());
                System.out.println("Time taken:  " + (endTime - startTime) + "  milli seconds");
                System.out.println("---------------------***************---------------------");

                // show results dialog
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        startButton.setDisable(false);
                        progressIndicator.setVisible(false);
                        waitMessageLabel.setVisible(false);

                        showResultsDialog("-  Number of prime numbers generated:  " + primeGen.getPrimeNumbersCount()
                                + "\n-  Time taken:  " + (endTime - startTime) + "  milli seconds"
                                + "\n-  Prime Numbers are saved to: " + new File(outputFileField.getText()).getAbsolutePath());
                    }
                });

            }
        }).start();
    }

    @FXML
    void changeFileLocation() {
        if (!outputFileField.isEditable()) {
            outputFileField.setEditable(true);
        }
        else {
            outputFileField.setEditable(false);
        }
    }

    @FXML
    void closeApp() {
        Platform.exit();
    }

    @FXML
    void showAboutDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("PrimeNumberGenerator++");
        alert.setContentText("(c) Copyright 2017");
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(
                new Image("file:Resources/app_iconapp_icon.png"));
        stage.initOwner(main.getPrimaryStage());
        alert.showAndWait();

    }

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    private void showResultsDialog(String results) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Results");
        alert.setHeaderText("Results");
        alert.setContentText(results);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(
                new Image("file:Resources/app_icon.png"));
        stage.initOwner(main.getPrimaryStage());
        alert.showAndWait();
    }
}
