package com.sondertara.common.io.resource;

import com.sondertara.common.base.Provider;

public interface ResourceLocationProvider<ID> extends Provider<ID, Location> {
    @Override
    Location get(ID resourceId);
}
