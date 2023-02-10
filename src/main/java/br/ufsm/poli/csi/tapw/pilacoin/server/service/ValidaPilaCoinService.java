package br.ufsm.poli.csi.tapw.pilacoin.server.service;

import br.ufsm.poli.csi.tapw.pilacoin.model.PilaCoin;
import br.ufsm.poli.csi.tapw.pilacoin.model.PilaCoinAmigo;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.Base64;

@Service
public class ValidaPilaCoinService {

    @Value("${endereco.server}")
    private String enderecoServer;

    public ValidaPilaCoinService() {
    }

    //@PostConstruct
    @SneakyThrows
    public PilaCoin validaPilaCoin(PilaCoin pila) {
        System.out.println(enderecoServer);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        //headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<PilaCoin> entity = new HttpEntity<>(pila, headers);
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<PilaCoin> resp = restTemplate.postForEntity("http://srv-ceesp.proj.ufsm.br:8097/pilacoin/", entity, PilaCoin.class);
            return resp.getBody();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return pila;
    }

    @SneakyThrows
    public String validaPilaCoinAmigo(PilaCoinAmigo pila) {
        // System.out.println("enderecoServer do amigo");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        //headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<PilaCoinAmigo> entity = new HttpEntity<>(pila, headers);
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> resp = restTemplate.postForEntity("http://srv-ceesp.proj.ufsm.br:8097/pilacoin/validaPilaOutroUsuario", entity, String.class);
            if (resp.getBody().equals("\"ok\"")) {
                System.out.println("Pila amigo validado");
            }
            System.out.println(resp.getBody());
            return resp.getBody();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("erro do amigo");
        }
        return "erro do amigo";
    }
}
