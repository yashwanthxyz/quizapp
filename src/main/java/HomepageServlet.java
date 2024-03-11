import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/HomepageServlet")
public class HomepageServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("username") != null) {
            int highscore = (int) session.getAttribute("highscore");
            if (session.getAttribute("updateHighscore") != null && (boolean) session.getAttribute("updateHighscore")) {
                Integer userId = (Integer) session.getAttribute("userId");
                if (userId != null && userId != -1) {
                    int currentScore = (int) session.getAttribute("score");
                    int currentHighscore = getHighscoreForUser(userId);
                    if (currentScore > currentHighscore) {
                        updateHighscoreForUser(userId, currentScore);
                        session.setAttribute("highscore", currentScore);
                    }
                }
                session.setAttribute("updateHighscore", false);
            }

            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Quiz App Homepage</title>");
            out.println("<link rel=\"icon\" href=\"icon.png\" type=\"image/png\">");
            out.println("<style>");
            out.println("body {font-family: Arial, sans-serif;margin: 0;padding: 0;background-color: #f1f1f1;color: #333;}");
            out.println(".container {width: 800px;margin: 20px auto;padding: 20px;background-color: #fff;border-radius: 10px;box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);}");
            out.println(".header {background-color: #333;color: white;text-align: right;padding: 10px;border-top-left-radius: 10px;border-top-right-radius: 10px;margin-bottom: 20px;}");
            out.println("h1 {color: #333;margin-top: 20px;}");
            out.println(".content {text-align: center;}");
            out.println(".button {padding: 10px 20px;background-color: #333;color: white;border: 2px solid #333;border-radius: 5px;text-decoration: none;display: inline-block;margin-top: 20px;cursor: pointer;}");
            out.println(".button:hover {background-color: #555;}");
            out.println(".highscore {margin-top: 20px;}");
            out.println(".user-details {text-align: left;margin-top: 10px;margin-left: 10px;font-size: 20px;}");
            out.println(".user-details:hover {color: #555;cursor: pointer;}");
            out.println(".logout-button {padding: 5px 10px;background-color: transparent;color: white;border: 2px solid white;border-radius: 5px;cursor: pointer;}");
            out.println(".logout-button:hover {background-color: white;color: #333;}");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<div class=\"container\">");
            out.println("<div class=\"header\">");
            out.println("<button class=\"logout-button\" onclick=\"location.href='LogoutServlet'\">Logout</button>");
            out.println("</div>");
            out.println("<h2>Welcome, " + session.getAttribute("username") + " <img src='https://em-content.zobj.net/source/apple/391/waving-hand_medium-light-skin-tone_1f44b-1f3fc_1f3fc.png' width='30' height='30' style='vertical-align: middle;'></h2>");
            out.println("<div class=\"content\">");
            out.println("<p><a class=\"button\" href=\"QuizServlet\">Launch Quiz</a></p>");
            out.println("<p class=\"highscore\"><strong>Highscore: </strong>" + session.getAttribute("highscore") + "</p>");
            out.println("</div>");
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");
        } else {
            response.sendRedirect("login.html");
        }
    }

    private int getHighscoreForUser(int userId) {
        int highscore = 0;
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/quiz_app", "root", "2003")) {
            String sql = "SELECT highscore FROM users WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, userId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        highscore = resultSet.getInt("highscore");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return highscore;
    }

    private void updateHighscoreForUser(int userId, int score) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/quiz_app", "root", "2003")) {
            String sql = "UPDATE users SET highscore = ? WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, score);
                statement.setInt(2, userId);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
