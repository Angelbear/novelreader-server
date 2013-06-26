package cn.com.sae;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.com.sae.utils.DataValidator;

public class Rank extends BaseHttpServlet {

	private static final long serialVersionUID = 1225162758982352693L;

	private static String[][] getRankRules = {
			{ "page", "Int" }, };

	private static DataValidator getRankValidator = new DataValidator(
			getRankRules);

	private void doGetRank(Map<String, String[]> params,
			HttpServletResponse response) throws ServletException, IOException {
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String uri = request.getRequestURI();
		if (uri.equals("/rank/get_rank")) {

		} else {
			_errorOutput(response);
		}
	}

}
