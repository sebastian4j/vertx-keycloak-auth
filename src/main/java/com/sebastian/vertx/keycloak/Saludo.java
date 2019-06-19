package com.sebastian.vertx.keycloak;

/**
 * @author Sebastián Ávila A.
 */
public class Saludo {

    private String mensaje;

    public Saludo() {
    }

    public Saludo(final String str) {
        mensaje = str;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    @Override
    public String toString() {
        return "Saludo{" + "mensaje=" + mensaje + '}';
    }

}
