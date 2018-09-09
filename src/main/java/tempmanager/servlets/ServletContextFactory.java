package tempmanager.servlets;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;

public class ServletContextFactory {

    private final ServletContext context;

    public ServletContextFactory(ServletContext context) {
        this.context = context;
    }

    public ServletContextFactory add(String path, Servlet servlet) {
        context.addServlet(path, servlet).addMapping(path);
        return this;
    }
}
