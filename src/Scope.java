import org.bytedeco.llvm.LLVM.LLVMValueRef;

import java.util.Map;

public interface Scope {
    public String getName();

    public void setName(String name);

    public Scope getEnclosingScope();

    public Map<String, Symbol> getSymbols();

    public void define(Symbol symbol);

    public Symbol resolve(String name);

    public void define1(String name, LLVMValueRef llvmValueRef);

    public LLVMValueRef resolve1(String name);
}