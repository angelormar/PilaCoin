package br.ufsm.poli.csi.tapw.pilacoin.controller;

import br.ufsm.poli.csi.tapw.pilacoin.model.Bloco;
import br.ufsm.poli.csi.tapw.pilacoin.server.colherdecha.WebSocketClientPila;
import br.ufsm.poli.csi.tapw.pilacoin.server.colherdecha.WebSocketClientBloco;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MineracaoController {

    @PostMapping("/myRequest")
    public ResponseEntity<String> myRequestMethod(@RequestBody String requestBody) {
        System.out.println(requestBody);
        // code to handle POST request
        return new ResponseEntity<>("Response Body", HttpStatus.OK);
    }

}