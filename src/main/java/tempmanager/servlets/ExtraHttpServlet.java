package tempmanager.servlets;

import tempmanager.servlets.request.ParameterAccessor;
import tempmanager.servlets.response.ResponseHeaderOutputter;

import javax.servlet.http.HttpServlet;

public class ExtraHttpServlet extends HttpServlet {

    private final ParameterAccessor parameterAccessor = new ParameterAccessor();

    private final ResponseHeaderOutputter headerOutputter = new ResponseHeaderOutputter();

    protected ParameterAccessor getAccessor() {
        return this.parameterAccessor;
    }

    protected ResponseHeaderOutputter getHeaderOutputter() {
        return this.headerOutputter;
    }
}
