package cn.com.sae;

import httl.Engine;
import httl.Template;
import net.arnx.jsonic.JSON;
import net.arnx.jsonic.JSONException;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import cn.com.sae.db.HibernateUtil;
import cn.com.sae.model.novel.Book;
import cn.com.sae.model.novel.SectionView;
import cn.com.sae.utils.DataValidator;

import com.sina.sae.taskqueue.SaeTaskQueue;

public class Admin extends BaseHttpServlet {

	private static final long serialVersionUID = -9143190827215838420L;

	private static String[][] listSectionsRules = { { "book_id", "Int" },
			{ "page_no", "Int" }, { "json", "Bool" } };

	private static DataValidator listSectionsValidator = new DataValidator(
			listSectionsRules);

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
