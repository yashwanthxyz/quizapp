import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String firstName = request.getParameter("first_name");
        String lastName = request.getParameter("last_name");
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Check if required fields are empty
        if (firstName == null || firstName.trim().isEmpty() ||
            lastName == null || lastName.trim().isEmpty() ||
            username == null || username.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            showErrorPage(response, "All fields are required.");
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Create a connection to the database
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/quiz_app", "root", "2003");

            // Insert the user's details into the database
            String query = "INSERT INTO users (first_name, last_name, username, email, password) VALUES (?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, username);
            pstmt.setString(4, email);
            pstmt.setString(5, password);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                // Registration successful
                response.sendRedirect("HomepageServlet");
            } else {
                showErrorPage(response, "Registration failed. Please try again later.");
            }
        } catch (Exception e) {
            showErrorPage(response, "Registration failed. Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
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
        out.println("<html><body><h1>Registration Failed</h1><p>Error: " + errorMessage + "</p></body></html>");
    }
}