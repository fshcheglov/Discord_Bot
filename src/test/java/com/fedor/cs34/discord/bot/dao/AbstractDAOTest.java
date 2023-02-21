package com.fedor.cs34.discord.bot.dao;

import com.fedor.cs34.discord.bot.CreateDatabases;
import com.fedor.cs34.discord.bot.DataAccess;
import com.fedor.cs34.discord.bot.util.data.nation.Leader;
import com.fedor.cs34.discord.bot.util.data.nation.Nation;
import com.fedor.cs34.discord.bot.util.data.nation.Species;
import com.fedor.cs34.discord.bot.util.data.system.Coordinates;
import com.fedor.cs34.discord.bot.util.data.system.Planet;
import com.fedor.cs34.discord.bot.util.data.system.Star;
import com.fedor.cs34.discord.bot.util.data.system.StarSystem;
import org.junit.jupiter.api.BeforeEach;

import java.sql.DriverManager;
import java.sql.SQLException;

public class AbstractDAOTest {
    public DataAccess dataAccess;

    @BeforeEach
    void setUp() throws Exception {
        var connection = DriverManager.getConnection("jdbc:h2:mem:", "", "");
        connection.setAutoCommit(false);
        CreateDatabases.startDatabase(connection, "interstellar_database.sql");

        dataAccess = new DataAccess(connection);
    }

    public Nation createNation(Coordinates coordinates, String ownerID, String name) throws SQLException {
        var system = new StarSystem(new Coordinates(coordinates.x, coordinates.y), "bar", null, -1);
        dataAccess.starSystemDAO.insert(system);
        Star star = new Star(dataAccess.starTypeDAO.random(), "foo", system, 4, -1);
        dataAccess.starDAO.insert(star);

        var leaderName = "My leader";
        var speciesName = "Species name";
        var population = 10;

        var planetName = "My Planet";
        var planetType = "My Planet Type";
        var resources = 0;
        var development = 18;
        var size = 8;

        var governmentId = 1;
        var economicId = 1;

        var stability = 56.78;
        var centralization = 30.18;
        var approval = 68.7;
        {
            var leader = new Leader(-1, leaderName);
            dataAccess.leaderDAO.insert(leader);

            var species = new Species(-1, speciesName);
            dataAccess.speciesDAO.insert(species);

            var capital = new Planet(planetName, planetType, resources, population, development, size, star, true, -1);
            dataAccess.planetDAO.insert(capital);

            var government = dataAccess.governmentDAO.getById(governmentId);
            var economy = dataAccess.economicDAO.getById(economicId);

            var writeNation = new Nation(name, leader, government, economy, species, population, stability, centralization, approval, capital, -1, ownerID);
            dataAccess.nationDAO.insert(writeNation);

            return writeNation;
        }
    }
}
