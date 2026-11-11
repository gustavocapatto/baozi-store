package com.baozistore.controller;

import com.baozistore.dto.PedidoRequest;
import com.baozistore.model.Cliente;
import com.baozistore.model.Pedido;
import com.baozistore.model.Produto;
import com.baozistore.repository.ClienteRepository;
import com.baozistore.repository.PedidoRepository;
import com.baozistore.repository.ProdutoRepository;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;

    public PedidoController(PedidoRepository pedidoRepository,
                           ClienteRepository clienteRepository,
                           ProdutoRepository produtoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
    }

    @PostMapping
    public ResponseEntity<?> criar(@Valid @RequestBody PedidoRequest request) {
        var cliente = clienteRepository.findById(request.clienteId());
        var produto = produtoRepository.findById(request.produtoId());

        if (cliente.isEmpty() || produto.isEmpty()) {
            return ResponseEntity.badRequest().body("Cliente ou produto não encontrado.");
        }

        Pedido pedido = new Pedido(cliente.get(), produto.get(), request.quantidade());
        Pedido salvo = pedidoRepository.save(pedido);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @GetMapping
    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> buscarPorId(@PathVariable Long id) {
        return pedidoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @Valid @RequestBody PedidoRequest request) {
        return pedidoRepository.findById(id)
                .map(pedidoExistente -> {
                    Cliente cliente = clienteRepository.findById(request.clienteId())
                            .orElse(null);
                    Produto produto = produtoRepository.findById(request.produtoId())
                            .orElse(null);

                    if (cliente == null || produto == null) {
                        return ResponseEntity.badRequest().body("Cliente ou produto não encontrado.");
                    }

                    pedidoExistente.setCliente(cliente);
                    pedidoExistente.setProduto(produto);
                    pedidoExistente.setQuantidade(request.quantidade());
                    return ResponseEntity.ok(pedidoRepository.save(pedidoExistente));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (!pedidoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        pedidoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
