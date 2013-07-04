package cn.com.sae.crawler.impl;

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

public class LiXiangWenXue extends BaseCrawler implements WebSiteCrawler {

	public List<SearchResult> searchNote(String keyWord) {
		// TODO Auto-generated method stub
		// http://www.lixiangwenxue.com/modules/article/search.php
		List<SearchResult> results = new ArrayList<SearchResult>();
		try {
			String searchURL = "http://www.lixiangwenxue.com/modules/article/search.php?searchkey="
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

	public Book getBookInfoFromSearchResult(String resultUrl) {
		try {
			if (resultUrl != null) {
				String html = this.fetchUrl.fetch(resultUrl);
				Document doc = Jsoup.parse(html);

				Elements title = doc.getElementsByClass("infokaishi");
				Elements img = doc.getElementsByClass("infoTu");
				Elements description = title;

				Book book = new Book();
				if (title.last() != null) {
					book.name = Encoding.getGBKStringFromISO8859String(title
							.last().child(0).text());
				}
				System.out.println(book.name);
				book.url = resultUrl;
				if (img.last() != null) {
					book.img = img.last().child(0).attr("src");
				}
				System.out.println(book.img);

				if (description.last() != null) {
					book.description = Encoding
							.getGBKStringFromISO8859String(description.last()
									.child(4).text());
				}
				System.out.println(book.description);
				return book;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<SectionInfo> retriveBookSections(String bookUrl) {
		List<SectionInfo> results = new ArrayList<SectionInfo>();
		try {
			if (bookUrl != null) {
				String html = this.fetchUrl.fetch(bookUrl);
				Document doc = Jsoup.parse(html);
				Elements resultTable = doc.getElementsByClass("acss");
				if (resultTable.last() != null) {
					Elements td = resultTable.get(0).select("tbody tr td.ccss");
					Iterator<Element> tds = td.iterator();
					while (tds.hasNext()) {
						Element d = tds.next();
						if (d.getElementsByTag("a") != null
								&& d.getElementsByTag("a").size() > 0) {
							SectionInfo result = new SectionInfo();
							result.name = Encoding
									.getGBKStringFromISO8859String(d
											.getElementsByTag("a").get(0)
											.text());
							result.url = d.getElementsByTag("a").get(0)
									.attr("href");
							results.add(result);
						}

					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;
	}

	public static final String NBSP_IN_UTF8 = "\u00a0";

	public Section getSection(String secUrl) {
		try {
			if (secUrl != null) {
				String html = this.fetchUrl.fetch(secUrl);
				Document doc = Jsoup.parse(cleanPreserveLineBreaks(html));
				Element title = doc.getElementById("title");
				Element text = doc.getElementById("content");
				Section section = new Section();
				section.title = Encoding.getGBKStringFromISO8859String(title
						.text());
				section.text = Encoding.getGBKStringFromISO8859String(text
						.text().replace(NBSP_IN_UTF8, " ")
						.replace("br2n", "\n"));
				return section;

			}
		} catch (Exception e) {

		}
		return null;
	}

	private List<SearchResult> getSearchResultsFromURL(String url) {
		List<SearchResult> results = new ArrayList<SearchResult>();
		String html = this.fetchUrl.fetch(url);
		Document doc = Jsoup.parse(html);
		System.out.println(html);
		Element content = doc.getElementById("content");
		if (content != null) {
			Elements trs = content.select("table tbody tr");
			Iterator<Element> itr = trs.iterator();
			while (itr.hasNext()) {
				Element e = itr.next();
				if (e.select("td.odd").size() > 0) {
					SearchResult r = new SearchResult();
					r.name = Encoding.getGBKStringFromISO8859String(e
							.select("td.odd").get(0).text());
					r.url = e.select("td.odd a").get(0).attr("href");
					r.author = Encoding.getGBKStringFromISO8859String(e
							.select("td.odd").get(1).text());
					r.from = this.crawlerName();
					results.add(r);
				}
			}
		}
		return results;
	}

	public List<SearchResult> getRank(int pageNo) {
		List<SearchResult> results = new ArrayList<SearchResult>();
		if (pageNo <= 0)
			return results;
		String rankUrl = "http://www.lixiangwenxue.com/top/allvisit_" + pageNo
				+ ".html";
		return this.getSearchResultsFromURL(rankUrl);
	}

	private String[] types = { "玄幻魔法", "武侠修真", "都市言情", "历史军事", "侦探推理", "网游动漫",
			"科幻小说", "恐怖灵异", "散文诗词", "其他类型", "全本" };

	public List<CategoryInfo> getCategoryInfo() {
		List<CategoryInfo> infos = new ArrayList<CategoryInfo>();
		for (int i = 0; i < types.length; i++) {
			CategoryInfo cat = new CategoryInfo();
			cat.name = types[i];
			cat.type = i + 1;
			infos.add(cat);
		}
		return infos;
	}

	public List<SearchResult> getCategory(int categoryType, int pageNo) {
		List<SearchResult> results = new ArrayList<SearchResult>();
		if (pageNo <= 0) {
			return results;
		}

		if (categoryType < 1 || categoryType > 11) {
			return results;
		}

		String categoryUrl;
		if (categoryType < 11) {
			categoryUrl = "http://www.lixiangwenxue.com/fl/" + categoryType
					+ "_" + pageNo + ".html";
		} else {
			categoryUrl = "http://www.lixiangwenxue.com/modules/article/index.php?fullflag=1&page="
					+ pageNo;
		}
		return this.getSearchResultsFromURL(categoryUrl);
	}

	@Override
	public String crawlerName() {
		return "lixiangwenxue";
	}

}
