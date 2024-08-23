package org.richard.home.service.dto;

public class PlayersTeamDTO {

    private String playerId;
    private String teamId;

    public PlayersTeamDTO() {
    }

    public PlayersTeamDTO(String playerId, String teamId) {
        this.playerId = playerId;
        this.teamId = teamId;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    @Override
    public String toString() {
        return "PlayersTeamDTO{" +
                "playerId='" + playerId + '\'' +
                ", teamId='" + teamId + '\'' +
                '}';
    }
}
