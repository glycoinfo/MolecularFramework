package org.eurocarbdb.MolecularFramework.util.analytical.misc;

import org.eurocarbdb.MolecularFramework.sugar.GlycoEdge;
import org.eurocarbdb.MolecularFramework.sugar.ModificationType;
import org.eurocarbdb.MolecularFramework.sugar.Monosaccharide;
import org.eurocarbdb.MolecularFramework.sugar.NonMonosaccharide;
import org.eurocarbdb.MolecularFramework.sugar.Substituent;
import org.eurocarbdb.MolecularFramework.sugar.Sugar;
import org.eurocarbdb.MolecularFramework.sugar.SugarUnitAlternative;
import org.eurocarbdb.MolecularFramework.sugar.SugarUnitCyclic;
import org.eurocarbdb.MolecularFramework.sugar.SugarUnitRepeat;
import org.eurocarbdb.MolecularFramework.sugar.UnvalidatedGlycoNode;
import org.eurocarbdb.MolecularFramework.util.traverser.GlycoTraverser;
import org.eurocarbdb.MolecularFramework.util.traverser.GlycoTraverserTree;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitor;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorException;



public class GlycoVisitorChargeCalculator implements GlycoVisitor {
	//TODO Check charges!
	
	 /**
     * Charge calculator for Glycans. Repeat Units calcultated once.
     * Acidic functions: -1
     * Phoshate (in chain): -1
     * Phospate (terminal): -2
     * Sulfate	(in chain): 0
     * Sulfate 	(terminal): -1
     * Pyruvate: -1
     * N-succinate:	-1
     * n-sulfate: -1
     * Amino (free)		  :	+1
     * N-ala : +1
     * 
     * 
     **/
	
		Integer m_icharge = 0;
	    private GlycoTraverser m_objTraverser;
	    
	    
		/* (non-Javadoc)
		 * @see org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitor#visit(org.eurocarbdb.MolecularFramework.sugar.Monosaccharide)
		 */
		public void visit(Monosaccharide a_objMonosaccharid) throws GlycoVisitorException {
			
			if (a_objMonosaccharid.hasModification(ModificationType.ACID,1)){
		    		m_icharge--;
		    	}
		    	
		    	if (a_objMonosaccharid.hasModification(ModificationType.ACID,a_objMonosaccharid.getSuperclass().getCAtomCount())){
		    		m_icharge--;
		    	}
		}
		/* (non-Javadoc)
		 * @see org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitor#visit(org.eurocarbdb.MolecularFramework.sugar.NonMonosaccharide)
		 */
		public void visit(NonMonosaccharide a_objResidue) throws GlycoVisitorException {
						
		}
		/* (non-Javadoc)
		 * @see org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitor#visit(org.eurocarbdb.MolecularFramework.sugar.SugarUnitRepeat)
		 */
		
		public void visit(SugarUnitRepeat a_objRepeat) throws GlycoVisitorException {
			GlycoTraverser t_objTraverser = this.getTraverser(this); 
	        t_objTraverser.traverseGraph(a_objRepeat); 
			
		}
		/* (non-Javadoc)
		 * @see org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitor#visit(org.eurocarbdb.MolecularFramework.sugar.Substituent)
		 */
		public void visit(Substituent a_objSubstituent) throws GlycoVisitorException {
			  	   if (a_objSubstituent.getSubstituentType().getName().equals("sulfate")){
		    		   m_icharge--;	    		   
		    	   }
		    	   if (a_objSubstituent.getSubstituentType().getName().equals("phosphate")){
		    		   m_icharge--;
		    		   m_icharge--;
		    	   }
		    	   
		    	   if (a_objSubstituent.getSubstituentType().getName().equals("pyrophosphate")){
		    		   m_icharge--;
		    		   m_icharge--;
		    		   m_icharge--;
		    	   }
		    	   if (a_objSubstituent.getSubstituentType().getName().equals("triphosphate")){
		    		   m_icharge--;
		    		   m_icharge--;
		    		   m_icharge--;
		    		   m_icharge--;
		    	   }
		     
		    	   if (a_objSubstituent.getSubstituentType().getName().equals("amino")){
		    		   m_icharge++;	    		   
		    	   }
		    	   
		    	   if (a_objSubstituent.getSubstituentType().getName().equals("amidino")){
		    		   m_icharge++;	    		   
		    	   }
		    	   if (a_objSubstituent.getSubstituentType().getName().equals("ethanolamine")){
		    		   m_icharge++;	    		   
		    	   }
		    	   if (a_objSubstituent.getSubstituentType().getName().equals("imino")){
		    		   m_icharge++;	    		   
		    	   }
		    	   if (a_objSubstituent.getSubstituentType().getName().equals("n-alanine")){
		    		   m_icharge++;	    		   
		    	   }
		    	   if (a_objSubstituent.getSubstituentType().getName().equals("n-dimethyl")){
		    		   m_icharge++;	    		   
		    	   }
		    	   if (a_objSubstituent.getSubstituentType().getName().equals("n-methyl")){
		    		   m_icharge++;	    		   
		    	   }
		    	   if (a_objSubstituent.getSubstituentType().getName().equals("n-succinate")){
		    		   m_icharge--;	    		   
		    	   }
		    	   if (a_objSubstituent.getSubstituentType().getName().equals("n-sulfate")){
		    		   m_icharge--;	    		   
		    	   }
		    	   if (a_objSubstituent.getSubstituentType().getName().equals("phosphate")){
		    		   m_icharge--;	    		   
		    	   }
		    	   if (a_objSubstituent.getSubstituentType().getName().equals("(r)-carboxymethyl")){
		    		   m_icharge--;	    		   
		    	   }
		    	   if (a_objSubstituent.getSubstituentType().getName().equals("(s)-carboxymethyl")){
		    		   m_icharge--;	    		   
		    	   }
		    	   if (a_objSubstituent.getSubstituentType().getName().equals("(r)-carboxyethyl")){
		    		   m_icharge--;	    		   
		    	   }
		    	   if (a_objSubstituent.getSubstituentType().getName().equals("(r)-carboxyethyl")){
		    		   m_icharge--;	    		   
		    	   }
		       
		       // pyruvate has different charges depending upon connectivity
		       
		       if (	a_objSubstituent.getSubstituentType().getName().equals("(r)-pyruvate")||
		    		a_objSubstituent.getSubstituentType().getName().equals("(s)-pyruvate")||
		    		a_objSubstituent.getSubstituentType().getName().equals("(x)-pyruvate")){
	    		   	m_icharge--;	    		   
	    	   }
			
		}
		/* (non-Javadoc)
		 * @see org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitor#visit(org.eurocarbdb.MolecularFramework.sugar.SugarUnitCyclic)
		 */
		public void visit(SugarUnitCyclic a_objCyclic) throws GlycoVisitorException {
			
			
		}
		/* (non-Javadoc)
		 * @see org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitor#visit(org.eurocarbdb.MolecularFramework.sugar.SugarUnitAlternative)
		 */
		public void visit(SugarUnitAlternative a_objAlternative) throws GlycoVisitorException {
			
			
		}
		/* (non-Javadoc)
		 * @see org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitor#visit(org.eurocarbdb.MolecularFramework.sugar.UnvalidatedGlycoNode)
		 */
		public void visit(UnvalidatedGlycoNode a_objUnvalidated) throws GlycoVisitorException {
			
			
		}
		/* (non-Javadoc)
		 * @see org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitor#visit(org.eurocarbdb.MolecularFramework.sugar.GlycoEdge)
		 */
		public void visit(GlycoEdge a_objLinkage) throws GlycoVisitorException {
						
		}
		/* (non-Javadoc)
		 * @see org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitor#start(org.eurocarbdb.MolecularFramework.sugar.Sugar)
		 */
		public void start(Sugar a_objSugar) throws GlycoVisitorException {
			this.clear();
	        this.m_objTraverser = this.getTraverser(this);
	        this.m_objTraverser.traverseGraph(a_objSugar);
			
		}
		/* (non-Javadoc)
		 * @see org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitor#getTraverser(org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitor)
		 */
		public GlycoTraverser getTraverser(GlycoVisitor a_objVisitor) throws GlycoVisitorException {
			return new GlycoTraverserTree(a_objVisitor);
		}
		/* (non-Javadoc)
		 * @see org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitor#clear()
		 */
		public void clear() {
			this.m_icharge=0;
			
		}    
	 
	    /**
	     * @see de.glycosciences.MolecularFrameWork.util.SugarVisitor#visit(de.glycosciences.MolecularFrameWork.sugar.NonMonosaccharide)
	     */
	   
		public Integer getCharge (){
			return this.m_icharge;
		}			
	     
	    
	}

