package br.ufsm.poli.csi.tapw.pilacoin.server.colherdecha;

import br.ufsm.poli.csi.tapw.pilacoin.server.model.Usuario;
import br.ufsm.poli.csi.tapw.pilacoin.server.repositories.UsuarioRepository;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import javax.transaction.Transactional;
import java.io.*;
import java.security.*;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class RegistraUsuarioService {

    @Value("${endereco.server}")
    private String enderecoServer;
    static PublicKey CHAVE_PUBLICA;
    static PrivateKey CHAVE_PRIVADA;
    static PublicKey CHAVE_PUBLICA_RAFAEL;

    @Autowired
    private UsuarioRepository usuarioRepostory;

    @PostConstruct
    public void init() {
        System.out.println("Registrado usuário: " + registraUsuario("angeo =)"));
    }

    @SneakyThrows
    @Transactional
    public UsuarioRest registraUsuario(String nome) {
        initPubKey();
        KeyPair keyPair = leKeyPair();
        UsuarioRest usuarioRest = UsuarioRest.builder().nome(nome).chavePublica(keyPair.getPublic().getEncoded()).build();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        //headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<UsuarioRest> entity = new HttpEntity<>(usuarioRest, headers);
        RestTemplate restTemplate = new RestTemplate();
        try {
            System.out.println();
            ResponseEntity<UsuarioRest> resp = restTemplate.postForEntity("http://" + enderecoServer + "/usuario/", entity, UsuarioRest.class);
            UsuarioRest usuario = resp.getBody();
            Usuario usuarioBD = new Usuario();
            usuarioBD.setNome(usuarioRest.nome);
            usuarioBD.setChavePublica(keyPair.getPublic().getEncoded());
            usuarioBD.setChavePrivada(keyPair.getPrivate().getEncoded());
            usuarioRepostory.save(usuarioBD);
            return usuario;
        } catch (Exception e) {
            System.out.println(e);
            System.out.println("usuario já cadastrado.");
            String strPubKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
            ResponseEntity<UsuarioRest> resp = restTemplate.postForEntity("http://" + enderecoServer + "/usuario/findByChave", new HttpEntity<>(strPubKey, headers), UsuarioRest.class);
            return resp.getBody();
        }
    }

    @SneakyThrows
    private void initPubKey(){
      //  File rafaelPub = new File("C:\\Users\\aluno\\Downloads\\master-pub.key");
      //  FileInputStream pubIn = new FileInputStream(rafaelPub);
       // byte[] barrPub = new byte[(int) pubIn.getChannel().size()];
      //  pubIn.read(barrPub);
     //   PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(barrPub));
      //  CHAVE_PUBLICA_RAFAEL = publicKey;
    }

    @SneakyThrows
    private KeyPair leKeyPair() {
        File fpub = new File("pub.key");
        File fpriv = new File("priv.key");
        if (fpub.exists() && fpriv.exists()) {
            FileInputStream pubIn = new FileInputStream(fpub);
            FileInputStream privIn = new FileInputStream(fpriv);
            byte[] barrPub = new byte[(int) pubIn.getChannel().size()];
            byte[] barrPriv = new byte[(int) privIn.getChannel().size()];
            pubIn.read(barrPub);
            privIn.read(barrPriv);
            PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(barrPub));
            PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(barrPriv));
            CHAVE_PUBLICA = publicKey;
            CHAVE_PRIVADA = privateKey;
            return new KeyPair(publicKey, privateKey);
        } else {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            KeyPair keyPair = kpg.generateKeyPair();
            FileOutputStream pubOut = new FileOutputStream("pub.key", false);
            FileOutputStream privOut = new FileOutputStream("priv.key", false);
            pubOut.write(keyPair.getPublic().getEncoded());
            privOut.write(keyPair.getPrivate().getEncoded());
            pubOut.close();
            privOut.close();
            return keyPair;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class UsuarioRest {
        private Long id;
        private byte[] chavePublica;
        private String nome;
    }

}
