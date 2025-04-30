package Group7.OAuth.application.exception;

public class InvalidEmailException extends RuntimeException {
    public InvalidEmailException(String username) {
        super(String.format("Username %s não é um e-mail válido", username));
    }
}