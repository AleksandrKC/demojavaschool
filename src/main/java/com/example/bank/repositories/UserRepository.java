package com.example.bank.repositories;

import com.example.bank.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {

    @Override
    Optional<Users> findById(Long aLong);

    @Query("SELECT u from Users u where u.username = ?1")
    @Nullable
    Optional<Users> getUsersByUsername(String username);

    Optional<Users> findByUsername(String username);

    @Query(value = "SELECT u.id FROM Users u WHERE u.username = ?1",
            nativeQuery = true)
    int findIdByUsername(String username);

    @Modifying
    @Query("update Users a set a.status = ?1 where a.username = ?2 and a.status!='D'")
    int updateStatusOfUser(@NonNull String status, @NonNull String username);

    @Query(value = "SELECT u.id, u.username,'***' as password, u.email,u.status FROM Users u",
           nativeQuery = true)
    List<Users> getUsersList();

    @Modifying
    @Query("update Users a set a.status = 'D' where a.username = ?1")
    int deleteUser(String userName);

    @Query( value = "SELECT TRANSFER_SEQ_CUSTOM.NEXTVAL FROM DUAL",
            nativeQuery = true)
    Long getNextId();

}
