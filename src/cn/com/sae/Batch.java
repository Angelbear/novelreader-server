package cn.com.sae;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
import cn.com.sae.utils.DataValidator;

import com.sina.sae.taskqueue.SaeTaskQueue;
import com.sina.sae.taskqueue.TaskQueueItem;

public class Batch extends BaseHttpServlet {

	private static final long serialVersionUID = 5959286771762913867L;

	private static String[][] crawlBookBatchRules = { { "book_id", "Int" },
			{ "src", "Str", "NotEmpty" } };

	private static DataValidator crawlBookBatchValidator = new DataValidator(
			crawlBookBatchRules);

	private void doCrawlBook(Map<String, String[]> params,
			HttpServletResponse response) throws ServletException, IOException {
		Map<String, String> opts = crawlBookBatchValidator
				.validateHttpParamData(params);

		if (opts != null) {
			_writeOKHTMLHeader(response);
			WebSiteCrawler crawler = NovelSearchEngine.getCrawlerByName(opts
					.get("src"));

			Book result = crawler.getBookInfoById(Integer.parseInt(opts
					.get("book_id")));

			SessionFactory factory =  HibernateUtil.getSessionFactory(false);
			Session session = factory
					.openSession();
			
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

	private void doCrawlBookBatch(Map<String, String[]> params,
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Map<String, String> opts = crawlBookValidator
				.validateHttpParamData(params);

		WebSiteCrawler crawler = NovelSearchEngine.getCrawlerByName(opts
				.get("src"));

		if (crawler != null) {
			SaeTaskQueue taskQueue = new SaeTaskQueue(crawler.crawlerName());
			List<TaskQueueItem> tasks = new ArrayList<TaskQueueItem>();

			if (opts != null) {
				int from = Integer.parseInt(opts.get("from").toString());
				int to = Integer.parseInt(opts.get("to").toString());
				if (to > from) {
					for (int i = from; i < to; i++) {
						TaskQueueItem item = new TaskQueueItem();
						item.setUrl("http://0.0.0.0:8080/batch/single");
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

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String[] segments = request.getRequestURI().split("/");
		if (segments.length >= 3 && segments[1].equals("batch")) {
			if (segments[2].equals("single")) {
				doCrawlBook(request.getParameterMap(), response);
				return;
			}
		}

		_errorOutput(response);
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String[] segments = request.getRequestURI().split("/");
		if (segments.length >= 3 && segments[1].equals("batch")) {
			if (segments[2].equals("batch")) {
				doCrawlBookBatch(request.getParameterMap(), request, response);
				return;
			}
		}

		_errorOutput(response);
	}
}
