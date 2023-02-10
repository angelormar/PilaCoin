package br.ufsm.poli.csi.tapw.pilacoin.server.wsService;

import br.ufsm.poli.csi.tapw.pilacoin.model.Bloco;
import br.ufsm.poli.csi.tapw.pilacoin.model.PilaCoinAmigo;
import br.ufsm.poli.csi.tapw.pilacoin.server.service.RegistraUsuarioService;
import br.ufsm.poli.csi.tapw.pilacoin.server.service.ValidaBlocoService;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.util.Objects;

@Service
public class WebSocketClientBloco {

    private MyStompSessionHandler sessionHandler = new MyStompSessionHandler();
    @Value("${endereco.server}")
    private String enderecoServer;
    static int numTent = 0;
    BigInteger numHash;
    BigInteger numHashAmigo;

    static boolean onMineraBloco = false;
    static boolean onValidaBloco = false;

    public static void setOnMinerBloco(boolean onMinerBloco) {
        WebSocketClientBloco.onMineraBloco = onMinerBloco;
    }

    public static void setOnValidaBloco(boolean onValidaBloco) {
        WebSocketClientBloco.onValidaBloco = onValidaBloco;
    }

    @PostConstruct
    private void init() throws IOException {
        System.out.println("iniciou");
        StandardWebSocketClient client = new StandardWebSocketClient();
        WebSocketStompClient stompClient = new WebSocketStompClient(client);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        stompClient.connect("ws://" + enderecoServer + "websocket/websocket", sessionHandler);
        System.out.println("conectou wsBloco");


    }


    @SneakyThrows
    @Scheduled(fixedRate = 1)
    private void descobreBloco() {
        while (onMineraBloco) {
            if (sessionHandler.bloco != null && sessionHandler.dificuldade != null) {
                SecureRandom rnd = new SecureRandom();
                Bloco bloco = Bloco.builder()
                        .chaveUsuarioMinerador(RegistraUsuarioService.getChavePublica().getEncoded())
                        .nonce(new BigInteger(128, rnd).toString())
                        .nonceBlocoAnterior(sessionHandler.bloco.getNonceBlocoAnterior())
                        .numeroBloco(sessionHandler.bloco.getNumeroBloco())
                        .transacoes(sessionHandler.bloco.getTransacoes()).build();
                //.id(null).build();
                ObjectMapper om = new ObjectMapper();
                om.setSerializationInclusion(JsonInclude.Include.NON_NULL);
                String blocojson = om.writeValueAsString(bloco);
                //System.out.println(blocojson);
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                byte[] hash = md.digest(blocojson.getBytes("UTF-8"));
                numHash = new BigInteger(hash).abs();
                if (numHash.compareTo(sessionHandler.dificuldade) < 0) {
                    // System.out.println(blocojson);
                    System.out.println("minerastes bloco");
                    Bloco blocoValidado = new ValidaBlocoService().validaBloco(bloco);
                } else {
                    if (numTent % 1000 == 0) {
                        System.out.println(numTent + " tentativas de minerar bloco");
                    }
                    numTent++;
                }
            }
        }

    }

    @SneakyThrows
    @Scheduled(fixedRate = 1)
    private void descobreBlocoAmigo() {
        while (onValidaBloco) {
            if (sessionHandler.bloco != null && sessionHandler.dificuldade != null) {
                Bloco bloco = sessionHandler.bloco;
                //Gera o hash para hashPilaBloco
                ObjectMapper om = new ObjectMapper();
                om.setSerializationInclusion(JsonInclude.Include.NON_NULL);
                String blocojson = om.writeValueAsString(bloco);
                //System.out.println(blocojson);

                MessageDigest md = MessageDigest.getInstance("SHA-256");
                byte[] hash = md.digest(blocojson.getBytes("UTF-8"));
                numHashAmigo = new BigInteger(hash).abs();

                if (numHashAmigo.compareTo(sessionHandler.dificuldade) < 0) {
                    PilaCoinAmigo blocoAmigo = PilaCoinAmigo.builder()
                            .assinatura(null)
                            .chavePublica(RegistraUsuarioService.getChavePublica().getEncoded())
                            .hashPilaBloco(hash)
                            .nonce(bloco.getNonce())
                            .tipo("BLOCO").build();

                    //Gera o hash para assinatura
                    ObjectMapper omAmigo = new ObjectMapper();
                    omAmigo.setSerializationInclusion(JsonInclude.Include.NON_NULL);
                    String blocojsonAmigo = omAmigo.writeValueAsString(blocoAmigo);
                    MessageDigest mdAmigo = MessageDigest.getInstance("SHA-256");
                    byte[] hashAmigo = mdAmigo.digest(blocojsonAmigo.getBytes("UTF-8"));

                    //criptografa hash com minha chave privada
                    Cipher cipherAES = Cipher.getInstance("RSA");
                    PrivateKey chavePrivada = RegistraUsuarioService.getChavePrivada();
                    cipherAES.init(Cipher.ENCRYPT_MODE, chavePrivada);
                    byte[] hashAmigoCifrado = cipherAES.doFinal(hashAmigo);

                    //Seta a assinatura
                    blocoAmigo = PilaCoinAmigo.builder()
                            .assinatura(hashAmigoCifrado)
                            .chavePublica(RegistraUsuarioService.getChavePublica().getEncoded())
                            .hashPilaBloco(hash)
                            .nonce(bloco.getNonce())
                            .tipo("BLOCO").build();

                    new ValidaBlocoService().validaBlocoAmigo(blocoAmigo);

                } else {
                    if (numTent % 1000 == 0) {
                        System.out.println(numTent + " tetativas bloco amigo");
                    }
                    numTent++;
                }
            }
        }
    }

    private static class MyStompSessionHandler implements StompSessionHandler {

        private BigInteger dificuldade;
        private Bloco bloco;
        // private Bloco blocoAmigo;

        @Override
        public void afterConnected(StompSession stompSession,
                                   StompHeaders stompHeaders) {
            stompSession.subscribe("/topic/dificuldade", this);
            stompSession.subscribe("/topic/descobrirNovoBloco", this);
            stompSession.subscribe("/topic/validaBloco", this);
        }

        @Override
        public void handleException(StompSession stompSession, StompCommand stompCommand, StompHeaders stompHeaders, byte[] bytes, Throwable throwable) {
            System.out.println(throwable.getMessage());
        }

        @Override
        public void handleTransportError(StompSession stompSession, Throwable throwable) {
            System.out.println(throwable.getMessage());
        }

        @Override
        public Type getPayloadType(StompHeaders stompHeaders) {
            //System.out.println(stompHeaders);
            if (Objects.equals(stompHeaders.getDestination(), "/topic/descobrirNovoBloco")) {
                return Bloco.class;
            } else if (Objects.equals(stompHeaders.getDestination(), "/topic/validaBloco")) {
                return Bloco.class;
            } else if (Objects.equals(stompHeaders.getDestination(), "/topic/dificuldade")) {
                return WebSocketClientPila.DificuldadeRet.class;
            }
            return null;
        }

        @Override
        public void handleFrame(StompHeaders stompHeaders, Object o) {
            //System.out.println("Received : " + o);
            if (o.getClass().equals(Bloco.class)) {
                assert o != null;
                bloco = Bloco.class.cast(o);
            }
            if (o.getClass().equals(WebSocketClientPila.DificuldadeRet.class)) {
                assert o != null;
                dificuldade = new BigInteger(((WebSocketClientPila.DificuldadeRet) o).getDificuldade(), 16);
            }
        }
    }

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DificuldadeRet {
        private String dificuldade;
    }

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DescobreBloco {
        private String bloco;
    }

}
