package com.baozistore;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class BaoziStoreApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void fluxoBasicoDaLoja() throws Exception {
        String clienteJson = "{\"nome\":\"Luciane123456\",\"clienteDesde\":\"2026-01-15\"}";
        MvcResult clienteResult = mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(clienteJson))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode clienteNode = objectMapper.readTree(clienteResult.getResponse().getContentAsString());
        long clienteId = clienteNode.get("id").asLong();

        String produtoJson = "{\"nome\":\"Paozinho Chines\",\"preco\":12.50,\"estoque\":true}";
        MvcResult produtoResult = mockMvc.perform(post("/produtos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(produtoJson))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode produtoNode = objectMapper.readTree(produtoResult.getResponse().getContentAsString());
        long produtoId = produtoNode.get("id").asLong();

        String pedidoJson = "{\"clienteId\":" + clienteId + ",\"produtoId\":" + produtoId + ",\"quantidade\":5}";
        mockMvc.perform(post("/pedidos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(pedidoJson))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/clientes")).andExpect(status().isOk());
        mockMvc.perform(get("/produtos")).andExpect(status().isOk());
        mockMvc.perform(get("/pedidos")).andExpect(status().isOk());
    }
}
