package com.joa.prexixion.signer.model;

public class File {
    private String nombre;
    private String bucket;

    public File() {
    }

    public File(String nombre, String bucket) {
        this.nombre = nombre;
        this.bucket = bucket;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    @Override
    public String toString() {
        return "File [nombre=" + nombre + ", bucket=" + bucket + "]";
    }

}
