import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.*;
import org.bytedeco.javacpp.BytePointer;

import java.io.IOException;

import static org.bytedeco.llvm.global.LLVM.LLVMDisposeMessage;
import static org.bytedeco.llvm.global.LLVM.LLVMPrintModuleToFile;

public class Main
{
    public static boolean judge = true;
    public static final BytePointer error = new BytePointer();
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("input path is required");
        }
        String source = args[0];
        CharStream input = CharStreams.fromFileName(source);
        SysYLexer sysYLexer = new SysYLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(sysYLexer);
        SysYParser sysYParser = new SysYParser(tokens);
//        reflect the error
        sysYParser.removeErrorListeners();
        ANTLRErrorListener myErrorListener = new BaseErrorListener(){
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
                System.err.println("Error type B at Line " + line + ':' + "mismatched input");
                judge = false;
            }
        };
        sysYParser.addErrorListener(myErrorListener);
        ParseTree tree = sysYParser.program();
        if(!judge){
            return;
        }
//        Visitor visitor = new Visitor();
//        visitor.visit(tree);
//        if(!visitor.hasFalse){//没有错误
//            visitor.cnt++;
//            visitor.visit(tree);
//        }
        MyVisitor visitor = new MyVisitor();
        visitor.visit(tree);
        if (LLVMPrintModuleToFile(visitor.module, args[1], error) != 0) {    // moudle是你自定义的LLVMModuleRef对象
            LLVMDisposeMessage(error);
        }

    }
}


