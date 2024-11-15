package compilador.semantica.src.main.java.br.ufscar.parser3;

import java.util.List;

import br.ufscar.dc.compiladores.expr.parser3.Escopos;
import br.ufscar.dc.compiladores.expr.parser3.ExpressoesParser.TermoContext;
import br.ufscar.dc.compiladores.expr.parser3.TabelaDeSimbolos;


    public class Calculador extends ExpressoesBaseVisitor<Double> {
      Escopos escoposAninhados = new Escopos();

    @Override
     public Double visitPrograma(ExpressoesParser.ProgramaContext ctx) {
        return visitExp(ctx.exp());
     }

    @Override
    public Double visitExp(ExpressoesParser.ExpContext ctx) {
       double valor = visitTermo(ctx.termo1);
       for(TermoContext ot : ctx.outrosTermos) {
          valor += visitTermo(ot);
}
        return valor;
    }
    @Override
     public Double visitTermo(TermoContext ctx) {
        if(ctx.expParentesis != null) {
                return visitExp(ctx.expParentesis);
            } else if(ctx.constante != null) {
                return Double.parseDouble(ctx.constante.getText() ) ;
            } else if(ctx.variavel != null) {
                List<TabelaDeSimbolos> escopos = escoposAninhados.percorrerEscoposAninhados();
                for (TabelaDeSimbolos ts : escopos) {
                EntradaTabelaDeSimbolos etds= ts.verificar(ctx.variavel.getText());
                if(etds != null) {
                  return etds.valor;
                }
            }
            throw new RuntimeException("Erro semântico: "+ctx.variavel.getText()+ "não foi declarada antes do uso" );
          } else {
            escoposAninhados.criarNovoEscopo();
            visitListaDecl(ctx.listaDecl());
            double retorno = visitExp(ctx.subexp);
            escoposAninhados.abandonarEscopo();
            return retorno;
          }
}
    @Override
     public Double visitDecI(ExpressoesParser.DecIContext ctx) {
        TabelaDeSimbolos escopoAtual = escoposAninhados.obterEscopoAtual() ;
        if (escopoAtual.verificar(ctx.nome.getText()) != null) {
            throw new RuntimeException("Erro semântico: "+ctx.nome.getText()+ "declarada duas vezes num mesmo escopo");
        }

      else {
          escopoAtuaI.inserir(ctx.nome. getText(), visitExp(ctx. exp()));
}
           return null;


    }
  }
