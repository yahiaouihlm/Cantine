package fr.sqli.Cantine.service.student.exceptions;

public class ExistingStudentException extends Exception{
    public ExistingStudentException(String message) {
        super(message);
    }
}
