package exceptions;

public class IncorrectLineException extends Exception{
    private String line;

    public IncorrectLineException(String line){
        this.line = line;
    }

    @Override
    public String toString(){
        return "[WARNING]: This line is incorrect: " + line;
    }
}
