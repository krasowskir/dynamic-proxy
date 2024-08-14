package org.richard.home.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;


public class AddressDTO {

    @JsonProperty("city")
    private String city;
    @JsonProperty("street")
    private String street;
    @JsonProperty("plz")
    private String plz;

    @JsonProperty("houseNumber")
    private int houseNumber;

    @JsonProperty("country")
    private Country country;

    public AddressDTO() {
    }

    public AddressDTO(String city, String street, String plz, int houseNumber, Country country) {
        this.city = city;
        this.street = street;
        this.plz = plz;
        this.houseNumber = houseNumber;
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getPlz() {
        return plz;
    }

    public void setPlz(String plz) {
        this.plz = plz;
    }

    public int getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(int houseNumber) {
        this.houseNumber = houseNumber;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return "AddressDTO{" +
                "city='" + city + '\'' +
                ", street='" + street + '\'' +
                ", plz='" + plz + '\'' +
                ", houseNumber=" + houseNumber +
                ", country=" + country +
                '}';
    }
}
