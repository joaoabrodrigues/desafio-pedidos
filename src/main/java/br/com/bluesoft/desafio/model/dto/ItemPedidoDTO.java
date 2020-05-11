package br.com.bluesoft.desafio.model.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemPedidoDTO {

    private ProdutoDTO produto;

    private Integer quantidade;

    private BigDecimal preco;

    private BigDecimal total;

}
