package cn.com.sae.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cn.com.sae.crawler.WebSiteCrawler;
import cn.com.sae.crawler.impl.LiXiangWenXue;
import cn.com.sae.crawler.impl.LuoQiuZhongWen;
import cn.com.sae.crawler.impl.WuJiuWenXue;
import cn.com.sae.model.SearchResult;
import cn.com.sae.model.novel.Book;
import cn.com.sae.model.novel.Section;
import cn.com.sae.model.novel.SectionInfo;
import cn.com.sae.proxy.CacheInvocationHandler;
import cn.com.sae.utils.ProxyUtils;

public class NovelSearchEngine {
	private static HashMap<String, WebSiteCrawler> crawlerImpl = new HashMap<String, WebSiteCrawler>();

	public static void init() {
		WebSiteCrawler li = new LiXiangWenXue();
		WebSiteCrawler li_proxy = (WebSiteCrawler)ProxyUtils.createProxy(WebSiteCrawler.class, new CacheInvocationHandler(li));
		WebSiteCrawler luo = new LuoQiuZhongWen();
		WebSiteCrawler luo_proxy = (WebSiteCrawler)ProxyUtils.createProxy(WebSiteCrawler.class, new CacheInvocationHandler(luo));
		WebSiteCrawler wujiu = new WuJiuWenXue();
		WebSiteCrawler wujiu_proxy = (WebSiteCrawler)ProxyUtils.createProxy(WebSiteCrawler.class, new CacheInvocationHandler(wujiu));
		// TODO
		crawlerImpl.put(li.crawlerName(), li_proxy);
		crawlerImpl.put(luo.crawlerName(), luo_proxy);
		crawlerImpl.put(wujiu.crawlerName(), wujiu_proxy);
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

	public static Book getBookInfoFromSearchResult(String resultUrl,
			String from) {
		WebSiteCrawler crawler = crawlerImpl.get(from);
		if (crawler != null) {
			return crawler.getBookInfoFromSearchResult(resultUrl);
		}
		return null;
	}

	public static List<SectionInfo> retriveBookSections(String bookUrl, String from) {
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
}
