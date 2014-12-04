/**
 * 
 */
package org.eurocarbdb.MolecularFramework.sugar;

import java.util.ArrayList;
import java.util.Iterator;

import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitor;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorException;
import org.eurocarbdb.MolecularFramework.util.visitor.Visitable;

/**
 * @author rene
 *
 */
public class GlycoEdge implements Visitable
{
    private GlycoNode m_objParent = null;
    private GlycoNode m_objChild = null;
    private ArrayList<Linkage> m_aLinkages = new ArrayList<Linkage>(); 
    
    public void setParent(GlycoNode a_objParent)
    {
        this.m_objParent = a_objParent;
    }
    
    public void setChild(GlycoNode a_objChild)
    {
        this.m_objChild = a_objChild;
    }
    
    public GlycoNode getChild()
    {
        return this.m_objChild;
    }
    
    public GlycoNode getParent()
    {
        return this.m_objParent;
    }

    public void setGlycosidicLinkages(ArrayList<Linkage> a_aLinkages) throws GlycoconjugateException
    {
        if ( a_aLinkages == null )
        {
            throw new GlycoconjugateException("null is not a valide set of linkages.");
        }
        this.m_aLinkages.clear();
        for (Iterator<Linkage> t_iterLinkage = a_aLinkages.iterator(); t_iterLinkage.hasNext();)
        {
            this.addGlycosidicLinkage(t_iterLinkage.next());            
        }
        this.m_aLinkages = a_aLinkages;
    }
    
    public ArrayList<Linkage> getGlycosidicLinkages()
    {
        return this.m_aLinkages;
    }
    
    public boolean addGlycosidicLinkage(Linkage a_objLinkage) throws GlycoconjugateException
    {
        if ( a_objLinkage == null )
        {
            throw new GlycoconjugateException("null linkage is not allowed");
        }
        if ( !this.m_aLinkages.contains(a_objLinkage) )
        {
            return this.m_aLinkages.add(a_objLinkage);
        }
        return false;
    }
    
    public boolean removeGlycosidicLinkage(Linkage a_objLinkage)
    {
        if ( this.m_aLinkages.contains(a_objLinkage) )
        {
            return this.m_aLinkages.remove(a_objLinkage);
        }
        return false;
    }

	/**
	 * @see org.eurocarbdb.MolecularFramework.util.visitor.Visitable#accept(org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitor)
	 */
	public void accept(GlycoVisitor a_objVisitor) throws GlycoVisitorException 
	{
		a_objVisitor.visit(this);
	}

    /**
     * copy without parent and childs
     * @return
     * @throws GlycoconjugateException
     */
	public GlycoEdge copy() throws GlycoconjugateException 
	{
	    GlycoEdge t_objCopy = new GlycoEdge();
        for (Iterator<Linkage> t_iterEdges = this.m_aLinkages.iterator(); t_iterEdges.hasNext();)
        {
            t_objCopy.addGlycosidicLinkage(t_iterEdges.next().copy());            
        }
        
        return t_objCopy;
	}
}