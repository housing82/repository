package com.universal.code.exception;


/**
 *
 * <p>
 * XMLParsingException
 * </p>
 *
 *

 *
 */
public class XMLParsingException  extends NestedException {

    /**
	 * 
	 */
	private static final long serialVersionUID = -302073675194879684L;

	/**
     * @param msg
     */
    public XMLParsingException(String msg) {
        super(msg);
    }

    public XMLParsingException(Throwable e) {
        super(e);
    }
    
    /**
     * @param string
     * @param e
     */
    public XMLParsingException(String msg, Throwable e) {
        super(msg, e);
    }

}
