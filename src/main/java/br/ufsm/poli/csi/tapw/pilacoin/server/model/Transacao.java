package br.ufsm.poli.csi.tapw.pilacoin.server.model;


import lombok.*;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonPropertyOrder(alphabetic = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Transacao {

    private byte[] assinatura ;
    private byte[] chaveUsuarioDestino ;
    private byte[] chaveUsuarioOrigem ;
    private Date dataTransacao ;
    @Id
    @GeneratedValue
    private Integer id   ;
    private Integer idBloco  ;
    private String noncePila ;
    private String status ;

    public byte[] getAssinatura() {
        return assinatura;
    }

    public void setAssinatura(byte[] assinatura) {
        this.assinatura = assinatura;
    }

    public byte[] getChaveUsuarioDestino() {
        return chaveUsuarioDestino;
    }

    public void setChaveUsuarioDestino(byte[] chaveUsuarioDestino) {
        this.chaveUsuarioDestino = chaveUsuarioDestino;
    }

    public byte[] getChaveUsuarioOrigem() {
        return chaveUsuarioOrigem;
    }

    public void setChaveUsuarioOrigem(byte[] chaveUsuarioOrigem) {
        this.chaveUsuarioOrigem = chaveUsuarioOrigem;
    }

    public Date getDataTransacao() {
        return dataTransacao;
    }

    public void setDataTransacao(Date dataTransacao) {
        this.dataTransacao = dataTransacao;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdBloco() {
        return idBloco;
    }

    public void setIdBloco(Integer idBloco) {
        this.idBloco = idBloco;
    }

    public String getNoncePila() {
        return noncePila;
    }

    public void setNoncePila(String noncePila) {
        this.noncePila = noncePila;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
