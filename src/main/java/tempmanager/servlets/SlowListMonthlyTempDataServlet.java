package tempmanager.servlets;

import tempmanager.models.TempratureHistory;
import tempmanager.models.TempratureRepository;
import tempmanager.models.TempratureStatus;
import tempmanager.services.TempratureService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.function.Consumer;

public class SlowListMonthlyTempDataServlet extends ExtraHttpServlet {

    private final Consumer<Runnable> trn;

    private final TempratureService temPService;

    public SlowListMonthlyTempDataServlet(Consumer<Runnable> trn, TempratureService temPService) {
        this.trn = trn;
        this.temPService = temPService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int year = getAccessor().getYear(req, "year");
        int month = getAccessor().getMonth(req, "month");
        trn.accept(() -> {
            getHeaderOutputter().writeCsvDownloadHeader(resp, year + "-slow-" + month + ".csv");
            List<TempratureStatus> tempratureHistories = temPService.listMonthlyTempDataSlow(year, month);
            PrintWriter writer = null;
            try {
                writer = resp.getWriter();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            for (TempratureStatus  hist : tempratureHistories) {
                writer.write(hist.getRecordedTimestamp());
                writer.write(",");
                writer.write(hist.getTemprature());
                writer.write("\r\n");
            }
            writer.flush();
        });
    }
}
