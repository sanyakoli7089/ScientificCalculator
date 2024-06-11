import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class ScientificCalculator extends JFrame{
    private JTextField display;

    public ScientificCalculator(){
        super("Scientific Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 500);
        setLayout(new BorderLayout());

        JPanel leftFunctionPanel = new JPanel(new GridLayout(6, 1));
        String[] leftFunctions = {"sin", "cos", "tan", "sqrt", "log", "("};
        for(String leftFunction : leftFunctions){
            JButton bt = new JButton(leftFunction);
            bt.setFont(new Font("Arial", Font.PLAIN, 16));
            bt.addActionListener(new ButtonClickListener());
            leftFunctionPanel.add(bt);
        }

        JPanel numberPanel = new JPanel(new GridLayout(4, 3));
        String[] numbers = {"7", "8", "9", "4", "5", "6", "1", "2", "3", "0", ".", "C"};
        for(String number : numbers) {
            JButton bt = new JButton(number);
            bt.setFont(new Font("Arial", Font.PLAIN, 12));
            bt.addActionListener(new ButtonClickListener());
            numberPanel.add(bt);
        }

        JPanel rightFunctionPanel = new JPanel(new GridLayout(6, 1));
        String[] rightFunctions = {"/", "*", "-", "+", "=", ")"};
        for(String rightFunction : rightFunctions){
            JButton bt = new JButton(rightFunction);
            bt.setFont(new Font("Arial", Font.PLAIN, 16));
            bt.addActionListener(new ButtonClickListener());
            rightFunctionPanel.add(bt);
        }

        display = new JTextField();
        display.setEditable(true);
        display.setFont(new Font("Arial", Font.PLAIN, 20));
        add(display, BorderLayout.NORTH);

        JPanel CalculatorPanel = new JPanel(new BorderLayout());
        CalculatorPanel.add(leftFunctionPanel, BorderLayout.WEST);
        CalculatorPanel.add(numberPanel, BorderLayout.CENTER);
        CalculatorPanel.add(rightFunctionPanel, BorderLayout.EAST);
        add(CalculatorPanel, BorderLayout.CENTER);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private class ButtonClickListener implements ActionListener {
        public void actionPerformed(ActionEvent e){
            JButton source  = (JButton) e.getSource();
            String buttonText = source.getText();
            
            switch (buttonText){
                case "=":
                evaluateExpression();
                break;
                case "sqrt":
                calculateSquareRoot();
                break;
                case "cos":
                calculateCos();
                break;
                case "sin":
                calculateSin();
                break;
                case "tan":
                calculateTan();
                break;
                case"(" :  
                case")" : 
                appendToDisplay(buttonText);
                break;
                case "C" :
                clearAll();
                break;
                default:
                appendToDisplay(buttonText);       
            }
        }

        private void evaluateExpression(){
            String expression = display.getText();
            try {
                double result = evaluate(expression);
                display.setText(Double.toString(result));
            } catch ( Exception e) {
                display.setText("Error");
            }
        }

        private void calculateSquareRoot(){
            double number = Double.parseDouble(display.getText());
            display.setText(Double.toString(Math.sqrt(number)));
        }

        private void calculateSin(){
            double angle = Double.parseDouble(display.getText());
            display.setText(Double.toString(Math.sin(Math.toRadians(angle))));
        }

        private void calculateCos(){
            double angle = Double.parseDouble(display.getText());
            display.setText(Double.toString(Math.cos(Math.toRadians(angle))));
        }

        private void calculateTan(){
            double angle = Double.parseDouble(display.getText());
            display.setText(Double.toString(Math.tan(Math.toRadians(angle))));
        }

        private void appendToDisplay(String text){
            String currText = display.getText();
            display.setText(currText + text);
        }

        private void clearAll(){
            display.setText("");
        }
    }

    private double evaluate(String expression){
        return new Object() {
            int pos = -1, ch;
            void nextChar() {
                ch = (++pos < expression.length()) ? expression.charAt(pos) : -1;
            }
            boolean eat(int charToEat) {
                while (ch ==' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }
            double parse() {
                nextChar();
                double x = parseExpression();
                if(pos < expression.length()) throw new RuntimeException("Unexpected: " + (char) ch);
                return x;
            }
            double parseExpression() {
                double x = parseTerm();
                for (; ;){
                    if (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
                    
                }
                double parseTerm() {
                double x = parseFactor();
                for (; ;){
                    if (eat('*')) x *= parseFactor();
                    else if (eat('/')) x /= parseFactor();
                    else return x;
                }
            }
            double parseFactor(){
                if (eat('+')) return parseFactor();
                if (eat('-')) return -parseFactor();

                double x;
                int startPos = this.pos;
                if(eat('(')) {
                    x = parseExpression();
                    eat(')');
                }
                else if ((ch >= '0' && ch <= '9')  ||  ch == '.') {
                    while ((ch >= '0'  &&  ch <= '9')  ||  ch == '.') nextChar();
                    x = Double.parseDouble(expression.substring(startPos, this.pos));
                }
                else if (ch >= 'a'  &&  ch <= '2') {
                    while (ch >= 'a'  &&  ch <= 'z') nextChar();
                    String func = expression.substring(startPos, this.pos);
                    x = parseFactor();
                    switch (func) {
                        case "sin":
                        x = Math.sin(x);
                        break;
                        case "cos":
                        x = Math.cos(x);
                        break;
                        case "tan":
                        x = Math.tan(x);
                        break;
                        default:
                        throw new RuntimeException("Unknown function: " + func);
                    }
                }
                else{
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }
                if (eat('^'))  x = Math.pow(x, parseFactor());
                return x;
                    
                }
            }.parse();
        }

    public static void main(String[] args){
        ScientificCalculator calc = new ScientificCalculator();
    }

}


