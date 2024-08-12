package ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import java.io.FileWriter;
import javax.swing.table.TableModel;
import com.opencsv.CSVWriter;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;


public class FrontPage {
    private JFrame frame;
    private JLabel companyLogoLabel;
    private JLabel companyNameField;
    private JLabel addressField;
    private JLabel phoneField;
    private JLabel appNameLabel;
    private JButton addRecordButton;
    private JButton addReceiptButton;
    private JButton viewDataButton;
    private JComboBox<String> reportComboBox;
    private JLabel footerLabel;

    private static final String TABLE_NAME = "tghapp";

    public class DesignUtils {
        // Font styles
        public static final Font headerFont = new Font("Verdana", Font.BOLD, 24);
        public static final Font labelFont = new Font("Verdana", Font.BOLD, 18);
        public static final Font inputFont = new Font("Verdana", Font.PLAIN, 18);
        public static final Font buttonFont = new Font("Verdana", Font.BOLD, 18);

        // Button styles
        public static final Color buttonBackgroundColor = new Color(70, 130, 180);
        public static final Color buttonForegroundColor = Color.WHITE;
        public static final Dimension buttonSize = new Dimension(100, 40);

        // Input field styles
        public static final Dimension passwordFieldSize = new Dimension(250, 40);
        public static final Color borderColor = new Color(70, 130, 180);
        public static Font getCustomFont() {
            // You can customize this font as needed
            return new Font("Verdana", Font.PLAIN, 14);
        }
        // Create a styled button
        public static JButton createStyledButton(String text) {
            JButton button = new JButton(text);
            button.setPreferredSize(buttonSize);
            button.setFont(buttonFont);
            button.setBackground(buttonBackgroundColor);
            button.setForeground(buttonForegroundColor);
            button.setFocusable(false);
            button.setBorder(BorderFactory.createLineBorder(borderColor, 1));
            return button;
        }
        public static void applyCustomStyles(JComponent component) {
            component.setFont(getCustomFont());
            component.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        }

        // Create a border for input fields
        public static Border createBorder() {
            return BorderFactory.createLineBorder(borderColor, 1);
        }
    }
    
    public FrontPage() {
        createGUI();
    }

    private void createGUI() {
    	frame = new JFrame("TGH");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Full screen mode
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(Color.WHITE);

        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel centerPanel = new JPanel();
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        topPanel.setPreferredSize(new Dimension(700, 150));
        centerPanel.setPreferredSize(new Dimension(700, 250));
        bottomPanel.setPreferredSize(new Dimension(700, 100));
        footerPanel.setPreferredSize(new Dimension(700, 50));

        // Set colors
        topPanel.setBackground(Color.CYAN);
        centerPanel.setBackground(Color.WHITE);
        bottomPanel.setBackground(Color.CYAN);
        footerPanel.setBackground(Color.CYAN);
        frame.getContentPane().setBackground(Color.WHITE);

        topPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        centerPanel.setBorder(new EmptyBorder(150, 10, 10, 10));
        bottomPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        footerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Load the image
        ImageIcon logoIcon = null;
        try {
            logoIcon = new ImageIcon(getClass().getResource("tghlogo.png")); // Changed to tghlogo.png
        } catch (Exception e) {
            System.out.println("Image not found.");
        }

        companyLogoLabel = new JLabel();
        if (logoIcon != null) {
            Image img = logoIcon.getImage();
            Image newImg = img.getScaledInstance(200, 130, Image.SCALE_SMOOTH);
            companyLogoLabel.setIcon(new ImageIcon(newImg));
        } else {
            companyLogoLabel.setText("Logo not found");
        }
//        companyLogoLabel.setFont(new Font("Arial", Font.BOLD, 30));

        companyNameField = new JLabel("tgh");
        companyNameField.setFont(new Font("Arial", Font.BOLD, 24));
        
        addressField = new JLabel("<html>#901, Ishaan Arcade-II, Opp. Hanumanji Temple,<br>Gokhale Road, Naupada,<br>Thane(West)-400 602.</html>");
        addressField.setFont(new Font("Segoe UI", Font.PLAIN, 16));  // Using a modern font like Segoe UI
        addressField.setForeground(new Color(51, 51, 51));  // Dark grey color for text

        phoneField = new JLabel("<html>Tel. : +91 25344000 / 67922440 <br>E-mail: ganesh.deshpande@tgh.in <br>www.tgh.in </html>");
        phoneField.setFont(new Font("Segoe UI", Font.PLAIN, 16));  // Consistent font style and size
        phoneField.setForeground(new Color(51, 51, 51));  // Same text color for consistency

        
        appNameLabel = new JLabel("Lendingkart Program");
        appNameLabel.setFont(new Font("Verdana", Font.BOLD, 40));
        appNameLabel.setForeground(new Color(70, 130, 180));

     // Decorate buttons
        addRecordButton = new JButton("Add New Records");
        addRecordButton.setPreferredSize(new Dimension(200, 50));
        addRecordButton.setFont(new Font("Arial", Font.PLAIN, 18));
        addRecordButton.setBackground(new Color(70, 130, 180));
        addRecordButton.setForeground(Color.WHITE);
        addRecordButton.setFocusable(false); // Make the button unfocusable

        addReceiptButton = new JButton("Add Receipt");
        addReceiptButton.setPreferredSize(new Dimension(200, 50));
        addReceiptButton.setFont(new Font("Arial", Font.PLAIN, 18));
        addReceiptButton.setBackground(new Color(70, 130, 180));
        addReceiptButton.setForeground(Color.WHITE);
        addReceiptButton.setFocusable(false); // Make the button unfocusable

        viewDataButton = new JButton("Report");
        viewDataButton.setPreferredSize(new Dimension(200, 50));
        viewDataButton.setFont(new Font("Arial", Font.PLAIN, 18));
        viewDataButton.setBackground(new Color(70, 130, 180));
        viewDataButton.setForeground(Color.WHITE);
        viewDataButton.setFocusable(false); // Make the button unfocusable


        // Create the dropdown menu for the viewDataButton
        JPopupMenu reportMenu = new JPopupMenu();
        JMenuItem codeItem = new JMenuItem("Code");
        JMenuItem partnerItem = new JMenuItem("Partner");
        JMenuItem allItem = new JMenuItem("All");
        JMenuItem dpdItem = new JMenuItem("DPD");
        
     // Style dropdown menu items to match the viewDataButton
        codeItem.setPreferredSize(new Dimension(200, 50));
        partnerItem.setPreferredSize(new Dimension(200, 50));
        allItem.setPreferredSize(new Dimension(200, 50));
        dpdItem.setPreferredSize(new Dimension(200, 50));

        codeItem.setFont(new Font("Arial", Font.PLAIN, 18));
        partnerItem.setFont(new Font("Arial", Font.PLAIN, 18));
        allItem.setFont(new Font("Arial", Font.PLAIN, 18));
        dpdItem.setFont(new Font("Arial", Font.PLAIN, 18));


        // Add action listeners for each menu item
        codeItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Create a dialog to ask for the prospect code
                JDialog dialog = new JDialog();
                dialog.setTitle("Enter Prospect Code");
                dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

                // Create a panel to hold the input field and button
                JPanel panel = new JPanel();
                panel.setLayout(new FlowLayout());

                // Create a label and input field for the prospect code
                JLabel label = new JLabel("Enter Prospect Code:");
                label.setFont(DesignUtils.labelFont);

                JTextField inputField = new JTextField(20);
                inputField.setFont(DesignUtils.inputFont);
                inputField.setPreferredSize(DesignUtils.passwordFieldSize); // Using the same size as password field
                inputField.setBorder(DesignUtils.createBorder());

                // Create a button to submit the input
                JButton submitButton = DesignUtils.createStyledButton("Submit");

                // Add the components to the panel
                panel.add(label);
                panel.add(inputField);
                panel.add(submitButton);

                // Add the panel to the dialog
                dialog.add(panel);

                // Set the dialog size and location
                dialog.setSize(500, 120); // Increased size for better visibility
                dialog.setLocationRelativeTo(null);

                // Show the dialog
                dialog.setVisible(true);

                // Add an action listener to the submit button
                submitButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // Get the input prospect code
                        String prospectCode = inputField.getText();

                        // Create a new table model to store the filtered data
                        DefaultTableModel filteredTableModel = new DefaultTableModel();

                        // Create a new connection to the database
                        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/excel_import", "root", "TGH@2024")) {
                            // Create a statement to execute the query
                            Statement stmt = conn.createStatement();

                            // Create a query to filter the data by prospect code
                            String query = "SELECT * FROM tghapp WHERE Prospect_code = '" + prospectCode + "'";

                            // Execute the query
                            ResultSet rs = stmt.executeQuery(query);

                            // Get the metadata of the result set
                            ResultSetMetaData metaData = rs.getMetaData();
                            int columnCount = metaData.getColumnCount();

                            // Add columns to the filtered table model
                            for (int i = 1; i <= columnCount; i++) {
                                filteredTableModel.addColumn(metaData.getColumnName(i));
                            }

                            // Add rows to the filtered table model
                            while (rs.next()) {
                                Object[] row = new Object[columnCount];
                                for (int i = 1; i <= columnCount; i++) {
                                    row[i - 1] = rs.getObject(i);
                                }
                                filteredTableModel.addRow(row);
                            }

                            // Create a new JTable with the filtered table model
                            JTable filteredTable = new JTable(filteredTableModel) {
                                @Override
                                public boolean isCellEditable(int row, int column) {
                                    return false; // Disable cell editing
                                }

                                @Override
                                public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                                    Component c = super.prepareRenderer(renderer, row, column);
                                    if (isCellSelected(row, column)) {
                                        c.setBackground(new java.awt.Color(184, 207, 229));
                                    } else if (row % 2 == 0) {
                                        c.setBackground(new java.awt.Color(240, 240, 240));
                                    } else {
                                        c.setBackground(java.awt.Color.WHITE);
                                    }
                                    return c;
                                }
                            };

                            // Apply custom font and row height
                            filteredTable.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 14));
                            filteredTable.setRowHeight(25);

                            // Enable column reordering
                            filteredTable.getTableHeader().setReorderingAllowed(true);

                            // Enable column sorting
                            filteredTable.setAutoCreateRowSorter(true);

                            filteredTable.setCellSelectionEnabled(true); // Enable cell selection
                            filteredTable.setDefaultEditor(Object.class, null); // Ensure no cell editor is used
                            filteredTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

                            for (int i = 0; i < filteredTable.getColumnCount(); i++) {
                                TableColumn column = filteredTable.getColumnModel().getColumn(i);
                                int preferredWidth = getColumnPreferredWidth(filteredTable, i);
                                column.setPreferredWidth(preferredWidth);
                            }

                            // Create a new scroll pane to hold the filtered table
                            JScrollPane filteredScrollPane = new JScrollPane(filteredTable);
                            filteredScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
                            filteredScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

                            // Create a new panel for the download button
                            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                            JButton downloadButton = DesignUtils.createStyledButton("Download");
                            buttonPanel.add(downloadButton);

                            // Create a new dialog to display the filtered table and download button
                            JDialog filteredDialog = new JDialog();
                            filteredDialog.setTitle("Filtered Table");
                            filteredDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                            filteredDialog.setSize(800, 600);
                            filteredDialog.setLayout(new BorderLayout());

                            filteredDialog.add(filteredScrollPane, BorderLayout.CENTER);
                            filteredDialog.add(buttonPanel, BorderLayout.NORTH);
                            filteredDialog.setLocationRelativeTo(null);
                            filteredDialog.setVisible(true);

//                             Add action listener to the download button
                             downloadButton.addActionListener(new DownloadButtonListener(filteredDialog, filteredTable));

                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(null, "Error connecting to the database: " + ex.getMessage());
                        }
                    }
                });
            }

            private int getColumnPreferredWidth(JTable table, int colIndex) {
                int maxWidth = 1000;
                int width = 100;
                for (int row = 0; row < table.getRowCount(); row++) {
                    TableCellRenderer renderer = table.getCellRenderer(row, colIndex);
                    Component comp = table.prepareRenderer(renderer, row, colIndex);
                    width = Math.max(comp.getPreferredSize().width + 1, width);
                }
                return Math.min(width, maxWidth);
            }
        });

        //partner
        partnerItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Create a dialog to ask for the Partner Name
                JDialog dialog = new JDialog();
                dialog.setTitle("Enter Partner Name");
                dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

                // Create a panel to hold the input field and button
                JPanel panel = new JPanel();
                panel.setLayout(new FlowLayout());

                // Create a label and input field for the Partner Name
                JLabel label = new JLabel("Enter Partner Name:");
                label.setFont(DesignUtils.labelFont);

                JTextField inputField = new JTextField(20);
                inputField.setFont(DesignUtils.inputFont);
                inputField.setPreferredSize(DesignUtils.passwordFieldSize); // Using the same size as password field
                inputField.setBorder(DesignUtils.createBorder());

                // Create a button to submit the input
                JButton submitButton = DesignUtils.createStyledButton("Submit");

                // Add the components to the panel
                panel.add(label);
                panel.add(inputField);
                panel.add(submitButton);

                // Add the panel to the dialog
                dialog.add(panel);

                // Set the dialog size and location
                dialog.setSize(500, 120); // Increased size for better visibility
                dialog.setLocationRelativeTo(null);

                // Show the dialog
                dialog.setVisible(true);

                // Add an action listener to the submit button
                submitButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // Get the input Partner Name
                        String partnerName = inputField.getText();

                        // Create a new table model to store the filtered data
                        DefaultTableModel filteredTableModel = new DefaultTableModel();

                        // Create a new connection to the database
                        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/excel_import", "root", "TGH@2024")) {
                            // Create a statement to execute the query
                            Statement stmt = conn.createStatement();

                            // Create a query to filter the data by Partner Name
                            String query = "SELECT * FROM tghapp WHERE Partner_Name = '" + partnerName + "'";

                            // Execute the query
                            ResultSet rs = stmt.executeQuery(query);

                            // Get the metadata of the result set
                            ResultSetMetaData metaData = rs.getMetaData();
                            int columnCount = metaData.getColumnCount();

                            // Add columns to the filtered table model
                            for (int i = 1; i <= columnCount; i++) {
                                filteredTableModel.addColumn(metaData.getColumnName(i));
                            }

                            // Add rows to the filtered table model
                            while (rs.next()) {
                                Object[] row = new Object[columnCount];
                                for (int i = 1; i <= columnCount; i++) {
                                    row[i - 1] = rs.getObject(i);
                                }
                                filteredTableModel.addRow(row);
                            }

                            // Create a new JTable with the filtered table model
                            JTable filteredTable = new JTable(filteredTableModel) {
                                @Override
                                public boolean isCellEditable(int row, int column) {
                                    return false; // Disable cell editing
                                }

                                @Override
                                public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                                    Component c = super.prepareRenderer(renderer, row, column);
                                    if (isCellSelected(row, column)) {
                                        c.setBackground(new java.awt.Color(184, 207, 229));
                                    } else if (row % 2 == 0) {
                                        c.setBackground(new java.awt.Color(240, 240, 240));
                                    } else {
                                        c.setBackground(java.awt.Color.WHITE);
                                    }
                                    return c;
                                }
                            };

                            // Apply custom font and row height
                            filteredTable.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 14));
                            filteredTable.setRowHeight(25);

                            // Enable column reordering
                            filteredTable.getTableHeader().setReorderingAllowed(true);

                            // Enable column sorting
                            filteredTable.setAutoCreateRowSorter(true);

                            filteredTable.setCellSelectionEnabled(true); // Enable cell selection
                            filteredTable.setDefaultEditor(Object.class, null); // Ensure no cell editor is used
                            filteredTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

                            for (int i = 0; i < filteredTable.getColumnCount(); i++) {
                                TableColumn column = filteredTable.getColumnModel().getColumn(i);
                                int preferredWidth = getColumnPreferredWidth(filteredTable, i);
                                column.setPreferredWidth(preferredWidth);
                            }

                            // Create a new scroll pane to hold the filtered table
                            JScrollPane filteredScrollPane = new JScrollPane(filteredTable);
                            filteredScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
                            filteredScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

                            // Create a new panel for the download button
                            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                            JButton downloadButton = DesignUtils.createStyledButton("Download");
                            buttonPanel.add(downloadButton);

                            // Create a new dialog to display the filtered table and download button
                            JDialog filteredDialog = new JDialog();
                            filteredDialog.setTitle("Filtered Table");
                            filteredDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                            filteredDialog.setSize(800, 600);
                            filteredDialog.setLayout(new BorderLayout());

                            filteredDialog.add(filteredScrollPane, BorderLayout.CENTER);
                            filteredDialog.add(buttonPanel, BorderLayout.NORTH);
                            filteredDialog.setLocationRelativeTo(null);
                            filteredDialog.setVisible(true);

                            // Add action listener to the download button
                             downloadButton.addActionListener(new DownloadButtonListener(filteredDialog, filteredTable));

                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(null, "Error connecting to the database: " + ex.getMessage());
                        }
                    }
                });
            }

            private int getColumnPreferredWidth(JTable table, int colIndex) {
                int maxWidth = 1000;
                int width = 100;
                for (int row = 0; row < table.getRowCount(); row++) {
                    TableCellRenderer renderer = table.getCellRenderer(row, colIndex);
                    Component comp = table.prepareRenderer(renderer, row, colIndex);
                    width = Math.max(comp.getPreferredSize().width + 1, width);
                }
                return Math.min(width, maxWidth);
            }
        });

        allItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/excel_import", "root", "TGH@2024");
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT * FROM tghapp");

                    // Create a table model to store the data
                    DefaultTableModel tableModel = new DefaultTableModel();
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    for (int i = 1; i <= columnCount; i++) {
                        tableModel.addColumn(metaData.getColumnName(i));
                    }

                    // Add rows to the table model
                    while (rs.next()) {
                        Object[] row = new Object[columnCount];
                        for (int i = 1; i <= columnCount; i++) {
                            row[i - 1] = rs.getObject(i);
                        }
                        tableModel.addRow(row);
                    }

                    // Create a JTable with the table model
                    JTable table = new JTable(tableModel) {
                        @Override
                        public boolean isCellEditable(int row, int column) {
                            return false; // Disable cell editing
                        }

                        @Override
                        public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                            Component c = super.prepareRenderer(renderer, row, column);
                            if (isCellSelected(row, column)) {
                                c.setBackground(new java.awt.Color(184, 207, 229));
                            } else if (row % 2 == 0) {
                                c.setBackground(new java.awt.Color(240, 240, 240));
                            } else {
                                c.setBackground(java.awt.Color.WHITE);
                            }
                            return c;
                        }
                    };

                    // Apply custom font and row height
                    table.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 14));
                    table.setRowHeight(25);

                    // Enable column reordering
                    table.getTableHeader().setReorderingAllowed(true);

                    // Enable column sorting
                    table.setAutoCreateRowSorter(true);

                    table.setCellSelectionEnabled(true); // Enable cell selection
                    table.setDefaultEditor(Object.class, null); // Ensure no cell editor is used
                    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

                    for (int i = 0; i < table.getColumnCount(); i++) {
                        TableColumn column = table.getColumnModel().getColumn(i);
                        int preferredWidth = getColumnPreferredWidth(table, i);
                        column.setPreferredWidth(preferredWidth);
                    }

                    // Create a scroll pane to hold the JTable
                    JScrollPane scrollPane = new JScrollPane(table);
                    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
                    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

                    // Create a panel for the download button
                    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                    JButton downloadButton = new JButton("Download");
                    buttonPanel.add(downloadButton);

                    // Create a dialog to display the table and download button
                    JDialog dialog = new JDialog();
                    dialog.setTitle("tghapp Table");
                    dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                    dialog.setSize(800, 600);
                    dialog.setLayout(new BorderLayout());

                    dialog.add(scrollPane, BorderLayout.CENTER);
                    dialog.add(buttonPanel, BorderLayout.NORTH);
                    dialog.setLocationRelativeTo(null);
                    dialog.setVisible(true);

                    // Add action listener to the download button
                    downloadButton.addActionListener(new DownloadButtonListener(dialog, table));

                    // Close the database connection
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error connecting to the database: " + ex.getMessage());
                }
            }

            private int getColumnPreferredWidth(JTable table, int colIndex) {
                int maxWidth = 1000;
                int width = 100;
                for (int row = 0; row < table.getRowCount(); row++) {
                    TableCellRenderer renderer = table.getCellRenderer(row, colIndex);
                    Component comp = table.prepareRenderer(renderer, row, colIndex);
                    width = Math.max(comp.getPreferredSize().width + 1, width);
                }
                return Math.min(width, maxWidth);
            }
        });
      
        dpdItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Create a new table model to store the filtered data
                DefaultTableModel dpdTableModel = new DefaultTableModel();

                // Add columns to the dpd table model
                dpdTableModel.addColumn("Prospect_Code");
                dpdTableModel.addColumn("Customer_Name");
                dpdTableModel.addColumn("Partner_Name");
                dpdTableModel.addColumn("Closing_Balance_of_Principal_O_S");
                dpdTableModel.addColumn("EMI_No");
                dpdTableModel.addColumn("DPD");

                // Create a new connection to the database
                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/excel_import", "root", "TGH@2024")) {
                    // Create a statement to execute the query
                    Statement stmt = conn.createStatement();

                    // Create the refined query
                    String query = "WITH MaxEMIRows AS (" +
                            "    SELECT " +
                            "        id, " +
                            "        Prospect_Code, " +
                            "        Partner_Name, " +
                            "        Customer_Name, " +
                            "        Closing_Balance_of_Principal_O_S, " +
                            "        EMI_No, " +
                            "        DPD, " +
                            "        ROW_NUMBER() OVER (PARTITION BY Prospect_Code ORDER BY EMI_No DESC) AS rn " +
                            "    FROM tghapp " +
                            "), " +
                            "RankedRows AS (" +
                            "    SELECT " +
                            "        id, " +
                            "        Prospect_Code, " +
                            "        Partner_Name, " +
                            "        Customer_Name, " +
                            "        Closing_Balance_of_Principal_O_S, " +
                            "        EMI_No, " +
                            "        DPD, " +
                            "        MAX(EMI_No) OVER (PARTITION BY Prospect_Code) " +
                            "    FROM MaxEMIRows " +
                            "    WHERE rn = 1 " +
                            ") " +
                            "SELECT " +
                            "    id, " +
                            "    Prospect_Code, " +
                            "    Partner_Name, " +
                            "    Customer_Name, " +
                            "    Closing_Balance_of_Principal_O_S, " +
                            "    EMI_No, " +
                            "    DPD " +
                            "FROM RankedRows " +
                            "ORDER BY id;";
                    // Execute the query
                    ResultSet rs = stmt.executeQuery(query);

                    // Add rows to the dpd table model
                    while (rs.next()) {
                        Object[] row = new Object[6];
                        row[0] = rs.getObject("Prospect_Code");
                        row[1] = rs.getObject("Customer_Name");
                        row[2] = rs.getObject("Partner_Name");
                        row[3] = rs.getObject("Closing_Balance_of_Principal_O_S");
                        row[4] = rs.getObject("EMI_No");
                        row[5] = rs.getObject("DPD");
                        dpdTableModel.addRow(row);
                    }

                    // Create a new JTable with the dpd table model
                    JTable dpdTable = new JTable(dpdTableModel) {
                        @Override
                        public boolean isCellEditable(int row, int column) {
                            return false; // Disable cell editing
                        }

                        @Override
                        public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                            Component c = super.prepareRenderer(renderer, row, column);
                            if (isCellSelected(row, column)) {
                                c.setBackground(new java.awt.Color(184, 207, 229));
                            } else if (row % 2 == 0) {
                                c.setBackground(new java.awt.Color(240, 240, 240));
                            } else {
                                c.setBackground(java.awt.Color.WHITE);
                            }
                            return c;
                        }
                    };

                    // Apply custom font and row height
                    dpdTable.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 14));
                    dpdTable.setRowHeight(25);

                    // Enable column reordering
                    dpdTable.getTableHeader().setReorderingAllowed(true);

                    // Enable column sorting
                    dpdTable.setAutoCreateRowSorter(true);

                    dpdTable.setCellSelectionEnabled(true); // Enable cell selection
                    dpdTable.setDefaultEditor(Object.class, null); // Ensure no cell editor is used
                    dpdTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

                    for (int i = 0; i < dpdTable.getColumnCount(); i++) {
                        TableColumn column = dpdTable.getColumnModel().getColumn(i);
                        int preferredWidth = getColumnPreferredWidth(dpdTable, i);
                        column.setPreferredWidth(preferredWidth);
                    }

                    // Create a new scroll pane to hold the dpd table
                    JScrollPane dpdScrollPane = new JScrollPane(dpdTable);
                    dpdScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
                    dpdScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

                    // Create a panel for the buttons
                    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                    JButton downloadButton = new JButton("Download");
                    JButton uploadButton = new JButton("Upload File");
                    JButton compareButton = new JButton("Compare");

                    buttonPanel.add(downloadButton);
                    buttonPanel.add(uploadButton);
                    buttonPanel.add(compareButton);

                    // Create a new dialog to display the dpd table and buttons
                    JDialog dpdDialog = new JDialog();
                    dpdDialog.setTitle("DPD Table");
                    dpdDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                    dpdDialog.setSize(800, 600);
                    dpdDialog.setLayout(new BorderLayout());

                    dpdDialog.add(dpdScrollPane, BorderLayout.CENTER);
                    dpdDialog.add(buttonPanel, BorderLayout.NORTH);
                    dpdDialog.setLocationRelativeTo(null);
                    dpdDialog.setVisible(true);

                    // Add action listener to the download button
                    downloadButton.addActionListener(new DownloadButtonListener(dpdDialog, dpdTable));

                    // Add action listener to the upload button
                    uploadButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            // Handle upload file action
                            JFileChooser fileChooser = new JFileChooser();
                            fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                                public boolean accept(File f) {
                                    return f.getName().toLowerCase().endsWith(".csv");
                                }

                                public String getDescription() {
                                    return "CSV files (*.csv)";
                                }
                            });
                            int returnValue = fileChooser.showOpenDialog(null);
                            if (returnValue == JFileChooser.APPROVE_OPTION) {
                                File selectedFile = fileChooser.getSelectedFile();
                                // Show a dialog with the file path
                                int confirm = JOptionPane.showConfirmDialog(dpdDialog, "Do you want to upload the file: " + selectedFile.getAbsolutePath() + "?", "Confirm Upload", JOptionPane.YES_NO_OPTION);
                                if (confirm == JOptionPane.YES_OPTION) {
                                    uploadCSVToDatabase(selectedFile);
                                }
                            }
                        }

                        private void uploadCSVToDatabase(File csvFile) {
                            String jdbcUrl = "jdbc:mysql://localhost:3306/excel_import";
                            String username = "root";
                            String password = "TGH@2024";
                            String tableName = "campare_data";

                            try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password)) {
                                // Clear existing data from the table
                                Statement stmt = conn.createStatement();
                                stmt.executeUpdate("TRUNCATE TABLE " + tableName);

                                String line;
                                try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
                                    String[] headers = br.readLine().split(",");
                                    String insertSQL = "INSERT INTO " + tableName + " (" + String.join(", ", headers) + ") VALUES (";

                                    StringBuilder placeholderBuilder = new StringBuilder();
                                    for (int i = 0; i < headers.length; i++) {
                                        placeholderBuilder.append("?");
                                        if (i < headers.length - 1) {
                                            placeholderBuilder.append(", ");
                                        }
                                    }
                                    insertSQL += placeholderBuilder.toString() + ")";

                                    PreparedStatement pstmt = conn.prepareStatement(insertSQL);
                                    while ((line = br.readLine()) != null) {
                                        String[] values = line.split(",");
                                        for (int i = 0; i < values.length; i++) {
                                            pstmt.setString(i + 1, values[i]);
                                        }
                                        pstmt.addBatch();
                                    }
                                    pstmt.executeBatch();
                                }
                                JOptionPane.showMessageDialog(dpdDialog, "File uploaded successfully.");
                            } catch (SQLException | IOException ex) {
                                JOptionPane.showMessageDialog(dpdDialog, "Error uploading file: " + ex.getMessage());
                            }
                        }
                    });

                    // Add action listener to the compare button (if needed)
                    compareButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            // Invoke Compare class
                            new Compare().setVisible(true);
                        }
                    });

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error connecting to the database: " + ex.getMessage());
                }
            }

            private int getColumnPreferredWidth(JTable table, int colIndex) {
                int maxWidth = 1000;
                int width = 100;
                for (int row = 0; row < table.getRowCount(); row++) {
                    TableCellRenderer renderer = table.getCellRenderer(row, colIndex);
                    Component comp = table.prepareRenderer(renderer, row, colIndex);
                    width = Math.max(comp.getPreferredSize().width + 1, width);
                }
                return Math.min(width, maxWidth);
            }
        });
        // Add menu items to the dropdown menu
        reportMenu.add(codeItem);
        reportMenu.add(partnerItem);
        reportMenu.add(allItem);
        reportMenu.add(dpdItem);

        // Add action listener to the button to show the dropdown menu
        viewDataButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reportMenu.show(viewDataButton, 0, viewDataButton.getHeight());
            }
        });

        // Action listener to run ExcelApp when button is clicked
        addRecordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Start ExcelApp
                ExcelApp.main(new String[]{}); // Assuming ExcelApp has a main method
            }
        });
        
        addReceiptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Create a dialog to ask for the file path and provide browse and append options
                JDialog dialog = new JDialog();
                dialog.setTitle("Add Receipt");
                dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

                // Create a panel to hold the input field, browse button, and append button
                JPanel panel = new JPanel();
                panel.setLayout(new FlowLayout());

                // Create a label and input field for the file path
                JLabel label = new JLabel("File Path:");
                JTextField filePathField = new JTextField(20);

                // Apply styles
                label.setFont(DesignUtils.getCustomFont());
                filePathField.setPreferredSize(new Dimension(250, 30));
                filePathField.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

                // Create a browse button
                JButton browseButton = DesignUtils.createStyledButton("Browse");

                // Add action listener to the browse button
                browseButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JFileChooser fileChooser = new JFileChooser();
                        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                            public boolean accept(File f) {
                                return f.getName().toLowerCase().endsWith(".csv") ||
                                        f.getName().toLowerCase().endsWith(".xls") ||
                                        f.getName().toLowerCase().endsWith(".xlsx") ||
                                        f.getName().toLowerCase().endsWith(".xlsb") ||
                                        f.getName().toLowerCase().endsWith(".xlsm");
                            }

                            public String getDescription() {
                                return "Excel files (*.csv, *.xls, *.xlsx, *.xlsb, *.xlsm)";
                            }
                        });
                        int returnValue = fileChooser.showOpenDialog(null);
                        if (returnValue == JFileChooser.APPROVE_OPTION) {
                            File selectedFile = fileChooser.getSelectedFile();
                            filePathField.setText(selectedFile.getAbsolutePath());
                        }
                    }
                });

                // Create an append button
                JButton appendButton = DesignUtils.createStyledButton("Append");

                // Add action listener to the append button
                appendButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String filePath = filePathField.getText();
                        File file = new File(filePath);
                        Connection conn = null;
                        try {
                            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/excel_import", "root", "TGH@2024");
                            compareAndAppendData(conn, file);
                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(null, "Error connecting to database: " + ex.getMessage());
                        } catch (CsvValidationException | IOException e1) {
                            e1.printStackTrace();
                        } finally {
                            if (conn != null) {
                                try {
                                    conn.close();
                                } catch (SQLException ex) {
                                    JOptionPane.showMessageDialog(null, "Error closing database connection: " + ex.getMessage());
                                }
                            }
                        }
                        dialog.dispose(); // Close the dialog after appending
                    }

                    private void compareAndAppendData(Connection conn, File file) throws IOException, CsvValidationException, SQLException {
                        ArrayList<String[]> newRows = new ArrayList<>();
                        ArrayList<String[]> duplicateRows = new ArrayList<>();

                        try (CSVReader reader = new CSVReader(new FileReader(file))) {
                            String[] headers = reader.readNext();
                            int idIndex = Arrays.asList(headers).indexOf("id");
                            int srNoIndex = Arrays.asList(headers).indexOf("Sr_No");

                            if (idIndex == -1 || srNoIndex == -1) {
                                throw new IllegalArgumentException("CSV does not contain required columns: id and Sr_No");
                            }

                            ArrayList<String[]> existingRows = getExistingRows(conn);

                            String[] row;
                            while ((row = reader.readNext()) != null) {
                                String id = row[idIndex];
                                String srNo = row[srNoIndex];
                                boolean isDuplicate = false;

                                for (String[] existingRow : existingRows) {
                                    if (id.equals(existingRow[idIndex]) && srNo.equals(existingRow[srNoIndex])) {
                                        isDuplicate = true;
                                        break;
                                    }
                                }

                                if (isDuplicate) {
                                    duplicateRows.add(row);
                                } else {
                                    newRows.add(row);
                                }
                            }

                            if (!duplicateRows.isEmpty()) {
                                StringBuilder duplicates = new StringBuilder("Duplicate rows found:\n");
                                for (String[] duplicateRow : duplicateRows) {
                                    duplicates.append(Arrays.toString(duplicateRow)).append("\n");
                                }

                                JTextArea textArea = new JTextArea(duplicates.toString());
                                textArea.setEditable(false);
                                JScrollPane scrollPane = new JScrollPane(textArea);
                                scrollPane.setPreferredSize(new Dimension(500, 300));

                                int userChoice = JOptionPane.showOptionDialog(null,
                                        scrollPane,
                                        "Duplicate Rows Found",
                                        JOptionPane.YES_NO_OPTION,
                                        JOptionPane.QUESTION_MESSAGE,
                                        null,
                                        new String[]{"Replace All", "Skip All"},
                                        "Skip All");

                                if (userChoice == JOptionPane.YES_OPTION) {
                                    for (String[] duplicateRow : duplicateRows) {
                                        replaceRowInDatabase(conn, headers, duplicateRow);
                                    }
                                }
                            }

                            for (String[] newRow : newRows) {
                                addRowToDatabase(conn, headers, newRow);
                            }
                        }
                    }

                    private ArrayList<String[]> getExistingRows(Connection conn) throws SQLException {
                        Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery("SELECT id, Sr_No FROM " + TABLE_NAME);
                        ArrayList<String[]> existingRows = new ArrayList<>();

                        while (rs.next()) {
                            String[] row = new String[2];
                            row[0] = rs.getString("id");
                            row[1] = rs.getString("Sr_No");
                            existingRows.add(row);
                        }

                        return existingRows;
                    }

                    private void addRowToDatabase(Connection conn, String[] headers, String[] row) throws SQLException {
                        String insertSQL = "INSERT INTO " + TABLE_NAME + " VALUES (";
                        for (int i = 0; i < headers.length; i++) {
                            insertSQL += "?,";
                        }
                        insertSQL = insertSQL.substring(0, insertSQL.length() - 1) + ")";
                        PreparedStatement pstmt = conn.prepareStatement(insertSQL);
                        for (int i = 0; i < headers.length; i++) {
                            pstmt.setString(i + 1, row[i]);
                        }
                        pstmt.executeUpdate();
                    }

                    private void replaceRowInDatabase(Connection conn, String[] headers, String[] row) throws SQLException {
                        String updateSQL = "UPDATE " + TABLE_NAME + " SET ";
                        for (int i = 0; i < headers.length; i++) {
                            updateSQL += headers[i] + " = ?,";
                        }
                        updateSQL = updateSQL.substring(0, updateSQL.length() - 1) + " WHERE id = ? AND Sr_No = ?;";
                        PreparedStatement pstmt = conn.prepareStatement(updateSQL);
                        for (int i = 0; i < headers.length; i++) {
                            pstmt.setString(i + 1, row[i]); // Set the new values
                        }
                        pstmt.setString(headers.length + 1, row[Arrays.asList(headers).indexOf("id")]); // Set the id for the WHERE clause
                        pstmt.setString(headers.length + 2, row[Arrays.asList(headers).indexOf("Sr_No")]); // Set the SR_No for the WHERE clause
                        pstmt.executeUpdate();
                    }
                });

                // Add the components to the panel
                panel.add(label);
                panel.add(filePathField);
                panel.add(browseButton);
                panel.add(appendButton);

                // Apply styles to the dialog
                dialog.setFont(DesignUtils.getCustomFont());
                dialog.setSize(700, 100);
                dialog.setLocationRelativeTo(null);

                // Add the panel to the dialog
                dialog.add(panel);

                // Show the dialog
                dialog.setVisible(true);
            }
        });

        String[] reports = {"Code", "Partner", "All", "DPD"};
        reportComboBox = new JComboBox<>(reports);

        footerLabel = new JLabel("© tghApp Registration");
        footerLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        footerLabel.setForeground(Color.GRAY);
        
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
        topRightPanel.add(phoneField); // Phone below address

        topPanel.add(topLeftPanel, BorderLayout.WEST);
        topPanel.add(topCenterPanel, BorderLayout.CENTER);
        topPanel.add(topRightPanel, BorderLayout.EAST);

        // Layout for the centerPanel
        centerPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0; // Center the appNameLabel in the first row
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0; // No vertical stretching
        centerPanel.add(appNameLabel, gbc);

        gbc.gridy = 1; // Move buttonPanel to the second row, directly below appNameLabel
        gbc.weighty = 0.1; // No vertical stretching
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(addRecordButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(addReceiptButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(viewDataButton);
        buttonPanel.add(Box.createHorizontalGlue());
        centerPanel.add(buttonPanel, gbc);

        bottomPanel.add(reportComboBox);

        footerPanel.add(footerLabel);

        frame.getContentPane().add(topPanel, BorderLayout.NORTH);
        frame.getContentPane().add(centerPanel, BorderLayout.CENTER);
        frame.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
        frame.getContentPane().add(footerPanel, BorderLayout.PAGE_END);
        frame.setLocationRelativeTo(null); // Center the window
        frame.setVisible(true);

        // Apply rounded corners to the frame
        frame.setUndecorated(true);
        frame.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
        frame.getRootPane().setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(70, 130, 180)));
    }

public class DownloadButtonListener implements ActionListener {
    private JDialog frame;
    private JTable table;

    public DownloadButtonListener(JDialog filteredDialog, JTable table) {
        this.frame = filteredDialog;
        this.table = table;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save File");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                return f.getName().toLowerCase().endsWith(".csv") || f.getName().toLowerCase().endsWith(".pdf");
            }

            public String getDescription() {
                return "CSV files (*.csv), PDF files (*.pdf)";
            }
        });

        int userSelection = fileChooser.showSaveDialog(frame);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();

            if (filePath.toLowerCase().endsWith(".csv")) {
                downloadAsCSV(filePath);
            } else if (filePath.toLowerCase().endsWith(".pdf")) {
                downloadAsPDF(filePath);
            } else {
                filePath += ".csv";  // Default to CSV if no extension is provided
                downloadAsCSV(filePath);
            }
        }
    }

    private void downloadAsCSV(String filePath) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            // Write the header row
            TableModel model = table.getModel();
            int columnCount = model.getColumnCount();
            String[] header = new String[columnCount];
            for (int i = 0; i < columnCount; i++) {
                header[i] = model.getColumnName(i);
            }
            writer.writeNext(header);

            // Write the data rows
            int rowCount = model.getRowCount();
            for (int i = 0; i < rowCount; i++) {
                String[] row = new String[columnCount];
                for (int j = 0; j < columnCount; j++) {
                    Object value = model.getValueAt(i, j);
                    row[j] = (value == null) ? "" : value.toString();
                }
                writer.writeNext(row);
            }

            JOptionPane.showMessageDialog(frame, "CSV file downloaded successfully.");
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error writing the file: " + ex.getMessage());
        }
    }

    private void downloadAsPDF(String filePath) {
        Document document = new Document(PageSize.A4.rotate());
        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            // Add the table data to the PDF document
            PdfPTable pdfTable = new PdfPTable(table.getColumnCount());
            pdfTable.setWidthPercentage(100);
            pdfTable.setSpacingBefore(10f);
            pdfTable.setSpacingAfter(10f);

            for (int i = 0; i < table.getColumnCount(); i++) {
                PdfPCell headerCell = new PdfPCell(new Phrase(table.getColumnName(i)));
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                pdfTable.addCell(headerCell);
            }

            // Add table rows
            for (int i = 0; i < table.getRowCount(); i++) {
                for (int j = 0; j < table.getColumnCount(); j++) {
                    Object value = table.getValueAt(i, j);
                    PdfPCell cell = new PdfPCell(new Phrase((value == null) ? "" : value.toString()));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    pdfTable.addCell(cell);
                }
            }

            document.add(pdfTable);
            JOptionPane.showMessageDialog(frame, "PDF file downloaded successfully.");
        } catch (DocumentException | IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error writing the file: " + ex.getMessage());
        } finally {
            document.close();
        }
    }
}

public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
            new FrontPage();
        }
    });
    }
}