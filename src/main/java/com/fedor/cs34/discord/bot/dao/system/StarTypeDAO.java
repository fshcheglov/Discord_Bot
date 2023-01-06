package com.fedor.cs34.discord.bot.dao.system;

import com.fedor.cs34.discord.bot.DataAccess;
import com.fedor.cs34.discord.bot.data.system.StarType;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StarTypeDAO {
    private final Connection connection;

    public StarTypeDAO(DataAccess dataAccess) {
        this.connection = dataAccess.connection;
    }

    public List<StarType> getAll() throws SQLException {
        var result = new ArrayList<StarType>();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM star_type");

        while (resultSet.next()) {
            var id = resultSet.getInt("id");
            var name = resultSet.getString("name");
            result.add(new StarType(name, id));
        }
        return result;
    }

    StarType getById(int id) throws SQLException {
        var statement = connection.prepareStatement("SELECT * FROM star_type WHERE id = ?");
        statement.setInt(1, id);
        var resultSet = statement.executeQuery();

        if (resultSet.next()) {
            return createFromResultSet(resultSet);
        } else {
            throw new IllegalArgumentException("No star type type with ID: " + id);
        }
    }

    int count() throws SQLException {
        var statement = connection.prepareStatement("SELECT COUNT (*) FROM star_type");
        var resultSet = statement.executeQuery();
        int count = -1;
        if (resultSet.next())
        {
            count = resultSet.getInt(1);
        }
        return count;
    }

    StarType random() throws SQLException {
        var randomID = 1 + new Random().nextInt(count());
        return getById(randomID);
    }

    public StarType getByName(String name) throws SQLException {
        var statement = connection.prepareStatement("SELECT * FROM star_type WHERE name = ?");
        statement.setString(1, name);
        var resultSet = statement.executeQuery();

        if (resultSet.next()) {
            return createFromResultSet(resultSet);
        } else {
            throw new IllegalArgumentException("No star type type with Name: " + name);
        }
    }

    static StarType createFromResultSet(ResultSet resultSet) throws SQLException {
        var id = resultSet.getInt("id");
        var name = resultSet.getString("name");
        return new StarType(name, id);
    }
}
