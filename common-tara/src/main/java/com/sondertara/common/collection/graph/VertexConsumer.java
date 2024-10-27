package com.sondertara.common.collection.graph;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public interface VertexConsumer<T> {
    void accept(@NonNull Graph<T> graph, @NonNull Vertex<T> vertex, @Nullable Edge<T> edge);
}
