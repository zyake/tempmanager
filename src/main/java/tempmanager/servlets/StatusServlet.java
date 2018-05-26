package tempmanager.servlets;

import tempmanager.models.RecordStatus;
import tempmanager.models.StatusResult;
import tempmanager.services.TempratureService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.function.Consumer;

public class StatusServlet extends HttpServlet {

    private final Consumer<Runnable> trns;

    private final TempratureService statusService;

    public StatusServlet(Consumer<Runnable> trns, TempratureService statusService) {
        this.trns = trns;
        this.statusService = statusService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        trns.accept(() -> {
            StatusResult status = statusService.TempratureStatus();
            req.setAttribute("timezone", status.GetTimezone());
            req.setAttribute("status", status.getStatus());
            req.setAttribute("histories", status.getHisoties());

            RecordStatus recordStatus = statusService.getRecordStatus();
            req.setAttribute("recordTotal", recordStatus.getRecordCount());
            req.setAttribute("todayTotal", recordStatus.getTodayRecordCount());
        });

        req.getRequestDispatcher("/status.jsp").forward(req, resp);
    }
}
