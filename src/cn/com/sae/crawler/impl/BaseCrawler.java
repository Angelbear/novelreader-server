package cn.com.sae.crawler.impl;

import com.sina.sae.fetchurl.SaeFetchurl;

public abstract class BaseCrawler {
	protected SaeFetchurl fetchUrl;

	public BaseCrawler() {
		fetchUrl = new SaeFetchurl();
		fetchUrl.setReadTimeout(10);
	}
	
	public static String cleanPreserveLineBreaks(String bodyHtml) {
	    return bodyHtml.replace("<br />", "br2n");
	}
}
