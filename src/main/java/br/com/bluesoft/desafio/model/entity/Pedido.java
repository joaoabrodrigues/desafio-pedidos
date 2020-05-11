package br.com.bluesoft.desafio.model.entity;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Fornecedor fornecedor;

    @Setter
    @OneToMany(targetEntity = ItemPedido.class, cascade = CascadeType.ALL)
    private List<ItemPedido> itens;

}
