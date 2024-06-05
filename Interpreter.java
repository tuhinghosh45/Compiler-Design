import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Interpreter {
    private final Map<String, Integer> variables = new HashMap<>();
    private final Stack<Integer> stack = new Stack<>();

    public void execute(List<CodeGenerator.Bytecode> bytecode) {
        for (CodeGenerator.Bytecode code : bytecode) {
            switch (code.operation) {
                case "LOAD_CONST":
                    stack.push(Integer.parseInt(code.operand));
                    System.out.println("PUSH " + code.operand);
                    break;
                case "STORE_VAR":
                    int value = stack.pop();
                    variables.put(code.operand, value);
                    System.out.println("STORE " + code.operand + " = " + value);
                    break;
                case "LOAD_VAR":
                    int varValue = variables.get(code.operand);
                    stack.push(varValue);
                    System.out.println("LOAD " + code.operand + " = " + varValue);
                    break;
                case "BINARY_+":
                    int secondOperand = stack.pop();
                    int firstOperand = stack.pop();
                    int result = firstOperand + secondOperand;
                    stack.push(result);
                    System.out.println("ADD: " + firstOperand + " + " + secondOperand + " = " + result);
                    break;
                case "BINARY_-":
                    secondOperand = stack.pop();
                    firstOperand = stack.pop();
                    result = firstOperand - secondOperand;
                    stack.push(result);
                    System.out.println("SUB: " + firstOperand + " - " + secondOperand + " = " + result);
                    break;
                case "BINARY_*":
                    secondOperand = stack.pop();
                    firstOperand = stack.pop();
                    result = firstOperand * secondOperand;
                    stack.push(result);
                    System.out.println("MUL: " + firstOperand + " * " + secondOperand + " = " + result);
                    break;
                case "BINARY_/":
                    secondOperand = stack.pop();
                    firstOperand = stack.pop();
                    result = firstOperand / secondOperand;
                    stack.push(result);
                    System.out.println("DIV: " + firstOperand + " / " + secondOperand + " = " + result);
                    break;
                default:
                    throw new RuntimeException("Unknown operation: " + code.operation);
            }
        }

        // Print final value of variable x
        if (variables.containsKey("x")) {
            System.out.println("Final value of x: " + variables.get("x"));
        } else {
            System.out.println("Variable x was not assigned a value.");
        }
    }
}
