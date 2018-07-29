package br.ucdb.pos.engenhariasoftware.testesoftware.service;

import br.ucdb.pos.engenhariasoftware.testesoftware.controller.vo.LancamentoVO;
import br.ucdb.pos.engenhariasoftware.testesoftware.controller.vo.ResultadoVO;
import br.ucdb.pos.engenhariasoftware.testesoftware.modelo.Lancamento;
import br.ucdb.pos.engenhariasoftware.testesoftware.modelo.TipoLancamento;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.stubbing.Answer;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import static org.testng.Assert.assertTrue;

import static br.ucdb.pos.engenhariasoftware.testesoftware.modelo.TipoLancamento.ENTRADA;
import static br.ucdb.pos.engenhariasoftware.testesoftware.modelo.TipoLancamento.SAIDA;
import static br.ucdb.pos.engenhariasoftware.testesoftware.util.Constantes.DD_MM_YYYY;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class LancamentoServiceTest {

    @Mock
    private LancamentoService lancamentoService;

    @BeforeClass
    public void init(){
        MockitoAnnotations.initMocks(this);
    }

    @DataProvider(name = "lancamentos0")
    protected Object[][] getLancamentos0() {
        return new Object[][]{
                new Object[]{new ArrayList<>()}
        };
    }

    @DataProvider(name = "lancamentos1")
    protected Object[][] getLancamentos1() {
        List<Lancamento> lancamentosCenario = new ArrayList<>();
        lancamentosCenario.add(new LancamentoBuilder( new Random()).build());
        return new Object[][]{
                new Object[]{lancamentosCenario}
        };
    }

    @DataProvider(name = "lancamentos3")
    protected Object[][] getLancamentos3() {
        List<Lancamento> lancamentosCenario = new ArrayList<>();
        for (int i = 0; i < 3; i++) {

            lancamentosCenario.add(new LancamentoBuilder( new Random()).build());
        }
        return new Object[][]{
                new Object[]{lancamentosCenario}
        };
    }

    @DataProvider(name = "lancamentos9")
    protected Object[][] getLancamentos9() {
        List<Lancamento> lancamentosCenario = new ArrayList<>();
        for (int i = 0; i < 9; i++) {

            lancamentosCenario.add(new LancamentoBuilder( new Random()).build());
        }
        return new Object[][]{
                new Object[]{lancamentosCenario}
        };
    }

    @DataProvider(name = "lancamentos10")
    protected Object[][] getLancamentos10() {
        List<Lancamento> lancamentosCenario = new ArrayList<>();
        for (int i = 0; i < 10; i++) {

            lancamentosCenario.add(new LancamentoBuilder( new Random()).build());
        }
        return new Object[][]{
                new Object[]{lancamentosCenario}
        };
    }

    @Test(dataProvider = "lancamentos0", groups = "cenario0")
    public void buscaAjax0(List<Lancamento> lancamentos) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        this.buscaAjaxTest(lancamentos, 0);
    }

    @Test(dataProvider = "lancamentos1", groups = "cenario1")
    public void buscaAjax1(List<Lancamento> lancamentos) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        this.buscaAjaxTest(lancamentos, 1);
    }

    @Test(dataProvider = "lancamentos3", groups = "cenario3")
    public void buscaAjax3(List<Lancamento> lancamentos) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        this.buscaAjaxTest(lancamentos, 3);
    }

    @Test(dataProvider = "lancamentos9", groups = "cenario9")
    public void buscaAjax9(List<Lancamento> lancamentos) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        this.buscaAjaxTest(lancamentos, 9);
    }

    @Test(dataProvider = "lancamentos10", groups = "cenario10")
    public void buscaAjax10(List<Lancamento> lancamentos) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        this.buscaAjaxTest(lancamentos, 10);
    }

    private void buscaAjaxTest(List<Lancamento> lancamentos, long tamanho) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        when(lancamentoService.getTotalEntrada(anyListOf(Lancamento.class))).thenCallRealMethod();
        when(lancamentoService.getTotalSaida(anyListOf(Lancamento.class))).thenCallRealMethod();
        when(lancamentoService.somaValoresPorTipo(anyListOf(Lancamento.class), any(TipoLancamento.class))).thenCallRealMethod();
        when(lancamentoService.getResultadoVO(anyListOf(Lancamento.class), anyInt(), anyLong())).thenCallRealMethod();
        when(lancamentoService.buscaAjax(anyString())).thenCallRealMethod();

        when(lancamentoService.busca(anyString())).thenReturn(lancamentos);
        when(lancamentoService.conta(anyString())).thenReturn((long) lancamentos.size());
        when(lancamentoService.tamanhoPagina()).thenReturn(10);

        BigDecimal totalEntrada = this.getTotalPorTipo(lancamentos, TipoLancamento.ENTRADA);
        BigDecimal totalEntradaObtido = lancamentoService.getTotalEntrada(lancamentos);
        assertEquals(totalEntradaObtido, totalEntrada);
        BigDecimal totalSaida = this.getTotalPorTipo(lancamentos, TipoLancamento.SAIDA) ;
        BigDecimal totalSaidadaObtido = lancamentoService.getTotalSaida(lancamentos);
        assertEquals(totalSaidadaObtido, totalSaida, "totalSaidadaObtido "+ totalSaidadaObtido.toString() + "totalSaida" + totalSaida.toString());

        long tamanhoObtido = lancamentoService.buscaAjax(anyString()).getTotalRegistros();
        assertEquals(tamanhoObtido, tamanho);

        final ResultadoVO resultadoVO = lancamentoService.buscaAjax(anyString());

        Field[] campos = Lancamento.class.getDeclaredFields();

        for (LancamentoVO lancamentoVO : resultadoVO.getLancamentos()) {
            for (Field campo : campos) {
                String atributo = campo.getName();

                assertTrue(doesObjectContainField(lancamentoVO, atributo));
                String valorAtributo = getMethodValue(lancamentoVO, atributo).toString();
                assertTrue(!valorAtributo.equals("") && valorAtributo != null);
            }
        }
    }

    private BigDecimal getTotalPorTipo(List<Lancamento> lancamentos, TipoLancamento tipo) {
        if (lancamentos.size() == 0)
            return BigDecimal.ZERO;
        return lancamentos.stream()
                .filter(l -> l.getTipoLancamento() == tipo)
                .map(Lancamento::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private boolean doesObjectContainField(Object object, String fieldName) {
        return Arrays.stream(object.getClass().getDeclaredFields()).anyMatch(f -> f.getName().equals(fieldName));
    }

    private Object getMethodValue(Object object, String atributo) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        try {
            String getMethodtext = "get" + atributo.substring(0, 1).toUpperCase() + atributo.substring(1);
            Method getMethod = object.getClass().getMethod(getMethodtext, new Class[]{});
            return getMethod.invoke(object, new Object[]{});
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public class LancamentoBuilder{
        private Lancamento lancamento;

        public LancamentoBuilder(Random aleatorio){
            Calendar calendario = Calendar.getInstance();
            calendario.set(2018, 7, aleatorio.nextInt(30) + 1);
            lancamento = new Lancamento();
            lancamento.setId(aleatorio.nextLong());
            lancamento.setDataLancamento(calendario.getTime());
            lancamento.setTipoLancamento(aleatorio.nextInt(2) == 1 ? TipoLancamento.ENTRADA : TipoLancamento.SAIDA);
            lancamento.setDescricao("Lançamento do tipo "+ lancamento.getTipoLancamento() + " na data " + new SimpleDateFormat("dd/MM/yyyy").format(lancamento.getDataLancamento()));
            lancamento.setValor(new BigDecimal(aleatorio.nextFloat()));
        }

        Lancamento build(){
            return lancamento;
        }
    }
}
