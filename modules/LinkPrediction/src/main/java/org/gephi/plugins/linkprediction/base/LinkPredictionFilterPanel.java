package org.gephi.plugins.linkprediction.base;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gephi.filters.spi.Filter;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.Table;
import org.gephi.plugins.linkprediction.warnings.IllegalIterationLimitWarning;
import org.openide.util.Lookup;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

import static org.gephi.plugins.linkprediction.base.LinkPredictionStatistics.initializeColumns;

/**
 * Filter panel which will be used with {@link LinkPredictionFilter} filter.
 * <p>
 * This base factory class will be used in all specific link prediction filter
 * implementations.
 *
 * @author Marco Romanutti
 * @see LinkPredictionFilter
 */
public class LinkPredictionFilterPanel extends javax.swing.JPanel {
    /** Filter on which panel will set its values */
    private LinkPredictionFilter filter;
    /** Slider to adapt number uf shown edges */
    private javax.swing.JSlider slider;
    /** Label which shows current chosen value of shown edges */
    private javax.swing.JLabel current;

    // Console logger
    private static Logger consoleLogger = LogManager.getLogger(LinkPredictionFilterPanel.class);


    /**
     * Creates a new link prediction filter panel.
     *
     * @param filter Algorithm used to limit edges
     */
    public LinkPredictionFilterPanel(Filter filter) {
        this.filter = (LinkPredictionFilter) filter;

        // Get max value from edges table
        Graph graph = Lookup.getDefault().lookup(GraphController.class).getGraphModel().getGraph();
        consoleLogger.debug("Initialize columns");
        Table edgeTable = graph.getModel().getEdgeTable();
        initializeColumns(edgeTable);

        int maxIteration = LinkPredictionStatistics.getMaxIteration(graph, filter.getName());
        if (consoleLogger.isDebugEnabled()) {
            consoleLogger.debug("Max iteration found: " + maxIteration);
        }
        // Stats have to be executed first
        if (maxIteration == 0) {
            consoleLogger.debug("Display warning - stats have to be executed first");
            new IllegalIterationLimitWarning();
        }

        // Set layout
        consoleLogger.debug("Apply panel layout");
        setLayout(new GridLayout(2, 1));

        // Add slider
        JPanel topPanel = new JPanel(new BorderLayout());
        // Set init value
        int initValue = maxIteration / 2;
        if (consoleLogger.isDebugEnabled()) {
            consoleLogger.debug("Set slider initially to " + initValue);
        }
        this.slider = new JSlider(JSlider.HORIZONTAL, 0, maxIteration, initValue);
        topPanel.add(slider);

        // Add current slider value
        JPanel bottomPanel = new JPanel(new BorderLayout());
        // Set init value
        ((LinkPredictionFilter) filter).setEdgesLimit(initValue);
        this.current = new JLabel(String.valueOf(initValue));
        bottomPanel.add(current);

        add(topPanel);
        add(bottomPanel);

        // Add listener
        consoleLogger.debug("Add listener to slider");
        slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                int edgesLimit = ((JSlider) e.getSource()).getValue();
                if (consoleLogger.isDebugEnabled()) {
                    consoleLogger.debug("Filter changed to new limit " + edgesLimit);
                }

                // Set current displayed value
                current.setText(String.valueOf(edgesLimit));
                // Set filter value
                ((LinkPredictionFilter) filter).setEdgesLimit(edgesLimit);

            }
        });
    }
}

