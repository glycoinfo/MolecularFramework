package org.eurocarbdb.MolecularFramework.io.glycam;

import org.eurocarbdb.MolecularFramework.sugar.GlycoEdge;
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

public class GlycoVisitorNamespaceCarbbank implements GlycoVisitor 
{
	public void clear() 
	{
	}

	public GlycoTraverser getTraverser(GlycoVisitor a_objVisitor) throws GlycoVisitorException 
	{
		return new GlycoTraverserSimple(a_objVisitor);
	}

	public void visit(Monosaccharide aObjMonosaccharid) throws GlycoVisitorException 
	{
		throw new GlycoVisitorException("Monosaccharide is not supported for exporting: Use Namespace translation first.");		
	}

	public void visit(NonMonosaccharide aObjResidue) throws GlycoVisitorException 
	{
		throw new GlycoVisitorException("NonMonosaccharide is not supported for exporting: Use Namespace translation first.");
	}

	public void visit(Substituent aObjSubstituent) throws GlycoVisitorException 
	{
		throw new GlycoVisitorException("Substituent is not supported for exporting: Use Namespace translation first.");		
	}

	public void visit(SugarUnitCyclic aObjCyclic) throws GlycoVisitorException 
	{
		// nothing to do
	}

	public void visit(SugarUnitAlternative aObjAlternative) throws GlycoVisitorException 
	{
		throw new GlycoVisitorException("Alternative residues are not supported for exporting: Use Namespace translation first.");		
	}

	public void visit(GlycoEdge aObjLinkage) throws GlycoVisitorException
	{
		// nothing to do
	}

	public void visit(SugarUnitRepeat a_repeat) throws GlycoVisitorException 
	{
		GlycoTraverser t_traverser = this.getTraverser(this);
		t_traverser.traverseGraph(a_repeat);
		for (UnderdeterminedSubTree t_tree : a_repeat.getUndeterminedSubTrees())
		{
			t_traverser.traverseGraph(t_tree);
		}
	}

	public void start(Sugar a_sugar) throws GlycoVisitorException 
	{
		GlycoTraverser t_traverser = this.getTraverser(this);
		t_traverser.traverseGraph(a_sugar);
		for (UnderdeterminedSubTree t_tree : a_sugar.getUndeterminedSubTrees())
		{
			t_traverser.traverseGraph(t_tree);
		}
	}

	public void visit(UnvalidatedGlycoNode a_node) throws GlycoVisitorException 
	{
		String t_name = a_node.getName();
		try
		{
		    if ( ( t_name.charAt(0) == 'D' || t_name.charAt(0) == 'L') && ( t_name.charAt(t_name.length()-1) == 'a' || t_name.charAt(t_name.length()-1) == 'b') )
		    {
		        // it is a monosaccharide
		        String t_nameNew = t_name.charAt(t_name.length()-1) + "-" + t_name.charAt(0) + "-" + t_name.substring(1,t_name.length()-1);
	            a_node.setName(t_nameNew);    
		    }
		}
		catch (Exception e) 
		{
			throw new GlycoVisitorException(e.getMessage(),e);
		}
	}

}
