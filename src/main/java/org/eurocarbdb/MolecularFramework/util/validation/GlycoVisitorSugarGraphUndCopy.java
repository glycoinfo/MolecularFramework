/**
 * 
 */
package org.eurocarbdb.MolecularFramework.util.validation;

import java.util.HashMap;

import org.eurocarbdb.MolecularFramework.sugar.GlycoEdge;
import org.eurocarbdb.MolecularFramework.sugar.GlycoNode;
import org.eurocarbdb.MolecularFramework.sugar.GlycoconjugateException;
import org.eurocarbdb.MolecularFramework.sugar.Monosaccharide;
import org.eurocarbdb.MolecularFramework.sugar.NonMonosaccharide;
import org.eurocarbdb.MolecularFramework.sugar.Substituent;
import org.eurocarbdb.MolecularFramework.sugar.Sugar;
import org.eurocarbdb.MolecularFramework.sugar.SugarUnitAlternative;
import org.eurocarbdb.MolecularFramework.sugar.SugarUnitCyclic;
import org.eurocarbdb.MolecularFramework.sugar.SugarUnitRepeat;
import org.eurocarbdb.MolecularFramework.sugar.UnderdeterminedSubTree;
import org.eurocarbdb.MolecularFramework.sugar.UnvalidatedGlycoNode;
import org.eurocarbdb.MolecularFramework.util.traverser.GlycoTraverser;
import org.eurocarbdb.MolecularFramework.util.traverser.GlycoTraverserTreeSingle;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitor;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorException;

/**
 * @author Logan
 *
 */
public class GlycoVisitorSugarGraphUndCopy implements GlycoVisitor 
{
	private HashMap<GlycoNode, GlycoNode> m_hResidue = new HashMap<GlycoNode, GlycoNode>();
	private UnderdeterminedSubTree m_objTree = null;
	private SugarGraphInformation m_objInfo = null;
	
	public void clear() 
	{
		this.m_hResidue.clear();
		this.m_objTree = null;
	}

	public GlycoTraverser getTraverser(GlycoVisitor a_objVisitor) throws GlycoVisitorException 
	{
		return new GlycoTraverserTreeSingle(a_objVisitor);
	}

	public void start(Sugar sugar) throws GlycoVisitorException 
	{
		throw new GlycoVisitorException("GlycoVisitorSugarGraphUndCopy does not work for sugar."); 
	}

	public void visit(Monosaccharide a_objMonosaccharide) throws GlycoVisitorException 
	{
		Monosaccharide t_objMs;
		try 
		{
			t_objMs = a_objMonosaccharide.copy();
			this.m_objTree.addNode(t_objMs);
			this.m_hResidue.put(a_objMonosaccharide, t_objMs);
			GlycoEdge t_objEdge = a_objMonosaccharide.getParentEdge();
			if ( t_objEdge != null )
			{
				GlycoEdge t_objEdgeNew = t_objEdge.copy();
				GlycoNode t_objParent = this.m_hResidue.get(t_objEdge.getParent());
				if ( t_objParent == null )
				{
					throw new GlycoVisitorException("Error with parent of monosaccharide by copying repeat unit.");
				}
				this.m_objTree.addEdge(t_objParent, t_objMs, t_objEdgeNew);
			}
		} 
		catch (GlycoconjugateException e) 
		{
			throw new GlycoVisitorException(e.getMessage(),e);
		}		
	}

	public void visit(NonMonosaccharide a_objResidue) throws GlycoVisitorException 
	{
		GlycoNode t_objNode = a_objResidue.getParentNode();
		if ( t_objNode == null )
		{
			throw new GlycoVisitorException("Error with unconnected non monosaccharide in UND.");
		}
		t_objNode = this.m_hResidue.get(t_objNode);
		if ( t_objNode != null )
		{
			this.m_objInfo.addTerminalInformation( new SugarGraphAglycon(a_objResidue,t_objNode,a_objResidue.getParentEdge()));
		}
	}

	public void visit(SugarUnitRepeat a_objRepeat) throws GlycoVisitorException 
	{
		throw new GlycoVisitorException("GlycoVisitorSugarGraphUndCopy does not work for SugarUnitRepeat.");
	}

	public void visit(Substituent a_objSubst) throws GlycoVisitorException 
	{
		Substituent t_objSubst;
		try 
		{
			t_objSubst = a_objSubst.copy();
			this.m_objTree.addNode(t_objSubst);
			this.m_hResidue.put(a_objSubst, t_objSubst);
			GlycoEdge t_objEdge = a_objSubst.getParentEdge();
			if ( t_objEdge != null )
			{
				GlycoEdge t_objEdgeNew = t_objEdge.copy();
				GlycoNode t_objParent = this.m_hResidue.get(t_objEdge.getParent());
				if ( t_objParent != null )
				{
					this.m_objTree.addEdge(t_objParent, t_objSubst, t_objEdgeNew);
				}
			}
		} 
		catch (GlycoconjugateException e) 
		{
			throw new GlycoVisitorException(e.getMessage(),e);
		}		
	}

	public void visit(SugarUnitCyclic cyclic) throws GlycoVisitorException 
	{
		throw new GlycoVisitorException("GlycoVisitorSugarGraphUndCopy does not work for SugarUnitCyclic.");		
	}

	public void visit(SugarUnitAlternative alternative) throws GlycoVisitorException 
	{
		throw new GlycoVisitorException("GlycoVisitorSugarGraphUndCopy does not work for SugarUnitAlternative.");		
	}

	public void visit(UnvalidatedGlycoNode a_objUn) throws GlycoVisitorException 
	{
		throw new GlycoVisitorException("GlycoVisitorSugarGraphUndCopy does not work for UnvalidatedGlycoNode.");
	}

	public void visit(GlycoEdge linkage) throws GlycoVisitorException 
	{
		// nothing to do		
	}

	public void setSugarGraphInfo(SugarGraphInformation currentSugarGraph) 
	{
		this.m_objInfo = currentSugarGraph;		
	}

	/**
	 * @param tree
	 * @return
	 * @throws GlycoVisitorException 
	 */
	public UnderdeterminedSubTree start(UnderdeterminedSubTree a_objTree) throws GlycoVisitorException 
	{
		this.clear();
		try
		{
			this.m_objTree = new UnderdeterminedSubTree();
			this.getTraverser(this).traverseGraph(a_objTree);
			this.m_objTree.setProbability(a_objTree.getProbabilityLower(), a_objTree.getProbabilityUpper());
			this.m_objTree.setConnection( a_objTree.getConnection().copy());
			return this.m_objTree;
		}
		catch (GlycoconjugateException e) 
		{
			throw new GlycoVisitorException(e.getMessage(),e);
		}
	}
}
