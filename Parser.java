import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final Tokenizer tokenizer;

    public Parser(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    public static abstract class ASTNode {
    }

    public static class Program extends ASTNode {
        public final List<ASTNode> statements;

        public Program(List<ASTNode> statements) {
            this.statements = statements;
        }
    }

    public static class VariableDeclaration extends ASTNode {
        public final String name;
        public final ASTNode initializer;

        public VariableDeclaration(String name, ASTNode initializer) {
            this.name = name;
            this.initializer = initializer;
        }
    }

    public static class ExpressionStatement extends ASTNode {
        public final ASTNode expression;

        public ExpressionStatement(ASTNode expression) {
            this.expression = expression;
        }
    }

    public static class BinaryExpression extends ASTNode {
        public final String operator;
        public final ASTNode left;
        public final ASTNode right;

        public BinaryExpression(String operator, ASTNode left, ASTNode right) {
            this.operator = operator;
            this.left = left;
            this.right = right;
        }
    }

    public static class Identifier extends ASTNode {
        public final String name;

        public Identifier(String name) {
            this.name = name;
        }
    }

    public static class NumberLiteral extends ASTNode {
        public final int value;

        public NumberLiteral(int value) {
            this.value = value;
        }
    }

    public static class AssignmentExpression extends ASTNode {
        public final ASTNode left;
        public final ASTNode right;

        public AssignmentExpression(ASTNode left, ASTNode right) {
            this.left = left;
            this.right = right;
        }
    }

    public ASTNode parse() {
        List<ASTNode> statements = new ArrayList<>();
        while (tokenizer.currentToken().type != Token.Type.EOF) {
            statements.add(parseStatement());
        }
        return new Program(statements);
    }

    private ASTNode parseStatement() {
        Token currentToken = tokenizer.currentToken();
        if (currentToken.type == Token.Type.IDENTIFIER && currentToken.text.equals("var")) {
            return parseVariableDeclaration();
        } else {
            return parseExpressionStatement();
        }
    }

    private ASTNode parseVariableDeclaration() {
        tokenizer.advance(); // consume 'var'
        if (tokenizer.currentToken().type != Token.Type.IDENTIFIER) {
            throw new RuntimeException("Expected identifier");
        }
        String varName = tokenizer.currentToken().text;
        tokenizer.advance(); // consume identifier
        if (tokenizer.currentToken().type != Token.Type.OPERATOR || !tokenizer.currentToken().text.equals("=")) {
            throw new RuntimeException("Expected '='");
        }
        tokenizer.advance(); // consume '='
        ASTNode initializer = parseExpression();
        if (tokenizer.currentToken().type != Token.Type.PUNCTUATION || !tokenizer.currentToken().text.equals(";")) {
            throw new RuntimeException("Expected ';'");
        }
        tokenizer.advance(); // consume ';'
        return new VariableDeclaration(varName, initializer);
    }

    private ASTNode parseExpressionStatement() {
        ASTNode expr = parseExpression();
        if (tokenizer.currentToken().type != Token.Type.PUNCTUATION || !tokenizer.currentToken().text.equals(";")) {
            throw new RuntimeException("Expected ';'");
        }
        tokenizer.advance(); // consume ';'
        return new ExpressionStatement(expr);
    }

    private ASTNode parseExpression() {
        return parseAssignmentExpression();
    }

    private ASTNode parseAssignmentExpression() {
        ASTNode left = parseAdditiveExpression();
        if (tokenizer.currentToken().type == Token.Type.OPERATOR && tokenizer.currentToken().text.equals("=")) {
            tokenizer.advance(); // consume '='
            ASTNode right = parseExpression();
            return new AssignmentExpression(left, right);
        }
        return left;
    }

    private ASTNode parseAdditiveExpression() {
        ASTNode left = parseMultiplicativeExpression();
        while (tokenizer.currentToken().type == Token.Type.OPERATOR &&
                (tokenizer.currentToken().text.equals("+") || tokenizer.currentToken().text.equals("-"))) {
            String operator = tokenizer.currentToken().text;
            tokenizer.advance(); // consume operator
            ASTNode right = parseMultiplicativeExpression();
            left = new BinaryExpression(operator, left, right);
        }
        return left;
    }

    private ASTNode parseMultiplicativeExpression() {
        ASTNode left = parsePrimaryExpression();
        while (tokenizer.currentToken().type == Token.Type.OPERATOR &&
                (tokenizer.currentToken().text.equals("*") || tokenizer.currentToken().text.equals("/"))) {
            String operator = tokenizer.currentToken().text;
            tokenizer.advance(); // consume operator
            ASTNode right = parsePrimaryExpression();
            left = new BinaryExpression(operator, left, right);
        }
        return left;
    }

    private ASTNode parsePrimaryExpression() {
        Token token = tokenizer.currentToken();
        if (token.type == Token.Type.NUMBER) {
            tokenizer.advance(); // consume number
            return new NumberLiteral(Integer.parseInt(token.text));
        } else if (token.type == Token.Type.IDENTIFIER) {
            ASTNode identifier = new Identifier(token.text);
            tokenizer.advance(); // consume identifier
            return identifier;
        } else if (token.type == Token.Type.PUNCTUATION && token.text.equals("(")) {
            tokenizer.advance(); // consume '('
            ASTNode expression = parseExpression();
            if (tokenizer.currentToken().type != Token.Type.PUNCTUATION || !tokenizer.currentToken().text.equals(")")) {
                throw new RuntimeException("Expected ')'");
            }
            tokenizer.advance(); // consume ')'
            return expression;
        } else {
            throw new RuntimeException("Unexpected token: " + token);
        }
    }
}
