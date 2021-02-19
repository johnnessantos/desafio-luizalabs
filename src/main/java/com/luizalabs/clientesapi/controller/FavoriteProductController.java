package com.luizalabs.clientesapi.controller;

import com.luizalabs.clientesapi.externals.consumer.ProductConsumer;
import com.luizalabs.clientesapi.externals.model.Product;
import com.luizalabs.clientesapi.model.Client;
import com.luizalabs.clientesapi.model.FavoriteProduct;
import com.luizalabs.clientesapi.repository.ClientRepository;
import com.luizalabs.clientesapi.repository.FavoriteProductRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping(value="/api/v1/favorite-product")
@Api(value="Endpoint favorite-product")
public class FavoriteProductController {

    @Autowired
    FavoriteProductRepository favoriteProductRepository;

    @Autowired
    ClientRepository clientRepository;

    private ProductConsumer productConsumer = new ProductConsumer();

    @GetMapping
    @ApiOperation(value="Listar todos os produtos favoritados")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getFavoriteProducts(Pageable pageable) {
        return new ResponseEntity<>(favoriteProductRepository.findAll(pageable), HttpStatus.OK);
    }

    @GetMapping("/{client_id}")
    @ApiOperation(value="Listar produtos por cliente com filtro por id")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> getFavoriteProduct(@PathVariable long client_id, Pageable pageable) {

        Page<FavoriteProduct> favoriteProductPage = favoriteProductRepository.findByClientId(
                client_id, pageable);
        List<FavoriteProduct> favoriteProducts = favoriteProductPage.getContent();

        long TotalElements = favoriteProductPage.getTotalElements();

        List<Product> products = new ArrayList<>();
        for(int i = 0; i<favoriteProducts.size(); i++) {
            products.add(productConsumer.consomer(favoriteProducts.get(i).getProduct_id()));
        }

        return new ResponseEntity<>(new PageImpl<Product>(products, pageable, TotalElements),
                HttpStatus.OK);
    }

    @PutMapping
    @ApiOperation(value="Adicionando um produto aos favoritos de um cliente")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> addFavoriteProduct(@RequestBody FavoriteProduct favoriteProduct) {
        try {
            Client client = clientRepository.findById(favoriteProduct.getClient().getId());
            if(client == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cliente não encontrado.");
            }else {
                FavoriteProduct favoriteProductFind = favoriteProductRepository.findByClientAndProdutoId(
                        client.getId(), favoriteProduct.getProduct_id());

                if (favoriteProductFind == null) {
                    Product product = productConsumer.consomer(favoriteProduct.getProduct_id());
                    if (product.getId() == null) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Produto não encontrado.");
                    } else {
                        return new ResponseEntity<>(
                                favoriteProductRepository.save(favoriteProduct),
                                HttpStatus.CREATED);
                    }
                } else {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Produto já adicionado como favorito.");
                }
            }
        } catch (NullPointerException ex) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Informe o cliente.", ex);
        }
    }

    @DeleteMapping("/{client_id}/{product_id}")
    @ApiOperation(value="Removendo um produto favorito de um cliente pelo id")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> deleteFavoriteProduct(@PathVariable long client_id,
                                                   @PathVariable String product_id) {
        FavoriteProduct favoriteProduct = favoriteProductRepository.findByClientAndProdutoId(client_id, product_id);
        if(favoriteProduct != null) {
            favoriteProductRepository.delete(favoriteProduct);
            return ResponseEntity.ok().build();
        }else {
            return ResponseEntity.notFound().build();
        }
    }
}
