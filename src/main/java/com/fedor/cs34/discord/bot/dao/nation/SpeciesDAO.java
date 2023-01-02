package com.fedor.cs34.discord.bot.dao.nation;

import com.fedor.cs34.discord.bot.data.nation.Species;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SpeciesDAO {
    private final Connection connection;

    public SpeciesDAO(Connection connection) {
        this.connection = connection;
    }

    public List<Species> getAll() throws SQLException {
        var result = new ArrayList<Species>();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM species");

        while (resultSet.next()) {
            result.add(createFromResultSet(resultSet));
        }
        return result;
    }

    Species getById(int id) throws SQLException {
        var statement = connection.prepareStatement("SELECT * FROM species WHERE id = ?");
        statement.setInt(1, id);
        var resultSet = statement.executeQuery();

        if (resultSet.next()) {
            return createFromResultSet(resultSet);
        } else {
            throw new IllegalArgumentException("No economic type with ID: " + id);
        }
    }

    public void insert(Species species) throws SQLException {
        var statement = connection.prepareStatement("insert into species (name) values(?)",
                Statement.RETURN_GENERATED_KEYS);
        statement.setString(1, species.name);
        statement.executeUpdate();
        var keys = statement.getGeneratedKeys();
        keys.next();
        species.id = keys.getInt(1);
    }

    static Species createFromResultSet(ResultSet resultSet) throws SQLException {
        var id = resultSet.getInt("id");
        var name = resultSet.getString("name");
        return new Species(name, id);
    }
}
