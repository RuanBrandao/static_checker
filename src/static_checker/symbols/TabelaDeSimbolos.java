package static_checker.symbols;

import static_checker.model.EntradaDeSimbolo;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TabelaDeSimbolos {
    private final Map<String, EntradaDeSimbolo> tabela;
    private int proximoIndice =1;

    public TabelaDeSimbolos() {
        this.tabela = new HashMap<>();
    }

    public EntradaDeSimbolo insertOrUpdate (String lexeme, String codigoAtomo, int linha, int tamanhoOriginal) {
        EntradaDeSimbolo entrada = tabela.get(lexeme);

        if (entrada != null) {
            entrada.atualizarEntrada(linha);
            return entrada;
        } else {
            int tamanhoDepoisTruncado = lexeme.length();
            String tipoInicio = "";
            switch (codigoAtomo) {
                case "IDN04": tipoInicio = "IN"; break; // Int
                case "IDN05": tipoInicio = "FP"; break; // Float
                case "IDN06": tipoInicio = "ST"; break; // String
                case "IDN07": tipoInicio = "CH"; break; // Char
            }
            EntradaDeSimbolo novaEntrada = new EntradaDeSimbolo(
                    proximoIndice, codigoAtomo, lexeme, tamanhoOriginal, tamanhoDepoisTruncado, linha, tipoInicio
            );

            tabela.put(lexeme, novaEntrada);
            proximoIndice++;
            return novaEntrada;
        }
    }

    public Collection<EntradaDeSimbolo> getAllEntries() {
        return tabela.values();
    }
}
