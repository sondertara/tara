package com.sondertara.common.lifecycle;

import com.sondertara.common.event.EventListener;

/**
 * Listener.
 * A listener for Lifecycle events.
 */
interface StatefulEventListener extends EventListener {
    void lifecycleStarting(StatefulLifecycle lifecycle);

    void lifecycleStarted(StatefulLifecycle lifecycle);

    void lifecycleFailure(StatefulLifecycle lifecycle, Throwable cause);

    void lifecycleStopping(StatefulLifecycle lifecycle);

    void lifecycleStopped(StatefulLifecycle lifecycle);
}