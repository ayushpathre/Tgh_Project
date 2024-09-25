package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdminLogin extends JFrame {
    private JPasswordField passwordField;
    private JButton loginButton;
    private boolean succeeded;
    private JButton togglePasswordButton;

    public AdminLogin() {
        super("Admin Login");

        JPanel topPanel = createTopPanel();
        JPanel footerPanel = createFooterPanel();

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;

        JLabel headerLabel = new JLabel("Admin Login");
        headerLabel.setFont(new Font("Verdana", Font.BOLD, 24));
        gbc.gridy = 0;
        centerPanel.add(headerLabel, gbc);

        gbc.gridy = 1;
        gbc.weighty = 0.1;
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new GridBagLayout());
        GridBagConstraints loginGbc = new GridBagConstraints();
        loginGbc.gridx = 0;
        loginGbc.gridy = 0;
        loginGbc.anchor = GridBagConstraints.CENTER;
        loginGbc.insets = new Insets(10, 10, 10, 10);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Verdana", Font.BOLD, 18));
        loginPanel.add(passwordLabel, loginGbc);

        // Password field and toggle button
        JPanel passwordPanel = new JPanel(new BorderLayout());
        passwordPanel.setPreferredSize(new Dimension(300, 40));

        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Verdana", Font.PLAIN, 18));
        passwordField.setPreferredSize(new Dimension(250, 40)); // Adjusted size
        passwordField.setBorder(BorderFactory.createLineBorder(new Color(70, 130, 180), 1)); // Border color same as button
        passwordPanel.add(passwordField, BorderLayout.CENTER);

        // Toggle button for showing/hiding password
        togglePasswordButton = new JButton(new ImageIcon("eye.png")); // Replace with the path to your eye icon
        togglePasswordButton.setFocusable(false);
        togglePasswordButton.setPreferredSize(new Dimension(40, 40));
        togglePasswordButton.setBackground(new Color(70, 130, 180)); // Same background as login button
        togglePasswordButton.setBorder(BorderFactory.createLineBorder(new Color(70, 130, 180), 1)); // Same border color

        togglePasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (passwordField.getEchoChar() == '\u0000') {
                    // Hide password
                    passwordField.setEchoChar('*');
                    togglePasswordButton.setIcon(new ImageIcon("crossed-eye.png")); // Replace with your closed eye icon
                } else {
                    // Show password
                    passwordField.setEchoChar('\u0000');
                    togglePasswordButton.setIcon(new ImageIcon("eye.png")); // Replace with your open eye icon
                }
            }
        });

        passwordPanel.add(togglePasswordButton, BorderLayout.EAST);
        loginGbc.gridy = 1;
        loginPanel.add(passwordPanel, loginGbc);

        loginGbc.gridy = 2;
        loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(100, 40));
        loginButton.setFont(new Font("Verdana", Font.BOLD, 18));
        loginButton.setBackground(new Color(70, 130, 180));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusable(false);
        loginButton.setBorder(BorderFactory.createLineBorder(new Color(70, 130, 180), 1)); // Same border color

        // Set the button action
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (isPasswordCorrect(new String(passwordField.getPassword()))) {
                    succeeded = true;
                    dispose();
                    FrontPage.main(new String[]{});
                } else {
                    JOptionPane.showMessageDialog(AdminLogin.this,
                            "Invalid password",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    passwordField.setText("");
                    succeeded = false;
                }
            }
        });

        loginPanel.add(loginButton, loginGbc);

        gbc.gridy = 2;
        centerPanel.add(loginPanel, gbc);

        // Set the Login button as the default button for Enter key
        getRootPane().setDefaultButton(loginButton);

        // Apply Header and Footer to Dialog
        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.PAGE_END);

        // Set to full-screen mode
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        if (gd.isFullScreenSupported()) {
            setUndecorated(true);
            gd.setFullScreenWindow(this);
        } else {
            setSize(600, 400); // Fallback size if full-screen is not supported
            setLocationRelativeTo(null);
        }

        setVisible(true);
    }

    public boolean isSucceeded() {
        return succeeded;
    }

    private boolean isPasswordCorrect(String input) {
        // Replace "admin" with the actual password you want to use
        return "TGH@2024".equals(input);
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setPreferredSize(new Dimension(700, 165));
        topPanel.setBackground(Color.CYAN);
        topPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        // Load the image
        ImageIcon logoIcon = null;
        try {
            logoIcon = new ImageIcon(getClass().getResource("tghlogo.png"));
        } catch (Exception e) {
            System.out.println("Image not found.");
        }

        JLabel companyLogoLabel = new JLabel();
        if (logoIcon != null) {
            Image img = logoIcon.getImage();
            Image newImg = img.getScaledInstance(200, 130, Image.SCALE_SMOOTH);
            companyLogoLabel.setIcon(new ImageIcon(newImg));
        } else {
            companyLogoLabel.setText("Logo not found");
        }

        JLabel companyNameField = new JLabel("tgh");
        companyNameField.setFont(new Font("Arial", Font.BOLD, 24));

        JLabel addressField = new JLabel("<html>#901, Ishaan Arcade-II, Opp. Hanumanji Temple,<br>Gokhale Road, Naupada,<br>Thane(West)-400 602.</html>");
        addressField.setFont(new Font("Segoe UI", Font.PLAIN, 16));  // Using a modern font like Segoe UI
        addressField.setForeground(new Color(51, 51, 51));  // Dark grey color for text

        JLabel phoneField = new JLabel("<html>Tel. : +91 25344000 / 67922440 <br>E-mail: ganesh.deshpande@tgh.in <br>www.tgh.in </html>");
        phoneField.setFont(new Font("Segoe UI", Font.PLAIN, 16));  // Consistent font style and size
        phoneField.setForeground(new Color(51, 51, 51));  // Same text color for consistency

//        JLabel appNameLabel = new JLabel("Lendingkart Program");
//        appNameLabel.setFont(new Font("Verdana", Font.BOLD, 40));
//        appNameLabel.setForeground(new Color(70, 130, 180));

        // New header text with adjusted size and style
        String headerText = "<html><div style='text-align: center;'>"
                + "<span style='font-size: 45px; font-weight: bold; font-family: \"Times New Roman\";'>tgh</span><br>"
                + "<span style='font-size: 13px; font-family: Verdana;'>PROCESS SOLUTIONS (P) LTD</span><br>"
                + "<span style='font-size: 13px; font-family: Verdana;'>SIMPLIFYING PROCESSES</span>"
                + "</div></html>";

        JLabel headerTextLabel = new JLabel(headerText);
        headerTextLabel.setFont(new Font("Arial", Font.PLAIN, 10)); // Very small font size

        // Layout for the topPanel
        JPanel topLeftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topLeftPanel.add(companyLogoLabel); // Logo on the left

        JPanel topCenterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        topCenterPanel.add(headerTextLabel); // Centered header text

        JPanel topRightPanel = new JPanel();
        topRightPanel.setLayout(new BoxLayout(topRightPanel, BoxLayout.Y_AXIS));
        topRightPanel.add(addressField); // Address on the right
        topRightPanel.add(Box.createVerticalStrut(5)); // Space between address and phone
        topRightPanel.add(phoneField); // Phone on the right

        topPanel.add(topLeftPanel, BorderLayout.WEST); // Align left panel to the left
        topPanel.add(topCenterPanel, BorderLayout.CENTER); // Center the header text
        topPanel.add(topRightPanel, BorderLayout.EAST); // Align right panel to the right

        // Adding the app name at the bottom
        JPanel appNamePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
//        appNamePanel.add(appNameLabel);
        topPanel.add(appNamePanel, BorderLayout.SOUTH);

        return topPanel;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setPreferredSize(new Dimension(700, 50));
        footerPanel.setBackground(Color.CYAN);
        footerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel footerLabel = new JLabel("© tghApp Registration");
        footerLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        footerLabel.setForeground(Color.GRAY);
        footerPanel.add(footerLabel);

        return footerPanel;
    }

    public static void main(String[] args) {
        AdminLogin dialog = new AdminLogin();
        dialog.setVisible(true);
    }
}
