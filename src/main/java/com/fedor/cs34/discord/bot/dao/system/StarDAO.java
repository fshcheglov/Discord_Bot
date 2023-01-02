package com.fedor.cs34.discord.bot.dao.system;

import com.fedor.cs34.discord.bot.data.system.Star;
import com.fedor.cs34.discord.bot.data.system.StarSystem;
import com.fedor.cs34.discord.bot.data.system.StarType;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StarDAO {
    private final Connection connection;

    public StarDAO(Connection connection) {
        this.connection = connection;
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

    public Star random(int x, int y, int nationID) throws SQLException {
        var starType = new StarTypeDAO(connection).random();
        var name = starType.name.charAt(0) + "-" + x + y;
        StarSystem system;
        if (nationID == 0) {
            system = new SystemDAO(connection).random(x, y);
        } else {
            system = new SystemDAO(connection).random(x, y, nationID);
        }
        var resources = new Random().nextInt(15);
        var star = new Star(starType, name, system, resources, 0);
        insert(star);
        return star;
    }

    Star getById(int id) throws SQLException {
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
        StarType type = new StarTypeDAO(connection).getById(resultSet.getInt("type"));
        String name = resultSet.getString("name");
        StarSystem system = new SystemDAO(connection).getById(resultSet.getInt("system"));
        int resources = resultSet.getInt("resources");
        int id = resultSet.getInt("id");

        return new Star(type, name, system, resources, id);
    }
}
