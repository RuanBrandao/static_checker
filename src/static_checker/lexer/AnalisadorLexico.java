package static_checker.lexer;

import static_checker.model.Atomo;
import static_checker.symbols.TabelaDeSimbolos;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AnalisadorLexico {
    private final BufferedReader reader;
    private final TabelaDeSimbolos tabelaDeSimbolos;
    private final Map<String, String> mapaReservados;
    private int linhaAtual = 1;

    public AnalisadorLexico(BufferedReader reader, TabelaDeSimbolos tabelaDeSimbolos) {
        this.reader = reader;
        this.tabelaDeSimbolos = tabelaDeSimbolos;
        this.mapaReservados = new HashMap<>();
        carregarPalavrasReservadas();
    }

    public Atomo getNextAtom() {

        int charCode;

        try {
            while (true) {

                reader.mark(2);
                charCode = reader.read();

                if (charCode == -1) {
                    return new Atomo("EOF", "EOF", linhaAtual, -1);
                }

                // Inicializamos a variável abaixo pra garantir que a entrada esteja no formato de caráctere
                char ch = (char) charCode;

                if(Character.isWhitespace(ch)) {
                    if (ch == '\n') {
                        linhaAtual++;
                    }
                    continue;
                }

                if(ch== '/') {
                    int next = peekNextChar();
                    if (next=='/') {
                        handleComentarioLinha();
                        continue;
                    } else if (next == '*') {
                        handleComentarioBloco();
                        continue;
                    }
                        
                    }

                if (!Character.isLetterOrDigit(ch) &&
                        ch != '_' && ch != '"' && ch != '\'' && ";,:?()[]{}/*%#<>=!+-".indexOf(ch) == -1) {

                    continue;
                }
                break;
                }


            char firstChar = (char) charCode;

            if (Character.isLetter(firstChar) || firstChar=='_') {
                return handleIdentifOrKeyword(firstChar);
            }

            if (Character.isDigit(firstChar)) {
                return handleNumber(firstChar);
            }

            if (firstChar == '"') return handleString();
            if (firstChar == '\'') return handleChar();

            switch (firstChar) {
                case ';': return new Atomo(mapaReservados.get(";"), ";", linhaAtual, -1);
                case ',': return new Atomo(mapaReservados.get(","), ",", linhaAtual, -1);
                case '?': return new Atomo(mapaReservados.get("?"), "?", linhaAtual, -1);
                case '(': return new Atomo(mapaReservados.get("("), "(", linhaAtual, -1);
                case ')': return new Atomo(mapaReservados.get(")"), ")", linhaAtual, -1);
                case '[': return new Atomo(mapaReservados.get("["), "[", linhaAtual, -1);
                case ']': return new Atomo(mapaReservados.get("]"), "]", linhaAtual, -1);
                case '{': return new Atomo(mapaReservados.get("{"), "{", linhaAtual, -1);
                case '}': return new Atomo(mapaReservados.get("}"), "}", linhaAtual, -1);
                case '+': return new Atomo(mapaReservados.get("+"), "+", linhaAtual, -1);
                case '-': return new Atomo(mapaReservados.get("-"), "-", linhaAtual, -1);
                case '*': return new Atomo(mapaReservados.get("*"), "*", linhaAtual, -1);
                case '%': return new Atomo(mapaReservados.get("%"), "%", linhaAtual, -1);
                case '#': return new Atomo(mapaReservados.get("#"), "#", linhaAtual, -1);
                case '/': return new Atomo(mapaReservados.get("/"), "/", linhaAtual, -1);

                case ':':
                    if (peekNextChar() == '=') {
                        reader.read();
                        return new Atomo(mapaReservados.get(":="), ":=", linhaAtual, -1);
                    } else {
                        return new Atomo(mapaReservados.get(":"), ":", linhaAtual, -1);
                    }

                case '<':
                    if (peekNextChar() == '=') {
                        reader.read();
                        return new Atomo(mapaReservados.get("<="), "<=", linhaAtual, -1);
                    } else {
                        return new Atomo(mapaReservados.get("<"), "<", linhaAtual, -1);
                    }

                case '>':
                    if (peekNextChar() == '=') {
                        reader.read();
                        return new Atomo(mapaReservados.get(">="), ">=", linhaAtual, -1);
                    } else {
                        return new Atomo(mapaReservados.get(">"), ">", linhaAtual, -1);
                    }

                case '=':
                    if (peekNextChar() == '=') {
                        reader.read();
                        return new Atomo(mapaReservados.get("=="), "==", linhaAtual, -1);
                    } else {
                        return new Atomo("ERR_LEX", "=", linhaAtual, -1);
                    }

                case '!':
                    if (peekNextChar() == '=') {
                        reader.read();
                        return new Atomo(mapaReservados.get("!="), "!=", linhaAtual, -1);
                    } else {
                        return new Atomo("ERR_LEX", "!", linhaAtual, -1);
                    }

                default:
                    return new Atomo("ERR_LEX", String.valueOf(firstChar), linhaAtual, -1);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return new Atomo("ERR_IO", e.getMessage(), linhaAtual, -1);
        }
    }

    private int peekNextChar() throws IOException {
        reader.mark(1);
        int nextChar = reader.read();
        reader.reset();
        return nextChar;
    }
    private void handleComentarioLinha() throws IOException {
        int character;

        while((character=reader.read()) != -1) {
            if (character == '\n') {
                linhaAtual++;
                break;
            }
        }
    }

    private void handleComentarioBloco() throws IOException {
        int character;

        while((character = reader.read()) != -1) {
            if(character == '*') {
                if(peekNextChar()=='/') {
                    reader.read();
                    break;
                }
            }
        }
    }

    private Atomo handleString() throws IOException {
        StringBuilder strBuilder = new StringBuilder();
        int tamOriginal = 0;
        strBuilder.append('"');
        tamOriginal++;

        while (true) {
            int cInt = reader.read();
            if (cInt == -1 || cInt == '\n') {
                if (cInt == '\n') linhaAtual++;
                return new Atomo("ERR_LEX", "String não fechada", linhaAtual, -1);
            }

            char c = (char) cInt;

            if (c == '"') {
                tamOriginal++;
                if (tamOriginal <= 35) strBuilder.append(c);
                break;
            }

            tamOriginal++;
            if (tamOriginal <= 35) strBuilder.append(c);
        }

        String lexeme = strBuilder.toString();

        var entrada = tabelaDeSimbolos.insertOrUpdate(lexeme, "IDN06", linhaAtual, tamOriginal);

        return new Atomo("IDN06", lexeme, linhaAtual, entrada.getNumeroEntrada());
    }
    private Atomo handleChar() throws IOException {
        StringBuilder strBuilder = new StringBuilder();
        int tamOriginal = 0;

        strBuilder.append('\'');
        tamOriginal++;

        int cInt = reader.read();
        if (cInt == -1 || cInt == '\n') {
            return new Atomo("ERR_LEX", "Char não fechado", linhaAtual, -1);
        }
        char meio = (char) cInt;
        tamOriginal++;
        strBuilder.append(meio);

        cInt = reader.read();
        char fim = (char) cInt;

        if (fim != '\'') {
            return new Atomo("ERR_LEX", "Char mal formado (esperava ')", linhaAtual, -1);
        }
        tamOriginal++;
        strBuilder.append(fim);

        String lexeme = strBuilder.toString();

        var entrada = tabelaDeSimbolos.insertOrUpdate(lexeme, "IDN07", linhaAtual, tamOriginal);

        return new Atomo("IDN07", lexeme, linhaAtual, entrada.getNumeroEntrada());
    }

    private Atomo handleIdentifOrKeyword(char firstChar) throws IOException {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(firstChar);
        int tamOriginal = 1;

        while (true) {
            int nextInt = peekNextChar();
            if (nextInt == -1) {
                break;
            }
            char next = (char) nextInt;

            if (Character.isLetterOrDigit(next) || next == '_') {
                reader.read();
                tamOriginal++;
                if (tamOriginal <= 35) {
                    strBuilder.append(next);
                }
            } else {
                break;
            }
        }
        String lexeme = strBuilder.toString().toUpperCase();

        if (mapaReservados.containsKey(lexeme)) {
            String codigo = mapaReservados.get(lexeme);
            return new Atomo(codigo, lexeme, linhaAtual, -1);
        } else {
            String codeIdn = "IDN02";
            var entrada = tabelaDeSimbolos.insertOrUpdate(lexeme, codeIdn, linhaAtual, tamOriginal);
            return new Atomo(codeIdn, lexeme, linhaAtual, entrada.getNumeroEntrada());
        }
    }

    private Atomo handleNumber(char firstChar) throws IOException {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(firstChar);
        int tamOriginal = 1;
        boolean isReal =false;

        while (Character.isDigit((char) peekNextChar()) ) {
            char c = (char) reader.read();
            tamOriginal++;
            if (tamOriginal <= 35) {
                strBuilder.append(c);
            }
        }

        int next = peekNextChar();
        if(next == '.') {
            isReal = true;
            char dot = (char) reader.read();
            tamOriginal++;
            if (tamOriginal <= 35) {
                strBuilder.append(dot);
            }
            while (Character.isDigit((char) peekNextChar())) {
                char c = (char) reader.read();
                tamOriginal++;
                if (tamOriginal <= 35) strBuilder.append(c);
            }
        }

        next = peekNextChar();
        if (next == 'e' || next == 'E') {
            isReal = true; //
            char e = (char) reader.read();
            tamOriginal++;
            if (tamOriginal <= 35) strBuilder.append(e);

            int afterE = peekNextChar();
            if (afterE == '+' || afterE == '-') {
                char sinal = (char) reader.read();
                tamOriginal++;
                if (tamOriginal <= 35) strBuilder.append(sinal);
            }

            while (Character.isDigit((char) peekNextChar())) {
                char c = (char) reader.read();
                tamOriginal++;
                if (tamOriginal <= 35) strBuilder.append(c);
            }
        }

        String lexeme = strBuilder.toString();

        String codeIdn = isReal ? "IDN05" : "IDN04";

        var entrada = tabelaDeSimbolos.insertOrUpdate(lexeme, codeIdn, linhaAtual, tamOriginal);

        return new Atomo(codeIdn, lexeme, linhaAtual, entrada.getNumeroEntrada());
    }

    private void carregarPalavrasReservadas() {
        mapaReservados.put("INTEGER", "PRS01");
        mapaReservados.put("REAL", "PRS02");
        mapaReservados.put("CHARACTER", "PRS03");
        mapaReservados.put("STRING", "PRS04");
        mapaReservados.put("BOOLEAN", "PRS05");
        mapaReservados.put("VOID", "PRS06");
        mapaReservados.put("TRUE", "PRS07");
        mapaReservados.put("FALSE", "PRS08");
        mapaReservados.put("VARTYPE", "PRS09");
        mapaReservados.put("FUNCTYPE", "PRS10");
        mapaReservados.put("PARAMTYPE", "PRS11");
        mapaReservados.put("DECLARATIONS", "PRS12");
        mapaReservados.put("ENDDECLARATIONS", "PRS13");
        mapaReservados.put("PROGRAM", "PRS14");
        mapaReservados.put("ENDPROGRAM", "PRS15");
        mapaReservados.put("FUNCTIONS", "PRS16");
        mapaReservados.put("ENDFUNCTIONS", "PRS17");
        mapaReservados.put("ENDFUNCTION", "PRS18");
        mapaReservados.put("RETURN", "PRS19");
        mapaReservados.put("IF", "PRS20");
        mapaReservados.put("ELSE", "PRS21");
        mapaReservados.put("ENDIF", "PRS22");
        mapaReservados.put("WHILE", "PRS23");
        mapaReservados.put("ENDWHILE", "PRS24");
        mapaReservados.put("BREAK", "PRS25");
        mapaReservados.put("PRINT", "PRS26");

        //Símbolos Reservados
        mapaReservados.put(";", "SRS01");
        mapaReservados.put(",", "SRS02");
        mapaReservados.put(":", "SRS03");
        mapaReservados.put(":=", "SRS04");
        mapaReservados.put("?", "SRS05");
        mapaReservados.put("(", "SRS06");
        mapaReservados.put(")", "SRS07");
        mapaReservados.put("[", "SRS08");
        mapaReservados.put("]", "SRS09");
        mapaReservados.put("{", "SRS10");
        mapaReservados.put("}", "SRS11");
        mapaReservados.put("+", "SRS12");
        mapaReservados.put("-", "SRS13");
        mapaReservados.put("*", "SRS14");
        mapaReservados.put("/", "SRS15");
        mapaReservados.put("%", "SRS16");
        mapaReservados.put("==", "SRS17");
        mapaReservados.put("!=", "SRS18");
        mapaReservados.put("#", "SRS18");
        mapaReservados.put("<", "SRS19");
        mapaReservados.put("<=", "SRS20");
        mapaReservados.put(">", "SRS21");
        mapaReservados.put(">=", "SRS22");
    }
}
