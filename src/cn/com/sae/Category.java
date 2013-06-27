package cn.com.sae;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.arnx.jsonic.JSON;
import cn.com.sae.controller.NovelSearchEngine;
import cn.com.sae.utils.DataValidator;

public class Category extends BaseHttpServlet {
	private static final long serialVersionUID = 5650122354872568760L;

	private static String[][] getCategoryRules = { { "page", "Int" },
			{ "type", "Int" }, { "from", "Str", "NotEmpty" } };

	private static DataValidator getCategoryValidator = new DataValidator(
			getCategoryRules);

	private void doGetCategory(Map<String, String[]> params,
			HttpServletResponse response) throws ServletException, IOException {
		Map<String, String> param = getCategoryValidator
				.validateHttpParamData(params);
		if (param != null) {
			_writeOKHTMLHeader(response);
			response.getWriter().println(
					JSON.encode(NovelSearchEngine.getCategory(
							Integer.valueOf(param.get("type")),
							Integer.valueOf(param.get("page")),
							param.get("from"))));
		} else {
			_errorOutput(response);
		}
	}

	private static String[][] getCategoryInfoRules = { { "from", "Str",
			"NotEmpty" } };

	private static DataValidator getCategoryInfoValidator = new DataValidator(
			getCategoryInfoRules);

	private void doGetCategoryInfo(Map<String, String[]> params,
			HttpServletResponse response) throws ServletException, IOException {
		Map<String, String> param = getCategoryInfoValidator
				.validateHttpParamData(params);
		if (param != null) {
			_writeOKHTMLHeader(response);
			response.getWriter().println(
					JSON.encode(NovelSearchEngine.getCategoryInfo(param
							.get("from"))));
		} else {
			_errorOutput(response);
		}
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String uri = request.getRequestURI();
		if (uri.equals("/category/get_category")) {
			doGetCategory(request.getParameterMap(), response);
		} else if (uri.equals("/category/get_category_info")) {
			doGetCategoryInfo(request.getParameterMap(), response);
		} else {
			_errorOutput(response);
		}
	}
}
