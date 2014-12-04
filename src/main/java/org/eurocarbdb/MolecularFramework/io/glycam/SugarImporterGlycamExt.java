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
 *	A-D-Manp-(1-3)-[A-D-Manp-(1-6)]-B-D-Manp-(1-4)-B-D-GlcpNAc-(1-4)-B-D-GlcpNAc-OH
 *
 *	start 		::= residue { linkage "-" { subbranch } residue }
 *  linkage     ::= "-" "(" number "-" number ")"
 *  resiude		::= symbol { symbol }
 *  subbranch	::= "[" residue linkage { "-" { subbranch } residue linkage } "]"
 *  symbol		::= character | "-" | number
 */
public class SugarImporterGlycamExt extends SugarImporterText
{
	/**
	 * residue { linkage "-" { subbranch } residue }
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
				if ( this.m_cToken != '-' )
				{
					throw new SugarImporterException("IUPAC005", this.m_iPosition);
				}
				this.nextToken();
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
				if ( t_strName.endsWith("-OH") )
				{
					t_residue.setName(t_strName.substring(0,t_strName.length()-3));
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
	 * "-" "(" number "-" number ")"
	 * 
	 * @param a_nodeChild
	 * @return
	 * @throws SugarImporterException 
	 * @throws GlycoconjugateException 
	 */
	private GlycoEdge linkage(UnvalidatedGlycoNode a_nodeChild) throws SugarImporterException, GlycoconjugateException 
	{
		Linkage t_objLinkage = new Linkage();
		if ( this.m_cToken != '-' )
		{
			throw new SugarImporterException("IUPAC005", this.m_iPosition);
		}
		this.nextToken();
		if ( this.m_cToken != '(' )
		{
			throw new SugarImporterException("IUPAC000", this.m_iPosition);
		}
		this.nextToken();
		t_objLinkage.addChildLinkage(this.number());
		if ( this.m_cToken != '-' )
		{
			throw new SugarImporterException("IUPAC005", this.m_iPosition);
		}
		this.nextToken();
		t_objLinkage.addParentLinkage(this.number());
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
	 * "[" residue linkage { "-" { subbranch } residue linkage } "]"
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
			if ( this.m_cToken != '-' )
			{
				throw new SugarImporterException("IUPAC005", this.m_iPosition);
			}
			this.nextToken();
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
	 * resiude		::= symbol { symbol }
	 * @throws SugarImporterException 
	 */
	private void residue() throws SugarImporterException 
	{
		this.symbol();
		while ( (this.m_cToken != '(' && this.m_cToken != '$') )
		{
			if ( this.m_cToken == '-' && this.aheadToken(1) == '(' )
			{
				return;
			}
			this.symbol();
		}
	}

	/**
	 * symbol		::= character | "?" | "-" | number
	 * @throws SugarImporterException 
	 */
	private void symbol() throws SugarImporterException 
	{
		if ( this.m_cToken == '?' )
		{
			this.nextToken();
		}
		else if ( this.m_cToken == '-' )
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
