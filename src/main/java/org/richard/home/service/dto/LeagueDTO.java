package org.richard.home.service.dto;

import jakarta.validation.constraints.NotBlank;

public class LeagueDTO {

    @NotBlank(message = "name cannot be null or empty")
    private String name;
    @NotBlank(message = "code cannot be null or empty")
    private String code;

    public LeagueDTO() {
    }

    public LeagueDTO(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "LeagueDTO{" +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
