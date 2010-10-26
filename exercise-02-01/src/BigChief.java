import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BigChief extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String selectedServlet = request.getParameter("servlet");
		String name = request.getParameter("name");
		
		if (name != null) {
			if ("GreetingA".equals(selectedServlet))
				request.getRequestDispatcher("GreetingA").forward(request, response);
			else if ("GreetingB".equals(selectedServlet))
				request.getRequestDispatcher("GreetingB").forward(request, response);
		}
		
		response.sendRedirect("error.html");
	}
}
