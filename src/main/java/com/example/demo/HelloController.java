package org.example.demo;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.time.Duration;
import java.time.Instant;

public class HelloController {

    @FXML
    private Button startButton, resetButton;
    @FXML
    private Label clockTime;
    @FXML
    private ListView<String> lapTimesList;

    private boolean isTimerRunning = false;
    private Instant startTime;
    private Task<Void> timerTask;

    @FXML
    private void initialize() {
        startButton.setOnAction(event -> toggleTimer());
        resetButton.setOnAction(event -> handleResetOrLap());
    }

    private void toggleTimer() {
        if (isTimerRunning) {
            stopTimer();
        } else {
            startTimer();
        }
    }

    private void handleResetOrLap() {
        if ("Reset".equals(resetButton.getText())) {
            resetClock();
        } else {
            recordLapTime();
        }
    }

    private void startTimer() {
        isTimerRunning = true;
        if (startTime == null) startTime = Instant.now();

        timerTask = createTimerTask();
        clockTime.textProperty().bind(timerTask.messageProperty());
        new Thread(timerTask).start();

        startButton.setText("Stop");
        resetButton.setText("Lap Time");
    }

    private Task<Void> createTimerTask() {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                while (!isCancelled()) {
                    updateMessage(formatDuration(Duration.between(startTime, Instant.now())));
                    Thread.sleep(10);
                }
                return null;
            }
        };
    }

    private void stopTimer() {
        if (timerTask != null) timerTask.cancel();
        isTimerRunning = false;
        clockTime.textProperty().unbind();
        startButton.setText("Start");
        resetButton.setText("Reset");
    }

    private void resetClock() {
        clockTime.setText("00:00:00");
        startTime = null;
    }

    private void recordLapTime() {
        if (startTime != null) {
            lapTimesList.getItems().add(formatDuration(Duration.between(startTime, Instant.now())));
        }
    }

    private String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();
        long millis = duration.toMillisPart();
        return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, millis);
    }
}
