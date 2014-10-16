package org.eurocarbdb.MolecularFramework.io.glycam;

import java.util.ArrayList;

import org.eurocarbdb.MolecularFramework.sugar.GlycoEdge;
import org.eurocarbdb.MolecularFramework.sugar.GlycoGraph;
import org.eurocarbdb.MolecularFramework.sugar.GlycoNode;
import org.eurocarbdb.MolecularFramework.sugar.GlycoconjugateException;
import org.eurocarbdb.MolecularFramework.util.traverser.GlycoTraverser;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitor;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorException;

public class GlycoTraverserIupacSequence extends GlycoTraverser
{
	public static Integer BRANCH_IN = 0;
	public static Integer BRANCH_OUT = 1;
	public static Integer BRANCH_NORMAL = 2;

	public GlycoTraverserIupacSequence(GlycoVisitor a_objVisitor) throws GlycoVisitorException 
	{
		super(a_objVisitor);
	}

	public void traverse(GlycoNode a_objNode) throws GlycoVisitorException
	{
		a_objNode.accept(this.m_objVisitor);
		ArrayList<GlycoEdge> t_objEdges = a_objNode.getChildEdges(); 
		// traverse child nodes
		int t_counter = 0;
		for (GlycoEdge t_glycoEdge : t_objEdges)
		{
			t_counter++;
			if ( t_counter < t_objEdges.size() )
			{
				this.m_iState = GlycoTraverserIupacSequence.BRANCH_IN;
				this.traverse(t_glycoEdge);	
				this.m_iState = GlycoTraverserIupacSequence.BRANCH_OUT;
				t_glycoEdge.accept(this.m_objVisitor);
			}
			else
			{
				this.m_iState = GlycoTraverserIupacSequence.BRANCH_NORMAL;
				this.traverse(t_glycoEdge);	
			}
		}
	}

	public void traverse(GlycoEdge a_objEdge) throws GlycoVisitorException
	{
		a_objEdge.accept(this.m_objVisitor);
		this.traverse( a_objEdge.getChild() );
	}

	public void traverseGraph(GlycoGraph a_objSugar) throws GlycoVisitorException
	{
		try 
		{
			ArrayList<GlycoNode> t_aRoots = a_objSugar.getRootNodes();
			if ( t_aRoots.size() != 1 )
			{
				throw new GlycoVisitorException("Unconnected sugars are not supported.");
			}
			this.traverse(t_aRoots.get(0));
		} 
		catch (GlycoconjugateException e) 
		{
			throw new GlycoVisitorException(e.getMessage(),e);
		}
	}
}
