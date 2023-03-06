package com.example.bank.entities;

import com.example.bank.interceptor.RequestInterceptor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;
import jakarta.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

@Entity
@Table(name = "account")
public class Account {

    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name= "increment", strategy= "increment")
    @Column(name = "id", length = 6, nullable = false)
    @JsonIgnore
    private long id;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "name")
    private String name;

    @Column(name = "status")
    private String status;
    @Column(name = "user_id")
    private int user_id;

    @Column(name = "bonuscounter")
    private int bonuscounter;
    public Account() {
    }

    public Account(String name, int user_id, BigDecimal amount, String status, int bonuscounter) {
        this.name = name;
        this.amount = amount;
        this.user_id = user_id;
        this.status = status;
        this.bonuscounter = bonuscounter;
    }
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getUserId() {
        return user_id;
    }
    public void setUser(int user_id) {
        this.user_id = user_id;
    }
    public int getBonusCounter() {
        return bonuscounter;
    }
    public void setBonusCounter(int bonuscounter) {
        this.bonuscounter = bonuscounter;
    }
}

