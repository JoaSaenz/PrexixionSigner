package com.joa.prexixion.signer.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "cliente")
public class Cliente {
    @Id
    @Column(length = 11)
    private String ruc;

    @Column(name = "razonSocial")
    private String razonSocial;

    @Column(name = "y")
    private String y;

    @Column(name = "nombreCorto")
    private String nombreCorto;

    public Cliente() {
    }

    public Cliente(String ruc, String razonSocial, String y, String nombreCorto) {
        this.ruc = ruc;
        this.razonSocial = razonSocial;
        this.y = y;
        this.nombreCorto = nombreCorto;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getNombreCorto() {
        return nombreCorto;
    }

    public void setNombreCorto(String nombreCorto) {
        this.nombreCorto = nombreCorto;
    }

    @Override
    public String toString() {
        return "Cliente [ruc=" + ruc + ", y=" + y + ", razonSocial=" + razonSocial + ", nombreCorto=" + nombreCorto
                + "]";
    }

}
