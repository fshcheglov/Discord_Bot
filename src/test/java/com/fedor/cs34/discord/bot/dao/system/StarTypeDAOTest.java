package com.fedor.cs34.discord.bot.dao.system;

import com.fedor.cs34.discord.bot.dao.AbstractDAOTest;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


public class StarTypeDAOTest extends AbstractDAOTest {

    @Test
    void getAll() throws SQLException {
        var stars = dataAccess.starTypeDAO.getAll();
        assertThat(stars, hasSize(6));
        var starType3 = stars.get(2);
        assertThat(starType3.name, equalTo("A-Class White Star"));
        assertThat(starType3.mapColor, equalTo(0xFFFFFF));
    }

    @Test
    void getByID() throws SQLException {
        var starType2 = dataAccess.starTypeDAO.getById(2);
        assertThat(starType2.name, equalTo("B-Class Blue Giant"));
        assertThat(starType2.mapColor, equalTo(0x0000FF));
    }

    @Test
    void count() throws SQLException {
        var count = dataAccess.starTypeDAO.count();
        assertThat(count, equalTo(6));
    }

    @Test
    void getByName() throws SQLException {
        var name = "K-Class Orange Dwarf";
        var star = dataAccess.starTypeDAO.getByName(name);

        assertThat(star.name, equalTo(name));
    }
}
