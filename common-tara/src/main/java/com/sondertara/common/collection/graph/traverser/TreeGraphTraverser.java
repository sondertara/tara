package com.sondertara.common.collection.graph.traverser;

import com.sondertara.common.collection.graph.Edge;
import com.sondertara.common.collection.graph.Graph;
import com.sondertara.common.collection.graph.Graphs;
import com.sondertara.common.collection.graph.Vertex;
import com.sondertara.common.collection.graph.VertexConsumer;
import com.sondertara.common.collection.graph.VisitStatus;

import java.util.Map;

/**
 * spanningTree
 * <p>
 * 深度优先遍历，但遍历时，先遍历父节点
 *
 * @param <T>
 */
public class TreeGraphTraverser<T> extends AbstractGraphTraverser<T> {

    protected void traverse(Map<String, VisitStatus> visitStatusMap, Graph<T> graph, Vertex<T> vertex, Edge<T> edge, VertexConsumer<T> consumer) {
        doVisit(visitStatusMap, graph, vertex, edge, consumer);
        for (int i = 0; i < vertex.getOutgoingEdgeCount(); i++) {
            Edge<T> e = vertex.getOutgoingEdge(i);
            if (Graphs.isNotVisited(visitStatusMap, e.getTo().getName())) {
                traverse(visitStatusMap, graph, e.getTo(), e, consumer);
            }
        }
    }
}
