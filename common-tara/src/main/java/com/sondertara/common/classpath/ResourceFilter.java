package com.sondertara.common.classpath;

import com.sondertara.common.io.resource.Location;

import java.util.function.Predicate;

/**
 * Filter predicate to determine which scanned resources should be returned.
 */
public interface ResourceFilter extends Predicate<Location> {
    /**
     * Return true if this resource should be included in the scan result.
     */
    @Override
    boolean test(Location resourceLocation);
}
