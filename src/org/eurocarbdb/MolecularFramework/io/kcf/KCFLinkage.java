/**
 * 
 */
package org.eurocarbdb.MolecularFramework.io.kcf;

import org.eurocarbdb.MolecularFramework.sugar.GlycoEdge;
import org.eurocarbdb.MolecularFramework.sugar.GlycoconjugateException;
import org.eurocarbdb.MolecularFramework.sugar.Linkage;

/**
 * @author rene
 *
 */
public class KCFLinkage
{
    private int m_iPosOne;
    private int m_iPosTwo;
    private int m_iResOne;
    private int m_iResTwo;
    
    public KCFLinkage (int a_iPosOne, int a_iPosTwo ,int a_iResOne, int a_iResTwo)
    {
        this.m_iPosOne = a_iPosOne;
        this.m_iPosTwo = a_iPosTwo;
        this.m_iResOne = a_iResOne;
        this.m_iResTwo = a_iResTwo;
    }
    
    public int getResidueOne()
    {
        return this.m_iResOne;
    }
    
    public int getResidueTwo()
    {
        return this.m_iResTwo;
    }
    
    public int getPositionOne()
    {
        return this.m_iPosOne;
    }

    public int getPositionTwo()
    {
        return this.m_iPosTwo;
    }

	/**
	 * @param b
	 * @return
	 * @throws GlycoconjugateException 
	 */
	public GlycoEdge getEdge(boolean a_bCorrectDirection) throws GlycoconjugateException 
	{
		GlycoEdge t_objEdge = new GlycoEdge();
		
		Linkage t_objLinkage = new Linkage();
		if ( a_bCorrectDirection )
		{
			t_objLinkage.addParentLinkage(this.m_iPosOne);
			t_objLinkage.addChildLinkage(this.m_iPosTwo);
		}
		else
		{
			t_objLinkage.addParentLinkage(this.m_iPosTwo);
			t_objLinkage.addChildLinkage(this.m_iPosOne);
		}		
		t_objEdge.addGlycosidicLinkage(t_objLinkage);		
		return t_objEdge;
	}
}