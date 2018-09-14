package tempmanager.servlets;

import tempmanager.models.StatusResult;
import tempmanager.services.TempratureService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.function.Consumer;

public class StatusServlet extends ExtraHttpServlet {

    private final Consumer<Runnable> trns;

    private final TempratureService statusService;

    public StatusServlet(Consumer<Runnable> trns, TempratureService statusService) {
        this.trns = trns;
        this.statusService = statusService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        getHeaderOutputter().writeNoCache(resp);
        trns.accept(() -> {
            statusService.insertLogStatus(req.getHeader("User-Agent"), req.getRequestURI());

            StatusResult status = statusService.getTempratureStatus();
            req.setAttribute("timezone", status.getTimezone());
            req.setAttribute("status", status.getStatus());
            req.setAttribute("weekly", status.getTempWeekly());
            req.setAttribute("monthly", status.getTempMonthly());

            int recordTotal = statusService.getRecordTotal();
            req.setAttribute("recordTotal", recordTotal);
        });

        req.getRequestDispatcher("status.jsp").forward(req, resp);
    }
}
