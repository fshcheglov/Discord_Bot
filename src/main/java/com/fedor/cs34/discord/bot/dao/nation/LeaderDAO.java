package com.fedor.cs34.discord.bot.dao.nation;

import com.fedor.cs34.discord.bot.DataAccess;
import com.fedor.cs34.discord.bot.data.nation.Leader;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class LeaderDAO {
    private final DataAccess dataAccess;
    private final Connection connection;

    public LeaderDAO(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
        this.connection = dataAccess.connection;
    }

    public void insert(Leader leader) throws SQLException {
        var statement = connection.prepareStatement("insert into leader (name) values(?)",
                Statement.RETURN_GENERATED_KEYS);
        statement.setString(1, leader.getName());
        statement.executeUpdate();
        var keys = statement.getGeneratedKeys();
        keys.next();
        leader.setId(keys.getInt(1));
    }

    public List<Leader> getAll() throws SQLException {
        var result = new ArrayList<Leader>();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM leader");

        while (resultSet.next()) {
            result.add(createFromResultSet(resultSet));
        }
        return result;
    }

    Leader getById(int id) throws SQLException {
        var statement = connection.prepareStatement("SELECT * FROM leader WHERE id = ?");
        statement.setInt(1, id);
        var resultSet = statement.executeQuery();

        if (resultSet.next()) {
            return createFromResultSet(resultSet);
        } else {
            throw new IllegalArgumentException("No leader with ID: " + id);
        }
    }

    static Leader createFromResultSet(ResultSet resultSet) throws SQLException {
        var id = resultSet.getInt("id");
        var name = resultSet.getString("name");
        return new Leader(id, name);
    }

}
