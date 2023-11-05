package fr.sqli.cantine.service.users.student.exceptions;

public class ExistingStudentException extends Exception{
    public ExistingStudentException(String message) {
        super(message);
    }
}
