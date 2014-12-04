package org.eurocarbdb.MolecularFramework.util.visitor;

import org.eurocarbdb.MolecularFramework.sugar.GlycoEdge;
import org.eurocarbdb.MolecularFramework.sugar.Monosaccharide;
import org.eurocarbdb.MolecularFramework.sugar.NonMonosaccharide;
import org.eurocarbdb.MolecularFramework.sugar.Substituent;
import org.eurocarbdb.MolecularFramework.sugar.Sugar;
import org.eurocarbdb.MolecularFramework.sugar.SugarUnitAlternative;
import org.eurocarbdb.MolecularFramework.sugar.SugarUnitCyclic;
import org.eurocarbdb.MolecularFramework.sugar.SugarUnitRepeat;
import org.eurocarbdb.MolecularFramework.sugar.UnvalidatedGlycoNode;
import org.eurocarbdb.MolecularFramework.util.traverser.GlycoTraverser;



public interface GlycoVisitor 
{	
	public void visit     ( Monosaccharide            a_objMonosaccharid  ) throws GlycoVisitorException;
    public void visit     ( NonMonosaccharide         a_objResidue 		  ) throws GlycoVisitorException;
    public void visit     ( SugarUnitRepeat           a_objRepeat         ) throws GlycoVisitorException;
    public void visit     ( Substituent               a_objSubstituent    ) throws GlycoVisitorException;
    public void visit     ( SugarUnitCyclic           a_objCyclic         ) throws GlycoVisitorException;
    public void visit     ( SugarUnitAlternative      a_objAlternative    ) throws GlycoVisitorException;
    public void visit	  ( UnvalidatedGlycoNode 	  a_objUnvalidated 	  ) throws GlycoVisitorException;
	public void visit	  ( GlycoEdge 				  a_objLinkage		  ) throws GlycoVisitorException;
	
	public void start	  ( Sugar 					  a_objSugar		  ) throws GlycoVisitorException;
	
	public GlycoTraverser getTraverser(GlycoVisitor a_objVisitor) throws GlycoVisitorException;
	
	public void clear();
}