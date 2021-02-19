package com.luizalabs.clientesapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.luizalabs.clientesapi.model.Client;
import com.luizalabs.clientesapi.repository.ClientRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
public class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClientRepository clientRepository;

    @Test
    public void getClientsNotUserForbiddenTest() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/client"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void getClientsWithUserOKTest() throws Exception {
        this.mockMvc.perform(get("/api/v1/client")
                .with(user("admin").password("admin").roles("ADMIN")))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void getClientsWithUserForbiddenTest() throws Exception {
        this.mockMvc.perform(get("/api/v1/client")
                .with(user("luizalabs").password("luizalabs").roles("USER")))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void addClientNotContentTest() throws Exception {
        this.mockMvc.perform(put("/api/v1/client")
                .with(user("luizalabs").password("luizalabs").roles("USER")))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void addClientContentStatusCreatedTest() throws Exception {
        int randomInt = new Random().nextInt();
        Client client = new Client("cliente"+randomInt, "cliente"+randomInt+"@email.com");
        this.mockMvc.perform(MockMvcRequestBuilders
                .put("/api/v1/client")
                .with(user("luizalabs").password("luizalabs").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(client)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());
    }

    @Test
    void updateClientStatusOkTest() throws Exception {
        int randomInt = new Random().nextInt();
        Client client = new Client("client"+randomInt, "client"+randomInt+"@email.com");
        clientRepository.save(client);

        String email = "cliente"+randomInt*10+"@email.com";
        client.setEmail(email);

        this.mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/client")
                .with(user("luizalabs").password("luizalabs").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(client)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(email));
    }

    @Test
    void deleteClientStatusOk() throws Exception {
        int randomInt = new Random().nextInt();
        Client client = new Client("client"+randomInt, "client"+randomInt+"@email.com");

        client = clientRepository.save(client);

        this.mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/v1/client/{id}", client.getId())
                .with(user("luizalabs").password("luizalabs").roles("USER")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").doesNotExist());
    }
}
