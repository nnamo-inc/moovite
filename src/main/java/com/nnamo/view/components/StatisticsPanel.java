package com.nnamo.view.components;

import com.nnamo.enums.RealtimeMetricType;
import com.nnamo.models.RealtimeMetricModel;
import com.nnamo.services.RealtimeGtfsService;
import com.nnamo.view.customcomponents.CustomGbc;
import com.nnamo.view.customcomponents.CustomTitle;
import com.nnamo.view.customcomponents.statistic.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jspecify.annotations.NonNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StatisticsPanel extends JPanel {
    private final JPanel tileContainer = new JPanel();
    private final CustomTitle title;
    private final ArrayList<StatisticUnit> statisticTiles = new ArrayList<>();
    private static final int TILE_WIDTH = 120;
    private static final int TILE_HEIGHT = 80;
    private static final int GAP = 10;

    private final StatisticTotalBus statBusTile = new StatisticTotalBus();
    private final StatisticEarlyBus statEarlyBusTile = new StatisticEarlyBus();
    private final StatisticLateBus statLateBusTile = new StatisticLateBus();
    private final StatisticPunctualBus statPunctualBusTile = new StatisticPunctualBus();
    private final StatisticStoppedBus statStoppedBusTile = new StatisticStoppedBus();
    private final StatisticDetourBus statDetourBusTile = new StatisticDetourBus();
    private final JFreeChart chart;

    public StatisticsPanel() {
        super();
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        title = new CustomTitle("Statistics");
        add(title, new CustomGbc().setPosition(0, 0)
                .setAnchor(GridBagConstraints.NORTH)
                .setInsets(5, 5, 5, 5));

        // Use GridBagLayout for the tile container for better control
        tileContainer.setLayout(new GridBagLayout());
        tileContainer.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JScrollPane scrollPane = new JScrollPane(tileContainer);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, new CustomGbc().setPosition(0, 1)
                .setFill(GridBagConstraints.BOTH)
                .setWeight(1.0, 1.0)
                .setInsets(5, 5, 5, 5));

        // Add component listener to both the panel and the tile container
        ComponentAdapter resizeListener = new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                SwingUtilities.invokeLater(() -> relayoutTiles());
            }
        };

        addComponentListener(resizeListener);
        tileContainer.addComponentListener(resizeListener);

        this.addStatisticTile(statBusTile);
        this.addStatisticTile(statEarlyBusTile);
        this.addStatisticTile(statLateBusTile);
        this.addStatisticTile(statPunctualBusTile);
        this.addStatisticTile(statStoppedBusTile);
        this.addStatisticTile(statDetourBusTile);

        this.chart = ChartFactory.createTimeSeriesChart(
                "Historical Data",
                "Date",
                "Count",
                null, // No dataset for now
                true, // Show legend
                true, // Tooltips
                false // URLs
        );

        chart.setBackgroundPaint(Color.WHITE);
        chart.setBorderPaint(Color.LIGHT_GRAY);
        chart.setBorderStroke(new BasicStroke(1.0f));
        chart.setBorderVisible(true);
        chart.setPadding(new RectangleInsets(5, 5, 5, 5));
        chart.setTitle(new TextTitle("Historical Data", new Font("Arial", Font.BOLD, 16)));

        chart.getPlot().setBackgroundPaint(Color.WHITE);

        // add chart to the panel
        JPanel chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Create ChartPanel with tooltips enabled
        ChartPanel jfreeChartPanel = new org.jfree.chart.ChartPanel(chart);
        jfreeChartPanel.setMouseWheelEnabled(true);
        jfreeChartPanel.setDomainZoomable(true);
        jfreeChartPanel.setRangeZoomable(true);

        chartPanel.add(jfreeChartPanel, BorderLayout.CENTER);
        add(chartPanel, new CustomGbc().setPosition(0, 2)
                .setFill(GridBagConstraints.BOTH)
                .setWeight(1.0, 0.5)
                .setInsets(5, 5, 5, 5));

        // Also listen to the scroll pane viewport changes
        scrollPane.getViewport().addChangeListener(e -> SwingUtilities.invokeLater(() -> relayoutTiles()));

        setVisible(false);
    }

    public void addStatisticTile(StatisticUnit tile) {
        if (tile == null) {
            throw new IllegalArgumentException("Tile cannot be null");
        }

        statisticTiles.add(tile);
        relayoutTiles();
    }

    /**
     * Relayouts the statistic tiles in the container based on the current width of the container.
     * It calculates the number of columns that can fit and arranges the tiles accordingly.
     * The "TotalBus" tile spans 3 columns if there is enough space.
     *
     * @author AI
     */
    private void relayoutTiles() {
        tileContainer.removeAll();

        if (statisticTiles.isEmpty()) {
            tileContainer.revalidate();
            tileContainer.repaint();
            return;
        }

        // Get the actual available width from the viewport
        int containerWidth = tileContainer.getParent() instanceof JViewport ?
                tileContainer.getParent().getWidth() : tileContainer.getWidth();

        // Fallback to parent panel width if viewport width is not available
        if (containerWidth <= 0) {
            containerWidth = getWidth() - 40; // Account for borders, padding, and potential scrollbar
        }

        // Ensure minimum width
        containerWidth = Math.max(containerWidth, TILE_WIDTH + GAP);

        // Calculate how many columns can fit
        int maxColumns = Math.max(1, (containerWidth + GAP) / (TILE_WIDTH + GAP));

        int row = 0;
        int col = 0;

        for (StatisticUnit tile : statisticTiles) {
            Dimension tileSize;
            int gridWidth = 1;

            // Check if this is TotalBus and if we have space for 3 columns
            if (tile instanceof StatisticTotalBus && maxColumns >= 3 && col == 0) {
                // Only span 3 columns if we're at the start of a row and have enough space
                gridWidth = Math.min(3, maxColumns);
                tileSize = new Dimension(TILE_WIDTH * gridWidth + GAP * (gridWidth - 1), TILE_HEIGHT);
            } else {
                tileSize = new Dimension(TILE_WIDTH, TILE_HEIGHT);
            }

            tile.setPreferredSize(tileSize);
            tile.setMinimumSize(tileSize);
            tile.setMaximumSize(tileSize);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = col;
            gbc.gridy = row;
            gbc.gridwidth = gridWidth;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.insets = new Insets(GAP / 2, GAP / 2, GAP / 2, GAP / 2);
            gbc.weightx = gridWidth; // Give more weight to wider tiles
            gbc.weighty = 0;

            tileContainer.add(tile, gbc);

            col += gridWidth;

            // Move to next row if we've filled this one
            if (col >= maxColumns) {
                row++;
                col = 0;
            }
        }

        // Add a filler component to push everything to the top
        GridBagConstraints fillerGbc = new GridBagConstraints();
        fillerGbc.gridx = 0;
        fillerGbc.gridy = row + 1;
        fillerGbc.gridwidth = GridBagConstraints.REMAINDER;
        fillerGbc.fill = GridBagConstraints.BOTH;
        fillerGbc.weighty = 1.0;
        tileContainer.add(Box.createVerticalGlue(), fillerGbc);

        tileContainer.revalidate();
        tileContainer.repaint();
    }

    public void setupListeners(RealtimeGtfsService realtimeGtfsService) {
        if (realtimeGtfsService == null) {
            throw new IllegalArgumentException("Listener cannot be null");
        }

        for (StatisticUnit tile : statisticTiles) {
            realtimeGtfsService.addListener(tile);
        }
    }

    private void setupMetricsListener(@NonNull StatisticUpdateListener listener) {
        for (StatisticUnit tile : statisticTiles) {
            tile.addStatisticUpdateListener(listener);
        }
    }

    /**
     * Sets up the data for all statistic tiles and initializes the {@link JFreeChart} chart with historical data.
     * It populates the chart. It also sets up a listener to update the chart in real-time.
     *
     * @param metricsMap a map containing lists of {@link RealtimeMetricModel} for each {@link RealtimeMetricType}
     * @author Davide Galilei
     */
    public void updateView(Map<RealtimeMetricType, List<RealtimeMetricModel>> metricsMap) {
        // get metrics from the db and paint the chart
        try {
            TimeSeriesCollection dataset = new TimeSeriesCollection();

            for (RealtimeMetricType type : metricsMap.keySet()) {
                String name = type.name().toLowerCase();
                TimeSeries series = new TimeSeries(name);
                List<RealtimeMetricModel> metrics = metricsMap.get(type);
                for (RealtimeMetricModel metric : metrics) {
                    series.add(new Second(metric.getCreatedAt()), metric.getValue());
                }
                dataset.addSeries(series);
            }

            chart.getXYPlot().setDataset(dataset);

            // Customize the renderer for thicker and brighter lines for each series
            XYPlot plot = chart.getXYPlot();
            XYLineAndShapeRenderer renderer = getXyLineAndShapeRenderer(dataset);

            plot.setRenderer(renderer);

            setupMetricsListener(
                    (type, value) -> {
                        // Update the chart with the new value
                        TimeSeries series = dataset.getSeries(type.name().toLowerCase());
                        if (series != null) {
                            series.addOrUpdate(new Second(), value);
                        } else {
                            series = new TimeSeries(type.name().toLowerCase());
                            series.add(new Second(), value);
                            dataset.addSeries(series);
                        }
                        chart.fireChartChanged(); // Refresh the chart
                    }
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates and configures an {@link XYLineAndShapeRenderer} for rendering time series data with thick lines and bright colors.
     * Each series in the provided dataset will be assigned a distinct color and a thick stroke for better visibility.
     *
     * @param dataset the {@link TimeSeriesCollection} containing the time series data to be rendered
     * @return a configured {@link XYLineAndShapeRenderer} for rendering the time series data
     * @author Davide Galilei
     * @see XYLineAndShapeRenderer
     * @see TimeSeriesCollection
     */
    private static XYLineAndShapeRenderer getXyLineAndShapeRenderer(TimeSeriesCollection dataset) {
        // Enable both lines and shapes (dots) - true, true
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, true);

        // Define bright colors for each series
        Color[] brightColors = {
                new Color(255, 69, 0),    // Red-Orange
                new Color(0, 191, 255),   // Deep Sky Blue
                new Color(50, 205, 50),   // Lime Green
                new Color(255, 20, 147),  // Deep Pink
                new Color(255, 215, 0),   // Gold
                new Color(138, 43, 226),  // Blue Violet
                new Color(255, 140, 0),   // Dark Orange
                new Color(0, 255, 127)    // Spring Green
        };

        // Apply thick strokes and bright colors to each series
        for (int i = 0; i < dataset.getSeriesCount(); i++) {
            renderer.setSeriesStroke(i, new BasicStroke(3.0f)); // Thick line (3.0f width)
            renderer.setSeriesPaint(i, brightColors[i % brightColors.length]);
            // Set shape size for the dots
            renderer.setSeriesShape(i, new java.awt.geom.Ellipse2D.Double(-3, -3, 6, 6));
        }
        return renderer;
    }

    public void setMetricCollector(MetricCollector collector) {
        statBusTile.setMetricCollector(collector);
        statEarlyBusTile.setMetricCollector(collector);
        statLateBusTile.setMetricCollector(collector);
        statPunctualBusTile.setMetricCollector(collector);
        statStoppedBusTile.setMetricCollector(collector);
        statDetourBusTile.setMetricCollector(collector);
    }
}
