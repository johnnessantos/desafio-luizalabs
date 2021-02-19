package com.luizalabs.clientesapi.externals.consumer;

import com.luizalabs.clientesapi.externals.model.Product;
import org.springframework.web.client.RestTemplate;

public class ProductConsumer {

    private RestTemplate restTemplate = new RestTemplate();

    private String URI = "http://challenge-api.luizalabs.com/api/product/";

    public String generateURL(String product_id) {
        return URI + product_id + "/";
    }

    public Product consomer(String product_id) {
        return restTemplate.getForObject(generateURL(product_id), Product.class);
    }
}
