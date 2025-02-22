package org.richard.home.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "league")
public class League {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LeagueIdGenerator")
    @SequenceGenerator(name = "LeagueIdGenerator", sequenceName = "leagues_seq", allocationSize = 1)
    private int id;
    private String code;
    private String name;

    public League() {
    }

    public League(int id, String code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof League league)) return false;
        return id == league.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "League{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", name=" + name +
                '}';
    }
}
