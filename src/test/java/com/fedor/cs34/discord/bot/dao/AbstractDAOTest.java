package com.fedor.cs34.discord.bot.dao;

import com.fedor.cs34.discord.bot.CreateDatabases;
import com.fedor.cs34.discord.bot.DataAccess;
import org.junit.jupiter.api.BeforeEach;

import java.sql.DriverManager;

public class AbstractDAOTest {
    public DataAccess dataAccess;

    @BeforeEach
    void setUp() throws Exception {
        var connection = DriverManager.getConnection("jdbc:h2:mem:", "", "");
        connection.setAutoCommit(false);
        CreateDatabases.startDatabase(connection, "interstellar_database.sql");

        dataAccess = new DataAccess(connection);
    }
}
