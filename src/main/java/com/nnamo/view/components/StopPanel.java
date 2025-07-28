package com.nnamo.view.components;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import com.nnamo.enumeration.PreferButtonState;
import com.nnamo.interfaces.FavoriteLineBehaviour;
import com.nnamo.interfaces.FavoriteStopBehaviour;
import com.nnamo.interfaces.TableRowClickListener;
import com.nnamo.models.RouteModel;
import com.nnamo.models.StopTimeModel;
import com.nnamo.models.TripModel;
import com.nnamo.view.customcomponents.GbcCustom;
import com.nnamo.view.customcomponents.InfoBar;
import com.nnamo.view.customcomponents.SearchBar;
import com.nnamo.view.customcomponents.CustomTable;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

public class StopPanel extends JPanel {
    // Stop info components
    private final InfoBar nomeFermata = new InfoBar("Nome fermata: ");
    private final InfoBar idFermata = new InfoBar("ID fermata: ");
    // Bus info components
    private final InfoBar busInArrivo = new InfoBar("Autobus in arrivo: ");
    private final InfoBar statoBusInArrivo = new InfoBar("Stato autobus in arrivo: ");
    private final InfoBar numeroPosti = new InfoBar("Numero posti: ");
    // Route info components
    private CustomTable table;
    private SearchBar searchBar;
    // Prefer components
    private JButton buttonPreferStop = new JButton("Stop");
    private PreferButtonState buttonPreferStopState = PreferButtonState.ADDMODE;
    private JButton buttonPreferRoute = new JButton("- -");
    private PreferButtonState buttonPreferRouteState = PreferButtonState.ADDMODE;

    private boolean favorite;

    private FavoriteStopBehaviour favStopBehaviour;
    private FavoriteLineBehaviour favLineBehaviour;
    private TableRowClickListener tableRowClickListener;

    // CONSTRUCTOR //
    public StopPanel() {
        super();
        // set the layout, create the border and the gbc, set the background color
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createLineBorder(new Color(60, 63, 65), 2));
        setBackground(new Color(60, 63, 65));

        // Stop info panel
        JPanel PanelStopInfo = newStopInfoPanel();
        add(PanelStopInfo, new GbcCustom().setPosition(0, 0).setWeight(1.0, 1.0)
                .setFill(GridBagConstraints.HORIZONTAL).setInsets(10, 10, 5, 5));

        // Bus info panel
        JPanel PanelbusInfo = newBusInfoPanel();
        add(PanelbusInfo, new GbcCustom().setPosition(0, 1).setWeight(1.0, 1.0)
                .setFill(GridBagConstraints.HORIZONTAL).setInsets(10, 10, 5, 5));

        // Route info panel
        JPanel tableRouteInfo = newRouteInfoPanel();
        TitledBorder titledBorder = new TitledBorder(new LineBorder(Color.lightGray, 2), "Tabella corse");
        tableRouteInfo.setBorder(titledBorder);
        add(tableRouteInfo, new GbcCustom().setPosition(1, 0).setWeight(1.0, 1.0).setHeight(2)
                .setFill(GridBagConstraints.BOTH).setInsets(10, 10, 5, 5));

        // Buttons prefer
        JPanel PanelPrefer = newPanelPrefer();
        add(PanelPrefer, new GbcCustom().setPosition(2, 0).setWeight(1.0, 1.0).setHeight(2)
                .setFill(GridBagConstraints.BOTH).setInsets(10, 10, 5, 5));

        // inizialize the listeners
        initListener();
        setVisible(false);
    }

    // METHODS //
    private JPanel newStopInfoPanel() {
        // Panel info stop with border
        JPanel infoStop = new JPanel(new GridBagLayout());
        TitledBorder titledBorder = new TitledBorder(new LineBorder(Color.lightGray, 2), "Informazioni fermata");
        infoStop.setBorder(titledBorder);

        // label name
        infoStop.add(nomeFermata,
                new GbcCustom().setPosition(0, 0).setWeight(1.0, 1.0).setAnchor(GridBagConstraints.EAST)
                        .setFill(GridBagConstraints.HORIZONTAL).setInsets(10, 10, 10, 10));

        // text name
        infoStop.add(idFermata, new GbcCustom().setPosition(0, 1).setWeight(1.0, 1.0).setAnchor(GridBagConstraints.WEST)
                .setFill(GridBagConstraints.HORIZONTAL).setInsets(10, 10, 10, 10));

        return infoStop;
    }

    private JPanel newBusInfoPanel() {
        // Info bus panel with border
        JPanel infoBus = new JPanel(new GridBagLayout());
        TitledBorder titledBorder = new TitledBorder(new LineBorder(Color.lightGray, 2), "Informazioni bus");
        infoBus.setBorder(titledBorder);

        // label bus
        infoBus.add(busInArrivo,
                new GbcCustom().setPosition(0, 0).setWeight(1.0, 1.0).setAnchor(GridBagConstraints.EAST)
                        .setFill(GridBagConstraints.HORIZONTAL).setInsets(10, 10, 10, 10));

        // text bus
        infoBus.add(statoBusInArrivo,
                new GbcCustom().setPosition(0, 1).setWeight(1.0, 1.0).setAnchor(GridBagConstraints.EAST)
                        .setFill(GridBagConstraints.HORIZONTAL).setInsets(10, 10, 10, 10));

        // label state
        infoBus.add(numeroPosti,
                new GbcCustom().setPosition(0, 2).setWeight(1.0, 1.0).setAnchor(GridBagConstraints.EAST)
                        .setFill(GridBagConstraints.HORIZONTAL).setInsets(10, 10, 10, 10));

        return infoBus;
    }

    private JPanel newRouteInfoPanel() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setPreferredSize(new Dimension(100, 400));
        this.table = new CustomTable(new String[] { "Autobus", "Orario Arrivo", "Stato", "In ritardo" });

        // Search bar
        mainPanel.add(searchBar = new SearchBar(),
                new GbcCustom().setPosition(0, 0).setWeight(1.0, 0).setAnchor(GridBagConstraints.CENTER)
                        .setFill(GridBagConstraints.HORIZONTAL).setInsets(2, 5, 2, 5));

        // Table
        mainPanel.add(table, new GbcCustom().setPosition(0, 1).setWeight(1.0, 1.0).setAnchor(GridBagConstraints.CENTER)
                .setFill(GridBagConstraints.BOTH).setInsets(10, 10, 10, 10));

        table.getRowSorter().setSortable(1, false);

        return mainPanel;
    }

    private JPanel newPanelPrefer() {
        JPanel panelPrefer = new JPanel(new GridBagLayout());
        TitledBorder titledBorder = new TitledBorder(new LineBorder(Color.lightGray, 2), "Preferiti");
        panelPrefer.setBorder(titledBorder);
        // button prefer
        panelPrefer.add(buttonPreferStop, new GbcCustom().setPosition(0, 0).setWeight(1.0, 1.0)
                .setFill(GridBagConstraints.BOTH).setInsets(10, 10, 10, 10).setAnchor(GridBagConstraints.CENTER));

        buttonPreferRoute.setEnabled(false);
        panelPrefer.add(buttonPreferRoute, new GbcCustom().setPosition(0, 1).setWeight(1.0, 1.0)
                .setFill(GridBagConstraints.BOTH).setInsets(10, 10, 10, 10).setAnchor(GridBagConstraints.CENTER));
        return panelPrefer;
    }

    private void initListener() {

        // Button prefer stop listener to change the state of the button and make the
        // call to add/remove the stop from favorites
        buttonPreferStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (favStopBehaviour != null) {
                    if (favorite) {
                        // favStopBehaviour.removeFavoriteStop(idFermata.getTextValue());
                    } else {
                        favStopBehaviour.addFavoriteStop(idFermata.getTextValue());
                        System.out.println("Stop " + idFermata.getTextValue() + " successfuly added to favorites");
                    }
                    setFavoriteStopFlag(!favorite);
                    return;
                }
                System.out.println("Fav Stop behaviour not implemented");
            }
        });

        // Button prefer route listener to change the state of the button and make the
        // call to add/remove the route from favorites
        // TODO need to separate the logic for adding and removing routes from favorites
        buttonPreferRoute.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (favStopBehaviour != null && buttonPreferRoute.isEnabled()) {
                    return;
                }
                System.out.println("Fav Route behaviour not implemented");
            }
        });

        // Table row click listener to enable the button and change the text
        table.setTableRowClickListener(new TableRowClickListener() {
            @Override
            public void onRowClick(Object rowData) {
                System.out.println("Row clicked: " + rowData);
                buttonPreferRoute.setEnabled(true);

                // TODO implement the logic written below

                /*
                 * if (vediSeAutobusStaTraIPreferiti(rowData)) {
                 * updateStopPanelPreferRouteButton("Rimuovi linea dai preferiti");
                 * }
                 * else {
                 * updateStopPanelPreferRouteButton("Aggiungi linea ai preferiti");
                 * }
                 */

            }
        });

        // Search bar listener for filtering the table
        searchBar.getSearchField().addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String searchText = searchBar.getText().trim();
                if (searchText.isEmpty()) {
                    table.getRowSorter().setRowFilter(null);
                } else {
                    table.getRowSorter().setRowFilter(RowFilter.regexFilter("^" + searchText, 0));
                }
            }
        });

        // Search bar button listener to clear the search field and reset the filter
        searchBar.getSearchButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchBar.setText("");
                table.getRowSorter().setRowFilter(null);
            }
        });

    }

    public void updateStopTimes(List<StopTimeModel> stopTimes) {
        DefaultTableModel model = table.getModel();
        model.setRowCount(0); // Remove previous rows
        for (StopTimeModel stopTime : stopTimes) {
            LocalTime arrivalTime = LocalTime.ofInstant(stopTime.getArrivalTime().toInstant(), ZoneId.systemDefault());
            TripModel trip = stopTime.getTrip(); // Corsa

            if (trip == null) {
                continue;
            }

            RouteModel route = trip.getRoute(); // Linea
            if (route == null || route.getShortName() == null) {
                continue;
            }

            model.addRow(new Object[] {
                    route.getShortName(),
                    arrivalTime.toString(),
                    "In Orario", // TODO: DA AGGIORNARE CON DATI IN REALTIME
                    null
            });
        }
    }

    public void updateFavoriteStopMessage(String string) {
        buttonPreferStop.setText(string);
    }

    public void updateFavoriteRouteMessage(String string) {
        buttonPreferRoute.setText(string);
    }
    // GETTERS AND SETTERS //

    public JTextField getTextID() {
        return idFermata.getJTextField();
    }

    public JTextField getTextName() {
        return nomeFermata.getJTextField();
    }

    public void setFavoriteStopFlag(boolean favorite) {
        this.favorite = favorite;

        if (favorite) {
            // TODO Implement remove route from favorites
            System.out.println("Route" + " successfuly added to favorites");
            updateFavoriteStopMessage("Rimuovi fermata dai preferiti");
        } else {
            // TODO Implement remove route from favorites
            System.out.println("Route" + " successfuly removed to favorites");
            updateFavoriteStopMessage("Aggiungi fermata dai preferiti");
        }
    }

    public void toggleFavoriteStop() {
        this.favorite = !this.favorite;
    }

    public void setFavStopBehaviour(FavoriteStopBehaviour behaviour) {
        if (behaviour != null) {
            this.favStopBehaviour = behaviour;
        }
    }

    public void setFavLineBehaviour(FavoriteLineBehaviour behaviour) {
        if (behaviour != null) {
            this.favLineBehaviour = behaviour;
        }
    }

    public boolean isRouteButtonEnabled() {
        return buttonPreferRoute.isEnabled();
    }
}
