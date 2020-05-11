package br.com.bluesoft.desafio.service;

import br.com.bluesoft.desafio.exception.BusinessException;
import br.com.bluesoft.desafio.model.dto.*;
import br.com.bluesoft.desafio.model.entity.Fornecedor;
import br.com.bluesoft.desafio.model.entity.ItemPedido;
import br.com.bluesoft.desafio.model.entity.Pedido;
import br.com.bluesoft.desafio.model.entity.Produto;
import br.com.bluesoft.desafio.repository.PedidoRepository;
import br.com.bluesoft.desafio.repository.ProdutoRepository;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PedidoService {

    private final FornecedorService fornecedorService;

    private final ProdutoRepository produtoRepository;

    private final PedidoRepository pedidoRepository;

    @Autowired
    public PedidoService(FornecedorService fornecedorService, ProdutoRepository produtoRepository, PedidoRepository pedidoRepository) {
        this.fornecedorService = fornecedorService;
        this.produtoRepository = produtoRepository;
        this.pedidoRepository = pedidoRepository;
    }

    public List<PedidoDTO> novoPedido(List<ProdutoDTO> produtos) {
        removeProdutosComQuantidadeZero(produtos);

        List<MultiValuedMap<String, ItemPedidoDTO>> itensPedido = produtos.stream().map(produto -> {
            produtoRepository.findById(produto.getGtin()).ifPresent(p -> produto.setNome(p.getNome()));

            List<FornecedorDTO> fornecedores = fornecedorService.getFornecedores(produto);

            FornecedorDTO melhorFornecedor = fornecedorService.getMelhorFornecedor(produto, fornecedores);
            PrecoFornecedorDTO melhorPreco = fornecedorService.getMelhorPrecoByFornecedor(produto, melhorFornecedor);

            ItemPedidoDTO itemPedido = criaItemPedido(produto, melhorPreco);

            MultiValuedMap<String, ItemPedidoDTO> map = new ArrayListValuedHashMap<>();
            map.put(melhorFornecedor.getCnpj(), itemPedido);
            return map;
        }).collect(Collectors.toList());

        MultiValuedMap<String, ItemPedidoDTO> all = unePedidosPorFornecedor(itensPedido);

        return criaPedidos(all);
    }

    private void removeProdutosComQuantidadeZero(List<ProdutoDTO> produtos) {
        produtos.removeIf(p -> Objects.isNull(p.getQuantidade()) || p.getQuantidade() <= 0);

        if (CollectionUtils.isEmpty(produtos)) {
            throw new BusinessException("Nenhuma quantidade informada.");
        }
    }

    private ItemPedidoDTO criaItemPedido(ProdutoDTO produtoDTO, PrecoFornecedorDTO melhorPreco) {
        return ItemPedidoDTO.builder()
                .produto(produtoDTO)
                .quantidade(produtoDTO.getQuantidade())
                .preco(melhorPreco.getPreco())
                .total(melhorPreco.getPreco().multiply(new BigDecimal(produtoDTO.getQuantidade())))
                .build();
    }

    private MultiValuedMap<String, ItemPedidoDTO> unePedidosPorFornecedor(List<MultiValuedMap<String, ItemPedidoDTO>> itensPedido) {
        MultiValuedMap<String, ItemPedidoDTO> all = new ArrayListValuedHashMap<>();
        itensPedido.forEach(all::putAll);
        return all;
    }

    private List<PedidoDTO> criaPedidos(MultiValuedMap<String, ItemPedidoDTO> all) {
        List<PedidoDTO> pedidos = all.keySet().stream().map(cnpj -> {
            Collection<ItemPedidoDTO> itens = all.get(cnpj);
            Fornecedor fornecedor = fornecedorService.getFornecedor(cnpj);
            return PedidoDTO.builder().itens(new ArrayList<>(itens)).fornecedor(fornecedor.toDTO()).build();
        }).collect(Collectors.toList());

        savePedidos(pedidos);

        return pedidos;
    }

    private void savePedidos(List<PedidoDTO> pedidosDTO) {
        pedidosDTO.forEach(pedidoDTO -> {
            Fornecedor fornecedor = fornecedorService.getFornecedor(pedidoDTO.getFornecedor().getCnpj());
            Pedido pedido = Pedido.builder().fornecedor(fornecedor).build();
            List<ItemPedido> itensLista = pedidoDTO.getItens().stream().map(item -> ItemPedido.builder()
                    .preco(item.getPreco())
                    .quantidade(item.getQuantidade())
                    .produto(produtoRepository.findById(item.getProduto().getGtin()).orElse(new Produto()))
                    .total(item.getTotal())
                    .build()
            ).collect(Collectors.toList());
            pedido.setItens(itensLista);
            pedidoRepository.save(pedido);
            pedidoDTO.setId(pedido.getId());
        });
    }
}
