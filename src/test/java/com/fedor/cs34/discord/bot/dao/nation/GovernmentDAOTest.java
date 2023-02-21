package com.fedor.cs34.discord.bot.dao.nation;

import com.fedor.cs34.discord.bot.dao.AbstractDAOTest;
import com.fedor.cs34.discord.bot.util.data.nation.Government;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GovernmentDAOTest extends AbstractDAOTest {
    @Test
    void getAll() throws SQLException {
        var elements = dataAccess.governmentDAO.getAll();
        assertThat(elements, hasSize(10));
        assertThat(elements, hasItems(
                samePropertyValuesAs(new Government(1, "Oligarchic Democracy"))
        ));
    }

    @Test
    void getById_whenExists() throws SQLException {
        int id = 5;
        var element = dataAccess.governmentDAO.getById(id);
        assertThat(element.getId(), equalTo(id));
        assertThat(element.getName(), equalTo("Federation"));
    }

    @Test
    void getById_whenNotExists() {
        assertThrows(IllegalArgumentException.class, () -> dataAccess.governmentDAO.getById(42));
    }

    @Test
    void getByName_whenExists() throws SQLException {
        var name = "Federation";
        var element = dataAccess.governmentDAO.getByName(name);
        assertThat(element.getId(), equalTo(5));
        assertThat(element.getName(), equalTo(name));
    }

    @Test
    void getByName_whenNotExists() {
        assertThrows(IllegalArgumentException.class, () -> dataAccess.governmentDAO.getByName("foo"));
    }
}
