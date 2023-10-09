import org.bytedeco.llvm.LLVM.LLVMValueRef;

import java.util.LinkedHashMap;
import java.util.Map;

public class BaseScope implements Scope {
    private final Scope enclosingScope;
    private final Map<String, Symbol> symbols = new LinkedHashMap<>();

    private final Map<String, LLVMValueRef> llvmValueRefs = new LinkedHashMap<>();

    private String name;

    public BaseScope(String name, Scope enclosingScope) {
        this.name = name;
        this.enclosingScope = enclosingScope;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Scope getEnclosingScope() {
        return this.enclosingScope;
    }

    public Map<String, Symbol> getSymbols() {
        return this.symbols;
    }

    public Map<String, LLVMValueRef> getLlvmValueRefs(){
        return this.llvmValueRefs;
    }

    public void define1(String llvmName, LLVMValueRef llvmValueRef){
        this.llvmValueRefs.put(llvmName, llvmValueRef);
    }

    public LLVMValueRef resolve1(String name){
        LLVMValueRef llvmValueRef = llvmValueRefs.get(name);
        if(llvmValueRef != null){
            return llvmValueRef;
        }
        if(enclosingScope != null){
            return enclosingScope.resolve1(name);
        }
        return null;
    }

    @Override
    public void define(Symbol symbol) {
        this.symbols.put(symbol.getName(), symbol);
    }

    @Override
    public Symbol resolve(String name) {
        Symbol symbol = symbols.get(name);
        if (symbol != null) {
            return symbol;
        }
        if (enclosingScope != null) {
            return enclosingScope.resolve(name);
        }
        return null;
    }
}
