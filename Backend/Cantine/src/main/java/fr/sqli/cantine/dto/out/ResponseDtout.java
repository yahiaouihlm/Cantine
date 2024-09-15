package fr.sqli.cantine.dto.out;

public class ResponseDtout {

    private String message;


    public ResponseDtout(String message) {
        this.message = message;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
