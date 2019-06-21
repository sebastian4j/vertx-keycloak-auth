package com.sebastian.vertx.keycloak;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.oauth2.providers.KeycloakAuth;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.OAuth2AuthHandler;
import java.util.Base64;

/**
 * @author Sebastián Ávila A.
 */
public class MainVerticle extends AbstractVerticle {

  
  @Override
  public void start(final Future<Void> fs) {
    final var router = Router.router(vertx);
    router.get("/").handler(this::root);

    final var oa = OAuth2AuthHandler.create(KeycloakAuth.create(vertx, new JsonObject()
        .put("realm", "sso").put("ssl-required", "external")
        .put("auth-server-url", "http://localhost:8282/auth").put("resource", "cliente-tres")
        .put("public-client", Boolean.TRUE).put("confidential-port", 0).put("realm-public-key",
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgSqzv4eVxA5Mc9nAUpPpkMeoY2xnkajZi6TQ0+RBKGRkqcILc/KVml5U3PFVOxSb4o7jvz0MgrXVDofIh0pwXwO7IzWtoDyU3IfJUdGNsI/obdUoggJT92CeW6+nYps1A1mDHrifyl92LQwDFQ5UfyY02APSlgCqQGpOaegmCJlroskuK3xax9qIQe2oG3rLscqblyA35gwgiqzmQP5pCg0eKrOH1Qtloo9sNpEiDq8wpKcKkyf4tKSGvsMWsVtAHyq1C91q65uqPyVWloN/iIRcY1VDZbmvJJc7X0z4pfNEOMiGYAru1T82MtJIjxwgP0CIwupGHNETnZH5g4eS1QIDAQAB")),
        "http://localhost:8081/callback");
    oa.setupCallback(router.get("/callback"));
    router.get("/seguro/*").handler(oa);
    router.get("/seguro/*").handler(this::seguro);
    router.get("/callback").handler(this::callback);
    vertx.createHttpServer().requestHandler(router).listen(8081, h -> {
      if (h.succeeded()) {
        fs.complete();
      } else {
        fs.fail(fs.cause());
      }
    });
  }

  private void callback(RoutingContext rc) {
    rc.response().end("");
  }

  public void root(final RoutingContext rc) {
    rc.response().end(Json.encodePrettily(new Saludo("hola!")));
  }

  public void seguro(final RoutingContext rc) {
    var user = rc.user();
    System.out.println(user.principal());
    final StringBuilder mensaje = new StringBuilder();
    final var privilegiado = "privilegiado";
    System.out.println(user.isAuthorized(privilegiado, r -> {
      if (r.succeeded() && r.result()) {
        mensaje.append("contiene el rol ").append(privilegiado);
      } else {
        mensaje.append("no contiene el rol ").append(privilegiado);
      }
    }));
    final var sudo = "sudo";
    try {
      final var partes = user.principal().getString("access_token").split("\\.");
      final var header = new JsonObject(new String(Base64.getDecoder().decode(partes[0])));
      final var body = new JsonObject(new String(Base64.getDecoder().decode(partes[1])));
      System.out.println(header);
      System.out.println(body);
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println(user.isAuthorized(sudo, r -> {
      if (r.succeeded() && r.result()) {
        mensaje.append(" contiene el rol ").append(sudo);
      } else {
        mensaje.append(" no contiene el rol ").append(sudo);
      }
    }));
    rc.response().end(Json.encodePrettily(new Saludo(mensaje.toString())));
  }
}

