package com.fedor.cs34.discord.bot.dao.system;

import com.fedor.cs34.discord.bot.dao.AbstractDAOTest;
import com.fedor.cs34.discord.bot.data.nation.Leader;
import com.fedor.cs34.discord.bot.data.nation.Nation;
import com.fedor.cs34.discord.bot.data.nation.Species;
import com.fedor.cs34.discord.bot.data.system.Coordinates;
import com.fedor.cs34.discord.bot.data.system.Planet;
import com.fedor.cs34.discord.bot.data.system.Star;
import com.fedor.cs34.discord.bot.data.system.StarSystem;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class StarSystemDAOTest extends AbstractDAOTest {
    @Test
    void getAll() throws SQLException {
        var starSystem1 = new StarSystem(new Coordinates(0, 0), "foo", null, -1);
        var starSystem2 = new StarSystem(new Coordinates(0, 1), "bar", null, -1);
        var starSystem3 = new StarSystem(new Coordinates(1, 1), "baz", null, -1);

        dataAccess.starSystemDAO.insert(starSystem1);
        dataAccess.starSystemDAO.insert(starSystem2);
        dataAccess.starSystemDAO.insert(starSystem3);

        Nation writeNation;
        {
            var star = new Star(dataAccess.starTypeDAO.random(), "foo", starSystem3, 5, -1);
            dataAccess.starDAO.insert(star);

            var name = "Nation name";
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

            var leader = new Leader(-1, leaderName);
            dataAccess.leaderDAO.insert(leader);

            var species = new Species(-1, speciesName);
            dataAccess.speciesDAO.insert(species);

            var capital = new Planet(planetName, planetType, resources, population, development, size, star, true, -1);
            dataAccess.planetDAO.insert(capital);

            var government = dataAccess.governmentDAO.getById(governmentId);
            var economy = dataAccess.economicDAO.getById(economicId);
            String ownerID = "-1";

            writeNation = new Nation(name, leader, government, economy, species, population, stability, centralization, approval, capital, -1, ownerID);
            dataAccess.nationDAO.insert(writeNation);

            dataAccess.planetDAO.setOwner(capital, writeNation);
        }

        var systems = dataAccess.starSystemDAO.getAll();
        assertThat(systems, hasSize(3));
        assertThat(systems.get(2).coordinates, samePropertyValuesAs(new Coordinates(1, 1)));
        assertThat(systems.get(2).name, stringContainsInOrder("baz"));
        assertThat(systems.get(2).owner, samePropertyValuesAs(writeNation));
        assertThat(systems.get(2).id, equalTo(3));
    }

    @Test
    void getOwnedBy() throws SQLException {
        var starSystem1 = new StarSystem(new Coordinates(0, 0), "foo", null, -1);
        var starSystem2 = new StarSystem(new Coordinates(0, 1), "bar", null, -1);

        dataAccess.starSystemDAO.insert(starSystem1);
        dataAccess.starSystemDAO.insert(starSystem2);

        Nation writeNation1;
        {
            var star = new Star(dataAccess.starTypeDAO.random(), "foo", starSystem1, 5, -1);
            dataAccess.starDAO.insert(star);

            var name = "Nation name";
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

            var leader = new Leader(-1, leaderName);
            dataAccess.leaderDAO.insert(leader);

            var species = new Species(-1, speciesName);
            dataAccess.speciesDAO.insert(species);

            var capital = new Planet(planetName, planetType, resources, population, development, size, star, true, -1);
            dataAccess.planetDAO.insert(capital);

            var government = dataAccess.governmentDAO.getById(governmentId);
            var economy = dataAccess.economicDAO.getById(economicId);
            String ownerID = "-1";

            writeNation1 = new Nation(name, leader, government, economy, species, population, stability, centralization, approval, capital, -1, ownerID);
            dataAccess.nationDAO.insert(writeNation1);

            dataAccess.planetDAO.setOwner(capital, writeNation1);
        }

        Nation writeNation2;
        {
            var star = new Star(dataAccess.starTypeDAO.random(), "foo", starSystem2, 5, -1);
            dataAccess.starDAO.insert(star);

            var name = "Nation name";
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

            var leader = new Leader(-1, leaderName);
            dataAccess.leaderDAO.insert(leader);

            var species = new Species(-1, speciesName);
            dataAccess.speciesDAO.insert(species);

            var capital = new Planet(planetName, planetType, resources, population, development, size, star, true, -1);
            dataAccess.planetDAO.insert(capital);

            var government = dataAccess.governmentDAO.getById(governmentId);
            var economy = dataAccess.economicDAO.getById(economicId);
            String ownerID = "-1";

            writeNation2 = new Nation(name, leader, government, economy, species, population, stability, centralization, approval, capital, -1, ownerID);
            dataAccess.nationDAO.insert(writeNation2);

            dataAccess.planetDAO.setOwner(capital, writeNation2);
        }

        var starSystem3 = new StarSystem(new Coordinates(1, 1), "baz", writeNation2, -1);
        dataAccess.starSystemDAO.insert(starSystem3);

        var ownedByNation1 = dataAccess.starSystemDAO.getOwnedBy(writeNation1);
        var ownedByNation2 = dataAccess.starSystemDAO.getOwnedBy(writeNation2);

        assertThat(ownedByNation1, hasSize(1));
        assertThat(ownedByNation1.get(0), samePropertyValuesAs(starSystem1));

        assertThat(ownedByNation2, hasSize(2));
        assertThat(ownedByNation2.get(0), samePropertyValuesAs(starSystem2));
        assertThat(ownedByNation2.get(1), samePropertyValuesAs(starSystem3));
    }

    @Test
    void setOwner() throws SQLException {
        var starSystem1 = new StarSystem(new Coordinates(0, 0), "foo", null, -1);
        dataAccess.starSystemDAO.insert(starSystem1);

        Nation writeNation1;
        {
            var star = new Star(dataAccess.starTypeDAO.random(), "foo", starSystem1, 5, -1);
            dataAccess.starDAO.insert(star);

            var name = "Nation name";
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

            var leader = new Leader(-1, leaderName);
            dataAccess.leaderDAO.insert(leader);

            var species = new Species(-1, speciesName);
            dataAccess.speciesDAO.insert(species);

            var capital = new Planet(planetName, planetType, resources, population, development, size, star, true, -1);
            dataAccess.planetDAO.insert(capital);

            var government = dataAccess.governmentDAO.getById(governmentId);
            var economy = dataAccess.economicDAO.getById(economicId);
            String ownerID = "-1";

            writeNation1 = new Nation(name, leader, government, economy, species, population, stability, centralization, approval, capital, -1, ownerID);
            dataAccess.nationDAO.insert(writeNation1);

            dataAccess.planetDAO.setOwner(capital, writeNation1);
        }
        var starSystem2 = new StarSystem(new Coordinates(0, 1), "bar", null, -1);
        dataAccess.starSystemDAO.insert(starSystem2);
        dataAccess.starSystemDAO.setOwner(starSystem2, writeNation1);

        var ownedSystems = dataAccess.starSystemDAO.getOwnedBy(writeNation1);
        assertThat(ownedSystems,hasSize(2));
        assertThat(ownedSystems.get(1), samePropertyValuesAs(starSystem2));
    }
}
