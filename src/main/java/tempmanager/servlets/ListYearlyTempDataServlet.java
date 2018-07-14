package tempmanager.servlets;

import tempmanager.services.TempratureService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.function.Consumer;

public class ListYearlyTempDataServlet extends HttpServlet {

    private final Consumer<Runnable> trn;

    private final TempratureService temPService;

    public ListYearlyTempDataServlet(Consumer<Runnable> trn, TempratureService temPService) {
        this.trn = trn;
        this.temPService = temPService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getParameter("year") == null) {
            throw new RuntimeException("The parameter year must exist.");
        }
        int year = Integer.parseInt(req.getParameter("year"));

        trn.accept(() -> {
            resp.setHeader("Content-Encoding", "UTF-8");
            resp.setContentType("text/csv; charset=UTF-8");
            resp.setHeader("Content-Disposition","inline; filename=temprature" + year + ".csv");

            try {
                temPService.listYearlyTempData(year, resp.getOutputStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
