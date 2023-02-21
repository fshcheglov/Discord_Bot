package com.fedor.cs34.discord.bot.dao.system;

import com.fedor.cs34.discord.bot.DataAccess;
import com.fedor.cs34.discord.bot.util.data.system.Coordinates;
import com.fedor.cs34.discord.bot.util.data.system.Star;
import com.fedor.cs34.discord.bot.util.data.system.StarSystem;
import com.fedor.cs34.discord.bot.util.data.system.StarType;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StarDAO {
    private final DataAccess dataAccess;
    private final Connection connection;

    private int readingId = -1;
    private Star readingInstance;

    public StarDAO(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
        this.connection = dataAccess.connection;
    }

    public List<Star> getAll() throws SQLException {
        var result = new ArrayList<Star>();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM star");

        while (resultSet.next()) {
            result.add(createFromResultSet(resultSet));
        }
        return result;
    }

    public int count() throws SQLException {
        var statement = connection.prepareStatement("SELECT COUNT(*) AS total FROM star");
        var resultSet = statement.executeQuery();
        resultSet.next();
        return resultSet.getInt("total");
    }

    public Star random(Coordinates coordinates) throws SQLException {
        // StarType type, String name, StarSystem system, int resources, int id

        var type = dataAccess.starTypeDAO.random();
        var name = type.name.charAt(0) + "-" + coordinates.x + coordinates.y;
        var randomSystem = new StarSystem(coordinates, name, null, 0);
        var resources = new Random().nextInt(20);

        var randomStar = new Star(type, name, randomSystem, resources, 0);

        dataAccess.starSystemDAO.insert(randomSystem);
        insert(randomStar);

        return randomStar;
    }


    public Star getById(int id) throws SQLException {
        if (id == readingId) {
            return readingInstance;
        }
        var statement = connection.prepareStatement("SELECT * FROM star WHERE id = ?");
        statement.setInt(1, id);
        var resultSet = statement.executeQuery();

        if (resultSet.next()) {
            return createFromResultSet(resultSet);
        } else {
            throw new IllegalArgumentException("No star with ID: " + id);
        }
    }

    public void insert(Star star) throws SQLException {
        var statement = connection.prepareStatement("insert into star (system, name, type, resources) " +
                        "values(?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS);
        statement.setInt(1, star.system.id);
        statement.setString(2, star.name);
        statement.setInt(3, star.type.id);
        statement.setInt(4, star.resources);
        statement.executeUpdate();
        var keys = statement.getGeneratedKeys();
        keys.next();
        star.id = keys.getInt(1);
    }

    Star createFromResultSet(ResultSet resultSet) throws SQLException {
        StarType type = dataAccess.starTypeDAO.getById(resultSet.getInt("type"));
        String name = resultSet.getString("name");
        int resources = resultSet.getInt("resources");
        int id = resultSet.getInt("id");
        var result = new Star(type, name, null, resources, id);
        readingId = id;
        readingInstance = result;
        result.system = dataAccess.starSystemDAO.getById(resultSet.getInt("system"));
        return result;
    }
}
