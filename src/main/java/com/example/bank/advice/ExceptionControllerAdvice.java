package com.example.bank.advice;
import com.example.bank.exceptions.*;
import com.example.bank.entities.ErrorDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(NotEnoughMoneyException.class)
    public ResponseEntity<ErrorDetails> exceptionNotEnoughMoneyHandler() {
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setMessage("Not enough money to make the payment.");
        return ResponseEntity
                .badRequest()
                .body(errorDetails);
    }
    @ExceptionHandler(NoTransactionsExistsException.class)
    public ResponseEntity<ErrorDetails> exceptionNoTransfersFoundHandler() {
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setMessage("No operations found.");
        return ResponseEntity
                .badRequest()
                .body(errorDetails);
    }
    @ExceptionHandler(AccountAlreadyExistsException.class)
    public ResponseEntity<ErrorDetails> exceptionAccountAlreadyExistsHandler() {
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setMessage("Can't create account because account with same name exists.");
        return ResponseEntity
                .badRequest()
                .body(errorDetails);
    }
    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorDetails> exceptionAccountNotFoundHandler() {
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setMessage("account not found.");
        return ResponseEntity
                .badRequest()
                .body(errorDetails);
    }
    @ExceptionHandler(OperationNotAvailableException.class)
    public ResponseEntity<ErrorDetails> exceptionOperationNotAvailableHandler() {
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setMessage("Operation with specified type not available. Acceptable operations: refill, withdrawal");
        return ResponseEntity
                .badRequest()
                .body(errorDetails);
    }
    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<ErrorDetails> exceptionUserAlreadyExistHandler() {
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setMessage("Users with same name already exists and you can't add with same name.");
        return ResponseEntity
                .badRequest()
                .body(errorDetails);
    }

    @ExceptionHandler(InDBQueryFailedException.class)
    public ResponseEntity<ErrorDetails> exceptionInDBQueryFailedHandler() {
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setMessage("In database result of operation !=1 - ask administrators.");
        return ResponseEntity
                .badRequest()
                .body(errorDetails);
    }

    @ExceptionHandler(AccountAlreadyLockedException.class)
    public ResponseEntity<ErrorDetails> exceptionAccountAlreadyLockedHandler() {
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setMessage("Account already locked.");
        return ResponseEntity
                .badRequest()
                .body(errorDetails);
    }
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorDetails> exceptionUserAlreadyLockedHandler() {
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setMessage("User already exists.");
        return ResponseEntity
                .badRequest()
                .body(errorDetails);
    }

    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<ErrorDetails> exceptionAccountLockedHandler() {
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setMessage("Account locked. No transfer available");
        return ResponseEntity
                .badRequest()
                .body(errorDetails);
    }
    @ExceptionHandler(BadRequestFormatException.class)
    public ResponseEntity<ErrorDetails> exceptionBadRequestFormatHandler() {
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setMessage("Bad request format - check your values in request");
        return ResponseEntity
                .badRequest()
                .body(errorDetails);
    }
}
