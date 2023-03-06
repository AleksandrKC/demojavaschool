package com.example.bank.repositories;

import com.example.bank.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

//    @Transactional
    @Modifying
    @Query("update Account a set a.status = 'C' where a.name = ?1 and a.user_id = ?2 and a.status!='C'")
    int lockAccount(@NonNull String name, @NonNull int user_id);

    @Query("select a from Account a where a.name = ?1 and a.user_id = ?2")
    @Nullable
    Optional<Account> getAccountByNameAndUserId(String name, int user_id);

    Account findAccountByName(String name);

    @Query("select a from Account a where a.user_id = ?1")
    @Nullable
    List<Optional<Account>> findAccountsOfUser(int user_id);

//    @Transactional
    @Modifying
    @Query("update Account a set a.amount = ?1 where a.name = ?2 and a.user_id = ?3 and a.status='A'")
    int updateAmountOfAccount(@NonNull BigDecimal amount, @NonNull String name, @NonNull int user_id);

    @Query("select a.bonuscounter from Account a where a.name = ?1 and a.user_id = ?2")
    @Nullable
    int getAccountBonusCounterByNameAndUserId(String name, int user_id);

//    @Transactional
    @Modifying
    @Query("update Account a set a.bonuscounter = ?1 where a.name = ?2 and a.user_id = ?3 and a.status='A'")
    int setAccountBonusCounterByNameAndUserId(@NonNull int bonuscounter, @NonNull String name, @NonNull int user_id);
}
