package com.fedor.cs34.discord.bot.dao.nation;

import com.fedor.cs34.discord.bot.dao.AbstractDAOTest;
import com.fedor.cs34.discord.bot.data.nation.Species;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SpeciesDAOTest extends AbstractDAOTest {
    @Test
    void insert() throws SQLException {
        var name = "Foo Bar";
        dataAccess.speciesDAO.insert(new Species(-1, name));
        var species = dataAccess.speciesDAO.getById(1);
        assertThat(species.getId(), equalTo(1));
        assertThat(species.getName(), equalTo(name));
    }

    @Test
    void getAll() throws SQLException {
        dataAccess.speciesDAO.insert(new Species(-1, "Foo"));
        dataAccess.speciesDAO.insert(new Species(-1, "Bar"));
        dataAccess.speciesDAO.insert(new Species(-1, "Baz"));

        var species = dataAccess.speciesDAO.getAll();
        assertThat(species, hasSize(3));
        assertThat(species, hasItems(
                samePropertyValuesAs(new Species(2, "Bar"))
        ));
    }

    @Test
    void getById_whenExists() throws SQLException {
        dataAccess.speciesDAO.insert(new Species(-1, "Foo"));
        dataAccess.speciesDAO.insert(new Species(-1, "Bar"));
        dataAccess.speciesDAO.insert(new Species(-1, "Baz"));

        var id = 3;
        var species = dataAccess.speciesDAO.getById(id);
        assertThat(species.getId(), equalTo(id));
        assertThat(species.getName(), equalTo("Baz"));
    }

    @Test
    void getById_whenNotExists() {
        assertThrows(IllegalArgumentException.class, () -> dataAccess.speciesDAO.getById(42));
    }
}
