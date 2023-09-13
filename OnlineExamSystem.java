package OnlineExam;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

class Question {
    private String text;
    private String[] options;
    private int correctOption;

    public Question(String text, String[] options, int correctOption) {
        this.text = text;
        this.options = options;
        this.correctOption = correctOption;
    }

    public String getText() {
        return text;
    }

    public String[] getOptions() {
        return options;
    }

    public boolean isCorrectAnswer(int selectedOption) {
        return selectedOption == correctOption;
    }
}

class User {
    private String username;
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public boolean authenticate(String enteredPassword) {
        return password.equals(enteredPassword);
    }
}

public class OnlineExamSystem extends JFrame {
    private User currentUser;
    private List<Question> questions;
    private int currentQuestionIndex;
    private int score;
    private Timer timer;
    private JTextArea questionTextArea;
    private ButtonGroup optionsGroup;
    private JButton startButton;
    private JButton submitButton;
    private JLabel timerLabel;
    private JLabel scoreLabel;
    private JPanel optionsPanel;

    public OnlineExamSystem() {
        setTitle("Online Exam System");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create a welcome label
        JLabel welcomeLabel = new JLabel("Welcome to MCQ Exam");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18)); // Customize font
        welcomeLabel.setHorizontalAlignment(JLabel.CENTER); // Center-align the label

        JPanel loginPanel = new JPanel(new FlowLayout());
        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField(15);
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(15);
        JButton loginButton = new JButton("Login");

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                currentUser = new User(username, password);
                JOptionPane.showMessageDialog(null, "Login successful. Welcome, " + currentUser.getUsername());
                initializeQuestions();
                startExam();
            }
        });

        loginPanel.add(usernameLabel);
        loginPanel.add(usernameField);
        loginPanel.add(passwordLabel);
        loginPanel.add(passwordField);
        loginPanel.add(loginButton);

        // Create a panel to hold the welcome label
        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.add(welcomeLabel, BorderLayout.NORTH);

        // Create a panel to hold both the welcome label and the login panel
        JPanel combinedPanel = new JPanel(new BorderLayout());
        combinedPanel.add(welcomePanel, BorderLayout.NORTH);
        combinedPanel.add(loginPanel, BorderLayout.CENTER);

        add(combinedPanel, BorderLayout.CENTER);
    }

    private void initializeQuestions() {
        questions = new ArrayList<>();
        questions.add(new Question("What is the primary purpose of the public static void main(String[] args) method in Java?", new String[]{"To declare variables", "To initialize objects", "To define the main class", "To start the execution of a Java program"}, 3));
        questions.add(new Question("Which keyword is used to create a new instance of a class in Java?", new String[]{"new", "create", "instance", "object"}, 0));
        questions.add(new Question("In Java, which data type is used to store single characters?", new String[]{"int", "char", "float", "string"}, 1));
        questions.add(new Question("What is the result of the following Java expression: `5 + 3 * 2`?", new String[]{"16", "13", "11", "12"}, 2));
        questions.add(new Question("Which of the following statements is true about the ArrayList class in Java?", new String[]{" It is a class for handling primitive data types.", "It is a resizable array that can hold objects.", "It is a built-in data type in Java.", "It can only store a fixed number of elements."}, 1));
        // Add more questions here
    }

    private void startExam() {
        currentQuestionIndex = 0;
        score = 0;
        getContentPane().removeAll();

        JPanel examPanel = new JPanel(new BorderLayout());

        questionTextArea = new JTextArea(5, 20);
        questionTextArea.setLineWrap(true);
        questionTextArea.setWrapStyleWord(true);
        questionTextArea.setEditable(false);
        JScrollPane questionScrollPane = new JScrollPane(questionTextArea);

        optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        optionsGroup = new ButtonGroup();

        JPanel controlPanel = new JPanel(new FlowLayout());
        startButton = new JButton("Start Exam");
        submitButton = new JButton("Submit Answer");
        timerLabel = new JLabel("");
        scoreLabel = new JLabel("Score: 0");

        controlPanel.add(startButton);
        controlPanel.add(submitButton);
        controlPanel.add(timerLabel);
        controlPanel.add(scoreLabel);

        examPanel.add(questionScrollPane, BorderLayout.CENTER);
        examPanel.add(optionsPanel, BorderLayout.EAST);
        examPanel.add(controlPanel, BorderLayout.SOUTH);

        add(examPanel, BorderLayout.CENTER);
        revalidate();

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startButton.setEnabled(false);
                submitButton.setEnabled(true);
                showQuestion();
            }
        });

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitAnswer();
            }
        });

        startCountdownTimer(30); // 30 seconds per question, adjust as needed
    }

    private void showQuestion() {
        if (currentQuestionIndex < questions.size()) {
            Question currentQuestion = questions.get(currentQuestionIndex);
            questionTextArea.setText("Question " + (currentQuestionIndex + 1) + ": " + currentQuestion.getText());

            String[] options = currentQuestion.getOptions();
            optionsGroup.clearSelection();
            optionsPanel.removeAll();

            for (int i = 0; i < options.length; i++) {
                JRadioButton optionButton = new JRadioButton(options[i]);
                optionButton.setActionCommand(Integer.toString(i));
                optionsGroup.add(optionButton);
                optionButton.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Add padding
                optionsPanel.add(optionButton);
            }

            optionsPanel.setLayout(new GridLayout(0, 1)); // Vertically spaced
            optionsPanel.revalidate(); // Refresh the options panel
        } else {
            finishExam();
        }
    }

    private void submitAnswer() {
        if (currentQuestionIndex < questions.size()) {
            Question currentQuestion = questions.get(currentQuestionIndex);
            int selectedOption = -1;

            for (Enumeration<AbstractButton> buttons = optionsGroup.getElements(); buttons.hasMoreElements();) {
                AbstractButton button = buttons.nextElement();
                if (button.isSelected()) {
                    selectedOption = Integer.parseInt(button.getActionCommand());
                    break;
                }
            }

            if (selectedOption != -1) {
                if (currentQuestion.isCorrectAnswer(selectedOption)) {
                    score++;
                }

                currentQuestionIndex++;
                scoreLabel.setText("Score: " + score);
                showQuestion();
            } else {
                JOptionPane.showMessageDialog(null, "Please select an answer.");
            }
        }
    }

    private void startCountdownTimer(int seconds) {
        int initialDelay = 0;
        int period = 1000;
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            private int remainingTime = seconds;

            @Override
            public void run() {
                if (remainingTime > 0) {
                    timerLabel.setText("Time remaining: " + remainingTime + " seconds");
                    remainingTime--;
                } else {
                    timerLabel.setText("Time's up!");
                    timer.cancel();
                }
            }
        }, initialDelay, period);
    }

    private void finishExam() {
        getContentPane().removeAll();
        JLabel finishLabel = new JLabel("Exam finished. Your score is: " + score);
        finishLabel.setHorizontalAlignment(JLabel.CENTER);
        add(finishLabel, BorderLayout.CENTER);

        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        JPanel exitPanel = new JPanel(new FlowLayout());
        exitPanel.add(exitButton);
        add(exitPanel, BorderLayout.SOUTH);

        revalidate();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new OnlineExamSystem().setVisible(true);
            }
        });
        
    }
    }
