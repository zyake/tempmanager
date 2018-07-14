package tempmanager.servlets.response;

import javax.servlet.http.HttpServletResponse;

public class ResponseHeaderOutputter {

    public void writeCsvDownloadHeader(HttpServletResponse resp, String filename) {
        resp.setHeader("Content-Encoding", "UTF-8");
        resp.setContentType("text/csv; charset=UTF-8");
        resp.setHeader("Content-Disposition","inline; filename=temprature" + filename);
    }

    public void writeNoCache(HttpServletResponse resp) {
        resp.setHeader("Cache-Control", "no-cache");
    }
}
