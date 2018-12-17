package tempmanager.servlets;

import tempmanager.services.TempratureService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.function.Consumer;

public class RecordTempratureServlet extends HttpServlet {

    private final TempratureService service;

    public RecordTempratureServlet(TempratureService service) {
        this.service = service;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try {
            service.recordTemprature(req.getReader().readLine());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
