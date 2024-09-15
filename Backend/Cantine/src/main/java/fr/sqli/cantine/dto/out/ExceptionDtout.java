package fr.sqli.cantine.dto.out;

public class ExceptionDtout {

    private String exceptionMessage;

    public ExceptionDtout(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }


    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }
}
