package ece351.util;

import java.lang.Character;
import java.lang.StringBuilder;
import java.util.Arrays;

public final class Lexer {

    private enum Kind {
        SIMPLE,
        ID,
        EOF,
    }

    private enum State {
        START,
        CONTINUE_ID,
        CONTINUE_ASSIGN,
    }

    private final char[] input;
    private int index;
    private Kind kind;
    private String token;

    public Lexer(final String input) {
        this.input = input.toCharArray();
        this.index = 0;
        advance();
    }

    private boolean isContinueID(char ch) {
        return (Character.isLetter(ch) || Character.isDigit(ch) || ch == '_');
    }

    public void advance() {
        StringBuilder stringBuilder = new StringBuilder();
        State state = State.START;
        kind = Kind.SIMPLE;

        if (index == input.length) {
            kind = Kind.EOF;
        }

        while (index < input.length) {
            char ch = input[index];
            ++index;

            if (state == State.START) {
                if (Character.isWhitespace(ch)) {
                    // We may have got to the end by ignoring whitespace
                    if (index == input.length) {
                        kind = Kind.EOF;
                    }
                    continue;
                }
                else if (Character.isLetter(ch)) {
                    kind = Kind.ID;
                    state = State.CONTINUE_ID;
                }
                else if (ch == '=') {
                    state = State.CONTINUE_ASSIGN;
                }
            }
            else if (state == State.CONTINUE_ID) {
                if (!isContinueID(ch)) {
                    --index;
                    break;
                }
            }
            else if (state == State.CONTINUE_ASSIGN) {
                if (ch == '>') {
                    state = State.START;
                }
                else {
                    --index;
                    break;
                }
            }
            stringBuilder.append(ch);
            if (state == State.START) {
                break;
            }
        }
        token = stringBuilder.toString();
    }

    public boolean inspect(final String s) {
        return token.equals(s);
    }

    public boolean inspect(final String... options) {
        for (final String s : options) {
            if (inspect(s)) {
                return true;
            }
        }
        return false;
    }

    public boolean inspectID() {
        return kind == Kind.ID;
    }

    public boolean inspectEOF() {
        return kind == Kind.EOF;
    }

    public String consume(final String s) {
        if (token.equals(s)) {
            advance();
            return s;
        }
        else {
            err("expected: '" + s + "' got '" + token + "'");
            return null; // dead code
        }
    }

    public String consume(final String... options) {
        for (final String s : options) {
            if (inspect(s)) {
                return consume(s);
            }
        }
        err("expected one of '" + Arrays.toString(options) + "' but had '"
            + token + "'");
        return null;
    }

    public String consumeID() {
        if (!inspectID()) err("expected: ID got '" + token + "'");
        final String result = token;
        advance();
        return result;
    }

	
    public void consumeEOF() {
        if (!inspectEOF()) err("expected: EOF");
        advance();
    }

    public String debugState() {
        return token;
    }
    
    protected void err(final String msg) {
        Debug.barf(msg);
    }

}
