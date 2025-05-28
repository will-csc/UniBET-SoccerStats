package com.futweb.controllers;

import com.futweb.models.*;
import com.futweb.services.TeamService;
import com.futweb.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class TeamController {

    @Autowired
    public TeamService teamService;

    @Autowired
    public UserService userService;

    @GetMapping("/teams")
    public String updateGames(HttpSession session) {
        Object userId = session.getAttribute("userId");
        Integer rank = (Integer) session.getAttribute("userRank");

        if(rank < 3){
            session.setAttribute("alerta", "Você não tem permissão para acessar esta página.");
            return "redirect:/home";
        }
        if (userId != null && userService.verifySession((Integer) userId)){
            return "teams";
        }

        return "login";
    }

    @GetMapping("/api/teams")
    @ResponseBody
    public List<Team> getAllTeams() {
        return teamService.findAll();
    }

    @PostMapping("/api/team")
    @ResponseBody
    public ResponseEntity<?> saveTeam(
            @RequestParam("team_name") String teamName,
            @RequestParam(value = "team_feeling", required = false) String teamFeeling,
            @RequestParam(value = "table_standing", required = false) Integer tableStanding,
            @RequestParam(value = "total_competitions", required = false) Integer totalCompetitions,
            @RequestParam(value = "motivation", required = false) String motivation,
            @RequestParam(value = "team_image", required = false) Optional<MultipartFile> teamImage
    ) {
        if (teamName == null || teamName.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("O nome do time é obrigatório.");
        }

        if (teamName.length() > 20) {
            return ResponseEntity.badRequest().body("O nome do time não pode ter mais de 20 caracteres.");
        }

        Optional<Team> existingTeamOpt = teamService.findByTeamName(teamName);
        Team team;

        if (existingTeamOpt.isPresent()) {
            // Atualização parcial
            team = existingTeamOpt.get();
            teamService.partialUpdate(team, teamFeeling, tableStanding, totalCompetitions, motivation);
            if (teamImage.isPresent()) {
                String imagePath = teamService.saveImage(teamImage.get());
                if (imagePath == null) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao salvar imagem.");
                }
                team.setTeam_image(imagePath);
            }
            return ResponseEntity.ok("Partial");
        } else {
            // Novo cadastro: todos os campos obrigatórios
            if (teamFeeling == null || teamFeeling.trim().isEmpty()
                    || tableStanding == null
                    || totalCompetitions == null
                    || motivation == null || motivation.trim().isEmpty()
                    || teamImage == null || teamImage.isEmpty()) {
                return ResponseEntity.badRequest().body("Todos os campos são obrigatórios para novo cadastro.");
            }

            team = new Team();
            team.setTeamName(teamName);
            team.setTeamFeeling(teamFeeling);
            team.setTableStading(tableStanding);
            team.setTotalCompetitions(totalCompetitions);
            team.setMotivation(motivation);

            String imagePath = teamService.saveImage(teamImage.get());
            if (imagePath == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao salvar imagem.");
            }
            team.setTeam_image(imagePath);
        }

        teamService.save(team);
        return ResponseEntity.ok(team);
    }

    @DeleteMapping("/api/team/{name}")
    @ResponseBody
    public ResponseEntity<String> deleteTeam(@PathVariable String name) {

        Optional<Team> Opteam = teamService.findByTeamName(name);

        if (Opteam.isPresent()) {
            Team team = Opteam.get();
            teamService.deleteByTeamID(team.getTeamId());
            return ResponseEntity.ok("Time excluído com sucesso!");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Time não encontrado.");
        }
    }

    @GetMapping("/api/top-winners")
    @ResponseBody
    public List<TeamDTO> getTopWinners() {
        return teamService.getTeamsWithMostGreens();
    }
    
}
