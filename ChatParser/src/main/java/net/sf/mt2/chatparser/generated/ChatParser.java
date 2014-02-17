/* Generated By:JavaCC: Do not edit this line. ChatParser.java */
        package net.sf.mt2.chatparser.generated;

        import java.util.List;
        import java.util.LinkedList;
        import net.sf.mt2.chatparser.ChatPart;
        import net.sf.mt2.chatparser.DiceExpressionPart;
        import net.sf.mt2.chatparser.TextPart;
        import net.sf.mt2.chatparser.ChatCommand;
        import net.sf.mt2.chatparser.UnknownCommandException;
        import net.sf.mt2.chatparser.ParsedChat;
        import net.sf.mt2.dice.*;
        import net.sf.mt2.dice.expression.*;
        import java.io.BufferedReader;
        import java.io.StringReader;
        import java.util.Collections;

        public class ChatParser implements ChatParserConstants {

                private String inputString;

                public ChatParser(String str) {
                        this(new BufferedReader(new StringReader(str)));
                        this.inputString=str;
                }

                public ParsedChat parse() throws UnknownCommandException {
                        try {
                                return START();
                        } catch(ParseException e) {
                                e.printStackTrace();
                                return new ParsedChat(new TextPart(inputString));
                        }
                }

  final private ParsedChat START() throws ParseException, UnknownCommandException {
    trace_call("START");
    try {
                ParsedChat list=new ParsedChat();
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CHAT_COMMAND_SLASH:
        jj_consume_token(CHAT_COMMAND_SLASH);
        CHAT_COMMAND(list);
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case 0:
          jj_consume_token(0);
          break;
        case TEXT:
                                         Token t;
          t = jj_consume_token(TEXT);
                                                if(!" ".equals(t.image))
                                                        list.add(new TextPart(t.image));
          break;
        default:
          jj_la1[0] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
        break;
      default:
        jj_la1[1] = jj_gen;
        ;
      }
      label_1:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case CODE_START:
        case TEXT:
          ;
          break;
        default:
          jj_la1[2] = jj_gen;
          break label_1;
        }
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case CODE_START:
          jj_consume_token(CODE_START);
                                 list.add(new DiceExpressionPart(DICE_EXPRESSION()));
          jj_consume_token(CODE_END);
          break;
        case TEXT:
                         Token t;
          t = jj_consume_token(TEXT);
                                if(list.getLast() instanceof TextPart)
                                        ((TextPart)list.getLast()).append(t.image);
                                else
                                        list.add(new TextPart(t.image));
          break;
        default:
          jj_la1[3] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
      }
      jj_consume_token(0);
                 {if (true) return list;}
    throw new Error("Missing return statement in function");
    } finally {
      trace_return("START");
    }
  }

  final private void CHAT_COMMAND(ParsedChat pc) throws ParseException, UnknownCommandException {
    trace_call("CHAT_COMMAND");
    try {
                 ChatCommand cc;
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case MACRO_EXEC:
        jj_consume_token(MACRO_EXEC);
                     cc=ChatCommand.MACRO_EXEC;
        break;
      case CLEAR_CHAT:
        jj_consume_token(CLEAR_CHAT);
                         cc=ChatCommand.CLEAR_CHAT;
        break;
      case EMIT:
        jj_consume_token(EMIT);
                     cc=ChatCommand.EMIT;
        break;
      case EMOTE:
        jj_consume_token(EMOTE);
                     cc=ChatCommand.EMOTE;
        break;
      case GM:
        jj_consume_token(GM);
                     cc=ChatCommand.GM;
        break;
      case GOTO:
        jj_consume_token(GOTO);
                            cc=ChatCommand.GOTO;
                            Token a1,a2;
        a1 = jj_consume_token(FLOAT);
        a2 = jj_consume_token(FLOAT);
                     pc.setArguments(a1.image,a2.image);
        break;
      case IMPERSONATE:
        jj_consume_token(IMPERSONATE);
                     cc=ChatCommand.IMPERSONATE;Token guid;
        guid = jj_consume_token(GUID);
                     pc.setArguments(guid.image);
        break;
      case OOC:
        jj_consume_token(OOC);
                     cc=ChatCommand.OOC;
        break;
      case REPLY:
        jj_consume_token(REPLY);
                     cc=ChatCommand.REPLY;
        break;
      case ROLL:
        jj_consume_token(ROLL);
                     cc=ChatCommand.ROLL;
                     pc.add(new DiceExpressionPart(DICE_EXPRESSION()));
        break;
      case ROLL_GM:
        jj_consume_token(ROLL_GM);
                     cc=ChatCommand.ROLL_GM;
                     pc.add(new DiceExpressionPart(DICE_EXPRESSION()));
        break;
      case ROLL_ME:
        jj_consume_token(ROLL_ME);
                     cc=ChatCommand.ROLL_ME;
                     pc.add(new DiceExpressionPart(DICE_EXPRESSION()));
        break;
      case ROLL_SECRET:
        jj_consume_token(ROLL_SECRET);
                     cc=ChatCommand.ROLL_SECRET;
                     pc.add(new DiceExpressionPart(DICE_EXPRESSION()));
        break;
      case SELF:
        jj_consume_token(SELF);
                     cc=ChatCommand.SELF;
        break;
      case TABLE:
        jj_consume_token(TABLE);
                     cc=ChatCommand.TABLE;
        break;
      case TOKEN_MACRO:
        jj_consume_token(TOKEN_MACRO);
                     cc=ChatCommand.TOKEN_MACRO;
        break;
      case TOKEN_SPEECH:
        jj_consume_token(TOKEN_SPEECH);
                     cc=ChatCommand.TOKEN_SPEECH;
        break;
      case WHISPER:
        jj_consume_token(WHISPER);
                     cc=ChatCommand.WHISPER;
        break;
      case UNKNOWN_COMMAND:
                         StringBuilder sb=new StringBuilder();
        label_2:
        while (true) {
                                 Token t;
          t = jj_consume_token(UNKNOWN_COMMAND);
                                 sb.append(t.image);
          switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
          case UNKNOWN_COMMAND:
            ;
            break;
          default:
            jj_la1[4] = jj_gen;
            break label_2;
          }
        }
                         {if (true) throw new UnknownCommandException(sb.toString());}
        break;
      default:
        jj_la1[5] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
                  token_source.SwitchTo(AFTER_CC) ;
                 pc.setChatCommand(cc);
    } finally {
      trace_return("CHAT_COMMAND");
    }
  }

  final private DiceExpression DICE_EXPRESSION() throws ParseException {
    trace_call("DICE_EXPRESSION");
    try {
                 DiceExpression first=TERM();
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case PLUS:
      case MINUS:
                         AdditionNode a=new AdditionNode(first);
        label_3:
        while (true) {
          switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
          case PLUS:
            jj_consume_token(PLUS);
                                 a.add(TERM());
            break;
          case MINUS:
            jj_consume_token(MINUS);
                                 a.subtract(TERM());
            break;
          default:
            jj_la1[6] = jj_gen;
            jj_consume_token(-1);
            throw new ParseException();
          }
          switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
          case PLUS:
          case MINUS:
            ;
            break;
          default:
            jj_la1[7] = jj_gen;
            break label_3;
          }
        }
                         {if (true) return a;}
        break;
      default:
        jj_la1[8] = jj_gen;
        ;
      }
                 {if (true) return first;}
    throw new Error("Missing return statement in function");
    } finally {
      trace_return("DICE_EXPRESSION");
    }
  }

  final private DiceExpression TERM() throws ParseException {
    trace_call("TERM");
    try {
                 DiceExpression first=PRIMARY();
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case MULTIPLICATION:
      case DIVISION:
                         MultiplicationNode a=new MultiplicationNode(first);
        label_4:
        while (true) {
          switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
          case MULTIPLICATION:
            jj_consume_token(MULTIPLICATION);
                                 a.multiplyBy(PRIMARY());
            break;
          case DIVISION:
            jj_consume_token(DIVISION);
                                 a.divideBy(PRIMARY());
            break;
          default:
            jj_la1[9] = jj_gen;
            jj_consume_token(-1);
            throw new ParseException();
          }
          switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
          case MULTIPLICATION:
          case DIVISION:
            ;
            break;
          default:
            jj_la1[10] = jj_gen;
            break label_4;
          }
        }
                         {if (true) return a;}
        break;
      default:
        jj_la1[11] = jj_gen;
        ;
      }
                 {if (true) return first;}
    throw new Error("Missing return statement in function");
    } finally {
      trace_return("TERM");
    }
  }

  final private DiceExpression PRIMARY() throws ParseException {
    trace_call("PRIMARY");
    try {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case NUMBER:
                         int number;
        number = NUMBER();
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case DICE_D:
        case DICE_FUDGE:
        case DICE_UBIQUITY:
        case DICE_SHADOWRUN_EXPLODING_GREMLIN:
        case DICE_SHADOWRUN_EXPLODING:
        case DICE_SHADOWRUN_GREMLIN:
        case DICE_SHADOWRUN:
                                 Dice d;
          d = DICE(number);
                                 {if (true) return new DiceNode(d);}
          break;
        default:
          jj_la1[12] = jj_gen;
                                 {if (true) return new NumberNode(number);}
        }
        break;
      case PARANTHESES_LEFT:
                         DiceExpression de;
        jj_consume_token(PARANTHESES_LEFT);
        de = DICE_EXPRESSION();
        jj_consume_token(PARANTHESES_RIGHT);
                         {if (true) return de;}
        break;
      case MINUS:
        jj_consume_token(MINUS);
                                 {if (true) return new NegationNode(PRIMARY());}
        break;
      default:
        jj_la1[13] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    throw new Error("Missing return statement in function");
    } finally {
      trace_return("PRIMARY");
    }
  }

  final private Dice DICE(int count) throws ParseException {
    trace_call("DICE");
    try {
                int type;
                int extra;
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case DICE_FUDGE:
      case DICE_UBIQUITY:
      case DICE_SHADOWRUN_EXPLODING_GREMLIN:
      case DICE_SHADOWRUN_EXPLODING:
      case DICE_SHADOWRUN_GREMLIN:
      case DICE_SHADOWRUN:
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case DICE_FUDGE:
          jj_consume_token(DICE_FUDGE);
                         {if (true) return new FudgeDice(count);}
          break;
        case DICE_UBIQUITY:
          jj_consume_token(DICE_UBIQUITY);
                         {if (true) return new UbiquityDice(count);}
          break;
        case DICE_SHADOWRUN:
          jj_consume_token(DICE_SHADOWRUN);
                         {if (true) return new ShadowrunBasicDice(count);}
          break;
        case DICE_SHADOWRUN_EXPLODING:
          jj_consume_token(DICE_SHADOWRUN_EXPLODING);
                         {if (true) return new ExplodingShadowrunBasicDice(count);}
          break;
        case DICE_SHADOWRUN_GREMLIN:
          jj_consume_token(DICE_SHADOWRUN_GREMLIN);
          extra = NUMBER();
                         {if (true) return new ShadowrunGremlinDice(count,extra);}
          break;
        case DICE_SHADOWRUN_EXPLODING_GREMLIN:
          jj_consume_token(DICE_SHADOWRUN_EXPLODING_GREMLIN);
          extra = NUMBER();
                         {if (true) return new ExplodingShadowrunGremlinDice(count,extra);}
          break;
        default:
          jj_la1[14] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
        break;
      case DICE_D:
        jj_consume_token(DICE_D);
        type = NUMBER();
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case DICE_D:
        case DICE_KEEP:
        case DICE_REROLL:
        case DICE_SUCCESS:
        case DICE_EXPLODING_SUCCESS:
        case DICE_EXPLODING:
        case DICE_OPEN:
        case DICE_HERO_STUN:
        case DICE_HERO_BODY:
          switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
          case DICE_D:
            jj_consume_token(DICE_D);
            extra = NUMBER();
                                         {if (true) return new DropDice(count,type,extra);}
            break;
          case DICE_KEEP:
            jj_consume_token(DICE_KEEP);
            extra = NUMBER();
                                         {if (true) return new KeepDice(count,type,extra);}
            break;
          case DICE_REROLL:
            jj_consume_token(DICE_REROLL);
            extra = NUMBER();
                                         {if (true) return new RerollDice(count,type,extra);}
            break;
          case DICE_SUCCESS:
            jj_consume_token(DICE_SUCCESS);
            extra = NUMBER();
                                         {if (true) return new SuccessDice(count,type,extra);}
            break;
          case DICE_EXPLODING_SUCCESS:
            jj_consume_token(DICE_EXPLODING_SUCCESS);
            extra = NUMBER();
                                         {if (true) return new ExplodingSuccessDice(count,type,extra);}
            break;
          case DICE_EXPLODING:
            jj_consume_token(DICE_EXPLODING);
                                         {if (true) return new ExplodingDice(count,type);}
            break;
          case DICE_OPEN:
            jj_consume_token(DICE_OPEN);
                                         {if (true) return new OpenDice(count,type);}
            break;
          case DICE_HERO_STUN:
            jj_consume_token(DICE_HERO_STUN);
                                         {if (true) return new HeroStunDice(count,type);}
            break;
          case DICE_HERO_BODY:
            jj_consume_token(DICE_HERO_BODY);
                                         {if (true) return new HeroBodyDice(count,type);}
            break;
          default:
            jj_la1[15] = jj_gen;
            jj_consume_token(-1);
            throw new ParseException();
          }
          break;
        default:
          jj_la1[16] = jj_gen;
          ;
        }
                         {if (true) return new SimpleDice(count,type);}
        break;
      default:
        jj_la1[17] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    throw new Error("Missing return statement in function");
    } finally {
      trace_return("DICE");
    }
  }

  final private int NUMBER() throws ParseException {
    trace_call("NUMBER");
    try {
                Token number;
      number = jj_consume_token(NUMBER);
                 {if (true) return Integer.parseInt(number.image);}
    throw new Error("Missing return statement in function");
    } finally {
      trace_return("NUMBER");
    }
  }

  /** Generated Token Manager. */
  public ChatParserTokenManager token_source;
  SimpleCharStream jj_input_stream;
  /** Current token. */
  public Token token;
  /** Next token. */
  public Token jj_nt;
  private int jj_ntk;
  private int jj_gen;
  final private int[] jj_la1 = new int[18];
  static private int[] jj_la1_0;
  static private int[] jj_la1_1;
  static {
      jj_la1_init_0();
      jj_la1_init_1();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {0x2000001,0x800000,0x3000000,0x3000000,0x400000,0x7ffc7e,0x18000000,0x18000000,0x18000000,0x60000000,0x60000000,0x60000000,0x0,0x90000000,0x0,0x0,0x0,0x0,};
   }
   private static void jj_la1_init_1() {
      jj_la1_1 = new int[] {0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x1f804,0x2,0x1f800,0x7fc,0x7fc,0x1f804,};
   }

  /** Constructor with InputStream. */
  public ChatParser(java.io.InputStream stream) {
     this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public ChatParser(java.io.InputStream stream, String encoding) {
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new ChatParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 18; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 18; i++) jj_la1[i] = -1;
  }

  /** Constructor. */
  public ChatParser(java.io.Reader stream) {
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new ChatParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 18; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 18; i++) jj_la1[i] = -1;
  }

  /** Constructor with generated Token Manager. */
  public ChatParser(ChatParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 18; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(ChatParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 18; i++) jj_la1[i] = -1;
  }

  private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      trace_token(token, "");
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }


/** Get the next Token. */
  final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
      trace_token(token, " (in getNextToken)");
    return token;
  }

/** Get the specific Token. */
  final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  private java.util.List jj_expentries = new java.util.ArrayList();
  private int[] jj_expentry;
  private int jj_kind = -1;

  /** Generate ParseException. */
  public ParseException generateParseException() {
    jj_expentries.clear();
    boolean[] la1tokens = new boolean[49];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 18; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
          if ((jj_la1_1[i] & (1<<j)) != 0) {
            la1tokens[32+j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 49; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.add(jj_expentry);
      }
    }
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = (int[])jj_expentries.get(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  private int trace_indent = 0;
  private boolean trace_enabled = true;

/** Enable tracing. */
  final public void enable_tracing() {
    trace_enabled = true;
  }

/** Disable tracing. */
  final public void disable_tracing() {
    trace_enabled = false;
  }

  private void trace_call(String s) {
    if (trace_enabled) {
      for (int i = 0; i < trace_indent; i++) { System.out.print(" "); }
      System.out.println("Call:   " + s);
    }
    trace_indent = trace_indent + 2;
  }

  private void trace_return(String s) {
    trace_indent = trace_indent - 2;
    if (trace_enabled) {
      for (int i = 0; i < trace_indent; i++) { System.out.print(" "); }
      System.out.println("Return: " + s);
    }
  }

  private void trace_token(Token t, String where) {
    if (trace_enabled) {
      for (int i = 0; i < trace_indent; i++) { System.out.print(" "); }
      System.out.print("Consumed token: <" + tokenImage[t.kind]);
      if (t.kind != 0 && !tokenImage[t.kind].equals("\"" + t.image + "\"")) {
        System.out.print(": \"" + t.image + "\"");
      }
      System.out.println(" at line " + t.beginLine + " column " + t.beginColumn + ">" + where);
    }
  }

  private void trace_scan(Token t1, int t2) {
    if (trace_enabled) {
      for (int i = 0; i < trace_indent; i++) { System.out.print(" "); }
      System.out.print("Visited token: <" + tokenImage[t1.kind]);
      if (t1.kind != 0 && !tokenImage[t1.kind].equals("\"" + t1.image + "\"")) {
        System.out.print(": \"" + t1.image + "\"");
      }
      System.out.println(" at line " + t1.beginLine + " column " + t1.beginColumn + ">; Expected token: <" + tokenImage[t2] + ">");
    }
  }

        }
