package tempmanager.servlets;

import tempmanager.services.TempratureService;
import tempmanager.servlets.request.ParameterAccessor;
import tempmanager.servlets.response.ResponseHeaderOutputter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
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
        int year = getAccessor().getYear(req, "year");
        int month = getAccessor().getMonth(req, "month");
        trn.accept(() -> {
            getHeaderOutputter().writeCsvDownloadHeader(resp, year + "-" + month + ".csv");
            try {
                temPService.listMonthlyTempData(year, month, resp.getOutputStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
