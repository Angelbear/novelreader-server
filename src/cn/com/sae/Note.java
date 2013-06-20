package cn.com.sae;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.com.sae.crawler.impl.LiXiangWenXue;
import cn.com.sae.model.SearchResult;
import cn.com.sae.model.novel.Book;
import cn.com.sae.model.novel.Section;
import cn.com.sae.model.novel.SectionInfo;

public class Note extends HttpServlet {

	private static final long serialVersionUID = 2056174360882626087L;

	public Note() {
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);
		LiXiangWenXue crawler = new LiXiangWenXue();
		List<SearchResult> results = crawler.searchNote("武极");
		Iterator<SearchResult> result = results.listIterator();
		while (result.hasNext()) {
			SearchResult r = result.next();
			response.getWriter().println(r.name + "<br />");
			response.getWriter().println(r.url + "<br />");
			Book b = crawler.getBookInfoFromSearchResult(r);
			List<SectionInfo> secInfos = crawler.retriveBookSections(b);
			Iterator<SectionInfo> infoItr = secInfos.listIterator();
			while (infoItr.hasNext()) {
				SectionInfo info = infoItr.next();
				response.getWriter().println(info.name + "<br />");
				response.getWriter().println(info.url + "<br />");
				Section section = crawler.getSection(info);
				response.getWriter().println(section.title + "<br />");
				response.getWriter().println(section.text + "<br />");
				return;
			}
		}
	}
}
