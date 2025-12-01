package static_checker;

import static_checker.lexer.AnalisadorLexico;
import static_checker.model.Atomo;
import static_checker.model.EntradaDeSimbolo;
import static_checker.symbols.TabelaDeSimbolos;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class StaticChecker {
    public static void main(String[] args) {

        System.out.println("=== COMPILADOR CAATINGUAGE 2025-2 ===");
        System.out.println("Equipe 10 - Iniciando Análise...");

        Scanner scanner = new Scanner(System.in);
        System.out.print("Digite o nome base do arquivo (ex: Teste1): ");
        String nomeBase = scanner.nextLine().trim();

        Path caminhoFonte = Paths.get(nomeBase + ".252");
        Path caminhoLex = Paths.get(nomeBase + ".LEX");
        Path caminhoTab = Paths.get(nomeBase + ".TAB");

        if (!Files.exists(caminhoFonte)) {
            System.err.println("ERRO: O arquivo fonte não foi encontrado: " + caminhoFonte.toAbsolutePath());
            return;
        }

        System.out.println("Lendo arquivo: " + caminhoFonte.toAbsolutePath());

        try (
                BufferedReader reader = Files.newBufferedReader(caminhoFonte, StandardCharsets.UTF_8);
                BufferedWriter writerLex = Files.newBufferedWriter(caminhoLex, StandardCharsets.UTF_8);
                BufferedWriter writerTab = Files.newBufferedWriter(caminhoTab, StandardCharsets.UTF_8)
        ) {

            TabelaDeSimbolos tabela = new TabelaDeSimbolos();
            AnalisadorLexico lexico = new AnalisadorLexico(reader, tabela);

            escreverCabecalho(writerLex, "RELATÓRIO DA ANÁLISE LÉXICA", nomeBase);
            escreverCabecalho(writerTab, "RELATÓRIO DA TABELA DE SÍMBOLOS", nomeBase);

            String ultimoTipoEncontrado = "";

            while (true) {
                Atomo atomo = lexico.getNextAtom();

                if (atomo.codigo().equals("EOF")) {
                    break;
                }

                if (atomo.codigo().equals("PRS01")) { // integer
                    ultimoTipoEncontrado = "IN";
                }

                else if (atomo.codigo().equals("PRS02")) { // real
                    ultimoTipoEncontrado = "FP";
                }

                else if (atomo.codigo().equals("PRS04")) { // string
                    ultimoTipoEncontrado = "ST";
                }

                else if (atomo.codigo().equals("PRS03")) { // char
                    ultimoTipoEncontrado = "CH";
                }

                else if (atomo.codigo().equals("SRS01")) {
                    ultimoTipoEncontrado = "";
                }

                else if (atomo.codigo().equals("IDN02") && !ultimoTipoEncontrado.isEmpty()) {
                    tabela.setTipoSimbolo(atomo.indice_tabela(), ultimoTipoEncontrado);
                }

                String linhaLex;
                if (atomo.indice_tabela() != -1) {
                    linhaLex = String.format("Lexeme: %s, Código: %s, ÍndiceTabSimb: %d, Linha: %d\n",
                            atomo.lexeme(), atomo.codigo(), atomo.indice_tabela(), atomo.linha());
                } else {
                    linhaLex = String.format("Lexeme: %s, Código: %s, Linha: %d\n",
                            atomo.lexeme(), atomo.codigo(), atomo.linha());
                }

                writerLex.write(linhaLex);
            }

            for (EntradaDeSimbolo entrada : tabela.getAllEntries()) {
                String linhaTab = String.format(
                        "Entrada: %d, Codigo: %s, Lexeme: %s, QtdCharAntesTrunc: %d, QtdCharDepoisTrunc: %d, TipoSimb: %s, Linhas: %s\n",
                        entrada.getNumeroEntrada(),
                        entrada.getCodigoAtomo(),
                        entrada.getLexeme(),
                        entrada.getQtCharsAntesDeTruncar(),
                        entrada.getQtCharsDpsDeTruncar(),
                        entrada.getTipoSimbolo(),
                        entrada.getLinhas().toString()
                );
                writerTab.write(linhaTab);
            }

            System.out.println("-------------------------------------------------");
            System.out.println("SUCESSO! Análise concluída.");
            System.out.println("Relatório Léxico gerado em: " + caminhoLex.toAbsolutePath());
            System.out.println("Tabela de Símbolos gerada em: " + caminhoTab.toAbsolutePath());

        } catch (IOException e) {
            System.err.println("Erro de Entrada/Saída: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void escreverCabecalho(BufferedWriter writer, String titulo, String arquivo) throws IOException {
        writer.write("Código da Equipe: 10\n");
        writer.write("Componentes:\n");
        writer.write("Iann Luca Rocha Lino;\n");
        writer.write("Leon Nascimento Moreira;\n");
        writer.write("Ruan Luis Matos Brandão Suarez;\n");
        writer.write("Ubirajara do Rosário Santana Junior;\n\n");
        writer.write(titulo + "\n");
        writer.write("Texto fonte analisado: " + arquivo + ".252\n");
        writer.write("-------------------------------------------------\n");
    }
}
