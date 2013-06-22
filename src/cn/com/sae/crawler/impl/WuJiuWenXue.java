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
import cn.com.sae.utils.Encoding;
import cn.com.sae.utils.PathUtilities;

public class WuJiuWenXue extends BaseCrawler implements WebSiteCrawler {

	@Override
	public List<SearchResult> searchNote(String keyWord) {
		List<SearchResult> results = new ArrayList<SearchResult>();
		try {
			String searchURL = "http://www.59to.com/modules/article/search.php?searchkey="
					+ java.net.URLEncoder.encode(keyWord, "gbk")
					+ "&searchtype=articlename";
			String html = this.fetchUrl.fetch(searchURL);
			Document doc = Jsoup.parse(html);
			Elements resultTable = doc.getElementsByClass("grid");
			if (resultTable.last() != null) {
				Elements trs = resultTable.get(0).getElementsByTag("tr");
				Iterator<Element> tr = trs.listIterator();
				while (tr.hasNext()) {
					Elements tds = tr.next().getElementsByClass("odd");
					Iterator<Element> td = tds.listIterator();
					if (td.hasNext()) {
						Element d = td.next();
						SearchResult result = new SearchResult();
						result.name = Encoding.getGBKStringFromISO8859String(d
								.text());
						result.url = d.getElementsByTag("a").get(0)
								.attr("href");
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
		// http://www.59to.com/modules/article/articleinfo.php?id=10866 ↓
		// http://www.59to.com/files/article/xiaoshuo/10/10866/index.html
		try {
			if (result != null && result.url != null) {
				String html = this.fetchUrl.fetch(result.url);
				Document doc = Jsoup.parse(html);
				Elements as = doc.select("a.btnlink");
				if (as.last() != null) {
					Element e = as.get(0);
					Book book = new Book();
					book.name = result.name;
					book.url = e.attr("href");
					return book;
				}
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
				Elements resultTable = doc.getElementsByClass("acss");
				if (resultTable.last() != null) {
					Elements as = resultTable.get(0)
							.select("tbody tr td.www a");
					Iterator<Element> a = as.listIterator();
					while (a.hasNext()) {
						Element e = a.next();
						SectionInfo result = new SectionInfo();
						result.name = Encoding.getGBKStringFromISO8859String(e
								.text());
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
				Elements title = doc.getElementsByClass("myleft");
				Element content = doc.getElementById("content");
				if (title.last() != null && content != null) {
					Section section = new Section();
					section.title = Encoding
							.getGBKStringFromISO8859String(title.last().text());
					Document secText = Jsoup.parseBodyFragment(content.text());
					secText.outputSettings().prettyPrint(false);
					secText.outputSettings().escapeMode(EscapeMode.xhtml);
					section.text = Encoding
							.getGBKStringFromISO8859String(secText.text()
									.replace(NBSP_IN_UTF8, ""));
					return section;
				}
			}
		} catch (Exception e) {

		}
		return null;
	}

	@Override
	public String crawlerName() {
		return "wujiuwenxue";
	}
}
