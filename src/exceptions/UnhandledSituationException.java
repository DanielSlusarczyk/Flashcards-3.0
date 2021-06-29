package exceptions;

public class UnhandledSituationException extends Exception{
    private String msg;

    public UnhandledSituationException(String line){
        this.msg = line;
    }

    @Override
    public String toString(){
        return "[WARNING]: This situation is unhandled: " + msg;
    }
}
