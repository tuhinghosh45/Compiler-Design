import java.util.ArrayList;
import java.util.List;

public class CodeGenerator {
    public static class Bytecode {
        final String operation;
        final String operand;

        public Bytecode(String operation, String operand) {
            this.operation = operation;
            this.operand = operand;
        }

        @Override
        public String toString() {
            return operation + " " + operand;
        }
    }

    public List<Bytecode> generate(Parser.ASTNode node) {
        List<Bytecode> bytecode = new ArrayList<>();
        if (node instanceof Parser.Program) {
            for (Parser.ASTNode statement : ((Parser.Program) node).statements) {
                bytecode.addAll(generate(statement));
            }
        } else if (node instanceof Parser.VariableDeclaration) {
            Parser.VariableDeclaration varDecl = (Parser.VariableDeclaration) node;
            bytecode.addAll(generate(varDecl.initializer));
            bytecode.add(new Bytecode("STORE_VAR", varDecl.name));
        } else if (node instanceof Parser.ExpressionStatement) {
            Parser.ExpressionStatement exprStmt = (Parser.ExpressionStatement) node;
            bytecode.addAll(generate(exprStmt.expression));
        } else if (node instanceof Parser.BinaryExpression) {
            Parser.BinaryExpression binExpr = (Parser.BinaryExpression) node;
            bytecode.addAll(generate(binExpr.left));
            bytecode.addAll(generate(binExpr.right));
            bytecode.add(new Bytecode("BINARY_" + binExpr.operator.toUpperCase(), ""));
        } else if (node instanceof Parser.Identifier) {
            bytecode.add(new Bytecode("LOAD_VAR", ((Parser.Identifier) node).name));
        } else if (node instanceof Parser.NumberLiteral) {
            bytecode.add(new Bytecode("LOAD_CONST", String.valueOf(((Parser.NumberLiteral) node).value)));
        } else if (node instanceof Parser.AssignmentExpression) {
            Parser.AssignmentExpression assignExpr = (Parser.AssignmentExpression) node;
            bytecode.addAll(generate(assignExpr.right));
            bytecode.add(new Bytecode("STORE_VAR", ((Parser.Identifier) assignExpr.left).name));
        }
        return bytecode;
    }
}
