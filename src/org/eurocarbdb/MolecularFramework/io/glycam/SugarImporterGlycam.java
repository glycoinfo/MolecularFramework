package org.eurocarbdb.MolecularFramework.io.glycam;

import java.util.ArrayList;
import java.util.Iterator;

import org.eurocarbdb.MolecularFramework.io.SugarImporterException;
import org.eurocarbdb.MolecularFramework.io.SugarImporterText;
import org.eurocarbdb.MolecularFramework.io.iupac.IupacSubTree;
import org.eurocarbdb.MolecularFramework.sugar.GlycoEdge;
import org.eurocarbdb.MolecularFramework.sugar.GlycoNode;
import org.eurocarbdb.MolecularFramework.sugar.GlycoconjugateException;
import org.eurocarbdb.MolecularFramework.sugar.Linkage;
import org.eurocarbdb.MolecularFramework.sugar.UnvalidatedGlycoNode;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorException;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorNodeType;

/**
 *  Importer for loading Glycam notation into EUROCarbDB object model
 *
 *	DManpa1-3/6DManpa1-6[DManpa1-3]DManpb1-4DGlcpNAcb1-4DGlcpNAcb1-OH
 *
 *	start 		::= residue { linkage { subbranch } residue }
 *  linkage     ::= number "-" number [ "/" number ]
 *  resiude		::= symbol { symbol }
 *  subbranch	::= "[" residue linkage { { subbranch } residue linkage } "]"
 *  symbol		::= character | number
 */
public class SugarImporterGlycam extends SugarImporterText
{
    private String m_reducingEnd = null;
	/**
	 * residue { linkage { subbranch } residue }
	 */
	protected void start() throws SugarImporterException 
	{
		try 
		{
			this.clear();
			UnvalidatedGlycoNode t_objResiduumChild = new UnvalidatedGlycoNode();
			UnvalidatedGlycoNode t_objResiduumParent = new UnvalidatedGlycoNode();
			int t_iStartPosition = this.m_iPosition;
			// residue 
			this.residue();
			String t_strResidueName = this.m_strText.substring( t_iStartPosition , this.m_iPosition );
			t_objResiduumChild.setName( t_strResidueName );
			this.m_objSugar.addNode(t_objResiduumChild);
			
			while ( this.m_cToken != '$' )
			{
				GlycoEdge t_objEdge = this.linkage(t_objResiduumChild);
				ArrayList<IupacSubTree> t_aIupacSubtree = new ArrayList<IupacSubTree>();
				while ( this.m_cToken == '[' )
				{
					t_aIupacSubtree.add(this.subbranch());				
				}
				t_iStartPosition = this.m_iPosition;
				this.residue();
				t_strResidueName = this.m_strText.substring( t_iStartPosition , this.m_iPosition );
				t_objResiduumParent.setName( t_strResidueName );
				this.m_objSugar.addNode(t_objResiduumParent);
				this.m_objSugar.addEdge(t_objResiduumParent, t_objResiduumChild, t_objEdge);
				// add subtrees
				for (Iterator<IupacSubTree> t_iterSubtree = t_aIupacSubtree.iterator(); t_iterSubtree.hasNext();) 
				{
					IupacSubTree t_objTree = t_iterSubtree.next();
					this.m_objSugar.addEdge(t_objResiduumParent, t_objTree.getGlycoNode(), t_objTree.getGlycoEdge());
				}
				t_objResiduumChild = t_objResiduumParent;
				t_objResiduumParent = new UnvalidatedGlycoNode();
			}
			if ( ! this.finished() )
			{
				throw new SugarImporterException("IUPAC002", this.m_iPosition);
			}
			// remove reducing -OH from root
			GlycoVisitorNodeType t_visNodeType = new GlycoVisitorNodeType();
			for (GlycoNode t_node : this.m_objSugar.getRootNodes()) 
			{
				UnvalidatedGlycoNode t_residue = t_visNodeType.getUnvalidatedNode(t_node);
				String t_strName = t_residue.getName();
				int t_index = t_strName.lastIndexOf("-");
				if ( t_index > 0 )
				{
				    t_residue.setName(t_strName.substring(0,t_index-1));
				    this.m_reducingEnd = t_strName.substring(t_index-1);
				}
			}
		}
		catch (GlycoVisitorException e) 
		{
			throw new SugarImporterException("COMMON000", this.m_iPosition);
		}
		catch (GlycoconjugateException e) 
		{
			throw new SugarImporterException("COMMON013", this.m_iPosition);
		}
		
	}

	/**
	 * number "-" number [ "/" number ]
	 * 
	 * @param a_nodeChild
	 * @return
	 * @throws SugarImporterException 
	 * @throws GlycoconjugateException 
	 */
	private GlycoEdge linkage(UnvalidatedGlycoNode a_nodeChild) throws SugarImporterException, GlycoconjugateException 
	{
		Linkage t_objLinkage = new Linkage();
		t_objLinkage.addChildLinkage(this.number());
		if ( this.m_cToken != '-' )
		{
			throw new SugarImporterException("IUPAC005", this.m_iPosition);
		}
		this.nextToken();
		t_objLinkage.addParentLinkage(this.number());
		while ( this.m_cToken == '/' )
		{
		    this.nextToken();
		    t_objLinkage.addParentLinkage(this.number());
		}
		GlycoEdge t_edge = new GlycoEdge();
		t_edge.addGlycosidicLinkage(t_objLinkage);
		return t_edge;
	}

	/**
	 * "[" residue linkage { { subbranch } residue linkage } "]"
	 */
	private IupacSubTree subbranch() throws SugarImporterException, GlycoconjugateException
	{
		IupacSubTree t_objIupacResidue = new IupacSubTree();
		if ( this.m_cToken != '[' )
		{
			throw new SugarImporterException("IUPAC004", this.m_iPosition);
		}
		this.nextToken();
		// residue
		int t_iStartPosition = this.m_iPosition;
		this.residue();
		String t_strResidueName = this.m_strText.substring( t_iStartPosition , this.m_iPosition );
		UnvalidatedGlycoNode t_node = new UnvalidatedGlycoNode();
		t_node.setName(t_strResidueName);
		// linkage
		GlycoEdge t_edge = this.linkage(t_node);
		t_objIupacResidue.setGlycoEdge(t_edge);
		t_objIupacResidue.setGlycoNode(t_node);
		while ( this.m_cToken != ']' )
		{
			ArrayList<IupacSubTree> t_aIupacSubtree = new ArrayList<IupacSubTree>();
			t_aIupacSubtree.add(t_objIupacResidue);
			while ( this.m_cToken == '[' )
			{
				t_aIupacSubtree.add(this.subbranch());				
			}
			// residue
			t_iStartPosition = this.m_iPosition;
			this.residue();
			t_strResidueName = this.m_strText.substring( t_iStartPosition , this.m_iPosition );
			t_node = new UnvalidatedGlycoNode();
			t_node.setName(t_strResidueName);
			// linkage 
			t_edge = this.linkage(t_node);
			this.m_objSugar.addNode(t_node);
			// add subtrees
			for (Iterator<IupacSubTree> t_iterSubtree = t_aIupacSubtree.iterator(); t_iterSubtree.hasNext();) 
			{
				IupacSubTree t_objTree = t_iterSubtree.next();
				this.m_objSugar.addEdge(t_node, t_objTree.getGlycoNode(), t_objTree.getGlycoEdge());
			}
			t_objIupacResidue = new IupacSubTree();
			t_objIupacResidue.setGlycoEdge(t_edge);
			t_objIupacResidue.setGlycoNode(t_node);
		}
		this.nextToken();
		return t_objIupacResidue;
	}

	/**
	 * symbol		::= character | number | -
	 * @throws SugarImporterException 
	 */
	private void symbol() throws SugarImporterException 
	{
	    int t_iDigit = (int) this.m_cToken;;
	    if ( t_iDigit > 47 && t_iDigit < 58 )
	    {
	        this.number();
	    }
	    else if ( this.m_cToken == '-' )
	    {
	        this.nextToken();
	    }
	    else
	    {
	        this.character();
	    }
	}

	   /**
     * resiude      ::= symbol { symbol }
     * @throws SugarImporterException 
     */
    private void residue() throws SugarImporterException 
    {
        this.symbol();
        while ( this.m_cToken != '$' )
        {
            if ( this.isNumber(0) && this.aheadToken(1) == '-' && this.isNumber(2) )
            {
                return;
            }
            this.symbol();
        }
    }

	private boolean isNumber(int a_position)
    {
	    int t_newPosition = this.m_iPosition + a_position;
	    if ( t_newPosition < this.m_iLength )
	    {
	        char t_character = this.m_strText.charAt( t_newPosition );
	        int t_iDigit = (int) t_character;
	        if ( t_iDigit > 47 && t_iDigit < 58 )
	        {
	            return true;
	        }
	    }
	    return false;
    }

    private void clear() 
	{}

    public String getReducingEnd()
    {
        return m_reducingEnd;
    }
}
