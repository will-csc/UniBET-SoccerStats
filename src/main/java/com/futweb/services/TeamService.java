package com.futweb.services;

import com.futweb.models.Guess;
import com.futweb.models.PerformanceStatsDTO;
import com.futweb.models.Team;
import com.futweb.models.TeamDTO;
import com.futweb.repositories.GuessRepository;
import com.futweb.repositories.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private GuessRepository guessRepository;

    public List<Team> findAll() {
        return teamRepository.findAll();
    }

    public void save(Team team) {
        team.setTeam_image(team.getTeam_image());
        teamRepository.save(team);
    }

    public Optional<Team> findByTeamName(String name) {
        return teamRepository.findByTeamName(name);
    }

    public String saveImage(MultipartFile file) {
        if (file.isEmpty()) return null;

        try {
            // Nome original do arquivo enviado
            String fileName = Paths.get(file.getOriginalFilename()).getFileName().toString();

            // Caminho destino: pasta "uploads/teams" dentro do diretório atual da aplicação
            Path uploadPath = Paths.get(System.getProperty("user.dir"), "uploads", "teams");

            // Cria o diretório se não existir
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Caminho completo do novo arquivo
            Path destinationFile = uploadPath.resolve(fileName).normalize();

            // Salva o arquivo no destino
            file.transferTo(destinationFile.toFile());

            // Retorna o caminho relativo para uso em HTML (exponha esta pasta via ResourceHandler)
            return "/uploads/teams/" + fileName;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void deleteByTeamID(Integer id) {
        teamRepository.deleteById(id);
    }

    public void partialUpdate(Team team,String teamFeeling,Integer tableStanding,
                              Integer totalCompetitions, String motivation){
        if (teamFeeling != null && !teamFeeling.trim().isEmpty()) {
            team.setTeamFeeling(teamFeeling);
        }

        if (tableStanding != null) {
            team.setTableStading(tableStanding);
        }

        if (totalCompetitions != null) {
            team.setTotalCompetitions(totalCompetitions);
        }

        if (motivation != null && !motivation.trim().isEmpty()) {
            team.setMotivation(motivation);
        }
    }

    public List<TeamDTO> getTeamsWithMostGreens() {
        return teamRepository.findTeamsWithMostGreens().stream()
                .sorted((a, b) -> Long.compare(b.getTotalGreens(), a.getTotalGreens()))
                .collect(Collectors.toList());
    }

    public Team findByTeamId(Integer teamId) {
        return teamRepository.findByTeamId(teamId);
    }

    public PerformanceStatsDTO calcularPerformance() {
        List<Guess> allGuesses = guessRepository.findAll();

        long greens = allGuesses.stream()
                .filter(g -> g.getWinnerId() != null && g.getWinnerId().getTeamId().equals(g.getMyTeamId().getTeamId()))
                .count();

        long reds = allGuesses.stream()
                .filter(g -> g.getWinnerId() != null && !g.getWinnerId().getTeamId().equals(g.getMyTeamId().getTeamId()))
                .count();

        long total = greens + reds;
        int percentualAcertos = total > 0 ? (int) ((greens * 100.0) / total) : 0;

        // Lógica de melhor/pior time pode permanecer a mesma, ou ser adaptada se quiser basear por acertos de apostas

        List<Team> allTeams = teamRepository.findAll();
        Team melhor = allTeams.stream()
                .max(Comparator.comparingInt(Team::getStrongPerformance))
                .orElse(null);

        Team pior = allTeams.stream()
                .min(Comparator.comparingInt(Team::getStrongPerformance))
                .orElse(null);

        TeamDTO melhorDTO = melhor != null ? new TeamDTO(
                melhor.getTeamId(),
                melhor.getTeamName(),
                melhor.getTeam_image(),
                melhor.getStrongPerformance().longValue(),
                melhor.getWeakPerformance().longValue()
        ) : null;

        TeamDTO piorDTO = pior != null ? new TeamDTO(
                pior.getTeamId(),
                pior.getTeamName(),
                pior.getTeam_image(),
                pior.getStrongPerformance().longValue(),
                pior.getWeakPerformance().longValue()
        ) : null;

        return new PerformanceStatsDTO(percentualAcertos, melhorDTO, piorDTO);
    }


}
