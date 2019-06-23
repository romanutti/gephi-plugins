package org.gephi.plugins.linkprediction.evaluation;

import org.gephi.graph.api.GraphModel;
import org.gephi.plugins.linkprediction.base.EvaluationMetric;
import org.gephi.statistics.spi.Statistics;

import java.util.ArrayList;
import java.util.List;

/**
 * Macro class that triggers the evaluation calculation for all selected algorithms.
 */
public class LinkPredictionEvaluation implements Statistics {
    // List of link prediction evaluations
    private List<EvaluationMetric> evaluations = new ArrayList<>();

    /**
     * Calculates evaluation metrics for all evaluations.
     *
     */
    public void execute(GraphModel graphModel) {

        evaluations.stream().forEach(evaluation -> {
                evaluation.run();
        });
    }

    @Override public String getReport() {
        return null;
    }

    public List<EvaluationMetric> getEvaluations() {
        return evaluations;
    }

    /**
     * Get specific link prediction algorithm from evaluations list
     * @param statistic Class of searched statistic
     * @return LinkPredictionStatistic
     */
    public EvaluationMetric getEvaluation(EvaluationMetric statistic) {
        return evaluations.get(evaluations.indexOf(statistic));
    }

    public void addEvaluation(EvaluationMetric evaluation) {
        if (!evaluations.contains(evaluation)) evaluations.add(evaluation);
    }
}
