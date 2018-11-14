// Generated from /home/aleric/repos/ammensa/src/main/antlr4/CourseGrammar.g4 by ANTLR 4.7

	package net.ammensa.parse.antlr;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class CourseGrammarParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.7", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, SEP=3, NL=4, WS=5, TEXT=6;
	public static final int
		RULE_courses = 0, RULE_course = 1, RULE_ingredients = 2, RULE_content = 3;
	public static final String[] ruleNames = {
		"courses", "course", "ingredients", "content"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'('", "')'", "'\u00E7'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, "SEP", "NL", "WS", "TEXT"
	};
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

	@Override
	public String getGrammarFileName() { return "CourseGrammar.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public CourseGrammarParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class CoursesContext extends ParserRuleContext {
		public List<CourseContext> course() {
			return getRuleContexts(CourseContext.class);
		}
		public CourseContext course(int i) {
			return getRuleContext(CourseContext.class,i);
		}
		public List<TerminalNode> SEP() { return getTokens(CourseGrammarParser.SEP); }
		public TerminalNode SEP(int i) {
			return getToken(CourseGrammarParser.SEP, i);
		}
		public List<TerminalNode> NL() { return getTokens(CourseGrammarParser.NL); }
		public TerminalNode NL(int i) {
			return getToken(CourseGrammarParser.NL, i);
		}
		public CoursesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_courses; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CourseGrammarListener ) ((CourseGrammarListener)listener).enterCourses(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CourseGrammarListener ) ((CourseGrammarListener)listener).exitCourses(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CourseGrammarVisitor ) return ((CourseGrammarVisitor<? extends T>)visitor).visitCourses(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CoursesContext courses() throws RecognitionException {
		CoursesContext _localctx = new CoursesContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_courses);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(12); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(8);
				course();
				setState(9);
				match(SEP);
				setState(10);
				match(NL);
				}
				}
				setState(14); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==NL || _la==TEXT );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CourseContext extends ParserRuleContext {
		public Token name;
		public TerminalNode TEXT() { return getToken(CourseGrammarParser.TEXT, 0); }
		public List<TerminalNode> NL() { return getTokens(CourseGrammarParser.NL); }
		public TerminalNode NL(int i) {
			return getToken(CourseGrammarParser.NL, i);
		}
		public List<IngredientsContext> ingredients() {
			return getRuleContexts(IngredientsContext.class);
		}
		public IngredientsContext ingredients(int i) {
			return getRuleContext(IngredientsContext.class,i);
		}
		public CourseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_course; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CourseGrammarListener ) ((CourseGrammarListener)listener).enterCourse(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CourseGrammarListener ) ((CourseGrammarListener)listener).exitCourse(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CourseGrammarVisitor ) return ((CourseGrammarVisitor<? extends T>)visitor).visitCourse(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CourseContext course() throws RecognitionException {
		CourseContext _localctx = new CourseContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_course);
		int _la;
		try {
			setState(36);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(17);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NL) {
					{
					setState(16);
					match(NL);
					}
				}

				setState(19);
				((CourseContext)_localctx).name = match(TEXT);
				setState(21);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NL) {
					{
					setState(20);
					match(NL);
					}
				}

				setState(29);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__0 || _la==TEXT) {
					{
					{
					setState(23);
					ingredients();
					setState(25);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==NL) {
						{
						setState(24);
						match(NL);
						}
					}

					}
					}
					setState(31);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(33);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NL) {
					{
					setState(32);
					match(NL);
					}
				}

				setState(35);
				((CourseContext)_localctx).name = match(TEXT);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class IngredientsContext extends ParserRuleContext {
		public List<IngredientsContext> ingredients() {
			return getRuleContexts(IngredientsContext.class);
		}
		public IngredientsContext ingredients(int i) {
			return getRuleContext(IngredientsContext.class,i);
		}
		public ContentContext content() {
			return getRuleContext(ContentContext.class,0);
		}
		public IngredientsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ingredients; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CourseGrammarListener ) ((CourseGrammarListener)listener).enterIngredients(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CourseGrammarListener ) ((CourseGrammarListener)listener).exitIngredients(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CourseGrammarVisitor ) return ((CourseGrammarVisitor<? extends T>)visitor).visitIngredients(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IngredientsContext ingredients() throws RecognitionException {
		IngredientsContext _localctx = new IngredientsContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_ingredients);
		int _la;
		try {
			int _alt;
			setState(61);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(39); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(38);
						match(T__0);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(41); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,7,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(46);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__0 || _la==TEXT) {
					{
					{
					setState(43);
					ingredients();
					}
					}
					setState(48);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(50); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(49);
						match(T__1);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(52); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,9,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(55);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__0) {
					{
					setState(54);
					match(T__0);
					}
				}

				setState(57);
				content();
				setState(59);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
				case 1:
					{
					setState(58);
					match(T__1);
					}
					break;
				}
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ContentContext extends ParserRuleContext {
		public List<TerminalNode> TEXT() { return getTokens(CourseGrammarParser.TEXT); }
		public TerminalNode TEXT(int i) {
			return getToken(CourseGrammarParser.TEXT, i);
		}
		public List<TerminalNode> NL() { return getTokens(CourseGrammarParser.NL); }
		public TerminalNode NL(int i) {
			return getToken(CourseGrammarParser.NL, i);
		}
		public ContentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_content; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CourseGrammarListener ) ((CourseGrammarListener)listener).enterContent(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CourseGrammarListener ) ((CourseGrammarListener)listener).exitContent(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CourseGrammarVisitor ) return ((CourseGrammarVisitor<? extends T>)visitor).visitContent(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ContentContext content() throws RecognitionException {
		ContentContext _localctx = new ContentContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_content);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(63);
			match(TEXT);
			setState(68);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,13,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(64);
					match(NL);
					setState(65);
					match(TEXT);
					}
					} 
				}
				setState(70);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,13,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\bJ\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\3\2\3\2\3\2\3\2\6\2\17\n\2\r\2\16\2\20\3\3\5\3\24\n"+
		"\3\3\3\3\3\5\3\30\n\3\3\3\3\3\5\3\34\n\3\7\3\36\n\3\f\3\16\3!\13\3\3\3"+
		"\5\3$\n\3\3\3\5\3\'\n\3\3\4\6\4*\n\4\r\4\16\4+\3\4\7\4/\n\4\f\4\16\4\62"+
		"\13\4\3\4\6\4\65\n\4\r\4\16\4\66\3\4\5\4:\n\4\3\4\3\4\5\4>\n\4\5\4@\n"+
		"\4\3\5\3\5\3\5\7\5E\n\5\f\5\16\5H\13\5\3\5\2\2\6\2\4\6\b\2\2\2S\2\16\3"+
		"\2\2\2\4&\3\2\2\2\6?\3\2\2\2\bA\3\2\2\2\n\13\5\4\3\2\13\f\7\5\2\2\f\r"+
		"\7\6\2\2\r\17\3\2\2\2\16\n\3\2\2\2\17\20\3\2\2\2\20\16\3\2\2\2\20\21\3"+
		"\2\2\2\21\3\3\2\2\2\22\24\7\6\2\2\23\22\3\2\2\2\23\24\3\2\2\2\24\25\3"+
		"\2\2\2\25\27\7\b\2\2\26\30\7\6\2\2\27\26\3\2\2\2\27\30\3\2\2\2\30\37\3"+
		"\2\2\2\31\33\5\6\4\2\32\34\7\6\2\2\33\32\3\2\2\2\33\34\3\2\2\2\34\36\3"+
		"\2\2\2\35\31\3\2\2\2\36!\3\2\2\2\37\35\3\2\2\2\37 \3\2\2\2 \'\3\2\2\2"+
		"!\37\3\2\2\2\"$\7\6\2\2#\"\3\2\2\2#$\3\2\2\2$%\3\2\2\2%\'\7\b\2\2&\23"+
		"\3\2\2\2&#\3\2\2\2\'\5\3\2\2\2(*\7\3\2\2)(\3\2\2\2*+\3\2\2\2+)\3\2\2\2"+
		"+,\3\2\2\2,\60\3\2\2\2-/\5\6\4\2.-\3\2\2\2/\62\3\2\2\2\60.\3\2\2\2\60"+
		"\61\3\2\2\2\61\64\3\2\2\2\62\60\3\2\2\2\63\65\7\4\2\2\64\63\3\2\2\2\65"+
		"\66\3\2\2\2\66\64\3\2\2\2\66\67\3\2\2\2\67@\3\2\2\28:\7\3\2\298\3\2\2"+
		"\29:\3\2\2\2:;\3\2\2\2;=\5\b\5\2<>\7\4\2\2=<\3\2\2\2=>\3\2\2\2>@\3\2\2"+
		"\2?)\3\2\2\2?9\3\2\2\2@\7\3\2\2\2AF\7\b\2\2BC\7\6\2\2CE\7\b\2\2DB\3\2"+
		"\2\2EH\3\2\2\2FD\3\2\2\2FG\3\2\2\2G\t\3\2\2\2HF\3\2\2\2\20\20\23\27\33"+
		"\37#&+\60\669=?F";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}