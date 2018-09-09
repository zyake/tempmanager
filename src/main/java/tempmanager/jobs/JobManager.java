package tempmanager.jobs;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class JobManager implements AutoCloseable {

    private final ScheduledExecutorService service;

    private final int delayInterval;

    public JobManager(int delayInterval, int threadSize) {
        this.service = Executors.newScheduledThreadPool(threadSize);
        this.delayInterval = delayInterval;
    }

    public JobManager put(AbstractTimerJob job) {
        service.scheduleAtFixedRate(job, delayInterval, delayInterval, TimeUnit.MINUTES);
        return this;
    }

    @Override
    public void close() throws Exception {
        service.shutdownNow();
        service.awaitTermination(delayInterval,TimeUnit.MINUTES);
    }
}
