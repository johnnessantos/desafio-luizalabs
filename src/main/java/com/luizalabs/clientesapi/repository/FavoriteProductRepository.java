package com.luizalabs.clientesapi.repository;

import com.luizalabs.clientesapi.model.FavoriteProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;


public interface FavoriteProductRepository extends PagingAndSortingRepository<FavoriteProduct, Long> {
    FavoriteProduct findById(long id);

    @Query(value = "select * from favorite_product fp where fp.client_id = ?1 and fp.product_id = ?2",
            nativeQuery = true)
    FavoriteProduct findByClientAndProdutoId(long client_id, String product_id);

    @Query(value = "select fp.* from favorite_product fp where fp.client_id = ?1",
            countQuery = "SELECT count(*) FROM favorite_product fp where fp.client_id = ?1",
            nativeQuery = true)
    Page<FavoriteProduct> findByClientId(long cliente_id, Pageable pageable);
}

