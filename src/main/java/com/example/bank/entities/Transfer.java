package com.example.bank.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
@NoArgsConstructor
//@AllArgsConstructor
@Table(name = "transfer")
public class Transfer {
    public Transfer(BigDecimal amount, String accountname, String status, int userid, String recorddate, String operationtype) {
        this.amount = amount;
        this.accountname = accountname;
        this.status = status;
        this.userid = userid;
        this.recorddate = recorddate;
        this.operationtype = operationtype;
    }
    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    public String getAccountname() {
        return accountname;
    }
    public void setAccountname(String accountname) {
        this.accountname = accountname;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public int getUserid() {return userid;}
    public void setUserid(int userid) {
        this.userid = userid;
    }
    public String getRecorddate() {
        return recorddate;
    }
    public void setRecorddate(String recorddate) {
        this.recorddate = recorddate;
    }
    public String getOperationtype() {
        return operationtype;
    }
    public void setOperationtype(String operationtype) {
        this.operationtype = operationtype;
    }

    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    @Column(name = "id", length = 6, nullable = false)
    @JsonIgnore
    private long id;

    @JsonProperty("amount")
    @Column(name = "amount")
    private BigDecimal amount;

    @JsonProperty("accountname")
    @Column(name = "accountname")
    private String accountname;

    @JsonProperty("status")
    @Column(name = "status")
    private String status;

    @JsonProperty("userid")
    @Column(name = "userid")
    private int userid;

    @JsonProperty("recorddate")
    @Column(name = "recorddate")
    private String recorddate;

    @JsonProperty("operationtype")
    @Column(name = "operationtype")
    private String operationtype;


}