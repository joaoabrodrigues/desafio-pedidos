package br.com.bluesoft.desafio.repository;

import org.springframework.data.repository.CrudRepository;

import br.com.bluesoft.desafio.model.entity.Produto;

public interface ProdutoRepository extends CrudRepository<Produto, String> {
}
