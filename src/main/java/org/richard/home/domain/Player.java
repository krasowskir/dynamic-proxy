package org.richard.home.domain;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "players")
@DynamicUpdate
public class Player {

    @Id
    @SequenceGenerator(name = "PlayerIdGenerator", sequenceName = "players_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PlayerIdGenerator")
    private Integer id;

    @Column
    private String name;
    private Integer alter;
    private String position;
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;
    //    @Embedded
    @Column(name = "country_of_birth")
    @Enumerated(EnumType.STRING)
    private Country countryOfBirth;

    public Player() {
    }

    public Player(String name, Integer alter, String position, LocalDate dateOfBirth, Country countryOfBirth) {
        this.name = name;
        this.alter = alter;
        this.position = position;
        this.dateOfBirth = dateOfBirth;
        this.countryOfBirth = countryOfBirth;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAlter() {
        return alter;
    }

    public void setAlter(Integer alter) {
        this.alter = alter;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getCountryOfBirth() {
        return countryOfBirth.getValue();
    }

    public void setCountryOfBirth(String countryOfBirth) {
        this.countryOfBirth = Country.valueOf(countryOfBirth);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Player player)) return false;
        return id.equals(player.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", alter=" + alter +
                ", position='" + position + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", countryOfBirth='" + countryOfBirth + '\'' +
                '}';
    }
}
