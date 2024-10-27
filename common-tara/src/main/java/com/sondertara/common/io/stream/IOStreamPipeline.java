package com.sondertara.common.io.stream;

import com.sondertara.common.base.ObjectUtils;
import com.sondertara.common.collection.CollectionUtils;
import com.sondertara.common.collection.Lists;
import com.sondertara.common.collection.StreamUtils;
import com.sondertara.common.function.Consumer4;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class  IOStreamPipeline {
    private List<IOStreamInterceptor> interceptors = new ArrayList<>();

    public void addFirst(IOStreamInterceptor interceptor) {
        interceptors.add(0, interceptor);
    }

    public void addLast(IOStreamInterceptor interceptor) {
        interceptors.add(interceptor);
    }

    public void beforeWrite(OutputStream outputStream, final byte[] b, final int off, final int len) {
        if (ObjectUtils.isNotEmpty(this.interceptors)) {
            boolean continueExecute = true;
            for (int i = 0; continueExecute && i < interceptors.size(); i++) {
                IOStreamInterceptor interceptor = this.interceptors.get(i);
                continueExecute = interceptor.beforeWrite(outputStream, b, off, len);
            }
        }
    }

    public void afterWrite(OutputStream outputStream, final byte[] b, final int off, final int len) {
        if (ObjectUtils.isNotEmpty(this.interceptors)) {
            boolean continueExecute = true;
            for (int i = 0; continueExecute && i < interceptors.size(); i++) {
                IOStreamInterceptor interceptor = this.interceptors.get(i);
                continueExecute = interceptor.afterWrite(outputStream, b, off, len);
            }
        }
    }

    public void beforeRead(final InputStream inputStream, final byte[] b, final int off, final int len) {
        if (ObjectUtils.isNotEmpty(this.interceptors)) {
            boolean continueExecute = true;
            for (int i = 0; continueExecute && i < interceptors.size(); i++) {
                IOStreamInterceptor interceptor = this.interceptors.get(i);
                continueExecute = interceptor.beforeRead(inputStream, b, off, len);
            }
        }
    }

    public void afterRead(final InputStream inputStream, final byte[] b, final int off, final int len) {
        if (ObjectUtils.isNotEmpty(this.interceptors)) {
            boolean continueExecute = true;
            for (int i = 0; continueExecute && i < interceptors.size(); i++) {
                IOStreamInterceptor interceptor = this.interceptors.get(i);
                continueExecute = interceptor.afterRead(inputStream, b, off, len);
            }
        }
    }

    public static IOStreamPipeline of(IOStreamInterceptor... interceptors) {
        return of(Lists.asList(interceptors));
    }

    public static IOStreamPipeline of(List<? extends IOStreamInterceptor> interceptors) {
        final IOStreamPipeline pipeline = new IOStreamPipeline();
        CollectionUtils.forEach(interceptors, new Consumer<IOStreamInterceptor>() {
            @Override
            public void accept(IOStreamInterceptor interceptor) {
                pipeline.addLast(interceptor);
            }
        });
        return pipeline;
    }

    public static IOStreamPipeline ofInputStreamConsumers(List<Consumer4<InputStream, byte[], Integer, Integer>> consumers) {
        return IOStreamPipeline.of(StreamUtils.of(consumers).map(ConsumerToInputStreamInterceptorAdapter.consumerToInterceptorMapper).collect(Collectors.toList()));
    }

    public static IOStreamPipeline ofOutputStreamConsumers(List<Consumer4<OutputStream, byte[], Integer, Integer>> consumers) {
        return IOStreamPipeline.of(consumers.stream().map(ConsumerToOutputStreamInterceptorAdapter.consumerToInterceptorMapper).collect(Collectors.toList()));
    }
}
