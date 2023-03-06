package com.example.bank.controllers;

import com.example.bank.entities.Transfer;
import com.example.bank.interceptor.RequestInterceptor;
import com.example.bank.repositories.TransferRepository;
import com.example.bank.services.TransferService;
import com.example.bank.entities.Account;
import com.example.bank.repositories.AccountRepository;
import com.example.bank.repositories.UserRepository;
import com.example.bank.services.AccountService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/user")
public class AccountController {

    private final Logger LOG = LoggerFactory.getLogger(RequestInterceptor.class);

    private final AccountService accountsService;
    private final TransferService transferService;

    private final UserRepository userRepository;
    private final TransferRepository transferRepository;

    public AccountController(AccountRepository accountRepository, AccountService accountsService, TransferService transferService, UserRepository userRepository,
                             TransferRepository transferRepository) {
        LOG.info("AccountController constructor call");
        this.accountsService = accountsService;
        this.transferService = transferService;
        this.userRepository = userRepository;
        this.transferRepository = transferRepository;
    }

    @PostMapping("/makeTransfer")
    @Transactional
    public ResponseEntity<Transfer> makeTransfer(@RequestBody Transfer transferRequest,
                                                 Principal principal) throws JsonProcessingException {

        LOG.info("AccountController.makeTransfer: incoming json: {} ;transferRequest.getAmount()= {};transferRequest.getAccountName()={};transferRequest.getOperationType()={}"
                , new ObjectMapper().writeValueAsString(transferRequest)
                , transferRequest.getAmount()
                , transferRequest.getAccountname()
                , transferRequest.getOperationtype());

        int currentUserId = userRepository.findIdByUsername(principal.getName());
        Transfer transfer = new Transfer(
                transferRequest.getAmount(),
                transferRequest.getAccountname(),
                "P",
                (int) currentUserId,
                "",
                transferRequest.getOperationtype()
        );
        LOG.info("/makeTransfer :" + transfer.getAccountname());
        transferService.processTransfer(transfer);
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(transfer);
    }

    @PostMapping("/getAllTransactionsByAccountName")
    public ResponseEntity<List<Transfer>> getAllTransactionsByAccountName(@RequestParam String accountName,
                                                                          Principal principal){
        LOG.info("AccountController.getAllTransactionsByAccountName");
        int currentUserId = userRepository.findIdByUsername(principal.getName());
        List<Transfer> transfers = transferService.getTransferByAccountNameAndUserId(accountName, currentUserId);
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(transfers);
    }
    @GetMapping("/getAccountBySpecifiedName")
    public ResponseEntity<Optional<Account>> getAccountBySpecifiedName(@RequestParam String accountName,
                                                                       Principal principal){
        LOG.info("AccountController.getAccountBySpecifiedName: {} ",accountName);
        int currentUserId = userRepository.findIdByUsername(principal.getName());
        Optional<Account> account = accountsService.getAccountByNameAndUserId(accountName, currentUserId);
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .header("search-result ", account.isEmpty() ? "Account not found" : "Account exists")
                .body(account);
    }

    @GetMapping("/getAccounts")
    public ResponseEntity<List<Optional<Account>>> getUserAccounts(Principal principal){
        LOG.info("AccountController.getUserAccounts");
        int currentUserId = userRepository.findIdByUsername(principal.getName());
        List<Optional<Account>> accounts = accountsService.findAccountsOfUser(currentUserId);
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .header("search-result ", accounts.isEmpty() ? "Accounts not found" : "Accounts exists")
                .body(accounts);
    }

    @RequestMapping(path="/lockAccount",
            method = RequestMethod.PATCH)
    public ResponseEntity<Account> lockAccount(@RequestParam String accountName,
                                               Principal principal){
        LOG.info("AccountController.lockAccount: {} ",accountName);
        int currentUserId = userRepository.findIdByUsername(principal.getName());
        //Берем id user, название счета и ставим статус = 'C'
        accountsService.lockAccount(accountName, currentUserId);
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .header("result", "OK LOCKED")
                .body(accountsService.getByName(accountName));
    }

    @RequestMapping(path="/createAccount",
            method = RequestMethod.POST)
    public ResponseEntity<Account> createAccount(
            @RequestParam String accountName,
            Principal principal){
        //Определяем id user, accountName... создаем счет с amount = 0
        LOG.info("AccountController.createAccount: {} ",accountName);

        int currentUserId = userRepository.findIdByUsername(principal.getName());
        Account accountNew = new Account(accountName,currentUserId, new BigDecimal("0"),"A",0);
        accountsService.addAccount(accountNew);
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(accountNew);
    }

}
