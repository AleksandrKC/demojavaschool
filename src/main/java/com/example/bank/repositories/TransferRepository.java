package com.example.bank.repositories;

import com.example.bank.entities.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {

    @Query("select a from Transfer a where a.accountname = ?1 and a.userid = ?2")
    @Nullable
    List<Transfer> getTransferByAccountNameAndUserId(String accountname, long userid);

    @Modifying
    @Query(value="insert into transfer(id, amount, accountname, userid, status, recorddate, operationtype) values ( TRANSFER_SEQ_CUSTOM.NEXTVAL, :amount, :accountname , :userid ,'P' , to_char(CURRENT_TIMESTAMP), :operationtype)",
           nativeQuery = true)
    int addTransfer(@NonNull BigDecimal amount, @NonNull String accountname, @NonNull long userid, @NonNull String operationtype);

}
