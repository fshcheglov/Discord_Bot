package com.fedor.cs34.discord.bot.dao.nation;

import com.fedor.cs34.discord.bot.dao.AbstractDAOTest;
import com.fedor.cs34.discord.bot.util.data.nation.Leader;
import com.fedor.cs34.discord.bot.util.data.nation.Nation;
import com.fedor.cs34.discord.bot.util.data.nation.Species;
import com.fedor.cs34.discord.bot.util.data.system.Coordinates;
import com.fedor.cs34.discord.bot.util.data.system.Planet;
import com.fedor.cs34.discord.bot.util.data.system.Star;
import com.fedor.cs34.discord.bot.util.data.system.StarSystem;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.sql.SQLException;

public class NationDAOTest extends AbstractDAOTest {
    @Test
    void insert() throws SQLException {
        var system = new StarSystem(new Coordinates(0,0), "bar", null, -1);
        dataAccess.starSystemDAO.insert(system);
        Star star = new Star(dataAccess.starTypeDAO.random(), "foo", system,4, -1);
        dataAccess.starDAO.insert(star);

        var name = "Nation name";
        var nationId = 1;
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
            String ownerID = "-1";

            var writeNation = new Nation(name, leader, government, economy, species, population, stability, centralization, approval, capital, -1, ownerID);
            dataAccess.nationDAO.insert(writeNation);
            assertThat(writeNation.id, equalTo(nationId));
        }

        {
            var nation = dataAccess.nationDAO.getById(nationId);
            assertThat(nation.nationName, equalTo(name));
            assertThat(nation.leader.getName(), equalTo(leaderName));
            assertThat(nation.government.getId(), equalTo(governmentId));
            assertThat(nation.economicType.getId(), equalTo(economicId));
            assertThat(nation.species.getName(), equalTo(speciesName));
            assertThat(nation.population, equalTo(population));
            assertThat(nation.centralization, equalTo(centralization));
            assertThat(nation.approval, equalTo(approval));

            assertThat(nation.capital.name, equalTo(planetName));
            assertThat(nation.capital.type, equalTo(planetType));
            assertThat(nation.capital.resources, equalTo(resources));
            assertThat(nation.capital.population, equalTo(population));
            assertThat(nation.capital.development, equalTo(development));
            assertThat(nation.capital.size, equalTo(size));
            assertThat(nation.capital.isHabitable, equalTo(true));
            assertThat(nation.capital.id, equalTo(1));
            assertThat(nation.capital.star.system.owner, sameInstance(nation));
        }

        {
            var starSystem = dataAccess.starSystemDAO.getById(1);
            assertThat(starSystem.owner.capital.star.system, sameInstance(starSystem));
        }
    }
}
