package cn.com.sae;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Portal extends BaseHttpServlet {

	private static final long serialVersionUID = -2747596017528957511L;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		System.out.println(request.getRequestURI());
		response.sendRedirect("http://xiaoshuoyuedu-ipa.stor.sinaapp.com/index.html");
	}
}
