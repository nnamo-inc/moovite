package com.nnamo.view.customcomponents;

import com.nnamo.interfaces.TableRowClickListener;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.Vector;

public class CustomTable extends JPanel {

    String[] tableColumns;
    DefaultTableModel model;
    TableRowSorter sorter;
    JTable table;
    JScrollPane scrollPane;
    Vector<Object> rowData;
    TableRowClickListener tableRowClickListener;

    public CustomTable(String[] tableColumns) {
        super(new BorderLayout());

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

        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && tableRowClickListener != null) {
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow != -1) {
                        int modelRow = table.convertRowIndexToModel(selectedRow);
                        rowData = (Vector<Object>) model.getDataVector().get(modelRow);
                        tableRowClickListener.onRowClick(rowData);
                    }
                }
            }
        });
    }

    // GETTERS AND SETTERS //
    public DefaultTableModel getModel() {
        return model;
    }

    public TableRowSorter getRowSorter() {
        return sorter;
    }

    public void setTableRowClickListener(TableRowClickListener tableRowClickListener) {
        this.tableRowClickListener = tableRowClickListener;
    }

    public Vector<Object> getRowData() {
        return rowData;
    }

    public JTable getTable() {
        return table;
    }
}