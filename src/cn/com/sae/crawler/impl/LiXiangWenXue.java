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

import cn.com.sae.annotation.UseMemcache;
import cn.com.sae.crawler.WebSiteCrawler;
import cn.com.sae.model.SearchResult;
import cn.com.sae.model.novel.Book;
import cn.com.sae.model.novel.Section;
import cn.com.sae.model.novel.SectionInfo;
import cn.com.sae.utils.Encoding;

public class LiXiangWenXue extends BaseCrawler implements WebSiteCrawler {

	@Override
	public List<SearchResult> searchNote(String keyWord) {
		List<SearchResult> results = new ArrayList<SearchResult>();
		try {
			String searchURL = "http://www.03wx.com/modules/article/03so.php?searchkey="
					+ java.net.URLEncoder.encode(keyWord, "gbk")
					+ "&searchtype=articlename&searchom=%CB%D1+%CB%F7";
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
						if (td.hasNext()) {
							result.author = Encoding
									.getGBKStringFromISO8859String(td.next()
											.text());
						}
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
	public Book getBookInfoFromSearchResult(String resultUrl) {
		// http://www.03wx.com/xsinfo/6/6474.htm ↓
		// http://www.03wx.com/files/article/html/6/6474/
		try {
			if (resultUrl != null) {
				String html = this.fetchUrl.fetch(resultUrl);
				Document doc = Jsoup.parse(html);
				Element content = doc.getElementById("content");
				Elements title = doc.select("h1.booktitle");
				Elements img = content
						.select("table tbody tr td table tbody tr td table tbody tr  td table tbody tr td a img");
				Elements description = content
						.select("table tbody tr td table tbody tr td p");

				String[] segments = resultUrl.split("/");
				String novel_id = segments[segments.length - 1].replace(".htm",
						"");
				String novel_type = segments[segments.length - 2];
				Book book = new Book();
				if (title.last() != null) {
					book.name = Encoding.getGBKStringFromISO8859String(title
							.last().text());
				}
				book.url = "http://www.03wx.com/files/article/html/"
						+ novel_type + "/" + novel_id + "/";
				if (img.last() != null) {
					book.img = img.last().attr("src");
				}

				if (description.last() != null) {
					book.description = Encoding
							.getGBKStringFromISO8859String(description.text()
									.replace(NBSP_IN_UTF8, ""));
				}
				return book;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<SectionInfo> retriveBookSections(String bookUrl) {
		List<SectionInfo> results = new ArrayList<SectionInfo>();
		try {
			if (bookUrl != null) {
				String html = this.fetchUrl.fetch(bookUrl);
				Document doc = Jsoup.parse(html);
				Elements resultTable = doc.getElementsByClass("bc001");
				if (resultTable.last() != null) {
					Elements trs = resultTable.get(0).getElementsByTag("tr");
					Iterator<Element> tr = trs.listIterator();
					while (tr.hasNext()) {
						Elements tds = tr.next().getElementsByClass("bc003");
						Iterator<Element> td = tds.listIterator();
						while (td.hasNext()) {
							Element d = td.next();
							SectionInfo result = new SectionInfo();
							result.name = Encoding
									.getGBKStringFromISO8859String(d
											.getElementsByTag("a").get(0)
											.attr("title"));
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

	public static final String NBSP_IN_UTF8 = "\u00a0";

	@Override
	public Section getSection(String secUrl) {
		try {
			if (secUrl != null) {
				String html = this.fetchUrl.fetch(secUrl);
				Document doc = Jsoup.parse(html);
				Elements uctxt = doc.getElementsByClass("uctxt");
				if (uctxt.last() != null) {
					Element title = uctxt.get(0).getElementsByClass("title")
							.get(0);
					Element text = uctxt.get(0).getElementById("ziti")
							.getElementsByTag("p").get(0);
					Section section = new Section();
					section.title = Encoding
							.getGBKStringFromISO8859String(title.text());
					Document secText = Jsoup.parseBodyFragment(text.text());
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

	public List<SearchResult> getRank(int pageNo) {
		List<SearchResult> results = new ArrayList<SearchResult>();
		if (pageNo <= 0)
			return results;
		String rankUrl = "http://www.03wx.com/xstopallvisit/0/" + ".htm";
		String html = this.fetchUrl.fetch(rankUrl);
		Document doc = Jsoup.parse(html);
		//*[@id="content"]/table/tbody/tr[2]
		return results;
	}

	@Override
	public String crawlerName() {
		return "lixiangwenxue";
	}
}
