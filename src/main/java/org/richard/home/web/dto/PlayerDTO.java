package org.richard.home.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayerDTO {

    @NotBlank(message = "name cannot be null or empty")
    private String name;
    @Min(value = 12, message = "age must be higher or at least 12")
    private int age;
    @NotBlank
    private String position;
    private LocalDate dateOfBirth;
    private Country countryOfBirth;

    public PlayerDTO() {
    }

    public PlayerDTO(String name, int age, String position, LocalDate dateOfBirth, Country countryOfBirth) {
        this.name = name;
        this.age = age;
        this.position = position;
        this.dateOfBirth = dateOfBirth;
        this.countryOfBirth = countryOfBirth;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
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

    public Country getCountryOfBirth() {
        return countryOfBirth;
    }

    public void setCountryOfBirth(Country countryOfBirth) {
        this.countryOfBirth = countryOfBirth;
    }

    @Override
    public String toString() {
        return "PlayerDTO{" +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", position='" + position + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", countryOfBirth=" + countryOfBirth +
                '}';
    }
}
