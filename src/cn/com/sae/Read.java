package cn.com.sae;

import httl.Engine;
import httl.Template;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.arnx.jsonic.JSON;

import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import cn.com.sae.db.HibernateUtil;
import cn.com.sae.model.novel.Book;
import cn.com.sae.model.novel.Section;
import cn.com.sae.model.novel.SectionView;
import cn.com.sae.utils.DataValidator;

public class Read extends BaseHttpServlet {

	private static final long serialVersionUID = -5038505956413020245L;

	private static String[][] viewSectionRules = { { "section_id", "Int" } };

	private static DataValidator viewSectionValidator = new DataValidator(
			viewSectionRules);

	private void doViewSection(Map<String, String[]> params,
			HttpServletResponse response) throws ServletException, IOException {
		Map<String, String> opts = viewSectionValidator
				.validateHttpParamData(params);
		if (opts != null) {

			SessionFactory factory = HibernateUtil.getSessionFactory(true);
			Session session = factory.openSession();

			Section record = (Section) session.get(Section.class,
					Integer.parseInt(opts.get("section_id")));
			if (record != null) {
				_writeOKHTMLHeader(response);
				record.text = record.text.replaceAll("\n", "</p><p>");
				response.getWriter().println(JSON.encode(record));
				return;
			}
		} else {
			_errorOutput(response);
		}
	}

	private static String[][] viewBookRules = { { "book_id", "Int" } };

	private static DataValidator viewBookValidator = new DataValidator(
			viewBookRules);

	@SuppressWarnings("unchecked")
	private void doViewBook(Map<String, String[]> params,
			HttpServletResponse response) throws ServletException, IOException {
		Map<String, String> opts = viewBookValidator
				.validateHttpParamData(params);

		if (opts != null) {

			SessionFactory factory = HibernateUtil.getSessionFactory(true);
			Session session = factory.openSession();

			Book record = (Book) session.get(Book.class,
					Integer.parseInt(opts.get("book_id")));

			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("book", record);
			Engine engine = Engine.getEngine();
			Template template;
			try {
				template = engine.getTemplate("/view.httl");
				template.render(parameters, response.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		} else {
			_errorOutput(response);
		}
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String uri = request.getRequestURI();
		if (uri.equals("/view/book")) {
			doViewBook(request.getParameterMap(), response);
		} else if (uri.equals("/view/section")) {
			doViewSection(request.getParameterMap(), response);
		} else {
			_errorOutput(response);
		}
	}

}
