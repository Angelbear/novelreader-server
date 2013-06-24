package cn.com.sae.crawler;

import java.util.List;

import cn.com.sae.annotation.UseMemcache;
import cn.com.sae.annotation.UseSaeKV;
import cn.com.sae.model.SearchResult;
import cn.com.sae.model.novel.Book;
import cn.com.sae.model.novel.Section;
import cn.com.sae.model.novel.SectionInfo;

public interface WebSiteCrawler {
	@UseMemcache
	@UseSaeKV
	public List<SearchResult> searchNote(String keyWord);

	@UseMemcache
	@UseSaeKV
	public Book getBookInfoFromSearchResult(String resultUrl);

	@UseMemcache
	@UseSaeKV
	public List<SectionInfo> retriveBookSections(String bookUrl);

	@UseMemcache
	@UseSaeKV
	public Section getSection(String secUrl);

	public String crawlerName();
}
