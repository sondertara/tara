package com.sondertara.common.collection.graph.traverser;

import com.sondertara.common.collection.graph.Edge;
import com.sondertara.common.collection.graph.Graph;
import com.sondertara.common.collection.graph.Graphs;
import com.sondertara.common.collection.graph.Vertex;
import com.sondertara.common.collection.graph.VertexConsumer;
import com.sondertara.common.collection.graph.VisitStatus;

import java.util.LinkedList;
import java.util.Map;

/**
 * 广度优先遍历，先从最高层遍历，逐层遍历
 *
 * @param <T>
 */
public class BreadthFirstGraphTraverser<T> extends AbstractGraphTraverser<T> {

    @Override
    protected void traverse(Map<String, VisitStatus> visitStatusMap, Graph<T> graph, Vertex<T> vertex, Edge<T> edge, VertexConsumer<T> consumer) {
        LinkedList<Vertex<T>> q = new LinkedList<Vertex<T>>();

        q.add(vertex);
        doVisit(visitStatusMap, graph, vertex, edge, consumer);

        while (!q.isEmpty()) {
            vertex = q.removeFirst();
            for (int i = 0; i < vertex.getOutgoingEdgeCount(); i++) {
                Edge<T> e = vertex.getOutgoingEdge(i);
                Vertex<T> to = e.getTo();
                if (Graphs.isNotVisited(visitStatusMap, to.getName())) {
                    q.add(to);
                    doVisit(visitStatusMap, graph, to, e, consumer);
                }

            }
        }
    }
}
