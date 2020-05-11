package br.com.bluesoft.desafio.service;

import br.com.bluesoft.desafio.model.dto.FornecedorDTO;
import br.com.bluesoft.desafio.model.dto.PrecoFornecedorDTO;
import br.com.bluesoft.desafio.model.dto.ProdutoDTO;
import br.com.bluesoft.desafio.model.entity.Fornecedor;
import br.com.bluesoft.desafio.repository.FornecedorRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

public class FornecedorServiceTest {

    private static final String FORNECEDOR_1_NOME = "Fornecedor 1";
    private static final String FORNECEDOR_1_CNPJ = "12.345.678/0001-90";

    private static final String FORNECEDOR_3_NOME = "Fornecedor 3";
    private static final String FORNECEDOR_3_CNPJ = "42.217.933/0001-85";

    @InjectMocks
    private FornecedorService fornecedorService;

    @Mock
    private FornecedorRepository fornecedorRepository;

    @Mock
    private RestTemplate restTemplate;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldGetFornecedoresByProduto() {
        given(restTemplate.getForObject(anyString(), eq(FornecedorDTO[].class))).willReturn(newFornecedoresArray());

        List<FornecedorDTO> fornecedores = fornecedorService.getFornecedores(ProdutoDTO.builder().build());

        assertEquals(1, fornecedores.size());
        assertEquals(FORNECEDOR_3_CNPJ, fornecedores.get(0).getCnpj());
        assertEquals(FORNECEDOR_3_NOME, fornecedores.get(0).getNome());
        assertEquals(1, fornecedores.get(0).getPrecos().size());
    }

    @Test
    public void shouldGetMelhorFornecedorByProduto() {
        ProdutoDTO produto = ProdutoDTO.builder().quantidade(10).build();
        List<FornecedorDTO> fornecedores = Arrays.asList(
                getFornecedorDTO(BigDecimal.valueOf(1.00) , FORNECEDOR_1_NOME, FORNECEDOR_1_CNPJ),
                getFornecedorDTO(BigDecimal.valueOf(1.10) , FORNECEDOR_3_NOME, FORNECEDOR_3_CNPJ));

        FornecedorDTO melhorFornecedor = fornecedorService.getMelhorFornecedor(produto, fornecedores);

        assertEquals(FORNECEDOR_1_NOME, melhorFornecedor.getNome());
        assertEquals(FORNECEDOR_1_CNPJ, melhorFornecedor.getCnpj());
    }

    @Test
    public void shouldGetMelhorPrecoByFornecedor() {
        FornecedorDTO fornecedorDTO = getFornecedorDTO(BigDecimal.valueOf(1.00), FORNECEDOR_1_NOME, FORNECEDOR_1_CNPJ);

        PrecoFornecedorDTO melhorPreco = fornecedorService.getMelhorPrecoByFornecedor(ProdutoDTO.builder().quantidade(10).build(), fornecedorDTO);

        assertEquals(BigDecimal.valueOf(1.0), melhorPreco.getPreco());
        assertEquals(Integer.valueOf(1), melhorPreco.getQuantidadeMinima());
    }

    @Test
    public void shouldGetFornecedorFromDatabase() {
        given(fornecedorRepository.findById(anyString())).willReturn(Optional.of(Fornecedor.builder().cnpj("123").nome("fornecedor").build()));

        Fornecedor fornecedor = fornecedorService.getFornecedor("123");

        assertEquals("123", fornecedor.getCnpj());
        assertEquals("fornecedor", fornecedor.getNome());
    }

    private FornecedorDTO[] newFornecedoresArray() {
        return new FornecedorDTO[]{getFornecedorDTO(BigDecimal.valueOf(10.00), FORNECEDOR_3_NOME, FORNECEDOR_3_CNPJ)};
    }

    private FornecedorDTO getFornecedorDTO(BigDecimal preco, String nome, String cnpj) {
        return FornecedorDTO.builder()
                .nome(nome)
                .cnpj(cnpj)
                .precos(Stream.of(PrecoFornecedorDTO.builder()
                        .quantidadeMinima(1)
                        .preco(preco)
                        .build()).collect(Collectors.toList()))
                .build();
    }
}