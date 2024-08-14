package org.richard.home.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.richard.home.config.StaticApplicationConfiguration;
import org.richard.home.infrastructure.ValidAddress;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TeamDto {

    @NotBlank(message = "name cannot be null or empty")
    private String name;
    @Min(value = 0, message = "budget must be higher than 0")
    private Integer budget;
    @NotBlank(message = "logo cannot be null or empty")
    private String logo;
    @NotBlank(message = "owner cannot be null or empty")
    private String owner;
    private String tla;

    @JsonProperty("address")
    @ValidAddress(message = "address is not valid")
    private AddressDTO address;
    private String phone;
    private String website;
    private String email;
    private String venue; //Heim ort
    private Integer wyId;
    @NotBlank(message = "leagueId cannot be null or empty")
    private String leagueId;

    public TeamDto() {
    }

    public TeamDto(String name, int budget, String logoUrl, String owner, String tla, AddressDTO address, String phone, String website, String email, String venue, int wyId, String leagueId) {
        this.name = name;
        this.budget = budget;
        this.logo = logoUrl;
        this.owner = owner;
        this.tla = tla;
        this.address = address;
        this.phone = phone;
        this.website = website;
        this.email = email;
        this.venue = venue;
        this.wyId = wyId;
        this.leagueId = leagueId;
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


    public AddressDTO getAddress() {
        return address;
    }

    public void setAddress(AddressDTO address) {
        this.address = address;
    }

    public void setAddress(String address) throws JsonProcessingException {
        this.address = StaticApplicationConfiguration.OBJECT_MAPPER.readValue(address, AddressDTO.class);
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
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

    public int getWyId() {
        return wyId;
    }

    public void setWyId(int wyId) {
        this.wyId = wyId;
    }

    public String getLeagueId() {
        return leagueId;
    }

    public void setLeagueId(String leagueId) {
        this.leagueId = leagueId;
    }

    @Override
    public String toString() {
        return "TeamDto{" +
                ", name='" + name + '\'' +
                ", budget=" + budget +
                ", logo='" + logo + '\'' +
                ", owner='" + owner + '\'' +
                ", tla='" + tla + '\'' +
                ", address=" + address +
                ", phone='" + phone + '\'' +
                ", website='" + website + '\'' +
                ", email='" + email + '\'' +
                ", venue='" + venue + '\'' +
                ", wyId=" + wyId +
                ", leagueId='" + leagueId + '\'' +
                '}';
    }
}
