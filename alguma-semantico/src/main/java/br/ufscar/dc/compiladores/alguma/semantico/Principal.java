package br.ufscar.dc.compiladores.alguma.semantico;

import java.io.IOException;
import java.io.PrintWriter;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CharStream;

public class Principal {
    public static void main(String args[]) throws IOException {
       if (args.length < 3) {
            System.out.println("Uso: java Principal <arquivo entrada> <arquivo saída C> <arquivo saída pseudo-código>");
            return;
        }

        // Etapa 1: Análise Léxica e Sintática
        CharStream cs = CharStreams.fromFileName(args[0]);
        AlgumaLexer lexer = new AlgumaLexer(cs);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        AlgumaParser parser = new AlgumaParser(tokens);

        // Cria a árvore sintática
        AlgumaParser.ProgramaContext arvore = parser.programa();

        // Etapa 2: Análise Semântica
        AlgumaSemantico as = new AlgumaSemantico();
        as.visitPrograma(arvore);
        
        // Imprime erros semânticos, se houver
        AlgumaSemanticoUtils.errosSemanticos.forEach((s) -> System.out.println(s));

        if (AlgumaSemanticoUtils.errosSemanticos.isEmpty()) {
            // Etapa 3: Geração de Código C
            AlgumaGeradorC agc = new AlgumaGeradorC();
            agc.visitPrograma(arvore);
            try (PrintWriter pw = new PrintWriter(args[1])) {
                pw.print(agc.saida.toString());
            }

            // Etapa 4: Geração de Pseudo-código
            AlgumaGeradorPcodigo agp = new AlgumaGeradorPcodigo();
            String pcod = agp.visitPrograma(arvore);
            try (PrintWriter pw = new PrintWriter(args[2])) {
                pw.print(pcod);
            }
        } 
    }
}