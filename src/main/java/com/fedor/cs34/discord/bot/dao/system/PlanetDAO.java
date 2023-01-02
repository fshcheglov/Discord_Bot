package com.fedor.cs34.discord.bot.dao.system;

import com.fedor.cs34.discord.bot.data.system.Planet;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PlanetDAO {
    private final Connection connection;

    public PlanetDAO(Connection connection) {
        this.connection = connection;
    }

    public List<Planet> getAll() throws SQLException {
        var result = new ArrayList<Planet>();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM planet");

        while (resultSet.next()) {
            result.add(createFromResultSet(resultSet, connection));
        }
        return result;
    }

    public Planet getById(int id) throws SQLException {
        var statement = connection.prepareStatement("SELECT * FROM planet WHERE id = ?");
        statement.setInt(1, id);
        var resultSet = statement.executeQuery();

        if (resultSet.next()) {
            return createFromResultSet(resultSet, connection);
        } else {
            throw new IllegalArgumentException("No planet with ID: " + id);
        }
    }

    public void insert(Planet planet) throws SQLException {
        var statement = connection.prepareStatement("insert into planet (name, type, resources," +
                        " population, development, planet_size, star, is_habitable) " +
                        "values(?, ?, ?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS);
        statement.setString(1, planet.name);
        statement.setString(2, planet.type);
        statement.setInt(3, planet.resources);
        statement.setInt(4, planet.population);
        statement.setInt(5, planet.development);
        statement.setInt(6, planet.size);
        statement.setInt(7, planet.star.id);
        statement.setInt(8, (planet.isHabitable ? 1 : 0));
        statement.executeUpdate();
        var keys = statement.getGeneratedKeys();
        keys.next();
        planet.id = keys.getInt(1);
    }

    static Planet createFromResultSet(ResultSet resultSet, Connection connection) throws SQLException {
        var name = resultSet.getString("name");
        var type = resultSet.getString("type");
        var resources = resultSet.getInt("resources");
        var population = resultSet.getInt("population");
        var development = resultSet.getInt("development");
        var size = resultSet.getInt("planet_size");
        //starDAO get by ID
        var star = new StarDAO(connection).getById(resultSet.getInt("star"));
        var isHabitable = resultSet.getInt("is_habitable") == 1;
        var id = resultSet.getInt("id");
        return new Planet(name, type, resources, population, development, size, star, isHabitable, id);
    }
}
