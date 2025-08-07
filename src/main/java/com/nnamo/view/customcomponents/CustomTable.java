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
import java.util.Vector;

public class CustomTable extends JPanel {

    JTable table;
    JButton resetSortingButton = new JButton("Reset Sorting");
    SearchBar searchBar = new SearchBar();

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
        checkClickRowData();
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
            add(searchBar, BorderLayout.NORTH);
        } else {
            searchBar.setVisible(false);
        }
    };

    private void checkClickRowData() {
        if (Arrays.asList(tableColumns).contains(columnSelect)) {
            this.columnSelect = columnSelect;
        } else {
            String callerClass = new Exception().getStackTrace()[1].getClassName();
            throw new IllegalArgumentException(
                    "\"" + columnSelect + "\"" + " colonna clickRowData invalida: " + callerClass + ".\n" +
                            "Colonne valide: " + Arrays.toString(tableColumns));
        }
    };

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
        searchBar.setVisible(isSearchable);
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