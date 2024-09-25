package ui;

import javax.swing.*;
import java.awt.*;

public class LoadingScreen {
    private JFrame loadingFrame;
    private JProgressBar progressBar;
    private JLabel messageLabel;

    public LoadingScreen() {
        loadingFrame = new JFrame("Processing...");
        loadingFrame.setUndecorated(true);
        loadingFrame.setSize(400, 100);
        loadingFrame.setLocationRelativeTo(null);
        loadingFrame.setLayout(new BorderLayout());

        progressBar = new JProgressBar(0, 100);
        progressBar.setIndeterminate(true);

        messageLabel = new JLabel("Please wait...", SwingConstants.CENTER);

        loadingFrame.add(messageLabel, BorderLayout.CENTER);
        loadingFrame.add(progressBar, BorderLayout.SOUTH);
    }

    public void setVisible(boolean visible) {
        loadingFrame.setVisible(visible);
    }

    public void setProgress(int progress) {
        progressBar.setIndeterminate(false);
        progressBar.setValue(progress);
    }

    public void setMessage(String message) {
        messageLabel.setText(message);
    }

    public void dispose() {
        loadingFrame.dispose();
    }
}