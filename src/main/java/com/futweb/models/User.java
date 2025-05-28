package com.futweb.models;

import jakarta.persistence.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private Integer userId;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "email", nullable = false, unique = true, length = 50)
    private String email;

    @Column(name = "user_password", nullable = false, length = 12)
    private String userPassword;

    @Column(name = "retrieve_code", nullable = false, length = 12)
    private String retrieveCode;

    @Column(name = "rank_code", nullable = false, length = 12)
    private String rankCode;

    @Column(name = "user_rank", columnDefinition = "INT DEFAULT 1" , nullable = false)
    private Integer user_rank;

    @Column(name = "user_image", length = 100)
    private String userImage;

    public String getRetrieveCode() {
        return retrieveCode;
    }

    public void setRetrieveCode(String retrieveCode) {
        this.retrieveCode = retrieveCode;
    }

    public String getRankCode() {
        return rankCode;
    }

    public void setRankCode() {
        this.rankCode = newCode();
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getretrieveCode() {
        return retrieveCode;
    }

    public Integer getUser_rank() {
        return user_rank;
    }

    public void setUser_rank(Integer user_rank) {
        this.user_rank = user_rank;
    }

    public boolean verifyPassword(){
        if(getUserPassword().length() > 12){
            return false;
        }
        return true;
    }

    public void setRetrieveCode() {
        this.retrieveCode = newCode();
    }

    public String newCode(){
        char[] special_char = {'!', '@', '#', '$', '%', '&', '*', '(', ')'};
        char[] uppercase_letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        char[] lowercase_letters = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        char[] numbers = "0123456789".toCharArray();

        String allChars = new String(uppercase_letters) +
                new String(lowercase_letters) +
                new String(numbers) +
                new String(special_char);
        char[] allCharacters = allChars.toCharArray();

        Random random = new Random();
        StringBuilder Code = new StringBuilder();

        for (int i = 0; i < 12; i++) {
            int index = random.nextInt(allCharacters.length);
            Code.append(allCharacters[index]);
        }

        return Code.toString();
    }

    public void sendResetEmail() {
        final String retrieveCode = getretrieveCode();
        sendCode(retrieveCode);
    }

    public void sendRankCode() {
        final String retrieveCode = getRankCode();
        sendCode(retrieveCode);
    }

    private void sendCode(String retrieveCode){
        File script = new File("src/main/java/com/futweb/services/sendcode.py");
        String pythonScriptPath = script.getAbsolutePath();

        try {
            // Construindo o comando
            ProcessBuilder pb = new ProcessBuilder(
                    "python", // ou "python3" dependendo do sistema
                    pythonScriptPath,
                    getEmail(),
                    retrieveCode
            );

            // Redirecionar erros e saídas para o console
            pb.inheritIO();

            // Iniciar o processo
            Process process = pb.start();

            // Esperar o processo terminar
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("Email enviado com sucesso!");
            } else {
                System.err.println("Erro ao enviar e-mail. Código de saída: " + exitCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}