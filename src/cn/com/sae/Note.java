package cn.com.sae;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.arnx.jsonic.JSON;

import cn.com.sae.controller.NovelSearchEngine;
import cn.com.sae.crawler.impl.LiXiangWenXue;
import cn.com.sae.model.SearchResult;
import cn.com.sae.model.novel.Book;
import cn.com.sae.model.novel.Section;
import cn.com.sae.model.novel.SectionInfo;

public class Note extends HttpServlet {

	private static final long serialVersionUID = 2056174360882626087L;

	public Note() {
		NovelSearchEngine.init();
	}

	private void _errorOutput(HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		response.getWriter().println("Invalid Usage");
	}

	private void _writeOKHTMLHeader(HttpServletResponse response) {
		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);
	}

	private void doSearchNote(Map<String, String[]> params,
			HttpServletResponse response) throws ServletException, IOException {
		if (params != null && params.get("key_word") != null
				&& params.get("key_word").length > 0) {
			_writeOKHTMLHeader(response);
			response.getWriter().println(
					JSON.encode(NovelSearchEngine.searchNote(params
							.get("key_word")[0])));
		} else {
			_errorOutput(response);
		}
	}

	private void doGetBookInfo(Map<String, String[]> params,
			HttpServletResponse response) throws ServletException, IOException {
		if (params != null && params.get("key_word") != null
				&& params.get("key_word").length > 0) {
			_writeOKHTMLHeader(response);
			response.getWriter().println(
					JSON.encode(NovelSearchEngine.searchNote(params
							.get("key_word")[0])));
		} else {
			_errorOutput(response);
		}
	}

	private void doRetriveSections(Map<String, String[]> params,
			HttpServletResponse response) throws ServletException, IOException {

	}

	private void doGeSection(Map<String, String[]> params,
			HttpServletResponse response) {

	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String uri = request.getRequestURI();
		System.out.println(uri);
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
