package br.ufsm.poli.csi.tapw.pilacoin.model;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.List;

import br.ufsm.poli.csi.tapw.pilacoin.server.model.Transacao;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonPropertyOrder(alphabetic = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Bloco implements Serializable {

     @Id
     @GeneratedValue
    private Long id;
    // private Long nonce;
    // private byte[] hashBlocoAnterior;
    // @OneToMany(mappedBy = "bloco")
    // private List<Transacao> transacoes;

   private byte[] chaveUsuarioMinerador;
   private String nonce;
   private String nonceBlocoAnterior;
   private Integer numeroBloco;
   @ElementCollection
   private List<Integer> transacoes;

   public byte[] getChaveUsuarioMinerador() {
      return chaveUsuarioMinerador;
   }

   public void setChaveUsuarioMinerador(byte[] chaveUsuarioMinerador) {
      this.chaveUsuarioMinerador = chaveUsuarioMinerador;
   }

   public String getNonce() {
      return nonce;
   }

   public void setNonce(String nonce) {
      this.nonce = nonce;
   }

   public String getNonceBlocoAnterior() {
      return nonceBlocoAnterior;
   }

   public void setNonceBlocoAnterior(String nonceBlocoAnterior) {
      this.nonceBlocoAnterior = nonceBlocoAnterior;
   }

   public Integer getNumeroBloco() {
      return numeroBloco;
   }

   public void setNumeroBloco(Integer numeroBloco) {
      this.numeroBloco = numeroBloco;
   }

   public List<Integer> getTransacoes() {
      return transacoes;
   }

   public void setTransacoes(List<Integer> transacoes) {
      this.transacoes = transacoes;
   }
}
