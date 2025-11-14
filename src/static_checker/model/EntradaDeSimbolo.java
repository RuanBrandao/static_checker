package static_checker.model;

import java.util.ArrayList;

public class EntradaDeSimbolo {
    private final int numeroEntrada;
    private final String codigoAtomo;
    private final String lexeme;

    private int qtCharsAntesDeTruncar;
    private int qtCharsDpsDeTruncar;
    private String tipoSimbolo;
    private final ArrayList<Integer> linhas;

    public EntradaDeSimbolo(int numeroEntrada, String codigoAtomo, String lexeme, int qtCharsAntesDeTruncar, int qtCharsDpsDeTruncar, ArrayList<Integer> linhas) {
        this.numeroEntrada = numeroEntrada;
        this.codigoAtomo = codigoAtomo;
        this.lexeme = lexeme;
        this.qtCharsAntesDeTruncar = qtCharsAntesDeTruncar;
        this.qtCharsDpsDeTruncar = qtCharsDpsDeTruncar;
        this.tipoSimbolo = "";
        this.linhas = linhas;
    }
}
