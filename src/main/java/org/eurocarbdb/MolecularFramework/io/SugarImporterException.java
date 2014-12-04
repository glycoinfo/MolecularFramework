package org.eurocarbdb.MolecularFramework.io;



public class SugarImporterException extends Exception 
{
	private String m_strErrorCode = "";
	private int m_iPosition = -1;
    private int m_iLine = -1;
    private Exception m_objException = null;
	
	public SugarImporterException(String a_strErrorCode, int a_iPosition) 
	{
		super();
		this.m_iPosition = a_iPosition;
		this.m_strErrorCode = a_strErrorCode;
	}
	
	public SugarImporterException(String a_strErrorCode, String a_message, int a_iPosition) 
    {
        super(a_message);
        this.m_iPosition = a_iPosition;
        this.m_strErrorCode = a_strErrorCode;
    }
	
	/**
	 * @param string
	 */
	public SugarImporterException(String a_strErrorCode) 
	{
		super();
		this.m_strErrorCode = a_strErrorCode;
	}

	/**
     * @param string
     * @param x
     * @param y
     */
    public SugarImporterException(String a_strErrorCode, int a_iPosition, int a_iLine)
    {
        super();
        this.m_iPosition = a_iPosition;
        this.m_strErrorCode = a_strErrorCode;
        this.m_iLine = a_iLine;
    }

    /**
	 * @param message
	 * @param e
	 */
	public SugarImporterException(String message, Throwable e) 
	{
		super(message,e);
		this.m_strErrorCode = message;
	}

	public SugarImporterException(String a_errorCode, String a_message, Exception a_exception)
    {
	    super(a_message,a_exception);
	    this.m_strErrorCode = a_errorCode;
    }

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public int getPosition()
	{
		return this.m_iPosition;
	}
    
    public int getLine()
    {
        return this.m_iLine;
    }
	
	public String getErrorCode()
	{
		return this.m_strErrorCode;
	}

	/**
	 * 
	 * @return ErrorText or null
	 */
	public String getErrorText()
	{
	    if ( this.getMessage() == null )
	    {
	        return ErrorTextEng.getErrorText(this.m_strErrorCode);
	    }
	    return this.getMessage();
	}
	
	/**
	 * 
	 * @return Exception or null
	 */
	public Exception getException()
	{
		return this.m_objException;
	}
	
}
