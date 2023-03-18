package com.fedor.cs34.discord.bot.dao.nation;

import com.fedor.cs34.discord.bot.dao.AbstractDAOTest;
import com.fedor.cs34.discord.bot.util.data.nation.ModifierType;
import com.fedor.cs34.discord.bot.util.data.nation.Nation;
import com.fedor.cs34.discord.bot.util.data.nation.NationModifier;
import com.fedor.cs34.discord.bot.util.data.system.Coordinates;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class ModifierDAOTest extends AbstractDAOTest {
    @Test
    void insert() throws SQLException {
        String name = "Foo";
        boolean flat = false;
        ModifierType type = dataAccess.nationModifierTypeDAO.getByName("Population");
        double value = 1.05;
        int duration = 5;
        Nation nation = createNation(new Coordinates(1, 1), "84923742903", "Bar");
        dataAccess.nationDAO.insert(nation);

        var modifier = new NationModifier(-1, name, flat, type, value, duration, nation);
        dataAccess.modifierDAO.insert(modifier);

        var testModifier = dataAccess.modifierDAO.getById(1);

        assertThat(testModifier.name, equalTo(name));
        assertThat(testModifier.isFlatModifier, equalTo(flat));
        assertThat(testModifier.type.type, equalTo(type.type));
        assertThat(testModifier.value, equalTo(value));
        assertThat(testModifier.duration, equalTo(duration));
        assertThat(testModifier.nation.id, equalTo(nation.id));
    }

    @Test
    void getAll() throws SQLException {
        String name = "Foo";
        boolean flat = false;
        ModifierType type = dataAccess.nationModifierTypeDAO.getByName("Population");
        double value = 1.05;
        int duration = 5;
        Nation nation = createNation(new Coordinates(1, 1), "84923742903", "Bar");
        dataAccess.nationDAO.insert(nation);

        var modifier = new NationModifier(-1, name, flat, type, value, duration, nation);
        dataAccess.modifierDAO.insert(modifier);

        String name2 = "Baz";
        boolean flat2 = true;
        ModifierType type2 = dataAccess.nationModifierTypeDAO.getByName("Resource Points");
        double value2 = -20;
        int duration2 = 1;
        Nation nation2 = createNation(new Coordinates(1, 2), "7423987423", "Test Nation");
        dataAccess.nationDAO.insert(nation2);

        var modifier2 = new NationModifier(-1, name2, flat2, type2, value2, duration2, nation2);
        dataAccess.modifierDAO.insert(modifier2);

        var all = dataAccess.modifierDAO.getAll();
        assertThat(all, hasItems(
                samePropertyValuesAs(modifier)
        ));

        assertThat(all, hasItems(
                samePropertyValuesAs(modifier2)
        ));
    }

}
