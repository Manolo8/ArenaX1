package br.com.tinycraft.arenax1.build;

import br.com.tinycraft.arenax1.exception.DataBaseException;

import java.sql.Connection;

public interface DataBase {

    Connection getConnection() throws DataBaseException;
}
