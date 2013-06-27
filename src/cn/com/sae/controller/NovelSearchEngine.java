package cn.com.sae.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletConfig;

import cn.com.sae.crawler.WebSiteCrawler;
import cn.com.sae.model.SearchResult;
import cn.com.sae.model.novel.Book;
import cn.com.sae.model.novel.CategoryInfo;
import cn.com.sae.model.novel.Section;
import cn.com.sae.model.novel.SectionInfo;
import cn.com.sae.proxy.CacheInvocationHandler;
import cn.com.sae.utils.ProxyUtils;

public class NovelSearchEngine {
	private static HashMap<String, WebSiteCrawler> crawlerImpl = new HashMap<String, WebSiteCrawler>();

	public static void init(ServletConfig config) {
		String crawlerClasses = config.getInitParameter("crawler.classes");

		if (crawlerClasses == null || crawlerClasses.equals(""))
			throw new RuntimeException("Please define crawler classes");

		String[] classes = crawlerClasses.split(",");

		for (String cls : classes) {
			try {
				WebSiteCrawler crawler = (WebSiteCrawler) Class.forName(cls)
						.newInstance();
				WebSiteCrawler crawlerProxy = (WebSiteCrawler) ProxyUtils
						.createProxy(WebSiteCrawler.class,
								new CacheInvocationHandler(crawler));
				crawlerImpl.put(crawler.crawlerName(), crawlerProxy);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	public static List<SearchResult> searchNote(String keyWord, String from) {
		List<SearchResult> results = new ArrayList<SearchResult>();
		if (from == null) {
			return searchNote(keyWord);
		}
		if (crawlerImpl.get(from) != null) {
			results.addAll(crawlerImpl.get(from).searchNote(keyWord));
		}
		return results;
	}

	public static List<SearchResult> searchNote(String keyWord) {
		List<SearchResult> results = new ArrayList<SearchResult>();
		Iterator<Entry<String, WebSiteCrawler>> itr = crawlerImpl.entrySet()
				.iterator();
		while (itr.hasNext()) {
			Map.Entry<String, WebSiteCrawler> pairs = (Map.Entry<String, WebSiteCrawler>) itr
					.next();
			results.addAll(pairs.getValue().searchNote(keyWord));
		}
		return results;
	}

	public static Book getBookInfoFromSearchResult(String resultUrl, String from) {
		WebSiteCrawler crawler = crawlerImpl.get(from);
		if (crawler != null) {
			return crawler.getBookInfoFromSearchResult(resultUrl);
		}
		return null;
	}

	public static List<SectionInfo> retriveBookSections(String bookUrl,
			String from) {
		WebSiteCrawler crawler = crawlerImpl.get(from);
		if (crawler != null) {
			return crawler.retriveBookSections(bookUrl);
		}
		return null;
	}

	public static Section getSection(String secUrl, String from) {
		WebSiteCrawler crawler = crawlerImpl.get(from);
		if (crawler != null) {
			return crawler.getSection(secUrl);
		}
		return null;
	}
	
	public static List<SearchResult> getRank(int pageNo, String from){
		WebSiteCrawler crawler = crawlerImpl.get(from);
		if (crawler != null) {
			return crawler.getRank(pageNo);
		}
		return null;
	}
	
	public static List<SearchResult> getCategory(int type, int pageNo, String from){
		WebSiteCrawler crawler = crawlerImpl.get(from);
		if (crawler != null) {
			return crawler.getCategory(type, pageNo);
		}
		return null;
	}
	
	public static List<CategoryInfo> getCategoryInfo(String from) {
		WebSiteCrawler crawler = crawlerImpl.get(from);
		if (crawler != null) {
			return crawler.getCategoryInfo();
		}
		return null;
	}
}
