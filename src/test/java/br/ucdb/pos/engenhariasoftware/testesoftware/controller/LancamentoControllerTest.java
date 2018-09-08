package br.ucdb.pos.engenhariasoftware.testesoftware.controller;

import br.ucdb.pos.engenhariasoftware.testesoftware.converter.DateToStringConverter;
import br.ucdb.pos.engenhariasoftware.testesoftware.converter.MoneyToStringConverter;
import br.ucdb.pos.engenhariasoftware.testesoftware.converter.StringToMoneyConverter;
import br.ucdb.pos.engenhariasoftware.testesoftware.modelo.Categoria;
import br.ucdb.pos.engenhariasoftware.testesoftware.modelo.Lancamento;
import br.ucdb.pos.engenhariasoftware.testesoftware.modelo.TipoLancamento;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.path.xml.XmlPath;
import io.restassured.response.Response;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;

import static io.restassured.RestAssured.given;
import static org.testng.Assert.assertEquals;

public class LancamentoControllerTest {

    @BeforeTest
    public void init() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
    }

    /* CONCLUSÃO
    * A comparação do JSON é falha, pois a mesma compara STRINGS, ou seja, o valor 100 para ele é menor que 50, nesse caso a comparação feita por código java não falhou.
    * Para funcionar a comparação  do Json tive o cuidado de escolher valores com o mesmo tamanho(caracteres).
    *
    * Obs: O teste irá levar em consideração somente os dados por ele gerado.
    * */

    @Test
    public void CasoTeste1() {
        removerLancamentos();
        Double valor  = 53.35;
        gerarLancamento(valor, 1);

        assertEquals(buscaMenorLancamentoMetodoJsonPath(), valor);
        assertEquals(buscaMenorLancamentoMetodoCodigoJava(), valor);
    }



    @Test
    public void CasoTeste2() {
        removerLancamentos();
        Double valor  = 23.35;
        gerarLancamento(valor, 2);

        assertEquals(buscaMenorLancamentoMetodoJsonPath(), valor);
        assertEquals(buscaMenorLancamentoMetodoCodigoJava(), valor);
    }

    @Test
    public void CasoTeste3() {
        removerLancamentos();
        Double valor  = 13.35;
        gerarLancamento(valor, 9);

        assertEquals(buscaMenorLancamentoMetodoJsonPath(), valor, "JsonPath");
        assertEquals(buscaMenorLancamentoMetodoCodigoJava(), valor, "Codigo Java");
    }

    private void gerarLancamento(Double valor, Integer quantidade) {
        for (int i = 0; i < quantidade; i++) {
            Lancamento lancamento = new Lancamento();
            Random aleatorio = new Random();
            Calendar calendario = Calendar.getInstance();
            calendario.set(2018, 9, aleatorio.nextInt(29) + 1);

            lancamento.setId(aleatorio.nextLong());
            lancamento.setDataLancamento(calendario.getTime());
            lancamento.setTipoLancamento(aleatorio.nextInt(2) == 1 ? TipoLancamento.ENTRADA : TipoLancamento.SAIDA);
            lancamento.setDescricao("LinonElCabron");
            lancamento.setCategoria(Categoria.ALIMENTACAO);
            lancamento.setValor(BigDecimal.valueOf( i<= 3 ? ((i+1)*valor) : ((i-2)*valor)));

            salvarLancamento(lancamento);
        }
    }

    private void salvarLancamento(Lancamento lancamento) {
        Response response = given().when()
                .formParam("descricao", lancamento.getDescricao())
                .formParam("valor", new MoneyToStringConverter().convert(lancamento.getValor()))
                .formParam("dataLancamento", new DateToStringConverter().convert(lancamento.getDataLancamento()))
                .formParam("tipoLancamento", lancamento.getTipoLancamento())
                .formParam("categoria", lancamento.getCategoria())
                .header("Content-Type", "application/x-www-form-urlencoded")
                .post("/salvar");
        assertEquals(response.getStatusCode(), 302);
    }

    private List<Lancamento> buscaLancamentos() {
        Response response = given()
                .when()
                .body("LinonElCabron")
                .post("/buscaLancamentos");
        InputStream in = response.asInputStream();
        assertEquals(response.getStatusCode(), 200);

        return JsonPath.with(in).getList("lancamentos", Lancamento.class);
    }

    public void removerLancamentos() {
        List<Lancamento> lancamentos = buscaLancamentos();

        for (Lancamento lancamento : lancamentos) {
            given().pathParam("id", lancamento.getId()).when().get("/remover/{id}");
        }
    }

    private String buscaMenorLancamento() {
        Response response = given()
                .when()
                .body("LinonElCabron")
                .post("/buscaLancamentos");
        InputStream in = response.asInputStream();
        assertEquals(response.getStatusCode(), 200);

        return JsonPath.with(in).getString("lancamentos.min{it.valor}.valor");
    }

    private Double buscaMenorLancamentoMetodoJsonPath() {
        String valor = buscaMenorLancamento();
        return new StringToMoneyConverter().convert(valor).doubleValue();
    }

    private Double buscaMenorLancamentoMetodoCodigoJava() {
        List<Lancamento> lancamentos = buscaLancamentos();
        BigDecimal min = lancamentos
                .stream()
                .map(Lancamento::getValor)
                .min(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);
        return min.doubleValue();
    }

}
