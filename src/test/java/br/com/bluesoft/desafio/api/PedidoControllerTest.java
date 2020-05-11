package br.com.bluesoft.desafio.api;

import br.com.bluesoft.desafio.model.dto.FornecedorDTO;
import br.com.bluesoft.desafio.model.dto.PrecoFornecedorDTO;
import br.com.bluesoft.desafio.model.dto.ProdutoDTO;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PedidoControllerTest extends AbstractMockMvc {

    private static final String PRODUTO_GTIN = "7894900011517";
    private static final String PRODUTO_NOME = "REFRIGERANTE COCA-COLA 2LT";
    private static final String FORNECEDOR_CNPJ = "42.217.933/0001-85";
    private static final String FORNECEDOR_NOME = "Fornecedor 3";

    @MockBean
    private RestTemplate restTemplate;

    private JacksonTester<List<ProdutoDTO>> json;

    @Test
    public void step1ShouldCreateNewOrder() throws Exception {
        given(restTemplate.getForObject(anyString(), eq(FornecedorDTO[].class))).willReturn(newFornecedoresArray());

        JsonContent<List<ProdutoDTO>> content = json.write(Collections.singletonList(ProdutoDTO.builder()
                .gtin(PRODUTO_GTIN)
                .nome(PRODUTO_NOME)
                .quantidade(10)
                .build()));

        mockMvc.perform(post("/api/pedidos")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(content.getJson()))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].fornecedor.cnpj", is(FORNECEDOR_CNPJ)))
                .andExpect(jsonPath("$[0].fornecedor.nome", is(FORNECEDOR_NOME)))
                .andExpect(jsonPath("$[0].itens", hasSize(1)))
                .andExpect(jsonPath("$[0].itens[0].produto.gtin", is(PRODUTO_GTIN)))
                .andExpect(jsonPath("$[0].itens[0].produto.quantidade", is(10)))
                .andExpect(jsonPath("$[0].itens[0].produto.nome", is(PRODUTO_NOME)))
                .andExpect(jsonPath("$[0].itens[0].quantidade", is(10)))
                .andExpect(jsonPath("$[0].itens[0].preco", equalTo(10.00)))
                .andExpect(jsonPath("$[0].itens[0].total", equalTo(100.00)));
    }

    @Test
    public void step2ShouldFindAllOrdersAfterCreation() throws Exception {
        mockMvc.perform(get("/api/pedidos"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].fornecedor.cnpj", is(FORNECEDOR_CNPJ)))
                .andExpect(jsonPath("$[0].fornecedor.nome", is(FORNECEDOR_NOME)))
                .andExpect(jsonPath("$[0].itens", hasSize(1)))
                .andExpect(jsonPath("$[0].itens[0].produto.gtin", is(PRODUTO_GTIN)))
                .andExpect(jsonPath("$[0].itens[0].produto.nome", is(PRODUTO_NOME)))
                .andExpect(jsonPath("$[0].itens[0].quantidade", is(10)))
                .andExpect(jsonPath("$[0].itens[0].preco", equalTo(10.00)))
                .andExpect(jsonPath("$[0].itens[0].total", equalTo(100.0)));
    }

    private FornecedorDTO[] newFornecedoresArray() {
        return new FornecedorDTO[]{FornecedorDTO.builder()
                .nome(FORNECEDOR_NOME)
                .cnpj(FORNECEDOR_CNPJ)
                .precos(Stream.of(PrecoFornecedorDTO.builder()
                        .quantidadeMinima(1)
                        .preco(BigDecimal.valueOf(10.00))
                        .build()).collect(Collectors.toList()))
                .build()};
    }
}