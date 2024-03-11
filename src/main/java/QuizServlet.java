import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import quizapp.Question;

@WebServlet("/QuizServlet")
public class QuizServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Question> questions = new ArrayList<>();
        questions.add(new Question("Which movie won the Academy Award for Best Picture in 2020?",
                new String[]{"Parasite", "1917", "Joker", "Once Upon a Time in Hollywood"}, 0));
        questions.add(new Question("Who is the main character in the TV series 'Breaking Bad'?",
                new String[]{"Walter White", "Jesse Pinkman", "Saul Goodman", "Skyler White"}, 0));
        questions.add(new Question("In the anime 'Naruto', what is the name of Naruto's rival?",
                new String[]{"Sasuke Uchiha", "Sakura Haruno", "Kakashi Hatake", "Hinata Hyuga"}, 0));
        questions.add(new Question("Which movie is based on a Marvel Comics character who is a master of kung fu?",
                new String[]{"Iron Man", "Doctor Strange", "Shang-Chi and the Legend of the Ten Rings", "Black Widow"}, 2));
        questions.add(new Question("What is the name of the fantasy TV series based on the book series by George R. R. Martin?",
                new String[]{"The Crown", "The Witcher", "Game of Thrones", "Outlander"}, 2));
        questions.add(new Question("In the anime 'Attack on Titan', what is the name of the city that is humanity's last stronghold against the Titans?",
                new String[]{"Wall Rose", "Wall Maria", "Wall Sheena", "Wall Sina"}, 3));
        questions.add(new Question("Which movie directed by Christopher Nolan features a dream-sharing technology?",
                new String[]{"Inception", "Interstellar", "Dunkirk", "The Prestige"}, 0));
        questions.add(new Question("Who is the creator of the animated TV series 'The Simpsons'?",
                new String[]{"Seth MacFarlane", "Matt Groening", "Trey Parker", "Mike Judge"}, 1));
        questions.add(new Question("In the anime 'One Piece', what is the name of the main character who seeks the legendary One Piece treasure?",
                new String[]{"Luffy D. Monkey", "Zoro Roronoa", "Sanji Vinsmoke", "Nami"}, 0));
        questions.add(new Question("Which movie won the first Academy Award for Best Animated Feature?",
                new String[]{"Shrek", "Monsters, Inc.", "Toy Story 2", "Shrek 2"}, 0));

        Collections.shuffle(questions);

        HttpSession session = request.getSession();
        session.setAttribute("questions", questions);
        session.setAttribute("currentQuestionIndex", 0);
        session.setAttribute("score", 0);
        session.setAttribute("attemptedQuestions", new boolean[questions.size()]);

        response.sendRedirect("quiz.jsp");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        @SuppressWarnings("unchecked")
        List<Question> questions = (List<Question>) session.getAttribute("questions");
        int currentQuestionIndex = (int) session.getAttribute("currentQuestionIndex");
        Question currentQuestion = questions.get(currentQuestionIndex);

        boolean[] attemptedQuestions = (boolean[]) session.getAttribute("attemptedQuestions");

        String userAnswerStr = request.getParameter("answer");
        if (userAnswerStr != null && userAnswerStr.matches("\\d+")) {
            int userAnswer = Integer.parseInt(userAnswerStr);
            attemptedQuestions[currentQuestionIndex] = true;
            if (userAnswer == currentQuestion.getCorrectAnswerIndex()) {
                int score = (int) session.getAttribute("score");
                session.setAttribute("score", score + 1);
            }
        } else {
            attemptedQuestions[currentQuestionIndex] = false;
        }

        // Move to the next question or end the quiz
        currentQuestionIndex++;
        if (currentQuestionIndex < questions.size()) {
            session.setAttribute("currentQuestionIndex", currentQuestionIndex);
            session.setAttribute("attemptedQuestions", attemptedQuestions);
            response.sendRedirect("quiz.jsp");
        } else {
            // Calculate number of attempted questions
            int numAttempted = 0;
            for (boolean attempted : attemptedQuestions) {
                if (attempted) {
                    numAttempted++;
                }
            }
            // Calculate number of unanswered questions
            int numUnanswered = questions.size() - numAttempted;
            session.setAttribute("numAttempted", numAttempted);
            session.setAttribute("numUnanswered", numUnanswered);
            session.setAttribute("attemptedQuestions", attemptedQuestions);

            // Update high score in MySQL if the current score is higher
            Integer userId = (Integer) session.getAttribute("userId");
            if (userId != null && userId != -1) {
                int currentScore = (int) session.getAttribute("score");
                int highscore = getHighscoreForUser(userId);
                if (currentScore > highscore) {
                    updateHighscoreForUser(userId, currentScore);
                    session.setAttribute("highscore", currentScore);
                }
            }

            response.sendRedirect("quiz-summary.jsp");
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
