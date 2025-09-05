package com.acme.pfm.concurrency;

import java.util.concurrent.*;

public final class ExecutorsProvider {
    private static final int CORES = Math.max(1, Runtime.getRuntime().availableProcessors());

    // IO-heavy pool: 2x cores, bounded queue
    public static ExecutorService ioPool(int maxQueue) {
        return new ThreadPoolExecutor(
                CORES * 2, CORES * 2,
                30L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(maxQueue),
                new ThreadPoolExecutor.CallerRunsPolicy() // backpressure
        );
    }

    private ExecutorsProvider() {
    }
}
