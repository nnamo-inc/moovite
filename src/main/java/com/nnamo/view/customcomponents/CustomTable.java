package com.nnamo.view.customcomponents;

import com.nnamo.interfaces.TableClickListener;

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
import java.sql.SQLException;
import java.util.Vector;

public class CustomTable extends JPanel {

    JScrollPane scrollPane;
    JTable table;
    String[] tableColumns;
    DefaultTableModel model;
    TableRowSorter sorter;
    Vector<Object> rowData;

    TableClickListener tableClickListener;

    JButton resetSortingButton = new JButton("Reset Sorting");
    SearchBar searchBar = new SearchBar();
    boolean isSearchable;

    public CustomTable(String[] tableColumns, boolean isSearchable) {
        super(new BorderLayout());
        this.isSearchable = isSearchable;

        if (isSearchable) {
            add(searchBar, BorderLayout.NORTH);
        } else {
            searchBar.setVisible(false);
        }

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


        add(resetSortingButton, BorderLayout.SOUTH);
        initListeners();
    }

    // GETTERS AND SETTERS //
    public void initListeners() {

        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && tableClickListener != null) {
                    System.out.println("Row selection changed");
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow != -1) {
                        int modelRow = table.convertRowIndexToModel(selectedRow);
                        rowData = (Vector<Object>) model.getDataVector().get(modelRow);
                        try {
                            tableClickListener.onRowClick(rowData);
                        } catch (SQLException ex) {
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
                table.clearSelection();
            }
        });

        if (isSearchable) {

            searchBar.getSearchField().addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    String searchText = searchBar.getText().trim();
                    if (searchText.isEmpty()) {
                        sorter.setRowFilter(null);
                    } else {
                        sorter.setRowFilter(RowFilter.regexFilter("^" + searchText, 0));
                    }
                }
            });

            searchBar.getSearchButton().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    searchBar.setText("");
                    sorter.setRowFilter(null);
                }
            });
        }
    }

    public void setTableClickListener(TableClickListener tableClickListener) {
        this.tableClickListener = tableClickListener;
    }

    public DefaultTableModel getModel() {
        return model;
    }

    public TableRowSorter getRowSorter() {
        return sorter;
    }

    public JTable getTable() {
        return table;
    }

    public void clear() {
        model.setRowCount(0);
    }

    public void addRow(Object[] rowData) {
        model.addRow(rowData);
    }
}