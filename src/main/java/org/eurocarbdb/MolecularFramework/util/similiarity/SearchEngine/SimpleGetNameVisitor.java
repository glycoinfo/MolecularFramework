/**
 * 
 */
package org.eurocarbdb.MolecularFramework.util.similiarity.SearchEngine;

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
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitor;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorException;

/**
 * @author sherget
 *
 */
public class SimpleGetNameVisitor implements GlycoVisitor {

	String m_sName;
	
	/* (non-Javadoc)
	 * @see org.glycomedb.MolecularFrameWork.util.visitor.GlycoVisitor#visit(org.glycomedb.MolecularFrameWork.sugar.Monosaccharide)
	 */
	public void visit(Monosaccharide a_objMonosaccharid) throws GlycoVisitorException {
		this.m_sName=a_objMonosaccharid.getGlycoCTName();
		
	}

	/* (non-Javadoc)
	 * @see org.glycomedb.MolecularFrameWork.util.visitor.GlycoVisitor#visit(org.glycomedb.MolecularFrameWork.sugar.NonMonosaccharide)
	 */
	public void visit(NonMonosaccharide a_objResidue) throws GlycoVisitorException {
		
		
	}

	/* (non-Javadoc)
	 * @see org.glycomedb.MolecularFrameWork.util.visitor.GlycoVisitor#visit(org.glycomedb.MolecularFrameWork.sugar.SugarUnitRepeat)
	 */
	public void visit(SugarUnitRepeat a_objRepeat) throws GlycoVisitorException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.glycomedb.MolecularFrameWork.util.visitor.GlycoVisitor#visit(org.glycomedb.MolecularFrameWork.sugar.Substituent)
	 */
	public void visit(Substituent a_objSubstituent) throws GlycoVisitorException {
		this.m_sName=a_objSubstituent.getSubstituentType().getName();
		
	}

	/* (non-Javadoc)
	 * @see org.glycomedb.MolecularFrameWork.util.visitor.GlycoVisitor#visit(org.glycomedb.MolecularFrameWork.sugar.SugarUnitCyclic)
	 */
	public void visit(SugarUnitCyclic a_objCyclic) throws GlycoVisitorException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.glycomedb.MolecularFrameWork.util.visitor.GlycoVisitor#visit(org.glycomedb.MolecularFrameWork.sugar.SugarUnitAlternative)
	 */
	public void visit(SugarUnitAlternative a_objAlternative) throws GlycoVisitorException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.glycomedb.MolecularFrameWork.util.visitor.GlycoVisitor#visit(org.glycomedb.MolecularFrameWork.sugar.UnvalidatedGlycoNode)
	 */
	public void visit(UnvalidatedGlycoNode a_objUnvalidated) throws GlycoVisitorException {
		this.m_sName=a_objUnvalidated.getName();
		
	}

	/* (non-Javadoc)
	 * @see org.glycomedb.MolecularFrameWork.util.visitor.GlycoVisitor#visit(org.glycomedb.MolecularFrameWork.sugar.GlycoEdge)
	 */
	public void visit(GlycoEdge a_objLinkage) throws GlycoVisitorException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.glycomedb.MolecularFrameWork.util.visitor.GlycoVisitor#start(org.glycomedb.MolecularFrameWork.sugar.Sugar)
	 */
	public void start(Sugar a_objSugar) throws GlycoVisitorException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.glycomedb.MolecularFrameWork.util.visitor.GlycoVisitor#getTraverser(org.glycomedb.MolecularFrameWork.util.visitor.GlycoVisitor)
	 */
	public GlycoTraverser getTraverser(GlycoVisitor a_objVisitor) throws GlycoVisitorException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.glycomedb.MolecularFrameWork.util.visitor.GlycoVisitor#clear()
	 */
	public void clear() {
		this.m_sName="";
		
	}
	
	public String getName(){
		return this.m_sName;
	}

}
