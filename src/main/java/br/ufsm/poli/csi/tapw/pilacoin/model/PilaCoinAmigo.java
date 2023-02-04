package br.ufsm.poli.csi.tapw.pilacoin.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Null;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.PublicKey;
import java.util.Date;

//@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonPropertyOrder(alphabetic = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PilaCoinAmigo implements Serializable {

    //@Id
    @GeneratedValue
   // private Long id;
    private byte[] assinatura;
    private byte[] chavePublica;
    private byte[] hashPilaBloco;
    private String nonce;
    private String tipo;


}