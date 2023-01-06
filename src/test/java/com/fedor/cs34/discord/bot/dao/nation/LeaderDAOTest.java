package com.fedor.cs34.discord.bot.dao.nation;

import com.fedor.cs34.discord.bot.dao.AbstractDAOTest;
import com.fedor.cs34.discord.bot.data.nation.Leader;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LeaderDAOTest extends AbstractDAOTest {
    @Test
    void insert() throws SQLException {
        var name = "Foo Bar";
        dataAccess.leaderDAO.insert(new Leader(-1, name));
        var leader = dataAccess.leaderDAO.getById(1);
        assertThat(leader.getId(), equalTo(1));
        assertThat(leader.getName(), equalTo(name));
    }

    @Test
    void getAll() throws SQLException {
        dataAccess.leaderDAO.insert(new Leader(-1, "Foo"));
        dataAccess.leaderDAO.insert(new Leader(-1, "Bar"));
        dataAccess.leaderDAO.insert(new Leader(-1, "Baz"));

        var leaders = dataAccess.leaderDAO.getAll();
        assertThat(leaders, hasSize(3));
        assertThat(leaders, hasItems(
                samePropertyValuesAs(new Leader(2, "Bar"))
        ));
    }

    @Test
    void getById_whenExists() throws SQLException {
        dataAccess.leaderDAO.insert(new Leader(-1, "Foo"));
        dataAccess.leaderDAO.insert(new Leader(-1, "Bar"));
        dataAccess.leaderDAO.insert(new Leader(-1, "Baz"));

        var id = 3;
        var leader = dataAccess.leaderDAO.getById(id);
        assertThat(leader.getId(), equalTo(id));
        assertThat(leader.getName(), equalTo("Baz"));
    }

    @Test
    void getById_whenNotExists() {
        assertThrows(IllegalArgumentException.class, () -> dataAccess.leaderDAO.getById(42));
    }
}
