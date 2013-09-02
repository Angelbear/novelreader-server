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

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;

import cn.com.sae.db.HibernateUtil;
import cn.com.sae.model.novel.Book;

public class Admin extends BaseHttpServlet {

	private static final long serialVersionUID = -9143190827215838420L;

	@SuppressWarnings("unchecked")
	private void doListBooks(Map<String, String[]> params,
			HttpServletResponse response) {

		Map<String, Object> parameters = new HashMap<String, Object>();
		SessionFactory factory = HibernateUtil.getSessionFactory(true);
		Session session = factory.openSession();
		List<Book> books = session.createCriteria(Book.class)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
		parameters.put("books", books);

		session.close();
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

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String uri = request.getRequestURI();
		if (uri.equals("/admin/book_list")) {
			doListBooks(request.getParameterMap(), response);
		} else {
			_errorOutput(response);
		}
	}
}
