package tempmanager.jobs;

import org.apache.log4j.Logger;

import java.util.TimerTask;
import java.util.function.Consumer;

public abstract class AbstractTimerJob implements Runnable {

    private final Logger LOGGER = Logger.getLogger(AbstractTimerJob.class);

    private final Consumer<Runnable> trn;

    protected AbstractTimerJob(Consumer<Runnable> trn) {
        this.trn = trn;
    }

    @Override
    public void run() {
        LOGGER.info("Start job...: " + getClass().getCanonicalName());
        trn.accept(() -> {
            try {
                runInternal();
            } catch(Exception ex) {
                LOGGER.warn("execution failed!", ex);
            }
        });
        LOGGER.info("End job...: " + getClass().getCanonicalName());
    }

    protected abstract void runInternal();
}
