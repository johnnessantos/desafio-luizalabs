package com.luizalabs.clientesapi.controller;

import com.luizalabs.clientesapi.model.Client;
import com.luizalabs.clientesapi.repository.ClientRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
@RequestMapping(value="/api/v1/client")
@Api(value="Endpoint client")
public class ClientController {

    @Autowired
    ClientRepository clientRepository;

    @GetMapping
    @ApiOperation(value="Obter lista de clientes")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getClients(Pageable pageble) {
        return new ResponseEntity<>(clientRepository.findAll(pageble), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @ApiOperation(value="Obter um cliente por id")
    public ResponseEntity<Client> getClient(@PathVariable long id) {
        return new ResponseEntity<>(clientRepository.findById(id), HttpStatus.OK);
    }

    @PutMapping
    @ApiOperation(value="Adicionar um cliente")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Client> addClient(@Valid @RequestBody Client client){
        Client client_new = clientRepository.findByEmail(client.getEmail());
        System.out.println(client.getName());
        if(client_new == null) {
            return new ResponseEntity<>(clientRepository.save(client), HttpStatus.CREATED);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Email já cadastrado.");
        }
    }

    @PostMapping
    @ApiOperation(value="Atualizar um cliente com todos seus campos")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> updateClient(@Valid @RequestBody Client client) {
        return new ResponseEntity<>(clientRepository.save(client), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value="Deletar um cliente por id")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> deleteClient(@PathVariable long id) {
        Client client = clientRepository.findById(id);
        if(client != null) {
            clientRepository.delete(client);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
