package cn.com.sae;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.arnx.jsonic.JSON;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Restrictions;

import cn.com.sae.controller.NovelSearchEngine;
import cn.com.sae.crawler.WebSiteCrawler;
import cn.com.sae.db.HibernateUtil;
import cn.com.sae.model.novel.Book;
import cn.com.sae.model.novel.Section;
import cn.com.sae.model.novel.SectionInfo;
import cn.com.sae.utils.Common;
import cn.com.sae.utils.DataValidator;

import com.sina.sae.taskqueue.SaeTaskQueue;
import com.sina.sae.taskqueue.TaskQueueItem;

public class Batch extends BaseHttpServlet {

	private static final long serialVersionUID = 5959286771762913867L;

	private static String[][] crawlBookBatchRules = { { "book_id", "Int" },
			{ "src", "Str", "NotEmpty" } };

	private static DataValidator crawlBookBatchValidator = new DataValidator(
			crawlBookBatchRules);

	private void doCrawlBookBatch(Map<String, String[]> params,
			HttpServletResponse response) throws ServletException, IOException {
		Map<String, String> opts = crawlBookBatchValidator
				.validateHttpParamData(params);

		if (opts != null) {
			_writeOKHTMLHeader(response);
			WebSiteCrawler crawler = NovelSearchEngine.getCrawlerByName(opts
					.get("src"));

			Book result = crawler.getBookInfoById(Integer.parseInt(opts
					.get("book_id")));

			SessionFactory factory = HibernateUtil.getSessionFactory(false);
			Session session = factory.openSession();

			Transaction t = session.beginTransaction();
			@SuppressWarnings("unchecked")
			List<Book> books = (List<Book>) session.createCriteria(Book.class)
					.add(Restrictions.eq("url", result.url)).list();

			if (books.size() > 0) {
				Book exist_book = (Book) books.get(0);
				exist_book.name = result.name;
				exist_book.from = result.from;
				exist_book.url = result.url;
				exist_book.img = result.img;
				exist_book.description = result.description;
				session.update(exist_book);
			} else {
				session.save(result);
			}

			t.commit();
			session.close();
			factory.close();

			if (result != null) {
				System.out.println(JSON.encode(result));
			}
		} else {
			_errorOutput(response);
		}
	}

	private static String[][] crawlBookRules = { { "from", "Int" },
			{ "to", "Int" }, { "src", "Str", "NotEmpty" } };

	private static DataValidator crawlBookValidator = new DataValidator(
			crawlBookRules);

	private void doCrawlBook(Map<String, String[]> params,
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Map<String, String> opts = crawlBookValidator
				.validateHttpParamData(params);

		WebSiteCrawler crawler = NovelSearchEngine.getCrawlerByName(opts
				.get("src"));

		if (crawler != null) {
			SaeTaskQueue taskQueue = new SaeTaskQueue("batch_book");
			List<TaskQueueItem> tasks = new ArrayList<TaskQueueItem>();

			if (opts != null) {
				int from = Integer.parseInt(opts.get("from").toString());
				int to = Integer.parseInt(opts.get("to").toString());
				if (to > from) {
					for (int i = from; i < to; i++) {
						TaskQueueItem item = new TaskQueueItem();
						String url = String
								.format("http://%s/batch/book",
										Common.isProduction() ? Common.SAE_PRODUCTION_DOMAIN
												: Common.LOCAL_TEST_DOMAIN);
						item.setUrl(url);
						Map<String, String> p = new HashMap<String, String>();
						p.put("book_id", String.format("%d", i));
						p.put("src", crawler.crawlerName());
						item.setPostStr(p);
						tasks.add(item);
					}
					taskQueue.addTask(tasks);
					boolean b = taskQueue.push();
					if (b) {
						System.out.println(taskQueue.getErrno());
						System.out.println(taskQueue.getErrmsg());
					}
				}
			}
		}
	}

	private static String[][] crawlSectionBatchRules = {
			{ "url", "Str", "NotEmpty" }, { "src", "Str", "NotEmpty" } };

	private static DataValidator crawlSectionBatchValidator = new DataValidator(
			crawlSectionBatchRules);

	private void doCrawlSectionBatch(Map<String, String[]> params,
			HttpServletResponse response) throws ServletException, IOException {
		Map<String, String> opts = crawlSectionBatchValidator
				.validateHttpParamData(params);

		if (opts != null) {
			_writeOKHTMLHeader(response);
			WebSiteCrawler crawler = NovelSearchEngine.getCrawlerByName(opts
					.get("src"));

			SessionFactory factory = HibernateUtil.getSessionFactory(false);
			Session session = factory.openSession();

			Transaction t = session.beginTransaction();
			@SuppressWarnings("unchecked")
			List<Section> sections = (List<Section>) session
					.createCriteria(Section.class)
					.add(Restrictions.eq("url", opts.get("url"))).list();

			if (sections.size() > 0) {
				Section exist_section = sections.get(0);
				Section result = crawler.getSection(opts.get("url"));
				// failed to get current page by link, try to refer the prev row
				if (result == null) {
					@SuppressWarnings("unchecked")
					List<Section> prev_sections = (List<Section>) session
							.createCriteria(Section.class)
							.add(Restrictions.lt("id", exist_section.id))
							.setMaxResults(1).list();
					if (prev_sections.size() > 0) {
						Section prev_section = prev_sections.get(0);
						if (prev_section.nextUrl != null
								&& prev_section.nextUrl.length() > 0) {
							result = crawler.getSection(opts
									.get(prev_section.nextUrl));
							if (result == null) {
								t.commit();
								session.close();
								factory.close();
								return;
							}
						}
					}
				}
				System.out.println(JSON.encode(result));
				exist_section.text = result.text;
				exist_section.url = result.url;
				exist_section.prevUrl = result.prevUrl;
				exist_section.nextUrl = result.nextUrl;
				session.update(exist_section);
			}

			t.commit();
			session.close();
			factory.close();

		} else {
			_errorOutput(response);
		}
	}

	private static String[][] crawlSectionRules = { { "book_id", "Int" } };

	private static DataValidator crawlSectionValidator = new DataValidator(
			crawlSectionRules);

	private void doCrawlSection(Map<String, String[]> params,
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Map<String, String> opts = crawlSectionValidator
				.validateHttpParamData(params);

		SessionFactory factory = HibernateUtil.getSessionFactory(true);
		Session session = factory.openSession();

		if (opts != null) {
			int book_id = Integer.parseInt(opts.get("book_id").toString());
			Book result = (Book) session.get(Book.class, book_id);

			WebSiteCrawler crawler = NovelSearchEngine
					.getCrawlerByName(result.from);
			if (crawler != null) {
				List<SectionInfo> sectionInfos = crawler
						.retriveBookSections(result.url);
				Iterator<SectionInfo> itr = sectionInfos.iterator();

				SaeTaskQueue taskQueue = new SaeTaskQueue("batch_section");
				List<TaskQueueItem> tasks = new ArrayList<TaskQueueItem>();
				Transaction t = session.beginTransaction();
				while (itr.hasNext()) {
					SectionInfo info = itr.next();
					Section s = new Section();
					s.url = info.url;
					s.from = result.from;
					s.title = info.name;
					s.book_id = result.id;
					
					System.out.println(JSON.encode(s));
					@SuppressWarnings("unchecked")
					List<Section> sections = (List<Section>) session
							.createCriteria(Section.class)
							.add(Restrictions.eq("url", s.url)).list();
					boolean update_section = false;
					if (sections == null || sections.size() == 0) {
						session.save(s);
						update_section = true;
					} else {
						Section sec = (Section) sections.get(0);
						if (sec.text == null || sec.text.length() == 0) {
							update_section = true;
						}
					}
					if (update_section) {
						TaskQueueItem item = new TaskQueueItem();
						String url = String
								.format("http://%s/batch/section",
										Common.isProduction() ? Common.SAE_PRODUCTION_DOMAIN
												: Common.LOCAL_TEST_DOMAIN);
						item.setUrl(url);
						Map<String, String> p = new HashMap<String, String>();
						p.put("url", s.url);
						p.put("src", crawler.crawlerName());
						item.setPostStr(p);
						tasks.add(item);
					}
				}
				t.commit();
				session.flush();
				factory.close();
				taskQueue.addTask(tasks);
				boolean b = taskQueue.push();
				if (b) {
					System.out.println(taskQueue.getErrno());
					System.out.println(taskQueue.getErrmsg());
				}
			}
		}
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String[] segments = request.getRequestURI().split("/");
		if (segments.length >= 3 && segments[1].equals("batch")) {
			if (segments[2].equals("book")) {
				doCrawlBookBatch(request.getParameterMap(), response);
				return;
			} else if (segments[2].equals("section")) {
				doCrawlSectionBatch(request.getParameterMap(), response);
				return;
			}
		}

		_errorOutput(response);
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String[] segments = request.getRequestURI().split("/");
		if (segments.length >= 3 && segments[1].equals("batch")) {
			if (segments[2].equals("book")) {
				doCrawlBook(request.getParameterMap(), request, response);
				return;
			} else if (segments[2].equals("section")) {
				doCrawlSection(request.getParameterMap(), request, response);
				return;
			}
		}

		_errorOutput(response);
	}
}
