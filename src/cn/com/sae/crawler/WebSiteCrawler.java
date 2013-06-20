package cn.com.sae.crawler;

import java.util.List;

import cn.com.sae.model.SearchResult;
import cn.com.sae.model.novel.Book;
import cn.com.sae.model.novel.Section;
import cn.com.sae.model.novel.SectionInfo;

public interface WebSiteCrawler {
	public List<SearchResult> searchNote(String keyWord);
	public Book getBookInfoFromSearchResult(SearchResult result);
	public List<SectionInfo> retriveBookSections(Book book);
	public Section getSection(SectionInfo sec);
}
