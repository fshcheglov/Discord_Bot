package com.fedor.cs34.discord.bot;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;

import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;

public class CreateDatabases {
    public static Connection createConnection(String pathName) throws Exception {
        String jdbcURL = String.format("jdbc:h2:C:/Users/Fedor3/Documents/database/H2/%s", pathName);
        String username = "Vrell";
        String password = "1234";

        Connection connection = DriverManager.getConnection(jdbcURL, username, password);

        return connection;
    }

    public static void startDatabase(Connection connection, String pathName) throws Exception {
        Reader reader = Resources.getResourceAsReader(String.format("com/fedor/cs34/discord/bot/%s", pathName));

        ScriptRunner runner = new ScriptRunner(connection);
        runner.setLogWriter(null);
//        runner.setErrorLogWriter(null);
        runner.runScript(reader);
        connection.commit();
        reader.close();
    }
}
