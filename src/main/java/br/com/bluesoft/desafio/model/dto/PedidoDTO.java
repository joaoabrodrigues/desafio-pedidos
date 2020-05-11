package br.com.bluesoft.desafio.model.dto;

import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PedidoDTO {

    @Setter
    private Long id;

    private FornecedorDTO fornecedor;

    private List<ItemPedidoDTO> itens;

}
