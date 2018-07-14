package tempmanager.servlets.request;

import javax.servlet.ServletRequest;

public class ParameterAccessor {

    public int getMonth(ServletRequest req, String param) {
        if (req.getParameter(param) == null) {
            throw new RuntimeException("The parameter month must exist.");
        }
        int month = Integer.parseInt(req.getParameter(param));
        if (month < 1 || month > 12) {
            throw new RuntimeException("The parameter month must be between 1 and 12: " + month);
        }
        return month;
    }

    public int getYear(ServletRequest req, String param) {
        if (req.getParameter(param) == null) {
            throw new RuntimeException("The parameter year must exist.");
        }
        int year = Integer.parseInt(req.getParameter(param));
        return year;
    }
}
