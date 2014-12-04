/**
 * 
 */
package org.eurocarbdb.MolecularFramework.sugar;

import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitor;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorException;

/**
 * @author Logan
 *
 */
public class UnvalidatedGlycoNode extends GlycoNode 
{
	private String m_strName = "";
	
	public void setName(String a_strName) throws GlycoconjugateException
	{
        if ( a_strName == null )
        {
            throw new GlycoconjugateException("null is not a valide name.");
        }
		this.m_strName = a_strName;
	}
	
	public String getName()
	{
		return this.m_strName;
	}
	
	/**
	 * @see org.eurocarbdb.MolecularFramework.util.visitor.Visitable#accept(org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitor)
	 */
	public void accept(GlycoVisitor a_objVisitor) throws GlycoVisitorException 
	{
		a_objVisitor.visit(this);		
	}

    /**
     * @see org.eurocarbdb.MolecularFramework.sugar.GlycoNode#copy()
     */
    public UnvalidatedGlycoNode copy() throws GlycoconjugateException
    {
        UnvalidatedGlycoNode t_objCopy = new UnvalidatedGlycoNode();
        t_objCopy.setName( this.m_strName );
        return t_objCopy;
    }
}
