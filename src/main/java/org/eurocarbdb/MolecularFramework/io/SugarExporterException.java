package org.eurocarbdb.MolecularFramework.io;



public class SugarExporterException extends Exception 
{
	/**
	 * @param string
	 */
	public SugarExporterException(String a_strErrorCode) 
	{
		super(a_strErrorCode);
	}

    public SugarExporterException(String a_strErrorCode, Throwable a_objThrow)
    {
        super(a_strErrorCode,a_objThrow);
    }

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
