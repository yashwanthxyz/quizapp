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

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String emailOrUsername = request.getParameter("emailOrUsername");
        String password = request.getParameter("password");

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Create a connection to the database
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/quiz_app", "root", "2003");

            // Check if the email/username and password match a record in the users table
            String query = "SELECT * FROM users WHERE (email = ? OR username = ?) AND password = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, emailOrUsername);
            pstmt.setString(2, emailOrUsername);
            pstmt.setString(3, password);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                // Login successful
                HttpSession session = request.getSession();
                session.setAttribute("userId", rs.getInt("id")); // Set the userId attribute
                session.setAttribute("username", rs.getString("username"));
                session.setAttribute("highscore", rs.getInt("highscore"));
                response.sendRedirect("HomepageServlet");
            } else {
                // Login failed
                showErrorPage(response, "Invalid username or password");
            }
        } catch (Exception e) {
            showErrorPage(response, "Login failed. Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void showErrorPage(HttpServletResponse response, String errorMessage) throws IOException {
        PrintWriter out = response.getWriter();
        out.println("<html><body><h1>Login Failed</h1><p>Error: " + errorMessage + "</p></body></html>");
    }
}
