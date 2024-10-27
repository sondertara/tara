package com.sondertara.common.pipeline.simplex;


public interface SimplexPipeline {
    void addFirst(SimplexHandler handler);

    void addLast(SimplexHandler handler) ;

     void clear();

    Object handle(Object message);

}
