package com.sondertara.excel.antlr.parser;// Generated from java-escape by ANTLR 4.11.1

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.RuntimeMetaData;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.VocabularyImpl;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue"})
public class VariableParserLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.11.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		STRING=1, QUOT=2, IDENTIFIER=3, NUMBER=4, DIGIT=5, AMP=6, ADD=7, MINUS=8, 
		MUL=9, DIV=10, POWER=11, PERCENT=12, ABS=13, EXCL=14, COLON=15, COMMA=16, 
		DOT=17, SEMI=18, LPAR=19, RPAR=20, EQ=21, NEQ=22, LTEQ=23, GTEQ=24, GT=25, 
		LT=26, AND_OP=27, OR_OP=28, LBRA=29, RBRA=30, LSQU=31, RSQU=32, WS=33;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"STRING", "QUOT", "IDENTIFIER", "NUMBER", "DIGIT", "AMP", "ADD", "MINUS", 
			"MUL", "DIV", "POWER", "PERCENT", "ABS", "EXCL", "COLON", "COMMA", "DOT", 
			"SEMI", "LPAR", "RPAR", "EQ", "NEQ", "LTEQ", "GTEQ", "GT", "LT", "AND_OP", 
			"OR_OP", "LBRA", "RBRA", "LSQU", "RSQU", "WS"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, "'\"'", null, null, null, "'&'", "'+'", "'-'", "'*'", "'/'", 
			"'^'", "'%'", "'$'", "'!'", "':'", "','", "'.'", "';'", "'('", "')'", 
			null, null, "'<='", "'>='", "'>'", "'<'", "'&&'", "'||'", "'{'", "'}'", 
			"'['", "']'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "STRING", "QUOT", "IDENTIFIER", "NUMBER", "DIGIT", "AMP", "ADD", 
			"MINUS", "MUL", "DIV", "POWER", "PERCENT", "ABS", "EXCL", "COLON", "COMMA", 
			"DOT", "SEMI", "LPAR", "RPAR", "EQ", "NEQ", "LTEQ", "GTEQ", "GT", "LT", 
			"AND_OP", "OR_OP", "LBRA", "RBRA", "LSQU", "RSQU", "WS"
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


	public VariableParserLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "VariableParser.g4"; }

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
		"\u0004\u0000!\u00af\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002\u0001"+
		"\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004"+
		"\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007"+
		"\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b"+
		"\u0007\u000b\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002"+
		"\u000f\u0007\u000f\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002"+
		"\u0012\u0007\u0012\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002"+
		"\u0015\u0007\u0015\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002"+
		"\u0018\u0007\u0018\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0002"+
		"\u001b\u0007\u001b\u0002\u001c\u0007\u001c\u0002\u001d\u0007\u001d\u0002"+
		"\u001e\u0007\u001e\u0002\u001f\u0007\u001f\u0002 \u0007 \u0001\u0000\u0001"+
		"\u0000\u0005\u0000F\b\u0000\n\u0000\f\u0000I\t\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0005\u0002Q\b"+
		"\u0002\n\u0002\f\u0002T\t\u0002\u0001\u0003\u0004\u0003W\b\u0003\u000b"+
		"\u0003\f\u0003X\u0001\u0003\u0001\u0003\u0004\u0003]\b\u0003\u000b\u0003"+
		"\f\u0003^\u0003\u0003a\b\u0003\u0001\u0004\u0004\u0004d\b\u0004\u000b"+
		"\u0004\f\u0004e\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001"+
		"\u0007\u0001\u0007\u0001\b\u0001\b\u0001\t\u0001\t\u0001\n\u0001\n\u0001"+
		"\u000b\u0001\u000b\u0001\f\u0001\f\u0001\r\u0001\r\u0001\u000e\u0001\u000e"+
		"\u0001\u000f\u0001\u000f\u0001\u0010\u0001\u0010\u0001\u0011\u0001\u0011"+
		"\u0001\u0012\u0001\u0012\u0001\u0013\u0001\u0013\u0001\u0014\u0001\u0014"+
		"\u0001\u0014\u0003\u0014\u0089\b\u0014\u0001\u0015\u0001\u0015\u0001\u0015"+
		"\u0001\u0015\u0003\u0015\u008f\b\u0015\u0001\u0016\u0001\u0016\u0001\u0016"+
		"\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0018\u0001\u0018\u0001\u0019"+
		"\u0001\u0019\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001b\u0001\u001b"+
		"\u0001\u001b\u0001\u001c\u0001\u001c\u0001\u001d\u0001\u001d\u0001\u001e"+
		"\u0001\u001e\u0001\u001f\u0001\u001f\u0001 \u0004 \u00aa\b \u000b \f "+
		"\u00ab\u0001 \u0001 \u0001G\u0000!\u0001\u0001\u0003\u0002\u0005\u0003"+
		"\u0007\u0004\t\u0005\u000b\u0006\r\u0007\u000f\b\u0011\t\u0013\n\u0015"+
		"\u000b\u0017\f\u0019\r\u001b\u000e\u001d\u000f\u001f\u0010!\u0011#\u0012"+
		"%\u0013\'\u0014)\u0015+\u0016-\u0017/\u00181\u00193\u001a5\u001b7\u001c"+
		"9\u001d;\u001e=\u001f? A!\u0001\u0000\u0004\u0003\u0000AZ__az\u0004\u0000"+
		"09AZ__az\u0001\u000009\u0003\u0000\t\n\r\r  \u00b7\u0000\u0001\u0001\u0000"+
		"\u0000\u0000\u0000\u0003\u0001\u0000\u0000\u0000\u0000\u0005\u0001\u0000"+
		"\u0000\u0000\u0000\u0007\u0001\u0000\u0000\u0000\u0000\t\u0001\u0000\u0000"+
		"\u0000\u0000\u000b\u0001\u0000\u0000\u0000\u0000\r\u0001\u0000\u0000\u0000"+
		"\u0000\u000f\u0001\u0000\u0000\u0000\u0000\u0011\u0001\u0000\u0000\u0000"+
		"\u0000\u0013\u0001\u0000\u0000\u0000\u0000\u0015\u0001\u0000\u0000\u0000"+
		"\u0000\u0017\u0001\u0000\u0000\u0000\u0000\u0019\u0001\u0000\u0000\u0000"+
		"\u0000\u001b\u0001\u0000\u0000\u0000\u0000\u001d\u0001\u0000\u0000\u0000"+
		"\u0000\u001f\u0001\u0000\u0000\u0000\u0000!\u0001\u0000\u0000\u0000\u0000"+
		"#\u0001\u0000\u0000\u0000\u0000%\u0001\u0000\u0000\u0000\u0000\'\u0001"+
		"\u0000\u0000\u0000\u0000)\u0001\u0000\u0000\u0000\u0000+\u0001\u0000\u0000"+
		"\u0000\u0000-\u0001\u0000\u0000\u0000\u0000/\u0001\u0000\u0000\u0000\u0000"+
		"1\u0001\u0000\u0000\u0000\u00003\u0001\u0000\u0000\u0000\u00005\u0001"+
		"\u0000\u0000\u0000\u00007\u0001\u0000\u0000\u0000\u00009\u0001\u0000\u0000"+
		"\u0000\u0000;\u0001\u0000\u0000\u0000\u0000=\u0001\u0000\u0000\u0000\u0000"+
		"?\u0001\u0000\u0000\u0000\u0000A\u0001\u0000\u0000\u0000\u0001C\u0001"+
		"\u0000\u0000\u0000\u0003L\u0001\u0000\u0000\u0000\u0005N\u0001\u0000\u0000"+
		"\u0000\u0007V\u0001\u0000\u0000\u0000\tc\u0001\u0000\u0000\u0000\u000b"+
		"g\u0001\u0000\u0000\u0000\ri\u0001\u0000\u0000\u0000\u000fk\u0001\u0000"+
		"\u0000\u0000\u0011m\u0001\u0000\u0000\u0000\u0013o\u0001\u0000\u0000\u0000"+
		"\u0015q\u0001\u0000\u0000\u0000\u0017s\u0001\u0000\u0000\u0000\u0019u"+
		"\u0001\u0000\u0000\u0000\u001bw\u0001\u0000\u0000\u0000\u001dy\u0001\u0000"+
		"\u0000\u0000\u001f{\u0001\u0000\u0000\u0000!}\u0001\u0000\u0000\u0000"+
		"#\u007f\u0001\u0000\u0000\u0000%\u0081\u0001\u0000\u0000\u0000\'\u0083"+
		"\u0001\u0000\u0000\u0000)\u0088\u0001\u0000\u0000\u0000+\u008e\u0001\u0000"+
		"\u0000\u0000-\u0090\u0001\u0000\u0000\u0000/\u0093\u0001\u0000\u0000\u0000"+
		"1\u0096\u0001\u0000\u0000\u00003\u0098\u0001\u0000\u0000\u00005\u009a"+
		"\u0001\u0000\u0000\u00007\u009d\u0001\u0000\u0000\u00009\u00a0\u0001\u0000"+
		"\u0000\u0000;\u00a2\u0001\u0000\u0000\u0000=\u00a4\u0001\u0000\u0000\u0000"+
		"?\u00a6\u0001\u0000\u0000\u0000A\u00a9\u0001\u0000\u0000\u0000CG\u0003"+
		"\u0003\u0001\u0000DF\t\u0000\u0000\u0000ED\u0001\u0000\u0000\u0000FI\u0001"+
		"\u0000\u0000\u0000GH\u0001\u0000\u0000\u0000GE\u0001\u0000\u0000\u0000"+
		"HJ\u0001\u0000\u0000\u0000IG\u0001\u0000\u0000\u0000JK\u0003\u0003\u0001"+
		"\u0000K\u0002\u0001\u0000\u0000\u0000LM\u0005\"\u0000\u0000M\u0004\u0001"+
		"\u0000\u0000\u0000NR\u0007\u0000\u0000\u0000OQ\u0007\u0001\u0000\u0000"+
		"PO\u0001\u0000\u0000\u0000QT\u0001\u0000\u0000\u0000RP\u0001\u0000\u0000"+
		"\u0000RS\u0001\u0000\u0000\u0000S\u0006\u0001\u0000\u0000\u0000TR\u0001"+
		"\u0000\u0000\u0000UW\u0003\t\u0004\u0000VU\u0001\u0000\u0000\u0000WX\u0001"+
		"\u0000\u0000\u0000XV\u0001\u0000\u0000\u0000XY\u0001\u0000\u0000\u0000"+
		"Y`\u0001\u0000\u0000\u0000Z\\\u0003!\u0010\u0000[]\u0003\t\u0004\u0000"+
		"\\[\u0001\u0000\u0000\u0000]^\u0001\u0000\u0000\u0000^\\\u0001\u0000\u0000"+
		"\u0000^_\u0001\u0000\u0000\u0000_a\u0001\u0000\u0000\u0000`Z\u0001\u0000"+
		"\u0000\u0000`a\u0001\u0000\u0000\u0000a\b\u0001\u0000\u0000\u0000bd\u0007"+
		"\u0002\u0000\u0000cb\u0001\u0000\u0000\u0000de\u0001\u0000\u0000\u0000"+
		"ec\u0001\u0000\u0000\u0000ef\u0001\u0000\u0000\u0000f\n\u0001\u0000\u0000"+
		"\u0000gh\u0005&\u0000\u0000h\f\u0001\u0000\u0000\u0000ij\u0005+\u0000"+
		"\u0000j\u000e\u0001\u0000\u0000\u0000kl\u0005-\u0000\u0000l\u0010\u0001"+
		"\u0000\u0000\u0000mn\u0005*\u0000\u0000n\u0012\u0001\u0000\u0000\u0000"+
		"op\u0005/\u0000\u0000p\u0014\u0001\u0000\u0000\u0000qr\u0005^\u0000\u0000"+
		"r\u0016\u0001\u0000\u0000\u0000st\u0005%\u0000\u0000t\u0018\u0001\u0000"+
		"\u0000\u0000uv\u0005$\u0000\u0000v\u001a\u0001\u0000\u0000\u0000wx\u0005"+
		"!\u0000\u0000x\u001c\u0001\u0000\u0000\u0000yz\u0005:\u0000\u0000z\u001e"+
		"\u0001\u0000\u0000\u0000{|\u0005,\u0000\u0000| \u0001\u0000\u0000\u0000"+
		"}~\u0005.\u0000\u0000~\"\u0001\u0000\u0000\u0000\u007f\u0080\u0005;\u0000"+
		"\u0000\u0080$\u0001\u0000\u0000\u0000\u0081\u0082\u0005(\u0000\u0000\u0082"+
		"&\u0001\u0000\u0000\u0000\u0083\u0084\u0005)\u0000\u0000\u0084(\u0001"+
		"\u0000\u0000\u0000\u0085\u0089\u0005=\u0000\u0000\u0086\u0087\u0005=\u0000"+
		"\u0000\u0087\u0089\u0005=\u0000\u0000\u0088\u0085\u0001\u0000\u0000\u0000"+
		"\u0088\u0086\u0001\u0000\u0000\u0000\u0089*\u0001\u0000\u0000\u0000\u008a"+
		"\u008b\u0005<\u0000\u0000\u008b\u008f\u0005>\u0000\u0000\u008c\u008d\u0005"+
		"!\u0000\u0000\u008d\u008f\u0005=\u0000\u0000\u008e\u008a\u0001\u0000\u0000"+
		"\u0000\u008e\u008c\u0001\u0000\u0000\u0000\u008f,\u0001\u0000\u0000\u0000"+
		"\u0090\u0091\u0005<\u0000\u0000\u0091\u0092\u0005=\u0000\u0000\u0092."+
		"\u0001\u0000\u0000\u0000\u0093\u0094\u0005>\u0000\u0000\u0094\u0095\u0005"+
		"=\u0000\u0000\u00950\u0001\u0000\u0000\u0000\u0096\u0097\u0005>\u0000"+
		"\u0000\u00972\u0001\u0000\u0000\u0000\u0098\u0099\u0005<\u0000\u0000\u0099"+
		"4\u0001\u0000\u0000\u0000\u009a\u009b\u0005&\u0000\u0000\u009b\u009c\u0005"+
		"&\u0000\u0000\u009c6\u0001\u0000\u0000\u0000\u009d\u009e\u0005|\u0000"+
		"\u0000\u009e\u009f\u0005|\u0000\u0000\u009f8\u0001\u0000\u0000\u0000\u00a0"+
		"\u00a1\u0005{\u0000\u0000\u00a1:\u0001\u0000\u0000\u0000\u00a2\u00a3\u0005"+
		"}\u0000\u0000\u00a3<\u0001\u0000\u0000\u0000\u00a4\u00a5\u0005[\u0000"+
		"\u0000\u00a5>\u0001\u0000\u0000\u0000\u00a6\u00a7\u0005]\u0000\u0000\u00a7"+
		"@\u0001\u0000\u0000\u0000\u00a8\u00aa\u0007\u0003\u0000\u0000\u00a9\u00a8"+
		"\u0001\u0000\u0000\u0000\u00aa\u00ab\u0001\u0000\u0000\u0000\u00ab\u00a9"+
		"\u0001\u0000\u0000\u0000\u00ab\u00ac\u0001\u0000\u0000\u0000\u00ac\u00ad"+
		"\u0001\u0000\u0000\u0000\u00ad\u00ae\u0006 \u0000\u0000\u00aeB\u0001\u0000"+
		"\u0000\u0000\n\u0000GRX^`e\u0088\u008e\u00ab\u0001\u0006\u0000\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}