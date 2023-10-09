import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.Objects;

public class Visitor extends SysYParserBaseVisitor<Void>{
    public boolean hasFalse = false;
    public int cnt = 1;//进行两次遍历，在第一次中判断是否出现类型检查相关错误

    private GlobalScope globalScope = null;
    private Scope currentScope = null;
    private int localScopeCounter = 0;

    @Override
    public Void visitChildren(RuleNode node) {
        if(cnt == 2){
            if(node.getRuleContext().getChildCount() != 0){
                for(int i = 0; i < 2 * node.getRuleContext().depth() - 2; i++){
                    System.err.print(" ");
                }
                System.err.println(Character.toUpperCase(SysYParser.ruleNames[node.getRuleContext().getRuleIndex()].charAt(0)) +
                        SysYParser.ruleNames[node.getRuleContext().getRuleIndex()].substring(1));
            }
        }
        Void result = this.defaultResult();
        int n = node.getChildCount();
        for(int i = 0; i < n && this.shouldVisitNextChild(node, result); ++i) {
            ParseTree c = node.getChild(i);
            Void childResult = c.accept(this);
            result = this.aggregateResult(result, childResult);
        }

        return result;
    }

    @Override
    public Void visitTerminal(TerminalNode node) {
        if(cnt == 2){
            String[][] table =  new String[][] {
                    {
                            "CONST", "INT", "VOID", "IF", "ELSE", "WHILE", "BREAK", "CONTINUE", "RETURN",
                            "PLUS", "MINUS", "MUL", "DIV", "MOD", "ASSIGN", "EQ", "NEQ", "LT", "GT", "LE", "GE", "NOT", "AND", "OR",
                            "L_PAREN", "R_PAREN", "L_BRACE", "R_BRACE", "L_BRACKT", "R_BRACKT", "COMMA", "SEMICOLON",
                            "IDENT",
                            "INTEGER_CONST"
                    },
                    {
                            "orange", "orange", "orange", "orange", "orange", "orange", "orange", "orange", "orange",
                            "blue", "blue", "blue", "blue", "blue", "blue", "blue", "blue", "blue", "blue", "blue", "blue", "blue", "blue", "blue",
                            "", "", "", "", "", "", "", "",
                            "red",
                            "green"
                    }
            };
            RuleNode par = (RuleNode) node.getParent();
            for (int i = 0; i < 24; i++){
                if(node.getSymbol().getType() - 1 == i){
                    for (int j = 0; j < 2 * par.getRuleContext().depth(); j++) {
                        System.err.print(" ");
                    }
                    System.err.println(node.getSymbol().getText() + ' ' + table[0][i] + '[' + table[1][i] + ']');
                    break;
                }
            }
            for (int i = 32; i < 34; i++){
                if(node.getSymbol().getType() - 1 == i){
                    for (int j = 0; j < 2 * par.getRuleContext().depth(); j++) {
                        System.err.print(" ");
                    }
                    if(i == 33){
                        long value;
                        if(Objects.equals(node.getSymbol().getText(), "0")){
                            System.err.println("0" + " INTEGER_CONST" + '[' + table[1][i] + ']');
                        } else if (node.getSymbol().getText().startsWith("0x") || node.getSymbol().getText().startsWith("0X")) {
                            value = Long.parseLong(node.getSymbol().getText().substring(2), 16);
                            System.err.println(value + " INTEGER_CONST" + '[' + table[1][i] + ']');
                        } else if (node.getSymbol().getText().charAt(0) == '0' && (node.getSymbol().getText().charAt(1) != 'x' && node.getSymbol().getText().charAt(1) != 'X')) {
                            value = Long.parseLong(node.getSymbol().getText().substring(1), 8);
                            System.err.println(value + " INTEGER_CONST" +  '[' + table[1][i] + ']');
                        }else {
                            value = Integer.parseInt(node.getSymbol().getText());
                            System.err.println(value + " INTEGER_CONST" + '[' + table[1][i] + ']');
                        }
                    }else {
                        System.err.println(node.getSymbol().getText() + ' ' + table[0][i] + '[' + table[1][i] + ']');
                    }
                    break;
                }
            }
        }
        return this.defaultResult();
    }

//    ----------------------------------------
//    产生作用域的三个区域

    //开启全局作用域
    @Override
    public Void visitProgram(SysYParser.ProgramContext ctx) {
        globalScope = new GlobalScope(null);
        currentScope = globalScope;
        Void ret = super.visitProgram(ctx);
        currentScope = currentScope.getEnclosingScope();
        return ret;
    }

    //函数作用域
    @Override
    public Void visitFuncDef(SysYParser.FuncDefContext ctx) {
        String retType = ctx.funcType().getText();
        globalScope.resolve(retType);
        String funcName = ctx.IDENT().getText();
        if (currentScope.getSymbols().containsKey(funcName)) {//定义函数时与已定义的函数同名
            System.err.println("Error type 4 at Line " + ctx.getStart().getLine() + ": Error!");
            hasFalse = true;
            return null;
        }
        ArrayList<Type> paramsType = new ArrayList<>();
        FunctionSymbol func = new FunctionSymbol(funcName, currentScope);
        func.type = new FunctionType((Type) globalScope.resolve(retType), paramsType);
        currentScope.define(func);
        currentScope = func;
        Void ret = super.visitFuncDef(ctx);
        currentScope = currentScope.getEnclosingScope();
        return ret;
    }

    //局部作用域
    @Override
    public Void visitBlock(SysYParser.BlockContext ctx) {
        LocalScope localScope = new LocalScope(currentScope);
        String localScopeName = localScope.getName() + localScopeCounter;
        localScope.setName(localScopeName);
        localScopeCounter++;
        currentScope = localScope;
        Void ret = super.visitBlock(ctx);
        currentScope = currentScope.getEnclosingScope();
        return ret;
    }

//    ----------------------------------------
//    符号表来源三个出处

    @Override
    public Void visitVarDecl(SysYParser.VarDeclContext ctx) {
//        变量声明
        String type = ctx.bType().getText();
//        System.err.println(type);
        for (SysYParser.VarDefContext varDefContext : ctx.varDef()){
            Type varType = (Type) globalScope.resolve(type);
            String varName = varDefContext.IDENT().getText();
            if(currentScope.getSymbols().containsKey(varName)){//变量类型只可能是int
//                变量重复声明
                System.err.println("Error type 3 at Line " + ctx.getStart().getLine() + ": Error!");
                hasFalse = true;
                continue;
            }
//            判断是否为数组类型
            for (SysYParser.ConstExpContext constExpContext : varDefContext.constExp()) {
                int elementCount = Integer.parseInt(String.valueOf(constExpContext.getText()));
                varType = new ArrayType(elementCount, varType);
            }
            VariableSymbol varSymbol = new VariableSymbol(varName, varType);
            currentScope.define(varSymbol);//加入符号表中
        }
        return super.visitVarDecl(ctx);
    }

    @Override
    public Void visitConstDecl(SysYParser.ConstDeclContext ctx) {
        String type = ctx.bType().getText();
        for (SysYParser.ConstDefContext constDefContext : ctx.constDef()){
            Type varType = (Type) globalScope.resolve(type);
            String varName = constDefContext.IDENT().getText();
            if(currentScope.getSymbols().containsKey(varName)){//变量类型只可能是int
//                变量重复声明
                System.err.println("Error type 3 at Line " + ctx.getStart().getLine() + ": Error!");
                hasFalse = true;
                continue;
            }
//            判断是否为数组类型
            for (SysYParser.ConstExpContext constExpContext : constDefContext.constExp()) {
                int elementCount = Integer.parseInt(String.valueOf(constExpContext.getText()));
                varType = new ArrayType(elementCount, varType);
            }
            VariableSymbol varSymbol = new VariableSymbol(varName, varType);
            currentScope.define(varSymbol);//加入符号表中
        }
        return super.visitConstDecl(ctx);
    }

    @Override
    public Void visitFuncFParam(SysYParser.FuncFParamContext ctx) {
        String varTypeName = ctx.bType().getText();
        Type varType = (Type) globalScope.resolve(varTypeName);
//        根据右括号数目判断是多少维数数组
        for (TerminalNode ignored : ctx.L_BRACKT()) {
            varType = new ArrayType(0, varType);
        }
        String varName = ctx.IDENT().getText();
        VariableSymbol varSymbol = new VariableSymbol(varName, varType);
        if (currentScope.getSymbols().containsKey(varName)) {//冲突存在
            System.err.println("Error type 3 at Line " + ctx.getStart().getLine() + ": Error!");
            hasFalse = true;
            return null;
        } else {//不存在，加入符号表中
            currentScope.define(varSymbol);
            ((FunctionSymbol) currentScope).type.paramsType.add(varType);
        }
        return super.visitFuncFParam(ctx);
    }

//    -------------------------------
//    遍历过程中需要类型检查的区域


    @Override
    public Void visitVarDef(SysYParser.VarDefContext ctx) {
        String varName = ctx.IDENT().getText();
        Symbol symbol = currentScope.resolve(varName);
//        多维数组声明时形式保证正确，不加以考虑
        if(ctx.ASSIGN() != null){
            if(ctx.initVal().exp() != null && (symbol.getType() instanceof ArrayType)){
                System.err.println("Error type 5 at Line " + ctx.getStart().getLine() + ": Error!");
                hasFalse = true;
                return null;
            } else if (ctx.initVal().exp() == null && !(symbol.getType() instanceof ArrayType)) {
                System.err.println("Error type 5 at Line " + ctx.getStart().getLine() + ": Error!");
                hasFalse = true;
                return null;
            }
        }
        return super.visitVarDef(ctx);
    }

    @Override
    public Void visitConstDef(SysYParser.ConstDefContext ctx) {
        String constName = ctx.IDENT().getText();
        Symbol symbol = currentScope.resolve(constName);
//        多维数组声明时形式保证正确，不加以考虑
        if(ctx.constInitVal().constExp() != null){
            if(ctx.constInitVal().constExp().exp() != null && (symbol.getType() instanceof ArrayType)){
                System.err.println("Error type 5 at Line " + ctx.getStart().getLine() + ": Error!");
                hasFalse = true;
                return null;
            } else if (ctx.constInitVal().constExp().exp() == null && !(symbol.getType() instanceof ArrayType)) {
                System.err.println("Error type 5 at Line " + ctx.getStart().getLine() + ": Error!");
                hasFalse = true;
                return null;
            }
        }
        return super.visitConstDef(ctx);
    }

    @Override
    public Void visitLVal(SysYParser.LValContext ctx) {
        String varName = ctx.IDENT().getText();
        Symbol symbol = currentScope.resolve(varName);
        if(symbol == null){
            //变量未声明,Error1
            System.err.println("Error type 1 at Line " + ctx.getStart().getLine() + ": Error!");
            hasFalse = true;
            return null;
        }
        Type varType = symbol.getType();
//        if(ctx.exp().size() != 0 && !(varType instanceof ArrayType)){
////            对非数组使用下标运算符,Error9
//            System.err.println("Error type 9 at Line " + ctx.getStart().getLine() + ": Error!");
//            hasFalse = true;
//            return null;
//        }
        for (int i = 0; i < ctx.exp().size(); ++i) {
            if (varType instanceof ArrayType) {
                varType = ((ArrayType) varType).subType;
            } else {
                System.err.println("Error type 9 at Line " + ctx.getStart().getLine() + ": Error!");
                hasFalse = true;
                return null;
            }
        }
        return super.visitLVal(ctx);
    }


    @Override
    public Void visitAssignStmt(SysYParser.AssignStmtContext ctx) {
        String lValName = ctx.lVal().IDENT().getText();
        Symbol lValSymbol = currentScope.resolve(lValName);
        if(lValSymbol != null){
            Type lValType = lValSymbol.getType();
            for (SysYParser.ExpContext ignored : ctx.lVal().exp()) {
                if (lValType instanceof ArrayType) {
                    lValType = ((ArrayType) lValType).subType;
                }
            }
            Type rValType = getExpType(ctx.exp());
            if (lValType instanceof FunctionType) {
                System.err.println("Error type 11 at Line " + ctx.getStart().getLine() + ": Error!");
                hasFalse = true;
                return null;
            }
//            赋值号两侧类型不匹配：赋值号两侧的类型不相同
            if(!Objects.equals(lValType.toString(), rValType.toString())){
//                System.err.println(lValType);
//                System.err.println(rValType);
                System.err.println("Error type 5 at Line " + ctx.getStart().getLine() + ": Error!");
                hasFalse = true;
                return null;
            }
        }
        return super.visitAssignStmt(ctx);
    }

    @Override
    public Void visitReturnStmt(SysYParser.ReturnStmtContext ctx) {
        Type retType = new BasicTypeSymbol("void");
        if(ctx.exp() != null){
            retType = getExpType(ctx.exp());
        }
        Scope tmpScope = currentScope;
        while (!(tmpScope instanceof FunctionSymbol)) {
            tmpScope = tmpScope.getEnclosingScope();
        }
        Type expectedType = ((FunctionSymbol) tmpScope).type.retType;
//        System.err.println(expectedType);
        if(!Objects.equals(expectedType.toString(), retType.toString())){
            System.err.println("Error type 7 at Line " + ctx.getStart().getLine() + ": Error!");
            hasFalse = true;
            return null;
        }
        return super.visitReturnStmt(ctx);
    }

    @Override
    public Void visitPlusExp(SysYParser.PlusExpContext ctx) {
        Type op1Type = getExpType(ctx.exp(0));
        Type op2Type = getExpType(ctx.exp(1));
        if(!Objects.equals(op1Type.toString(), "int") || !Objects.equals(op2Type.toString(), "int")){
            System.err.println("Error type 6 at Line " + ctx.getStart().getLine() + ": Error!");
            hasFalse = true;
            return null;
        }
        return super.visitPlusExp(ctx);
    }

    @Override
    public Void visitMulExp(SysYParser.MulExpContext ctx) {
        Type op1Type = getExpType(ctx.exp(0));
        Type op2Type = getExpType(ctx.exp(1));
        if(!Objects.equals(op1Type.toString(), "int") || !Objects.equals(op2Type.toString(), "int")){
            System.err.println("Error type 6 at Line " + ctx.getStart().getLine() + ": Error!");
            hasFalse = true;
            return null;
        }
        return super.visitMulExp(ctx);
    }

    @Override
    public Void visitCallFuncExp(SysYParser.CallFuncExpContext ctx) {
        String funcName = ctx.IDENT().getText();
        Symbol symbol = currentScope.resolve(funcName);
        if (symbol == null) {//函数未定义：使用了没有声明和定义的函数
            System.err.println("Error type 2 at Line " + ctx.getStart().getLine() + ": Error!");
            hasFalse = true;
            return null;
        } else if (!(symbol.getType() instanceof FunctionType)) {//对变量使用函数调用：对变量使用函数调用运算符
            System.err.println("Error type 10 at Line " + ctx.getStart().getLine() + ": Error!");
            hasFalse = true;
            return null;
        } else {//函数参数不适用：函数参数的数量或类型与函数声明的参数数量或类型不一致
            FunctionType functionType = (FunctionType) symbol.getType();
            ArrayList<Type> paramsType = functionType.paramsType;
            ArrayList<Type> argsType = new ArrayList<>();
            if (ctx.funcRParams() != null) {
                for (SysYParser.ParamContext paramContext : ctx.funcRParams().param()) {
                    argsType.add(getExpType(paramContext.exp()));
                }
            }
            boolean Judge = true;
            int len1 = paramsType.size();
            int len2 = argsType.size();
            if(len1 != len2){
                Judge = false;
            }else {
                for (int i = 0; i < len1; i++){
                    if(!Objects.equals(paramsType.get(i).toString(), argsType.get(i).toString())){
                        Judge = false;
                        break;
                    }
                }
            }

            if (!Judge) {
                System.err.println("Error type 8 at Line " + ctx.getStart().getLine() + ": Error!");
                hasFalse = true;
                return null;
            }
        }
        return super.visitCallFuncExp(ctx);
    }

    @Override
    public Void visitUnaryOpExp(SysYParser.UnaryOpExpContext ctx) {
        Type opType = getExpType(ctx.exp());
        if(!Objects.equals(opType.toString(), "int")){
            System.err.println("Error type 6 at Line " + ctx.getStart().getLine() + ": Error!");
            hasFalse = true;
            return null;
        }
        return super.visitUnaryOpExp(ctx);
    }

    @Override
    public Void visitExpCond(SysYParser.ExpCondContext ctx) {
        Type opType = getExpType(ctx.exp());
        if(!Objects.equals(opType.toString(), "int")){
            System.err.println("Error type 6 at Line " + ctx.getStart().getLine() + ": Error!");
            hasFalse = true;
            return null;
        }
        return super.visitExpCond(ctx);
    }

    private Type getExpType(SysYParser.ExpContext ctx){
        if(ctx instanceof SysYParser.CallFuncExpContext){
            String fucName = ((SysYParser.CallFuncExpContext) ctx).IDENT().getText();
            Symbol symbol = currentScope.resolve(fucName);
            if(symbol != null && symbol.getType() instanceof FunctionType){
                FunctionType functionType = (FunctionType) currentScope.resolve(fucName).getType();
                return functionType.retType;
            }
        } else if (ctx instanceof SysYParser.ExpParenthesisContext) {
            return getExpType(((SysYParser.ExpParenthesisContext) ctx).exp());
        } else if (ctx instanceof SysYParser.UnaryOpExpContext) {
            return getExpType(((SysYParser.UnaryOpExpContext) ctx).exp());
        } else if (ctx instanceof SysYParser.LvalExpContext) {
            String varName = ((SysYParser.LvalExpContext) ctx).lVal().IDENT().getText();
            Symbol symbol = currentScope.resolve(varName);
            Type varType = symbol.getType();
            for (SysYParser.ExpContext ignored : ((SysYParser.LvalExpContext) ctx).lVal().exp()) {
                if (varType instanceof ArrayType) {
                    varType = ((ArrayType) varType).subType;
                }
            }
            return varType;
        } else if (ctx instanceof SysYParser.NumberExpContext) {
            return new BasicTypeSymbol("int");
        } else if (ctx instanceof SysYParser.PlusExpContext) {
            Type op1Type = getExpType(((SysYParser.PlusExpContext) ctx).exp(0));
            Type op2Type = getExpType(((SysYParser.PlusExpContext) ctx).exp(1));
            if (op1Type.toString().equals("int") && op2Type.toString().equals("int")) {
                return op1Type;
            }
        } else if (ctx instanceof SysYParser.MulExpContext) {
            Type op1Type = getExpType(((SysYParser.MulExpContext) ctx).exp(0));
            Type op2Type = getExpType(((SysYParser.MulExpContext) ctx).exp(1));
            if (op1Type.toString().equals("int") && op2Type.toString().equals("int")) {
                return op1Type;
            }
        }
        return new BasicTypeSymbol("void");
    }
}
