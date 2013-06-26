package cn.com.sae;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.arnx.jsonic.JSON;
import cn.com.sae.controller.NovelSearchEngine;
import cn.com.sae.utils.DataValidator;

public class Note extends BaseHttpServlet {

	private static final long serialVersionUID = 2056174360882626087L;

	public Note() {
	}

	public void init() {
		NovelSearchEngine.init(this.getServletConfig());
	}

	private static String[][] searchNoteRules = {
			{ "key_word", "Str", "NotEmpty" }, { "from", "Str" } };
	private static DataValidator searchNoteValidator = new DataValidator(
			searchNoteRules);

	private void doSearchNote(Map<String, String[]> params,
			HttpServletResponse response) throws ServletException, IOException {

		Map<String, String> opts = searchNoteValidator
				.validateHttpParamData(params);

		if (opts != null) {
			_writeOKHTMLHeader(response);
			response.getWriter().println(
					JSON.encode(NovelSearchEngine.searchNote(
							opts.get("key_word"), opts.get("from"))));
		} else {
			_errorOutput(response);
		}
	}

	private static String[][] getBookInfoRules = {
			{ "from", "Str", "NotEmpty" }, { "url", "Str", "NotEmpty" } };

	private static DataValidator getBookInfoValidator = new DataValidator(
			getBookInfoRules);

	private void doGetBookInfo(Map<String, String[]> params,
			HttpServletResponse response) throws ServletException, IOException {

		Map<String, String> opts = getBookInfoValidator
				.validateHttpParamData(params);
		if (opts != null) {
			_writeOKHTMLHeader(response);
			response.getWriter().println(
					JSON.encode(NovelSearchEngine.getBookInfoFromSearchResult(
							opts.get("url"), opts.get("from"))));
		} else {
			_errorOutput(response);
		}
	}

	private static String[][] retriveSectionsRules = {
			{ "from", "Str", "NotEmpty" }, { "url", "Str", "NotEmpty" } };

	private static DataValidator retriveSectionsRulesValidator = new DataValidator(
			retriveSectionsRules);

	private void doRetriveSections(Map<String, String[]> params,
			HttpServletResponse response) throws ServletException, IOException {
		Map<String, String> opts = retriveSectionsRulesValidator
				.validateHttpParamData(params);
		if (opts != null) {
			_writeOKHTMLHeader(response);
			response.getWriter().println(
					JSON.encode(NovelSearchEngine.retriveBookSections(
							opts.get("url"), opts.get("from"))));
		} else {
			_errorOutput(response);
		}
	}

	private static String[][] getSectionRules = {
			{ "from", "Str", "NotEmpty" }, { "url", "Str", "NotEmpty" } };

	private static DataValidator getSectionValidator = new DataValidator(
			getSectionRules);

	private void doGeSection(Map<String, String[]> params,
			HttpServletResponse response) throws ServletException, IOException {
		Map<String, String> opts = getSectionValidator
				.validateHttpParamData(params);
		if (opts != null) {
			_writeOKHTMLHeader(response);
			response.getWriter().println(
					JSON.encode(NovelSearchEngine.getSection(opts.get("url"),
							opts.get("from"))));
		} else {
			_errorOutput(response);
		}
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String uri = request.getRequestURI();
		if (uri.equals("/note/search_novel")) {
			doSearchNote(request.getParameterMap(), response);
		} else if (uri.equals("/note/get_book_info")) {
			doGetBookInfo(request.getParameterMap(), response);
		} else if (uri.equals("/note/retrieve_sections")) {
			doRetriveSections(request.getParameterMap(), response);
		} else if (uri.equals("/note/get_section")) {
			doGeSection(request.getParameterMap(), response);
		} else {
			_errorOutput(response);
		}
	}
}
