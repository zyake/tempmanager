package tempmanager.jobs;

import org.apache.log4j.Logger;
import tempmanager.models.TempratureRepository;

import javax.json.Json;
import javax.json.stream.JsonParser;
import java.io.IOException;
import java.net.URL;
import java.util.function.Consumer;

public class PrefectureTempratureRecordJob extends AbstractTimerJob {
    private static final Logger LOGGER = Logger.getLogger(PrefectureTempratureRecordJob.class);

    private final TempratureRepository repository;

    protected PrefectureTempratureRecordJob(Consumer<Runnable> trn, TempratureRepository repository) {
        super(trn);
        this.repository = repository;
    }

    @Override
    protected void runInternal() {
        try {
            JsonParser parser = Json.createParser(new URL("http://api.openweathermap.org/data/2.5/forecast?id=524901&APPID=8b48c138d380cabb23293764730fd977").openStream());
            while (parser.hasNext()) {
                JsonParser.Event next = parser.next();
                switch (next) {
                    case KEY_NAME:
                }

            }
        } catch (IOException e) {
            LOGGER.warn("Whether API call failed!", e);
            return;
        }
    }
}
