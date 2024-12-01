package br.ufscar.dc.compiladores.alguma.semantico;

import br.ufscar.dc.compiladores.alguma.semantico.TabelaDeSimbolos.TipoAlguma;

public class AlgumaGeradorC extends AlgumaBaseVisitor<Void> {

    StringBuilder saida;// Armazena o código C gerado
    TabelaDeSimbolos tabela;// Tabela de símbolos para controle semântico

    // Construtor inicializa os objetos de saída e tabela de símbolos
    public AlgumaGeradorC() {
        saida = new StringBuilder();
        this.tabela = new TabelaDeSimbolos();
    }

    @Override
    public Void visitPrograma(AlgumaParser.ProgramaContext ctx) {
        // Adiciona os cabeçalhos padrão do C
        saida.append("#include <stdio.h>\n");
        saida.append("#include <stdlib.h>\n");
        saida.append("\n");

        // Visita as declarações do programa
        ctx.declaracao().forEach(dec -> visitDeclaracao(dec));
        saida.append("\n");

        // Adiciona a função main
        saida.append("int main() {\n");

        // Visita os comandos do programa
        ctx.comando().forEach(cmd -> visitComando(cmd));
        saida.append("}\n");
        return null;
    }

    @Override
    public Void visitDeclaracao(AlgumaParser.DeclaracaoContext ctx) {
        String nomeVar = ctx.VARIAVEL().getText();
        String strTipoVar = ctx.TIPO_VAR().getText();
        TabelaDeSimbolos.TipoAlguma tipoVar = TabelaDeSimbolos.TipoAlguma.INVALIDO;

        // Mapeia os tipos de Alguma para os tipos equivalentes em C
        switch (strTipoVar) {
            case "INTEIRO":
                tipoVar = TabelaDeSimbolos.TipoAlguma.INTEIRO;
                strTipoVar = "int";
                break;
            case "REAL":
                tipoVar = TabelaDeSimbolos.TipoAlguma.REAL;
                strTipoVar = "float";
                break;
            default:
                // Caso não esperado, pois a análise sintática já valida os tipos
                break;
        }

        // Adiciona a variável na tabela de símbolos
        tabela.adicionar(nomeVar, tipoVar);
        saida.append(strTipoVar + " " + nomeVar + ";\n");
        return null;
    }

    @Override
    public Void visitComandoAtribuicao(AlgumaParser.ComandoAtribuicaoContext ctx) {
        // Gera atribuição de variável no formato C
        saida.append(ctx.VARIAVEL().getText() + " = ");
        visitExpressaoAritmetica(ctx.expressaoAritmetica());
        saida.append(";\n");
        return null;
    }

    @Override
    public Void visitComandoCondicao(AlgumaParser.ComandoCondicaoContext ctx) {
        saida.append("if(");
        visitExpressaoRelacional(ctx.expressaoRelacional());
        saida.append(")\n");
        visitComando(ctx.comando(0));
        if (ctx.comando().size() > 1) { // tem else
            saida.append("else\n");
            visitComando(ctx.comando(1));
        }
        return null;
    }

    @Override
    public Void visitComandoEntrada(AlgumaParser.ComandoEntradaContext ctx) {
        String nomeVar = ctx.VARIAVEL().getText();
        TipoAlguma tipoVariavel = AlgumaSemanticoUtils.verificarTipo(tabela, nomeVar);
        String aux = "";
        switch (tipoVariavel) {
            case INTEIRO:
                aux = "%d";
                break;
            case REAL:
                aux = "%f";
                break;
        }

        // Gera o comando scanf correspondente
        saida.append("scanf(\"" + aux + "\", &" + nomeVar + ");\n");
        return null;
    }

    @Override
    public Void visitComandoRepeticao(AlgumaParser.ComandoRepeticaoContext ctx) {
        saida.append("while(");
        visitExpressaoRelacional(ctx.expressaoRelacional());
        saida.append(")\n");
        visitComando(ctx.comando());
        return null;
    }

    @Override
    public Void visitComandoSaida(AlgumaParser.ComandoSaidaContext ctx) {
        if (ctx.CADEIA() != null) {
            // Saída de uma cadeia de caracteres
            String aux = ctx.CADEIA().getText();
            aux = aux.substring(1, aux.length() - 1);
            saida.append("printf(\"" + aux + "\\n\");\n");
        } else {
            // Saída de uma expressão aritmética
            TipoAlguma tipoExpressao = AlgumaSemanticoUtils.verificarTipo(tabela, ctx.expressaoAritmetica());
            String aux = "";

            // Seleciona o formato correto para a impressão
            switch (tipoExpressao) {
                case INTEIRO:
                    aux = "%d";
                    break;
                case REAL:
                    aux = "%f";
                    break;
            }
            saida.append("printf(\"" + aux + "\\n\",");
            visitExpressaoAritmetica(ctx.expressaoAritmetica());
            saida.append(");\n");
        }
        return null;
    }

    @Override
    public Void visitSubAlgoritmo(AlgumaParser.SubAlgoritmoContext ctx) {
        // Gera um bloco de código com chaves
        saida.append("{\n");
        ctx.comando().forEach(cmd -> visitComando(cmd));
        saida.append("}\n");
        return null;
    }

    @Override
    public Void visitExpressaoAritmetica(AlgumaParser.ExpressaoAritmeticaContext ctx) {
        // Visita termos e operadores aritméticos
        visitTermoAritmetico(ctx.termoAritmetico(0));
        for (int i = 0; i < ctx.OP_ARIT1().size(); i++) {
            saida.append(" " + ctx.OP_ARIT1(i).getText() + " ");
            visitTermoAritmetico(ctx.termoAritmetico(i + 1));
        }
        return null;
    }

    @Override
    public Void visitTermoAritmetico(AlgumaParser.TermoAritmeticoContext ctx) {
        // Processa fatores aritméticos
        visitFatorAritmetico(ctx.fatorAritmetico(0));
        for (int i = 0; i < ctx.OP_ARIT2().size(); i++) {
            saida.append(" " + ctx.OP_ARIT2(i).getText() + " ");
            visitFatorAritmetico(ctx.fatorAritmetico(i + 1));
        }
        return null;
    }

    @Override
    public Void visitFatorAritmetico(AlgumaParser.FatorAritmeticoContext ctx) {
        // Verifica o tipo do fator (número, variável ou expressão)
        if (ctx.NUMINT() != null) {
            saida.append(ctx.NUMINT().getText());
        } else if (ctx.NUMREAL() != null) {
            saida.append(ctx.NUMREAL().getText());
        } else if (ctx.VARIAVEL() != null) {
            saida.append(ctx.VARIAVEL().getText());
        } else {
            saida.append("(");
            visitExpressaoAritmetica(ctx.expressaoAritmetica());
            saida.append(")");
        }
        return null;
    }

    @Override
    public Void visitExpressaoRelacional(AlgumaParser.ExpressaoRelacionalContext ctx) {
        // Processa expressões relacionais e booleanas
        visitTermoRelacional(ctx.termoRelacional(0));
        for (int i = 0; i < ctx.OP_BOOL().size(); i++) {
            String aux = null;
            if (ctx.OP_BOOL(i).getText().equals("E")) {
                aux = "&&";
            } else {
                aux = "||";
            }
            saida.append(" " + aux + " ");
            visitTermoRelacional(ctx.termoRelacional(i + 1));
        }
        return null;
    }

    @Override
    public Void visitTermoRelacional(AlgumaParser.TermoRelacionalContext ctx) {
        // Processa termos relacionais ou subexpressões
        if (ctx.expressaoRelacional() != null) {
            saida.append("(");
            visitExpressaoRelacional(ctx.expressaoRelacional());
            saida.append(")");
        } else {
            visitExpressaoAritmetica(ctx.expressaoAritmetica(0));
            String aux = ctx.OP_REL().getText();
            if (aux.equals("<>")) {
                aux = "!=";
            } else if (aux.equals("=")) {
                aux = "==";
            }
            saida.append(" " + aux + " ");
            visitExpressaoAritmetica(ctx.expressaoAritmetica(1));
        }
        return null;
    }
}
