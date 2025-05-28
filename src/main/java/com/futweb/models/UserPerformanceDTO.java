package com.futweb.models;

public class UserPerformanceDTO {
    private Integer userId;
    private Long totalAcertos;
    private Long totalErros;

    public UserPerformanceDTO(Integer userId, Long totalAcertos, Long totalErros) {
        this.userId = userId;
        this.totalAcertos = totalAcertos;
        this.totalErros = totalErros;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Long getTotalAcertos() {
        return totalAcertos;
    }

    public void setTotalAcertos(Long totalAcertos) {
        this.totalAcertos = totalAcertos;
    }

    public Long getTotalErros() {
        return totalErros;
    }

    public void setTotalErros(Long totalErros) {
        this.totalErros = totalErros;
    }
}

