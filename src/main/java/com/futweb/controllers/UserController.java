package com.futweb.controllers;

import com.futweb.models.TeamDTO;
import com.futweb.services.GuessService;
import com.futweb.services.TeamService;
import com.futweb.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.futweb.models.User;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private GuessService guessService;

    @GetMapping("/home")
    public String showHome(@RequestParam(required = false) String email,
                           @RequestParam(required = false) String user_password,
                           Model model, HttpSession session) {
        // Verifica se há alertas
        Object alerta = session.getAttribute("alerta");
        if (alerta != null) {
            model.addAttribute("alerta", alerta);
            session.removeAttribute("alerta"); // evitar repetir
        }

        // Se já estiver logado, apenas mostra a home
        if (session.getAttribute("userId") != null) {
            model.addAttribute("userName", "Olá " + session.getAttribute("userName") + "!");
            model.addAttribute("userImage", session.getAttribute("userImage"));
            return "home";
        }

        // Se os parâmetros não foram passados (acesso direto pela URL)
        if (email == null || user_password == null) {
            return "redirect:/login";
        }

        Optional<User> userOpt = userService.authenticate(email, user_password);

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // Adiciona Variaveis principais do usuario à sessão
            session.setAttribute("userName", user.getUsername());
            session.setAttribute("userImage", user.getUserImage());
            model.addAttribute("userName", "Olá " + user.getUsername() + "!");
            model.addAttribute("userImage", user.getUserImage());
            session.setAttribute("userId", user.getUserId());
            session.setAttribute("userRank", user.getUser_rank());

            return "home";
        } else {
            model.addAttribute("loginError", "Email ou senha incorretos.");
            return "login";
        }
    }

    @GetMapping("/home-redirect")
    public String returnHome(HttpSession session, Model model){
        Object userId = session.getAttribute("userId");
        Object alerta = session.getAttribute("alerta");

        if (alerta != null) {
            model.addAttribute("alerta", alerta);
            session.removeAttribute("alerta"); // evitar repetir
        }
        if (userId != null && userService.verifySession((Integer) userId)){
            return "home";
        }
        return "login";
    }

    @PostMapping("/login")
    public String insertUser(User user, Model model) {
        return userService.registerUser(user, model);
    }

    @PostMapping("/reset-password")
    public String updatePassword(@RequestParam("retrieveCode") String retrieveCode,
                                 @RequestParam("userPassword") String userPassword,
                                 Model model) {
        return userService.resetPassword(retrieveCode, userPassword, model);
    }

    @GetMapping("/forget")
    public String sendCode(@RequestParam String email, Model model) {
        return userService.sendResetCode(email, model);
    }

    @GetMapping("/api/performance")
    @ResponseBody
    public List<TeamDTO> getTopWinners() {
        return teamService.getTeamsWithMostGreens();
    }

    @PostMapping("/api/generate-dashboard")
    @ResponseBody
    public ResponseEntity<String> generateDashboard(@RequestParam Integer acertos,
                                                    @RequestParam Integer erros,
                                                    HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        try {
            File script = new File("src/main/java/com/futweb/services/dashboard.py");
            String pythonScriptPath = script.getAbsolutePath();

            ProcessBuilder pb = new ProcessBuilder(
                    "python", // ou "python3" dependendo do sistema
                    pythonScriptPath,
                    acertos.toString(),
                    erros.toString(),
                    userId.toString()
            );
            pb.inheritIO();

            Process process = pb.start();

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                return ResponseEntity.ok("Dashboard gerado com sucesso.");
            } else {
                return ResponseEntity.status(500).body("Erro ao gerar dashboard");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erro ao executar script Python");
        }
    }

    @GetMapping("/api/performancestats")
    public ResponseEntity<?> getPerformance(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        return guessService.findPerformanceByUserId(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/logout")
    public String logginOut(HttpSession session) {
        session.invalidate();  // encerra a sessão
        return "redirect:/login";  // redireciona para página de login
    }

    @GetMapping("/settings")
    public String showSettings(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null && userService.verifySession(userId)){
            return "login";
        }
        return "settings";
    }

    @PutMapping(value = "/api/users/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateUser(
            @PathVariable Integer id,
            @RequestPart(name = "username", required = false) String username,
            @RequestPart(name = "email", required = false) String email,
            @RequestPart(name = "userPassword", required = false) String userPassword,
            @RequestPart(value = "userImage", required = false) MultipartFile userImage,
            HttpSession session) {

        boolean emailAndNameExists = userService.verifyExistEmailandName(username, email);
        if (emailAndNameExists) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário já existe, insira nome ou email diferentes");
        }

        Optional<User> optionalUser = userService.findUserById(id);
        if (optionalUser.isEmpty()) return ResponseEntity.notFound().build();

        User user = optionalUser.get();

        // Atualizar os dados que vierem preenchidos
        if (username != null && !username.trim().isEmpty()) {
            user.setUsername(username);
        }
        if (email != null && !email.trim().isEmpty()) {
            user.setEmail(email);
        }
        if (userPassword != null && !userPassword.trim().isEmpty()) {
            user.setUserPassword(userPassword);
        }

        // Salvar imagem
        if (userImage != null && !userImage.isEmpty()) {
            try {
                String imageName = UUID.randomUUID() + "_" + userImage.getOriginalFilename();
                Path uploadPath = Paths.get("uploads", "profile");

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                Path imagePath = uploadPath.resolve(imageName);
                Files.copy(userImage.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);

                System.out.println("Imagem salva em: " + imagePath);
                user.setUserImage(imageName);
            } catch (IOException e) {
                e.printStackTrace(); // <-- imprime o erro no console
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao salvar a imagem: " + e.getMessage());
            }
        }

        // Salva no banco
        userService.updateUser(user);

        // Atualiza a sessão
        session.setAttribute("userName", user.getUsername());
        session.setAttribute("userImage", user.getUserImage());

        return ResponseEntity.ok("Usuário atualizado com sucesso.");
    }

    @PostMapping("/api/send-code/{userId}")
    public ResponseEntity<String> sendCode(@PathVariable Integer userId) {
        Optional<User> optUser = userService.findUserById(userId);

        if(optUser.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não existe");
        }

        User user = optUser.get();
        user.sendRankCode();

        return ResponseEntity.ok("Código enviado!");
    }

    @DeleteMapping("/api/users/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Integer userId) {
        userService.deleteUserById(userId);
        return ResponseEntity.ok("Usuário deletado com sucesso.");
    }

    @PostMapping("/api/validate-rank/{userId}")
    public ResponseEntity<String> validateRankCode(
            @PathVariable Integer userId,
            @RequestBody Map<String, String> payload,
            HttpSession session) {

        Optional<User> optUser = userService.findUserById(userId);
        if (optUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não existe");
        }

        User user = optUser.get();
        String rankCode = payload.get("rankCode");

        if (rankCode == null || !rankCode.equals(user.getRankCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Código inválido");
        }

        // Atualiza o rank (exemplo: aumenta 1, max 3)
        user.setRankCode();
        user.setUser_rank(2);
        session.setAttribute("userRank", user.getUser_rank());

        userService.updateUser(user);

        return ResponseEntity.ok("Rank atualizado com sucesso!");
    }

    @PostMapping("/make-dashboard-performance")
    public ResponseEntity<?> makePerformanceDashboard(HttpSession session) {
        try {
            Integer userId = (Integer) session.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(401).body("Usuário não autenticado");
            }

            File script = new File("src/main/java/com/futweb/services/performance.py");
            String pythonScriptPath = script.getAbsolutePath();

            ProcessBuilder pb = new ProcessBuilder(
                    "python", // ou "python3"
                    pythonScriptPath,
                    userId.toString()
            );
            pb.inheritIO();

            Process process = pb.start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                // Retorne o nome da imagem gerada
                String fileName = "performance_" + userId + ".png";
                return ResponseEntity.ok(fileName);
            } else {
                return ResponseEntity.status(404).body("Usuário sem apostas.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao executar o script");
        }
    }


}
