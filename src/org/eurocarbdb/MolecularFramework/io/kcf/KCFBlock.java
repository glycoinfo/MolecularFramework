/**
 * 
 */
package org.eurocarbdb.MolecularFramework.io.kcf;

import org.eurocarbdb.MolecularFramework.sugar.GlycoGraph;
import org.eurocarbdb.MolecularFramework.sugar.GlycoNode;
import org.eurocarbdb.MolecularFramework.sugar.Linkage;
import org.eurocarbdb.MolecularFramework.sugar.SugarUnitRepeat;

/**
 * @author rene
 *
 */
public class KCFBlock
{
    private double m_dLeftUp	= 0;
    private double m_dLeftDown	= 0;
    private Double m_dLeft	= null;
    private Double m_dRight	= null;
    private double m_dRightUp	= 0;
    private double m_dRightDown	= 0;
    private int m_iRepeatMin = 0;
    private int m_iRepeatMax = 0;
    private GlycoGraph m_objGraph = null;
    private Linkage m_objEdge = null; 
    private GlycoNode m_objResidue = null;
    private SugarUnitRepeat m_objRepeat = null;
    
    public KCFBlock ()
    {}
    
    
    
    public double getRightUp()
    {
    	return this.m_dRightUp;
    }
    
    public double getRightDown()
    {
    	return this.m_dRightDown;
    }
    
    public double getLeftUp()
    {
    	return this.m_dLeftUp;
    }
    
    public double getLeftDown()
    {
    	return this.m_dLeftDown;
    }
    
    public double getLeft()
    {
    	return this.m_dLeft;
    }
    
    public double getRight()
    {
    	return this.m_dRight;
    }
    
    public void setMin(int a_iValue)
    {
    	this.m_iRepeatMin = a_iValue;
    }
    
    public void setMax(int a_iValue)
    {
    	this.m_iRepeatMax = a_iValue;
    }
    
    public int getMin()
    {
    	return this.m_iRepeatMin;
    }
    
    public int getMax()
    {
    	return this.m_iRepeatMax;
    }



	/**
	 * @param coo1
	 * @param coo2
	 * @param coo4
	 */
	public void setBracket(double coo1, double coo2, double coo4) 
	{
		if ( this.m_dLeft == null )
		{
			this.m_dLeft = coo1;
			if ( coo2 > coo4 )
			{
				this.m_dLeftUp = coo2;
				this.m_dLeftDown = coo4;
			}
			else
			{
				this.m_dLeftUp = coo4;
				this.m_dLeftDown = coo2;
			}
		}
		else
		{
			if ( this.m_dLeft < coo1 )
			{
				this.m_dRight = coo1;
				if ( coo2 > coo4 )
				{
					this.m_dRightUp = coo2;
					this.m_dRightDown = coo4;
				}
				else
				{
					this.m_dRightUp = coo4;
					this.m_dRightDown = coo2;
				}
			}
			else
			{
				this.m_dRight = this.m_dLeft;
				this.m_dRightDown = this.m_dLeftDown;
				this.m_dRightUp = this.m_dLeftUp;
				this.m_dLeft = coo1;
				if ( coo2 > coo4 )
				{
					this.m_dLeftUp = coo2;
					this.m_dLeftDown = coo4;
				}
				else
				{
					this.m_dLeftUp = coo4;
					this.m_dLeftDown = coo2;
				}	
			}
		}
	}



	/**
	 * @param graph
	 */
	public void setParentGraph(GlycoGraph a_objGraph) 
	{
		this.m_objGraph = a_objGraph;
	}
	
	public GlycoGraph getParentGraph()
	{
		return this.m_objGraph;
	}



	/**
	 * @param edge
	 * @param residue
	 */
	public void setRepeatLinkage(Linkage a_objEdge, GlycoNode a_objResidue) 
	{
		this.m_objEdge = a_objEdge;
		this.m_objResidue = a_objResidue;
	}
	
	public Linkage getRepeatLinkage()
	{
		return this.m_objEdge;
	}
	
	public GlycoNode getRepeatChild()
	{
		return this.m_objResidue;
	}



	/**
	 * @param repeat
	 */
	public void setRepeatUnit(SugarUnitRepeat repeat) 
	{
		this.m_objRepeat = repeat;		
	}
	
	public SugarUnitRepeat getRepeatUnit()
	{
		return this.m_objRepeat;
	}
}
