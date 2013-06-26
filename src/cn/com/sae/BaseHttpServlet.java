package cn.com.sae;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

public class BaseHttpServlet extends HttpServlet {

	private static final long serialVersionUID = -1064412268570701648L;

	protected void _errorOutput(HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		response.getWriter().println("Invalid Usage");
	}

	protected void _writeOKHTMLHeader(HttpServletResponse response) {
		response.setContentType("application/json;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
	}
}
