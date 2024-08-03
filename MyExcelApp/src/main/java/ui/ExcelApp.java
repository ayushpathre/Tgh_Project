package ui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.*;
import javax.swing.table.*;  
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

public class ExcelApp {     
    private JFrame frame;
    private JTextField filePathField;
    private JButton browseButton;
    private JButton uploadButton;
    private JButton downloadButton;
    private JButton viewDataButton;
    private JButton filterButton; // New button for filter
    private JButton refreshButton;
    private JButton updateAllButton;
    private JTextArea reportArea;
    private JTable table;
    private JTextField dataInput; // New input box for data
    private JButton addButton; // New button to add data
    private static final String DB_URL = "jdbc:mysql://localhost:3306/excel_import";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "TGH@2024";
    private static final String TABLE_NAME = "tghapp";

    public ExcelApp() {
        createGUI();
    }

    private void createGUI() {
        frame = new JFrame("Excel Importer");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel filePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel excelFileLabel = new JLabel("EXCEL FILE:");
        filePathField = new JTextField(20);
        browseButton = new JButton("Browse");
        uploadButton = new JButton("Upload");
        viewDataButton = new JButton("View Data");
        filterButton = new JButton("Filter");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        downloadButton = new JButton("Download");
        refreshButton = new JButton("Refresh");
        updateAllButton = new JButton("Update All");

        // New input box and button
        dataInput = new JTextField(20); // Input box for data
        addButton = new JButton("Add"); // Button to add data
        addButton.addActionListener(new AddButtonListener()); // Add ActionListener

        browseButton.addActionListener(new BrowseButtonListener());
        uploadButton.addActionListener(new UploadButtonListener());
        viewDataButton.addActionListener(new ViewDataButtonListener());
        refreshButton.addActionListener(new RefreshButtonListener());
        downloadButton.addActionListener(new DownloadButtonListener(frame));
        updateAllButton.addActionListener(new UpdateAllButtonListener());
        filterButton.addActionListener(new FilterButtonListener());

        // Create Verify button with dropdown menu
        JButton verifyButton = new JButton("Verify");
        JPopupMenu verifyMenu = new JPopupMenu();
        JMenuItem negativeNoItem = new JMenuItem("Negative_No");
        JMenuItem prospectCodeItem = new JMenuItem("Prospect_code");
        JMenuItem loanTenureItem = new JMenuItem("Loan_Tenuare");

        // Add menu items to the popup menu
        verifyMenu.add(negativeNoItem);
        verifyMenu.add(prospectCodeItem);
        verifyMenu.add(loanTenureItem);

        // Add ActionListeners to menu items
        negativeNoItem.addActionListener(e -> verifyNegativeNo());
        prospectCodeItem.addActionListener(e -> verifyProspectCode());
        loanTenureItem.addActionListener(e -> verifyLoanTenure());

        // Add ActionListener to show the popup menu when the verify button is clicked
        verifyButton.addActionListener(e -> verifyMenu.show(verifyButton, verifyButton.getWidth(), verifyButton.getHeight()));

        // Add components to filePanel and buttonPanel
        filePanel.add(excelFileLabel);
        filePanel.add(filePathField);
        filePanel.add(browseButton);
        filePanel.add(uploadButton);
        filePanel.add(viewDataButton);
        filePanel.add(verifyButton);  // Add Verify button next to View Data button
        buttonPanel.add(filterButton);
        buttonPanel.add(downloadButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(updateAllButton);
        buttonPanel.add(filterButton);
        buttonPanel.add(dataInput);
        buttonPanel.add(addButton);

        topPanel.add(filePanel, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        reportArea = new JTextArea(10, 40);
        reportArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(reportArea);

        // Create footer panel
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel footerLabel = new JLabel("© tghApp");

        footerPanel.add(footerLabel);

        frame.getContentPane().add(topPanel, BorderLayout.NORTH);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        frame.getContentPane().add(footerPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    // Implementations for verification methods
    private void verifyNegativeNo() {
        // Implement verification logic for Negative_No
        try {
            String[] columnsToCheck = {"Principal_Received", "Lendingkart_Share_One", "Partner_share_One", "Bounce_Charges_Received", "Penal_Charges_Received"};
            boolean negativeValueFound = false; // Flag to track if any negative values are found

            for (int row = 0; row < table.getRowCount(); row++) {
                for (String column : columnsToCheck) {
                    int columnIndex = table.getColumnModel().getColumnIndex(column);
                    Object value = table.getValueAt(row, columnIndex);

                    // Debug output: Print the column name, row number, and value
                    System.out.println("Checking column: " + column + ", row: " + row + ", value: " + value);

                    // Check if the value is a number or can be parsed as a number and is less than 0
                    if (isNegative(value)) {
                        String id = table.getModel().getValueAt(row, 0).toString();
                        String srNo = table.getModel().getValueAt(row, 1).toString();
                        String errorMsg = "Negative value found in row with ID: " + id + " and Sr_No: " + srNo + " in column: " + column;

                        // Show the error dialog
                        JOptionPane.showMessageDialog(frame, errorMsg, "Negative Value Error", JOptionPane.ERROR_MESSAGE);
                        negativeValueFound = true; // Set the flag to true
                        break; // Stop checking further columns in the current row
                    }
                }
                if (negativeValueFound) {
                    break; // Stop checking further rows if a negative value has been found
                }
            }

            // If no negative values were found
            if (!negativeValueFound) {
                JOptionPane.showMessageDialog(frame, "No negative values found.");
            }
        } catch (Exception e) {
            // Display an error message if an exception occurs
            JOptionPane.showMessageDialog(frame, "Error during negative value verification: " + e.getMessage(), "Verification Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean isNegative(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue() < 0;
        } else if (value instanceof String) {
            try {
                double numericValue = Double.parseDouble((String) value);
                return numericValue < 0;
            } catch (NumberFormatException e) {
                // Ignore if value is not a valid number
            }
        }
        return false;
    }

    private void verifyProspectCode() {
        // Implement verification logic for Prospect_code
        try {
            Connection conn = connectToDatabase();
            if (conn != null) {
                // Validate Prospect_Code consistency
                boolean isValid = true;
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT id, Prospect_Code FROM " + TABLE_NAME);

                Map<String, Set<Integer>> prospectToIdsMap = new HashMap<>();
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String prospectCode = rs.getString("Prospect_Code");

                    prospectToIdsMap
                            .computeIfAbsent(prospectCode, k -> new HashSet<>())
                            .add(id);
                }

                // Check for any Prospect_Code associated with multiple ids
                for (Map.Entry<String, Set<Integer>> entry : prospectToIdsMap.entrySet()) {
                    if (entry.getValue().size() > 1) {
                        String prospectCode = entry.getKey();
                        Set<Integer> ids = entry.getValue();
                        String errorMsg = "Prospect_Code '" + prospectCode + "' is associated with multiple IDs: " + ids;
                        JOptionPane.showMessageDialog(frame, errorMsg, "Prospect Code Error", JOptionPane.ERROR_MESSAGE);
                        isValid = false;
                        break;
                    }
                }

                if (isValid) {
                    JOptionPane.showMessageDialog(frame, "No issues with Prospect_code found.");
                }

                rs.close();
                stmt.close();
                conn.close();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error during verification: " + e.getMessage(), "Verification Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void verifyLoanTenure() {
        // Implement verification logic for Loan_Tenure
        try {
            Map<String, Map<Integer, Integer>> idToSrNoCounts = new HashMap<>();
            for (int row = 0; row < table.getRowCount(); row++) {
                String id = table.getModel().getValueAt(row, 0).toString();
                int srNo = Integer.parseInt(table.getModel().getValueAt(row, 1).toString());
                int loanTenure = Integer.parseInt(table.getModel().getValueAt(row, table.getColumnModel().getColumnIndex("Loan_Tenure")).toString());

                idToSrNoCounts.putIfAbsent(id, new HashMap<>());
                Map<Integer, Integer> srNoCounts = idToSrNoCounts.get(id);

                srNoCounts.put(srNo, srNoCounts.getOrDefault(srNo, 0) + 1);

                if (srNoCounts.get(srNo) > loanTenure) {
                    JOptionPane.showMessageDialog(frame, "Sr_No exceeds Loan_Tenure in row with ID: " + id, "Sr_No Validation Error", JOptionPane.ERROR_MESSAGE);
                    return; // Stop checking further once an error is found
                }
            }
            JOptionPane.showMessageDialog(frame, "All Sr_No values are within the Loan_Tenure limits.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error during Loan_Tenure verification: " + e.getMessage(), "Verification Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private class FilterButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JDialog filterDialog = new JDialog(frame, "Filter Data", true);
            filterDialog.setLayout(new BorderLayout());
            filterDialog.setSize(500, 300);

            JPanel columnPanel = new JPanel();
            columnPanel.setLayout(new BoxLayout(columnPanel, BoxLayout.Y_AXIS));
            JCheckBox[] columnCheckBoxes = new JCheckBox[table.getColumnCount()];
            for (int i = 0; i < table.getColumnCount(); i++) {
                columnCheckBoxes[i] = new JCheckBox(table.getColumnName(i));
                columnPanel.add(columnCheckBoxes[i]);
            }

            // Add the column panel to a scroll pane
            JScrollPane scrollPane = new JScrollPane(columnPanel);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

            JTextField filterField = new JTextField(20);
            JButton applyFilterButton = new JButton("Apply Filter");

            applyFilterButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ArrayList<String> selectedColumns = new ArrayList<>();
                    for (JCheckBox checkBox : columnCheckBoxes) {
                        if (checkBox.isSelected()) {
                            selectedColumns.add(checkBox.getText());
                        }
                    }
                    String filterValue = filterField.getText().trim();

                    if (!selectedColumns.isEmpty() && !filterValue.isEmpty()) {
                        applyFilter(selectedColumns, filterValue);
                        filterDialog.dispose();
                    } else {
                        JOptionPane.showMessageDialog(frame, "Please select at least one column and enter a filter value.");
                    }
                }
            });

            JPanel filterPanel = new JPanel();
            filterPanel.add(new JLabel("Filter Value:"));
            filterPanel.add(filterField);
            filterPanel.add(applyFilterButton);

            filterDialog.add(scrollPane, BorderLayout.CENTER);
            filterDialog.add(filterPanel, BorderLayout.SOUTH);
            filterDialog.setLocationRelativeTo(frame);
            filterDialog.setVisible(true);
        }

        private void applyFilter(ArrayList<String> selectedColumns, String filterValue) {
            DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
            TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
            table.setRowSorter(sorter);

            RowFilter<DefaultTableModel, Object> rf = new RowFilter<DefaultTableModel, Object>() {
                @Override
                public boolean include(RowFilter.Entry<? extends DefaultTableModel, ? extends Object> entry) {
                    for (String column : selectedColumns) {
                        int columnIndex = tableModel.findColumn(column);
                        if (entry.getStringValue(columnIndex).contains(filterValue)) {
                            return true;
                        }
                    }
                    return false;
                }
            };
            sorter.setRowFilter(rf);
        }
    }

    private class BrowseButtonListener implements ActionListener {
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
            int returnValue = fileChooser.showOpenDialog(frame);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                filePathField.setText(selectedFile.getAbsolutePath());
            }
        }
    }

    private class UploadButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String filePath = filePathField.getText();
            if (filePath != null && !filePath.isEmpty()) {
                try {
                    File file = new File(filePath);
                    if (file.exists() && file.isFile()) {
                        Connection conn = connectToDatabase();
                        if (conn != null) {
                            int userChoice = JOptionPane.showOptionDialog(frame,
                                "Do you want to replace the existing table or append to it?",
                                "Upload Options",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE,
                                null,
                                new String[]{"Replace", "Append"},
                                "Replace");

                            if (userChoice == JOptionPane.YES_OPTION) {
                                // Replace existing table
                                Statement stmt = conn.createStatement();
                                stmt.executeUpdate("TRUNCATE TABLE " + TABLE_NAME);
                                if (filePath.toLowerCase().endsWith(".csv")) {
                                    uploadCSV(file, conn, TABLE_NAME);
                                } else {
                                    importExcelFileWithPOI(file, conn, TABLE_NAME);
                                }
                            } else if (userChoice == JOptionPane.NO_OPTION) {
                                // Append to existing table
                                if (filePath.toLowerCase().endsWith(".csv")) {
                                    compareAndAppendData(conn, file);
                                } else {
                                    importExcelFileWithPOI(file, conn, TABLE_NAME);
                                }
                            }

                            JOptionPane.showMessageDialog(frame, "Data imported successfully!");
                        } else {
                            JOptionPane.showMessageDialog(frame, "Error connecting to database");
                        }
                    } else {
                        JOptionPane.showMessageDialog(frame, "Invalid file path");
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Error importing data: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Please select a file");
            }
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

                    int userChoice = JOptionPane.showOptionDialog(frame,
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
                String value = row[i].trim();
                if (value.isEmpty() && headers[i].equalsIgnoreCase("Received_Date")) {
                    pstmt.setNull(i + 1, java.sql.Types.DATE); // Set null for empty date fields
                } else if (headers[i].equalsIgnoreCase("Received_Date")) {
                    pstmt.setDate(i + 1, java.sql.Date.valueOf(value));
                } else {
                    pstmt.setString(i + 1, value);
                }
            }
            pstmt.setString(headers.length + 1, row[Arrays.asList(headers).indexOf("id")]); // Set the id for the WHERE clause
            pstmt.setString(headers.length + 2, row[Arrays.asList(headers).indexOf("Sr_No")]); // Set the SR_No for the WHERE clause
            pstmt.executeUpdate();
        }
    }

   	public class DownloadButtonListener implements ActionListener {
        private JFrame frame;

        public DownloadButtonListener(JFrame frame) {
            this.frame = frame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save CSV File");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                public boolean accept(File f) {
                    return f.getName().toLowerCase().endsWith(".csv");
                }

                public String getDescription() {
                    return "CSV files (*.csv)";
                }
            });

            int userSelection = fileChooser.showSaveDialog(frame);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                String filePath = fileToSave.getAbsolutePath();
                if (!filePath.toLowerCase().endsWith(".csv")) {
                    filePath += ".csv";
                }

                try {
                    // MySQL connection parameters
                    String url = DB_URL;
                    String username = USERNAME;
                    String password = PASSWORD;

                    // The query to fetch data from the database
                    String query = "SELECT * FROM " + TABLE_NAME;

                    // Establish connection and execute the query
                    try (Connection connection = DriverManager.getConnection(url, username, password);
                         Statement statement = connection.createStatement();
                         ResultSet resultSet = statement.executeQuery(query)) {

                        // Create a CSVWriter to write the data to a CSV file
                        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
                            // Write the header row
                            ResultSetMetaData metaData = resultSet.getMetaData();
                            int columnCount = metaData.getColumnCount();
                            String[] header = new String[columnCount];
                            for (int i = 1; i <= columnCount; i++) {
                                header[i - 1] = metaData.getColumnName(i);
                            }
                            writer.writeNext(header);

                            // Write the data rows
                            while (resultSet.next()) {
                                String[] row = new String[columnCount];
                                for (int i = 1; i <= columnCount; i++) {
                                    Object value = resultSet.getObject(i);
                                    if (value instanceof Date || value instanceof Timestamp) {
                                        row[i - 1] = resultSet.getString(i); // Assuming default date format in SQL
                                    } else if (value instanceof Number) {
                                        row[i - 1] = resultSet.getString(i);
                                    } else {
                                        row[i - 1] = resultSet.getString(i);
                                    }
                                }
                                writer.writeNext(row);
                            }

                            JOptionPane.showMessageDialog(frame, "File downloaded successfully.");
                        } catch (IOException ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(frame, "Error writing the file: " + ex.getMessage());
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(frame, "Error downloading the file: " + ex.getMessage());
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
                }
            }
        }
    }
    
    private class ViewDataButtonListener implements ActionListener {
        @SuppressWarnings("serial")
		@Override
        public void actionPerformed(ActionEvent e) {
            try {
                Connection conn = connectToDatabase();
                if (conn != null) {
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT * FROM " + TABLE_NAME);

                    DefaultTableModel tableModel = new DefaultTableModel();
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    for (int i = 1; i <= columnCount; i++) {
                        tableModel.addColumn(metaData.getColumnName(i));
                    }

                    while (rs.next()) {
                        Object[] row = new Object[columnCount];
                        for (int i = 0; i < columnCount; i++) {
                            row[i] = rs.getObject(i + 1);
                        }
                        tableModel.addRow(row);
                    }

                    table = new JTable(tableModel) {
                        @Override
                        public boolean isCellEditable(int row, int column) {
                            return false; // Disable cell editing
                        }
                    };
                    table.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            int row = table.rowAtPoint(e.getPoint());
                            int column = table.columnAtPoint(e.getPoint());
                            
                            if (row >= 0 && column >= 0) {
                                String cellValue = table.getValueAt(row, column).toString();
                                dataInput.setText(cellValue);
                            }
                        }
                    });

                    table.setCellSelectionEnabled(false); // Disable cell selection
                    table.setDefaultEditor(Object.class, null); // Ensure no cell editor is used
                    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

                    for (int i = 0; i < table.getColumnCount(); i++) {
                        TableColumn column = table.getColumnModel().getColumn(i);
                        int preferredWidth = getColumnPreferredWidth(table, i);
                        column.setPreferredWidth(preferredWidth);
                    }

                    JScrollPane scrollPane = new JScrollPane(table,
                            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                            JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

                    frame.getContentPane().remove(1);
                    frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

                    frame.revalidate();
                    frame.repaint();

                } else {
                    JOptionPane.showMessageDialog(frame, "Error connecting to database");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame, "Error retrieving data: " + ex.getMessage());
            }
        }        
    }

    private class RefreshButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                Connection conn = connectToDatabase();
                if (conn != null) {
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT * FROM " + TABLE_NAME);

                    DefaultTableModel tableModel = new DefaultTableModel();
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    for (int i = 1; i <= columnCount; i++) {
                        tableModel.addColumn(metaData.getColumnName(i));
                    }

                    while (rs.next()) {
                        Object[] row = new Object[columnCount];
                        for (int i = 0; i < columnCount; i++) {
                            row[i] = rs.getObject(i + 1);
                        }
                        tableModel.addRow(row);
                    }

                    table = new JTable(tableModel);
                    table.setCellSelectionEnabled(true);
                    table.setDefaultEditor(Object.class, new DefaultCellEditor(new JTextField()));

                    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

                    for (int i = 0; i < table.getColumnCount(); i++) {
                        TableColumn column = table.getColumnModel().getColumn(i);
                        int preferredWidth = getColumnPreferredWidth(table, i);
                        column.setPreferredWidth(preferredWidth);
                    }

                    JScrollPane scrollPane = new JScrollPane(table,
                            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                            JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS); // Adding JScrollPane with both scroll bars

                    frame.getContentPane().remove(1); // Remove the previous component in center
                    frame.getContentPane().add(scrollPane, BorderLayout.CENTER); // Add scroll pane

                    frame.revalidate();
                    frame.repaint();
                } else {
                    JOptionPane.showMessageDialog(frame, "Error connecting to database");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame, "Error retrieving data: " + ex.getMessage());
            }
        }
    }

    private class UpdateAllButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                // Connect to the database
                Connection conn = connectToDatabase();
                
             // Fetch today's date
                LocalDate today = LocalDate.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String formattedDate = today.format(formatter);

                
                if (conn != null) {
                    // Call stored procedures
                    callStoredProcedure(conn, "CalculatePrincipalBalances");
                    callStoredProcedure(conn, "Calculate_Partner_share_One_2");
                    callStoredProcedure(conn, "Calculate_Lendingkart_Share_One_1");
                    callStoredProcedureWithParameter(conn, "excel_import.update_emi_no", formattedDate);
                    callStoredProcedure(conn, "update_DPD");

                    // Retrieve data from the updated table
                    retrieveAndDisplayData(conn);

                    JOptionPane.showMessageDialog(frame, "Procedures executed and data updated successfully!");
                } else {
                    JOptionPane.showMessageDialog(frame, "Error connecting to database");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame, "Error updating data: " + ex.getMessage());
            }
        }

        private void callStoredProcedure(Connection conn, String procedureName) throws SQLException {
            CallableStatement stmt = conn.prepareCall("{call " + procedureName + "()}");
            stmt.execute();
            stmt.close();
        }
        private void callStoredProcedureWithParameter(Connection conn, String procedureName, String parameter) throws SQLException {
            CallableStatement stmt = conn.prepareCall("{call " + procedureName + "(?)}");
            stmt.setString(1, parameter);
            stmt.execute();
            stmt.close();
        }
        private void retrieveAndDisplayData(Connection conn) throws SQLException {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM " + TABLE_NAME);

            DefaultTableModel tableModel = new DefaultTableModel();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                tableModel.addColumn(metaData.getColumnName(i));
            }

            while (rs.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    row[i] = rs.getObject(i + 1);
                }
                tableModel.addRow(row);
            }

            if (table != null) {
                frame.getContentPane().remove(1); // Remove the previous component in center
            }

            table = new JTable(tableModel);
            table.setCellSelectionEnabled(true);
            table.setDefaultEditor(Object.class, new DefaultCellEditor(new JTextField()));
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

            for (int i = 0; i < table.getColumnCount(); i++) {
                TableColumn column = table.getColumnModel().getColumn(i);
                int preferredWidth = getColumnPreferredWidth(table, i);
                column.setPreferredWidth(preferredWidth);
            }

            JScrollPane scrollPane = new JScrollPane(table,
                    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

            frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

            frame.revalidate();
            frame.repaint();
        }
    }

    private class AddButtonListener implements ActionListener {
    	@Override
    	public void actionPerformed(ActionEvent e) {
    	    String newData = dataInput.getText();
    	    int selectedRow = table.getSelectedRow();
    	    int selectedColumn = table.getSelectedColumn();

    	    if (selectedRow != -1 && selectedColumn != -1 && newData != null && !newData.isEmpty()) {
    	        try {
    	            Connection conn = connectToDatabase();
    	            if (conn != null) {
    	                String columnName = table.getColumnName(selectedColumn);
    	                String primaryKeyColumn1 = table.getColumnName(0); // First column
    	                String primaryKeyColumn2 = table.getColumnName(1); // Second column
    	                String primaryKeyValue1 = table.getModel().getValueAt(selectedRow, 0).toString();
    	                String primaryKeyValue2 = table.getModel().getValueAt(selectedRow, 1).toString();

    	                // Prepare the SQL update query with both primary keys
    	                String updateQuery = "UPDATE " + TABLE_NAME + " SET " + columnName + " = ? WHERE " + primaryKeyColumn1 + " = ? AND " + primaryKeyColumn2 + " = ?";
    	                PreparedStatement stmt = conn.prepareStatement(updateQuery);
    	                stmt.setString(1, newData);
    	                stmt.setString(2, primaryKeyValue1);
    	                stmt.setString(3, primaryKeyValue2);
    	                stmt.executeUpdate();

    	                // Refresh the table after updating data
    	                refreshTable(conn);

    	                dataInput.setText(""); // Clear the input box
    	            } else {
    	                JOptionPane.showMessageDialog(frame, "Error connecting to database");
    	            }
    	        } catch (SQLException ex) {
    	            JOptionPane.showMessageDialog(frame, "Error updating data: " + ex.getMessage());
    	        }
    	    } else {
    	        JOptionPane.showMessageDialog(frame, "Please select a cell to update and enter new data");
    	    }
    	}


        private void refreshTable(Connection conn) {
            try {
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM " + TABLE_NAME);

                DefaultTableModel tableModel = new DefaultTableModel();
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                // Set column names
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

                table.setModel(tableModel); // Update table model
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame, "Error refreshing data: " + ex.getMessage());
            }
        }
    }

    private int getColumnPreferredWidth(JTable table, int columnIndex) {
        int minWidth = 100;
        int maxWidth = 300;

        for (int row = 0; row < table.getRowCount(); row++) {
            TableCellRenderer renderer = table.getCellRenderer(row, columnIndex);
            Component comp = table.prepareRenderer(renderer, row, columnIndex);
            int width = comp.getPreferredSize().width + 2;

            if (width > minWidth) {
                minWidth = width;
            }
        }

        TableColumn column = table.getColumnModel().getColumn(columnIndex);
        TableCellRenderer headerRenderer = column.getHeaderRenderer();
        if (headerRenderer == null) {
            headerRenderer = table.getTableHeader().getDefaultRenderer();
        }
        Object headerValue = column.getHeaderValue();
        Component headerComp = headerRenderer.getTableCellRendererComponent(table, headerValue, false, false, 0, columnIndex);
        int headerWidth = headerComp.getPreferredSize().width + 2;

        return Math.min(Math.max(minWidth, headerWidth), maxWidth);
    }

    private Connection connectToDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error connecting to database: " + e.getMessage());
            return null;
        }
    }

    private void uploadCSV(File file, Connection conn, String tableName) throws CsvValidationException {
        try {
            CSVReader reader = new CSVReader(new FileReader(file));
            String[] headers = reader.readNext();
            int numColumns = headers.length;

            String sql = "INSERT INTO " + tableName + " VALUES (";
            for (int i = 0; i < numColumns; i++) {
                sql += "?";
                if (i < numColumns - 1) {
                    sql += ", ";
                }
            }
            sql += ")";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            String[] row;
            while ((row = reader.readNext()) != null) {
                for (int i = 0; i < numColumns; i++) {
                    String value = row[i].trim();
                    if (value.isEmpty()) {
                        pstmt.setNull(i + 1, java.sql.Types.DATE); // Assuming the column is a DATE column
                    } else {
                        // Assuming the second column is the DATE column. Adjust as necessary.
                        if (headers[i].equalsIgnoreCase("Received_Date")) {
                            pstmt.setDate(i + 1, java.sql.Date.valueOf(value));
                        } else {
                            pstmt.setString(i + 1, value);
                        }
                    }
                }
                pstmt.addBatch();
            }
            pstmt.executeBatch();

            reader.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error importing CSV data: " + ex.getMessage());
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error reading CSV file: " + ex.getMessage());
        }
    }
    
    private void importExcelFileWithPOI(File file, Connection conn, String tableName) throws SQLException, IOException, InvalidFormatException {
        Workbook workbook = WorkbookFactory.create(new FileInputStream(file));
        Sheet sheet = workbook.getSheetAt(0);
        String createTableSQL = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (";
        for (int i = 0; i < sheet.getRow(0).getLastCellNum(); i++) {
            String columnName = sheet.getRow(0).getCell(i).getStringCellValue();
            createTableSQL += columnName + " VARCHAR(255),";
        }
        createTableSQL = createTableSQL.substring(0, createTableSQL.length() - 1) + ")";
        PreparedStatement pstmt = conn.prepareStatement(createTableSQL);
        pstmt.executeUpdate();

        String insertSQL = "INSERT INTO " + TABLE_NAME + " VALUES (";
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            for (int j = 0; j < row.getLastCellNum(); j++) {
                String cellValue = row.getCell(j).toString();
                insertSQL += "'" + cellValue + "',";
            }
            insertSQL = insertSQL.substring(0, insertSQL.length() - 1) + ")";
            pstmt = conn.prepareStatement(insertSQL);
            pstmt.executeUpdate();
            insertSQL = "INSERT INTO " + TABLE_NAME + " VALUES (";
        }
        workbook.close();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ExcelApp();
            }
        });
    }
}
