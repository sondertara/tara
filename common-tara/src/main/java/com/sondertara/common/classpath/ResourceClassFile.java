package com.sondertara.common.classpath;

import com.sondertara.common.base.Delegatable;
import com.sondertara.common.io.resource.AbstractLocatableResource;
import com.sondertara.common.io.resource.Location;
import com.sondertara.common.io.resource.Resource;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class ResourceClassFile extends ClassFile implements Delegatable<Resource> {
    @NonNull
    private Resource delegate;

    public ResourceClassFile() {
    }

    public ResourceClassFile(Resource delegate) {
        setDelegate(delegate);
    }

    @Override
    public String getAbsolutePath() {
        if (delegate instanceof AbstractLocatableResource) {
            return ((AbstractLocatableResource) delegate).getAbsolutePath();
        }
        return null;
    }

    @Override
    public Location getLocation() {
        if(delegate instanceof AbstractLocatableResource){
            return ((AbstractLocatableResource) delegate).getLocation();
        }
        return null;
    }

    @Override
    public String getPrefix() {
        if(delegate instanceof AbstractLocatableResource){
            return ((AbstractLocatableResource) delegate).getPrefix();
        }
        return null;
    }

    @Override
    public String getPath() {
        if(delegate instanceof AbstractLocatableResource){
            return ((AbstractLocatableResource) delegate).getPath();
        }
        return delegate.toString();
    }

    @Override
    public String toString() {
        return getPath();
    }

    @Override
    public Object getRealResource() {
        return delegate.getRealResource();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return delegate.getInputStream();
    }

    @Override
    public long contentLength() {
        return delegate.contentLength();
    }

    @Override
    public URL getUrl() throws IOException {
        if (delegate instanceof AbstractLocatableResource) {
            return ((AbstractLocatableResource) delegate).getUrl();
        }
        return null;
    }

    @Override
    public Resource getDelegate() {
        return delegate;
    }

    @Override
    public void setDelegate(Resource delegate) {
        this.delegate = delegate;
    }
}

