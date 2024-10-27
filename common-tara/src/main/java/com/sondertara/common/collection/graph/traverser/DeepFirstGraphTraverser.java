package com.sondertara.common.collection.graph.traverser;

import com.sondertara.common.collection.graph.Edge;
import com.sondertara.common.collection.graph.Graph;
import com.sondertara.common.collection.graph.Graphs;
import com.sondertara.common.collection.graph.Vertex;
import com.sondertara.common.collection.graph.VertexConsumer;
import com.sondertara.common.collection.graph.VisitStatus;

import java.util.Map;

/**
 * 深度优先遍历，且先子后父
 *
 * @param <T>
 */
public class DeepFirstGraphTraverser<T> extends AbstractGraphTraverser<T> {

    protected void traverse(Map<String, VisitStatus> visitStatusMap, Graph<T> graph, Vertex<T> vertex, Edge<T> edge, VertexConsumer<T> consumer) {
        for (int i = 0; i < vertex.getOutgoingEdgeCount(); i++) {
            Edge<T> e = vertex.getOutgoingEdge(i);
            if (Graphs.isNotVisited(visitStatusMap, e.getTo().getName())) {
                traverse(visitStatusMap, graph, e.getTo(), e, consumer);
            }
        }

        doVisit(visitStatusMap, graph, vertex, edge, consumer);
    }


}
