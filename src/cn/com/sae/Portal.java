package cn.com.sae;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.com.sae.utils.Common;

public class Portal extends BaseHttpServlet {

	private static final long serialVersionUID = -2747596017528957511L;

	private static final int DEFAULT_BUFFER_SIZE = 10240;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String relativeWebPath = request.getRequestURI();
		if (relativeWebPath.equals("/")) {
			response.sendRedirect("http://xiaoshuoyuedu-ipa.stor.sinaapp.com/index.html");
		} else {
			String absolutePath = "/WEB-INF/classes" + relativeWebPath;

			String contentType = getServletContext().getMimeType(absolutePath);

			if (contentType == null) {
				contentType = "application/octet-stream";
			}

			File file = new File(relativeWebPath);

			InputStream is = this.getServletContext().getResourceAsStream(
					absolutePath);
			if (is != null) {
				response.reset();
				response.setBufferSize(DEFAULT_BUFFER_SIZE);
				response.setContentType(contentType);
				response.setHeader("Content-Length",
						String.valueOf(is.available()));
				response.setHeader("Content-Disposition", "inline; filename=\""
						+ file.getName() + "\"");

				byte[] content = new byte[DEFAULT_BUFFER_SIZE];
				OutputStream out = response.getOutputStream();

				while (is.read(content) > 0) {
					out.write(content);
				}
			}
		}
	}
}
