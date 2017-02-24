/* Generated By:JavaCC: Do not edit this line. Token.java Version 7.0 */
/* JavaCCOptions:TOKEN_EXTENDS=,KEEP_LINE_COLUMN=true,SUPPORT_CLASS_VISIBILITY_PUBLIC=false */
package org.semanticweb.owlapi.krss1.parser;

@SuppressWarnings("all")
class Token implements java.io.Serializable {

  private static final long serialVersionUID = 1L;

  public int kind;

  public int beginLine;
  public int beginColumn;
  public int endLine;
  public int endColumn;

  public String image;

  public Token next;

  public Token specialToken;

  public Token() {
  }

  public Token(int kind) {
    this(kind, null);
  }

  public Token(int kind, String image) {
    this.kind = kind;
    this.image = image;
  }

  public static Token newToken(int ofKind, String image) {
    switch (ofKind) {
      default:
        return new Token(ofKind, image);
    }
  }

  public static Token newToken(int ofKind) {
    return newToken(ofKind, null);
  }

  public Object getValue() {
    return null;
  }

  public String toString() {
    return image;
  }

}
/* JavaCC - OriginalChecksum=a997ca6899c0adeb64bc4e1f0e3b9738 (do not edit this line) */
