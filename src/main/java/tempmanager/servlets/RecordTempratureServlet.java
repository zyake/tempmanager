package tempmanager.servlets;

import tempmanager.services.TempratureService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.function.Consumer;

public class RecordTempratureServlet extends HttpServlet {

    private final Consumer<Runnable> trn;

    private final TempratureService service;

    public RecordTempratureServlet(Consumer<Runnable> trn, TempratureService service) {
        this.trn = trn;
        this.service = service;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        trn.accept(()-> {
            try {
                service.recordTemprature(req.getReader().readLine());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
