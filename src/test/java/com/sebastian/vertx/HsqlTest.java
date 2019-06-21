package com.sebastian.vertx;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.assertj.core.api.Assertions;
import org.hsqldb.Server;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class HsqlTest {
  private static final Server server = new Server();

  private static final String BD = "hsql";

  @BeforeAll
  public static void iniciar() throws Exception {
    server.setDatabaseName(0, BD);
    server.setDatabasePath(0, "mem:" + BD);
    server.setPort(9001);
    server.start();
    cargarDatosIniciales();
  }

  @AfterAll
  public static void detener() {
    server.stop();
    server.shutdown();
  }

  private static Connection conectar() throws Exception {
    String url = "jdbc:hsqldb:hsql://" + server.getAddress() + ":" + server.getPort() + "/" + BD;
    Class.forName("org.hsqldb.jdbc.JDBCDriver");
    return DriverManager.getConnection(url, "SA", "");
  }

  private static void cargarDatosIniciales() throws Exception {
    try (Connection con = conectar()) {
      final var archivo = HsqlTest.class.getClassLoader().getResource("data.sql");
      Files.readAllLines(Paths.get(archivo.toURI())).forEach(l -> {
        try (var ps = con.prepareStatement(l)) {
          ps.execute();
        } catch (SQLException e) {
          throw new RuntimeException(e);
        }
      });
    }
  }

  @Test
  public void existenDatosEsperados() throws Exception {
    try (var con = conectar();
        var ps = con.prepareStatement("select count(*) from public.personas");
        var rs = ps.executeQuery()) {
      while (rs.next()) {
        Assertions.assertThat(rs.getInt(1)).isEqualTo(4);
      }
    }
  }

}
