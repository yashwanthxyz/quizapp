<%@ page import="java.util.List" %>
<%@ page import="quizapp.Question" %>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Quiz Summary</title>
    <link rel="icon" href="icon.png" type="image/png">
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 0;
            background-color: #f1f1f1;
            color: #333;
        }

        .quiz-container {
            max-width: 800px;
            margin: 20px auto;
            padding: 20px;
            background-color: #fff;
            border-radius: 10px;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
        }

        h1 {
            text-align: center;
            margin-bottom: 20px;
        }

        .question {
            margin-bottom: 10px;
        }

        .answer {
            margin-left: 20px;
        }

        .button-container {
            text-align: center;
            margin-top: 20px;
        }

        button {
            padding: 10px 20px;
            background-color: #333;
            color: white;
            border: 2px solid #333;
            border-radius: 5px;
            cursor: pointer;
        }

        button:hover {
            background-color: #555;
        }
    </style>
</head>
<body>
    <div class="quiz-container">
        <h1>Quiz Summary</h1>
        <% 
            List<Question> questions = (List<Question>) session.getAttribute("questions");
            boolean[] attemptedQuestions = (boolean[]) session.getAttribute("attemptedQuestions");
            int score = (int) session.getAttribute("score");
            int questionsAttempted = 0;
            for (boolean attempted : attemptedQuestions) {
                if (attempted) {
                    questionsAttempted++;
                }
            }
        %>
        <p>Questions attempted: <%= questionsAttempted %> out of <%= questions.size() %></p>
        <p>Score: <%= score %></p>
        <div class="button-container">
            <form action="HomepageServlet">
                <button type="submit">Go to Home</button>
            </form>
        </div>
    </div>
</body>
</html>
