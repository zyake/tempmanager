package tempmanager.jobs;

import org.apache.log4j.Logger;

import java.util.TimerTask;
import java.util.function.Consumer;

public abstract class AbstractTimerJob extends TimerTask {

    private final Logger LOGGER = Logger.getLogger(AbstractTimerJob.class);

    private final Consumer<Runnable> trn;

    protected AbstractTimerJob(Consumer<Runnable> trn) {
        this.trn = trn;
    }

    @Override
    public void run() {
        LOGGER.info("Start job...: " + getClass().getCanonicalName());
        trn.accept(() -> {
            runInternal();
        });
        LOGGER.info("End job...: " + getClass().getCanonicalName());
    }

    protected abstract void runInternal();
}
