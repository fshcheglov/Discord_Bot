package com.fedor.cs34.discord.bot.dao.nation;

import com.fedor.cs34.discord.bot.DataAccess;
import com.fedor.cs34.discord.bot.util.data.nation.ModifierType;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class NationModifierTypeDAO {
    private final Connection connection;

    public NationModifierTypeDAO(DataAccess dataAccess) {
        this.connection = dataAccess.connection;
    }

    public List<ModifierType> getAll() throws SQLException {
        var result = new ArrayList<ModifierType>();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM modifier_type");

        while (resultSet.next()) {
            var id = resultSet.getInt("id");
            var type = resultSet.getString("type");
            result.add(new ModifierType(type, id));
        }
        return result;
    }

    ModifierType getById(int id) throws SQLException {
        var statement = connection.prepareStatement("SELECT * FROM modifier_type WHERE id = ?");
        statement.setInt(1, id);
        var resultSet = statement.executeQuery();

        if (resultSet.next()) {
            return createFromResultSet(resultSet);
        } else {
            throw new IllegalArgumentException("No modifier type with ID: " + id);
        }
    }

    int count() throws SQLException {
        var statement = connection.prepareStatement("SELECT COUNT (*) FROM modifier_type");
        var resultSet = statement.executeQuery();
        int count = -1;
        if (resultSet.next()) {
            count = resultSet.getInt(1);
        }
        return count;
    }

    public ModifierType getByName(String name) throws SQLException {
        var statement = connection.prepareStatement("SELECT * FROM modifier_type WHERE type = ?");
        statement.setString(1, name);
        var resultSet = statement.executeQuery();

        if (resultSet.next()) {
            return createFromResultSet(resultSet);
        } else {
            throw new IllegalArgumentException("No modifier type with type: " + name);
        }
    }

    static ModifierType createFromResultSet(ResultSet resultSet) throws SQLException {
        var id = resultSet.getInt("id");
        var type = resultSet.getString("type");
        return new ModifierType(type, id);
    }
}
