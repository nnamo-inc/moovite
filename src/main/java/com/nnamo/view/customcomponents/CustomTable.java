package com.nnamo.view.customcomponents;

import com.nnamo.enums.ColumnName;
import com.nnamo.enums.DataType;
import com.nnamo.interfaces.TableCheckIsFavBehaviour;
import com.nnamo.interfaces.TableRowClickBehaviour;
import com.nnamo.utils.CustomColor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Vector;

/**
 * Custom {@link JPanel} that provides a {@link JTable} with search functionality and sorting capabilities.
 * It allows adding, removing, and clearing rows, and supports custom behaviors for row clicks and favorite checks.
 *
 * @author Riccardo Finocchiaro
 * @author Samuele Lombardi
 *
 * @see JPanel
 * @see JTable
 * @see CustomSearchBar
 */
public class CustomTable extends JPanel {

    JTable table;
    JButton resetButton;
    CustomSearchBar searchBar;

    JScrollPane scrollPane;
    ColumnName[] tableColumns;
    DataType dataType;
    ArrayList<ColumnName> searchColumns;
    DefaultTableModel model;
    TableRowSorter sorter;
    Vector<Object> rowData;
    private boolean isSearchable = true;

    TableRowClickBehaviour tableRowClickBehaviour;
    TableCheckIsFavBehaviour tableCheckIsFavBehaviour;

    // CONSTRUCTOR //
    /**
     * Creates a {@link CustomTable} with the specified {@link JTable} {@link ColumnName} and {@link DataType}.
     * Initializes the {@link JTable} model, sets up the {@link JTable}, and adds a {@link ScrollPane} to scroll all the rows.
     *
     * @param tableColumns Array of column names for the table.
     * @param dataType The data type of the table.
     *
     * @see ColumnName
     * @see DataType
     * @see JTable
     * @see ScrollPane
     */
    public CustomTable(ColumnName[] tableColumns, DataType dataType) {
        super(new BorderLayout());
        this.tableColumns = tableColumns;
        this.dataType = dataType;
        this.model = new DefaultTableModel(tableColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.table = new JTable(model);
        this.scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
        this.sorter = new TableRowSorter(model);
        table.setRowSorter(sorter);

        ListSelectionModel selectionModel = table.getSelectionModel();
        selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        resetButton = new JButton("Reset Sorting");
        resetButton.putClientProperty("JButton.buttonType", "roundRect");
        resetButton.setBackground(CustomColor.RED); // o new Color(128, 0, 0);
        resetButton.setForeground(Color.WHITE); // testo bianco

        add(resetButton, BorderLayout.SOUTH);

        add(resetButton, BorderLayout.SOUTH);

        initDefaultComparator();
        isSearchable = false;
        initListeners();
    }
    /**
     * Creates a {@link CustomTable} with the specified table {@link ColumnName}, hidden columns, searchable {@link ColumnName} and {@link DataType}.
     * Initializes the {@link JTable} model, sets up the {@link JTable}, adds a {@link ScrollPane}
     * to scroll all the rows and adds a {@link CustomSearchBar} for filtering rows based on the specified search columns.
     *
     * @param tableColumns Array of column names for the table.
     * @param hiddenColumns Array of column names to be hidden in the table.
     * @param searchColumn Array of column names to be used for searching.
     * @param dataType The data type of the table.
     *
     * @see ColumnName
     * @see DataType
     * @see JTable
     * @see CustomSearchBar
     */
    public CustomTable(ColumnName[] tableColumns, ColumnName[] hiddenColumns, ColumnName[] searchColumn, DataType dataType) {
        this(tableColumns, dataType);

        searchBar = new CustomSearchBar();
        searchColumns = new ArrayList<>();
        setSearchColumns(searchColumn);
        searchBar.setVisible(true);
        add(searchBar, BorderLayout.NORTH);

        for (ColumnName columnName : hiddenColumns) {
            int colIndex = Arrays.asList(tableColumns).indexOf(columnName);
            var column = table.getColumnModel().getColumn(colIndex);
            if (column != null) {
                table.removeColumn(column);
                System.out.println((String) column.getHeaderValue());
            }
        }
    }
    /**
     * Creates a {@link CustomTable} with the specified table {@link ColumnName}, hidden columns and {@link DataType}.
     * Initializes the {@link JTable} model, sets up the {@link JTable}, adds a {@link ScrollPane}
     * to scroll all the rows.
     *
     * @param tableColumns Array of column names for the table.
     * @param hiddenColumns Array of column names to be hidden in the table.
     * @param dataType The data type of the table.
     *
     * @see ColumnName
     * @see DataType
     * @see JTable
     */
    public CustomTable(ColumnName[] tableColumns, ColumnName[] hiddenColumns, DataType dataType) {
        this(tableColumns, dataType);

        for (ColumnName columnName : hiddenColumns) {
            int colIndex = Arrays.asList(tableColumns).indexOf(columnName);
            var column = table.getColumnModel().getColumn(colIndex);
            if (column != null) {
                table.removeColumn(column);
                System.out.println((String) column.getHeaderValue());
            }
        }
    }

    // METHODS //
    private boolean rowExists(Object[] rowData) {
        for (int i = 0; i < model.getRowCount(); i++) {
            boolean match = true;
            for (int j = 0; j < rowData.length; j++) {
                if (!model.getValueAt(i, j).equals(rowData[j])) {
                    match = false;
                    break;
                }
            }
            if (match) return true;
        }
        return false;
    }
    /**
     * Adds a new row to the {@link JTable} with the specified data.
     *
     * @param rowData An array of objects representing the data for the new row.
     */
    public void addRow(Object[] rowData) {
        if (!rowExists(rowData)) {
            model.addRow(rowData);
        } else {
            System.out.println("Row already exists in the table");

        }
    }

    /**
     * Adds a new row to the table with the specified data.
     * If the row already exists, it will not be added again.
     *
     * @param string the string used to check if the row exists.
     * @param columnName the column name to check for the string.
     *
     */
    public void removeRow(String string, ColumnName columnName) {
        for (int i = 0; i < model.getRowCount(); i++) {
            try {
                int colIndex = Arrays.asList(tableColumns).indexOf(columnName);
                if (model.getValueAt(i, colIndex).equals(string)) {
                    model.removeRow(i);
                    break;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                System.err.println("Column " + columnName + " not found in table.");
            }
        }
    }
    /**
     * Removes all the rows from the {@link JTable}.
     *
     * @see JTable
     */
    public void clear() {
        model.setRowCount(0);
    }
    
    ////////////// AI STUFF! //////////////
    private void initDefaultComparator() {
        Comparator<Object> comparator = (o1, o2) -> {
            String s1 = o1 == null ? "" : o1.toString().trim();
            String s2 = o2 == null ? "" : o2.toString().trim();

            // 1. Solo numeri che iniziano con 0
            boolean s1OnlyDigits = s1.matches("\\d+");
            boolean s2OnlyDigits = s2.matches("\\d+");
            boolean s1StartsWithZero = s1OnlyDigits && s1.startsWith("0");
            boolean s2StartsWithZero = s2OnlyDigits && s2.startsWith("0");
            if (s1StartsWithZero && !s2StartsWithZero)
                return -1;
            if (!s1StartsWithZero && s2StartsWithZero)
                return 1;
            if (s1StartsWithZero && s2StartsWithZero)
                return Long.compare(Long.parseLong(s1), Long.parseLong(s2));

            // 2. Solo numeri (senza lettere)
            if (s1OnlyDigits && !s2OnlyDigits)
                return -1;
            if (!s1OnlyDigits && s2OnlyDigits)
                return 1;
            if (s1OnlyDigits && s2OnlyDigits)
                return Long.compare(Long.parseLong(s1), Long.parseLong(s2));

            // 3. Numeri seguiti da lettere (es: 123abc)
            boolean s1NumLet = s1.matches("\\d+[a-zA-Z]+.*");
            boolean s2NumLet = s2.matches("\\d+[a-zA-Z]+.*");
            if (s1NumLet && !s2NumLet)
                return -1;
            if (!s1NumLet && s2NumLet)
                return 1;
            if (s1NumLet && s2NumLet) {
                s1StartsWithZero = s1.startsWith("0");
                s2StartsWithZero = s2.startsWith("0");
                if (s1StartsWithZero && !s2StartsWithZero)
                    return -1;
                if (!s1StartsWithZero && s2StartsWithZero)
                    return 1;
                // Se entrambi iniziano con lo stesso tipo di cifra, confronto numerico e poi
                // alfabetico
                String n1 = s1.replaceAll("\\D.*", "");
                String n2 = s2.replaceAll("\\D.*", "");
                int cmp = Long.compare(Long.parseLong(n1), Long.parseLong(n2));
                if (cmp != 0)
                    return cmp;
                return s1.compareToIgnoreCase(s2);
            }

            // 4. Lettere seguite da numeri (es: abc123)
            boolean s1LetNum = s1.matches("[a-zA-Z]+\\d+.*");
            boolean s2LetNum = s2.matches("[a-zA-Z]+\\d+.*");
            if (s1LetNum && !s2LetNum)
                return -1;
            if (!s1LetNum && s2LetNum)
                return 1;
            if (s1LetNum && s2LetNum) {
                // Estrai la parte di lettere e la parte numerica
                String l1 = s1.replaceAll("(\\D+).*", "$1");
                String l2 = s2.replaceAll("(\\D+).*", "$1");
                int cmp = l1.compareToIgnoreCase(l2);
                if (cmp != 0)
                    return cmp;
                // Se le lettere sono uguali, confronta la parte numerica
                String n1 = s1.replaceAll(".*?(\\d+).*", "$1");
                String n2 = s2.replaceAll(".*?(\\d+).*", "$1");
                return Long.compare(Long.parseLong(n1), Long.parseLong(n2));
            }

            // 5. Nessun numero: solo lettere, ordina alfabeticamente
            boolean s1NoNum = !s1.matches(".*\\d.*");
            boolean s2NoNum = !s2.matches(".*\\d.*");
            if (s1NoNum && !s2NoNum)
                return -1;
            if (!s1NoNum && s2NoNum)
                return 1;
            if (s1NoNum && s2NoNum) {
                return s1.compareToIgnoreCase(s2);
            }

            // Fallback alfabetico
            return s1.compareToIgnoreCase(s2);
        };

        for (int i = 0; i < tableColumns.length; i++) {
            sorter.setComparator(i, comparator);
        }
    }

    // BEHAVIOURS METHODS //
    private void initListeners() {

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int selectedRow = table.getSelectedRow();
                    if (selectedRow != -1 && tableRowClickBehaviour != null) {
                        int modelRow = table.convertRowIndexToModel(selectedRow);
                        rowData = (Vector<Object>) model.getDataVector().get(modelRow);
                        try {
                            tableRowClickBehaviour.onRowClick(rowData, tableColumns, dataType);
                        } catch (SQLException | IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }
        });

        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                sorter.setSortKeys(null);
            }
        });

        if (isSearchable) {

            searchBar.getField().addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    String searchText = searchBar.getFieldText().trim();
                    if (searchText.isEmpty()) {
                        sorter.setRowFilter(null);
                    } else {
                        ArrayList<RowFilter<DefaultTableModel, Object>> filters = new ArrayList<>();
                        if (searchColumns.isEmpty()) {
                            filters.add(RowFilter.regexFilter("(?i)" + searchText, 0));
                        } else {
                            for (ColumnName column : searchColumns) {
                                int index = table.getColumnModel().getColumnIndex(column.toString());
                                filters.add(RowFilter.regexFilter("(?i)" + searchText, index));
                            }
                        }
                        RowFilter<DefaultTableModel, Object> combinedFilter = RowFilter.orFilter(filters);
                        sorter.setRowFilter(combinedFilter);
                    }
                }
            });

            searchBar.getButton().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    searchBar.setField("");
                    sorter.setRowFilter(null);
                }
            });
        }
    }

    /**
     * Sets the {@link TableCheckIsFavBehaviour} to check if a row is marked as favorite.
     *
     * @param tableRowClickBehaviour The behavior to set.
     *
     * @see TableCheckIsFavBehaviour
     */
    public void setTableRowClickBehaviour(TableRowClickBehaviour tableRowClickBehaviour) {
        this.tableRowClickBehaviour = tableRowClickBehaviour;
    }

    /**
     * Sets the visibility of the search bar.
     *
     * @param tableCheckIsFavBehaviour {@code true} to show the search bar, {@code false} to hide it.
     *
     * @see CustomSearchBar
     */
    public void setTableCheckIsFavBehaviour(TableCheckIsFavBehaviour tableCheckIsFavBehaviour) {
        this.tableCheckIsFavBehaviour = tableCheckIsFavBehaviour;
    }

    // GETTERS AND SETTERS //

    /**
     * Sets the columns to be used for searching in the table.
     *
     * @param columns
     */
    public void setSearchColumns(ColumnName... columns) {
        for (ColumnName column : columns) {
            int index = table.getColumnModel().getColumnIndex(column.toString());
            if (index >= 0 && index < tableColumns.length) {
                searchColumns.add(column);
            } else {
                throw new IllegalArgumentException("Column index out of bounds: " + column);
            }
        }
    }

    /**
     * Gets the {@link JTable}.
     *
     * @return the {@link JTable} instance.
     *
     * @see JTable
     */
    public JTable getTable() {
        return table;
    }
}
