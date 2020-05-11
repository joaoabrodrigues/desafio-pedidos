package br.com.bluesoft.desafio.repository;

import br.com.bluesoft.desafio.model.entity.Fornecedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FornecedorRepository extends JpaRepository<Fornecedor, String> {
}
