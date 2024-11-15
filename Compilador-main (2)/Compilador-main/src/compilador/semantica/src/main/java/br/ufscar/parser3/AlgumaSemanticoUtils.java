package compilador.semantica.src.main.java.br.ufscar.parser3;

import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.Token;

public class AlgumaSemanticoUtils {
       
    
    
    public static List<String> errosSemanticos = new  ArrayList<>();
    public static void adicionarErroSemantico(Token t, String mensagem) { 
      int linha = t.getLine();
      int coluna= t.getCharPositionInLine();
    errosSemanticos.add(String.format ("Erro %d:%d - %s",linha, coluna, mensagem));
    }

    public static TabelaDeSimbolos.TipoAlguma verificarTipo(TabelaDeSimbolos tabela,
       AlgumaParser.ExpressaoAritmeticaContext ctx) {
     TabelaDeSimbolos.TipoAlguma ret = null;
     for(var ta: ctx.termoAritmetico()) {
         TabelaDeSimbolos.TipoAlguma aux = verificarTipo(tabela, ta);
         if(ret == null) {
         ret = aux;     
    } else if (ret != aux && aux != TabelaDeSimbolos.TipoAlguma.INVALIDO){
        adicionarErroSemantico (ctx.start,"Expressão"+ctx.getText()+ "contém tipos incompatíveis");
        ret = TabelaDeSimbolos.TipoAlguma.INVALIDO;
    }
}
         return ret;
} 
     private static TabelaDeSimbolos.TipoAlguma verificarTipo(TabelaDeSimbolos tabela, AlgumaParser.termoAritmeticoContext ctx) {
     TabelaDeSimbolos.TipoAlguma ret = null;
    
      for (var fa : ctx.fatorAritmetico()) {
         TabelaDeSimbolos.TipoAlguma aux = verificarTipo (tabela, fa);
                  if (ret == null) {
                  ret = aux;
               }  else if (ret != aux && aux != TabelaDeSimboIos.TipoAIguma.INVALIDO) {
                adicionarErroSemantico(ctx.start, "Termo " + ctx.getText() + "contém tipos incompatíveis");
                ret = TabelaDeSimbolos.TipoAlguma.INVALID0;
               }
             return ret; }
             
         
         
         }
         
             private static TabelaDeSimbolos.TipoAlguma verificarTipo(TabelaDeSimbolos tabela, AlgumaParser.fatorAritmeticoContext ctx) {
                if(ctx.NUMINT() != null) {
                    return TabelaDeSimbolos.TipoAlguma.INTEIRO;
                    } else if(ctx.NUMREAL() != null)  {
                    return TabelaDeSimbolos.TipoAlguma.REAL;
                    } else if(ctx.expressaoAritmetica() != null) {
                    return verificarTipo (tabela, ctx.expressaoAritmetica());
                    } else {
                      String nomeVar = ctx.VARIAVEL().getText();
                      if(!tabela.existe(nomeVar)) {
                        adicionarErroSemantico(ctx.VARIAVEL().getSymbol(),"VARIAVEL" +nomeVar+ "não foi declarada antes do uso" );
                        return TabelaDeSimbolos.TipoAlguma.INVALIDO;
                      }
                    return verificarTipo (Tabela, nomeVar);
             
             }

            }
        }
