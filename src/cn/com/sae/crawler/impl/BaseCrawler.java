package cn.com.sae.crawler.impl;

import com.sina.sae.fetchurl.SaeFetchurl;

public class BaseCrawler {
	protected SaeFetchurl fetchUrl;
	
	public BaseCrawler() {
		fetchUrl = new SaeFetchurl();
	}
}
