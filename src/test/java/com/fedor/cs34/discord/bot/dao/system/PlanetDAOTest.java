package com.fedor.cs34.discord.bot.dao.system;

import com.fedor.cs34.discord.bot.dao.AbstractDAOTest;
import com.fedor.cs34.discord.bot.data.system.Coordinates;
import com.fedor.cs34.discord.bot.data.system.Planet;
import com.fedor.cs34.discord.bot.data.system.Star;
import com.fedor.cs34.discord.bot.data.system.StarSystem;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PlanetDAOTest extends AbstractDAOTest {
    @Test
    void getAll() throws SQLException {
        var system = new StarSystem(new Coordinates(0,0), "bar", null, -1);
        dataAccess.starSystemDAO.insert(system);
        Star star = new Star(dataAccess.starTypeDAO.random(), "foo", system,4, -1);
        dataAccess.starDAO.insert(star);

        dataAccess.planetDAO.insert(new Planet("foo", "Rocky world", 10, 100, 1, 11, star, true, -1));;
        dataAccess.planetDAO.insert(new Planet("bar", "Wet world", 15, 30, 7, 16, star, true, -1));

        var planets = dataAccess.planetDAO.getAll();
        assertThat(planets, hasSize(2));
        assertThat(planets, hasItems(
                samePropertyValuesAs(new Planet("bar", "Wet world", 15, 30, 7, 16, star, true, 2))
        ));
    }

    @Test
    void getById_whenExists() throws SQLException {
        var system = new StarSystem(new Coordinates(0,0), "bar", null, -1);
        dataAccess.starSystemDAO.insert(system);
        Star star = new Star(dataAccess.starTypeDAO.random(), "foo", system,4, -1);
        dataAccess.starDAO.insert(star);

        dataAccess.planetDAO.insert(new Planet("foo", "Rocky world", 10, 100, 1, 11, star, true, -1));
        dataAccess.planetDAO.insert(new Planet("bar", "Wet world", 15, 30, 7, 16, star, true, -1));
        dataAccess.planetDAO.insert(new Planet("baz", "Gas Giant", 6, 0, 0, 40, star, false, -1));


        var id = 3;
        var planet = dataAccess.planetDAO.getById(id);
        assertThat(planet.id, equalTo(id));
        assertThat(planet.name, equalTo("baz"));
    }

    @Test
    void getById_whenNotExists() {
        assertThrows(IllegalArgumentException.class, () -> dataAccess.planetDAO.getById(42));
    }
}
