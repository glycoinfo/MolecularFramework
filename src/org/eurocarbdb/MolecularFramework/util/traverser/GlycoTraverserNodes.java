package org.eurocarbdb.MolecularFramework.util.traverser;

import java.util.Iterator;

import org.eurocarbdb.MolecularFramework.sugar.GlycoEdge;
import org.eurocarbdb.MolecularFramework.sugar.GlycoGraph;
import org.eurocarbdb.MolecularFramework.sugar.GlycoNode;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitor;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorException;

/**
 * Traverser which touches each residue of the tree only one time. 
 * Traverse no linkages.
 * 
 * @author rene
 *
 */
public class GlycoTraverserNodes extends GlycoTraverser 
{
    /**
     * @param a_objVisitor
     * @throws GlycoVisitorException 
     */
    public GlycoTraverserNodes(GlycoVisitor a_objVisitor) throws GlycoVisitorException
    {
        super(a_objVisitor);
    }

    @Override
	public void traverse(GlycoNode a_objResidue) throws GlycoVisitorException
	{
        a_objResidue.accept( this.m_objVisitor );
	}

	@Override
	public void traverse(GlycoEdge a_objLinkage) throws GlycoVisitorException 
	{
		// do nothing
	}

	/**
	 * @see de.glycosciences.MolecularFrameWork.util.SugarTraverser#traverse(de.glycosciences.MolecularFrameWork.sugar.Sugar)
	 */
	@Override
	public void traverseGraph(GlycoGraph a_objSugar) throws GlycoVisitorException 
	{
        Iterator<GlycoNode> t_objIterator = a_objSugar.getNodeIterator();
        while ( t_objIterator.hasNext() )
        {
            this.traverse(t_objIterator.next());
        }
	}

	
}
