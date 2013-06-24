package cn.com.sae.crawler.impl;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities.EscapeMode;
import org.jsoup.select.Elements;

import cn.com.sae.crawler.WebSiteCrawler;
import cn.com.sae.model.SearchResult;
import cn.com.sae.model.novel.Book;
import cn.com.sae.model.novel.Section;
import cn.com.sae.model.novel.SectionInfo;
import cn.com.sae.utils.PathUtilities;

public class LuoQiuZhongWen extends BaseCrawler implements WebSiteCrawler {

	@Override
	public List<SearchResult> searchNote(String keyWord) {
		List<SearchResult> results = new ArrayList<SearchResult>();
		try {
			String searchURL = "http://www.luoqiu.com/modules/article/seeroh.php?searchkey="
					+ java.net.URLEncoder.encode(keyWord, "gbk")
					+ "&searchtype=articlename";
			String html = this.fetchUrl.fetch(searchURL);
			Document doc = Jsoup.parse(html);
			Elements resultTable = doc.getElementsByClass("searchresult");
			if (resultTable.last() != null) {
				Elements uls = resultTable.get(0).getElementsByTag("ul");
				Iterator<Element> ul = uls.listIterator();
				while (ul.hasNext()) {
					Elements a = ul.next().select("h2 b a");
					if (a.last() != null) {
						Element d = a.last();
						SearchResult result = new SearchResult();
						result.name = d.text();
						result.url = d.attr("href");
						result.from = this.crawlerName();
						results.add(result);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;
	}

	@Override
	public Book getBookInfoFromSearchResult(SearchResult result) {
		// http://www.luoqiu.com/book/74/74568.html ↓
		// http://www.luoqiu.com/html/74/74568/index.html
		try {
			if (result != null && result.url != null) {
				String[] segments = result.url.split("/");
				String novel_id = segments[segments.length - 1].replace(
						".html", "");
				String novel_type = segments[segments.length - 2];
				Book book = new Book();
				book.name = result.name;
				book.url = "http://www.luoqiu.com/html/" + novel_type + "/"
						+ novel_id + "/index.html";
				return book;
			}
		} catch (Exception e) {

		}
		return null;
	}

	@Override
	public List<SectionInfo> retriveBookSections(Book book) {
		List<SectionInfo> results = new ArrayList<SectionInfo>();
		try {
			if (book != null && book.url != null) {
				String html = this.fetchUrl.fetch(book.url);
				Document doc = Jsoup.parse(html);
				Elements resultTable = doc.getElementsByClass("booklist");
				if (resultTable.last() != null) {
					Elements as = resultTable.get(0).select("span a");
					Iterator<Element> a = as.listIterator();
					while (a.hasNext()) {
						Element e = a.next();
						SectionInfo result = new SectionInfo();
						result.name = e.text();
						result.url = PathUtilities.resolveRelativePath(
								e.attr("href"), book.url);
						results.add(result);
					}
				}

			}
		} catch (Exception e) {

		}
		return results;
	}

	public String toHex(String arg) throws UnsupportedEncodingException {
		return String.format("%x", new BigInteger(1, arg.getBytes("utf-8")));
	}

	public static final String NBSP_IN_UTF8 = "\u00a0";

	@Override
	public Section getSection(SectionInfo sec) {
		try {
			if (sec != null && sec.url != null) {
				String html = this.fetchUrl.fetch(sec.url);
				Document doc = Jsoup.parse(html);
				Elements title = doc.getElementsByClass("bname_content");
				Element content = doc.getElementById("content");
				if (title.last() != null && content != null) {
					Section section = new Section();
					section.title = title.last().text();
					Document secText = Jsoup.parseBodyFragment(content.text());
					secText.outputSettings().prettyPrint(false);
					secText.outputSettings().escapeMode(EscapeMode.xhtml);
					section.text = secText.text().replace(NBSP_IN_UTF8, "");
					return section;
				}
			}
		} catch (Exception e) {

		}
		return null;
	}

	@Override
	public String crawlerName() {
		return "luoqiuzhongwen";
	}
}