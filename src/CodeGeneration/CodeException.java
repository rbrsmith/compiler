package CodeGeneration;


/**
 * Thrown when there is an exception in the code
 */
public class CodeException extends CompilerException {

    private String message;

    public CodeException(){}

    public CodeException(boolean register, boolean var) {
        if(register) this.message = "Assembly Code Exception:  Registers are all in use!";
        if(var) this.message = "Assembly Code Exception:  Variable stack is empty!";
    };

    public CodeException(String s) {
        super(s);
    }


    @Override
    public String getMessage() {
        return message;
    }

}
