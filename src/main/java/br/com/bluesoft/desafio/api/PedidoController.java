package br.com.bluesoft.desafio.api;

import br.com.bluesoft.desafio.model.dto.PedidoDTO;
import br.com.bluesoft.desafio.model.dto.ProdutoDTO;
import br.com.bluesoft.desafio.model.entity.Pedido;
import br.com.bluesoft.desafio.repository.PedidoRepository;
import br.com.bluesoft.desafio.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@Validated
public class PedidoController {

    private final PedidoService pedidoService;

    private final PedidoRepository pedidoRepository;

    @Autowired
    public PedidoController(PedidoService pedidoService, PedidoRepository pedidoRepository) {
        this.pedidoService = pedidoService;
        this.pedidoRepository = pedidoRepository;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<Pedido> findAll() {
        return pedidoRepository.findAll();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<PedidoDTO>> novoPedido(@RequestBody @NotEmpty List<ProdutoDTO> produtos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.novoPedido(produtos));
    }
}
