package com.universal.code.exception;

public class NestedException extends Exception {

    /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 8648558384788230106L;

	private static final String CAUSED_BY = "\nCaused by: ";

    private Throwable cause = null;

    public NestedException() {
        super();
    }
    
    /**
     * Constructor
     *
     * @param message
     *            error message
     */
    public NestedException(String message) {
        super(message);
    }

    /**
     * Constructor
     *
     * @param cause
     *            the nested exception (caused by)
     */
    public NestedException(Throwable cause) {
        super();
        this.cause = cause;
    }

    /**
     * Constructor
     *
     * @param message
     *            error message
     * @param cause
     *            the nested exception (caused by)
     */
    public NestedException(String message, Throwable cause) {
        super(message);
        this.cause = cause;
    }

    /**
     * Gets the causing exception, if any.
     *
     * @return The cause of the exception
     */
    @Override
	public Throwable getCause() {
        return cause;
    }
	/**
	 * Sets the value of the cause property.
	 *
	 * @param cause the new value of the cause property
	 */
	public void setCause(Throwable cause) {
		this.cause = cause;
	}

    /**
     * Converts the exception to a string representation
     *
     * @return The string representation of the exception
     */
    @Override
	public String toString() {
        if (cause == null) {
            return super.toString();
        } else {
            return super.toString() + CAUSED_BY + cause.toString();
        }
    }

    /**
     * Sends a stack trace to System.err (including the root cause, if any)
     */
    @Override
	public void printStackTrace() {

        if (cause != null) {
            System.err.println(CAUSED_BY);
            cause.printStackTrace();
        } else {
            super.printStackTrace();
        }
    }

    /**
     * Sends a stack trace to the PrintStream passed in (including the root
     * cause, if any)
     *
     * @param ps -
     *            the PrintStream to send the output to
     */
    @Override
	public void printStackTrace(java.io.PrintStream ps) {
        // super.printStackTrace(ps);
        if (cause != null) {
            ps.println(CAUSED_BY);
            cause.printStackTrace(ps);
        } else {
            super.printStackTrace(ps);
        }
    }

    /**
     * Sends a stack trace to the PrintWriter passed in (including the root
     * cause, if any)
     *
     * @param pw -
     *            the PrintWriter to send the output to
     */
    @Override
	public void printStackTrace(java.io.PrintWriter pw) {
        // super.printStackTrace(pw);
        if (cause != null) {
            pw.println(CAUSED_BY);
            cause.printStackTrace(pw);
        } else {
            super.printStackTrace(pw);
        }
    }
}
