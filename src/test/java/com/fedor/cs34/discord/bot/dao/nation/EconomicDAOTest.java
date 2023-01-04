package com.fedor.cs34.discord.bot.dao.nation;

import com.fedor.cs34.discord.bot.dao.AbstractDOATest;
import com.fedor.cs34.discord.bot.data.nation.EconomicType;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EconomicDAOTest extends AbstractDOATest {
    @Test
    void getAll() throws SQLException {
        var elements = dataAccess.economicDAO.getAll();
        assertThat(elements, hasSize(5));
        assertThat(elements, hasItems(
                samePropertyValuesAs(new EconomicType(1, "Free Market Capitalism"))
        ));
    }

    @Test
    void getById_whenExists() throws SQLException {
        var economicType = dataAccess.economicDAO.getById(2);
        assertEquals(economicType.getId(), 2);
        assertEquals(economicType.getName(), "Free Market Socialism");
    }

    @Test
    void getById_whenNotExists() {
        assertThrows(IllegalArgumentException.class, () -> dataAccess.economicDAO.getById(42));
    }

    @Test
    void getByName_whenExists() throws SQLException {
        var name = "Free Market Socialism";
        var economicType = dataAccess.economicDAO.getByName(name);
        assertEquals(economicType.getId(), 2);
        assertEquals(economicType.getName(), name);
    }

    @Test
    void getByName_whenNotExists() {
        assertThrows(IllegalArgumentException.class, () -> dataAccess.economicDAO.getByName("foo"));
    }
}