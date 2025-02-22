package org.richard.home.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.DynamicUpdate;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@DynamicUpdate
@Entity
@Table(name = "teams")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TeamsIdGenerator")
    @SequenceGenerator(allocationSize = 1, sequenceName = "teams_seq", name = "TeamsIdGenerator")
    private int id;
    private String name;
    private int budget;
    private String logo;
    private String tla;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinTable(name = "plays_in", joinColumns = @JoinColumn(name = "teamId"), inverseJoinColumns = @JoinColumn(name = "addressId"))
    private Address address;

    private String phone;
    private String email;
    private String venue;
    private String website;
    private String owner;
    private int wyId;

    @ManyToOne
    @JoinColumn(name = "league_id")
    private League league;

    @Transient
    private JsonNode squad;

    public Team() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBudget() {
        return budget;
    }

    public void setBudget(int budget) {
        this.budget = budget;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    @JsonProperty("founded")
    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getTla() {
        return tla;
    }

    public void setTla(String tla) {
        this.tla = tla;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

//    public JsonNode getSquad() {
//        return squad;
//    }

//    public void setSquad(JsonNode squad) {
//        this.squad = squad;
//    }

    public int getWyId() {
        return wyId;
    }

    public void setWyId(int wyId) {
        this.wyId = wyId;
    }

    public League getLeague() {
        return league;
    }

    public void setLeague(League league) {
        this.league = league;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Team team)) return false;
        return id == team.id;
    }


    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Team{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", budget=" + budget +
                ", logo='" + logo + '\'' +
                ", tla='" + tla + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", venue='" + venue + '\'' +
                ", website='" + website + '\'' +
                ", owner='" + owner + '\'' +
                ", wyId=" + wyId +
                ", league=" + league +
                ", squad=" + squad +
                '}';
    }
}
