package ui;

import app.SkyscaperApp;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import ui.logging.ListLogger;
import ui.util.ProgramState;

import java.io.File;

public class MainController {

    public Label statusText;
    public Button moveButton;
    public Label dropTextSource;
    public Label dropTextDestination;
    public ProgressIndicator stateIndicator;
    public ListView<ListLogger.Log> logList;

    private String sourcePath;
    private String destinationPath;
    private ListLogger listLogger;
    private ProgramState currentProgramState = ProgramState.IDLE;

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
                sourcePath = null;

                listLogger.successLog();
            }

            currentProgramState = ProgramState.IDLE;
            updateStates();
        });
    }

    /**
     * Opens a file chooser for choosing the source file.
     *
     * @param event the click event opening the chooser
     */
    public void openSourceFileChooser(Event event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a resource file");
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            sourcePath = selectedFile.getPath();

            updateStates();
        }
    }

    /**
     * Opens a directory chooser for opening the destination dir.
     *
     * @param event the click event opening the chooser
     */
    public void openDestinationDirChooser(Event event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select destination directory");
        File selectedDir = directoryChooser.showDialog(null);

        if (selectedDir != null) {
            destinationPath = selectedDir.getPath();

            updateStates();
        }
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
     * Called on a drag enter event.
     */
    public void onSourceDragEnter() {
        changeDragDropStyleFor(dropTextSource, true, sourcePath != null);
    }

    /**
     * Called on a drag exit event.
     */
    public void onSourceDragExit() {
        changeDragDropStyleFor(dropTextSource, false, sourcePath != null);
    }

    /**
     * Called on a drag enter event.
     */
    public void onDestinationDragEnter() {
        changeDragDropStyleFor(dropTextDestination, true, destinationPath != null);
    }

    /**
     * Called on a drag exit event.
     */
    public void onDestinationDragExit() {
        changeDragDropStyleFor(dropTextDestination, false, destinationPath != null);
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
     * Changes the drag and drop style for the given label based on the given attribute flags.
     * There can only be one active flag. If both flags are non-null, the enter flag has a higher priority.
     *
     * @param label    the label to change to style of
     * @param enter    the enter flag
     * @param accepted the accepted flag
     */
    private void changeDragDropStyleFor(Label label, Boolean enter, Boolean accepted) {

        label.getStyleClass().clear();

        String style = "dropText";

        if (enter != null && enter) {
            style = "dropTextSelected";
        }

        if (accepted != null && accepted) {
            style = "dropTextAccepted";
        }

        label.getStyleClass().add(style);
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

                changeDragDropStyleFor(dropTextSource, null, sourcePath != null);
                changeDragDropStyleFor(dropTextDestination, null, destinationPath != null);

                break;
            case PERFORMING:

                statusText.setText("Performing...");
                stateIndicator.setVisible(true);

                moveButton.setDisable(true);

                break;
        }

    }


}

