package cn.com.sae;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.jni.Directory;

import cn.com.sae.utils.Common;

public class Portal extends BaseHttpServlet {

	private static final long serialVersionUID = -2747596017528957511L;

	private static final int DEFAULT_BUFFER_SIZE = 10240;

	private void sendCacheManifestFile(HttpServletResponse response)
			throws IOException {
		/*
		 * CACHE MANIFEST # Version: 201309121427
		 * 
		 * CACHE: /static/css/monocore.css /static/css/monoctrl.css
		 * /static/css/page.css /static/js/jquery.min.js /static/js/monocore.js
		 * /static/js/monoctrl.js /static/js/threepane.js
		 * /static/js/fontselector.js /static/js/storagecache.js
		 * 
		 * NETWORK: /view/book /view/section
		 */

		response.reset();
		response.setBufferSize(DEFAULT_BUFFER_SIZE);
		response.setContentType("text/cache-manifest");
		StringBuilder manifest = new StringBuilder();
		manifest.append("CACHE MANIFEST\n");
		if (Common.isProduction()) {
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
			manifest.append("#Version:" + sdf1.format(new Date()) + "\n");
		} else {
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd-hh-mm");
			manifest.append("#Version:" + sdf1.format(new Date()) + "\n");
		}
		manifest.append("\n");
		manifest.append("CACHE:\n");

		for (String f : this.getServletContext().getResourcePaths(
				"/WEB-INF/classes/static/js/")) {
			System.out.println(f);
			manifest.append( f.replace("/WEB-INF/classes", "") + "\n");
		}

		for (String f : this.getServletContext().getResourcePaths(
				"/WEB-INF/classes/static/css/")) {
			System.out.println(f);
			manifest.append(f.replace("/WEB-INF/classes", "") + "\n");
		}

		manifest.append("\n");
		manifest.append("NETWORK:\n");
		manifest.append("/view/book\n");
		manifest.append("/view/section");
		String content = manifest.toString();

		response.setHeader("Content-Length",
				String.valueOf(content.getBytes().length));
		response.setHeader("Content-Disposition",
				"inline; filename=\"cache.manifest\"");
		OutputStream out = response.getOutputStream();
		out.write(content.getBytes());
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String relativeWebPath = request.getRequestURI();
		System.out.println(relativeWebPath);
		if (relativeWebPath.equals("/")) {
			response.sendRedirect("http://xiaoshuoyuedu-ipa.stor.sinaapp.com/index.html");
		} else {
			String absolutePath = "/WEB-INF/classes" + relativeWebPath;

			String contentType = getServletContext().getMimeType(absolutePath);

			if (relativeWebPath.endsWith(".manifest")) {
				contentType = "text/cache-manifest";
				sendCacheManifestFile(response);
				return;
			}

			if (relativeWebPath.endsWith(".map")) {
				contentType = "application/json";
			}

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
