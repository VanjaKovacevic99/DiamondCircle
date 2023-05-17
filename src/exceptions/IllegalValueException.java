package exceptions;

public class IllegalValueException extends Exception {
    public IllegalValueException(){
        super("Vrijednosti nisu iz dozvoljenog opsega!");
    }
    public IllegalValueException(String msg){
        super(msg);
    }
}
