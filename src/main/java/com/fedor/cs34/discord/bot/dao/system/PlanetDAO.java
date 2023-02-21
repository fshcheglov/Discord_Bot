package com.fedor.cs34.discord.bot.dao.system;

import com.fedor.cs34.discord.bot.DataAccess;
import com.fedor.cs34.discord.bot.util.data.nation.Nation;
import com.fedor.cs34.discord.bot.util.data.system.Planet;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PlanetDAO {
    private final DataAccess dataAccess;
    private final Connection connection;

    private int readingId = -1;
    private Planet readingInstance;

    public PlanetDAO(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
        this.connection = dataAccess.connection;
    }

    public List<Planet> getAll() throws SQLException {
        var result = new ArrayList<Planet>();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM planet");

        while (resultSet.next()) {
            result.add(createFromResultSet(resultSet));
        }
        return result;
    }

    public int count() throws SQLException {
        var statement = connection.prepareStatement("SELECT COUNT(*) AS total FROM planet");
        var resultSet = statement.executeQuery();
        resultSet.next();
        return resultSet.getInt("total");
    }

    public Planet getById(int id) throws SQLException {
        if (id == readingId) {
            return readingInstance;
        }

        var statement = connection.prepareStatement("SELECT * FROM planet WHERE id = ?");
        statement.setInt(1, id);
        var resultSet = statement.executeQuery();

        if (resultSet.next()) {
            return createFromResultSet(resultSet);
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

    Planet createFromResultSet(ResultSet resultSet) throws SQLException {
        var id = resultSet.getInt("id");
        var name = resultSet.getString("name");
        var type = resultSet.getString("type");
        var resources = resultSet.getInt("resources");
        var population = resultSet.getInt("population");
        var development = resultSet.getInt("development");
        var size = resultSet.getInt("planet_size");
        var isHabitable = resultSet.getInt("is_habitable") == 1;
        var result = new Planet(name, type, resources, population, development, size, null, isHabitable, id);
        readingId = id;
        readingInstance = result;
        result.star = dataAccess.starDAO.getById(resultSet.getInt("star"));
        return result;
    }

    public void setOwner(Planet planet, Nation nation) throws SQLException {
        var statement = connection.prepareStatement("UPDATE planet SET owner = ? WHERE id = ? ");
        var statement2 = connection.prepareStatement("UPDATE system2 SET owner = ? WHERE id = ? ");

        statement.setInt(1, nation.id);
        statement.setInt(2, planet.id);
        statement.executeUpdate();

        statement2.setInt(1, nation.id);
        statement2.setInt(2, planet.star.system.id);
        statement2.executeUpdate();

    }
}
