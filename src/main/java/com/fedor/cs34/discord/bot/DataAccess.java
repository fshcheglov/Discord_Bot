package com.fedor.cs34.discord.bot;

import com.fedor.cs34.discord.bot.dao.nation.*;
import com.fedor.cs34.discord.bot.dao.system.PlanetDAO;
import com.fedor.cs34.discord.bot.dao.system.StarDAO;
import com.fedor.cs34.discord.bot.dao.system.StarTypeDAO;
import com.fedor.cs34.discord.bot.dao.system.StarSystemDAO;

import java.sql.Connection;

public class DataAccess {
    public final Connection connection;
    public final EconomicDAO economicDAO;
    public final GovernmentDAO governmentDAO;
    public final LeaderDAO leaderDAO;
    public final NationDAO nationDAO;
    public final SpeciesDAO speciesDAO;
    public final PlanetDAO planetDAO;
    public final StarDAO starDAO;
    public final StarTypeDAO starTypeDAO;
    public final StarSystemDAO starSystemDAO;
    public final ModifierDAO modifierDAO;
    public final NationModifierTypeDAO nationModifierTypeDAO;


    public DataAccess(Connection connection) {
        this.connection = connection;
        this.economicDAO = new EconomicDAO(this);
        this.governmentDAO = new GovernmentDAO(this);
        this.leaderDAO = new LeaderDAO(this);
        this.nationDAO = new NationDAO(this);
        this.speciesDAO = new SpeciesDAO(this);
        this.planetDAO = new PlanetDAO(this);
        this.starDAO = new StarDAO(this);
        this.starTypeDAO = new StarTypeDAO(this);
        this.starSystemDAO = new StarSystemDAO(this);
        this.modifierDAO = new ModifierDAO(this);
        this.nationModifierTypeDAO = new NationModifierTypeDAO(this);
    }
}
