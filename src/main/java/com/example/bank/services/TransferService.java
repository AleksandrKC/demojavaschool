package com.example.bank.services;

import com.example.bank.entities.Account;
import com.example.bank.entities.Transfer;
import com.example.bank.exceptions.*;
import com.example.bank.interceptor.RequestInterceptor;
import com.example.bank.repositories.AccountRepository;
import com.example.bank.repositories.TransferRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class TransferService {

    private final Logger LOG = LoggerFactory.getLogger(RequestInterceptor.class);

    private final BigDecimal BONUSFORPOSITIVEACCOUNTAMOUNT = new BigDecimal(5);
    private final BigDecimal BONUSFORNEGATIVEACCOUNTAMOUNT = new BigDecimal(8);
    @Autowired
    private TransferRepository transferRepository;
    @Autowired
    private AccountRepository accountRepository;

    public List<Transfer> getTransferByAccountNameAndUserId(String accountname, long userid) {
        LOG.info("TransferService: call of getTransferByAccountNameAndUserId: accountname "+ accountname);
        List<Transfer> transfers = transferRepository.getTransferByAccountNameAndUserId(accountname, userid);
        if (transfers.isEmpty()){
            throw new NoTransactionsExistsException();
        }
        return transfers;
    }

    @Transactional
    public String processTransfer(Transfer transferNew){

        BigDecimal amount = transferNew.getAmount();
        String accountName = transferNew.getAccountname();
        String status = transferNew.getStatus();
        int userId = transferNew.getUserid();
        String recorddate = transferNew.getRecorddate();
        String operationType = transferNew.getOperationtype();

        if (accountName==null){ throw new BadRequestFormatException(); }

        Optional<Account> account = accountRepository.getAccountByNameAndUserId(accountName, userId);

        if (account.isEmpty()){ throw new AccountNotFoundException(); }

        String accountStatus = account.get().getStatus();

        int accountBonusTimes = account.get().getBonusCounter();

        if (accountStatus.equals("C")){ throw new AccountLockedException(); }

        LOG.info("TransferService.processTransferNew: transferAccountName=" + accountName
                + "; transferamount=" + amount
                + "; transferoperationType=" + operationType
                + "; transferRecorddate=" + recorddate
                + "; transferStatus=" + status
                + "; userId=" + userId
                + "; accountStatus=" + accountStatus
                + "; accountBonusTimes=" + accountBonusTimes
                );

        if (amount != null && operationType != null && amount.signum()==1){

            if (operationType.equals("refill")) {

                BigDecimal amountAfterRefill = amount.add(account.get().getAmount());

                //пополнение всегда пожалуйста если счет не заблокирован
                int result = transferRepository.addTransfer(amount, accountName, userId,"refill");

                LOG.info("refill addTransfer result=" + result);

                result = accountRepository.updateAmountOfAccount(amountAfterRefill, accountName, userId);

                LOG.info("refill updateAmountOfAccount result=" + result);

                accountRepository.setAccountBonusCounterByNameAndUserId(0, accountName, userId);

            } else if (operationType.equals("withdrawal")) {

                //user может добавлять и выводить деньги со счета
                //по умолчанию есть возможность овердрафта в 50$. Нельзя превышать лимит овердрафта
                //Когда пользователь снимает сумму которая делает его баланс < 0$, то система пополняет счет пользователя на 5$
                //Когда пользователь снимает со своего счета при уже отрицательном балансе, система пополняет его счет на 8$
                //Счет блокируется после 3х последовательных операций приводящих к пополнению счета системой

                //Текущий остаток на счете
                BigDecimal accountAmount = account.get().getAmount();

                BigDecimal transferAmount = amount;

                //остаток на счете после снятия
                BigDecimal amountAfterSubstract =  accountAmount.subtract(transferAmount);

                //по умолчанию есть возможность овердрафта в 50$. Нельзя превышать лимит овердрафта
                if (amountAfterSubstract.compareTo(new BigDecimal(-50))==-1){ throw new NotEnoughMoneyException();}

                boolean isCaseWhenWithdrawalAndAmountMoreThenZero = (accountAmount.compareTo(transferAmount) == 1) ? true : false;
                boolean isCaseWhenAccountShouldBeLocked = (accountBonusTimes==2 && ((accountAmount.signum()>=0 && amountAfterSubstract.signum()==-1)||(accountAmount.signum()==-1))) ? true : false;
                boolean isCaseWhenWillBeBonusFiveDollars = (accountAmount.signum()==-1) ? true : false;
                boolean isCaseWhenWillBeBonusEightDollars = (accountAmount.signum()>=0 && amountAfterSubstract.signum()==-1) ? true : false;

                if (isCaseWhenWithdrawalAndAmountMoreThenZero){

                    //на счете больше денег чем снимают можно проводить
                    transferRepository.addTransfer(amount, accountName, userId,"withdrawal");
                    accountRepository.updateAmountOfAccount(amountAfterSubstract, accountName, userId);
                    accountRepository.setAccountBonusCounterByNameAndUserId(0, accountName, userId);

                } else if (isCaseWhenAccountShouldBeLocked) {
                    //Счет блокируется после 3х последовательных операций приводящих к пополнению счета системой

                    if (isCaseWhenWillBeBonusFiveDollars){

                        transferRepository.addTransfer(amount, accountName, userId,"withdrawal with following system bonus 8");
                        transferRepository.addTransfer(BONUSFORNEGATIVEACCOUNTAMOUNT, accountName, userId,"8 system bonus after withdrawal");

                    } else if (isCaseWhenWillBeBonusEightDollars) {
                        transferRepository.addTransfer(amount, accountName, userId,"withdrawal with following system bonus 5");
                        transferRepository.addTransfer(BONUSFORPOSITIVEACCOUNTAMOUNT, accountName, userId,"5 system bonus after withdrawal");
                    }

                    accountRepository.updateAmountOfAccount(amountAfterSubstract, accountName, userId);
                    accountRepository.lockAccount(accountName, userId);
                    accountRepository.setAccountBonusCounterByNameAndUserId(3, accountName, userId);

                } else if (isCaseWhenWillBeBonusFiveDollars) {

                    //Когда польователь снимает со своего счета при уже отрицательном балансе, система пополняет его счет на 8$
                    transferRepository.addTransfer(amount, accountName, userId,"withdrawal with following system bonus 8");
                    transferRepository.addTransfer(BONUSFORNEGATIVEACCOUNTAMOUNT, accountName, userId,"8 system bonus after withdrawal");
                    accountRepository.updateAmountOfAccount(amountAfterSubstract.add(BONUSFORNEGATIVEACCOUNTAMOUNT), accountName, userId); //todo make constant BigDecimal system bonuses 5 and 8
                    accountRepository.setAccountBonusCounterByNameAndUserId(accountBonusTimes + 1, accountName, userId);

                } else if (isCaseWhenWillBeBonusEightDollars) {

                    //Когда пользователь снимает сумму которая делает его баланс < 0$, то система пополняет счет пользователя на 5$
                    transferRepository.addTransfer(amount, accountName, userId,"withdrawal with following system bonus 5");
                    transferRepository.addTransfer(BONUSFORPOSITIVEACCOUNTAMOUNT, accountName, userId,"5 system bonus after withdrawal");
                    accountRepository.updateAmountOfAccount(amountAfterSubstract.add(BONUSFORPOSITIVEACCOUNTAMOUNT), accountName, userId);
                    accountRepository.setAccountBonusCounterByNameAndUserId(accountBonusTimes + 1, accountName, userId);
                }
            }
        }
        else{
            throw new BadRequestFormatException();
        }
        return "Transfer processed";
    }
}