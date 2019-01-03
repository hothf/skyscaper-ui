package ui;

import app.SkyscaperApp;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import ui.logging.ListLogger;
import ui.util.ProgramState;

public class MainController {

    public Label statusText;
    public Button moveButton;
    public Label dropTextSource;
    public ListView<ListLogger.Log> logList;
    public Label dropTextDestination;

    public ProgressIndicator stateIndicator;

    private String sourcePath;
    private String destinationPath;
    private ProgramState currentProgramState = ProgramState.IDLE;

    private ListLogger listLogger;

    @FXML
    public void initialize() {

        listLogger = new ListLogger(logList);

        updateStates();

        SkyscaperApp.INSTANCE.setLogger(listLogger);
    }

    /**
     * Performs an upload of the translation file.
     *
     * @param actionEvent the click action event
     */
    public void move(ActionEvent actionEvent) {

        if (sourcePath == null || destinationPath == null) {
            return;
        }

        currentProgramState = ProgramState.PERFORMING;
        updateStates();

        SkyscaperApp.INSTANCE.performAsync(new String[]{sourcePath, destinationPath}, this::postCompletionOnMain);
    }

    /**
     * Updates the main view with the success or failure of the operation.
     *
     * @param success true if the operation was successful, false otherwise
     */
    private void postCompletionOnMain(boolean success) {
        Platform.runLater(() -> {
            if (success) {
                destinationPath = null;
                sourcePath = null;

                listLogger.successLog();
            }

            currentProgramState = ProgramState.IDLE;
            updateStates();
        });
    }

    /**
     * Called on a drag event of a file.
     *
     * @param dragEvent the drag event
     */
    public void onDrag(DragEvent dragEvent) {
        Dragboard db = dragEvent.getDragboard();
        if (db.hasFiles()) {
            dragEvent.acceptTransferModes(TransferMode.COPY);
        } else {
            dragEvent.consume();
        }
    }

    /**
     * Called on the drop event of a source file.
     *
     * @param dragEvent the drag event
     */
    public void onDroppedSource(DragEvent dragEvent) {

        sourcePath = handleDragDrop(dragEvent);

        updateStates();
    }

    /**
     * Called on the drop event of a destination file.
     *
     * @param dragEvent the drag event
     */
    public void onDroppedDestination(DragEvent dragEvent) {

        destinationPath = handleDragDrop(dragEvent);

        updateStates();
    }

    /**
     * Handles dropping of a drag drop.
     *
     * @param dragEvent the drag event
     * @return the path of a dropped file or null, if none could be retrieved
     */
    private String handleDragDrop(DragEvent dragEvent) {

        String path = null;

        Dragboard db = dragEvent.getDragboard();
        boolean success = false;
        if (db.hasFiles()) {
            success = true;
            path = db.getFiles().get(0).getPath();
        }
        dragEvent.setDropCompleted(success);
        dragEvent.consume();

        return path;
    }

    /**
     * Updates all visible ui states.
     */
    private void updateStates() {

        switch (currentProgramState) {
            case IDLE:

                statusText.setText("Ready.");
                stateIndicator.setVisible(false);

                moveButton.setDisable(sourcePath == null || destinationPath == null);

                dropTextSource.setText(sourcePath == null ? "Source" : sourcePath);
                dropTextDestination.setText(destinationPath == null ? "Destination" : destinationPath);

                break;
            case PERFORMING:

                statusText.setText("Performing...");
                stateIndicator.setVisible(true);

                moveButton.setDisable(true);

                break;
        }

    }


}

