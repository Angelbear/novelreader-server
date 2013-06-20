package cn.com.sae.crawler.impl;

import com.sina.sae.fetchurl.SaeFetchurl;

public abstract class BaseCrawler {
	protected SaeFetchurl fetchUrl;
	
	public abstract String crawlerName();
	
	public BaseCrawler() {
		fetchUrl = new SaeFetchurl();
	}
}
