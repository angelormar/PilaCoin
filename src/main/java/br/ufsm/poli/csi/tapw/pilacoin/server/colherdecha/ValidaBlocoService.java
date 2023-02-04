package br.ufsm.poli.csi.tapw.pilacoin.server.colherdecha;

import br.ufsm.poli.csi.tapw.pilacoin.model.PilaCoinAmigo;
import br.ufsm.poli.csi.tapw.pilacoin.model.Bloco;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ValidaBlocoService {

    @Value("${endereco.server}")
    private String enderecoServer;

    //@PostConstruct
    @SneakyThrows
    public Bloco validaBloco(Bloco bloco) {
        System.out.println(enderecoServer);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        //headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<Bloco> entity = new HttpEntity<>(bloco, headers);
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<Bloco> resp = restTemplate.postForEntity("http://srv-ceesp.proj.ufsm.br:8097/bloco/", entity, Bloco.class);
            return resp.getBody();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return bloco;
    }

    @SneakyThrows
    public String validaBlocoAmigo(PilaCoinAmigo bloco) {
        System.out.println(enderecoServer);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        //headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<PilaCoinAmigo> entity = new HttpEntity<>(bloco, headers);
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> resp = restTemplate.postForEntity("http://srv-ceesp.proj.ufsm.br:8097/bloco/validaBlocoOutroUsuario", entity, String.class);
            if(resp.getBody().equals("\"ok\"")){
                System.out.println("Bloco amigo validado");
            }
            return resp.getBody();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return "Erro bloco amigo";
    }

}
