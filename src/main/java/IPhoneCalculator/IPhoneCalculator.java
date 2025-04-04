package IPhoneCalculator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

public class IPhoneCalculator extends JFrame implements ActionListener {

    private static final Color COLOR_BACKGROUND = new Color(40, 40, 40);
    private static final Color COLOR_DISPLAY_BG = COLOR_BACKGROUND;
    private static final Color COLOR_DISPLAY_FG = Color.WHITE;
    private static final Color COLOR_DARK_GRAY_BG = new Color(100, 100, 100);
    private static final Color COLOR_DARK_GRAY_FG = Color.WHITE;
    private static final Color COLOR_LIGHT_GRAY_BG = new Color(160, 160, 160);
    private static final Color COLOR_LIGHT_GRAY_FG = Color.BLACK;
    private static final Color COLOR_ORANGE_BG = new Color(255, 159, 10);
    private static final Color COLOR_ORANGE_FG = Color.WHITE;

    private static final Font FONT_DISPLAY = new Font("Arial", Font.PLAIN, 60);
    private static final Font FONT_BUTTON = new Font("Arial", Font.BOLD, 24);

    private JTextField displayField;
    private JPanel buttonPanel;

    private String currentInput = "0";
    private String operator = "";
    private double firstOperand = 0;
    private boolean startNewInput = true;

    private DecimalFormat decimalFormat = new DecimalFormat("#.##########");


    public IPhoneCalculator() {
        setTitle("Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(COLOR_BACKGROUND);
        setLayout(new BorderLayout(0, 0));

        displayField = new JTextField("0", 12);
        displayField.setEditable(false);
        displayField.setBackground(COLOR_DISPLAY_BG);
        displayField.setForeground(COLOR_DISPLAY_FG);
        displayField.setHorizontalAlignment(JTextField.RIGHT);
        displayField.setFont(FONT_DISPLAY);
        displayField.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(displayField, BorderLayout.NORTH);

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(5, 4, 5, 5));
        buttonPanel.setBackground(COLOR_BACKGROUND);
        buttonPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        String[] buttonLabels = {
                "AC", "+/-", "", "÷",
                "7", "8", "9", "×",
                "4", "5", "6", "-",
                "1", "2", "3", "+",
                "0", ".", "", "=",
        };

        for (String label : buttonLabels) {
            if (label.isEmpty()) {
                JPanel placeholder = new JPanel();
                placeholder.setPreferredSize(new Dimension(50, 50));
                placeholder.setOpaque(false);
                buttonPanel.add(placeholder);
            } else {
                JButton button = createStyledButton(label);
                button.addActionListener(this);
                buttonPanel.add(button);
            }
        }

        add(buttonPanel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }

    private JButton createStyledButton(String label) {
        JButton button = new JButton(label);
        button.setFont(FONT_BUTTON);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);

        switch (label) {
            case "÷":
            case "×":
            case "-":
            case "+":
            case "=":
                button.setBackground(COLOR_ORANGE_BG);
                button.setForeground(COLOR_ORANGE_FG);
                break;
            case "AC":
            case "+/-":
                button.setBackground(COLOR_LIGHT_GRAY_BG);
                button.setForeground(COLOR_LIGHT_GRAY_FG);
                break;
            default:
                if (!label.isEmpty()) {
                    button.setBackground(COLOR_DARK_GRAY_BG);
                    button.setForeground(COLOR_DARK_GRAY_FG);
                }
                break;
        }
        return button;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        try {
            switch (command) {
                case "0": case "1": case "2": case "3": case "4":
                case "5": case "6": case "7": case "8": case "9":
                    handleNumberInput(command);
                    break;
                case ".":
                    handleDecimalInput();
                    break;
                case "÷": case "×": case "-": case "+":
                    handleOperatorInput(command);
                    break;
                case "=":
                    handleEqualsInput();
                    break;
                case "AC":
                    handleClearInput();
                    break;
                case "+/-":
                    handlePlusMinusInput();
                    break;
            }
            updateDisplay();
        } catch (Exception ex) {
            displayField.setText("Error");
            resetCalculatorState();
            currentInput = "Error";
            startNewInput = true;
        }
    }

    private void handleNumberInput(String number) {
        if (currentInput.equals("Error")) return;

        if (startNewInput) {
            currentInput = number;
            startNewInput = false;
        } else {
            if (currentInput.equals("0")) {
                currentInput = number;
            } else {
                if (currentInput.length() < 12) {
                    currentInput += number;
                }
            }
        }
    }

    private void handleDecimalInput() {
        if (currentInput.equals("Error")) return;

        if (startNewInput) {
            currentInput = "0.";
            startNewInput = false;
        } else if (!currentInput.contains(".")) {
            if (currentInput.length() < 12) {
                currentInput += ".";
            }
        }
    }

    private void handleOperatorInput(String op) {
        if (currentInput.equals("Error")) return;

        if (!operator.isEmpty() && !startNewInput) {
            handleEqualsInput();
            if (currentInput.equals("Error")) return;
        }

        try {
            if (!currentInput.equals("Error")) {
                firstOperand = Double.parseDouble(currentInput);
                operator = op;
                startNewInput = true;
            }
        } catch (NumberFormatException e) {
            displayField.setText("Error");
            resetCalculatorState();
            currentInput = "Error";
            startNewInput = true;
        }
    }

    private void handleEqualsInput() {
        if (currentInput.equals("Error") || operator.isEmpty() || startNewInput) {
            return;
        }

        try {
            double secondOperand = Double.parseDouble(currentInput);
            double result = calculate(firstOperand, secondOperand, operator);

            currentInput = formatResult(result);
            operator = "";
            startNewInput = true;

        } catch (NumberFormatException e) {
            displayField.setText("Error");
            resetCalculatorState();
            currentInput = "Error";
            startNewInput = true;
        } catch (ArithmeticException ae) {
            displayField.setText("Error");
            resetCalculatorState();
            currentInput = "Error";
            startNewInput = true;
        }
    }

    private void handleClearInput() {
        resetCalculatorState();
        updateDisplay();
    }

    private void resetCalculatorState() {
        currentInput = "0";
        firstOperand = 0;
        operator = "";
        startNewInput = true;
        displayField.setFont(FONT_DISPLAY);
    }

    private void handlePlusMinusInput() {
        if (currentInput.equals("Error") || currentInput.equals("0")) return;
        if (startNewInput || currentInput.isEmpty()) return;

        try {
            double value = Double.parseDouble(currentInput);
            value = -value;
            currentInput = formatResult(value);
        } catch (NumberFormatException e) {
            displayField.setText("Error");
            resetCalculatorState();
            currentInput = "Error";
            startNewInput = true;
        }
    }

    private double calculate(double num1, double num2, String op) {
        switch (op) {
            case "+":
                return num1 + num2;
            case "-":
                return num1 - num2;
            case "×":
                return num1 * num2;
            case "÷":
                if (num2 == 0) {
                    throw new ArithmeticException("Division by zero");
                }
                return num1 / num2;
            default:
                System.err.println("Warning: Invalid operator encountered: " + op);
                return Double.NaN;
        }
    }

    private void updateDisplay() {
        String textToDisplay = currentInput;
        int threshold = 8;
        if (textToDisplay.contains(".")) threshold++;
        if (textToDisplay.contains("-")) threshold++;

        if (textToDisplay.length() > threshold) {
            if (textToDisplay.length() > threshold + 3) {
                displayField.setFont(FONT_DISPLAY.deriveFont(30f));
            } else {
                displayField.setFont(FONT_DISPLAY.deriveFont(40f));
            }
        } else {
            displayField.setFont(FONT_DISPLAY);
        }
        displayField.setText(textToDisplay);
    }

    private String formatResult(double result) {
        if (Double.isNaN(result) || Double.isInfinite(result)) {
            resetCalculatorState();
            return "Error";
        }

        String formatted = decimalFormat.format(result);

        if (formatted.equals("-0")) {
            return "0";
        }

        int displayLimit = 12;
        if (formatted.length() > displayLimit) {
            System.err.println("Warning: Result too long for display: " + formatted);
            resetCalculatorState();
            return "Error";
        }
        return formatted;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            IPhoneCalculator calculator = new IPhoneCalculator();
            calculator.setVisible(true);
        });
    }
}