package br.com.bluesoft.desafio.model.entity;

import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
@Getter
public class Produto {

    @Id
    private String gtin;

    private String nome;
}
