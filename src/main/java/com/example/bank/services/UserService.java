package com.example.bank.services;

import com.example.bank.dto.UserDto;
import com.example.bank.entities.Roles;
import com.example.bank.entities.Users;
import com.example.bank.exceptions.UserAlreadyExistsException;
import com.example.bank.interceptor.RequestInterceptor;
import com.example.bank.repositories.RoleRepository;
import com.example.bank.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private final Logger LOG = LoggerFactory.getLogger(RequestInterceptor.class);

    private UserRepository userRepository;
    private RoleRepository roleRepository;

    @Transactional
    public int updateStatusOfUser(String status, String userName){
        LOG.info("UserService: call of updateStatusOfUser");
        int result = userRepository.updateStatusOfUser(status, userName);
        return result;
    }

    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        LOG.info("UserService: in constructor with arguments ");
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public Users findByUsername(String username){
        LOG.info("UserService: call of findByUsername");
        Users user = userRepository.findByUsername(username).get();
        if(user ==null){
            throw new UsernameNotFoundException(String.format("Users '%s' not found",username));
        }
        return user;
    }

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        LOG.info("UserService: call of loadUserByUsername with username: " + username);

        Users users = findByUsername(username);
        return new org.springframework.security.core.userdetails.User(users.getUsername(), users.getPassword(), mapRolesToAuthorities(users.getRoles()));
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Roles> roles){
        LOG.info("UserService: call of mapRolesToAuthorities");
        return roles.stream().map(r->new SimpleGrantedAuthority(r.getName())).collect(Collectors.toList());
    }

    @Transactional
    public List<Users> getUsersList() {
        LOG.info("UserService: call of getAllUsers");
        List<Users> users = userRepository.getUsersList();
        LOG.info("UserService.getAllUsers: users.size() = {}", users.size());
        return users;
    }

    @Transactional
    public int deleteUser(String userName){
        LOG.info("UserService: call of deleteUser");
        return userRepository.deleteUser(userName);
    }

    public void execute(Long newUserId, String newUserName, String newUserPassword, String newUserEmail) throws SQLException {
        PreparedStatement statement = null;
        ResultSet result = null;
        Connection connection = DriverManager.getConnection(
                "jdbc:h2:mem:mydb", "sa", "");
        final String SQL_INSERT1 = "INSERT INTO USERS (ID, USERNAME, PASSWORD, EMAIL, STATUS) VALUES (?,?,?,?,?)";
        final String SQL_INSERT2 = "INSERT INTO users_roles (user_id, role_id) VALUES (?,?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(SQL_INSERT1)) {
            preparedStatement.setLong(1, newUserId);
            preparedStatement.setString(2, newUserName);
            preparedStatement.setString(3, newUserPassword);
            preparedStatement.setString(4, newUserEmail);
            preparedStatement.setString(5, "A");

            int row = preparedStatement.executeUpdate();

            // rows affected
            LOG.info("UserService.createUser: add user to USERS affected {}", row);

        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try (PreparedStatement preparedStatement = connection.prepareStatement(SQL_INSERT2)) {
            preparedStatement.setLong(1, newUserId);
            preparedStatement.setLong(2, roleRepository.findByName("ROLE_USER").getId());

            int row = preparedStatement.executeUpdate();

            // rows affected
            LOG.info("UserService.createUser: add role to users_roles affected {}", row);

        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        connection.close();
    }
    @Transactional
    public UserDto createNewUser(UserDto userDto) throws SQLException {
        if (userRepository.getUsersByUsername(userDto.getUsername()).isPresent()){
            throw new UserAlreadyExistsException();
        }
        Long newId = userRepository.getNextId();
        String newEncodedPassword = new BCryptPasswordEncoder().encode(userDto.getPassword());
//        LOG.info("UserService.createNewUser: newId={}; userDto.getEmail()={}; newEncodedPassword={}", newId, userDto.getEmail(),newEncodedPassword);
//        Users userNew = new Users(newId,
//                                  userDto.getUsername(),
//                                  newEncodedPassword,
//                                  userDto.getEmail(),
//                            "A",
//                            null);
//        Users user = userRepository.save(new Users(userDto.getUsername(), new BCryptPasswordEncoder().encode(userDto.getPassword()),"A",userDto.getEmail()));
//        int r = userRepository.createUserCustom(newId, userDto.getUsername(), newEncodedPassword ,"A",userDto.getEmail(),newId);

//        LOG.info("UserService.createNewUser2: newId={}; userDto.getEmail()={}; newEncodedPassword={}", userNew.getId(), userNew.getEmail(),userNew.getPassword());
//        Users user = userRepository.save(userNew);
//        Users newUser = new Users();
//        Long newId = userRepository.getNextId();
//        toDto(newUser);
//        newUser.setId(newId);
//        newUser.setUsername(userDto.getUsername());
//        newUser.setPassword(new BCryptPasswordEncoder().encode(userDto.getPassword()));
//        newUser.setStatus("A");
//        newUser.setEmail(userDto.getEmail());
//        createUserRole(user, roleRepository.findByName("ROLE_USER"));
//        addUserRole(newId);
        execute(newId, userDto.getUsername(), newEncodedPassword, userDto.getEmail());
        return userDto;//userRepository.save(user);
    }
//    @Transactional(readOnly = false)
//    public void executeBatch(final String sql, final Object[]... parameters) {
//        getSession().doWork(new Work() {
//
//            public void execute(Connection connection) throws SQLException {
//                connection.setAutoCommit(false);
//                PreparedStatement stmt = connection.prepareStatement(sql);
//                for (Object[] arr : parameters) {
//                    int i = 1;
//                    for (Object p : arr) {
//                        stmt.setObject(i++, p);
//                    }
//                    stmt.addBatch();
//                }
//                stmt.executeBatch();
//                connection.commit();
//            }
//        });
//    }

//    @Transactional
//    public void createUserRole(Users user, Roles role) {
//        UsersRoles userRole = usersRolesRepository.findByUserIdAndRoleId(user.getId(), role.getId());
//        if (userRole == null) usersRolesRepository.save(new UsersRoles(user.getId(), role.getId()));
//    }
//    private UserDto toDto(Users user){
//        return UserDto.builder()
//                .id(user.getId())
//                .username(user.getUsername())
//                .email(user.getEmail())
////                .password(user.getPassword())
//                .status(user.getStatus())
//                .build();
//
//    }
//    public int addUserRole(Long userId) {
//        int result = userRepository.addUserRole(userId, roleRepository.findByName("ROLE_USER").get().getId());
//        return result;
////        UserRole userRole = userRoleRepository.findByUserIdAndRoleId(user.getId(), role.getId());
////        if (userRole == null) userRoleRepository.save(new UserRole(user.getId(), role.getId()));
//    }
//    public Account addAccount(Account account) {
//        if (accountRepository.getAccountByNameAndUserId(account.getName(), account.getUserId()).isPresent()){
//            throw new AccountAlreadyExistsException();
//        }
//        return accountRepository.saveAndFlush(account);
//    }
}
