package br.ufsm.poli.csi.tapw.pilacoin.server.controller;

import br.ufsm.poli.csi.tapw.pilacoin.model.PilaCoin;
import br.ufsm.poli.csi.tapw.pilacoin.server.service.RegistraUsuarioService;
import br.ufsm.poli.csi.tapw.pilacoin.server.wsService.WebSocketClientPila;
import br.ufsm.poli.csi.tapw.pilacoin.server.wsService.WebSocketClientBloco;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.web.client.RestTemplate;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/pilaBloco")
public class MineracaoController {
    @PostMapping("/pila")
    public ResponseEntity<String> mineraPila(@RequestBody Boolean requestBody) {
        System.out.println(requestBody);
        WebSocketClientPila.setOnMinerPila(requestBody);
        // code to handle POST request
        return new ResponseEntity<>("Response Body", HttpStatus.OK);
    }

    @PostMapping("/bloco")
    public ResponseEntity<String> mineraBloco(@RequestBody Boolean requestBody) {
        System.out.println(requestBody);
        WebSocketClientBloco.setOnMinerBloco(requestBody);
        // code to handle POST request
        return new ResponseEntity<>("Response Body", HttpStatus.OK);
    }

    @PostMapping("/validapila")
    public ResponseEntity<String> validaPila(@RequestBody Boolean requestBody) {
        System.out.println(requestBody);
        WebSocketClientPila.setOnValidaPila(requestBody);
        // code to handle POST request
        return new ResponseEntity<>("Response Body", HttpStatus.OK);
    }

    @PostMapping("/validabloco")
    public ResponseEntity<String> validaBloco(@RequestBody Boolean requestBody) {
        System.out.println(requestBody);
        WebSocketClientBloco.setOnValidaBloco(requestBody);
        // code to handle POST request
        return new ResponseEntity<>("Response Body", HttpStatus.OK);
    }

    @PostMapping("/transacao")
    public ResponseEntity<String> solicitaTransacao(@RequestBody Boolean requestBody) {
        System.out.println(requestBody);
        // code to handle POST request
        return new ResponseEntity<>("Response Body", HttpStatus.OK);
    }

    @GetMapping("/carteira")
    public ResponseEntity<List<PilaCoin>> getCarteiraPila() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        HttpEntity<String> entity = new HttpEntity<String>(headers);

        ResponseEntity<PilaCoin[]> response = restTemplate.exchange(
                "http://srv-ceesp.proj.ufsm.br:8097/pilacoin/all", HttpMethod.GET, entity, PilaCoin[].class);

        // List<PilaCoin> pilas = (List<PilaCoin>) response;
        System.out.println(response.getBody());
        int count = 0;
        List<PilaCoin> pilasUsuario = new ArrayList<>();
        for (PilaCoin pila : response.getBody()) {
            if (Arrays.equals(pila.getChaveCriador(), RegistraUsuarioService.getChavePublica().getEncoded())) {
                pilasUsuario.add(pila);
            }
        }
        // code to handle POST request
        return new ResponseEntity<>(pilasUsuario, HttpStatus.OK);

    }

}
