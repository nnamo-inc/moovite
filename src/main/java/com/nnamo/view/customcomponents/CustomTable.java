package com.nnamo.view.customcomponents;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;

public class CustomTable extends JPanel {

    String[] tableColumns;
    DefaultTableModel model;
    JTable table;
    TableRowSorter sorter;
    JScrollPane scrollPane;

    public CustomTable(String[] tableColumns) {
        super();
        setLayout(new GridBagLayout());

        this.tableColumns = tableColumns;
        this.model = new DefaultTableModel(tableColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        this.table = new JTable(model);

        this.scrollPane = new JScrollPane(table);
        add(scrollPane, new GbcCustom().setPosition(0, 0).setWeight(1.0, 1.0).setAnchor(GridBagConstraints.CENTER)
                .setFill(GridBagConstraints.BOTH).setInsets(2, 5, 2, 5));

        this.sorter = new TableRowSorter(model);
        table.setRowSorter(sorter);
    }

    // GETTERS AND SETTERS //
    public DefaultTableModel getModel() {
        return model;
    }

    public void setRowSorter(TableRowSorter sorter) {
        table.setRowSorter(sorter);
    }

    public TableRowSorter getRowSorter() {
        return sorter;
    }
}
