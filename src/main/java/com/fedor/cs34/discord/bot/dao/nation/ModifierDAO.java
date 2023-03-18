package com.fedor.cs34.discord.bot.dao.nation;

import com.fedor.cs34.discord.bot.DataAccess;
import com.fedor.cs34.discord.bot.util.data.nation.NationModifier;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ModifierDAO {
    private final Connection connection;
    private final DataAccess dataAccess;


    public ModifierDAO(DataAccess dataAccess) {
        this.connection = dataAccess.connection;
        this.dataAccess = dataAccess;
    }

    public void insert(NationModifier modifier) throws SQLException {
        var statement = connection.prepareStatement("insert into modifier (name, is_flat, type, " +
                "modifier_value, duration, nation) values(?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
        statement.setString(1, modifier.name);
        statement.setInt(2, modifier.isFlatModifier ? 1 : 0);
        statement.setInt(3, modifier.type.id);
        statement.setDouble(4, modifier.value);
        statement.setInt(5, modifier.duration);
        statement.setInt(6, modifier.nation.id);

        statement.executeUpdate();
        var keys = statement.getGeneratedKeys();
        keys.next();
        modifier.id = keys.getInt(1);
    }

    public List<NationModifier> getAll() throws SQLException {
        var result = new ArrayList<NationModifier>();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM modifier");

        while (resultSet.next()) {
            var id = resultSet.getInt("id");
            var name = resultSet.getString("name");
            var isFlat = resultSet.getInt("is_flat") == 1;
            var type = dataAccess.nationModifierTypeDAO.getById(resultSet.getInt("type"));
            var value = resultSet.getDouble("modifier_value");
            var duration = resultSet.getInt("duration");
            var nation = resultSet.getInt("nation");

            var nationModifier = new NationModifier(id, name, isFlat, type, value, duration, dataAccess.nationDAO.getById(nation));
            result.add(nationModifier);
        }
        return result;
    }

    public NationModifier getById(int id) throws SQLException {
        var statement = connection.prepareStatement("SELECT * FROM modifier WHERE id = ?");
        statement.setInt(1, id);
        var resultSet = statement.executeQuery();

        if (resultSet.next()) {
            return createFromResultSet(resultSet);
        } else {
            throw new IllegalArgumentException("No modifier with ID: " + id);
        }
    }

    public NationModifier getByName(String name) throws SQLException {
        var statement = connection.prepareStatement("SELECT * FROM modifier WHERE name = ?");
        statement.setString(1, name);
        var resultSet = statement.executeQuery();

        if (resultSet.next()) {
            return createFromResultSet(resultSet);
        } else {
            throw new IllegalArgumentException("No modifier with Name: " + name);
        }
    }

    NationModifier createFromResultSet(ResultSet resultSet) throws SQLException {
        var id = resultSet.getInt("id");
        var name = resultSet.getString("name");
        var isFlat = resultSet.getInt("is_flat") == 1;
        var type = dataAccess.nationModifierTypeDAO.getById(resultSet.getInt("type"));
        var value = resultSet.getDouble("modifier_value");
        var duration = resultSet.getInt("duration");
        var nation = dataAccess.nationDAO.getById(resultSet.getInt("nation"));

        return new NationModifier(id, name, isFlat, type, value, duration, nation);
    }
}
