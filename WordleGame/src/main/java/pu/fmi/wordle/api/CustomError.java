package pu.fmi.wordle.api;

public class CustomError {

    private final String code;
    private final String value;
    private final String message;

    public CustomError(String code, String value, String message) {
        this.code = code;
        this.value = value;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }
}
