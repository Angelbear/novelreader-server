package cn.com.sae;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.arnx.jsonic.JSON;
import cn.com.sae.controller.NovelSearchEngine;
import cn.com.sae.crawler.impl.LiXiangWenXue;
import cn.com.sae.utils.DataValidator;

public class Rank extends BaseHttpServlet {

	private static final long serialVersionUID = 1225162758982352693L;

	private static String[][] getRankRules = { { "page", "Int" },
			{ "from", "Str", "NotEmpty" } };

	private static DataValidator getRankValidator = new DataValidator(
			getRankRules);

	private void doGetRank(Map<String, String[]> params,
			HttpServletResponse response) throws ServletException, IOException {
		Map<String, String> param = getRankValidator
				.validateHttpParamData(params);
		if (param.get("page") != null) {
			_writeOKHTMLHeader(response);
			response.getWriter().println(
					JSON.encode(NovelSearchEngine.getRank(
							Integer.valueOf(param.get("page")),
							param.get("from"))));
		} else {
			_errorOutput(response);
		}
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String uri = request.getRequestURI();
		if (uri.equals("/rank/get_rank")) {
			doGetRank(request.getParameterMap(), response);
		} else {
			_errorOutput(response);
		}
	}

}
