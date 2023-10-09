import java.util.ArrayList;

public class FunctionType implements Type {
    public Type retType;
    public ArrayList<Type> paramsType;

    FunctionType(Type retType, ArrayList<Type> paramsType) {
        this.retType = retType;
        this.paramsType = paramsType;
    }
}