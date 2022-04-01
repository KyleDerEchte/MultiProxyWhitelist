package de.kyleonaut.multiproxywhitelist.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author kyleonaut
 */
public class RequestExecutor {
    private final ExecutorService executor;

    public RequestExecutor() {
        this.executor = Executors.newFixedThreadPool(32);
    }


    public void execute(Runnable runnable) {
        this.executor.submit(runnable);
    }

    public void close() {
        this.executor.shutdown();
    }
}
