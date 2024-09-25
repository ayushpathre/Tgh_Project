package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;

public class Edit extends JFrame {
	private JFrame frame;
    private JTable table;
    private JTextField dataInput;
    private JButton addButton, deleteButton, filterButton, backButton;
    private JScrollPane tableScrollPane;

    public Edit() {
        super("Edit Data");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Create table and buttons
        table = new JTable();
        dataInput = new JTextField(20);
        addButton = new JButton("Add");
        deleteButton = new JButton("Delete");
        filterButton = new JButton("Filter");
        backButton = new JButton("Back");

        // Set layout and add components
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.add(dataInput);
        headerPanel.add(addButton);
        headerPanel.add(deleteButton);
        headerPanel.add(filterButton);

        // Position the back button to the top right corner
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(headerPanel, BorderLayout.WEST);
        topPanel.add(backButton, BorderLayout.EAST);

        // Create scroll pane and add table
        tableScrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        tableScrollPane.setPreferredSize(new Dimension(780, 400));

        add(topPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);

        // Add action listeners
        addButton.addActionListener(new AddButtonListener());
        deleteButton.addActionListener(new DeleteButtonListener());
        filterButton.addActionListener(new FilterButtonListener());
        backButton.addActionListener(new BackButtonListener());

        // Load data into the table
        loadData();

        setVisible(true);
    }

    private void loadData() {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/excel_import", "remote", "TGH@2024");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM tghapp");

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

            table.setModel(tableModel);
            table.setCellSelectionEnabled(true);
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // Disable auto-resizing to enable horizontal scrolling

            // Adjust the column widths based on both header and cell content width
            adjustColumnWidths(table);

            conn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage());
        }
    }

    private void adjustColumnWidths(JTable table) {
        for (int column = 0; column < table.getColumnCount(); column++) {
            int preferredWidth = getColumnPreferredWidth(table, column);
            TableColumn tableColumn = table.getColumnModel().getColumn(column);
            tableColumn.setPreferredWidth(preferredWidth);
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


    private class AddButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String newData = dataInput.getText();
            int selectedRow = table.getSelectedRow();
            int selectedColumn = table.getSelectedColumn();

            if (selectedRow != -1 && selectedColumn != -1 && newData != null && !newData.isEmpty()) {
                try {
                    Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/excel_import", "remote", "TGH@2024");
                    String columnName = table.getColumnName(selectedColumn);
                    String primaryKeyColumn1 = table.getColumnName(0); // Assuming first column is primary key
                    String primaryKeyColumn2 = table.getColumnName(1); // Assuming second column is primary key
                    String primaryKeyValue1 = table.getModel().getValueAt(table.convertRowIndexToModel(selectedRow), 0).toString();
                    String primaryKeyValue2 = table.getModel().getValueAt(table.convertRowIndexToModel(selectedRow), 1).toString();

                    String updateQuery = "UPDATE tghapp SET " + columnName + " = ? WHERE " + primaryKeyColumn1 + " = ? AND " + primaryKeyColumn2 + " = ?";
                    PreparedStatement stmt = conn.prepareStatement(updateQuery);
                    stmt.setString(1, newData);
                    stmt.setString(2, primaryKeyValue1);
                    stmt.setString(3, primaryKeyValue2);
                    stmt.executeUpdate();

                    // Refresh the table after updating data
                    loadData();

                    // Show confirmation dialog
                    JOptionPane.showMessageDialog(Edit.this, "New entry added successfully!");

                    dataInput.setText(""); // Clear the input box
                    conn.close();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(Edit.this, "Error updating data: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(Edit.this, "Please select a cell to update and enter new data");
            }
        }
    }

    private class DeleteButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int[] selectedRows = table.getSelectedRows();
            int[] selectedColumns = table.getSelectedColumns();

            if (selectedRows.length > 0) {
                try {
                    Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/excel_import", "remote", "TGH@2024");
                    boolean entireRowDeleted = false;

                    for (int selectedRow : selectedRows) {
                        if (selectedColumns.length == table.getColumnCount()) {
                            // Delete the entire row if all columns of the row are selected
                            String primaryKeyColumn1 = table.getColumnName(0); // Assuming first column is primary key
                            String primaryKeyColumn2 = table.getColumnName(1); // Assuming second column is primary key
                            String primaryKeyValue1 = table.getModel().getValueAt(table.convertRowIndexToModel(selectedRow), 0).toString();
                            String primaryKeyValue2 = table.getModel().getValueAt(table.convertRowIndexToModel(selectedRow), 1).toString();

                            String deleteQuery = "DELETE FROM tghapp WHERE " + primaryKeyColumn1 + " = ? AND " + primaryKeyColumn2 + " = ?";
                            PreparedStatement stmt = conn.prepareStatement(deleteQuery);
                            stmt.setString(1, primaryKeyValue1);
                            stmt.setString(2, primaryKeyValue2);
                            stmt.executeUpdate();

                            entireRowDeleted = true;
                        } else {
                            // Clear selected cells to NULL if not all columns of the row are selected
                            for (int selectedColumn : selectedColumns) {
                                String columnName = table.getColumnName(selectedColumn);
                                String primaryKeyColumn1 = table.getColumnName(0); // Assuming first column is primary key
                                String primaryKeyColumn2 = table.getColumnName(1); // Assuming second column is primary key
                                String primaryKeyValue1 = table.getModel().getValueAt(table.convertRowIndexToModel(selectedRow), 0).toString();
                                String primaryKeyValue2 = table.getModel().getValueAt(table.convertRowIndexToModel(selectedRow), 1).toString();

                                String updateQuery = "UPDATE tghapp SET " + columnName + " = NULL WHERE " + primaryKeyColumn1 + " = ? AND " + primaryKeyColumn2 + " = ?";
                                PreparedStatement stmt = conn.prepareStatement(updateQuery);
                                stmt.setString(1, primaryKeyValue1);
                                stmt.setString(2, primaryKeyValue2);
                                stmt.executeUpdate();
                            }
                        }
                    }

                    // Refresh the table after deleting data
                    loadData();

                    if (entireRowDeleted) {
                        JOptionPane.showMessageDialog(Edit.this, "Row(s) deleted successfully!");
                    } else {
                        JOptionPane.showMessageDialog(Edit.this, "Cell(s) cleared successfully!");
                    }

                    conn.close();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(Edit.this, "Error deleting data: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(Edit.this, "Please select a cell or row to delete");
            }
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

    private class BackButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Implement back button logic here
            dispose(); // Closes the current window
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Edit::new);
    }
}
