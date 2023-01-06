package com.fedor.cs34.discord.bot.data.nation;

import com.fedor.cs34.discord.bot.data.system.Planet;

public class Nation {
    public String nationName;
    public Leader leader;
    public Species species;

    public Government government;
    public EconomicType economicType;

    public Development development = new Development();

    public int population;

    public double stability;
    public double centralization;
    public double approval;

    public Planet capital;

    public int id;
    public String ownerID;

    public Nation(String nationName, Leader leader, Government government, EconomicType economicType, Species species,
                  int population, double stability, double centralization, double approval, Planet capital,
                  int id, String ownerID) {
        this.nationName = nationName;
        this.leader = leader;
        this.government = government;
        this.economicType = economicType;
        this.species = species;
        this.population = population;
        this.stability = stability;
        this.centralization = centralization;
        this.approval = approval;
        this.capital = capital;
        this.id = id;
        this.ownerID = ownerID;
    }

    public String getNationInfo() {
        return ("**Nation name: ** " + nationName + "\n" +
                "**Leader: ** " + leader.name + "\n" +
                "**Species: ** " + species.name + "\n\n" +

                "**Government: ** " + government.getName() + "\n" +
                "**Economic System:** " + economicType.getName() + "\n\n" +

                "**Resource Points: **" + development.resourcePoints + "\n" +
                "**Economic Points: **" + development.economicPoints + "\n" +
                "**Manpower Points: **" + development.manpowerPoints + "\n\n" +

                "**Population: **" + population + "\n\n" +

                "**Stability: **" + stability + "\n" +
                "**Centralization: **" + centralization + "\n" +
                "**Approval: **" + approval + "\n\n" +

                "**Capital: ** " + capital.name + "\n");

    }

    public String returnNameAndOwner() {
        return ("**Nation name:** " + nationName + " | **Owner:** <@" + ownerID + ">\n");
    }

}