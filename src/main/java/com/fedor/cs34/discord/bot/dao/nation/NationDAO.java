package com.fedor.cs34.discord.bot.dao.nation;

import com.fedor.cs34.discord.bot.DataAccess;
import com.fedor.cs34.discord.bot.data.nation.Nation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class NationDAO {
    private final DataAccess dataAccess;
    private final Connection connection;

    private int readingId = -1;
    private Nation readingInstance;

    public NationDAO(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
        this.connection = dataAccess.connection;
    }

    public void insert(Nation nation) throws SQLException {
        nation.capital.star.system.owner = nation;

        var statement = connection.prepareStatement("insert into nation " +
                        "(name, leader, government, economic_type, primary_species, ownerID, resource_points," +
                        " economic_points, manpower_points, stability, centralization, approval, population, capital) " +
                        "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS);

        statement.setString(1, nation.nationName);
        statement.setInt(2, nation.leader.getId());
        statement.setInt(3, nation.government.getId());
        statement.setInt(4, nation.economicType.getId());
        statement.setInt(5, nation.species.getId());
        statement.setString(6, nation.ownerID);
        statement.setInt(7, nation.development.resourcePoints);
        statement.setInt(8, nation.development.economicPoints);
        statement.setInt(9, nation.development.manpowerPoints);
        // Remove Stability
        // Remove Approval
        statement.setDouble(10, nation.stability);
        statement.setDouble(11, nation.centralization);
        statement.setDouble(12, nation.approval);
        statement.setInt(13, nation.population);
        statement.setInt(14, nation.capital.id);

        statement.executeUpdate();
        var keys = statement.getGeneratedKeys();
        keys.next();
        nation.id = keys.getInt(1);
        dataAccess.starSystemDAO.setOwner(nation.capital.star.system, nation);
    }

    public List<Nation> getAll() throws SQLException {
        var result = new ArrayList<Nation>();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM nation");

        while (resultSet.next()) {
            var id = resultSet.getInt("id");
            var nationName = resultSet.getString("name");
            var leader = dataAccess.leaderDAO.getById(resultSet.getInt("leader"));
            var government = dataAccess.governmentDAO.getById(resultSet.getInt("government"));
            var economicType = dataAccess.economicDAO.getById(resultSet.getInt("economic_type"));
            var species = dataAccess.speciesDAO.getById(resultSet.getInt("primary_species"));
            var population = resultSet.getInt("population");
            var stability = resultSet.getDouble("stability");
            var centralization = resultSet.getDouble("centralization");
            var approval = resultSet.getDouble("approval");

            var resourcePoints = resultSet.getInt("resource_points");
            var economicPoints = resultSet.getInt("economic_points");
            var manpowerPoints = resultSet.getInt("manpower_points");

            var capital = dataAccess.planetDAO.getById(resultSet.getInt("capital"));

            var ownerID = resultSet.getString("ownerID");

            var nationToReturn = new Nation(nationName, leader, government, economicType, species, population, stability,
                    centralization, approval, capital, id, ownerID);
            nationToReturn.development.resourcePoints = resourcePoints;
            nationToReturn.development.economicPoints = economicPoints;
            nationToReturn.development.manpowerPoints = manpowerPoints;

            result.add(nationToReturn);
        }
        return result;
    }



    public Nation getById(int id) throws SQLException {
        if (id == readingId) {
            return readingInstance;
        }

        var statement = connection.prepareStatement("SELECT * FROM nation WHERE id = ?");
        statement.setInt(1, id);
        var resultSet = statement.executeQuery();

        if (resultSet.next()) {
            return createFromResultSet(resultSet);
        } else {
            throw new IllegalArgumentException("No nation with ID: " + id);
        }
    }

    Nation createFromResultSet(ResultSet resultSet) throws SQLException {
        var id = resultSet.getInt("id");
        var nationName = resultSet.getString("name");
        var leader = dataAccess.leaderDAO.getById(resultSet.getInt("leader"));
        var government = dataAccess.governmentDAO.getById(resultSet.getInt("government"));
        var economicType = dataAccess.economicDAO.getById(resultSet.getInt("economic_type"));
        var species = dataAccess.speciesDAO.getById(resultSet.getInt("primary_species"));
        var population = resultSet.getInt("population");
        var stability = resultSet.getDouble("stability");
        var centralization = resultSet.getDouble("centralization");
        var approval = resultSet.getDouble("approval");

        var resourcePoints = resultSet.getInt("resource_points");
        var economicPoints = resultSet.getInt("economic_points");
        var manpowerPoints = resultSet.getInt("manpower_points");

        var ownerID = resultSet.getString("ownerID");


        var result = new Nation(nationName, leader, government, economicType, species, population, stability,
                centralization, approval, null, id, ownerID);
        result.development.resourcePoints = resourcePoints;
        result.development.economicPoints = economicPoints;
        result.development.manpowerPoints = manpowerPoints;

        readingId = id;
        readingInstance = result;

        result.capital = dataAccess.planetDAO.getById(resultSet.getInt("capital"));
        return result;
    }
}
