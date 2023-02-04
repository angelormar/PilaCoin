package br.ufsm.poli.csi.tapw.pilacoin.server.model;

import lombok.Builder;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Usuario {

    @Id
    @GeneratedValue
    private Long id;
    private byte[] chavePublica;
    private byte[] chavePrivada;
    private String nome;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getChavePublica() {
        return chavePublica;
    }

    public void setChavePublica(byte[] chavePublica) {
        this.chavePublica = chavePublica;
    }

    public byte[] getChavePrivada() {
        return chavePrivada;
    }

    public void setChavePrivada(byte[] chavePrivada) {
        this.chavePrivada = chavePrivada;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
