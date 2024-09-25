package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class Compare extends JFrame {

    public Compare() {
        setTitle("Reconciliation");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        // Create the panel with the table
        JPanel tablePanel = new JPanel(new BorderLayout());

        // Add header
        JLabel header = new JLabel("Lendingkart Reconciliation", JLabel.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 18));
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        tablePanel.add(header, BorderLayout.NORTH);

        // Create and add the table
        JTable table = createStyledTableFromQuery();
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        tablePanel.add(scrollPane, BorderLayout.CENTER);
        add(tablePanel, BorderLayout.CENTER);
    }

    private JTable createStyledTableFromQuery() {
        String url = "jdbc:mysql://192.168.10.6:3306/excel_import";
        String user = "remote";
        String password = "TGH@2024";

        String query = """
            WITH MaxEMIRows AS (
                SELECT 
                    id, 
                    Prospect_Code, 
                    Partner_Name, 
                    Customer_Name, 
                    Closing_Balance_of_Principal_O_S, 
                    EMI_No, 
                    DPD, 
                    ROW_NUMBER() OVER (PARTITION BY Prospect_Code ORDER BY EMI_No DESC) AS rn 
                FROM tghapp 
            ), 
            RankedRows AS (
                SELECT 
                    id, 
                    Prospect_Code, 
                    Partner_Name, 
                    Customer_Name, 
                    Closing_Balance_of_Principal_O_S, 
                    EMI_No, 
                    DPD, 
                    MAX(EMI_No) OVER (PARTITION BY Prospect_Code) AS Max_EMI_No
                FROM MaxEMIRows 
                WHERE rn = 1 
            )
            -- Combine LEFT JOIN and RIGHT JOIN to simulate FULL OUTER JOIN
            SELECT 
                COALESCE(r.Prospect_Code, c.Prospect_Code) AS Prospect_Code,
                COALESCE(r.Partner_Name, c.Partner_Name) AS Partner_Name,
                COALESCE(r.Customer_Name, c.Customer_Name) AS Customer_Name,
                COALESCE(r.Closing_Balance_of_Principal_O_S) AS Outstanding_as_per_Lendingkart,
                COALESCE(c.Outstanding_client) AS Outstanding_Patner, 
                CASE 
                    WHEN r.Closing_Balance_of_Principal_O_S IS NOT NULL AND c.Outstanding_client IS NOT NULL AND r.Closing_Balance_of_Principal_O_S = c.Outstanding_client THEN 'matched values'
                    ELSE 'unmatched values'
                END AS Outstanding_status,
                r.EMI_No AS EMI_No_Lendingkart,
                c.EMI_No AS EMI_No_Partner,
                CASE 
                    WHEN r.EMI_No IS NOT NULL AND c.EMI_No IS NOT NULL AND r.EMI_No = c.EMI_No THEN 'matched values'
                    ELSE 'unmatched values'
                END AS No_Status,
                COALESCE(r.DPD) AS DPD_as_per_Lendingkart, 
                COALESCE(c.DPD_client) AS DPD_as_per_Partner,
                CASE 
                    WHEN r.DPD IS NOT NULL AND c.DPD_client IS NOT NULL AND r.DPD = c.DPD_client THEN 'matched values'
                    ELSE 'unmatched values'
                END AS DPD_status,
                CASE 
                    WHEN r.Prospect_Code IS NULL AND c.Prospect_Code IS NOT NULL THEN 'code is not found in lendingkart'
                    WHEN r.Prospect_Code IS NOT NULL AND c.Prospect_Code IS NULL THEN 'code is not found in Client'
                    ELSE NULL
                END AS Status
            FROM campare_data c
            LEFT JOIN RankedRows r ON c.Prospect_Code = r.Prospect_Code

            UNION

            SELECT 
                COALESCE(r.Prospect_Code, c.Prospect_Code) AS Prospect_Code,
                COALESCE(r.Partner_Name, c.Partner_Name) AS Partner_Name,
                COALESCE(r.Customer_Name, c.Customer_Name) AS Customer_Name,
                COALESCE(r.Closing_Balance_of_Principal_O_S) AS Outstanding_as_per_Lendingkart,
                COALESCE(c.Outstanding_client) AS Outstanding_Patner, 
                CASE 
                    WHEN r.Closing_Balance_of_Principal_O_S IS NOT NULL AND c.Outstanding_client IS NOT NULL AND r.Closing_Balance_of_Principal_O_S = c.Outstanding_client THEN 'matched values'
                    ELSE 'unmatched values'
                END AS Outstanding_status,
                r.EMI_No AS EMI_No_Lendingkart,
                c.EMI_No AS EMI_No_Partner,
                CASE 
                    WHEN r.EMI_No IS NOT NULL AND c.EMI_No IS NOT NULL AND r.EMI_No = c.EMI_No THEN 'matched values'
                    ELSE 'unmatched values'
                END AS No_Status,
                COALESCE(r.DPD) AS DPD_as_per_Lendingkart, 
                COALESCE(c.DPD_client) AS DPD_as_per_Partner,
                CASE 
                    WHEN r.DPD IS NOT NULL AND c.DPD_client IS NOT NULL AND r.DPD = c.DPD_client THEN 'matched values'
                    ELSE 'unmatched values'
                END AS DPD_status,
                CASE 
                    WHEN r.Prospect_Code IS NULL AND c.Prospect_Code IS NOT NULL THEN 'code is not found in lendingkart'
                    WHEN r.Prospect_Code IS NOT NULL AND c.Prospect_Code IS NULL THEN 'code is not found in Client'
                    ELSE NULL
                END AS Status
            FROM campare_data c
            RIGHT JOIN RankedRows r ON c.Prospect_Code = r.Prospect_Code

            ORDER BY Prospect_Code;
            """;

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            return createStyledTable(buildTableModel(rs));

        } catch (SQLException e) {
            e.printStackTrace();
            return new JTable();
        }
    }

    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Disable cell editing
            }

            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);

                if (isCellSelected(row, column)) {
                    c.setBackground(new Color(184, 207, 229));
                } else if (row % 2 == 0) {
                    c.setBackground(new Color(240, 240, 240));
                } else {
                    c.setBackground(Color.WHITE);
                }

                // Change text color for specific values
                if (column == getColumnIndex("Outstanding_status") || column == getColumnIndex("DPD_status") || column == getColumnIndex("No_Status")) {
                    String value = (String) getValueAt(row, column);
                    if ("matched values".equals(value)) {
                        c.setForeground(new Color(0, 153, 0));
                    } else if ("unmatched values".equals(value)) {
                        c.setForeground(Color.RED);
                    } else {
                        c.setForeground(Color.BLACK);
                    }
                } else {
                    c.setForeground(Color.BLACK);
                }

                return c;
            }

            private int getColumnIndex(String columnName) {
                return getColumnModel().getColumnIndex(columnName);
            }
        };

        // Apply custom font and row height
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(25);

        // Set column properties
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        setColumnWidthsBasedOnHeader(table);

        // Enable column reordering
        table.getTableHeader().setReorderingAllowed(true);

        // Enable column sorting
        table.setAutoCreateRowSorter(true);

        // Bold the header names
        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setFont(new Font("Arial", Font.BOLD, 14));

        return table;
    }

    private void setColumnWidthsBasedOnHeader(JTable table) {
        JTableHeader header = table.getTableHeader();
        int columnCount = table.getColumnCount();

        for (int column = 0; column < columnCount; column++) {
            TableColumn tableColumn = table.getColumnModel().getColumn(column);
            TableCellRenderer headerRenderer = tableColumn.getHeaderRenderer();
            if (headerRenderer == null) {
                headerRenderer = header.getDefaultRenderer();
            }
            Component headerComponent = headerRenderer.getTableCellRendererComponent(table, tableColumn.getHeaderValue(), false, false, 0, column);
            int headerWidth = headerComponent.getPreferredSize().width;

            int maxWidth = headerWidth;
            for (int row = 0; row < table.getRowCount(); row++) {
                TableCellRenderer cellRenderer = table.getCellRenderer(row, column);
                Component cellComponent = table.prepareRenderer(cellRenderer, row, column);
                int cellWidth = cellComponent.getPreferredSize().width;
                maxWidth = Math.max(maxWidth, cellWidth);
            }

            tableColumn.setPreferredWidth(maxWidth + 10); // Add some padding
        }
    }

    private DefaultTableModel buildTableModel(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();

        // Column names
        Vector<String> columnNames = new Vector<>();
        int columnCount = metaData.getColumnCount();
        for (int column = 1; column <= columnCount; column++) {
            columnNames.add(metaData.getColumnName(column));
        }

        // Data
        Vector<Vector<Object>> data = new Vector<>();
        while (rs.next()) {
            Vector<Object> vector = new Vector<>();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                vector.add(rs.getObject(columnIndex));
            }
            data.add(vector);
        }

        return new DefaultTableModel(data, columnNames);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Compare compare = new Compare();
            compare.setVisible(true);
        });
    }
}