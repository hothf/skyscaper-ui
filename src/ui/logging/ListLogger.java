package ui.logging;

import javafx.application.Platform;
import javafx.scene.control.ListCell;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import logger.Logger;
import logger.Severity;
import javafx.scene.control.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * A ui component for displaying log items in a list.
 */
public class ListLogger implements Logger {

    private ListView<Log> listView;
    private SimpleDateFormat dateFormatter;

    /**
     * Creates a new log item ui component.
     *
     * @param listView the list used for displaying the items
     */
    public ListLogger(ListView<Log> listView) {
        this.listView = listView;
        this.dateFormatter = new SimpleDateFormat("[HH:mm:ss]");

        listView.setCellFactory(list -> new LoggerCell());
    }

    @Override
    public ArrayList<Severity> getSeverityFilter() {
        return new ArrayList<>();
    }

    @Override
    public void log(Severity severity, String logMessage) {

        String timeStamp = formatTime(Calendar.getInstance().getTime());

        String level;
        Paint paint;

        switch (severity) {
            case WARNING:
                paint = Color.ORANGE;
                level = "W:";
                break;
            case ERROR:
                paint = Color.RED;
                level = "E:";
                break;
            default:
                paint = Color.BLACK;
                level = "I:";
        }

        String message = timeStamp + " " + level + " " + logMessage;

        postUiUpdate(new Log(message, paint));
    }

    /**
     * Logs a success.
     */
    public void successLog() {

        String timeStamp = formatTime(Calendar.getInstance().getTime());

        postUiUpdate(new Log(timeStamp + " S: " + "Moving successful !", Color.GREEN));
    }


    /**
     * Posts a ui update with the given log entry.
     *
     * @param log the log entry
     */
    private void postUiUpdate(Log log) {
        Platform.runLater(() -> {
            listView.getItems().add(log);

            listView.scrollTo(listView.getItems().size() - 1);
        });
    }

    private String formatTime(Date date) {
        return dateFormatter.format(date);
    }

    /**
     * Custom log item generated from log entries.
     */
    public static class Log {

        private String message;
        private Paint paint;

        /**
         * Creates a new log with the given message and paint.
         *
         * @param message the message
         * @param paint   the paint
         */
        public Log(String message, Paint paint) {
            this.message = message;
            this.paint = paint;
        }

        /**
         * Retrieves the message.
         *
         * @return the message
         */
        public String getMessage() {
            return message;
        }

        /**
         * Retrieves the paint.
         *
         * @return the paint
         */
        public Paint getPaint() {
            return paint;
        }

    }

    /**
     * Custom ui manipulations of cells.
     */
    private static class LoggerCell extends ListCell<Log> {

        @Override
        protected void updateItem(Log item, boolean empty) {
            super.updateItem(item, empty);

            if (item != null) {
                setText(item.getMessage());
                setTextFill(item.getPaint());
            }
        }
    }
}
