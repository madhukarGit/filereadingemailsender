package com.chadmockitoracle.chadmockito.service;

import com.chadmockitoracle.chadmockito.domain.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class AccountServiceImpl {

    @Autowired
    private RestTemplate restTemplate;

    public Account fetchAccountDetails() {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth("eazybytes", "12345");


        Account account = new Account();
        account.setUserName("MadMax123");
        account.setBalance("100000.00");
        account.setFilter(Arrays.asList("ENODEB", "GNODEB", "TNODEB"));
        HttpEntity<Account> entity = new HttpEntity<>(account, headers);

        ResponseEntity<Account> response = null;
        try {
            response =
                    restTemplate.exchange("http://localhost:9212/bank",
                            HttpMethod.POST,entity, Account.class);
            System.out.println("resposne ----"+ response);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return response.getBody();
    }

    public ResponseEntity<String> testAccountController(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth("eazybytes", "12345");
        HttpEntity<String> entity = new HttpEntity<>( headers);

        return restTemplate.exchange("http://localhost:9212/bank", HttpMethod.GET,entity,String.class);
    }
}
