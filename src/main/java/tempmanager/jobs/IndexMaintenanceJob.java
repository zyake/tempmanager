package tempmanager.jobs;

import tempmanager.models.TempratureRepository;

import java.util.function.Consumer;

public class IndexMaintenanceJob extends AbstractTimerJob {

    private final TempratureRepository repository;

    public IndexMaintenanceJob(TempratureRepository repository, Consumer<Runnable> trn) {
        super(trn);
        this.repository = repository;
    }

    @Override
    protected void runInternal() {
        repository.maintenanceIndexes();
    }
}
