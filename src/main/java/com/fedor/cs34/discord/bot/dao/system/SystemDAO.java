package com.fedor.cs34.discord.bot.dao.system;

import com.fedor.cs34.discord.bot.DataAccess;
import com.fedor.cs34.discord.bot.data.nation.Nation;
import com.fedor.cs34.discord.bot.data.system.Coordinates;
import com.fedor.cs34.discord.bot.data.system.StarSystem;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SystemDAO {
    private final DataAccess dataAccess;
    private final Connection connection;

    private int readingId = -1;
    private StarSystem readingInstance;

    public SystemDAO(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
        this.connection = dataAccess.connection;
    }

    public List<StarSystem> getAll() throws SQLException {
        var result = new ArrayList<StarSystem>();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM system2");

        while (resultSet.next()) {
            result.add(createFromResultSet(resultSet));
        }
        return result;
    }

    StarSystem random(int x, int y, int nationID) throws SQLException {
        var coordinates = new Coordinates(x, y);
        var name = "";
        Nation owner;
        owner = dataAccess.nationDAO.getById(nationID);
        var system = new StarSystem(coordinates, name, owner, 0);
        insert(system);
        return system;
    }

    StarSystem random(int x, int y) throws SQLException {
        var coordinates = new Coordinates(x, y);
        var name = "";
        var system = new StarSystem(coordinates, name, null, 0);
        insert(system);
        return system;
    }

  public  StarSystem getById(int id) throws SQLException {
        if (id == readingId) {
            return readingInstance;
        }
        var statement = connection.prepareStatement("SELECT * FROM system2 WHERE id = ?");
        statement.setInt(1, id);
        var resultSet = statement.executeQuery();

        if (resultSet.next()) {
            return createFromResultSet(resultSet);
        } else {
            throw new IllegalArgumentException("No system with ID: " + id);
        }
    }

    public void insert(StarSystem system) throws SQLException {
        var statement = connection.prepareStatement("insert into system2 (name, map_x, map_y) " +
                        "values(?,?,?)",
                Statement.RETURN_GENERATED_KEYS);
        statement.setString(1, system.name);
        statement.setInt(2, system.coordinates.x);
        statement.setInt(3, system.coordinates.y);
        statement.executeUpdate();
        var keys = statement.getGeneratedKeys();
        keys.next();
        system.id = keys.getInt(1);
    }

    private StarSystem createFromResultSet(ResultSet resultSet) throws SQLException {
        var name = resultSet.getString("name");
        var id = resultSet.getInt("id");
        var coordinates = new Coordinates(resultSet.getInt("map_x"), resultSet.getInt("map_y"));


        var result = new StarSystem(coordinates, name, null, id);
        readingId = id;
        readingInstance = result;

        var ownerId = resultSet.getInt("owner");
        if (!resultSet.wasNull()) {
            result.owner = dataAccess.nationDAO.getById(ownerId);
        }

        return result;
    }

    public void setOwner(StarSystem starSystem, Nation nation) throws SQLException {
        starSystem.owner = nation;
        var statement = connection.prepareStatement("UPDATE system2 SET owner=? WHERE ID=?");
        statement.setInt(1, nation.id);
        statement.setInt(2, starSystem.id);
        statement.executeUpdate();
    }
}
