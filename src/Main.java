import java.math.BigInteger;
import java.util.*;

public class Main {
    private static class Node {
        BigInteger v;
        Node p;

        Node(BigInteger v, Node p) {
            this.v = v;
            this.p = p;
        }
    }

    private static class Stack {
        private Node last;

        public Stack() {
            this.last = null;
        }

        public void insert(BigInteger v) {
            last = new Node(v, last);
        }

        public BigInteger pop() {
            if (last == null) {
                return null;
            }
            BigInteger v = last.v;
            last = last.p;
            return v;
        }

        public BigInteger top() {
            if (last == null) {
                return null;
            }
            return last.v;
        }

        public boolean isStackEmpty() {
            return last == null;
        }
    }


    private static boolean isNumeric(String str) {
        return str.matches("[0-9]+");
    }
    public static boolean isAlphaNumeric(String str) {
        return (str.matches("[a-zA-Z0-9]+") && !str.matches("[0-9]+"));
    }

    private static boolean isStringIncorrect(String[] inputList, String[] operationList,
                                     Map<String, BigInteger> variablesList, boolean flagEquals) {
        int counterNumbers = 0;
        int counterOperations = 0;
        for (int i = 0; i < inputList.length; i++) {
            if (!isNumeric(inputList[i]) && !(Arrays.asList(operationList).contains(inputList[i]))
                    && !variablesList.containsKey(inputList[i])) {
                int errorNumber = i +1;
                if (flagEquals) {
                    errorNumber +=2;
                }
                System.err.println("Ошибка, в " + errorNumber + " символе неподдерживаемый ввод");
                return true;
            }
            if (isNumeric(inputList[i]) || variablesList.containsKey(inputList[i])) counterNumbers++;
            if (Arrays.asList(operationList).contains(inputList[i])) counterOperations++;
        }
        if (counterNumbers - counterOperations > 1) {
            System.err.println("Ошибка, недостаточно операций");
            return true;
        }
        return false;
    }

    private static boolean operation(String s, Stack stack,
                                     BigInteger rightNumber, BigInteger leftNumber) {
        switch (s) {
            case "+" -> stack.insert(leftNumber.add(rightNumber));
            case "-" -> stack.insert(leftNumber.subtract(rightNumber));
            case "*" -> stack.insert(leftNumber.multiply(rightNumber));
            case "/" -> {
                if (rightNumber.equals(BigInteger.ZERO)) {
                    System.err.println("Ошибка, деление на ноль");
                    return true;
                } else {
                    stack.insert(leftNumber.divide(rightNumber));
                }
            }
        }
        return false;
    }


    private static void processExpression(String[] inputList,
                                          Map<String, BigInteger> variablesList, Stack stack) {
        boolean flagError = false;
        for (String s : inputList) {
            if (isNumeric(s)) {
                stack.insert(new BigInteger(s));
            } else if (variablesList.containsKey(s)) {
                stack.insert(variablesList.get(s));
            } else {
                if (stack.isStackEmpty()) {
                    System.err.println("Ошибка, не достаточно численных значений для операции");
                    flagError = true;
                    break;
                } else {
                    BigInteger rightNumber = stack.pop();
                    if (stack.isStackEmpty()) {
                        System.err.println("Ошибка, не достаточно численных значений для операции");
                        flagError = true;
                        break;
                    }
                    BigInteger leftNumber = stack.pop();
                    if (operation(s, stack, rightNumber, leftNumber)) {
                        flagError = true;
                        break;
                    }
                }
            }
        }
        if (!flagError) {
            System.out.println(stack.top());
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Вводите выражения, которые хотите вычислить, " +
                "для выхода из программы напишите \"quit\" (без кавычек)");
        System.out.println("Переменные могут состоять только из цифр и латинских букв," +
                " переменная должна содержать хотя бы одну букву");
        String[] operationList = {"+", "-", "*", "/"};
        Map<String, BigInteger> variablesList = new HashMap<>();

        while (true) {
            boolean flagEquals = false;
            String inputString = sc.nextLine().trim();
            if (inputString.equals("quit")) {
                break;
            }

            if (inputString.contains("=")) {
                flagEquals = true;
                String[] splitString = inputString.split("=");
                if ("".equals(splitString[0])) {
                    System.err.println("Ошибка, нет переменной для присваивания");
                    continue;
                }
                if (splitString.length < 2) {
                    System.err.println("Ошибка, не хватает значений для присваивания");
                    continue;
                }
                if (splitString.length > 2) {
                    System.err.println("Ошибка, слишком много значений присваивания");
                    continue;
                }
                String variableName = splitString[0].trim();
                String expression = splitString[1].trim();
                String[] inputList = expression.split(" ");

                if (variableName.equals("quit")){
                    System.err.println("Переменная не может быть кодовым словом для выхода");
                    continue;
                }
                if (!(Arrays.asList(operationList).contains(variableName)) && !variablesList.containsKey(variableName)) {
                    if (isAlphaNumeric(variableName)) {
                        variablesList.put(variableName, BigInteger.ZERO);
                    } else {
                        System.err.println("Некорректно введенная переменная");
                        continue;
                    }
                }
                if ((Arrays.asList(operationList).contains(variableName))){
                    System.err.println("Некорректно введенная переменная");
                    continue;
                }

                if (isStringIncorrect(inputList, operationList, variablesList, flagEquals)) {
                    continue;
                }
                Stack stack = new Stack();
                processExpression(inputList, variablesList, stack);

                if (!stack.isStackEmpty()) {
                    variablesList.put(variableName, stack.top());
                }

            } else {
                String[] inputList = inputString.split(" ");
                if (isStringIncorrect(inputList, operationList, variablesList, flagEquals)){
                    continue;
                }
                Stack stack = new Stack();
                processExpression(inputList, variablesList, stack);
            }
        }
    }
}
