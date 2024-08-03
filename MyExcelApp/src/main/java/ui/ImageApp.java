package ui;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class ImageApp {
    private JFrame frame;
    private JTextField imageFilePathField;
    private JButton browseButton;
    private JButton uploadButton;
    private JLabel imageView;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ImageApp().createAndShowGUI();
            }
        });
    }

    private void createAndShowGUI() {
        frame = new JFrame("Image App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel filePanel = new JPanel();
        filePanel.setLayout(new FlowLayout());

        JLabel imageFileLabel = new JLabel("Image File:");
        imageFilePathField = new JTextField(20);
        browseButton = new JButton("Browse");
        browseButton.addActionListener(new BrowseButtonActionListener());

        filePanel.add(imageFileLabel);
        filePanel.add(imageFilePathField);
        filePanel.add(browseButton);

        panel.add(filePanel);

        uploadButton = new JButton("Upload");
        uploadButton.addActionListener(new UploadButtonActionListener());
        panel.add(uploadButton);

        imageView = new JLabel();
        imageView.setPreferredSize(new Dimension(400, 300));
        imageView.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panel.add(imageView);

        frame.getContentPane().add(panel);
        frame.pack();
        frame.setVisible(true);
    }

    private class BrowseButtonActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "gif", "bmp");
            fileChooser.setFileFilter(filter);
            int returnValue = fileChooser.showOpenDialog(frame);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                imageFilePathField.setText(selectedFile.getAbsolutePath());
            }
        }
    }

    private class UploadButtonActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String imageFilePath = imageFilePathField.getText();
            if (imageFilePath!= null &&!imageFilePath.isEmpty()) {
                try {
                    File file = new File(imageFilePath);
                    if (file.exists() && file.isFile()) {
                        ImageIcon imageIcon = new ImageIcon(imageFilePath);
                        imageView.setIcon(imageIcon);
                        frame.pack();
                    } else {
                        JOptionPane.showMessageDialog(frame, "File not found or is not a valid file.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Error reading file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Please select a file.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}