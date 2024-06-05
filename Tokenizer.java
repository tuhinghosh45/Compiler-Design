import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Token {
    enum Type { NUMBER, IDENTIFIER, KEYWORD, OPERATOR, PUNCTUATION, EOF }

    final Type type;
    final String text;

    Token(Type type, String text) {
        this.type = type;
        this.text = text;
    }

    @Override
    public String toString() {
        return type + "('" + text + "')";
    }
}

public class Tokenizer {
    private static final Pattern TOKEN_PATTERN = Pattern.compile(
            "\\s*(?:(?<NUMBER>\\d+)|(?<IDENTIFIER>[a-zA-Z_][a-zA-Z0-9_]*)|(?<OPERATOR>[+\\-*/=])|(?<PUNCTUATION>[;()])|(?<KEYWORD>var))\\s*"
    );

    private final Matcher matcher;
    private Token currentToken;

    public Tokenizer(String source) {
        matcher = TOKEN_PATTERN.matcher(source);
        advance();
    }

    public void advance() {
        if (matcher.lookingAt()) {
            if (matcher.group("NUMBER") != null) {
                currentToken = new Token(Token.Type.NUMBER, matcher.group("NUMBER"));
            } else if (matcher.group("IDENTIFIER") != null) {
                currentToken = new Token(Token.Type.IDENTIFIER, matcher.group("IDENTIFIER"));
            } else if (matcher.group("OPERATOR") != null) {
                currentToken = new Token(Token.Type.OPERATOR, matcher.group("OPERATOR"));
            } else if (matcher.group("PUNCTUATION") != null) {
                currentToken = new Token(Token.Type.PUNCTUATION, matcher.group("PUNCTUATION"));
            } else if (matcher.group("KEYWORD") != null) {
                currentToken = new Token(Token.Type.KEYWORD, matcher.group("KEYWORD"));
            }
            matcher.region(matcher.end(), matcher.regionEnd());
        } else {
            currentToken = new Token(Token.Type.EOF, "");
        }
    }

    public Token currentToken() {
        return currentToken;
    }
}
