package br.com.bluesoft.desafio.model.entity;

import br.com.bluesoft.desafio.model.dto.FornecedorDTO;
import br.com.bluesoft.desafio.model.dto.PrecoFornecedorDTO;
import lombok.*;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Fornecedor {

    @Id
    private String cnpj;

    private String nome;

    @Setter
    @OneToMany(targetEntity = PrecoFornecedor.class, cascade = CascadeType.ALL)
    private List<PrecoFornecedor> precos;

    public FornecedorDTO toDTO() {
        return FornecedorDTO.builder()
                .cnpj(getCnpj())
                .nome(getNome())
                .precos(getPrecos().stream().map(p ->
                        PrecoFornecedorDTO.builder()
                                .preco(p.getPreco())
                                .quantidadeMinima(p.getQuantidadeMinima())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}
