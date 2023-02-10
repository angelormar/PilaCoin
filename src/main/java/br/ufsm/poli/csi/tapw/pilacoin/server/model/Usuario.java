package br.ufsm.poli.csi.tapw.pilacoin.server.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonPropertyOrder(alphabetic = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Usuario {

    @Id
    @GeneratedValue
    private Long id;
    private byte[] chave_publica;
    private byte[] chave_privada;
    private String nome;
    private String senha;
    private transient String token;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getChave_publica() {
        return chave_publica;
    }

    public void setChave_publica(byte[] chavePublica) {
        this.chave_publica = chavePublica;
    }

    public byte[] getChave_privada() {
        return chave_privada;
    }

    public void setChave_privada(byte[] chavePrivada) {
        this.chave_privada = chavePrivada;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

}
