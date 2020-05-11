package br.com.bluesoft.desafio.model.entity;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "ITEM_PEDIDO")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "GTIN", referencedColumnName = "GTIN")
    private Produto produto;

    private Integer quantidade;

    private BigDecimal preco;

    private BigDecimal total;

}
