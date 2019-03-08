package com.acme.mvc;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

public class TablePrefixedSpringPhysicalNamingStrategy extends PhysicalNamingStrategyStandardImpl {
  @Override
  public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment jdbcEnvironment) {
    Identifier identifier = super.toPhysicalTableName(name, jdbcEnvironment);
    return new Identifier(identifier.getText() + "_tab", identifier.isQuoted());
  }
}
