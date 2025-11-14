package static_checker.model;

public record Atomo(
        String codigo,
        String lexeme,
        int linha,
        int indice_tabela
) {

}
