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
import cn.com.sae.annotation.UseSaeKV;
import cn.com.sae.crawler.WebSiteCrawler;
import cn.com.sae.model.SearchResult;
import cn.com.sae.model.novel.Book;
import cn.com.sae.model.novel.CategoryInfo;
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
		// http://www.59to.com/modules/article/articleinfo.php?id=10866 ↓
		// http://www.59to.com/files/article/xiaoshuo/10/10866/index.html
		try {
			if (resultUrl != null) {
				String html = this.fetchUrl.fetch(resultUrl);
				Document doc = Jsoup.parse(html);
				Elements as = doc.select("a.btnlink");
				Element content = doc.getElementById("content");
				Elements title = content
						.select("table tbody tr td table tbody tr td table tbody tr td span");
				// *[@id="content"]/table/tbody/tr[4]/td/table/tbody/tr/td[2]/br[3]
				Elements img = content
						.select("table tbody tr td table tbody tr td a img");
				Elements description = content
						.select("table tbody tr td table tbody tr td span.hottext");
				// *[@id="content"]/table/tbody/tr[1]/td/table/tbody/tr[1]/td/table/tbody/tr/td[1]/span
				// *[@id="content"]/table/tbody/tr[4]/td/table/tbody/tr/td[2]/a[1]/img
				// *[@id="content"]/table/tbody/tr[4]/td/table/tbody/tr/td[2]/span[2]
				if (as.last() != null) {
					Element e = as.get(0);
					Book book = new Book();
					book.url = e.attr("href");
					if (title.last() != null) {
						book.name = Encoding
								.getGBKStringFromISO8859String(title.last()
										.text());
					}
					if (img.last() != null) {
						book.img = img.last().attr("src");
					}
					if (description.last() != null) {
						book.description = Encoding
								.getGBKStringFromISO8859String(description
										.last().parent().text()
										.replace(NBSP_IN_UTF8, ""));
					}
					return book;
				}
			}
		} catch (Exception e) {

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
								e.attr("href"), bookUrl);
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
	public Section getSection(String secUrl) {
		try {
			if (secUrl != null) {
				String html = this.fetchUrl.fetch(secUrl);
				Document doc = Jsoup.parse(cleanPreserveLineBreaks(html));
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
									.replace(NBSP_IN_UTF8, " ")
									.replace("br2n", "\n"));
					return section;
				}
			}
		} catch (Exception e) {

		}
		return null;
	}

	public List<SearchResult> getRank(int pageNo) {
		return null;
	}

	@Override
	public String crawlerName() {
		return "wujiuwenxue";
	}

	@Override
	public List<CategoryInfo> getCategoryInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SearchResult> getCategory(int type, int pageNo) {
		// TODO Auto-generated method stub
		return null;
	}
}
