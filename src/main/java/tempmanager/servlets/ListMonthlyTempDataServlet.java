package tempmanager.servlets;

import tempmanager.services.TempratureService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.function.Consumer;

public class ListMonthlyTempDataServlet extends ExtraHttpServlet {

    private final Consumer<Runnable> trn;

    private final TempratureService temPService;

    public ListMonthlyTempDataServlet(Consumer<Runnable> trn, TempratureService temPService) {
        this.trn = trn;
        this.temPService = temPService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        getHeaderOutputter().writeNoCache(resp);
        int year = getAccessor().getYear(req, "year");
        int month = getAccessor().getMonth(req, "month");
        trn.accept(() -> {
            getHeaderOutputter().writeCsvDownloadHeader(resp, year + "-" + month + ".csv");
            try {
                temPService.listMonthlyTempData(year, month, resp.getWriter());
                resp.getWriter().flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
