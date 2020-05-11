package br.com.bluesoft.desafio.service;

import br.com.bluesoft.desafio.exception.BusinessException;
import br.com.bluesoft.desafio.model.dto.FornecedorDTO;
import br.com.bluesoft.desafio.model.dto.PrecoFornecedorDTO;
import br.com.bluesoft.desafio.model.dto.ProdutoDTO;
import br.com.bluesoft.desafio.model.entity.Fornecedor;
import br.com.bluesoft.desafio.model.entity.PrecoFornecedor;
import br.com.bluesoft.desafio.repository.FornecedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
public class FornecedorService {

    private final FornecedorRepository fornecedorRepository;

    @Value("${fornecedores.url}")
    private String fornecedoresUrl;

    private final RestTemplate restTemplate;

    @Autowired
    public FornecedorService(RestTemplate restTemplate, FornecedorRepository fornecedorRepository) {
        this.restTemplate = restTemplate;
        this.fornecedorRepository = fornecedorRepository;
    }

    public List<FornecedorDTO> getFornecedores(ProdutoDTO produto) {
        FornecedorDTO[] fornecedoresArray = restTemplate.getForObject(fornecedoresUrl + produto.getGtin(), FornecedorDTO[].class);
        return Optional.ofNullable(fornecedoresArray).map(Arrays::asList).orElseThrow(getRuntimeExceptionSupplier(produto));
    }

    public FornecedorDTO getMelhorFornecedor(ProdutoDTO produto, List<FornecedorDTO> fornecedores) {
        FornecedorDTO fornecedorDTO = fornecedores.stream()
                .filter(f -> {
                    f.getPrecos().removeIf(p -> p.getQuantidadeMinima() > produto.getQuantidade());
                    return !CollectionUtils.isEmpty(f.getPrecos());
                }).min((fornecedor1, fornecedor2) -> {
                    fornecedor1.getPrecos().sort(Comparator.comparing(PrecoFornecedorDTO::getPreco));
                    PrecoFornecedorDTO preco1 = fornecedor1.getPrecos().stream().findFirst().orElseThrow(getRuntimeExceptionSupplier(produto));

                    fornecedor2.getPrecos().sort(Comparator.comparing(PrecoFornecedorDTO::getPreco));
                    PrecoFornecedorDTO preco2 = fornecedor2.getPrecos().stream().findFirst().orElseThrow(getRuntimeExceptionSupplier(produto));

                    return preco1.getPreco().compareTo(preco2.getPreco());
                }).orElseThrow(getRuntimeExceptionSupplier(produto));

        salvaFornecedorCasoNaoExista(fornecedorDTO);

        return fornecedorDTO;
    }


    public PrecoFornecedorDTO getMelhorPrecoByFornecedor(ProdutoDTO produto, FornecedorDTO melhorFornecedor) {
        melhorFornecedor.getPrecos().removeIf(p -> p.getQuantidadeMinima() > produto.getQuantidade());
        return melhorFornecedor.getPrecos().stream().min(Comparator.comparing(PrecoFornecedorDTO::getPreco)).orElseThrow(getRuntimeExceptionSupplier(produto));
    }

    Fornecedor getFornecedor(String cnpj) {
        return fornecedorRepository.findById(cnpj).orElse(new Fornecedor());
    }

    private Supplier<RuntimeException> getRuntimeExceptionSupplier(ProdutoDTO produto) {
        return () -> new BusinessException(String.format("Nenhum fornecedor encontrado para a quantidade solicitada do produto %s", produto.getNome()));
    }

    private void salvaFornecedorCasoNaoExista(FornecedorDTO fornecedorDTO) {
        Optional<Fornecedor> optionalFornecedor = fornecedorRepository.findById(fornecedorDTO.getCnpj());

        if (!optionalFornecedor.isPresent()) {
            Fornecedor fornecedor = Fornecedor.builder()
                    .cnpj(fornecedorDTO.getCnpj())
                    .nome(fornecedorDTO.getNome())
                    .build();
            List<PrecoFornecedor> precos = fornecedorDTO.getPrecos().stream().map(preco -> PrecoFornecedor.builder()
                    .preco(preco.getPreco())
                    .quantidadeMinima(preco.getQuantidadeMinima())
                    .build()
            ).collect(Collectors.toList());
            fornecedor.setPrecos(precos);

            fornecedorRepository.save(fornecedor);
        }
    }
}
