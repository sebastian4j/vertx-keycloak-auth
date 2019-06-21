package com.sebastian.vertx.keycloak;

import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Sebastián Ávila A.
 */
public class App {
  private static final Logger LOGGER = LoggerFactory.getLogger(MainVerticle.class);

  public static void main(String[] args) {
    LOGGER.info("comenzando");
    Vertx.vertx().deployVerticle(new MainVerticle());
  }
}
