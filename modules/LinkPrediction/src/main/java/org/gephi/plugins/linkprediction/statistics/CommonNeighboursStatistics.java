package org.gephi.plugins.linkprediction.statistics;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gephi.graph.api.GraphFactory;
import org.gephi.graph.api.Node;
import org.gephi.plugins.linkprediction.base.LinkPredictionStatistics;
import org.gephi.plugins.linkprediction.util.Complexity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.gephi.plugins.linkprediction.statistics.CommonNeighboursStatisticsBuilder.COMMON_NEIGHBOURS_NAME;

/**
 * Class to calculate link predictions based on common neighbour algorithm.
 *
 * @author Marco Romanutti
 * @see LinkPredictionStatistics
 */
public class CommonNeighboursStatistics extends LinkPredictionStatistics {
    // Console logger
    private static Logger consoleLogger = LogManager.getLogger(CommonNeighboursStatistics.class);

    static {
        complexity = Complexity.QUADRATIC;
    }


    /**
     * Gets the name of the respective algorithm.
     *
     * @return Algorithm name
     */
    @Override public String getAlgorithmName() {
        return COMMON_NEIGHBOURS_NAME;
    }

    /**
     * Iterates over all nodes twice to initially calculate prediction values.
     *
     * @param factory Factory to create new edges
     */
    protected void calculateAll(GraphFactory factory) {
        // Iterate on all nodes for first execution
        consoleLogger.debug("Initial calculation");
        List<Node> nodesA = new ArrayList<>(Arrays.asList(graph.getNodes().toArray()));
        List<Node> nodesB = new ArrayList<>(nodesA);

        for (Node a : nodesA) {
            consoleLogger.log(Level.DEBUG, () -> "Calculation for node " + a.getId());

            // Remove self from neighbours
            nodesB.remove(a);

            // Get neighbours of a
            List<Node> aNeighbours = getNeighbours(a);

            // Calculate common neighbors
            for (Node b : nodesB) {
                // Get neighbours of b
                consoleLogger.log(Level.DEBUG, () -> "Calculation for node " + b.getId());
                List<Node> bNeighbours = getNeighbours(b);

                // Count number of neighbours
                int commonNeighboursCount = getCommonNeighboursCount(aNeighbours, bNeighbours);
                consoleLogger.log(Level.DEBUG, () -> "Number of neighbours for node " + b.getId() + ": " + commonNeighboursCount);

                // Temporary save calculated
                // value if edge does not exist
                if (isNewEdge(a, b, COMMON_NEIGHBOURS_NAME)) {
                    saveCalculatedValue(factory, a, b, commonNeighboursCount);
                }
            }
        }
    }

    /**
     * Recalculates the link prediction probability for neighbours of affected nodes.
     *
     * @param factory Factory to create new edges
     * @param a       Center node
     */
    @Override protected void recalculateProbability(GraphFactory factory, Node a) {
        consoleLogger.debug("Recalculate probability for affected nodes");
        // Get neighbours of a
        List<Node> aNeighbours = getNeighbours(a);

        // Get edges and remove
        // self from potential neighbours
        List<Node> nodesB = new ArrayList<>(Arrays.asList(graph.getNodes().toArray()));
        nodesB.remove(a);

        // Iterate over other nodes
        // that could become new neighbours
        for (Node b : nodesB) {

            // Update temporary saved values
            // if edge does not exist
            if (isNewEdge(a, b, COMMON_NEIGHBOURS_NAME)) {
                consoleLogger.log(Level.DEBUG, () -> "Calculation for edge new between " + a.getId() + " and " + b.getId());
                List<Node> bNeighbours = getNeighbours(b);
                int commonNeighboursCount = getCommonNeighboursCount(aNeighbours, bNeighbours);

                // Update saved and calculated values
                consoleLogger.log(Level.DEBUG, () -> "Update value to " + commonNeighboursCount);
                updateCalculatedValue(factory, a, b, commonNeighboursCount);
            }
        }
    }

    /**
     * Counts number of common neighbours of two nodes
     *
     * @param aNeighbours Neighbours of a
     * @param bNeighbours Neighbours of b
     * @return Number of common neighbours
     */
    private int getCommonNeighboursCount(List<Node> aNeighbours, List<Node> bNeighbours) {
        consoleLogger.debug("Get common neighbours count");
        return aNeighbours.stream().filter(bNeighbours::contains).collect(Collectors.toList()).size();
    }

}
