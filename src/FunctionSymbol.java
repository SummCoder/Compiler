public class FunctionSymbol extends BaseScope implements Symbol {
    public FunctionType type;
    public FunctionSymbol(String name, Scope enclosingScope) {
        super(name, enclosingScope);
    }
    @Override
    public Type getType() {
        return type;
    }
}