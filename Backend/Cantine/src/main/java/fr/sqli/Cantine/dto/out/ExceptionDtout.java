package fr.sqli.Cantine.dto.out;

public class ExceptionDtout {

    private  String  excptionMessage ;

    public ExceptionDtout(String excptionMessage) {
        this.excptionMessage = excptionMessage;
    }

    public String getExcptionMessage() {
        return excptionMessage;
    }

    public void setExcptionMessage(String excptionMessage) {
        this.excptionMessage = excptionMessage;
    }
}
