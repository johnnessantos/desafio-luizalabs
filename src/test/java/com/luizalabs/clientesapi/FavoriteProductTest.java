package com.luizalabs.clientesapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.luizalabs.clientesapi.model.Client;
import com.luizalabs.clientesapi.model.FavoriteProduct;
import com.luizalabs.clientesapi.repository.ClientRepository;
import com.luizalabs.clientesapi.repository.FavoriteProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Random;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
public class FavoriteProductTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private FavoriteProductRepository favoriteProductRepository;

    @Test
    public void getFavoriteProductsNotUserForbiddenTest() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/favorite-product"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void getFavoriteProductsWithUserOKTest() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/favorite-product")
                .with(user("admin").password("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    public void getFavoriteProductsWithUserForbiddenTest() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/favorite-product")
                .with(user("luizalabs").password("luizalabs").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void addFavoriteProductsNotContentTest() throws Exception {
        this.mockMvc.perform(put("/api/v1/favorite-product")
                .with(user("luizalabs").password("luizalabs").roles("USER")))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void addFavoriteProductsWithContentStatusCreatedTest() throws Exception {
        int randomInt = new Random().nextInt();
        Client client = new Client("client"+randomInt, "client"+randomInt+"@email.com");
        client = clientRepository.save(client);

        String product_id = "ec92cbdd-b7e6-e2c2-a0e8-70ae001cb3d8";
        String favoriteProductJson = "{\"client\": {\"id\": "+client.getId()+"},\"product_id\":\""+product_id+"\"}";

        this.mockMvc.perform(MockMvcRequestBuilders
                .put("/api/v1/favorite-product")
                .with(user("luizalabs").password("luizalabs").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(favoriteProductJson))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());

        favoriteProductRepository.delete(favoriteProductRepository.findByClientAndProdutoId(client.getId(), product_id));
        clientRepository.delete(client);
    }

    @Test
    void deleteClientStatusOk() throws Exception {
        int randomInt = new Random().nextInt();
        Client client = new Client("client"+randomInt, "client"+randomInt+"@email.com");

        client = clientRepository.save(client);
        String product_id = "ec92cbdd-b7e6-e2c2-a0e8-70ae001cb3d8";

        favoriteProductRepository.save(new FavoriteProduct(client, product_id));

        this.mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/v1/favorite-product/{client_id}/{product_id}", client.getId(), product_id)
                .with(user("luizalabs").password("luizalabs").roles("USER")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").doesNotExist());
    }
}
