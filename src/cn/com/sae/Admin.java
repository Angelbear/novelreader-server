package cn.com.sae;

import httl.Engine;
import httl.Template;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.arnx.jsonic.JSON;
import net.arnx.jsonic.JSONException;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import cn.com.sae.controller.NovelSearchEngine;
import cn.com.sae.crawler.WebSiteCrawler;
import cn.com.sae.db.HibernateUtil;
import cn.com.sae.model.novel.Book;
import cn.com.sae.model.novel.Section;
import cn.com.sae.model.novel.SectionView;
import cn.com.sae.proxy.FilterInvocationHandler;
import cn.com.sae.utils.DataValidator;
import cn.com.sae.utils.ProxyUtils;

import com.sina.sae.taskqueue.SaeTaskQueue;

public class Admin extends BaseHttpServlet {

	private static final long serialVersionUID = -9143190827215838420L;

	private static String[][] listSectionsRules = { { "book_id", "Int" },
			{ "page_no", "Int" }, { "json", "Bool" } };

	private static DataValidator listSectionsValidator = new DataValidator(
			listSectionsRules);

	public void init() {
		NovelSearchEngine.init(this.getServletConfig());
	}

	@SuppressWarnings("unchecked")
	private void doListSections(Map<String, String[]> params,
			HttpServletResponse response) {

		Map<String, String> opts = listSectionsValidator
				.validateHttpParamData(params);

		if (opts != null) {
			Map<String, Object> parameters = new HashMap<String, Object>();
			SessionFactory factory = HibernateUtil.getSessionFactory(true);
			Session session = factory.openSession();

			int page_no = -1;
			if (opts.get("page_no") != null) {
				page_no = Integer.parseInt(opts.get("page_no").toString());
			}

			int book_id = Integer.parseInt(opts.get("book_id").toString());
			Book result = (Book) session.get(Book.class, book_id);

			List<SectionView> sections;
			if (page_no >= 0) {
				sections = session.createCriteria(SectionView.class)
						.add(Restrictions.eq("book_id", book_id))
						.addOrder(Order.asc("id")).setMaxResults(10)
						.setFirstResult(10 * page_no).list();
			} else {
				sections = result.sections;
			}
			parameters.put("book", result);
			parameters.put("sections", sections);

			if (Integer.parseInt(opts.get("json")) == 1) {
				_writeOKHTMLHeader(response);
				try {
					response.getWriter().println(JSON.encode(sections));
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				Engine engine = Engine.getEngine();
				Template template;
				try {
					template = engine.getTemplate("/sections.httl");
					template.render(parameters, response.getOutputStream());
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void doListBooks(Map<String, String[]> params,
			HttpServletResponse response) {

		Map<String, Object> parameters = new HashMap<String, Object>();
		SessionFactory factory = HibernateUtil.getSessionFactory(true);
		Session session = factory.openSession();
		List<Book> books = session.createCriteria(Book.class)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
		parameters.put("books", books);

		Engine engine = Engine.getEngine();
		Template template;
		try {
			template = engine.getTemplate("/books.httl");
			template.render(parameters, response.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private void doQueryTaskQueue(Map<String, String[]> params,
			HttpServletResponse response) {

		Map<String, Object> parameters = new HashMap<String, Object>();

		SaeTaskQueue taskQueue1 = new SaeTaskQueue("batch_book");
		SaeTaskQueue taskQueue2 = new SaeTaskQueue("batch_section");
		parameters.put("currentLength1",
				Integer.valueOf(taskQueue1.getCurLength()));
		parameters.put("currentLength2",
				Integer.valueOf(taskQueue2.getCurLength()));
		parameters.put(
				"maxLength1",
				Integer.valueOf(taskQueue1.getLeftLength()
						+ taskQueue1.getCurLength()));
		parameters.put(
				"maxLength2",
				Integer.valueOf(taskQueue2.getLeftLength()
						+ taskQueue2.getCurLength()));

		Engine engine = Engine.getEngine();
		Template template;
		try {
			template = engine.getTemplate("/taskqueue.httl");
			template.render(parameters, response.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private static String[][] updateBookBatchRules = { { "book_id", "Int" } };

	private static DataValidator updateBookBatchValidator = new DataValidator(
			updateBookBatchRules);

	private void doUpdateBookBatch(Map<String, String[]> params,
			HttpServletResponse response) throws ServletException, IOException {
		Map<String, String> opts = updateBookBatchValidator
				.validateHttpParamData(params);

		if (opts != null) {
			SessionFactory factory = HibernateUtil.getSessionFactory(true);
			Session session = factory.openSession();

			Book record = (Book) session.get(Book.class,
					Integer.parseInt(opts.get("book_id")));
			session.close();
			if (record != null) {
				factory = HibernateUtil.getSessionFactory(false);
				session = factory.openSession();
				WebSiteCrawler crawler = NovelSearchEngine
						.getCrawlerByName(record.from);
				WebSiteCrawler originCrawler = (WebSiteCrawler) ProxyUtils
						.unwrapProxy(crawler);
				WebSiteCrawler filterCrawler = (WebSiteCrawler) ProxyUtils
						.createProxy(WebSiteCrawler.class,
								new FilterInvocationHandler(originCrawler));
				Book result = filterCrawler
						.getBookInfoFromSearchResult(record.url);
				if (result != null) {
					record.name = result.name;
					record.img = result.img;
					record.description = result.description;
					session.update(record);

					_writeOKHTMLHeader(response);
					result.id = record.id;
					response.getWriter().println(JSON.encode(result));
				}
				session.flush();
			}
		} else {
			_errorOutput(response);
		}
	}

	private static String[][] updateSectionBatchRules = { { "section_id", "Int" } };

	private static DataValidator updateSectionBatchValidator = new DataValidator(
			updateSectionBatchRules);

	@SuppressWarnings("unchecked")
	private void doUpdateSectionBatch(Map<String, String[]> params,
			HttpServletResponse response) throws ServletException, IOException {
		Map<String, String> opts = updateSectionBatchValidator
				.validateHttpParamData(params);

		if (opts != null) {
			SessionFactory factory = HibernateUtil.getSessionFactory(true);
			Session session = factory.openSession();

			Section record = (Section) session.get(Section.class,
					Integer.parseInt(opts.get("section_id")));
			session.close();
			if (record != null) {
				factory = HibernateUtil.getSessionFactory(false);
				session = factory.openSession();
				WebSiteCrawler crawler = NovelSearchEngine
						.getCrawlerByName(record.from);
				WebSiteCrawler originCrawler = (WebSiteCrawler) ProxyUtils
						.unwrapProxy(crawler);
				WebSiteCrawler filterCrawler = (WebSiteCrawler) ProxyUtils
						.createProxy(WebSiteCrawler.class,
								new FilterInvocationHandler(originCrawler));
				Section result = null;
				if (record.url != null) {
					result = filterCrawler.getSection(record.url);
				} else {
					List<Section> prev_sections = (List<Section>) session
							.createCriteria(Section.class)
							.add(Restrictions.lt("id", record.id))
							.addOrder(Order.desc("id")).setMaxResults(1).list();
					if (prev_sections.size() > 0) {
						Section prev_section = prev_sections.get(0);
						if (prev_section.nextUrl != null
								&& prev_section.nextUrl.length() > 0) {
							result = filterCrawler
									.getSection(prev_section.nextUrl);
						}
					}
				}

				if (result != null) {
					record.text = result.text;
					record.url = result.url;
					record.prevUrl = result.prevUrl;
					record.nextUrl = result.nextUrl;
					session.update(record);

					_writeOKHTMLHeader(response);
					result.id = record.id;
					response.getWriter().println(JSON.encode(result));
				}
				session.flush();
			}
		} else {
			_errorOutput(response);
		}
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String uri = request.getRequestURI();
		if (uri.equals("/admin/update_book")) {
			doUpdateBookBatch(request.getParameterMap(), response);
		} else if (uri.equals("/admin/update_section")) {
			doUpdateSectionBatch(request.getParameterMap(), response);
		} else {
			_errorOutput(response);
		}
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String uri = request.getRequestURI();
		if (uri.equals("/admin/book_list")) {
			doListBooks(request.getParameterMap(), response);
		} else if (uri.equals("/admin/section_list")) {
			doListSections(request.getParameterMap(), response);
		} else if (uri.equals("/admin/task_queue")) {
			doQueryTaskQueue(request.getParameterMap(), response);
		} else {
			_errorOutput(response);
		}
	}
}
