/**
 * 
 */
package org.eurocarbdb.MolecularFramework.util.validation;

import java.util.HashMap;
import java.util.Iterator;

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
public class GlycoVisitorSugarGraphRepeatCopy implements GlycoVisitor 
{
	private HashMap<GlycoNode, GlycoNode> m_hResidue = new HashMap<GlycoNode, GlycoNode>();
	private SugarUnitRepeat m_objRepeat = null;
	private SugarGraphInformation m_objInfo = null;
	
	public void clear() 
	{
		this.m_hResidue.clear();
		this.m_objRepeat = null;
	}

	public GlycoTraverser getTraverser(GlycoVisitor a_objVisitor) throws GlycoVisitorException 
	{
		return new GlycoTraverserTreeSingle(a_objVisitor);
	}

	public void start(Sugar sugar) throws GlycoVisitorException 
	{
		throw new GlycoVisitorException("GlycoVisitorSugarGraphRepeatCopy does not work for sugar."); 
	}

	public void visit(Monosaccharide a_objMonosaccharide) throws GlycoVisitorException 
	{
		Monosaccharide t_objMs;
		try 
		{
			t_objMs = a_objMonosaccharide.copy();
			this.m_objRepeat.addNode(t_objMs);
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
				this.m_objRepeat.addEdge(t_objParent, t_objMs, t_objEdgeNew);
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
			throw new GlycoVisitorException("Error with unconnected or starting non monosaccharide.");
		}
		t_objNode = this.m_hResidue.get(t_objNode);
		if ( t_objNode != null )
		{
			this.m_objInfo.addTerminalInformation( new SugarGraphAglycon(a_objResidue,t_objNode,a_objResidue.getParentEdge()));
		}
	}

	public void visit(SugarUnitRepeat a_objRepeat) throws GlycoVisitorException 
	{
		try
		{
			GlycoVisitorSugarGraphRepeatCopy t_visCopy = new GlycoVisitorSugarGraphRepeatCopy();
			t_visCopy.setSugarGraphInfo(this.m_objInfo);
			SugarUnitRepeat t_objRepeat = t_visCopy.start(a_objRepeat);
			this.m_objRepeat.addNode(t_objRepeat);
			this.m_hResidue.put(a_objRepeat, t_objRepeat);
			GlycoEdge t_objEdge = a_objRepeat.getParentEdge();
			if ( t_objEdge != null )
			{
				GlycoEdge t_objEdgeNew = t_objEdge.copy();
				GlycoNode t_objParent = this.m_hResidue.get(t_objEdge.getParent());
				if ( t_objParent == null )
				{
					throw new GlycoVisitorException("Error with parent of repeat by copying repeat unit.");
				}
				this.m_objRepeat.addEdge(t_objParent, t_objRepeat, t_objEdgeNew);
			}
		} 
		catch (GlycoconjugateException e) 
		{
			throw new GlycoVisitorException(e.getMessage(),e);
		}
	}

	public void visit(Substituent a_objSubst) throws GlycoVisitorException 
	{
		Substituent t_objSubst;
		try 
		{
			t_objSubst = a_objSubst.copy();
			this.m_objRepeat.addNode(t_objSubst);
			this.m_hResidue.put(a_objSubst, t_objSubst);
			GlycoEdge t_objEdge = a_objSubst.getParentEdge();
			if ( t_objEdge != null )
			{
				GlycoEdge t_objEdgeNew = t_objEdge.copy();
				GlycoNode t_objParent = this.m_hResidue.get(t_objEdge.getParent());
				if ( t_objParent != null )
				{
					this.m_objRepeat.addEdge(t_objParent, t_objSubst, t_objEdgeNew);
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
		throw new GlycoVisitorException("GlycoVisitorSugarGraphRepeatCopy does not work for SugarUnitCyclic.");		
	}

	public void visit(SugarUnitAlternative alternative) throws GlycoVisitorException 
	{
		throw new GlycoVisitorException("GlycoVisitorSugarGraphRepeatCopy does not work for SugarUnitAlternative.");		
	}

	public void visit(UnvalidatedGlycoNode a_objUn) throws GlycoVisitorException 
	{
		throw new GlycoVisitorException("GlycoVisitorSugarGraphRepeatCopy does not work for UnvalidatedGlycoNode.");
	}

	public void visit(GlycoEdge linkage) throws GlycoVisitorException 
	{
		// nothing to do		
	}

	public void setSugarGraphInfo(SugarGraphInformation currentSugarGraph) 
	{
		this.m_objInfo = currentSugarGraph;		
	}

	public SugarUnitRepeat start(SugarUnitRepeat a_objRepeat) throws GlycoVisitorException 
	{
		this.clear();
		try
		{
			this.m_objRepeat = new SugarUnitRepeat();
			this.getTraverser(this).traverseGraph(a_objRepeat);
			this.m_objRepeat.setMinRepeatCount(a_objRepeat.getMinRepeatCount());
			this.m_objRepeat.setMaxRepeatCount(a_objRepeat.getMaxRepeatCount());
			GlycoEdge t_objEdge = a_objRepeat.getRepeatLinkage();
			GlycoEdge t_objEdgeNew = t_objEdge.copy();
			GlycoNode t_objParent = this.m_hResidue.get(t_objEdge.getParent());
			GlycoNode t_objChild = this.m_hResidue.get(t_objEdge.getChild());
			if ( t_objParent == null || t_objChild == null )
			{
				throw new GlycoVisitorException("Error parent/child of repeat linkage is not part of repeat.");
			}
			this.m_objRepeat.setRepeatLinkage(t_objEdgeNew, t_objParent, t_objChild);	
			// UND
			GlycoVisitorSugarGraphUndCopy t_visCopyUND = new GlycoVisitorSugarGraphUndCopy();
			t_visCopyUND.setSugarGraphInfo(this.m_objInfo);			
			for (Iterator<UnderdeterminedSubTree> t_iterUND = a_objRepeat.getUndeterminedSubTrees().iterator(); t_iterUND.hasNext();) 
			{				
				UnderdeterminedSubTree t_objTree = t_iterUND.next();
				UnderdeterminedSubTree t_objTreeNew = t_visCopyUND.start(t_objTree);
				this.m_objRepeat.addUndeterminedSubTree(t_objTreeNew);
				GlycoNode t_objNodeNew = null;
				for (Iterator<GlycoNode> t_iterParent = t_objTree.getParents().iterator(); t_iterParent.hasNext();) 
				{
					GlycoNode t_objNode = t_iterParent.next();
					t_objNodeNew = this.m_hResidue.get(t_objNode);
					if ( t_objNodeNew == null )
					{
						throw new GlycoVisitorException("Parent of UnderdeterminedSubTree is not part of the repeat unit.");
					}
					this.m_objRepeat.addUndeterminedSubTreeParent(t_objTreeNew,t_objNodeNew);
				}
			}
			return this.m_objRepeat;
		}
		catch (GlycoconjugateException e) 
		{
			throw new GlycoVisitorException(e.getMessage(),e);
		}
	}
}
