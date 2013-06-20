package cn.com.sae.crawler.impl;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.ibm.icu.text.CharsetDetector;

import cn.com.sae.crawler.WebSiteCrawler;
import cn.com.sae.model.SearchResult;
import cn.com.sae.model.novel.Book;
import cn.com.sae.model.novel.Section;
import cn.com.sae.model.novel.SectionInfo;

public class LiXiangWenXue extends BaseCrawler implements WebSiteCrawler {

	@Override
	public List<SearchResult> searchNote(String keyWord) {
		List<SearchResult> results = new ArrayList<SearchResult>();
		try {
			String searchURL = "http://www.03wx.com/modules/article/03so.php?searchkey="
					+ java.net.URLEncoder.encode(keyWord, "gbk")
					+ "&searchtype=articlename&searchom=%CB%D1+%CB%F7";
			System.out.println(searchURL);
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
						result.name = d.text();
						result.url = d.getElementsByTag("a").get(0)
								.attr("href");
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
		// http://www.03wx.com/xsinfo/6/6474.htm ↓
		// http://www.03wx.com/files/article/html/6/6474/
		try {
			if (result != null && result.url != null) {
				String[] segments = result.url.split("/");
				String novel_id = segments[segments.length - 1].replace(".htm",
						"");
				String novel_type = segments[segments.length - 2];
				Book book = new Book();
				book.name = result.name;
				book.url = "http://www.03wx.com/files/article/html/"
						+ novel_type + "/" + novel_id + "/";
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
				Elements resultTable = doc.getElementsByClass("bc001");
				if (resultTable.last() != null) {
					Elements trs = resultTable.get(0).getElementsByTag("tr");
					Iterator<Element> tr = trs.listIterator();
					while (tr.hasNext()) {
						Elements tds = tr.next().getElementsByClass("bc003");
						Iterator<Element> td = tds.listIterator();
						if (td.hasNext()) {
							Element d = td.next();
							SectionInfo result = new SectionInfo();
							result.name = d.getElementsByTag("a").get(0)
									.attr("title");
							result.url = d.getElementsByTag("a").get(0)
									.attr("href");
							results.add(result);
						}

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

	@Override
	public Section getSection(SectionInfo sec) {
		try {
			if (sec != null && sec.url != null) {
				String html = this.fetchUrl.fetch(sec.url);
				Document doc = Jsoup.parse(html);
				Elements uctxt = doc.getElementsByClass("uctxt");
				if (uctxt.last() != null) {
					Element title = uctxt.get(0).getElementsByClass("title")
							.get(0);
					Element text = uctxt.get(0).getElementById("ziti")
							.getElementsByTag("p").get(0);
					Section section = new Section();
					section.title = title.text();
					section.text = text.text();
					return section;
				}
			}
		} catch (Exception e) {

		}
		return null;
	}
}
