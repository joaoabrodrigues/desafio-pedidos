package br.com.bluesoft.desafio.model.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProdutoDTO {

    private String gtin;

    private Integer quantidade;

    @Setter
    private String nome;
}
