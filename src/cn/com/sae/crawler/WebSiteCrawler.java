package cn.com.sae.crawler;

import java.util.List;

import cn.com.sae.annotation.UseMemcache;
import cn.com.sae.annotation.UseSaeKV;
import cn.com.sae.model.SearchResult;
import cn.com.sae.model.novel.Book;
import cn.com.sae.model.novel.CategoryInfo;
import cn.com.sae.model.novel.Section;
import cn.com.sae.model.novel.SectionInfo;
import cn.com.sae.utils.Common;

public interface WebSiteCrawler {
	@UseMemcache(expiry = Common.MINITE)
	@UseSaeKV(expiry = 10 * Common.MINITE)
	public List<SearchResult> searchNote(String keyWord);

	@UseMemcache(expiry = Common.MINITE)
	@UseSaeKV(expiry = Common.HOUR)
	public Book getBookInfoFromSearchResult(String resultUrl);

	@UseMemcache(expiry = Common.MINITE)
	@UseSaeKV(expiry = Common.HOUR)
	public List<SectionInfo> retriveBookSections(String bookUrl);

	@UseMemcache(expiry = Common.MINITE)
	@UseSaeKV(expiry = Common.WEEK)
	public Section getSection(String secUrl);
	
	@UseMemcache(expiry = Common.HOUR)
	@UseSaeKV(expiry = Common.DAY)
	public List<SearchResult> getRank(int pageNo);
	
	public List<CategoryInfo> getCategoryInfo();
	
	@UseMemcache(expiry = Common.HOUR)
	@UseSaeKV(expiry = Common.DAY)
	public List<SearchResult> getCategory(int type, int pageNo);

	public String crawlerName();
}
