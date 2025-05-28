package com.futweb.services;

import com.futweb.models.User;
import com.futweb.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Optional<User> authenticate(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (password.equals(user.getUserPassword())) {
                return Optional.of(user); // Login válido
            }
        }

        return Optional.empty(); // Login inválido
    }

    public boolean verifyExistEmailandName(String email, String name){
        boolean emailExistente = userRepository.existsByEmail(email);
        boolean nomeExistente = userRepository.existsByUsername(name);

        // Validações
        if (emailExistente || nomeExistente) {
            return true;
        }
        return false;
    }

    public String registerUser(User user, Model model) {
        String email = user.getEmail();
        String nome = user.getUsername();

        // Validações
        if (verifyExistEmailandName(email,nome)) {
            model.addAttribute("signUpError", "Nome ou e-mail já cadastrados.");
            return "signup";
        }

        if (!email.contains("@") || !email.contains(".com")) {
            model.addAttribute("signUpError", "E-mail fora do padrão");
            return "signup";
        }

        if (!user.verifyPassword()) {
            model.addAttribute("signUpError", "Senha muito grande, máximo 12 caracteres");
            return "signup";
        }

        // Configurações padrão
        user.setRetrieveCode();
        user.setRankCode();
        user.setUser_rank(1);

        userRepository.save(user);
        return "redirect:/login";
    }

    public String resetPassword(String retrieveCode, String newPassword, Model model) {
        Optional<User> userOpt = userRepository.findByRetrieveCode(retrieveCode);

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            user.setUserPassword(newPassword);
            user.setRetrieveCode(); // Gera um novo código após redefinir
            userRepository.save(user);

            return "login";
        } else {
            model.addAttribute("forgetError", "Código de recuperação inválido");
            return "forget";
        }
    }

    public String sendResetCode(String email, Model model) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            try {
                user.sendResetEmail();
                model.addAttribute("email", email);
                return "forget";
            } catch (Exception e) {
                model.addAttribute("loginError", "Erro ao enviar o código por e-mail.");
                return "login";
            }
        }

        model.addAttribute("loginError", "O usuário não existe!");
        return "login";
    }

    public boolean verifySession(Integer userId) {
        Optional<User> user = Optional.ofNullable(userRepository.findByUserId(userId));
        return user.isPresent();
    }

    public Optional<User> findUserById(Integer userId) {
        return userRepository.findById(userId);
    }

    public void updateUser(User user){
        userRepository.save(user);
    }

    public void deleteUserById(Integer userId) {
        userRepository.deleteById(userId);
    }
}

