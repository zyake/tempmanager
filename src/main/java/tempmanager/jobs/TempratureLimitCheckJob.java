package tempmanager.jobs;

import org.apache.log4j.Logger;
import tempmanager.mail.MailSender;
import tempmanager.models.TempratureRepository;
import tempmanager.models.TempratureStatus;

import java.util.Properties;
import java.util.function.Consumer;

public class TempratureLimitCheckJob extends AbstractTimerJob {

    private static final Logger LOGGER = Logger.getLogger(TempratureLimitCheckJob.class);

    private final TempratureRepository repository;

    private final Properties serverConfig;

    private final static String DECRYPT_KEY = "4618f037-8b8e-47c3-b23f-37c762a8cc61";

    public TempratureLimitCheckJob(Consumer<Runnable> trn, TempratureRepository repository, Properties serverConfig) {
        super(trn);
        this.repository = repository;
        this.serverConfig = serverConfig;
    }

    @Override
    protected void runInternal() {
        LOGGER.info("start checking...");

        TempratureStatus status = repository.getTempratureStatus();
        if (Float.parseFloat(status.getTemprature()) > status.getTempLimit()) {
            LOGGER.warn(String.format("temprature limit exceeded!: %s, %d", status.getTemprature(), status.getTempLimit()));

            String password = repository.getPassword(DECRYPT_KEY);
            MailSender mailSender = new MailSender();
            String msg = String.format("Temprature warning! \r\n current temprature: %s\r\n temprature limit: %d", status.getTemprature(), status.getTempLimit());
            mailSender.sendEmail(serverConfig, "zyake.mk4@gmail.com","zyake.mk4@gmail.com", password, "Temprature Warning", msg);
        }
        LOGGER.info("End checking.");

    }
}
