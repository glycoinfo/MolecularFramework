package org.eurocarbdb.MolecularFramework.io.Glyde;



public class Reference 
{
	private String m_strReferenceType = "";
	private String m_strReference = "";
	
	public Reference(String string, String id) 
	{
		this.m_strReference = id;
		this.m_strReferenceType = string;
	}
	
	public String getReferenceType()
	{
		return this.m_strReferenceType;
	}

	public String getReference()
	{
		return this.m_strReference;
	}
}
