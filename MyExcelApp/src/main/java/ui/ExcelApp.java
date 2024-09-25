package ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
    
public class ExcelApp {     
    private JFrame frame;
    private JTextField filePathField;
    private JButton browseButton;
    private JButton uploadButton;
    private JButton downloadButton;
    private JButton filterButton; 
    private JButton refreshButton;
    private JButton updateAllButton;
    private JTextArea reportArea;
    private JTable table;
//    private JTextField dataInput;
//    private JButton addButton; 
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
        JLabel excelFileLabel = new JLabel("CSV FILE:");
        filePathField = new JTextField(20);
        browseButton = new JButton("Browse");
        uploadButton = new JButton("Upload");
        refreshButton = new JButton("Refresh");
        filterButton = new JButton("Filter");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        downloadButton = new JButton("Download");
        updateAllButton = new JButton("Update All");

        // New input box and button
//        dataInput = new JTextField(20); // Input box for data
//        addButton = new JButton("Add"); // Button to add data
//        addButton.addActionListener(new AddButtonListener()); // Add ActionListener
//
//        // New delete button
//        JButton deleteButton = new JButton("Delete"); // Button to delete data
//        deleteButton.addActionListener(new DeleteButtonListener()); // Add ActionListener

        browseButton.addActionListener(new BrowseButtonListener());
        uploadButton.addActionListener(new UploadButtonListener());
        refreshButton.addActionListener(new refreshButtonListener());
        downloadButton.addActionListener(new DownloadButtonListener());
        updateAllButton.addActionListener(new UpdateAllButtonListener());
        filterButton.addActionListener(new FilterButtonListener());

        // Create Verify button without drop-down menu
        JButton verifyButton = new JButton("Verify");

        // Add ActionListener to verify button
        verifyButton.addActionListener(e -> verifyAll());
        
        // Add Edit button
        JButton editButton = new JButton("Edit");
        editButton.addActionListener(e -> openEditWindow());

        // Add components to filePanel and buttonPanel
        filePanel.add(excelFileLabel);
        filePanel.add(filePathField);
        filePanel.add(browseButton);
        filePanel.add(uploadButton);
        filePanel.add(refreshButton);
        filePanel.add(verifyButton);
        buttonPanel.add(filterButton);
        buttonPanel.add(downloadButton);
        buttonPanel.add(updateAllButton);
//        buttonPanel.add(dataInput);
//        buttonPanel.add(addButton);
//        buttonPanel.add(deleteButton); // Add delete button here
        buttonPanel.add(editButton);
        
        topPanel.add(filePanel, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        reportArea = new JTextArea(10, 40);
        reportArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(reportArea);

        // Create footer panel
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel footerLabel = new JLabel("© tghApp Registration");

        footerPanel.add(footerLabel);

        frame.getContentPane().add(topPanel, BorderLayout.NORTH);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        frame.getContentPane().add(footerPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }
    
    private void openEditWindow() {
        new Edit(); // Instantiate and display the Edit window
    }

    private void verifyAll() {
    StringBuilder errorMessages = new StringBuilder();

    // Perform Negative_No verification
    StringBuilder negativeErrors = new StringBuilder("Negative No:\n");
    try {
        String[] columnsToCheck = {"id",	"Sr_No",	"Contract_ID",	"Prospect_Code",	"Partner_Name",	"Customer_Name", "Customer_Interest_Rate_As_Per_All_Index_Sheet",	"Partner_Interest_Rate",	"Lendingkart_Interest_Rate",	"Loan_Disbursment",	"Lendingkart_Total_Interest_Receivable",	"Partner_Total_Interest_Receivable",	"Loan_Tenure",	"Disbursement_Date",	"EMI_Amount",	"EMI_Date",	"Pre_EMI_Date",	"No_of_Days",	"Total_Pre_EMI_Int",	"Lendingkart_Share",	"Partner_Share",	"Opening_Bal_of_Principal_O_S",	"Opening_Balance_of_Lendingkart_Interest_O_S",	"Opening_Balance_Of_Partner_Interest_O_S",	"Principal_Receivable_As_per_Repayment_Schedule",	"Lendingkart_Int_Receivale_As_Per_Repayment_Schedule",	"Partner_Int_Receivale_As_Per_Repayment_Schedule",	"Total_Interest_Receivable",	"EMI_Receivable_As_Per_Repayment_Schedule",	"Bounce_Charges_Receivable",	"Penal_Charges_Receivable",	"Principal_Received", "Lendingkart_Share_One", "Partner_share_One",	"Total_Interest_Received",	"Bounce_Charges_Received",	"Penal_Charges_Received",	"EMI_Amount_Received",	"Advance_Received",	"Total_Received",	"Received_Date",	"Closing_Balance_of_Principal_O_S",	"Closing_Balance_Of_Lendingkart_Int_O_S",	"Closing_Balance_Of_Partner_Int_O_S",	"Total_O_S",	"EMI_No",	"DPD"};
        boolean negativeValueFound = false;

        for (int row = 0; row < table.getRowCount(); row++) {
            for (String column : columnsToCheck) {
                int columnIndex = table.getColumnModel().getColumnIndex(column);
                Object value = table.getValueAt(row, columnIndex);

                if (isNegative(value)) {
                    String id = table.getModel().getValueAt(row, 0).toString();
                    String srNo = table.getModel().getValueAt(row, 1).toString();
                    String errorMsg = "Negative value found in row with ID: " + id + " and Sr_No: " + srNo + " in column: " + column;
                    negativeErrors.append(errorMsg).append("\n");
                    negativeValueFound = true;
                }
            }
        }

        if (!negativeValueFound) {
            negativeErrors.append("No negative values found.\n");
        }
    } catch (Exception e) {
        negativeErrors.append("Error during negative value verification: ").append(e.getMessage()).append("\n");
    }

    // Perform Prospect_code verification
    StringBuilder prospectErrors = new StringBuilder("Duplicate Prospect_Nos:\n");
    try {
        Connection conn = connectToDatabase();
        if (conn != null) {
            boolean isValid = true;
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id, Prospect_Code FROM " + TABLE_NAME);

            Map<String, Set<Integer>> prospectToIdsMap = new HashMap<>();
            while (rs.next()) {
                int id = rs.getInt("id");
                String prospectCode = rs.getString("Prospect_Code");

                prospectToIdsMap.computeIfAbsent(prospectCode, k -> new HashSet<>()).add(id);
            }

            for (Map.Entry<String, Set<Integer>> entry : prospectToIdsMap.entrySet()) {
                if (entry.getValue().size() > 1) {
                    String prospectCode = entry.getKey();
                    Set<Integer> ids = entry.getValue();
                    String errorMsg = "Prospect_Code '" + prospectCode + "' is associated with multiple IDs: " + ids;
                    prospectErrors.append(errorMsg).append("\n");
                    isValid = false;
                }
            }

            if (isValid) {
                prospectErrors.append("No issues with Prospect_code found.\n");
            }

            rs.close();
            stmt.close();
            conn.close();
        }
    } catch (Exception e) {
        prospectErrors.append("Error during verification: ").append(e.getMessage()).append("\n");
    }

    // Perform Loan_Tenure verification
    StringBuilder loanTenureErrors = new StringBuilder("Loan_Tenure miscounts:\n");

    try {
        Map<String, Map<Integer, Integer>> idToSrNoCounts = new HashMap<>();
        Map<String, Set<Integer>> idToSrNos = new HashMap<>();
        Map<String, Integer> idToLoanTenure = new HashMap<>();
        Map<String, List<Map<String, String>>> idToRowsData = new HashMap<>();
        
        boolean loanTenureIssuesFound = false;
        boolean uniquenessIssuesFound = false;

        for (int row = 0; row < table.getRowCount(); row++) {
            Object idValue = table.getModel().getValueAt(row, 0);
            Object srNoValue = table.getModel().getValueAt(row, 1);
            Object loanTenureValue = table.getModel().getValueAt(row, table.getColumnModel().getColumnIndex("Loan_Tenure"));
            Object contractIdValue = table.getModel().getValueAt(row, table.getColumnModel().getColumnIndex("Contract_ID"));
            Object prospectCodeValue = table.getModel().getValueAt(row, table.getColumnModel().getColumnIndex("Prospect_Code"));
            Object partnerNameValue = table.getModel().getValueAt(row, table.getColumnModel().getColumnIndex("Partner_Name"));
            Object customerNameValue = table.getModel().getValueAt(row, table.getColumnModel().getColumnIndex("Customer_Name"));

            if (idValue == null || srNoValue == null || loanTenureValue == null ||
                contractIdValue == null || prospectCodeValue == null ||
                partnerNameValue == null || customerNameValue == null) {
                continue; // Skip if any value is null
            }

            String id = idValue.toString();
            int srNo = Integer.parseInt(srNoValue.toString());
            int loanTenure = Integer.parseInt(loanTenureValue.toString());
            String contractId = contractIdValue.toString();
            String prospectCode = prospectCodeValue.toString();
            String partnerName = partnerNameValue.toString();
            String customerName = customerNameValue.toString();

            // Track Sr_No counts per ID
            idToSrNoCounts.putIfAbsent(id, new HashMap<>());
            Map<Integer, Integer> srNoCounts = idToSrNoCounts.get(id);
            srNoCounts.put(srNo, srNoCounts.getOrDefault(srNo, 0) + 1);

            if (srNoCounts.get(srNo) > loanTenure) {
                loanTenureErrors.append("Sr_No exceeds Loan_Tenure in row with ID: ").append(id).append(", Sr_No: ").append(srNo).append("\n");
                loanTenureIssuesFound = true;
            }

            // Store the Loan_Tenure for each ID (only once)
            idToLoanTenure.putIfAbsent(id, loanTenure);

            // Track Sr_No entries for each ID
            idToSrNos.putIfAbsent(id, new HashSet<>());
            Set<Integer> srNos = idToSrNos.get(id);

            // Check for duplicates
            if (!srNos.add(srNo)) {
                loanTenureErrors.append("Duplicate Sr_No: ").append(srNo).append(" for ID: ").append(id).append("\n");
                loanTenureIssuesFound = true;
            }

            // Group rows data by ID
            idToRowsData.putIfAbsent(id, new ArrayList<>());
            Map<String, String> currentRowData = new HashMap<>();
            currentRowData.put("Contract_ID", contractId);
            currentRowData.put("Prospect_Code", prospectCode);
            currentRowData.put("Partner_Name", partnerName);
            currentRowData.put("Customer_Name", customerName);
            idToRowsData.get(id).add(currentRowData);
        }

        // Check if Sr_No count matches Loan_Tenure for each ID
        for (Map.Entry<String, Set<Integer>> entry : idToSrNos.entrySet()) {
            String id = entry.getKey();
            Set<Integer> srNos = entry.getValue();
            int loanTenure = idToLoanTenure.get(id);

            // Find missing Sr_No
            for (int i = 1; i <= loanTenure; i++) {
                if (!srNos.contains(i)) {
                    loanTenureErrors.append("Missing Sr_No: ").append(i).append(" for ID: ").append(id).append("\n");
                    loanTenureIssuesFound = true;
                }
            }

            // Check if the total count of Sr_No matches Loan_Tenure
            if (srNos.size() != loanTenure) {
                loanTenureErrors.append("ID: ").append(id).append(" has ").append(srNos.size())
                    .append(" Sr_No entries but Loan_Tenure is ").append(loanTenure).append("\n");
                loanTenureIssuesFound = true;
            }
        }

        // Check for uniqueness within each ID group
        for (Map.Entry<String, List<Map<String, String>>> entry : idToRowsData.entrySet()) {
            String id = entry.getKey();
            List<Map<String, String>> rowsData = entry.getValue();

            if (!rowsData.isEmpty()) {
                // Get expected values from the first row
                Map<String, String> expectedValues = rowsData.get(0);
                for (int i = 1; i < rowsData.size(); i++) {
                    Map<String, String> rowData = rowsData.get(i);
                    for (String column : new String[]{"Contract_ID", "Prospect_Code", "Partner_Name", "Customer_Name"}) {
                        if (!rowData.get(column).equals(expectedValues.get(column))) {
                            loanTenureErrors.append("Unique value found for ID: ").append(id)
                                    .append(", Sr_No: ").append(i + 1) // Sr_No index is 1-based
                                    .append(", Column: ").append(column)
                                    .append(", Value: ").append(rowData.get(column)).append("\n");
                            uniquenessIssuesFound = true;
                        }
                    }
                }
            }
        }

        if (!loanTenureIssuesFound && !uniquenessIssuesFound) {
            loanTenureErrors.append("All Sr_No values are within Loan_Tenure limits, and no uniqueness issues found.\n");
        }

    } catch (Exception e) {
        loanTenureErrors.append("Error during Loan_Tenure verification: ").append(e.getMessage()).append("\n");
    }

    // Output the results
    System.out.println(loanTenureErrors.toString());


    //empty cells code
    StringBuilder emptyCellErrors = new StringBuilder("Empty Cells:\n");

    
    checkForEmptyCells(emptyCellErrors);

    errorMessages.append(negativeErrors).append("\n").append(prospectErrors).append("\n").append(loanTenureErrors).append("\n").append(emptyCellErrors);

    JTextArea textArea = new JTextArea(errorMessages.toString());
    textArea.setEditable(false);
    JScrollPane scrollPane = new JScrollPane(textArea);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    scrollPane.setPreferredSize(new Dimension(500, 400));

    JButton downloadButton = new JButton("Download");
    downloadButton.addActionListener(e -> {
        try {
            downloadErrorsToExcel();
            JOptionPane.showMessageDialog(frame, "Download successful! File saved to Desktop/discrepancy_Report.xlsx");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Error during download: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    });

    Object[] options = {"OK", downloadButton};
    JOptionPane.showOptionDialog(frame, scrollPane, "Verification Results", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
}

    private void checkForEmptyCells(StringBuilder errorMessages) {
    boolean emptyCellFound = false;

    String[] requiredColumns = {"id", "Sr_No", "Contract_ID", "Prospect_Code", "Partner_Name", "Customer_Name", "Customer_Interest_Rate_As_Per_All_Index_Sheet", "Partner_Interest_Rate", "Lendingkart_Interest_Rate", "Loan_Disbursment", "Lendingkart_Total_Interest_Receivable", "Partner_Total_Interest_Receivable", "Loan_Tenure", "Disbursement_Date", "EMI_Amount", "EMI_Date"};

    for (int row = 0; row < table.getRowCount(); row++) {
        for (String column : requiredColumns) {
            int columnIndex = table.getColumnModel().getColumnIndex(column);
            Object value = table.getValueAt(row, columnIndex);

            if (value == null || value.toString().isEmpty()) {
                String id = table.getModel().getValueAt(row, 0).toString();
                String srNo = table.getModel().getValueAt(row, 1).toString();
                String columnName = table.getColumnName(columnIndex);
                errorMessages.append("Error ").append(row + 1).append(": Empty cell found in row with ID: ").append(id).append(" and Sr_No: ").append(srNo).append(" in column: ").append(columnName).append("\n");
                emptyCellFound = true;
            }
        }
    }

    if (emptyCellFound) {
        errorMessages.append("\n");
    }
}
    
    private void downloadErrorsToExcel() throws Exception {
        String userDesktopPath = System.getProperty("user.home") + "/Desktop/discrepancy_Report.csv";

        try (Workbook workbook = new XSSFWorkbook()) {
            // Create sheets for different types of errors
            Sheet sheet1 = workbook.createSheet("Negative_No_Errors");
            Sheet sheet2 = workbook.createSheet("Prospect_Code_Errors");
            Sheet sheet3 = workbook.createSheet("Loan_Tenure_Errors");
            Sheet sheet4 = workbook.createSheet("Empty_Cell_Errors");

            // Fill sheets with error data
            fillSheetWithErrorRows(sheet1, getNegativeErrorRows());
            fillSheetWithErrorRows(sheet2, getProspectErrorRows());
            fillSheetWithErrorRows(sheet3, getUniqueValueErrorRows());
            fillSheetWithErrorRows(sheet4, getEmptyCellErrorRows());

            // Write to file
            try (FileOutputStream fileOut = new FileOutputStream(userDesktopPath)) {
                workbook.write(fileOut);
            }
        }
    }

	private List<Integer> getEmptyCellErrorRows() {
    List<Integer> errorRows = new ArrayList<>();
    String[] requiredColumns = {"id", "Sr_No", "Contract_ID", "Prospect_Code", "Partner_Name", "Customer_Name", "Customer_Interest_Rate_As_Per_All_Index_Sheet", "Partner_Interest_Rate", "Lendingkart_Interest_Rate", "Loan_Disbursment", "Lendingkart_Total_Interest_Receivable", "Partner_Total_Interest_Receivable", "Loan_Tenure", "Disbursement_Date", "EMI_Amount", "EMI_Date"};

    for (int row = 0; row < table.getRowCount(); row++) {
        for (String column : requiredColumns) {
            int columnIndex = table.getColumnModel().getColumnIndex(column);
            Object value = table.getValueAt(row, columnIndex);

            if (value == null || value.toString().isEmpty()) {
                errorRows.add(row);
                break; // Assuming you want to capture the first instance of an empty cell in a row
            }
        }
    }

    return errorRows;
}

	private List<Integer> getNegativeErrorRows() {
    List<Integer> errorRows = new ArrayList<>();
    String[] columnsToCheck = {"id",	"Sr_No",	"Contract_ID",	"Prospect_Code",	"Partner_Name",	"Customer_Name", "Customer_Interest_Rate_As_Per_All_Index_Sheet",	"Partner_Interest_Rate",	"Lendingkart_Interest_Rate",	"Loan_Disbursment",	"Lendingkart_Total_Interest_Receivable",	"Partner_Total_Interest_Receivable",	"Loan_Tenure",	"Disbursement_Date",	"EMI_Amount",	"EMI_Date",	"Pre_EMI_Date",	"No_of_Days",	"Total_Pre_EMI_Int",	"Lendingkart_Share",	"Partner_Share",	"Opening_Bal_of_Principal_O_S",	"Opening_Balance_of_Lendingkart_Interest_O_S",	"Opening_Balance_Of_Partner_Interest_O_S",	"Principal_Receivable_As_per_Repayment_Schedule",	"Lendingkart_Int_Receivale_As_Per_Repayment_Schedule",	"Partner_Int_Receivale_As_Per_Repayment_Schedule",	"Total_Interest_Receivable",	"EMI_Receivable_As_Per_Repayment_Schedule",	"Bounce_Charges_Receivable",	"Penal_Charges_Receivable",	"Principal_Received", "Lendingkart_Share_One", "Partner_share_One",	"Total_Interest_Received",	"Bounce_Charges_Received",	"Penal_Charges_Received",	"EMI_Amount_Received",	"Advance_Received",	"Total_Received",	"Received_Date",	"Closing_Balance_of_Principal_O_S",	"Closing_Balance_Of_Lendingkart_Int_O_S",	"Closing_Balance_Of_Partner_Int_O_S",	"Total_O_S",	"EMI_No",	"DPD"};

    for (int row = 0; row < table.getRowCount(); row++) {
        for (String column : columnsToCheck) {
            int columnIndex = table.getColumnModel().getColumnIndex(column);
            Object value = table.getValueAt(row, columnIndex);

            if (isNegative(value)) {
                errorRows.add(row);
                break;
            }
        }
    }

    return errorRows;
}

	private List<Integer> getProspectErrorRows() {
    List<Integer> errorRows = new ArrayList<>();
    try {
        Connection conn = connectToDatabase();
        if (conn != null) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id, Prospect_Code FROM " + TABLE_NAME);

            Map<String, Set<Integer>> prospectToIdsMap = new HashMap<>();
            while (rs.next()) {
                int id = rs.getInt("id");
                String prospectCode = rs.getString("Prospect_Code");

                prospectToIdsMap.computeIfAbsent(prospectCode, k -> new HashSet<>()).add(id);
            }

            for (Map.Entry<String, Set<Integer>> entry : prospectToIdsMap.entrySet()) {
                if (entry.getValue().size() > 1) {
                    for (Integer id : entry.getValue()) {
                        for (int row = 0; row < table.getRowCount(); row++) {
                            if (table.getModel().getValueAt(row, 0).toString().equals(id.toString())) {
                                errorRows.add(row);
                                break;
                            }
                        }
                    }
                }
            }

            rs.close();
            stmt.close();
            conn.close();
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return errorRows;
}

	private List<Integer> getUniqueValueErrorRows() {
	    List<Integer> errorRows = new ArrayList<>();
	    Map<String, List<Integer>> idToRowIndexes = new HashMap<>();
	    Map<String, List<Map<String, String>>> idToRowsData = new HashMap<>();

	    for (int row = 0; row < table.getRowCount(); row++) {
	        Object idValue = table.getModel().getValueAt(row, 0); // Assuming ID is in the first column
	        Object contractIdValue = table.getModel().getValueAt(row, table.getColumnModel().getColumnIndex("Contract_ID"));
	        Object prospectCodeValue = table.getModel().getValueAt(row, table.getColumnModel().getColumnIndex("Prospect_Code"));
	        Object partnerNameValue = table.getModel().getValueAt(row, table.getColumnModel().getColumnIndex("Partner_Name"));
	        Object customerNameValue = table.getModel().getValueAt(row, table.getColumnModel().getColumnIndex("Customer_Name"));

	        if (idValue == null || contractIdValue == null || prospectCodeValue == null ||
	            partnerNameValue == null || customerNameValue == null) {
	            continue; // Skip if any value is null
	        }

	        String id = idValue.toString();
	        String contractId = contractIdValue.toString();
	        String prospectCode = prospectCodeValue.toString();
	        String partnerName = partnerNameValue.toString();
	        String customerName = customerNameValue.toString();

	        // Track row indexes by ID
	        idToRowIndexes.putIfAbsent(id, new ArrayList<>());
	        idToRowIndexes.get(id).add(row);

	        // Group rows data by ID
	        idToRowsData.putIfAbsent(id, new ArrayList<>());
	        Map<String, String> currentRowData = new HashMap<>();
	        currentRowData.put("Contract_ID", contractId);
	        currentRowData.put("Prospect_Code", prospectCode);
	        currentRowData.put("Partner_Name", partnerName);
	        currentRowData.put("Customer_Name", customerName);
	        idToRowsData.get(id).add(currentRowData);
	    }

	    // Check for uniqueness issues within each ID group
	    for (Map.Entry<String, List<Map<String, String>>> entry : idToRowsData.entrySet()) {
	        String id = entry.getKey();
	        List<Map<String, String>> rowsData = entry.getValue();

	        if (!rowsData.isEmpty()) {
	            // Get expected values from the first row
	            Map<String, String> expectedValues = rowsData.get(0);
	            for (int i = 1; i < rowsData.size(); i++) {
	                Map<String, String> rowData = rowsData.get(i);
	                for (String column : new String[]{"Contract_ID", "Prospect_Code", "Partner_Name", "Customer_Name"}) {
	                    if (!rowData.get(column).equals(expectedValues.get(column))) {
	                        // Add the row index to errorRows for unique value issues
	                        errorRows.add(idToRowIndexes.get(id).get(i));
	                        break; // Stop further checks for this row since it's already marked
	                    }
	                }
	            }
	        }
	    }

	    return errorRows;
	}

	private void fillSheetWithErrorRows(Sheet sheet, List<Integer> errorRows) {
    int rowCount = 0;

    // Add column headers
    Row headerRow = sheet.createRow(rowCount++);
    for (int col = 0; col < table.getColumnCount(); col++) {
        Cell cell = headerRow.createCell(col);
        cell.setCellValue(table.getColumnName(col));
    }

    // Add error rows
    for (int row : errorRows) {
        Row excelRow = sheet.createRow(rowCount++);
        for (int col = 0; col < table.getColumnCount(); col++) {
            Cell cell = excelRow.createCell(col);
            Object value = table.getValueAt(row, col);
            if (value != null) {
                cell.setCellValue(value.toString());
            }
        }
    }
}

	private boolean isNegative(Object value) {
	    if (value == null) {
	        return false; // or throw an exception if null values are not expected
	    }
	    if (value instanceof Number) {
	        return ((Number) value).doubleValue() < 0;
	    }
	    try {
	        return Double.parseDouble(value.toString()) < 0;
	    } catch (NumberFormatException e) {
	        return false;
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
                    if (entry.getStringValue(columnIndex).equals(filterValue)) {
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
            String insertSQL = "INSERT INTO " + TABLE_NAME + " (";
            for (int i = 0; i < headers.length; i++) {
                insertSQL += headers[i] + ",";
            }
            insertSQL = insertSQL.substring(0, insertSQL.length() - 1) + ") VALUES (";
            for (int i = 0; i < headers.length; i++) {
                insertSQL += "?,";
            }
            insertSQL = insertSQL.substring(0, insertSQL.length() - 1) + ")";

            PreparedStatement pstmt = conn.prepareStatement(insertSQL);
            for (int i = 0; i < headers.length; i++) {
                String value = row[i].trim();
                if (value.isEmpty() && (headers[i].equalsIgnoreCase("Received_Date") ||
                                        headers[i].equalsIgnoreCase("Pre_EMI_Date") ||
                                        headers[i].equalsIgnoreCase("Disbursement_Date") ||
                                        headers[i].equalsIgnoreCase("EMI_Date"))) {
                    pstmt.setNull(i + 1, java.sql.Types.DATE); // Set null for empty date fields
                } else if (headers[i].equalsIgnoreCase("Received_Date") ||
                           headers[i].equalsIgnoreCase("Pre_EMI_Date") ||
                           headers[i].equalsIgnoreCase("Disbursement_Date") ||
                           headers[i].equalsIgnoreCase("EMI_Date")) {
                    pstmt.setDate(i + 1, java.sql.Date.valueOf(value));
                } else {
                    pstmt.setString(i + 1, value);
                }
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

                try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
                    // Write the header row
                    DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
                    int columnCount = tableModel.getColumnCount();
                    String[] header = new String[columnCount];
                    for (int i = 0; i < columnCount; i++) {
                        header[i] = tableModel.getColumnName(i);
                    }
                    writer.writeNext(header);

                    // Write the filtered data rows
                    // Use the JTable's row sorter to get the filtered rows
                    for (int i = 0; i < table.getRowCount(); i++) {
                        String[] row = new String[columnCount];
                        for (int j = 0; j < columnCount; j++) {
                            // Use convertRowIndexToModel to get the correct row index in the model
                            Object value = table.getValueAt(i, j);
                            row[j] = value != null ? value.toString() : "";
                        }
                        writer.writeNext(row);
                    }

                    JOptionPane.showMessageDialog(frame, "File downloaded successfully.");
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Error writing the file: " + ex.getMessage());
                }
            }
        }
    }

    private class refreshButtonListener implements ActionListener {
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
//                            dataInput.setText(cellValue);
                        }
                    }
                });

                // Set cell selection enabled to true
                table.setCellSelectionEnabled(true);
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

    private class UpdateAllButtonListener implements ActionListener {       
    @Override
    public void actionPerformed(ActionEvent e) {
        LoadingScreen loadingScreen = new LoadingScreen();
        
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                loadingScreen.setVisible(true);
                loadingScreen.setMessage("Connecting to database...");

                try {
                    // Connect to the database
                    Connection conn = connectToDatabase();

                    // Fetch today's date
                    LocalDate today = LocalDate.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    String formattedDate = today.format(formatter);

                    if (conn != null) {
                        loadingScreen.setMessage("Executing stored procedures...");

                        // Call stored procedures
                        callStoredProcedure(conn, "Calculate_Partner_share_One_2");
                        loadingScreen.setMessage("Calculating Partner Share...");
                        
                        callStoredProcedure(conn, "Calculate_Lendingkart_Share_One_1");
                        loadingScreen.setMessage("Calculating Lendingkart Share...");

                        callStoredProcedure(conn, "CalculatePrincipalBalances");
                        loadingScreen.setMessage("Calculating Principal Balances...");
                        
                        callStoredProcedureWithParameter(conn, "excel_import.update_emi_no", formattedDate);
                        loadingScreen.setMessage("Updating EMI Numbers...");

                        callStoredProcedure(conn, "update_DPD");
                        loadingScreen.setMessage("Updating DPD...");

                        loadingScreen.setMessage("Fetching and displaying data...");

                        // Retrieve data from the updated table using pagination
                        retrieveAndDisplayDataWithPagination(conn, 0, 1000);

                        JOptionPane.showMessageDialog(frame, "Procedures executed and data updated successfully!");
                    } else {
                        JOptionPane.showMessageDialog(frame, "Error connecting to database");
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(frame, "Error updating data: " + ex.getMessage());
                } finally {
                    loadingScreen.dispose();
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void callStoredProcedure(Connection conn, String procedureName) throws SQLException {
        try (CallableStatement stmt = conn.prepareCall("{call " + procedureName + "()}")) {
            stmt.execute();
        }
    }

    private void callStoredProcedureWithParameter(Connection conn, String procedureName, String parameter) throws SQLException {
        try (CallableStatement stmt = conn.prepareCall("{call " + procedureName + "(?)}")) {
            stmt.setString(1, parameter);
            stmt.execute();
        }
    }

    private void retrieveAndDisplayDataWithPagination(Connection conn, int offset, int limit) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM " + TABLE_NAME + " LIMIT " + limit + " OFFSET " + offset);

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

    // Example to calculate preferred width for table columns
    private int getColumnPreferredWidth(JTable table, int columnIndex) {
        TableColumn column = table.getColumnModel().getColumn(columnIndex);
        int preferredWidth = 0;
        for (int row = 0; row < table.getRowCount(); row++) {
            TableCellRenderer cellRenderer = table.getCellRenderer(row, columnIndex);
            Component comp = table.prepareRenderer(cellRenderer, row, columnIndex);
            preferredWidth = Math.max(comp.getPreferredSize().width + 1, preferredWidth);
        }
        return preferredWidth;
    }
}

//    private class AddButtonListener implements ActionListener {
//    @Override
//    public void actionPerformed(ActionEvent e) {
//        String newData = dataInput.getText();
//        int selectedRow = table.getSelectedRow();
//        int selectedColumn = table.getSelectedColumn();
//
//        if (selectedRow != -1 && selectedColumn != -1 && newData != null && !newData.isEmpty()) {
//            try {
//                Connection conn = connectToDatabase();
//                if (conn != null) {
//                    String columnName = table.getColumnName(selectedColumn);
//                    String primaryKeyColumn1 = table.getColumnName(0); // First column
//                    String primaryKeyColumn2 = table.getColumnName(1); // Second column
//                    String primaryKeyValue1 = table.getModel().getValueAt(table.convertRowIndexToModel(selectedRow), 0).toString();
//                    String primaryKeyValue2 = table.getModel().getValueAt(table.convertRowIndexToModel(selectedRow), 1).toString();
//
//                    // Prepare the SQL update query with both primary keys
//                    String updateQuery = "UPDATE " + TABLE_NAME + " SET " + columnName + " = ? WHERE " + primaryKeyColumn1 + " = ? AND " + primaryKeyColumn2 + " = ?";
//                    PreparedStatement stmt = conn.prepareStatement(updateQuery);
//                    stmt.setString(1, newData);
//                    stmt.setString(2, primaryKeyValue1);
//                    stmt.setString(3, primaryKeyValue2);
//                    stmt.executeUpdate();
//
//                    // Refresh the table after updating data
//                    refreshTable(conn);
//
//                    dataInput.setText(""); // Clear the input box
//                } else {
//                    JOptionPane.showMessageDialog(frame, "Error connecting to database");
//                }
//            } catch (SQLException ex) {
//                JOptionPane.showMessageDialog(frame, "Error updating data: " + ex.getMessage());
//            }
//        } else {
//            JOptionPane.showMessageDialog(frame, "Please select a cell to update and enter new data");
//        }
//    }
//
//    private void refreshTable(Connection conn) {
//        try {
//            Statement stmt = conn.createStatement();
//            ResultSet rs = stmt.executeQuery("SELECT * FROM " + TABLE_NAME);
//
//            DefaultTableModel tableModel = new DefaultTableModel();
//            ResultSetMetaData metaData = rs.getMetaData();
//            int columnCount = metaData.getColumnCount();
//
//            // Set column names
//            for (int i = 1; i <= columnCount; i++) {
//                tableModel.addColumn(metaData.getColumnName(i));
//            }
//
//            // Add rows to the table model
//            while (rs.next()) {
//                Object[] row = new Object[columnCount];
//                for (int i = 1; i <= columnCount; i++) {
//                    row[i - 1] = rs.getObject(i);
//                }
//                tableModel.addRow(row);
//            }
//
//            table.setModel(tableModel); // Update table model
//
//            // Set column widths
//            for (int i = 0; i < table.getColumnCount(); i++) {
//                TableColumn column = table.getColumnModel().getColumn(i);
//                int preferredWidth = getColumnPreferredWidth(table, i);
//                column.setPreferredWidth(preferredWidth);
//            }
//
//            // Repaint the table
//            table.revalidate();
//            table.repaint();
//        } catch (SQLException ex) {
//            JOptionPane.showMessageDialog(frame, "Error refreshing data: " + ex.getMessage());
//        }
//    }
//}
//
//    private class DeleteButtonListener implements ActionListener {
//        @Override
//        public void actionPerformed(ActionEvent e) {
//            int[] selectedRows = table.getSelectedRows();
//            int[] selectedColumns = table.getSelectedColumns();
//
//            if (selectedRows.length == 1 && selectedColumns.length == 1) {
//                // Single cell is selected, delete the cell's value
//                deleteSingleCell(selectedRows[0], selectedColumns[0]);
//            } else if (selectedRows.length == 1 && selectedColumns.length == table.getColumnCount()) {
//                // Entire row is selected, delete the row
//                deleteEntireRow(selectedRows[0]);
//            } else {
//                // Multiple cells selected, show error message
//                JOptionPane.showMessageDialog(frame, "Please select either a single cell or an entire row's columns to delete.");
//            }
//        }
//
//        private void deleteSingleCell(int selectedRow, int selectedColumn) {
//            try {
//                Connection conn = connectToDatabase();
//                if (conn != null) {
//                    String columnName = table.getColumnName(selectedColumn);
//                    String primaryKeyColumn1 = table.getColumnName(0); // First column
//                    String primaryKeyColumn2 = table.getColumnName(1); // Second column
//                    String primaryKeyValue1 = table.getModel().getValueAt(table.convertRowIndexToModel(selectedRow), 0).toString();
//                    String primaryKeyValue2 = table.getModel().getValueAt(table.convertRowIndexToModel(selectedRow), 1).toString();
//
//                    // Prepare the SQL delete query to set the value to null
//                    String deleteQuery = "UPDATE " + TABLE_NAME + " SET " + columnName + " = NULL WHERE " + primaryKeyColumn1 + " = ? AND " + primaryKeyColumn2 + " = ?";
//                    PreparedStatement stmt = conn.prepareStatement(deleteQuery);
//                    stmt.setString(1, primaryKeyValue1);
//                    stmt.setString(2, primaryKeyValue2);
//                    stmt.executeUpdate();
//
//                    // Refresh the table after deleting data
//                    refreshTable(conn);
//                } else {
//                    JOptionPane.showMessageDialog(frame, "Error connecting to database");
//                }
//            } catch (SQLException ex) {
//                JOptionPane.showMessageDialog(frame, "Error deleting data: " + ex.getMessage());
//            }
//        }
//
//        private void deleteEntireRow(int selectedRow) {
//            try {
//                Connection conn = connectToDatabase();
//                if (conn != null) {
//                    String primaryKeyColumn1 = table.getColumnName(0); // First column
//                    String primaryKeyColumn2 = table.getColumnName(1); // Second column
//                    String primaryKeyValue1 = table.getModel().getValueAt(table.convertRowIndexToModel(selectedRow), 0).toString();
//                    String primaryKeyValue2 = table.getModel().getValueAt(table.convertRowIndexToModel(selectedRow), 1).toString();
//
//                    // Prepare the SQL delete query to delete the entire row
//                    String deleteQuery = "DELETE FROM " + TABLE_NAME + " WHERE " + primaryKeyColumn1 + " = ? AND " + primaryKeyColumn2 + " = ?";
//                    PreparedStatement stmt = conn.prepareStatement(deleteQuery);
//                    stmt.setString(1, primaryKeyValue1);
//                    stmt.setString(2, primaryKeyValue2);
//                    stmt.executeUpdate();
//
//                    // Refresh the table after deleting the row
//                    refreshTable(conn);
//                } else {
//                    JOptionPane.showMessageDialog(frame, "Error connecting to database");
//                }
//            } catch (SQLException ex) {
//                JOptionPane.showMessageDialog(frame, "Error deleting row: " + ex.getMessage());
//            }
//        }
//
//		private void refreshTable(Connection conn) {
//			try {
//	            Statement stmt = conn.createStatement();
//	            ResultSet rs = stmt.executeQuery("SELECT * FROM " + TABLE_NAME);
//
//	            DefaultTableModel tableModel = new DefaultTableModel();
//	            ResultSetMetaData metaData = rs.getMetaData();
//	            int columnCount = metaData.getColumnCount();
//
//	            // Set column names
//	            for (int i = 1; i <= columnCount; i++) {
//	                tableModel.addColumn(metaData.getColumnName(i));
//	            }
//
//	            // Add rows to the table model
//	            while (rs.next()) {
//	                Object[] row = new Object[columnCount];
//	                for (int i = 1; i <= columnCount; i++) {
//	                    row[i - 1] = rs.getObject(i);
//	                }
//	                tableModel.addRow(row);
//	            }
//
//	            table.setModel(tableModel); // Update table model
//
//	            // Set column widths
//	            for (int i = 0; i < table.getColumnCount(); i++) {
//	                TableColumn column = table.getColumnModel().getColumn(i);
//	                int preferredWidth = getColumnPreferredWidth(table, i);
//	                column.setPreferredWidth(preferredWidth);
//	            }
//
//	            // Repaint the table
//	            table.revalidate();
//	            table.repaint();
//	        } catch (SQLException ex) {
//	            JOptionPane.showMessageDialog(frame, "Error refreshing data: " + ex.getMessage());
//	        }
//		}
//    }

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

            // Set the output date format (MySQL format)
            SimpleDateFormat mysqlDateFormat = new SimpleDateFormat("yyyy-MM-dd");

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

                    // Check if the current header is one of the date columns
                    if (headers[i].equalsIgnoreCase("Disbursement_Date") ||
                            headers[i].equalsIgnoreCase("EMI_Date") ||
                            headers[i].equalsIgnoreCase("Pre_EMI_Date") ||
                            headers[i].equalsIgnoreCase("Received_Date")) {

                        java.sql.Date sqlDate = null;
                        if (!value.isEmpty()) {
                            try {
                                // If the value is numeric, it's likely an Excel serial date
                                if (value.matches("\\d+")) {
                                    int excelSerialDate = Integer.parseInt(value);
                                    java.util.Date parsedDate = convertExcelDateToJavaDate(excelSerialDate);
                                    String formattedDate = mysqlDateFormat.format(parsedDate);
                                    sqlDate = java.sql.Date.valueOf(formattedDate);
                                } else {
                                    // Handle any other potential date formats here
                                    sqlDate = java.sql.Date.valueOf(value);
                                }
                            } catch (Exception ex) {
                                // If conversion fails, set the date as NULL
                                sqlDate = null;
                            }
                        }

                        if (sqlDate != null) {
                            pstmt.setDate(i + 1, sqlDate);
                        } else {
                            pstmt.setNull(i + 1, java.sql.Types.DATE); // If parsing failed, set NULL
                        }
                    } else {
                        pstmt.setString(i + 1, value);
                    }
                }
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            reader.close();
        } catch (SQLException | IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error importing CSV data: " + ex.getMessage());
        }
    }
   
   private java.util.Date convertExcelDateToJavaDate(int excelSerialDate) {
        // Excel date serial numbers start from 1900-01-01 (day 1)
        Calendar calendar = Calendar.getInstance();
        calendar.set(1900, Calendar.JANUARY, 1);
        calendar.add(Calendar.DAY_OF_MONTH, excelSerialDate - 2); // Subtract 2 because Excel starts at day 1
        return calendar.getTime();
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