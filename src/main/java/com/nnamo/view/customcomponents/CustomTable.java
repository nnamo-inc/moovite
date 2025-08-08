package com.nnamo.view.customcomponents;

import com.nnamo.enums.ColumnName;
import com.nnamo.interfaces.TableRowClickBehaviour;
import com.nnamo.interfaces.TableSearchBehaviour;
import com.nnamo.utils.CustomColor;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
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

public class CustomTable extends JPanel {

    JTable table;
    JButton resetSortingButton = new JButton("Reset Sorting");
    CustomSearchBar customSearchBar = new CustomSearchBar();

    JScrollPane scrollPane;
    ColumnName[] tableColumns;
    ColumnName columnSelect;
    ArrayList<ColumnName> searchColumns = new ArrayList<>();
    DefaultTableModel model;
    TableRowSorter sorter;
    Vector<Object> rowData;
    boolean isSearchable = true;



    TableRowClickBehaviour tableRowClickBehaviour;
    TableSearchBehaviour tableSearchBehaviour;

    // CONSTRUCTOR //
    public CustomTable(ColumnName[] tableColumns, ColumnName columnSelect) {
        super(new BorderLayout());
        this.columnSelect = columnSelect;
        this.tableColumns = tableColumns;
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

        resetSortingButton.setBackground(CustomColor.RED);
        add(resetSortingButton, BorderLayout.SOUTH);

        checkSearchable();
        checkDataColumnAvailable();
        initDefaultComparator();
        initListeners();

    }

    // METHODS //
    public void clear() {
        model.setRowCount(0);
    }

    public void addRow(Object[] rowData) {
        model.addRow(rowData);
    }

    public void removeRow(String string) {
        for (int i = 0; i < model.getRowCount(); i++) {
            if (model.getValueAt(i, 1).equals(string)) {
                model.removeRow(i);
                break;
            }
        }
    }

    private void checkSearchable() {
        if (isSearchable) {
            add(customSearchBar, BorderLayout.NORTH);
        } else {
            customSearchBar.setVisible(false);
        }
    };

    private void checkDataColumnAvailable() {
        if (Arrays.asList(tableColumns).contains(columnSelect)) {
            this.columnSelect = columnSelect;
        } else {
            String callerClass = new Exception().getStackTrace()[1].getClassName();
            throw new IllegalArgumentException(
                    "\"" + columnSelect + "\"" + " colonna clickRowData invalida: " + callerClass + ".\n" +
                            "Colonne valide: " + Arrays.toString(tableColumns));
        }
    };

    ////////////// COMPLETAMENTE FATTO CON AI //////////////
    private void initDefaultComparator() {
        Comparator<Object> comparator = (o1, o2) -> {
            String s1 = o1 == null ? "" : o1.toString().trim();
            String s2 = o2 == null ? "" : o2.toString().trim();

            // 1. Solo numeri che iniziano con 0
            boolean s1OnlyDigits = s1.matches("\\d+");
            boolean s2OnlyDigits = s2.matches("\\d+");
            boolean s1StartsWithZero = s1OnlyDigits && s1.startsWith("0");
            boolean s2StartsWithZero = s2OnlyDigits && s2.startsWith("0");
            if (s1StartsWithZero && !s2StartsWithZero) return -1;
            if (!s1StartsWithZero && s2StartsWithZero) return 1;
            if (s1StartsWithZero && s2StartsWithZero)
                return Long.compare(Long.parseLong(s1), Long.parseLong(s2));

            // 2. Solo numeri (senza lettere)
            if (s1OnlyDigits && !s2OnlyDigits) return -1;
            if (!s1OnlyDigits && s2OnlyDigits) return 1;
            if (s1OnlyDigits && s2OnlyDigits)
                return Long.compare(Long.parseLong(s1), Long.parseLong(s2));

            // 3. Numeri seguiti da lettere (es: 123abc)
                boolean s1NumLet = s1.matches("\\d+[a-zA-Z]+.*");
                boolean s2NumLet = s2.matches("\\d+[a-zA-Z]+.*");
                if (s1NumLet && !s2NumLet) return -1;
                if (!s1NumLet && s2NumLet) return 1;
                if (s1NumLet && s2NumLet) {
                    s1StartsWithZero = s1.startsWith("0");
                    s2StartsWithZero = s2.startsWith("0");
                    if (s1StartsWithZero && !s2StartsWithZero) return -1;
                    if (!s1StartsWithZero && s2StartsWithZero) return 1;
                    // Se entrambi iniziano con lo stesso tipo di cifra, confronto numerico e poi alfabetico
                    String n1 = s1.replaceAll("\\D.*", "");
                    String n2 = s2.replaceAll("\\D.*", "");
                    int cmp = Long.compare(Long.parseLong(n1), Long.parseLong(n2));
                    if (cmp != 0) return cmp;
                    return s1.compareToIgnoreCase(s2);
                }

            // 4. Lettere seguite da numeri (es: abc123)
            boolean s1LetNum = s1.matches("[a-zA-Z]+\\d+.*");
            boolean s2LetNum = s2.matches("[a-zA-Z]+\\d+.*");
            if (s1LetNum && !s2LetNum) return -1;
            if (!s1LetNum && s2LetNum) return 1;
            if (s1LetNum && s2LetNum) {
                // Estrai la parte di lettere e la parte numerica
                String l1 = s1.replaceAll("(\\D+).*", "$1");
                String l2 = s2.replaceAll("(\\D+).*", "$1");
                int cmp = l1.compareToIgnoreCase(l2);
                if (cmp != 0) return cmp;
                // Se le lettere sono uguali, confronta la parte numerica
                String n1 = s1.replaceAll(".*?(\\d+).*", "$1");
                String n2 = s2.replaceAll(".*?(\\d+).*", "$1");
                return Long.compare(Long.parseLong(n1), Long.parseLong(n2));
            }

// 5. Nessun numero: solo lettere, ordina alfabeticamente
            boolean s1NoNum = !s1.matches(".*\\d.*");
            boolean s2NoNum = !s2.matches(".*\\d.*");
            if (s1NoNum && !s2NoNum) return -1;
            if (!s1NoNum && s2NoNum) return 1;
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
    // LISTENERS METHODS //
    public void initListeners() {

        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && tableRowClickBehaviour != null) {
                    System.out.println("Row selection changed");
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow != -1) {
                        int modelRow = table.convertRowIndexToModel(selectedRow);
                        rowData = (Vector<Object>) model.getDataVector().get(modelRow);
                        try {
                            int index = table.getColumnModel().getColumnIndex(columnSelect.toString());
                            tableRowClickBehaviour.onRowClick(rowData, index);
                        } catch (SQLException | IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }
            }
        });

        resetSortingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                sorter.setSortKeys(null);
            }
        });

        if (isSearchable) {

            customSearchBar.getField().addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    String searchText = customSearchBar.getFieldText().trim();
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

            customSearchBar.getButton().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    customSearchBar.setField("");
                    sorter.setRowFilter(null);
                }
            });
        }

    // LISTENERS METHODS //
    }

    public void setRowClickBehaviour(TableRowClickBehaviour tableRowClickBehaviour) {
        this.tableRowClickBehaviour = tableRowClickBehaviour;
    }

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

    // GETTERS AND SETTERS //
    public void setIsSearchable(boolean isSearchable) {
        customSearchBar.setVisible(isSearchable);
    }

    public JTable getTable() {
        return table;
    }

    public DefaultTableModel getModel() {
        return model;
    }

    public TableRowSorter getRowSorter() {
        return sorter;
    }
}