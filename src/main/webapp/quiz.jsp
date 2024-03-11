<%@ page import="java.util.List" %>
<%@ page import="quizapp.Question" %>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Quiz</title>
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
            text-align: left;
            padding: 10px 0;
        }

        label {
            display: block;
            margin-bottom: 10px;
        }

        input[type="radio"] {
            display: inline-block;
            vertical-align: middle;
            margin-right: 5px;
        }

        .button-container {
            text-align: center;
            margin-top: 20px;
        }

        hr {
            margin: 20px 0;
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
        <h1>Quiz</h1>
        <div class="question">
            <% 
                List<Question> questions = (List<Question>) session.getAttribute("questions");
                int currentQuestionIndex = (int) session.getAttribute("currentQuestionIndex");
                Question currentQuestion = questions.get(currentQuestionIndex);
            %>
            <h2><%= currentQuestion.getQuestion() %></h2>
            <form action="QuizServlet" method="post">
                <% for (int i = 0; i < currentQuestion.getOptions().length; i++) { %>
                    <label>
                        <input type="radio" name="answer" value="<%= i %>"> <%= currentQuestion.getOptions()[i] %>
                    </label>
                <% } %>
                <div class="button-container">
                    <button type="submit"><%= (currentQuestionIndex == questions.size() - 1) ? "Submit" : "Next" %></button>
                </div>
            </form>
        </div>
    </div>
</body>
</html>
