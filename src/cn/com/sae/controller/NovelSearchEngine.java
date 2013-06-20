package cn.com.sae.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.com.sae.crawler.WebSiteCrawler;
import cn.com.sae.crawler.impl.LiXiangWenXue;
import cn.com.sae.model.SearchResult;
import cn.com.sae.model.novel.Book;
import cn.com.sae.model.novel.Section;
import cn.com.sae.model.novel.SectionInfo;

public class NovelSearchEngine {
	private static HashMap<String, WebSiteCrawler> crawlerImpl = new HashMap<String, WebSiteCrawler>();

	public static void init() {
		LiXiangWenXue li = new LiXiangWenXue();
		// TODO
		crawlerImpl.put(li.crawlerName(), li);
	}

	public static List<SearchResult> searchNote(String keyWord) {
		List<SearchResult> results = new ArrayList<SearchResult>();
		Iterator itr = crawlerImpl.entrySet().iterator();
		while (itr.hasNext()) {
			Map.Entry<String, WebSiteCrawler> pairs = (Map.Entry<String, WebSiteCrawler>) itr
					.next();
			results.addAll(pairs.getValue().searchNote(keyWord));
		}
		return results;
	}

	public static Book getBookInfoFromSearchResult(SearchResult result,
			String from) {
		WebSiteCrawler crawler = crawlerImpl.get(from);
		if (crawler != null) {
			return crawler.getBookInfoFromSearchResult(result);
		}
		return null;
	}

	public static List<SectionInfo> retriveBookSections(Book book, String from) {
		WebSiteCrawler crawler = crawlerImpl.get(from);
		if (crawler != null) {
			return crawler.retriveBookSections(book);
		}
		return null;
	}

	public static Section getSection(SectionInfo sec, String from) {
		WebSiteCrawler crawler = crawlerImpl.get(from);
		if (crawler != null) {
			return crawler.getSection(sec);
		}
		return null;
	}
}