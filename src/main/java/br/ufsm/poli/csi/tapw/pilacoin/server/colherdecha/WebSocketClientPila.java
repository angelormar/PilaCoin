package br.ufsm.poli.csi.tapw.pilacoin.server.colherdecha;

import br.ufsm.poli.csi.tapw.pilacoin.model.PilaCoin;
import br.ufsm.poli.csi.tapw.pilacoin.model.PilaCoinAmigo;
import br.ufsm.poli.csi.tapw.pilacoin.model.Bloco;
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
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Objects;

@Service
public class WebSocketClientPila {

    private MyStompSessionHandler sessionHandler = new MyStompSessionHandler();
    @Value("${endereco.server}")
    private String enderecoServer;
    static int numTent = 0;
    BigInteger numHash;
    BigInteger numHashAmigo;
    BigInteger dificuldadeAntiga = new BigInteger("100");

    static boolean onMinerPila;

    public WebSocketClientPila() {
    }

    @PostConstruct
    private void init() {
        System.out.println("iniciou");
        StandardWebSocketClient client = new StandardWebSocketClient();
        WebSocketStompClient stompClient = new WebSocketStompClient(client);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        stompClient.connect("ws://" + enderecoServer + "websocket/websocket" , sessionHandler);
        System.out.println("conectou");

    }

    @SneakyThrows
    @Scheduled(fixedRate = 1)
    private void mineraPilacoin() {
                while (true){
                    if (sessionHandler.dificuldade != null) {
                        SecureRandom rnd = new SecureRandom();
                        PilaCoin pilaCoin = PilaCoin.builder()
                                //.assinaturaMaster(null)//RegistraUsuarioService.CHAVE_PUBLICA_RAFAEL.getEncoded())
                                .dataCriacao(new Date())
                                .chaveCriador(RegistraUsuarioService.CHAVE_PUBLICA.getEncoded())
                                .nonce(new BigInteger(128, rnd).toString()).build();
                        //.id(null).build();
                        ObjectMapper om = new ObjectMapper();
                        om.setSerializationInclusion(JsonInclude.Include.NON_NULL);
                        String pilajson = om.writeValueAsString(pilaCoin);
                        //System.out.println(pilajson);
                        MessageDigest md = MessageDigest.getInstance("SHA-256");
                        byte[] hash = md.digest(pilajson.getBytes("UTF-8"));
                        numHash = new BigInteger(hash).abs();
                        if (numHash.compareTo(sessionHandler.dificuldade) < 0) {
                           // System.out.println(pilajson);
                            System.out.println("minerastes");
                            PilaCoin pilaCoinValidado = new ValidaPilaCoinService().validaPilaCoin(pilaCoin);

                        } else {
                            if(numTent%100000==0){

                                System.out.println(numTent);
                            }
                            // System.out.println("nao minerastes");

                            numTent++;
                        }

                    }


        }
    }

    @SneakyThrows
    @Scheduled(fixedRate = 1)
    private void validaPilaAmigo() {
        if (sessionHandler.pilaCoinAmigo != null && sessionHandler.dificuldade != null) {
            PilaCoin pilaCoin = sessionHandler.pilaCoinAmigo;
            //Gera o hash para hashPilaBloco
            ObjectMapper om = new ObjectMapper();
            om.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            String pilajson = om.writeValueAsString(pilaCoin);
            //System.out.println(pilajson);

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(pilajson.getBytes("UTF-8"));
            numHashAmigo = new BigInteger(hash).abs();

            if (numHashAmigo.compareTo(sessionHandler.dificuldade) < 0) {
                PilaCoinAmigo pilaCoinAmigo = PilaCoinAmigo.builder()
                        .assinatura(null)
                        .chavePublica(RegistraUsuarioService.CHAVE_PUBLICA.getEncoded())
                        .hashPilaBloco(hash)
                        .nonce(pilaCoin.getNonce())
                        .tipo("PILA").build();

                //Gera o hash para assinatura
                ObjectMapper omAmigo = new ObjectMapper();
                omAmigo.setSerializationInclusion(JsonInclude.Include.NON_NULL);
                String pilajsonAmigo = omAmigo.writeValueAsString(pilaCoinAmigo);
                MessageDigest mdAmigo = MessageDigest.getInstance("SHA-256");
                byte[] hashAmigo = mdAmigo.digest(pilajsonAmigo.getBytes("UTF-8"));

                //criptografa hash com minha chave privada
                Cipher cipherAES = Cipher.getInstance("RSA");
                PrivateKey chavePrivada = RegistraUsuarioService.CHAVE_PRIVADA;
                cipherAES.init(Cipher.ENCRYPT_MODE, chavePrivada);
                byte[] hashAmigoCifrado = cipherAES.doFinal(hashAmigo);

                //Seta a assinatura
                pilaCoinAmigo = PilaCoinAmigo.builder()
                        .assinatura(hashAmigoCifrado)
                        .chavePublica(RegistraUsuarioService.CHAVE_PUBLICA.getEncoded())
                        .hashPilaBloco(hash)
                        .nonce(pilaCoin.getNonce())
                        .tipo("PILA").build();



                new ValidaPilaCoinService().validaPilaCoinAmigo(pilaCoinAmigo);

            } else {
                if(numTent%100000==0){
                    System.out.println(numTent + " amigo");
                }
                numTent++;
            }

        }
    }

    @Scheduled(fixedRate = 800)
    private void printWs() {
        if (sessionHandler.dificuldade != null) {
            //if ( dificuldadeAntiga.compareTo(sessionHandler.dificuldade) != 0 ){
            //    System.out.println("Dificuldade Atual: " + sessionHandler.dificuldade);
                dificuldadeAntiga = sessionHandler.dificuldade; 
           // }
        }
        if (sessionHandler.pilaCoinAmigo != null) {
        //    System.out.println("Pilacoin amigo Atual: " + sessionHandler.pilaCoinAmigo);
        }
    }

    private static class MyStompSessionHandler implements StompSessionHandler {

        private BigInteger dificuldade;
        private PilaCoin pilaCoinAmigo;
        private Bloco bloco;
       // private Bloco blocoAmigo;

        @Override
        public void afterConnected(StompSession stompSession,
                                   StompHeaders stompHeaders)
        {
            stompSession.subscribe("/topic/descobrirNovoBloco", this);
            stompSession.subscribe("/topic/dificuldade", this);
            stompSession.subscribe("/topic/validaMineracao", this);
            //stompSession.subscribe("/topic/validaBloco", this);
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
            if (Objects.equals(stompHeaders.getDestination(), "/topic/dificuldade")) {
                return DificuldadeRet.class;
            }
            //System.out.println(stompHeaders);
            else if (Objects.equals(stompHeaders.getDestination(), "/topic/validaMineracao")) {
                return PilaCoin.class;
            }
            return null;
        }

        @Override
        public void handleFrame(StompHeaders stompHeaders, Object o) {
                 //System.out.println("Received : " + o);
            if (o.getClass().equals(DificuldadeRet.class)){
                assert o != null;
                dificuldade = new BigInteger(((DificuldadeRet) o).getDificuldade(), 16);
            } if (o.getClass().equals(PilaCoin.class)){
                pilaCoinAmigo = PilaCoin.class.cast(o);
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
    public static class ValidaMineracao {
        private String validaMineracao;
    }

}
