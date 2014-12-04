package org.eurocarbdb.MolecularFramework.util.traverser;

import org.eurocarbdb.MolecularFramework.sugar.GlycoEdge;
import org.eurocarbdb.MolecularFramework.sugar.GlycoGraph;
import org.eurocarbdb.MolecularFramework.sugar.GlycoNode;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitor;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorException;



public abstract class GlycoTraverser  
{
	public static final int ENTER 	= 0;
	public static final int LEAVE 	= 1;
	public static final int RETURN 	= 2;
	
	protected GlycoVisitor m_objVisitor = null;
	protected int m_iState = 0; 

	
	public GlycoTraverser ( GlycoVisitor a_objVisitor ) throws GlycoVisitorException
	{
        if ( a_objVisitor == null )
        {
            throw new GlycoVisitorException("Null visitor given to traverser");
        }
        this.m_objVisitor = a_objVisitor;
	}

    public abstract void traverse( GlycoNode a_objNode ) throws GlycoVisitorException;
	public abstract void traverse( GlycoEdge a_objEdge ) throws GlycoVisitorException;	

	public abstract void traverseGraph( GlycoGraph a_objSugar ) throws GlycoVisitorException;
    
	public int getState()
	{
		return this.m_iState;
	}
}
