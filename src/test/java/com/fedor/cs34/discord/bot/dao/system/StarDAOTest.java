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

public class StarDAOTest extends AbstractDAOTest {
    @Test
    void getAll() throws SQLException {
        var randomStarTypeOne = dataAccess.starTypeDAO.random();
        var randomStarTypeTwo = dataAccess.starTypeDAO.random();
        var randomStarTypeThree = dataAccess.starTypeDAO.random();


        var system1 = new StarSystem(new Coordinates(0, 0), "foo", null, -1);
        dataAccess.starSystemDAO.insert(system1);
        dataAccess.starDAO.insert(new Star(randomStarTypeOne, "foo", system1, 5, -1));

        var system2 = new StarSystem(new Coordinates(0, 1), "bar", null, -1);
        dataAccess.starSystemDAO.insert(system2);
        dataAccess.starDAO.insert(new Star(randomStarTypeTwo, "bar", system2, 3, -1));

        var system3 = new StarSystem(new Coordinates(1, 0), "baz", null, -1);
        dataAccess.starSystemDAO.insert(system3);
        dataAccess.starDAO.insert(new Star(randomStarTypeThree, "baz", system3, 8, -1));

        var stars = dataAccess.starDAO.getAll();
        assertThat(stars, hasSize(3));
        assertThat(stars, hasItems(
                samePropertyValuesAs(new Star(randomStarTypeTwo, "bar", new StarSystem(new Coordinates(0, 1), "bar", null, -1), 3, 2))
        ));
    }

    @Test
    void randomWithNation() throws SQLException {
        var system1 = new StarSystem(new Coordinates(0, 0), "foo", null, -1);
        dataAccess.starSystemDAO.insert(system1);
        var star = new Star(dataAccess.starTypeDAO.random(), "foo", system1, 5, -1);
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

        var writeNation = new Nation(name, leader, government, economy, species, population, stability, centralization, approval, capital, -1, ownerID);
        dataAccess.nationDAO.insert(writeNation);

        dataAccess.planetDAO.setOwner(capital, writeNation);

        var random = dataAccess.starDAO.random(1);
        var idStar = dataAccess.starDAO.getById(1);
        assertThat(random, samePropertyValuesAs(idStar));
    }


}
