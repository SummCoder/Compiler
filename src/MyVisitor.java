import org.antlr.v4.runtime.tree.TerminalNode;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.llvm.LLVM.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

import static org.bytedeco.llvm.global.LLVM.*;

public class MyVisitor extends SysYParserBaseVisitor<LLVMValueRef>{
    //创建module
    LLVMModuleRef module = LLVMModuleCreateWithName("module");

    //初始化IRBuilder，后续将使用这个builder去生成LLVM IR
    LLVMBuilderRef builder = LLVMCreateBuilder();

    //考虑到我们的语言中仅存在int一个基本类型，可以通过下面的语句为LLVM的int型重命名方便以后使用
    LLVMTypeRef i32Type = LLVMInt32Type();
    LLVMTypeRef voidType = LLVMVoidType();

    LLVMValueRef zero = LLVMConstInt(i32Type, 0, /* signExtend */ 0);

    private GlobalScope globalScope = null;
    private Scope currentScope = null;
    private int localScopeCounter = 0;

//    是否有return值，避免void无return语句
    boolean judge = false;
//    判定如今处于哪一个函数，以此追加基本块
    LLVMValueRef currentFunction;
    List<LLVMBasicBlockRef> blocks = new ArrayList<>();
    Stack<LLVMBasicBlockRef> whileConditionStack = new Stack<>();
    Stack<LLVMBasicBlockRef> afterWhileStack = new Stack<>();
    int count = 0;

    public void LLVMIRVisitor() {
        //初始化LLVM
        LLVMInitializeCore(LLVMGetGlobalPassRegistry());
        LLVMLinkInMCJIT();
        LLVMInitializeNativeAsmPrinter();
        LLVMInitializeNativeAsmParser();
        LLVMInitializeNativeTarget();
    }
    @Override
    public LLVMValueRef visitTerminal(TerminalNode node) {
        if (node.getSymbol().getType() == SysYParser.INTEGER_CONST) {
            long value = 0;
            if(Objects.equals(node.getSymbol().getText(), "0")){

            } else if (node.getSymbol().getText().startsWith("0x") || node.getSymbol().getText().startsWith("0X")) {
                value = Long.parseLong(node.getSymbol().getText().substring(2), 16);
            } else if (node.getSymbol().getText().charAt(0) == '0' && (node.getSymbol().getText().charAt(1) != 'x' && node.getSymbol().getText().charAt(1) != 'X')) {
                value = Long.parseLong(node.getSymbol().getText().substring(1), 8);
            }else {
                value = Integer.parseInt(node.getSymbol().getText());
            }
            return LLVMConstInt(i32Type, value, 1);
        }
        return super.visitTerminal(node);
    }

    @Override
    public LLVMValueRef visitFuncDef(SysYParser.FuncDefContext ctx) {
////        生成函数main
//        LLVMTypeRef returnType = i32Type;
//        LLVMTypeRef ft = LLVMFunctionType(returnType, LLVMVoidType(), /* argumentCount */ 0, /* isVariadic */ 0);
//        LLVMValueRef function = LLVMAddFunction(module, /*functionName:String*/"main", ft);
////        创建基本块
//        LLVMBasicBlockRef block = LLVMAppendBasicBlock(function, /*blockName:String*/"mainEntry");
//        LLVMPositionBuilderAtEnd(builder, block);
////        开启main作用域
//        currentScope = new BaseScope("mainEntry", currentScope);
//        LLVMValueRef ret = super.visitFuncDef(ctx);
//        currentScope = currentScope.getEnclosingScope();
//        return ret;

//        认真写一下函数部分，之前只有main函数，属于是偷鸡了
//        得到函数返回类型
        String retType = ctx.funcType().getText();
        LLVMTypeRef returnType = getType(retType);
//        生成函数参数类型，需获得参数个数以及参数类型
        int funcFParamCount = 0;
        if(ctx.funcFParams() != null){
            funcFParamCount = ctx.funcFParams().funcFParam().size();
        }
//        ArrayList<LLVMTypeRef> paramsType = new ArrayList<>();
//        for (int i = 0; i < funcFParamCount; i++) {
//            paramsType.add(getType(ctx.funcFParams().funcFParam(i).bType().getText()));
//        }
        PointerPointer<Pointer> argumentTypes = new PointerPointer<>(funcFParamCount);
        LLVMTypeRef paramType = i32Type;
        for (int i = 0; i < funcFParamCount; i++) {
            paramType = i32Type;
            if (ctx.funcFParams().funcFParam(i).L_BRACKT(0) != null) {   //参数为数组类型，实际传入指针类型
                paramType = LLVMPointerType(paramType, 0);
            }
            argumentTypes.put(i, paramType);
        }
        LLVMTypeRef ft = LLVMFunctionType(returnType, argumentTypes, funcFParamCount, 0);
//        生成函数，即向之前创建的module中添加函数
        LLVMValueRef function = LLVMAddFunction(module, /*functionName:String*/ctx.IDENT().getText(), ft);
        currentFunction = function;
//        创建基本块
        LLVMBasicBlockRef block = LLVMAppendBasicBlock(function, ctx.IDENT().getText() + "Entry");
        LLVMPositionBuilderAtEnd(builder, block);
//        开启函数局部作用域
        BaseScope funcScope = new BaseScope(ctx.IDENT().getText(), currentScope);
        currentScope.define1(ctx.IDENT().getText(), function);

        ArrayList<Type> paramsType = new ArrayList<>();
        FunctionSymbol func = new FunctionSymbol(ctx.IDENT().getText(), currentScope);
        func.type = new FunctionType((Type) globalScope.resolve(retType), paramsType);
        currentScope.define(func);

        currentScope = funcScope;
//        将函数参数存入符号表中并声明空间
        for (int i = 0; i < funcFParamCount; i++) {
            paramType = i32Type;
            if(ctx.funcFParams().funcFParam(i).L_BRACKT(0) != null){
                paramType = LLVMPointerType(paramType, 0);//区别于一般数组，为指针类型
            }
            String varName = ctx.funcFParams().funcFParam(i).IDENT().getText();
            LLVMValueRef varPointer = LLVMBuildAlloca(builder, paramType, varName);

//            System.err.println(LLVMGetTypeKind(paramType) + " para");

            currentScope.define1(varName, varPointer);
            LLVMValueRef varValue = LLVMGetParam(function, i);
            LLVMBuildStore(builder, varValue, varPointer);
        }

        LLVMValueRef ret = super.visitFuncDef(ctx);
        currentScope = currentScope.getEnclosingScope();
//        if(!judge) {
//            LLVMBuildRet(builder, null);
//        }

        if(returnType.equals(voidType) && !judge){
            LLVMBuildRet(builder, null);
        }else {
            LLVMBuildRet(builder, zero);
        }
        judge = false;
        return ret;
    }

    LLVMTypeRef getType(String type){
        LLVMTypeRef Type;
        if(Objects.equals(type, "int")){
            Type = i32Type;
        }else {
            Type = voidType;
        }
        return Type;
    }

    @Override
    public LLVMValueRef visitPlusExp(SysYParser.PlusExpContext ctx) {
        LLVMValueRef valueRef1 = visit(ctx.exp(0));
        LLVMValueRef valueRef2 = visit(ctx.exp(1));
        if (ctx.PLUS() != null) {
            return LLVMBuildAdd(builder, valueRef1, valueRef2, "tmp_");
        } else {
            return LLVMBuildSub(builder, valueRef1, valueRef2, "tmp_");
        }
    }

    @Override
    public LLVMValueRef visitMulExp(SysYParser.MulExpContext ctx) {
        LLVMValueRef valueRef1 = visit(ctx.exp(0));
        LLVMValueRef valueRef2 = visit(ctx.exp(1));
        if (ctx.MUL() != null) {
            return LLVMBuildMul(builder, valueRef1, valueRef2, "tmp_");
        } else if (ctx.DIV() != null) {
            return LLVMBuildSDiv(builder, valueRef1, valueRef2, "tmp_");
        } else {
            return LLVMBuildSRem(builder, valueRef1, valueRef2, "tmp_");
        }
    }

    @Override
    public LLVMValueRef visitUnaryOpExp(SysYParser.UnaryOpExpContext ctx) {
        LLVMValueRef valueRef = visit(ctx.exp());
        if(ctx.unaryOp().MINUS() != null){
            return LLVMBuildSub(builder, zero, valueRef, "tmp_");
        } else if (ctx.unaryOp().NOT() != null) {
            long Value = LLVMConstIntGetZExtValue(valueRef);
            if (Value == 0) {
                return LLVMConstInt(i32Type, 1, 1);
            } else {
                return LLVMConstInt(i32Type, 0, 1);
            }
        }else {
            return valueRef;
        }
//        return super.visitUnaryOpExp(ctx);
    }

    @Override
    public LLVMValueRef visitReturnStmt(SysYParser.ReturnStmtContext ctx) {
        LLVMValueRef result = visit(ctx.exp());
        judge = true;
        return LLVMBuildRet(builder, result);
    }

    //开启全局作用域
    @Override
    public LLVMValueRef visitProgram(SysYParser.ProgramContext ctx) {
        globalScope = new GlobalScope(null);
        currentScope = globalScope;
        LLVMValueRef ret = super.visitProgram(ctx);
        currentScope = currentScope.getEnclosingScope();
        return ret;
    }

    //局部作用域
    @Override
    public LLVMValueRef visitBlock(SysYParser.BlockContext ctx) {
        LocalScope localScope = new LocalScope(currentScope);
        String localScopeName = localScope.getName() + localScopeCounter;
        localScope.setName(localScopeName);
        localScopeCounter++;
        currentScope = localScope;
        LLVMValueRef ret = super.visitBlock(ctx);
        currentScope = currentScope.getEnclosingScope();
        return ret;
    }

    @Override
    public LLVMValueRef visitVarDecl(SysYParser.VarDeclContext ctx) {
//        变量声明
        for (SysYParser.VarDefContext varDefContext : ctx.varDef()){
            String varName = varDefContext.IDENT().getText();
            LLVMValueRef varPointer;
            LLVMTypeRef varType = i32Type;
            int arrayCount = 0;//用以记录数组元素的多少
            if(varDefContext.L_BRACKT(0) != null){
                arrayCount = Integer.parseInt(varDefContext.constExp(0).getText());
//                System.err.println(arrayCount);
                varType = LLVMArrayType(varType, arrayCount);
            }
            if (currentScope == globalScope) {
                //创建名为globalVar的全局变量
                varPointer = LLVMAddGlobal(module, varType, /*globalVarName:String*/varName);
                if(arrayCount == 0){
                    LLVMSetInitializer(varPointer, /* constantVal:LLVMValueRef*/zero);//非数组
                }else {
                    PointerPointer<Pointer> arrayElement = new PointerPointer<>(arrayCount);
                    for (int i = 0; i < arrayCount; ++i) {
                        arrayElement.put(i, zero);
                    }
                    LLVMValueRef initArray = LLVMConstArray(i32Type, arrayElement, arrayCount);
                    LLVMSetInitializer(varPointer, initArray);
                }
            } else {
                varPointer = LLVMBuildAlloca(builder, varType, varName);
//                System.err.println(LLVMGetTypeKind(varType));
//                System.err.println(LLVMGetTypeKind(LLVMGetElementType(LLVMTypeOf(varPointer))));
//                LLVMBuildStore(builder, zero, varPointer);
            }
//            存在赋值语句
            if (varDefContext.ASSIGN() != null) {
                SysYParser.ExpContext expContext = varDefContext.initVal().exp();
                if (expContext != null) {//非数组
                    LLVMValueRef initVal = visit(expContext);
                    if (currentScope == globalScope) {
                        LLVMSetInitializer(varPointer, initVal);
                    }else {
                        LLVMBuildStore(builder, initVal, varPointer);
                    }
                }else {
//                    数组
//                    L_BRACE (initVal (COMMA initVal)*)? R_BRACE
                    int initValCount = varDefContext.initVal().initVal().size();//已经初始化的数组个数
                    if(currentScope == globalScope){
                        PointerPointer<Pointer> arrayElement = new PointerPointer<>(arrayCount);
                        for (int i = 0; i < initValCount; ++i) {
                            arrayElement.put(i, this.visit(varDefContext.initVal().initVal(i).exp()));
                        }
                        for (int i = initValCount; i < arrayCount; ++i) {
                            arrayElement.put(i, zero);
                        }
                        LLVMValueRef initArray = LLVMConstArray(i32Type, arrayElement, arrayCount);
                        LLVMSetInitializer(varPointer, initArray);
                    }else {
                        //只能单个单个数字进行存储
                        for (int i = 0; i < initValCount; ++i) {
                            LLVMValueRef[] gepIndices = { zero, LLVMConstInt(i32Type, i, 0) };
                            LLVMValueRef elementPtr = LLVMBuildGEP(builder, varPointer, new PointerPointer<>(gepIndices), 2, "pointer");
                            LLVMBuildStore(builder, this.visit(varDefContext.initVal().initVal(i).exp()), elementPtr);
                        }
                        for (int i = initValCount; i < arrayCount; ++i) {
                            LLVMValueRef[] gepIndices = { zero, LLVMConstInt(i32Type, i, 0) };
                            LLVMValueRef elementPtr = LLVMBuildGEP(builder, varPointer, new PointerPointer<>(gepIndices), 2, "pointer");
                            LLVMBuildStore(builder, zero, elementPtr);
                        }
                    }
                }
            }
            currentScope.define1(varName, varPointer);//加入符号表中
//            System.err.println(LLVMGetTypeKind(LLVMTypeOf(varPointer)));
        }
        return super.visitVarDecl(ctx);
    }

    @Override
    public LLVMValueRef visitConstDecl(SysYParser.ConstDeclContext ctx) {
        for (SysYParser.ConstDefContext constDefContext : ctx.constDef()){
            String constName = constDefContext.IDENT().getText();
            LLVMValueRef constPointer;
            LLVMTypeRef constType = i32Type;
            int arrayCount = 0;
            if(constDefContext.L_BRACKT(0) != null){
                arrayCount = Integer.parseInt(constDefContext.constExp(0).getText());
                constType = LLVMArrayType(constType, arrayCount);
            }
            if (currentScope == globalScope) {
                //创建名为globalVar的全局变量
                constPointer = LLVMAddGlobal(module, constType, /*globalVarName:String*/constName);
                if(arrayCount == 0){
                    LLVMSetInitializer(constPointer, /* constantVal:LLVMValueRef*/zero);
                }else {
                    PointerPointer<Pointer> arrayElement = new PointerPointer<>(arrayCount);
                    for (int i = 0; i < arrayCount; ++i) {
                        arrayElement.put(i, zero);
                    }
                    LLVMValueRef initArray = LLVMConstArray(i32Type, arrayElement, arrayCount);
                    LLVMSetInitializer(constPointer, initArray);
                }
            } else {
                constPointer = LLVMBuildAlloca(builder, constType, constName);
//                LLVMBuildStore(builder, zero, constPointer);
            }
//            存在赋值语句
            if (constDefContext.ASSIGN() != null) {
                SysYParser.ConstExpContext expContext = constDefContext.constInitVal().constExp();
                if (expContext != null) {
                    LLVMValueRef initVal = visit(expContext);
                    if (currentScope == globalScope) {
                        LLVMSetInitializer(constPointer, initVal);
                    }else {
                        LLVMBuildStore(builder, initVal, constPointer);
                    }
                }else {
                    int initConstCount = constDefContext.constInitVal().constInitVal().size();//已经初始化的数组个数
                    if(currentScope == globalScope){
                        PointerPointer<Pointer> arrayElement = new PointerPointer<>(arrayCount);
                        for (int i = 0; i < initConstCount; ++i) {
                            arrayElement.put(i, this.visit(constDefContext.constInitVal().constInitVal(i).constExp()));
                        }
                        for (int i = initConstCount; i < arrayCount; ++i) {
                            arrayElement.put(i, zero);
                        }
                        LLVMValueRef initArray = LLVMConstArray(i32Type, arrayElement, arrayCount);
                        LLVMSetInitializer(constPointer, initArray);
                    }else {
                        //只能单个单个数字进行存储
                        for (int i = 0; i < initConstCount; ++i) {
                            LLVMValueRef[] gepIndices = { zero, LLVMConstInt(i32Type, i, 0) };
                            LLVMValueRef elementPtr = LLVMBuildGEP(builder, constPointer, new PointerPointer<>(gepIndices), 2, "pointer");
                            LLVMBuildStore(builder, this.visit(constDefContext.constInitVal().constInitVal(i).constExp()), elementPtr);
                        }
                        for (int i = initConstCount; i < arrayCount; ++i) {
                            LLVMValueRef[] gepIndices = { zero, LLVMConstInt(i32Type, i, 0) };
                            LLVMValueRef elementPtr = LLVMBuildGEP(builder, constPointer, new PointerPointer<>(gepIndices), 2, "pointer");
                            LLVMBuildStore(builder, zero, elementPtr);
                        }
                    }
                }
            }
            currentScope.define1(constName, constPointer);//加入符号表中
        }
        return super.visitConstDecl(ctx);
    }

    @Override
    public LLVMValueRef visitLVal(SysYParser.LValContext ctx) {
        String lValName = ctx.IDENT().getText();
        LLVMValueRef varPointer = currentScope.resolve1(lValName);
        LLVMValueRef elementPtr;
        if(LLVMGetTypeKind(LLVMGetElementType(LLVMTypeOf(varPointer))) == LLVMArrayTypeKind){//数组类型
//            System.err.println("hello " + lValName);
//            int index = Integer.parseInt(this.visit(ctx.exp(0)).toString());
            if(ctx.exp().size() > 0){
                LLVMValueRef[] gepIndices = {zero, this.visit(ctx.exp(0)) };
                elementPtr = LLVMBuildGEP(builder, varPointer, new PointerPointer<>(gepIndices), 2, "pointer");
            }else {
                LLVMValueRef[] gepIndices = {zero, zero};
                elementPtr = LLVMBuildGEP(builder, varPointer, new PointerPointer<>(gepIndices), 2, "pointer");
                return elementPtr;//函数调用
            }
        }else if(LLVMGetTypeKind(LLVMGetElementType(LLVMTypeOf(varPointer))) == LLVMPointerTypeKind){//函数中以指针类型代替数组
//            System.err.println("hahhahaha " + lValName);
            if(ctx.exp().size() > 0){
                LLVMValueRef[] gepIndices = {this.visit(ctx.exp(0)) };
                LLVMValueRef pointer = LLVMBuildLoad(builder, varPointer, lValName);
                elementPtr = LLVMBuildGEP(builder, pointer, new PointerPointer<>(gepIndices), 1, "pointer");
            }else {
//                return varPointer;//函数调用，这样会产生双重指针等情况
//                return null;
                elementPtr = varPointer;
            }
        }else {//普通变量
//            System.err.println("wuwuuw " + lValName);
            elementPtr = varPointer;
        }
//        取出左值
        return LLVMBuildLoad(builder, elementPtr, ctx.getText());
    }

    @Override
    public LLVMValueRef visitAssignStmt(SysYParser.AssignStmtContext ctx) {
//        取出左值
        String lValName = ctx.lVal().IDENT().getText();
        LLVMValueRef lVal = currentScope.resolve1(ctx.lVal().IDENT().getText());
        //        取出右值
        LLVMValueRef rVal = visit(ctx.exp());
        if(LLVMGetTypeKind(LLVMGetElementType(LLVMTypeOf(lVal))) == LLVMArrayTypeKind){
            if(ctx.lVal().exp().size() > 0){
                LLVMValueRef[] gepIndices = {zero, this.visit(ctx.lVal().exp(0)) };
                lVal = LLVMBuildGEP(builder, lVal, new PointerPointer<>(gepIndices), 2, "pointer");
            }
        } else if (LLVMGetTypeKind(LLVMGetElementType(LLVMTypeOf(lVal))) == LLVMPointerTypeKind) {
            if(ctx.lVal().exp().size() > 0){
                LLVMValueRef[] gepIndices = {this.visit(ctx.lVal().exp(0)) };
                LLVMValueRef pointer = LLVMBuildLoad(builder, lVal, lValName);
                lVal = LLVMBuildGEP(builder, pointer, new PointerPointer<>(gepIndices), 1, "pointer");
            }
        }
        return LLVMBuildStore(builder, rVal, lVal);
    }

    @Override
    public LLVMValueRef visitExpParenthesis(SysYParser.ExpParenthesisContext ctx) {
        return visit(ctx.exp());
    }

    @Override
    public LLVMValueRef visitCallFuncExp(SysYParser.CallFuncExpContext ctx) {
        LLVMValueRef function = currentScope.resolve1(ctx.IDENT().getText());

        int funcFParamCount = 0;
        if(ctx.funcRParams() != null){
            funcFParamCount = ctx.funcRParams().param().size();
        }
        PointerPointer<Pointer> argumentTypes = new PointerPointer<>(funcFParamCount);
        for (int i = 0; i < funcFParamCount; i++) {
            SysYParser.ParamContext paramContext = ctx.funcRParams().param(i);
            SysYParser.ExpContext expContext = paramContext.exp();
            argumentTypes.put(i, this.visit(expContext));
        }
        if(Objects.equals(((FunctionType) currentScope.resolve(ctx.IDENT().getText()).getType()).retType.toString(), "void")){
            return LLVMBuildCall(builder, function, argumentTypes, funcFParamCount, "");
        }
        return LLVMBuildCall(builder, function, argumentTypes, funcFParamCount, "fun");
    }

//    复写条件语句，控制流
    @Override
    public LLVMValueRef visitConditionStmt(SysYParser.ConditionStmtContext ctx) {
        LLVMValueRef result;
//        创建需要的基本块
        count = 0;
        blocks.clear();
        createBlock(ctx.cond());
//        追加三个基本块
        LLVMBasicBlockRef trueBlock = LLVMAppendBasicBlock(currentFunction, "true");
        LLVMBasicBlockRef falseBlock = LLVMAppendBasicBlock(currentFunction, "false");
        LLVMBasicBlockRef nextBlock = LLVMAppendBasicBlock(currentFunction, "entry");
        blocks.add(falseBlock);
        blocks.add(trueBlock);
        result = this.visit(ctx.cond());
//        生成比较指令，比较条件语句结果与0是否相等
        LLVMValueRef condition = LLVMBuildICmp(builder, /*这是个int型常量，表示比较的方式*/LLVMIntNE, result, zero, "tmp_");
//        条件跳转指令，选择跳转到哪个块
        LLVMBuildCondBr(builder,/*condition:LLVMValueRef*/ condition,/*ifTrue:LLVMBasicBlockRef*/ trueBlock,/*ifFalse:LLVMBasicBlockRef*/ falseBlock);
        LLVMPositionBuilderAtEnd(builder, trueBlock);//后续生成的指令将追加在trueBlock的后面
        this.visit(ctx.stmt(0));// if语句
        LLVMBuildBr(builder, nextBlock);//跳转至下一模块
        LLVMPositionBuilderAtEnd(builder, falseBlock);
//        排除没有else语句的情况
        if(ctx.ELSE() != null){
            this.visit(ctx.stmt(1));
        }
        LLVMBuildBr(builder, nextBlock);
        LLVMPositionBuilderAtEnd(builder, nextBlock);
        return null;
    }

    @Override
    public LLVMValueRef visitWhileStmt(SysYParser.WhileStmtContext ctx) {
        LLVMBasicBlockRef whileCondition = LLVMAppendBasicBlock(currentFunction, "whileCondition");
        LLVMBasicBlockRef whileBody = LLVMAppendBasicBlock(currentFunction, "whileBody");
        LLVMBasicBlockRef nextBlock = LLVMAppendBasicBlock(currentFunction, "entry");
        LLVMBuildBr(builder, whileCondition);
        LLVMPositionBuilderAtEnd(builder, whileCondition);

        count = 0;
        blocks.clear();
        createBlock(ctx.cond());
        blocks.add(nextBlock);
        blocks.add(whileBody);

        LLVMValueRef condVal = this.visit(ctx.cond());
        LLVMValueRef condition = LLVMBuildICmp(builder, LLVMIntNE, zero, condVal, "result");
        LLVMBuildCondBr(builder, condition, whileBody, nextBlock);
        LLVMPositionBuilderAtEnd(builder, whileBody);
        whileConditionStack.push(whileCondition);
        afterWhileStack.push(nextBlock);
        this.visit(ctx.stmt());
        LLVMBuildBr(builder, whileCondition);
        whileConditionStack.pop();
        afterWhileStack.pop();
        LLVMPositionBuilderAtEnd(builder, nextBlock);
        return null;
    }

    @Override
    public LLVMValueRef visitBreakStmt(SysYParser.BreakStmtContext ctx) {
        return LLVMBuildBr(builder, afterWhileStack.peek());
    }

    @Override
    public LLVMValueRef visitContinueStmt(SysYParser.ContinueStmtContext ctx) {
        return LLVMBuildBr(builder, whileConditionStack.peek());
    }

    @Override
    public LLVMValueRef visitLtCond(SysYParser.LtCondContext ctx) {
        LLVMValueRef lvalueRef = this.visit(ctx.cond(0));
        LLVMValueRef rvalueRef = this.visit(ctx.cond(1));
        LLVMValueRef revalueRef;
        if(ctx.LT() != null){
            revalueRef = LLVMBuildICmp(builder, LLVMIntSLT, lvalueRef, rvalueRef, "tmp_");
        } else if (ctx.GT() != null) {
            revalueRef = LLVMBuildICmp(builder, LLVMIntSGT, lvalueRef, rvalueRef, "tmp_");
        } else if (ctx.LE() != null) {
            revalueRef = LLVMBuildICmp(builder, LLVMIntSLE, lvalueRef, rvalueRef, "tmp_");
        }else {
            revalueRef = LLVMBuildICmp(builder, LLVMIntSGE, lvalueRef, rvalueRef, "tmp_");
        }
        return LLVMBuildZExt(builder, revalueRef, i32Type, "tmp_");
    }

    @Override
    public LLVMValueRef visitExpCond(SysYParser.ExpCondContext ctx) {
        return super.visitExpCond(ctx);
    }

    @Override
    public LLVMValueRef visitAndCond(SysYParser.AndCondContext ctx) {
        LLVMValueRef lValue = this.visit(ctx.cond(0));
        LLVMValueRef condition = LLVMBuildICmp(builder, /*这是个int型常量，表示比较的方式*/LLVMIntNE, lValue, zero, "tmp_");
        LLVMBuildCondBr(builder,/*condition:LLVMValueRef*/ condition,/*ifTrue:LLVMBasicBlockRef*/ blocks.get(count),/*ifFalse:LLVMBasicBlockRef*/ blocks.get(blocks.size() - 2));
        LLVMPositionBuilderAtEnd(builder, blocks.get(count));
        count++;
        LLVMValueRef rValue = this.visit(ctx.cond(1));
        return rValue;
    }


    @Override
    public LLVMValueRef visitOrCond(SysYParser.OrCondContext ctx) {
        LLVMValueRef lValue = this.visit(ctx.cond(0));
        LLVMValueRef condition = LLVMBuildICmp(builder, /*这是个int型常量，表示比较的方式*/LLVMIntNE, lValue, zero, "tmp_");
        LLVMBuildCondBr(builder,/*condition:LLVMValueRef*/ condition,/*ifTrue:LLVMBasicBlockRef*/ blocks.get(blocks.size() - 1),/*ifFalse:LLVMBasicBlockRef*/ blocks.get(count));
        LLVMPositionBuilderAtEnd(builder, blocks.get(count));
        count++;
        LLVMValueRef rValue = this.visit(ctx.cond(1));
        return rValue;
    }

    void createBlock(SysYParser.CondContext ctx){
        if(ctx instanceof SysYParser.AndCondContext){
            LLVMBasicBlockRef condBlock = LLVMAppendBasicBlock(currentFunction, "cond");
            blocks.add(condBlock);
            createBlock(((SysYParser.AndCondContext) ctx).cond(0));
        } else if (ctx instanceof SysYParser.OrCondContext) {
            LLVMBasicBlockRef condBlock = LLVMAppendBasicBlock(currentFunction, "cond");
            blocks.add(condBlock);
            createBlock(((SysYParser.OrCondContext) ctx).cond(0));
        }
    }


    @Override
    public LLVMValueRef visitEqCond(SysYParser.EqCondContext ctx) {
        LLVMValueRef lvalueRef = this.visit(ctx.cond(0));
        LLVMValueRef rvalueRef = this.visit(ctx.cond(1));
        LLVMValueRef revalueRef;
        if(ctx.EQ() != null){//等于条件语句
            revalueRef = LLVMBuildICmp(builder, LLVMIntEQ, lvalueRef, rvalueRef, "tmp_");
        }else {
            revalueRef = LLVMBuildICmp(builder, LLVMIntNE, lvalueRef, rvalueRef, "tmp_");
        }
        return LLVMBuildZExt(builder, revalueRef, i32Type, "tmp_");
    }
}
