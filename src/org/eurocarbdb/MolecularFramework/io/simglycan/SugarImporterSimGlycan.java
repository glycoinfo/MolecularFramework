/**
 * 
 */
package org.eurocarbdb.MolecularFramework.io.simglycan;

import java.util.ArrayList;
import java.util.Iterator;

import org.eurocarbdb.MolecularFramework.io.SugarImporterException;
import org.eurocarbdb.MolecularFramework.io.SugarImporterText;
import org.eurocarbdb.MolecularFramework.io.iupac.IupacSubTree;
import org.eurocarbdb.MolecularFramework.sugar.GlycoEdge;
import org.eurocarbdb.MolecularFramework.sugar.GlycoconjugateException;
import org.eurocarbdb.MolecularFramework.sugar.Linkage;
import org.eurocarbdb.MolecularFramework.sugar.UnvalidatedGlycoNode;

/**
 * SimGlycan interpretation of IUPAC condensed
 *
 *	Gal(b1-)[S(-3)]GlcNAc(b1-2)Man
 *
 *	start 		::= residue { linkage { subbranch } residue }
 *  linkage     ::= "(" [ [ anomer ] number ] "-" [ number ] ")"
 *	anomer		::= a | b | ? | o
 *  resiude		::= symbol { symbol }
 *  subbranch	::= "[" residue linkage { { subbranch } residue linkage } "]"
 *  symbol		::= character | "?" | "-" | number
 */
public class SugarImporterSimGlycan extends SugarImporterText
{
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
		}
		catch (GlycoconjugateException e) 
		{
			throw new SugarImporterException("COMMON013", this.m_iPosition);
		}

	}

	/**
	 * "(" [ [ anomer ] number ] "-" [ number ] ")"
	 * 
	 * @param a_nodeChild
	 * @return
	 * @throws SugarImporterException 
	 * @throws GlycoconjugateException 
	 */
	private GlycoEdge linkage(UnvalidatedGlycoNode a_nodeChild) throws SugarImporterException, GlycoconjugateException 
	{
		Linkage t_objLinkage = new Linkage();
		if ( this.m_cToken != '(' )
		{
			throw new SugarImporterException("IUPAC000", this.m_iPosition);
		}
		this.nextToken();
		if ( this.m_cToken != '-' )
		{
			// residue "(" [anomer] linkageposition 
			int t_iDigit = (int) this.m_cToken;
			if ( t_iDigit < 49 || t_iDigit > 57 )
			{
				a_nodeChild.setName( this.anomer() + "-" + a_nodeChild.getName() );
			}
			t_objLinkage.addChildLinkage(this.number());
		}
		else
		{
			t_objLinkage.addChildLinkage(Linkage.UNKNOWN_POSITION);	
		}
		if ( this.m_cToken != '-' )
		{
			throw new SugarImporterException("IUPAC005", this.m_iPosition);
		}
		this.nextToken();
		if ( this.m_cToken != ')' )
		{
			t_objLinkage.addParentLinkage(this.number());
		}
		else
		{
			t_objLinkage.addParentLinkage(Linkage.UNKNOWN_POSITION);
		}
		GlycoEdge t_edge = new GlycoEdge();
		t_edge.addGlycosidicLinkage(t_objLinkage);
		if ( this.m_cToken != ')' )
		{
			throw new SugarImporterException("IUPAC001", this.m_iPosition);
		}
		this.nextToken();
		return t_edge;
	}

	/**
	 * "[" residue linkage { { subbranch } residue linkage } "]"
	 * 
	 * @throws SugarImporterException 
	 * @throws GlycoconjugateException 
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
	 * anomer		::= a | b | ? | o
	 * @throws SugarImporterException 
	 */
	private String anomer() throws SugarImporterException 
	{
		if ( this.m_cToken == 'a' )
		{
			this.nextToken();
			return "a";
		}
		else if ( this.m_cToken == 'b' )
		{
			this.nextToken();
			return "b";
		}
		else if ( this.m_cToken == '?' )
		{
			this.nextToken();
			return "?";
		}
		else if ( this.m_cToken == 'o' )
		{
			this.nextToken();
			return "o";
		}
		else
		{
			throw new SugarImporterException("IUPAC0003", this.m_iPosition);
		}
	}

	/**
	 * resiude		::= symbol { symbol }
	 * @throws SugarImporterException 
	 */
	private void residue() throws SugarImporterException 
	{
		this.symbol();
		while ( this.m_cToken != '(' && this.m_cToken != '$' )
		{
			this.symbol();
		}
	}

	/**
	 * symbol		::= character | "?" | "-" | number
	 * @throws SugarImporterException 
	 */
	private void symbol() throws SugarImporterException 
	{
		if ( this.m_cToken == '?' || this.m_cToken == '-' )
		{
			this.nextToken();
		}
		else
		{
			int t_iDigit = (int) this.m_cToken;;
			if ( t_iDigit > 47 && t_iDigit < 58 )
			{
				this.number();
			}
			else
			{
				this.character();
			}
		}
	}

	private void clear() 
	{}

}
