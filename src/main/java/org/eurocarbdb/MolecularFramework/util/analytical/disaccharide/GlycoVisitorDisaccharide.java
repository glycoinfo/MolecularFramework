/**
 * 
 */
package org.eurocarbdb.MolecularFramework.util.analytical.disaccharide;

import java.util.ArrayList;
import java.util.Iterator;

import org.eurocarbdb.MolecularFramework.sugar.GlycoEdge;
import org.eurocarbdb.MolecularFramework.sugar.GlycoNode;
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
import org.eurocarbdb.MolecularFramework.util.traverser.GlycoTraverserSimple;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitor;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorException;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorNodeType;

/**
 * Calculates all di-saccharides. Connection into uncertain or statistical terminal residues are not calculated, to avoid duplication.
 * Internal repeat linkage is also not calculated
 * @author Logan
 *
 */
public class GlycoVisitorDisaccharide implements GlycoVisitor 
{
	private ArrayList<Disaccharide> m_aDisaccharide = new ArrayList<Disaccharide>();
	private GlycoVisitorNodeType m_visNodeType = new GlycoVisitorNodeType();
	private boolean m_bCalculateStatistic = false;
	
	public void setCalculateStatistic(boolean a_bValue)
	{
		this.m_bCalculateStatistic = a_bValue;
	}
	
	public ArrayList<Disaccharide> getDisaccharide()
	{
		return this.m_aDisaccharide;
	}
	
	/**
	 * @see org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitor#clear()
	 */
	public void clear() 
	{
		this.m_aDisaccharide.clear();
	}

	/**
	 * @see org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitor#getTraverser(org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitor)
	 */
	public GlycoTraverser getTraverser(GlycoVisitor a_objVisitor) throws GlycoVisitorException 
	{
		return new GlycoTraverserSimple(a_objVisitor);
	}

	/**
	 * @see org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitor#visit(org.eurocarbdb.MolecularFramework.sugar.NonMonosaccharide)
	 */
	public void visit(NonMonosaccharide residue) throws GlycoVisitorException 
	{
		// do nothing
	}

	/**
	 * @see org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitor#visit(org.eurocarbdb.MolecularFramework.sugar.Substituent)
	 */
	public void visit(Substituent substituent) throws GlycoVisitorException 
	{
		// do nothing
	}

	/**
	 * @see org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitor#visit(org.eurocarbdb.MolecularFramework.sugar.UnvalidatedGlycoNode)
	 */
	public void visit(UnvalidatedGlycoNode unvalidated) throws GlycoVisitorException 
	{
		// do nothing
	}

	/**
	 * @see org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitor#visit(org.eurocarbdb.MolecularFramework.sugar.GlycoEdge)
	 */
	public void visit(GlycoEdge linkage) throws GlycoVisitorException 
	{
		// do nothing
	}

	/**
	 * @see org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitor#visit(org.eurocarbdb.MolecularFramework.sugar.SugarUnitCyclic)
	 */
	public void visit(SugarUnitCyclic a_objCyclic) throws GlycoVisitorException 
	{
		Monosaccharide t_objParent = this.getMonosaccharide(a_objCyclic.getParentNode(),false);
		if ( t_objParent != null )
		{
			// parent is monosaccharide
			Monosaccharide t_objChild = this.getMonosaccharide(a_objCyclic.getCyclicStart(),true);
			if ( t_objChild != null )
			{
				// child also monosaccharide
				Disaccharide t_objDisaccaride = new Disaccharide();
				t_objDisaccaride.setParent( t_objParent );
				t_objDisaccaride.setChild( t_objChild );
				t_objDisaccaride.setLinkage(a_objCyclic.getParentEdge());
				this.m_aDisaccharide.add(t_objDisaccaride);
			}
		}
	}

	/**
	 * @see org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitor#visit(org.eurocarbdb.MolecularFramework.sugar.SugarUnitAlternative)
	 */
	public void visit(SugarUnitAlternative alternative) throws GlycoVisitorException 
	{
		throw new GlycoVisitorException("SugarUnitAlternative are not supported.");
	}

	/**
	 * @see org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitor#start(org.eurocarbdb.MolecularFramework.sugar.Sugar)
	 */
	public void start(Sugar a_objSugar) throws GlycoVisitorException 
	{
		this.clear();
		GlycoTraverser t_objTraverser = this.getTraverser(this);
		t_objTraverser.traverseGraph(a_objSugar);
		// underdeterminded
		for (Iterator<UnderdeterminedSubTree> t_iterUnder = a_objSugar.getUndeterminedSubTrees().iterator(); t_iterUnder.hasNext();) 
		{
			UnderdeterminedSubTree t_objTree = t_iterUnder.next();
			// special linkage
			if ( t_objTree.getProbabilityLower() < 100 && this.m_bCalculateStatistic )
			{
				// statisic
				t_objTree = t_iterUnder.next();
				t_objTraverser = this.getTraverser(this);
				t_objTraverser.traverseGraph(t_objTree);					
			}
			else if (t_objTree.getProbabilityLower() == 100 )
			{				
				t_objTraverser = this.getTraverser(this);
				t_objTraverser.traverseGraph(t_objTree);
			}			
		}
	}

	/**
	 * @see org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitor#visit(org.eurocarbdb.MolecularFramework.sugar.Monosaccharide)
	 */
	public void visit(Monosaccharide a_objMonosaccharid) throws GlycoVisitorException 
	{
		GlycoNode t_objNode = a_objMonosaccharid.getParentNode();
		if ( t_objNode != null )
		{
			Monosaccharide t_objParent = this.getMonosaccharide(t_objNode,false);
			if ( t_objParent != null )
			{
				// parent is monosaccharide
				Disaccharide t_objDisaccaride = new Disaccharide();
				t_objDisaccaride.setParent( t_objParent );
				t_objDisaccaride.setChild( a_objMonosaccharid );
				t_objDisaccaride.setLinkage(a_objMonosaccharid.getParentEdge());
				this.m_aDisaccharide.add(t_objDisaccaride);
			}
		}
	}

	/**
	 * @see org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitor#visit(org.eurocarbdb.MolecularFramework.sugar.SugarUnitRepeat)
	 */
	public void visit(SugarUnitRepeat a_objRepeat) throws GlycoVisitorException 
	{
		GlycoTraverser t_objTraverser = this.getTraverser(this);
		t_objTraverser.traverseGraph(a_objRepeat);
		// in and out
		GlycoNode t_objNode = a_objRepeat.getParentNode();
		if ( t_objNode != null )
		{
			Monosaccharide t_objParent = this.getMonosaccharide(t_objNode,false);
			if ( t_objParent != null )
			{
				// parent is monosaccharide
				Monosaccharide t_objChild = this.getMonosaccharide(a_objRepeat.getRepeatLinkage().getChild(),true);
				if ( t_objChild != null )
				{
					// is a monosaccharide not a substituent
					Disaccharide t_objDisaccaride = new Disaccharide();
					t_objDisaccaride.setParent( t_objParent );
					t_objDisaccaride.setChild( t_objChild );
					t_objDisaccaride.setLinkage(a_objRepeat.getParentEdge());
					this.m_aDisaccharide.add(t_objDisaccaride);
				}
			}
		}
		// underdeterminded
		for (Iterator<UnderdeterminedSubTree> t_iterUnder = a_objRepeat.getUndeterminedSubTrees().iterator(); t_iterUnder.hasNext();) 
		{
			UnderdeterminedSubTree t_objTree = t_iterUnder.next();
			// special linkage
			if ( t_objTree.getProbabilityLower() < 100 && this.m_bCalculateStatistic )
			{
				// statisic
				t_objTraverser = this.getTraverser(this);
				t_objTraverser.traverseGraph(t_objTree);					
			}
			else if (t_objTree.getProbabilityLower() == 100 )
			{				
				t_objTraverser = this.getTraverser(this);
				t_objTraverser.traverseGraph(t_objTree);
			}			
		}
	}

	/**
	 * @param child
	 * @param b
	 * @return
	 * @throws GlycoVisitorException 
	 */
	private Monosaccharide getMonosaccharide(GlycoNode a_objResidue, boolean a_bRepeatIn) throws GlycoVisitorException 
	{
		int t_iNodeType = this.m_visNodeType.getNodeType(a_objResidue);
		if ( t_iNodeType == GlycoVisitorNodeType.MONOSACCHARIDE )
		{
			return this.m_visNodeType.getMonosaccharide(a_objResidue);
		}
		else if ( t_iNodeType == GlycoVisitorNodeType.REPEAT )
		{
			if ( a_bRepeatIn )
			{
				return this.getMonosaccharide(
						this.m_visNodeType.getSugarUnitRepeat(a_objResidue).getRepeatLinkage().getChild(),
						a_bRepeatIn);
			}
			else
			{
				return this.getMonosaccharide(
						this.m_visNodeType.getSugarUnitRepeat(a_objResidue).getRepeatLinkage().getParent(),
						a_bRepeatIn);
			}
		}
		return null;
	}
}
