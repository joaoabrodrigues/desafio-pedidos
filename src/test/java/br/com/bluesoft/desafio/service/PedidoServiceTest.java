package br.com.bluesoft.desafio.service;

import br.com.bluesoft.desafio.exception.BusinessException;
import br.com.bluesoft.desafio.model.dto.FornecedorDTO;
import br.com.bluesoft.desafio.model.dto.PedidoDTO;
import br.com.bluesoft.desafio.model.dto.PrecoFornecedorDTO;
import br.com.bluesoft.desafio.model.dto.ProdutoDTO;
import br.com.bluesoft.desafio.model.entity.Fornecedor;
import br.com.bluesoft.desafio.model.entity.PrecoFornecedor;
import br.com.bluesoft.desafio.repository.PedidoRepository;
import br.com.bluesoft.desafio.repository.ProdutoRepository;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

public class PedidoServiceTest {

    @InjectMocks
    private PedidoService pedidoService;

    @Mock
    private FornecedorService fornecedorService;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private PedidoRepository pedidoRepository;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldCreateNovoPedido() {
        given(fornecedorService.getMelhorFornecedor(any(ProdutoDTO.class), anyList())).willReturn(getFornecedorDTO());
        given(fornecedorService.getMelhorPrecoByFornecedor(any(ProdutoDTO.class), any(FornecedorDTO.class))).willReturn(PrecoFornecedorDTO.builder().preco(BigDecimal.ONE).quantidadeMinima(1).build());
        given(fornecedorService.getFornecedor(anyString())).willReturn(Fornecedor.builder().nome("Fornecedor 1").cnpj("123").precos(Collections.singletonList(PrecoFornecedor.builder().build())).build());

        List<PedidoDTO> pedidos = pedidoService.novoPedido(Stream.of(ProdutoDTO.builder().gtin("123").quantidade(10).build()).collect(Collectors.toList()));
        assertEquals(1, pedidos.size());
        assertEquals("Fornecedor 1", pedidos.get(0).getFornecedor().getNome());
        assertEquals("123", pedidos.get(0).getFornecedor().getCnpj());
    }

    @Test
    public void shouldThrowExceptionWithQuantidadeZero() {
        expectedException.expect(BusinessException.class);
        expectedException.expectMessage("Nenhuma quantidade informada.");

        pedidoService.novoPedido(Stream.of(ProdutoDTO.builder().gtin("123").quantidade(0).build()).collect(Collectors.toList()));
    }

    private FornecedorDTO getFornecedorDTO() {
        return FornecedorDTO.builder()
                .nome("Fornecedor")
                .cnpj("123")
                .precos(Stream.of(PrecoFornecedorDTO.builder()
                        .quantidadeMinima(1)
                        .preco(BigDecimal.TEN)
                        .build()).collect(Collectors.toList()))
                .build();
    }

}