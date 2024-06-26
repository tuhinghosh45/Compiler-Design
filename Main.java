import java.util.List;

public class Main {
    public static void main(String[] args) {
        String source = "var x = 42; x = x + 3;";
        Tokenizer tokenizer = new Tokenizer(source);

        // Tokenize source code
        System.out.println("Tokens:");
        while (tokenizer.currentToken().type != Token.Type.EOF) {
            System.out.println(tokenizer.currentToken());
            tokenizer.advance();
        }

        // Reset the tokenizer for the parser
        tokenizer = new Tokenizer(source);

        // Parse tokens into AST
        Parser parser = new Parser(tokenizer);
        Parser.ASTNode ast = parser.parse();

        // Generate bytecode from AST
        CodeGenerator codeGenerator = new CodeGenerator();
        List<CodeGenerator.Bytecode> bytecode = codeGenerator.generate(ast);

        // Print generated bytecode
        System.out.println("Generated Bytecode:");
        for (CodeGenerator.Bytecode code : bytecode) {
            System.out.println(code);
        }

        // Execute bytecode
        Interpreter interpreter = new Interpreter();
        interpreter.execute(bytecode);
    }
}
