// Generated from ./src/SysYLexer.g4 by ANTLR 4.9.1
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class SysYLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.9.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		CONST=1, INT=2, VOID=3, IF=4, ELSE=5, WHILE=6, BREAK=7, CONTINUE=8, RETURN=9, 
		PLUS=10, MINUS=11, MUL=12, DIV=13, MOD=14, ASSIGN=15, EQ=16, NEQ=17, LT=18, 
		GT=19, LE=20, GE=21, NOT=22, AND=23, OR=24, L_PAREN=25, R_PAREN=26, L_BRACE=27, 
		R_BRACE=28, L_BRACKT=29, R_BRACKT=30, COMMA=31, SEMICOLON=32, IDENT=33, 
		INTEGER_CONST=34, WS=35, LINE_COMMENT=36, MULTILINE_COMMENT=37;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"CONST", "INT", "VOID", "IF", "ELSE", "WHILE", "BREAK", "CONTINUE", "RETURN", 
			"PLUS", "MINUS", "MUL", "DIV", "MOD", "ASSIGN", "EQ", "NEQ", "LT", "GT", 
			"LE", "GE", "NOT", "AND", "OR", "L_PAREN", "R_PAREN", "L_BRACE", "R_BRACE", 
			"L_BRACKT", "R_BRACKT", "COMMA", "SEMICOLON", "IDENT", "INTEGER_CONST", 
			"WS", "LINE_COMMENT", "MULTILINE_COMMENT"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'const'", "'int'", "'void'", "'if'", "'else'", "'while'", "'break'", 
			"'continue'", "'return'", "'+'", "'-'", "'*'", "'/'", "'%'", "'='", "'=='", 
			"'!='", "'<'", "'>'", "'<='", "'>='", "'!'", "'&&'", "'||'", "'('", "')'", 
			"'{'", "'}'", "'['", "']'", "','", "';'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "CONST", "INT", "VOID", "IF", "ELSE", "WHILE", "BREAK", "CONTINUE", 
			"RETURN", "PLUS", "MINUS", "MUL", "DIV", "MOD", "ASSIGN", "EQ", "NEQ", 
			"LT", "GT", "LE", "GE", "NOT", "AND", "OR", "L_PAREN", "R_PAREN", "L_BRACE", 
			"R_BRACE", "L_BRACKT", "R_BRACKT", "COMMA", "SEMICOLON", "IDENT", "INTEGER_CONST", 
			"WS", "LINE_COMMENT", "MULTILINE_COMMENT"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public SysYLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "SysYLexer.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\'\u010d\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\3\2\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3"+
		"\3\3\3\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7"+
		"\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3"+
		"\t\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\f\3\f\3\r\3\r\3\16\3\16\3\17"+
		"\3\17\3\20\3\20\3\21\3\21\3\21\3\22\3\22\3\22\3\23\3\23\3\24\3\24\3\25"+
		"\3\25\3\25\3\26\3\26\3\26\3\27\3\27\3\30\3\30\3\30\3\31\3\31\3\31\3\32"+
		"\3\32\3\33\3\33\3\34\3\34\3\35\3\35\3\36\3\36\3\37\3\37\3 \3 \3!\3!\3"+
		"\"\3\"\7\"\u00b7\n\"\f\"\16\"\u00ba\13\"\3#\3#\3#\3#\6#\u00c0\n#\r#\16"+
		"#\u00c1\3#\3#\3#\3#\6#\u00c8\n#\r#\16#\u00c9\3#\3#\3#\3#\6#\u00d0\n#\r"+
		"#\16#\u00d1\3#\3#\3#\3#\6#\u00d8\n#\r#\16#\u00d9\3#\3#\6#\u00de\n#\r#"+
		"\16#\u00df\3#\3#\3#\7#\u00e5\n#\f#\16#\u00e8\13#\5#\u00ea\n#\3$\6$\u00ed"+
		"\n$\r$\16$\u00ee\3$\3$\3%\3%\3%\3%\7%\u00f7\n%\f%\16%\u00fa\13%\3%\3%"+
		"\3%\3%\3&\3&\3&\3&\7&\u0104\n&\f&\16&\u0107\13&\3&\3&\3&\3&\3&\4\u00f8"+
		"\u0105\2\'\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33"+
		"\17\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30/\31\61\32\63\33\65\34\67"+
		"\359\36;\37= ?!A\"C#E$G%I&K\'\3\2\n\5\2C\\aac|\6\2\62;C\\aac|\4\2\62;"+
		"CH\4\2\62;ch\3\2\629\3\2\63;\3\2\62;\5\2\13\f\17\17\"\"\2\u011c\2\3\3"+
		"\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2"+
		"\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3"+
		"\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2"+
		"%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61"+
		"\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2"+
		"\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2\2I"+
		"\3\2\2\2\2K\3\2\2\2\3M\3\2\2\2\5S\3\2\2\2\7W\3\2\2\2\t\\\3\2\2\2\13_\3"+
		"\2\2\2\rd\3\2\2\2\17j\3\2\2\2\21p\3\2\2\2\23y\3\2\2\2\25\u0080\3\2\2\2"+
		"\27\u0082\3\2\2\2\31\u0084\3\2\2\2\33\u0086\3\2\2\2\35\u0088\3\2\2\2\37"+
		"\u008a\3\2\2\2!\u008c\3\2\2\2#\u008f\3\2\2\2%\u0092\3\2\2\2\'\u0094\3"+
		"\2\2\2)\u0096\3\2\2\2+\u0099\3\2\2\2-\u009c\3\2\2\2/\u009e\3\2\2\2\61"+
		"\u00a1\3\2\2\2\63\u00a4\3\2\2\2\65\u00a6\3\2\2\2\67\u00a8\3\2\2\29\u00aa"+
		"\3\2\2\2;\u00ac\3\2\2\2=\u00ae\3\2\2\2?\u00b0\3\2\2\2A\u00b2\3\2\2\2C"+
		"\u00b4\3\2\2\2E\u00e9\3\2\2\2G\u00ec\3\2\2\2I\u00f2\3\2\2\2K\u00ff\3\2"+
		"\2\2MN\7e\2\2NO\7q\2\2OP\7p\2\2PQ\7u\2\2QR\7v\2\2R\4\3\2\2\2ST\7k\2\2"+
		"TU\7p\2\2UV\7v\2\2V\6\3\2\2\2WX\7x\2\2XY\7q\2\2YZ\7k\2\2Z[\7f\2\2[\b\3"+
		"\2\2\2\\]\7k\2\2]^\7h\2\2^\n\3\2\2\2_`\7g\2\2`a\7n\2\2ab\7u\2\2bc\7g\2"+
		"\2c\f\3\2\2\2de\7y\2\2ef\7j\2\2fg\7k\2\2gh\7n\2\2hi\7g\2\2i\16\3\2\2\2"+
		"jk\7d\2\2kl\7t\2\2lm\7g\2\2mn\7c\2\2no\7m\2\2o\20\3\2\2\2pq\7e\2\2qr\7"+
		"q\2\2rs\7p\2\2st\7v\2\2tu\7k\2\2uv\7p\2\2vw\7w\2\2wx\7g\2\2x\22\3\2\2"+
		"\2yz\7t\2\2z{\7g\2\2{|\7v\2\2|}\7w\2\2}~\7t\2\2~\177\7p\2\2\177\24\3\2"+
		"\2\2\u0080\u0081\7-\2\2\u0081\26\3\2\2\2\u0082\u0083\7/\2\2\u0083\30\3"+
		"\2\2\2\u0084\u0085\7,\2\2\u0085\32\3\2\2\2\u0086\u0087\7\61\2\2\u0087"+
		"\34\3\2\2\2\u0088\u0089\7\'\2\2\u0089\36\3\2\2\2\u008a\u008b\7?\2\2\u008b"+
		" \3\2\2\2\u008c\u008d\7?\2\2\u008d\u008e\7?\2\2\u008e\"\3\2\2\2\u008f"+
		"\u0090\7#\2\2\u0090\u0091\7?\2\2\u0091$\3\2\2\2\u0092\u0093\7>\2\2\u0093"+
		"&\3\2\2\2\u0094\u0095\7@\2\2\u0095(\3\2\2\2\u0096\u0097\7>\2\2\u0097\u0098"+
		"\7?\2\2\u0098*\3\2\2\2\u0099\u009a\7@\2\2\u009a\u009b\7?\2\2\u009b,\3"+
		"\2\2\2\u009c\u009d\7#\2\2\u009d.\3\2\2\2\u009e\u009f\7(\2\2\u009f\u00a0"+
		"\7(\2\2\u00a0\60\3\2\2\2\u00a1\u00a2\7~\2\2\u00a2\u00a3\7~\2\2\u00a3\62"+
		"\3\2\2\2\u00a4\u00a5\7*\2\2\u00a5\64\3\2\2\2\u00a6\u00a7\7+\2\2\u00a7"+
		"\66\3\2\2\2\u00a8\u00a9\7}\2\2\u00a98\3\2\2\2\u00aa\u00ab\7\177\2\2\u00ab"+
		":\3\2\2\2\u00ac\u00ad\7]\2\2\u00ad<\3\2\2\2\u00ae\u00af\7_\2\2\u00af>"+
		"\3\2\2\2\u00b0\u00b1\7.\2\2\u00b1@\3\2\2\2\u00b2\u00b3\7=\2\2\u00b3B\3"+
		"\2\2\2\u00b4\u00b8\t\2\2\2\u00b5\u00b7\t\3\2\2\u00b6\u00b5\3\2\2\2\u00b7"+
		"\u00ba\3\2\2\2\u00b8\u00b6\3\2\2\2\u00b8\u00b9\3\2\2\2\u00b9D\3\2\2\2"+
		"\u00ba\u00b8\3\2\2\2\u00bb\u00bc\7\62\2\2\u00bc\u00bd\7z\2\2\u00bd\u00bf"+
		"\3\2\2\2\u00be\u00c0\t\4\2\2\u00bf\u00be\3\2\2\2\u00c0\u00c1\3\2\2\2\u00c1"+
		"\u00bf\3\2\2\2\u00c1\u00c2\3\2\2\2\u00c2\u00ea\3\2\2\2\u00c3\u00c4\7\62"+
		"\2\2\u00c4\u00c5\7Z\2\2\u00c5\u00c7\3\2\2\2\u00c6\u00c8\t\4\2\2\u00c7"+
		"\u00c6\3\2\2\2\u00c8\u00c9\3\2\2\2\u00c9\u00c7\3\2\2\2\u00c9\u00ca\3\2"+
		"\2\2\u00ca\u00ea\3\2\2\2\u00cb\u00cc\7\62\2\2\u00cc\u00cd\7z\2\2\u00cd"+
		"\u00cf\3\2\2\2\u00ce\u00d0\t\5\2\2\u00cf\u00ce\3\2\2\2\u00d0\u00d1\3\2"+
		"\2\2\u00d1\u00cf\3\2\2\2\u00d1\u00d2\3\2\2\2\u00d2\u00ea\3\2\2\2\u00d3"+
		"\u00d4\7\62\2\2\u00d4\u00d5\7Z\2\2\u00d5\u00d7\3\2\2\2\u00d6\u00d8\t\5"+
		"\2\2\u00d7\u00d6\3\2\2\2\u00d8\u00d9\3\2\2\2\u00d9\u00d7\3\2\2\2\u00d9"+
		"\u00da\3\2\2\2\u00da\u00ea\3\2\2\2\u00db\u00dd\7\62\2\2\u00dc\u00de\t"+
		"\6\2\2\u00dd\u00dc\3\2\2\2\u00de\u00df\3\2\2\2\u00df\u00dd\3\2\2\2\u00df"+
		"\u00e0\3\2\2\2\u00e0\u00ea\3\2\2\2\u00e1\u00ea\7\62\2\2\u00e2\u00e6\t"+
		"\7\2\2\u00e3\u00e5\t\b\2\2\u00e4\u00e3\3\2\2\2\u00e5\u00e8\3\2\2\2\u00e6"+
		"\u00e4\3\2\2\2\u00e6\u00e7\3\2\2\2\u00e7\u00ea\3\2\2\2\u00e8\u00e6\3\2"+
		"\2\2\u00e9\u00bb\3\2\2\2\u00e9\u00c3\3\2\2\2\u00e9\u00cb\3\2\2\2\u00e9"+
		"\u00d3\3\2\2\2\u00e9\u00db\3\2\2\2\u00e9\u00e1\3\2\2\2\u00e9\u00e2\3\2"+
		"\2\2\u00eaF\3\2\2\2\u00eb\u00ed\t\t\2\2\u00ec\u00eb\3\2\2\2\u00ed\u00ee"+
		"\3\2\2\2\u00ee\u00ec\3\2\2\2\u00ee\u00ef\3\2\2\2\u00ef\u00f0\3\2\2\2\u00f0"+
		"\u00f1\b$\2\2\u00f1H\3\2\2\2\u00f2\u00f3\7\61\2\2\u00f3\u00f4\7\61\2\2"+
		"\u00f4\u00f8\3\2\2\2\u00f5\u00f7\13\2\2\2\u00f6\u00f5\3\2\2\2\u00f7\u00fa"+
		"\3\2\2\2\u00f8\u00f9\3\2\2\2\u00f8\u00f6\3\2\2\2\u00f9\u00fb\3\2\2\2\u00fa"+
		"\u00f8\3\2\2\2\u00fb\u00fc\7\f\2\2\u00fc\u00fd\3\2\2\2\u00fd\u00fe\b%"+
		"\2\2\u00feJ\3\2\2\2\u00ff\u0100\7\61\2\2\u0100\u0101\7,\2\2\u0101\u0105"+
		"\3\2\2\2\u0102\u0104\13\2\2\2\u0103\u0102\3\2\2\2\u0104\u0107\3\2\2\2"+
		"\u0105\u0106\3\2\2\2\u0105\u0103\3\2\2\2\u0106\u0108\3\2\2\2\u0107\u0105"+
		"\3\2\2\2\u0108\u0109\7,\2\2\u0109\u010a\7\61\2\2\u010a\u010b\3\2\2\2\u010b"+
		"\u010c\b&\2\2\u010cL\3\2\2\2\16\2\u00b8\u00c1\u00c9\u00d1\u00d9\u00df"+
		"\u00e6\u00e9\u00ee\u00f8\u0105\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}