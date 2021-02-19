package com.luizalabs.clientesapi.repository;

import com.luizalabs.clientesapi.model.Client;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ClientRepository extends PagingAndSortingRepository<Client, Long> {
    Client findById(long id);

    @Query("select c from Client c where c.email = ?1")
    Client findByEmail(String email);
}
