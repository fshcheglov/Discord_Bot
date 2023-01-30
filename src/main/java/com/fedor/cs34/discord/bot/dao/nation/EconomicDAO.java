package com.fedor.cs34.discord.bot.dao.nation;

import com.fedor.cs34.discord.bot.DataAccess;
import com.fedor.cs34.discord.bot.data.nation.EconomicType;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class EconomicDAO {
    private final Connection connection;

    public EconomicDAO(DataAccess dataAccess) {
        this.connection = dataAccess.connection;
    }

    public List<EconomicType> getAll() throws SQLException {
        var result = new ArrayList<EconomicType>();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM economy");

        while (resultSet.next()) {
            result.add(createFromResultSet(resultSet));
        }
        return result;
    }

    public EconomicType getById(int id) throws SQLException {
        var statement = connection.prepareStatement("SELECT * FROM economy WHERE id = ?");
        statement.setInt(1, id);
        var resultSet = statement.executeQuery();

        if (resultSet.next()) {
            return createFromResultSet(resultSet);
        } else {
            throw new IllegalArgumentException("No economic type with ID: " + id);
        }
    }

    public EconomicType getByName(String name) throws SQLException {
        var statement = connection.prepareStatement("SELECT * FROM economy WHERE name = ?");
        statement.setString(1, name);
        var resultSet = statement.executeQuery();

        if (resultSet.next()) {
            return createFromResultSet(resultSet);
        } else {
            throw new IllegalArgumentException("No economic type with Name: " + name);
        }
    }

    private static EconomicType createFromResultSet(ResultSet resultSet) throws SQLException {
        var id = resultSet.getInt("id");
        var name = resultSet.getString("name");
        return new EconomicType(id, name);
    }
}
