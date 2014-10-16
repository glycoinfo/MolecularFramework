/**
 * 
 */
package org.eurocarbdb.MolecularFramework.util.analytical.misc;


import org.eurocarbdb.MolecularFramework.sugar.GlycoEdge;
import org.eurocarbdb.MolecularFramework.sugar.GlycoNode;
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


import java.util.HashMap;


/**
 * @author sahnemann
 *
 */
public class GlycoVisitorSugarGraphComposition implements GlycoVisitor
{
	private HashMap <Substituent,Integer> m_hSubstituent= new HashMap <Substituent,Integer> ();
    private HashMap <Monosaccharide,Integer> m_hMonosaccharide = new HashMap <Monosaccharide,Integer> ();
    private HashMap <NonMonosaccharide,Integer> m_hNonMonosaccharide = new HashMap <NonMonosaccharide,Integer> ();
	   
	private GlycoTraverser m_objTraverser;
    /**
     * @see de.glycosciences.MolecularFrameWork.util.SugarVisitor#visit(de.glycosciences.MolecularFrameWork.sugar.Monosaccharide)
     */
    public void visit(Monosaccharide a_objMonosaccharid) throws GlycoVisitorException
    {
        if ( this.m_objTraverser.getState() == GlycoTraverser.ENTER )
        {
        	if (!this.m_hMonosaccharide.containsKey(a_objMonosaccharid)){
            	this.m_hMonosaccharide.put(a_objMonosaccharid,1);
            }
            else {
            	this.m_hMonosaccharide.put(a_objMonosaccharid,
            			this.m_hMonosaccharide.get(a_objMonosaccharid)+1);
            }
        }
    }

    /**
     * @see de.glycosciences.MolecularFrameWork.util.SugarVisitor#visit(de.glycosciences.MolecularFrameWork.sugar.NonMonosaccharide)
     */
    public void visit(NonMonosaccharide a_objResidue) throws GlycoVisitorException
    {
        if ( this.m_objTraverser.getState() == GlycoTraverser.ENTER )
        {
        	if (!this.m_hNonMonosaccharide.containsKey(a_objResidue)){
            	this.m_hNonMonosaccharide.put(a_objResidue,1);
            }
            else {
            	this.m_hNonMonosaccharide.put(a_objResidue,
            			this.m_hNonMonosaccharide.get(a_objResidue)+1);
            }
        }
    }

   

    /**
     * @see de.glycosciences.MolecularFrameWork.util.SugarVisitor#visit(de.glycosciences.MolecularFrameWork.sugar.GlycosidicLinkage)
     */
    public void visit(GlycoEdge a_objLinkage) throws GlycoVisitorException
    {
        // Nothing to do        
    }

    /**
     * @see de.glycosciences.MolecularFrameWork.util.SugarVisitor#visit(de.glycosciences.MolecularFrameWork.sugar.SugarRepeatingUnit)
     */
    public void visit(SugarUnitRepeat a_objRepeate) throws GlycoVisitorException
    {
    	if ( this.m_objTraverser.getState() == GlycoTraverser.ENTER )
        {
    		GlycoTraverser t_trav = this.m_objTraverser;
    		this.m_objTraverser = this.getTraverser(this);
    		this.m_objTraverser.traverseGraph(a_objRepeate);
    		this.m_objTraverser= t_trav;  
    		
        	
        }
    }

    /**
     * @throws GlycoVisitorException 
     * @see de.glycosciences.MolecularFrameWork.util.SugarVisitor#getTraverser(de.glycosciences.MolecularFrameWork.util.SugarVisitor)
     */
    public GlycoTraverser getTraverser(GlycoVisitor a_objVisitor) throws GlycoVisitorException
    {
        return new GlycoTraverserTree(a_objVisitor);
    }

    /* (non-Javadoc)
	 * @see org.glycomedb.MolecularFrameWork.util.visitor.GlycoVisitor#visit(org.glycomedb.MolecularFrameWork.sugar.Substituent)
	 */
	public void visit(Substituent a_objSubstituent) throws GlycoVisitorException {
		 if ( this.m_objTraverser.getState() == GlycoTraverser.ENTER )
	        {
	            if (!this.m_hSubstituent.containsKey(a_objSubstituent)){
	            	this.m_hSubstituent.put(a_objSubstituent,1);
	            }
	            else {
	            	this.m_hSubstituent.put(a_objSubstituent,
	            			this.m_hSubstituent.get(a_objSubstituent)+1);
	            }
	        }
		
	}

	/* (non-Javadoc)
	 * @see org.glycomedb.MolecularFrameWork.util.visitor.GlycoVisitor#visit(org.glycomedb.MolecularFrameWork.sugar.SugarUnitCyclic)
	 */
	public void visit(SugarUnitCyclic a_objCyclic) throws GlycoVisitorException {
				
	}

	/* (non-Javadoc)
	 * @see org.glycomedb.MolecularFrameWork.util.visitor.GlycoVisitor#visit(org.glycomedb.MolecularFrameWork.sugar.SugarUnitAlternative)
	 */
	public void visit(SugarUnitAlternative a_objAlternative) throws GlycoVisitorException {
		
	}

	/* (non-Javadoc)
	 * @see org.glycomedb.MolecularFrameWork.util.visitor.GlycoVisitor#visit(org.glycomedb.MolecularFrameWork.sugar.UnvalidatedGlycoNode)
	 */
	public void visit(UnvalidatedGlycoNode a_objUnvalidated) throws GlycoVisitorException {
				
	}

    
    /**
     * @see de.glycosciences.MolecularFrameWork.util.SugarVisitor#clear()
     */
    public void clear()
    {
        this.m_hMonosaccharide.clear();
        this.m_hNonMonosaccharide.clear();
        this.m_hSubstituent.clear();
    }   
    
    
    public void start(Sugar a_objSugar) throws GlycoVisitorException
	{
        this.clear();
        this.m_objTraverser = this.getTraverser(this);
        this.m_objTraverser.traverseGraph(a_objSugar);
	}

	public void start(GlycoNode a_objResidue) throws GlycoVisitorException 
	{
        this.clear();
        this.m_objTraverser = this.getTraverser(this);
        this.m_objTraverser.traverse(a_objResidue);
	}
	
	public HashMap <NonMonosaccharide,Integer> getNonMonosaccharides (){
		return this.m_hNonMonosaccharide;
	}
	
	public HashMap <Monosaccharide,Integer> getMonosaccharides () {
		return this.m_hMonosaccharide;
	}
	
	public HashMap <Substituent,Integer> getSubstituents () {
		return this.m_hSubstituent;
	}
	
	
}