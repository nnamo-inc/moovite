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

import static com.nnamo.enums.ColumnName.TYPE;

/**
 * Custom {@link JPanel} that provides a {@link JTable} with search functionality and sorting capabilities.
 * It allows adding, removing, and clearing rows, and supports custom behaviors for row clicks and favorite checks.
 *
 * @author Riccardo Finocchiaro
 * @author Samuele Lombardi
 * @see JPanel
 * @see JTable
 * @see CustomSearchBar
 */
public class CustomTable extends JPanel {

    private final JTable table;
    private final JButton resetButton;
    private CustomSearchBar searchBar;

    private final JScrollPane scrollPane;
    private final ColumnName[] tableColumns;
    private final ColumnName[] hiddenColumns;
    private ColumnName[] searchColumns;
    private final DataType dataType;
    private final DefaultTableModel model;
    private final TableRowSorter sorter;
    private Vector<Object> rowData;
    private boolean isSearchable;
    private final CustomRadioButtonsPanel radioButtonsPanel;

    TableRowClickBehaviour tableRowClickBehaviour;

    // CONSTRUCTOR //
    /**
     * Create a {@link CustomTable} with the specified parameters inside the builder.
     *
     * @param builder The builder containing the configuration parameters for the table.
     */
    public CustomTable(Builder builder) {
        super(new GridBagLayout());
        System.out.println("Building CustomTable with builder: " + builder);

        this.tableColumns = builder.tableColumns;
        this.dataType = builder.dataType;
        this.hiddenColumns = builder.hiddenColumns;
        this.searchColumns = builder.searchColumns;
        this.radioButtonsPanel = builder.radioButtonsPanel;

        this.model = new DefaultTableModel(tableColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.table = new JTable(model);

        this.scrollPane = new JScrollPane(table);
        add(scrollPane, new CustomGbc()
                .setPosition(0, 2)
                .setWeight(1.0, 1.0)
                .setFill(GridBagConstraints.BOTH)
                .setInsets(2, 5, 2, 5));

        this.sorter = new TableRowSorter(model);
        table.setRowSorter(sorter);

        ListSelectionModel selectionModel = table.getSelectionModel();
        selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        this.resetButton = new JButton("Reset Sorting");
        resetButton.setBackground(CustomColor.RED);
        resetButton.setForeground(Color.WHITE);
        resetButton.setOpaque(false);
        add(resetButton, new CustomGbc()
                .setPosition(0, 3)
                .setWeight(1.0, 0.0)
                .setFill(GridBagConstraints.HORIZONTAL)
                .setInsets(-3, 5, 5, 5));

        if (!(builder.searchColumns.length == 0)) {
            System.out.println("Search columns set to: " + Arrays.toString(searchColumns));

            searchBar = new CustomSearchBar();
            setSearchColumns(searchColumns);
            searchBar.setVisible(true);
            add(searchBar, new CustomGbc()
                    .setPosition(0, 0)
                    .setWeight(1.0, 0.0)
                    .setFill(GridBagConstraints.BOTH)
                    .setInsets(2, 5, 2, 5));
            isSearchable = true;
        }
        if (!(builder.hiddenColumns.length == 0)) {
            System.out.println("Hidden columns set to: " + Arrays.toString(hiddenColumns));

            System.out.println("\n" + "\n");
            System.out.println("table columns: " + Arrays.toString(tableColumns));
            System.out.println("hidden columns: " + Arrays.toString(hiddenColumns));
            for (ColumnName columnName : hiddenColumns) {
                int colIndex = Arrays.asList(tableColumns).indexOf(columnName);
                System.out.println("Hiding column: " + columnName + " at index " + colIndex);
                var column = table.getColumnModel().getColumn(colIndex);
                if (column != null) {
                    table.removeColumn(column);
                }
            }
        }

        if (radioButtonsPanel != null) {
            add(radioButtonsPanel, new CustomGbc()
                    .setPosition(0, 1)
                    .setWeight(1.0, 0.0)
                    .setFill(GridBagConstraints.HORIZONTAL)
                    .setInsets(2, 5, 2, 5));
        }

        initDefaultComparator();
        initListeners();
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
     * @param string     the string used to check if the row exists.
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

    public void addAllRows(Object[][] rowsData) {
        Vector<Vector<Object>> dataVector = new Vector<>();
        for (Object[] rowData : rowsData) {
            if (!rowExists(rowData)) {
                Vector<Object> row = new Vector<>();
                for (Object data : rowData) {
                    row.add(data);
                }
                dataVector.add(row);
            }
        }
        for (Vector<Object> row : dataVector) {
            model.getDataVector().add(row);
        }
        model.fireTableDataChanged();
    }

    /// /////////// AI STUFF! //////////////
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
                    System.out.println("Key released: " + e.getKeyChar());
                    String searchText = searchBar.getFieldText().trim();
                    if (searchText.isEmpty()) {
                        sorter.setRowFilter(null);
                    } else {
                        ArrayList<RowFilter<DefaultTableModel, Object>> filters = new ArrayList<>();
                        if (searchColumns.length == 0) {
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

        if (radioButtonsPanel != null) {
            for (JRadioButton radioButton : radioButtonsPanel.getRadioButtons()) {
                radioButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String filterText = radioButton.getText();
                        if (filterText.equals("All")) {
                            sorter.setRowFilter(null);
                        } else {
                            int routeTypeColIndex = table.getColumnModel().getColumnIndex(TYPE.toString()); // replace with your column name
                            RowFilter<DefaultTableModel, Object> filter = RowFilter.regexFilter("(?i)" + filterText, routeTypeColIndex);
                            sorter.setRowFilter(filter);
                        }
                    }
                });
            }
        }
    }

    /**
     * Sets the {@link TableCheckIsFavBehaviour} to check if a row is marked as favorite.
     *
     * @param tableRowClickBehaviour The behavior to set.
     * @see TableCheckIsFavBehaviour
     */
    public void setTableRowClickBehaviour(TableRowClickBehaviour tableRowClickBehaviour) {
        this.tableRowClickBehaviour = tableRowClickBehaviour;
    }
    // GETTERS AND SETTERS //

    /**
     * Sets the columns to be used for searching in the table.
     *
     * @param columns
     */
    public void setSearchColumns(ColumnName[] columns) {
        System.out.println("Setting search columns: " + Arrays.toString(columns));
        for (ColumnName column : columns) {
            int index = Arrays.asList(tableColumns).indexOf(column);
            System.out.println("    Column: " + column + ", Index: " + index);
            if (index >= 0 && index < tableColumns.length) {
                ArrayList<ColumnName> temp = new ArrayList<>(Arrays.asList(searchColumns));
                temp.add(column);
                searchColumns = temp.toArray(new ColumnName[0]);
            } else {
                throw new IllegalArgumentException("Column index out of bounds: " + column);
            }
        }
    }

    /**
     * Gets the {@link JTable}.
     *
     * @return the {@link JTable} instance.
     * @see JTable
     */
    public JTable getTable() {
        return table;
    }

    /**
     * Builder class for constructing a {@link CustomTable} instance with customizable parameters.
     *
     * @see CustomTable
     */
    public static class Builder {

        ColumnName[] tableColumns = new ColumnName[]{};
        ColumnName[] hiddenColumns = new ColumnName[]{};
        ColumnName[] searchColumns = new ColumnName[]{};
        CustomRadioButtonsPanel radioButtonsPanel = null;
        DataType dataType;

        /**
         * Sets the columns to be displayed in the table.
         *
         * @param tableColumns
         * @return
         */
        public Builder setTableColumns(ColumnName[] tableColumns) {
            System.out.println("Setting table columns: " + Arrays.toString(tableColumns));
            this.tableColumns = tableColumns;
            return this;
        }

        /**
         * sets the columns to be hidden in the table.
         *
         * @param hiddenColumns
         * @return
         */
        public Builder setHiddenColumns(ColumnName[] hiddenColumns) {
            System.out.println("Setting hidden columns: " + Arrays.toString(hiddenColumns));
            this.hiddenColumns = hiddenColumns;
            return this;
        }

        /**
         * Sets the columns to be used for searching in the table.
         * @param searchColumns
         * @return
         */
        public Builder setSearchColumns(ColumnName[] searchColumns) {
            System.out.println("Setting search columns: " + Arrays.toString(searchColumns));
            this.searchColumns = searchColumns;
            return this;
        }

        /**
         * Sets the data type for the table.
         *
         * @param dataType
         * @return
         */
        public Builder setDataType(DataType dataType) {
            System.out.println("Setting data type: " + dataType);
            this.dataType = dataType;
            return this;
        }

        /**
         * Sets custom radio buttons for filtering in the table.
         *
         * @param radioButtons
         * @return
         */
        public Builder setCustomRadioButtons(ArrayList<JRadioButton> radioButtons) {
            this.radioButtonsPanel = new CustomRadioButtonsPanel(radioButtons, "Route");
            return this;
        }

        /**
         * Builds and returns a {@link CustomTable} instance with the specified parameters.
         *
         * @return a new {@link CustomTable} instance.
         */
        public CustomTable build() {
            return new CustomTable(this);
        }


    }
}
