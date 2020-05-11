package br.com.bluesoft.desafio.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PrecoFornecedorDTO {

    private BigDecimal preco;

    @JsonProperty(value = "quantidade_minima")
    private Integer quantidadeMinima;

}
