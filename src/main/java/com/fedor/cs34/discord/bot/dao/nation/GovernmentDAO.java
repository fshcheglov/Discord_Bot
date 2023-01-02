package com.fedor.cs34.discord.bot.dao.nation;

import com.fedor.cs34.discord.bot.data.nation.Government;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class GovernmentDAO {
    private final Connection connection;

    public GovernmentDAO(Connection connection) {
        this.connection = connection;
    }

    public List<Government> getAll() throws SQLException {
        var result = new ArrayList<Government>();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM government");

        while (resultSet.next()) {
            var id = resultSet.getInt("id");
            var name = resultSet.getString("name");
            result.add(new Government(name, id));
        }
        return result;
    }

    Government getById(int id) throws SQLException {
        var statement = connection.prepareStatement("SELECT * FROM government WHERE id = ?");
        statement.setInt(1, id);
        var resultSet = statement.executeQuery();

        if (resultSet.next()) {
            return createFromResultSet(resultSet);
        } else {
            throw new IllegalArgumentException("No government type with ID: " + id);
        }
    }

   public Government getByName(String name) throws SQLException {
        var statement = connection.prepareStatement("SELECT * FROM government WHERE name = ?");
        statement.setString(1, name);
        var resultSet = statement.executeQuery();

        if (resultSet.next()) {
            return createFromResultSet(resultSet);
        } else {
            throw new IllegalArgumentException("No government type with Name: " + name);
        }
    }

    static Government createFromResultSet(ResultSet resultSet) throws SQLException {
        var id = resultSet.getInt("id");
        var name = resultSet.getString("name");
        return new Government(name, id);
    }
}
