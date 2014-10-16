package org.eurocarbdb.MolecularFramework.sugar;

/**
 * @author Logan
 *
 */
public class Modification 
{
    private Integer m_iPositionOne = Modification.UNKNOWN_POSITION;
    private Integer m_iPositionTwo = null;
    public static final int UNKNOWN_POSITION   = 0;

    private ModificationType m_enumModification;
    
    public Modification( ModificationType symbol , int a_iPosition) throws GlycoconjugateException
    {
    	this.m_enumModification = symbol;
    	this.setPositionOne(a_iPosition);
    }

    public Modification( ModificationType symbol , int a_iPositionOne , Integer a_iPositionTwo) throws GlycoconjugateException
    {
    	this.m_enumModification = symbol;
    	this.setPositionOne(a_iPositionOne);
    	this.setPositionTwo(a_iPositionTwo);
    }

    public Modification( String symbol , int a_iPosition) throws GlycoconjugateException
    {
    	this.m_enumModification = ModificationType.forName(symbol);
    	this.setPositionOne(a_iPosition);
    }

    public Modification( String a_strModi , int a_iPositionOne , Integer a_iPositionTwo) throws GlycoconjugateException
    {
    	this.m_enumModification = ModificationType.forName(a_strModi);
    	this.setPositionOne(a_iPositionOne);
    	this.setPositionTwo(a_iPositionTwo);
    }

    private void setPositionOne(Integer a_iPosition) throws GlycoconjugateException
    {
    	if ( a_iPosition == null )
        {
            throw new GlycoconjugateException("Invalid value for attach position");
        }
    	if ( a_iPosition < Modification.UNKNOWN_POSITION )
        {
            throw new GlycoconjugateException("Invalid value for attach position");
        }
        this.m_iPositionOne = a_iPosition;
    }
    
    public int getPositionOne()
    {
        return this.m_iPositionOne;
    }

    private void setPositionTwo(Integer a_iPosition) throws GlycoconjugateException
    {
    	if ( a_iPosition == null )
    	{
    		this.m_iPositionTwo = null;
    	}
    	else
    	{
	        if ( a_iPosition < Modification.UNKNOWN_POSITION )
	        {
	            throw new GlycoconjugateException("Invalid value for attach position");
	        }
	        this.m_iPositionTwo = a_iPosition;
    	}
    }

    public Integer getPositionTwo()
    {
        return this.m_iPositionTwo;
    }
    
    public String getName()
    {
    	return this.m_enumModification.getName();
    }
    
    public ModificationType getModificationType()
    {
    	return this.m_enumModification;
    }

    public boolean hasPositionTwo(){
    	
    	if (this.m_iPositionTwo==null){
    		return false;
    	}
    	else return true;
    }
    
    public boolean hasPositionOne(){
    	
    	if (this.m_iPositionOne==null){
    		return false;
    	}
    	else return true;
    }

	/**
	 * @return
	 * @throws GlycoconjugateException 
	 */
	public Modification copy() throws GlycoconjugateException 
	{
		Modification t_objCopy = new Modification( this.m_enumModification.getName() , this.m_iPositionOne , this.m_iPositionTwo);
		return t_objCopy;
	}
}
