package com.sondertara.common.io.resource;


import com.sondertara.common.base.EmptyEvalutible;
import com.sondertara.common.base.Emptys;
import com.sondertara.common.text.StringUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * A starting location to scan from.
 */
public final class Location implements Comparable<Location>, EmptyEvalutible {

    /**
     * The prefix part of the location. Can be either classpath: or filesystem:.
     */
    @Nullable
    private String prefix;

    /**
     * The path part of the location.
     */
    @NonNull
    private String path;

    private String pathSeparator;

    public String getPathSeparator() {
        return pathSeparator;
    }

    public Location(String prefix, String path, String pathSeparator) {
        this.path = path;
        this.prefix = StringUtils.getEmptyIfNull(prefix);
        this.pathSeparator = StringUtils.useValueIfBlank(pathSeparator, "/");
    }

    public Location(String prefix, String path) {
        this(prefix, path, null);
    }

    /**
     * Return the path part of the location.
     */
    public String getPath() {
        return path;
    }

    /**
     * Return the prefix denoting classpath of filesystem.
     */
    public String getPrefix() {
        return prefix;
    }

    public String getLocation() {
        return prefix + path;
    }

    public int compareTo(Location o) {
        Objects.requireNonNull(o);
        return getLocation().compareTo(o.getLocation());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Location location = (Location) o;
        return location.getLocation().equals(this.getLocation());
    }

    @Override
    public int hashCode() {
        return getLocation().hashCode();
    }

    @Override
    public String toString() {
        return getLocation();
    }

    @Override
    public boolean isEmpty() {
        return Emptys.isEmpty(path);
    }

    @Override
    public boolean isNull() {
        return Emptys.isNull(prefix);
    }
}

