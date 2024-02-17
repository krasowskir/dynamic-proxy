package org.richard.home.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class Trainer {

    @Id
    private int wyid;
    private String name;
    private String country;
    private String birthDate;

    public Trainer() {
    }

    public Trainer(int wyid, String name, String country, String birthDate) {
        this.wyid = wyid;
        this.name = name;
        this.country = country;
        this.birthDate = birthDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public int getWyid() {
        return wyid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Trainer)) return false;
        Trainer trainer = (Trainer) o;
        return Objects.equals(wyid, trainer.wyid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(wyid);
    }

    @Override
    public String toString() {
        return "Trainer{" +
                "wyid='" + wyid + '\'' +
                ", name='" + name + '\'' +
                ", country='" + country + '\'' +
                ", birthDate='" + birthDate + '\'' +
                '}';
    }
}
