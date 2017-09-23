/* The following code was generated by JFlex 1.4.3 on 23/09/17 20:41 */

// ---------------------------------------------------------------------------
// Fichier d'entrée JFLex pour l'analyseur lexical
// ---------------------------------------------------------------------------

package fr.esisar.compilation.syntaxe;

import java_cup.runtime.*;
import java.util.Hashtable;

/**
 * La classe Lexical permet de realiser l'analyse lexicale.
 */


public class Lexical implements java_cup.runtime.Scanner {

  /** This character denotes the end of file */
  public static final int YYEOF = -1;

  /** initial size of the lookahead buffer */
  private static final int ZZ_BUFFERSIZE = 16384;

  /** lexical states */
  public static final int YYINITIAL = 0;

  /**
   * ZZ_LEXSTATE[l] is the state in the DFA for the lexical state l
   * ZZ_LEXSTATE[l+1] is the state in the DFA for the lexical state l
   *                  at the beginning of a line
   * l is of the form l = 2*k, k a non negative integer
   */
  private static final int ZZ_LEXSTATE[] = { 
     0, 0
  };

  /** 
   * Translates characters to character classes
   */
  private static final String ZZ_CMAP_PACKED = 
    "\11\0\1\14\1\40\25\0\1\10\1\11\1\13\5\11\1\46\1\47"+
    "\1\45\1\4\1\51\1\5\1\7\1\44\12\1\1\50\1\12\1\41"+
    "\1\43\1\42\2\11\1\15\1\22\1\2\1\17\1\6\1\33\1\23"+
    "\1\37\1\24\2\2\1\31\1\34\1\16\1\26\1\36\1\2\1\20"+
    "\1\32\1\30\1\35\1\25\1\27\1\2\1\21\1\2\1\52\1\11"+
    "\1\53\1\11\1\3\1\11\1\15\1\22\1\2\1\17\1\6\1\33"+
    "\1\23\1\37\1\24\2\2\1\31\1\34\1\16\1\26\1\36\1\2"+
    "\1\20\1\32\1\30\1\35\1\25\1\27\1\2\1\21\1\2\4\11"+
    "\uff81\0";

  /** 
   * Translates characters to character classes
   */
  private static final char [] ZZ_CMAP = zzUnpackCMap(ZZ_CMAP_PACKED);

  /** 
   * Translates DFA states to action switch labels.
   */
  private static final int [] ZZ_ACTION = zzUnpackAction();

  private static final String ZZ_ACTION_PACKED_0 =
    "\1\0\1\1\1\2\1\3\1\4\1\5\1\3\1\6"+
    "\1\7\1\10\1\1\14\3\1\7\1\11\1\12\1\13"+
    "\1\14\1\15\1\16\1\17\1\20\1\21\1\22\1\23"+
    "\1\0\1\7\2\3\1\24\1\0\1\25\6\3\1\26"+
    "\2\3\1\27\1\30\1\31\2\3\1\32\4\3\1\33"+
    "\1\34\1\35\1\36\1\37\1\40\1\3\1\41\2\3"+
    "\1\42\1\3\1\43\6\3\1\44\1\45\1\3\1\46"+
    "\2\3\1\47\1\3\1\50\3\3\1\51\1\3\1\52"+
    "\2\3\1\53\1\54\1\55\2\3\1\56\2\3\1\57"+
    "\1\60";

  private static int [] zzUnpackAction() {
    int [] result = new int[107];
    int offset = 0;
    offset = zzUnpackAction(ZZ_ACTION_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAction(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /** 
   * Translates a state to a row index in the transition table
   */
  private static final int [] ZZ_ROWMAP = zzUnpackRowMap();

  private static final String ZZ_ROWMAP_PACKED_0 =
    "\0\0\0\54\0\130\0\204\0\54\0\260\0\334\0\u0108"+
    "\0\u0134\0\54\0\u0160\0\u018c\0\u01b8\0\u01e4\0\u0210\0\u023c"+
    "\0\u0268\0\u0294\0\u02c0\0\u02ec\0\u0318\0\u0344\0\u0370\0\54"+
    "\0\u039c\0\u03c8\0\54\0\u03f4\0\54\0\54\0\54\0\u0420"+
    "\0\54\0\54\0\54\0\u044c\0\u0478\0\u04a4\0\u04d0\0\54"+
    "\0\u0160\0\u04fc\0\u0528\0\u0554\0\u0580\0\u05ac\0\u05d8\0\u0604"+
    "\0\u0630\0\u065c\0\u0688\0\204\0\204\0\204\0\u06b4\0\u06e0"+
    "\0\204\0\u070c\0\u0738\0\u0764\0\u0790\0\54\0\54\0\54"+
    "\0\54\0\u044c\0\204\0\u07bc\0\204\0\u07e8\0\u0814\0\204"+
    "\0\u0840\0\204\0\u086c\0\u0898\0\u08c4\0\u08f0\0\u091c\0\u0948"+
    "\0\204\0\204\0\u0974\0\204\0\u09a0\0\u09cc\0\204\0\u09f8"+
    "\0\204\0\u0a24\0\u0a50\0\u0a7c\0\204\0\u0aa8\0\204\0\u0ad4"+
    "\0\u0b00\0\204\0\204\0\204\0\u0b2c\0\u0b58\0\204\0\u0b84"+
    "\0\u0bb0\0\204\0\204";

  private static int [] zzUnpackRowMap() {
    int [] result = new int[107];
    int offset = 0;
    offset = zzUnpackRowMap(ZZ_ROWMAP_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackRowMap(String packed, int offset, int [] result) {
    int i = 0;  /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int high = packed.charAt(i++) << 16;
      result[j++] = high | packed.charAt(i++);
    }
    return j;
  }

  /** 
   * The transition table of the DFA
   */
  private static final int [] ZZ_TRANS = zzUnpackTrans();

  private static final String ZZ_TRANS_PACKED_0 =
    "\1\2\1\3\1\4\1\2\1\5\1\6\1\7\1\10"+
    "\1\11\1\2\1\12\1\13\1\11\1\14\1\15\1\16"+
    "\1\17\1\4\1\20\1\4\1\21\1\4\1\22\1\23"+
    "\1\24\2\4\1\25\1\26\1\4\1\27\1\4\1\30"+
    "\1\31\1\32\1\33\1\34\1\35\1\36\1\37\1\40"+
    "\1\41\1\42\1\43\55\0\1\3\5\0\1\44\45\0"+
    "\3\4\2\0\1\4\6\0\23\4\21\0\1\45\47\0"+
    "\3\4\2\0\1\4\6\0\1\4\1\46\12\4\1\47"+
    "\6\4\23\0\1\50\54\0\1\11\3\0\1\11\40\0"+
    "\12\51\1\52\1\0\23\51\1\0\13\51\1\0\3\4"+
    "\2\0\1\4\6\0\1\4\1\53\1\4\1\54\17\4"+
    "\15\0\3\4\2\0\1\55\6\0\11\4\1\56\6\4"+
    "\1\57\2\4\15\0\3\4\2\0\1\4\6\0\7\4"+
    "\1\60\1\4\1\61\11\4\15\0\3\4\2\0\1\62"+
    "\6\0\23\4\15\0\3\4\2\0\1\63\6\0\23\4"+
    "\15\0\3\4\2\0\1\4\6\0\16\4\1\64\4\4"+
    "\15\0\3\4\2\0\1\4\6\0\3\4\1\65\12\4"+
    "\1\66\4\4\15\0\3\4\2\0\1\4\6\0\3\4"+
    "\1\67\16\4\1\70\15\0\3\4\2\0\1\4\6\0"+
    "\11\4\1\71\10\4\1\72\15\0\3\4\2\0\1\4"+
    "\6\0\11\4\1\73\11\4\15\0\3\4\2\0\1\4"+
    "\6\0\11\4\1\74\11\4\15\0\3\4\2\0\1\4"+
    "\6\0\3\4\1\75\17\4\57\0\1\76\53\0\1\77"+
    "\53\0\1\100\53\0\1\101\11\0\1\102\53\0\37\45"+
    "\1\0\13\45\1\0\3\4\2\0\1\4\6\0\2\4"+
    "\1\103\20\4\15\0\3\4\2\0\1\4\6\0\15\4"+
    "\1\104\5\4\27\0\1\51\41\0\3\4\2\0\1\4"+
    "\6\0\2\4\1\105\20\4\15\0\3\4\2\0\1\4"+
    "\6\0\3\4\1\106\17\4\15\0\3\4\2\0\1\4"+
    "\6\0\12\4\1\107\10\4\15\0\3\4\2\0\1\4"+
    "\6\0\13\4\1\110\7\4\15\0\3\4\2\0\1\4"+
    "\6\0\14\4\1\111\6\4\15\0\3\4\2\0\1\4"+
    "\6\0\10\4\1\112\12\4\15\0\3\4\2\0\1\4"+
    "\6\0\12\4\1\113\10\4\15\0\3\4\2\0\1\4"+
    "\6\0\1\114\22\4\15\0\3\4\2\0\1\4\6\0"+
    "\6\4\1\115\14\4\15\0\3\4\2\0\1\4\6\0"+
    "\7\4\1\116\13\4\15\0\3\4\2\0\1\4\6\0"+
    "\7\4\1\117\13\4\15\0\3\4\2\0\1\120\6\0"+
    "\23\4\15\0\3\4\2\0\1\4\6\0\3\4\1\121"+
    "\17\4\15\0\3\4\2\0\1\4\6\0\2\4\1\122"+
    "\20\4\15\0\3\4\2\0\1\4\6\0\11\4\1\123"+
    "\11\4\15\0\3\4\2\0\1\124\6\0\23\4\15\0"+
    "\3\4\2\0\1\4\6\0\1\125\22\4\15\0\2\4"+
    "\1\126\2\0\1\4\6\0\23\4\15\0\3\4\2\0"+
    "\1\4\6\0\14\4\1\127\6\4\15\0\3\4\2\0"+
    "\1\4\6\0\1\4\1\130\21\4\15\0\3\4\2\0"+
    "\1\4\6\0\2\4\1\131\20\4\15\0\3\4\2\0"+
    "\1\4\6\0\7\4\1\132\13\4\15\0\3\4\2\0"+
    "\1\4\6\0\13\4\1\133\7\4\15\0\3\4\2\0"+
    "\1\4\6\0\14\4\1\134\6\4\15\0\3\4\2\0"+
    "\1\4\6\0\1\4\1\135\21\4\15\0\3\4\2\0"+
    "\1\4\6\0\6\4\1\136\14\4\15\0\3\4\2\0"+
    "\1\4\6\0\4\4\1\137\16\4\15\0\3\4\2\0"+
    "\1\4\6\0\14\4\1\140\6\4\15\0\3\4\2\0"+
    "\1\4\6\0\13\4\1\141\7\4\15\0\3\4\2\0"+
    "\1\4\6\0\1\4\1\142\21\4\15\0\3\4\2\0"+
    "\1\143\6\0\23\4\15\0\3\4\2\0\1\144\6\0"+
    "\23\4\15\0\3\4\2\0\1\4\6\0\3\4\1\145"+
    "\17\4\15\0\3\4\2\0\1\4\6\0\7\4\1\146"+
    "\13\4\15\0\3\4\2\0\1\4\6\0\11\4\1\147"+
    "\11\4\15\0\3\4\2\0\1\4\6\0\1\150\22\4"+
    "\15\0\3\4\2\0\1\4\6\0\1\4\1\151\21\4"+
    "\15\0\3\4\2\0\1\4\6\0\17\4\1\152\3\4"+
    "\15\0\3\4\2\0\1\153\6\0\23\4\14\0";

  private static int [] zzUnpackTrans() {
    int [] result = new int[3036];
    int offset = 0;
    offset = zzUnpackTrans(ZZ_TRANS_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackTrans(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      value--;
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /* error codes */
  private static final int ZZ_UNKNOWN_ERROR = 0;
  private static final int ZZ_NO_MATCH = 1;
  private static final int ZZ_PUSHBACK_2BIG = 2;

  /* error messages for the codes above */
  private static final String ZZ_ERROR_MSG[] = {
    "Unkown internal scanner error",
    "Error: could not match input",
    "Error: pushback value was too large"
  };

  /**
   * ZZ_ATTRIBUTE[aState] contains the attributes of state <code>aState</code>
   */
  private static final int [] ZZ_ATTRIBUTE = zzUnpackAttribute();

  private static final String ZZ_ATTRIBUTE_PACKED_0 =
    "\1\0\1\11\2\1\1\11\4\1\1\11\15\1\1\11"+
    "\2\1\1\11\1\1\3\11\1\1\3\11\1\0\3\1"+
    "\1\11\1\0\24\1\4\11\52\1";

  private static int [] zzUnpackAttribute() {
    int [] result = new int[107];
    int offset = 0;
    offset = zzUnpackAttribute(ZZ_ATTRIBUTE_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAttribute(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }

  /** the input device */
  private java.io.Reader zzReader;

  /** the current state of the DFA */
  private int zzState;

  /** the current lexical state */
  private int zzLexicalState = YYINITIAL;

  /** this buffer contains the current text to be matched and is
      the source of the yytext() string */
  private char zzBuffer[] = new char[ZZ_BUFFERSIZE];

  /** the textposition at the last accepting state */
  private int zzMarkedPos;

  /** the current text position in the buffer */
  private int zzCurrentPos;

  /** startRead marks the beginning of the yytext() string in the buffer */
  private int zzStartRead;

  /** endRead marks the last character in the buffer, that has been read
      from input */
  private int zzEndRead;

  /** number of newlines encountered up to the start of the matched text */
  private int yyline;

  /** the number of characters up to the start of the matched text */
  private int yychar;

  /**
   * the number of characters from the last newline up to the start of the 
   * matched text
   */
  private int yycolumn;

  /** 
   * zzAtBOL == true <=> the scanner is currently at the beginning of a line
   */
  private boolean zzAtBOL = true;

  /** zzAtEOF == true <=> the scanner is at the EOF */
  private boolean zzAtEOF;

  /** denotes if the user-EOF-code has already been executed */
  private boolean zzEOFDone;

  /* user code: */
   /**
    * Le dictionnaire associe à chaque mot réservé le code du lexème 
    * correspondant.
    */
   private final Hashtable<String,Integer> 
      dictionnaire = initialiserDictionnaire(); 

   /**
    * Initialisation du dictionnaire.
    */
   static Hashtable<String,Integer> initialiserDictionnaire() {
      Hashtable<String,Integer> dico = new Hashtable<String,Integer>();
      dico.put("and", sym.AND);
      dico.put("array", sym.ARRAY);
      dico.put("begin", sym.BEGIN);
      dico.put("div", sym.DIV);
      dico.put("do", sym.DO);
      dico.put("downto", sym.DOWNTO);
      dico.put("end", sym.END);
      dico.put("else", sym.ELSE);
      dico.put("for", sym.FOR);
      dico.put("if", sym.IF);
      dico.put("mod", sym.MOD);
      dico.put("new_line", sym.NEW_LINE);
      dico.put("not", sym.NOT);
      dico.put("null", sym.NULL);
      dico.put("of", sym.OF);
      dico.put("or", sym.OR);
      dico.put("program", sym.PROGRAM);
      dico.put("read", sym.READ);
      dico.put("then", sym.THEN);
      dico.put("to", sym.TO);
      dico.put("while", sym.WHILE);
      dico.put("write", sym.WRITE);
      return dico;
   }

   /**
    * Le numéro de la ligne courante.
    */
   int numLigne() {
      return yyline + 1;
   }

   private Symbol symbol(int code_lexeme) {
      return new Symbol(code_lexeme, numLigne(), 0);
   }

   private Symbol symbol(int code_lexeme, Object value) {
      return new Symbol(code_lexeme, numLigne(), 0, value);
   }

   /**
    * Convertit un code de lexème en String correspondante.
    */
   static String toString(int code_lexeme) {
      switch (code_lexeme) {
         case sym.IDF: 
            return "IDF";
         case sym.CONST_ENT:
            return "CONST_ENT";
         case sym.CONST_REEL:
            return "CONST_REEL";
         case sym.CONST_CHAINE:
            return "CONST_CHAINE";
         case sym.AND:
            return "AND";
         case sym.ARRAY:
            return "ARRAY";
         case sym.BEGIN:
            return "BEGIN";
         case sym.DIV:
            return "DIV";
         case sym.DO:
            return "DO";
         case sym.DOWNTO:
            return "DOWNTO";
         case sym.ELSE:
            return "ELSE";
         case sym.END:
            return "END";
         case sym.FOR:
            return "FOR";
         case sym.IF:
            return "IF";
         case sym.MOD:
            return "MOD";
         case sym.NEW_LINE:
            return "NEW_LINE";
         case sym.NOT:
            return "NOT";
         case sym.NULL:
            return "NULL";
         case sym.OF:
            return "OF";
         case sym.OR:
            return "OR";
         case sym.PROGRAM:
            return "PROGRAM";
         case sym.READ:
            return "READ";
         case sym.THEN:
            return "THEN";
         case sym.TO:
            return "TO";
         case sym.WHILE:
            return "WHILE";
         case sym.WRITE:
            return "WRITE";
         case sym.INF:
            return "INF";
         case sym.SUP:
            return "SUP";
         case sym.EGAL:
            return "EGAL";
         case sym.DIFF:
            return "DIFF";
         case sym.INF_EGAL:
            return "INF_EGAL";
         case sym.SUP_EGAL:
            return "SUP_EGAL";
         case sym.PLUS:
            return "PLUS";
         case sym.MOINS:
            return "MOINS";
         case sym.MULT:
            return "MULT";
         case sym.DIV_REEL:
            return "DIV_REEL";
         case sym.PAR_OUVR:
            return "PAR_OUVR";
         case sym.PAR_FERM:
            return "PAR_FERM";
         case sym.DOUBLE_POINT:
            return "DOUBLE_POINT";
         case sym.DEUX_POINTS:
            return "DEUX_POINTS";
         case sym.VIRGULE:
            return "VIRGULE";
         case sym.POINT_VIRGULE:
            return "POINT_VIRGULE";
         case sym.CROCH_OUVR:
            return "CROCH_OUVR";
         case sym.CROCH_FERM:
            return "CROCH_FERM";
         case sym.AFFECT:
            return "AFFECT";
         case sym.POINT:
            return "POINT";
         default:
            throw new ErreurInterneLexical(
               "Token inconnu dans toString(int code_lexeme)");
      }
   }


   /**
    * Convertit un lexème ("Symbole") en String correspondante.
    */
   static String toString(Symbol s) {
      String ts;
      switch (s.sym) {
         case sym.IDF:
            String nom = (String) s.value;
            ts = "(" + nom + ")";
            break;
         case sym.CONST_ENT:
            Integer n = (Integer) s.value;
            ts = "(" + n.intValue() + ")";
            break;
         case sym.CONST_REEL:
            Float f = (Float) s.value;
            ts = "(" + f.floatValue() + ")";
            break;
         case sym.CONST_CHAINE:
            String chaine = (String) s.value;
            ts = "(" + chaine + ")";
            break;

         default:
            ts = "";
      }
      return toString(s.sym) + ts + " " + s.left + ":" + s.right;
   }


  /**
   * Creates a new scanner
   * There is also a java.io.InputStream version of this constructor.
   *
   * @param   in  the java.io.Reader to read input from.
   */
  public Lexical(java.io.Reader in) {
    this.zzReader = in;
  }

  /**
   * Creates a new scanner.
   * There is also java.io.Reader version of this constructor.
   *
   * @param   in  the java.io.Inputstream to read input from.
   */
  public Lexical(java.io.InputStream in) {
    this(new java.io.InputStreamReader(in));
  }

  /** 
   * Unpacks the compressed character translation table.
   *
   * @param packed   the packed character translation table
   * @return         the unpacked character translation table
   */
  private static char [] zzUnpackCMap(String packed) {
    char [] map = new char[0x10000];
    int i = 0;  /* index in packed string  */
    int j = 0;  /* index in unpacked array */
    while (i < 162) {
      int  count = packed.charAt(i++);
      char value = packed.charAt(i++);
      do map[j++] = value; while (--count > 0);
    }
    return map;
  }


  /**
   * Refills the input buffer.
   *
   * @return      <code>false</code>, iff there was new input.
   * 
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  private boolean zzRefill() throws java.io.IOException {

    /* first: make room (if you can) */
    if (zzStartRead > 0) {
      System.arraycopy(zzBuffer, zzStartRead,
                       zzBuffer, 0,
                       zzEndRead-zzStartRead);

      /* translate stored positions */
      zzEndRead-= zzStartRead;
      zzCurrentPos-= zzStartRead;
      zzMarkedPos-= zzStartRead;
      zzStartRead = 0;
    }

    /* is the buffer big enough? */
    if (zzCurrentPos >= zzBuffer.length) {
      /* if not: blow it up */
      char newBuffer[] = new char[zzCurrentPos*2];
      System.arraycopy(zzBuffer, 0, newBuffer, 0, zzBuffer.length);
      zzBuffer = newBuffer;
    }

    /* finally: fill the buffer with new input */
    int numRead = zzReader.read(zzBuffer, zzEndRead,
                                            zzBuffer.length-zzEndRead);

    if (numRead > 0) {
      zzEndRead+= numRead;
      return false;
    }
    // unlikely but not impossible: read 0 characters, but not at end of stream    
    if (numRead == 0) {
      int c = zzReader.read();
      if (c == -1) {
        return true;
      } else {
        zzBuffer[zzEndRead++] = (char) c;
        return false;
      }     
    }

	// numRead < 0
    return true;
  }

    
  /**
   * Closes the input stream.
   */
  public final void yyclose() throws java.io.IOException {
    zzAtEOF = true;            /* indicate end of file */
    zzEndRead = zzStartRead;  /* invalidate buffer    */

    if (zzReader != null)
      zzReader.close();
  }


  /**
   * Resets the scanner to read from a new input stream.
   * Does not close the old reader.
   *
   * All internal variables are reset, the old input stream 
   * <b>cannot</b> be reused (internal buffer is discarded and lost).
   * Lexical state is set to <tt>ZZ_INITIAL</tt>.
   *
   * @param reader   the new input stream 
   */
  public final void yyreset(java.io.Reader reader) {
    zzReader = reader;
    zzAtBOL  = true;
    zzAtEOF  = false;
    zzEOFDone = false;
    zzEndRead = zzStartRead = 0;
    zzCurrentPos = zzMarkedPos = 0;
    yyline = yychar = yycolumn = 0;
    zzLexicalState = YYINITIAL;
  }


  /**
   * Returns the current lexical state.
   */
  public final int yystate() {
    return zzLexicalState;
  }


  /**
   * Enters a new lexical state
   *
   * @param newState the new lexical state
   */
  public final void yybegin(int newState) {
    zzLexicalState = newState;
  }


  /**
   * Returns the text matched by the current regular expression.
   */
  public final String yytext() {
    return new String( zzBuffer, zzStartRead, zzMarkedPos-zzStartRead );
  }


  /**
   * Returns the character at position <tt>pos</tt> from the 
   * matched text. 
   * 
   * It is equivalent to yytext().charAt(pos), but faster
   *
   * @param pos the position of the character to fetch. 
   *            A value from 0 to yylength()-1.
   *
   * @return the character at position pos
   */
  public final char yycharat(int pos) {
    return zzBuffer[zzStartRead+pos];
  }


  /**
   * Returns the length of the matched text region.
   */
  public final int yylength() {
    return zzMarkedPos-zzStartRead;
  }


  /**
   * Reports an error that occured while scanning.
   *
   * In a wellformed scanner (no or only correct usage of 
   * yypushback(int) and a match-all fallback rule) this method 
   * will only be called with things that "Can't Possibly Happen".
   * If this method is called, something is seriously wrong
   * (e.g. a JFlex bug producing a faulty scanner etc.).
   *
   * Usual syntax/scanner level error handling should be done
   * in error fallback rules.
   *
   * @param   errorCode  the code of the errormessage to display
   */
  private void zzScanError(int errorCode) {
    String message;
    try {
      message = ZZ_ERROR_MSG[errorCode];
    }
    catch (ArrayIndexOutOfBoundsException e) {
      message = ZZ_ERROR_MSG[ZZ_UNKNOWN_ERROR];
    }

    throw new Error(message);
  } 


  /**
   * Pushes the specified amount of characters back into the input stream.
   *
   * They will be read again by then next call of the scanning method
   *
   * @param number  the number of characters to be read again.
   *                This number must not be greater than yylength()!
   */
  public void yypushback(int number)  {
    if ( number > yylength() )
      zzScanError(ZZ_PUSHBACK_2BIG);

    zzMarkedPos -= number;
  }


  /**
   * Contains user EOF-code, which will be executed exactly once,
   * when the end of file is reached
   */
  private void zzDoEOF() throws java.io.IOException {
    if (!zzEOFDone) {
      zzEOFDone = true;
      yyclose();
    }
  }


  /**
   * Resumes scanning until the next regular expression is matched,
   * the end of input is encountered or an I/O-Error occurs.
   *
   * @return      the next token
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  public java_cup.runtime.Symbol next_token() throws java.io.IOException,    ErreurLexicale
 {
    int zzInput;
    int zzAction;

    // cached fields:
    int zzCurrentPosL;
    int zzMarkedPosL;
    int zzEndReadL = zzEndRead;
    char [] zzBufferL = zzBuffer;
    char [] zzCMapL = ZZ_CMAP;

    int [] zzTransL = ZZ_TRANS;
    int [] zzRowMapL = ZZ_ROWMAP;
    int [] zzAttrL = ZZ_ATTRIBUTE;

    while (true) {
      zzMarkedPosL = zzMarkedPos;

      boolean zzR = false;
      for (zzCurrentPosL = zzStartRead; zzCurrentPosL < zzMarkedPosL;
                                                             zzCurrentPosL++) {
        switch (zzBufferL[zzCurrentPosL]) {
        case '\u000B':
        case '\u000C':
        case '\u0085':
        case '\u2028':
        case '\u2029':
          yyline++;
          zzR = false;
          break;
        case '\r':
          yyline++;
          zzR = true;
          break;
        case '\n':
          if (zzR)
            zzR = false;
          else {
            yyline++;
          }
          break;
        default:
          zzR = false;
        }
      }

      if (zzR) {
        // peek one character ahead if it is \n (if we have counted one line too much)
        boolean zzPeek;
        if (zzMarkedPosL < zzEndReadL)
          zzPeek = zzBufferL[zzMarkedPosL] == '\n';
        else if (zzAtEOF)
          zzPeek = false;
        else {
          boolean eof = zzRefill();
          zzEndReadL = zzEndRead;
          zzMarkedPosL = zzMarkedPos;
          zzBufferL = zzBuffer;
          if (eof) 
            zzPeek = false;
          else 
            zzPeek = zzBufferL[zzMarkedPosL] == '\n';
        }
        if (zzPeek) yyline--;
      }
      zzAction = -1;

      zzCurrentPosL = zzCurrentPos = zzStartRead = zzMarkedPosL;
  
      zzState = ZZ_LEXSTATE[zzLexicalState];


      zzForAction: {
        while (true) {
    
          if (zzCurrentPosL < zzEndReadL)
            zzInput = zzBufferL[zzCurrentPosL++];
          else if (zzAtEOF) {
            zzInput = YYEOF;
            break zzForAction;
          }
          else {
            // store back cached positions
            zzCurrentPos  = zzCurrentPosL;
            zzMarkedPos   = zzMarkedPosL;
            boolean eof = zzRefill();
            // get translated positions and possibly new buffer
            zzCurrentPosL  = zzCurrentPos;
            zzMarkedPosL   = zzMarkedPos;
            zzBufferL      = zzBuffer;
            zzEndReadL     = zzEndRead;
            if (eof) {
              zzInput = YYEOF;
              break zzForAction;
            }
            else {
              zzInput = zzBufferL[zzCurrentPosL++];
            }
          }
          int zzNext = zzTransL[ zzRowMapL[zzState] + zzCMapL[zzInput] ];
          if (zzNext == -1) break zzForAction;
          zzState = zzNext;

          int zzAttributes = zzAttrL[zzState];
          if ( (zzAttributes & 1) == 1 ) {
            zzAction = zzState;
            zzMarkedPosL = zzCurrentPosL;
            if ( (zzAttributes & 8) == 8 ) break zzForAction;
          }

        }
      }

      // store back cached position
      zzMarkedPos = zzMarkedPosL;

      switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
        case 48: 
          { return symbol(sym.NEW_LINE);
          }
        case 49: break;
        case 44: 
          { return symbol(sym.WRITE);
          }
        case 50: break;
        case 25: 
          { return symbol(sym.OF);
          }
        case 51: break;
        case 15: 
          { return symbol(sym.PAR_FERM);
          }
        case 52: break;
        case 26: 
          { return symbol(sym.TO);
          }
        case 53: break;
        case 28: 
          { return symbol(sym.SUP_EGAL);
          }
        case 54: break;
        case 20: 
          { return symbol(sym.DOUBLE_POINT);
          }
        case 55: break;
        case 3: 
          { return symbol(sym.IDF, yytext());
          }
        case 56: break;
        case 32: 
          { return symbol(sym.END);
          }
        case 57: break;
        case 43: 
          { return symbol(sym.BEGIN);
          }
        case 58: break;
        case 12: 
          { return symbol(sym.DIV_REEL);
          }
        case 59: break;
        case 18: 
          { return symbol(sym.CROCH_OUVR);
          }
        case 60: break;
        case 34: 
          { return symbol(sym.NOT);
          }
        case 61: break;
        case 33: 
          { return symbol(sym.AND);
          }
        case 62: break;
        case 6: 
          { return symbol(sym.POINT);
          }
        case 63: break;
        case 27: 
          { return symbol(sym.INF_EGAL);
          }
        case 64: break;
        case 35: 
          { return symbol(sym.DIV);
          }
        case 65: break;
        case 30: 
          { return symbol(sym.AFFECT);
          }
        case 66: break;
        case 5: 
          { return symbol(sym.MOINS);
          }
        case 67: break;
        case 29: 
          { return symbol(sym.DIFF);
          }
        case 68: break;
        case 14: 
          { return symbol(sym.PAR_OUVR);
          }
        case 69: break;
        case 4: 
          { return symbol(sym.PLUS);
          }
        case 70: break;
        case 37: 
          { return symbol(sym.MOD);
          }
        case 71: break;
        case 42: 
          { return symbol(sym.ARRAY);
          }
        case 72: break;
        case 31: 
          { try {
                                return symbol(sym.CONST_REEL,
                          new Double(yytext()));
                          } catch (NumberFormatException e) {
                               throw new ErreurLexicale();
                          }
          }
        case 73: break;
        case 46: 
          { return symbol(sym.DOWNTO);
          }
        case 74: break;
        case 22: 
          { return symbol(sym.DO);
          }
        case 75: break;
        case 17: 
          { return symbol(sym.VIRGULE);
          }
        case 76: break;
        case 38: 
          { return symbol(sym.ELSE);
          }
        case 77: break;
        case 2: 
          { try {
                                return symbol(sym.CONST_ENT,
                           new Integer(yytext()));
                           } catch (NumberFormatException e) {
                                e.printStackTrace();
                                throw new ErreurLexicale();
                           }
          }
        case 78: break;
        case 41: 
          { return symbol(sym.THEN);
          }
        case 79: break;
        case 13: 
          { return symbol(sym.MULT);
          }
        case 80: break;
        case 45: 
          { return symbol(sym.WHILE);
          }
        case 81: break;
        case 23: 
          { return symbol(sym.IF);
          }
        case 82: break;
        case 21: 
          { String valueBase = yytext();
                            String value = "";
                            for(int i = 0; i < valueBase.length(); i++){
                                if(valueBase.charAt(i) == '"')
                                    continue;
                                value += valueBase.charAt(i);
                            }
                            return symbol(sym.CONST_CHAINE,value);
          }
        case 83: break;
        case 8: 
          { return symbol(sym.POINT_VIRGULE);
          }
        case 84: break;
        case 24: 
          { return symbol(sym.OR);
          }
        case 85: break;
        case 11: 
          { return symbol(sym.EGAL);
          }
        case 86: break;
        case 36: 
          { return symbol(sym.FOR);
          }
        case 87: break;
        case 10: 
          { return symbol(sym.SUP);
          }
        case 88: break;
        case 9: 
          { return symbol(sym.INF);
          }
        case 89: break;
        case 40: 
          { return symbol(sym.READ);
          }
        case 90: break;
        case 19: 
          { return symbol(sym.CROCH_FERM);
          }
        case 91: break;
        case 16: 
          { return symbol(sym.DEUX_POINTS);
          }
        case 92: break;
        case 1: 
          { System.out.println("Erreur Lexicale : '" +
                            yytext() + "' non reconnu ... ligne " + 
                            numLigne()) ;
                         throw new ErreurLexicale() ;
          }
        case 93: break;
        case 39: 
          { return symbol(sym.NULL);
          }
        case 94: break;
        case 47: 
          { return symbol(sym.PROGRAM);
          }
        case 95: break;
        case 7: 
          { 
          }
        case 96: break;
        default: 
          if (zzInput == YYEOF && zzStartRead == zzCurrentPos) {
            zzAtEOF = true;
            zzDoEOF();
              { return new java_cup.runtime.Symbol(sym.EOF); }
          } 
          else {
            zzScanError(ZZ_NO_MATCH);
          }
      }
    }
  }


}
