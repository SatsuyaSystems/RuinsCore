package de.satsuya.ruinsCore.core.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Funktionales Interface für flexible SQL Parameter-Binding
 */
@FunctionalInterface
public interface StatementSetter {
    void setValues(PreparedStatement stmt) throws SQLException;
}

