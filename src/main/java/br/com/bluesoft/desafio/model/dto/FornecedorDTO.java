package br.com.bluesoft.desafio.model.dto;

import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FornecedorDTO {

    private String cnpj;

    private String nome;

    private List<PrecoFornecedorDTO> precos;

}
