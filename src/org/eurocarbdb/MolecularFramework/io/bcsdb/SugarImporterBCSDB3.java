package org.eurocarbdb.MolecularFramework.io.bcsdb;


import java.util.ArrayList;
import java.util.Iterator;

import org.eurocarbdb.MolecularFramework.io.SugarImporterException;
import org.eurocarbdb.MolecularFramework.io.SugarImporterText;
import org.eurocarbdb.MolecularFramework.io.bcsdb.BcsdbSubTree3;
import org.eurocarbdb.MolecularFramework.sugar.GlycoEdge;
import org.eurocarbdb.MolecularFramework.sugar.GlycoNode;
import org.eurocarbdb.MolecularFramework.sugar.GlycoconjugateException;
import org.eurocarbdb.MolecularFramework.sugar.Linkage;
import org.eurocarbdb.MolecularFramework.sugar.Sugar;
import org.eurocarbdb.MolecularFramework.sugar.SugarUnitRepeat;
import org.eurocarbdb.MolecularFramework.sugar.UnderdeterminedSubTree;
import org.eurocarbdb.MolecularFramework.sugar.UnvalidatedGlycoNode;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorException;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorReplaceName;

/**
 * start            ::= "-" [ "P" "-" ] <linkage_redu> ")" <rsugarchain> "(" <linkage_nonredu> "-" [ "P" "-" ] 
 *                  | "P" "-" ")" [ <sidechain> ] <sugarchain>
 *                  | <sugarchain> 
 *
 * rsugarchain      ::= [ <sidechain> ] <residue> { <linkages> [ <sidechain> ] <residue> }  <== spezielles zusatzkriterium ;
 * 
 * sugarchain       ::= <residue> { <linkage> [ <sidechain> ] <residue> } [ "(" <linkage_nonredu> "-" <residue> ] <== spezielles zusatzkriterium ; 
 *
 * sidechain        ::= "[" <side_residue> [ <sidechain_follow> ] "]"
 * 
 * side_residue     ::= <residue> <linkage> | "P" "-" <linkage_redu> ")"  | "S" "-" <linkage_redu> ")"
 * 
 * sidechain_follow ::= "," <side_residue> [ <sidechain_follow> ] 
 *                  | <sidechain> <residue> <linkage> [ <sidechain_follow> ]
 *                  | <residue> <linkage> [ <sidechain_follow> ]  
 * 
 * residue          ::= <character> | '?'  { <character> | <number> | '?' | '-' }
 * 
 * linkage          ::= "(" <linkage_nonredu> "-" { "P" "-" } <linkage_redu> ")"
 * 
 * linkage_redu     ::= "?" | <number>
 * 
 * linkage_nonredu  ::= "?" | <number>
 * 
 * @author rene
 *
 */
public class SugarImporterBCSDB3 extends SugarImporterText
{    
	private int m_iMinRepeatCount = -1;
	private int m_iMaxRepeatCount = -1;
    private BcsdbSubTree3 m_objRepeatEnd = null;
    private Linkage m_objInternalLinkage = null;
    
    /**
	 * Parse a string according the gramatic of the language. Uses recursiv decent
	 *  
	 * @param a_strStream		String that is to parse
	 * @throws ImportExeption 
	 */
	public Sugar parse(String a_strStream) throws SugarImporterException 
	{
		String[] t_aCodes = a_strStream.split(" // ");
        this.m_objRepeatEnd = null;
        this.m_objInternalLinkage = null;
		this.m_objSugar = new Sugar();
		this.m_iPosition = -1;
        // Copie string and add endsymbol
		this.m_strText = t_aCodes[0].trim() + '$';
		this.m_iLength = this.m_strText.length();
		// get first token . Error ? ==> string empty
		this.nextToken();
		this.start();
		// sug definition
		if ( t_aCodes.length > 1 )
		{
		    t_aCodes = t_aCodes[1].split(";");
		    for (int t_iCounter = 0; t_iCounter < t_aCodes.length; t_iCounter++) 
		    {
		        // comment field exists
		        int t_iPos = t_aCodes[t_iCounter].indexOf("=");
		        if ( t_iPos != -1 )
		        {
		            String t_strSug = t_aCodes[t_iCounter].substring(0,t_iPos).trim();
		            if ( this.isReplaceString(t_strSug) )
		            {
		            	// found sugar residue
		            	GlycoVisitorReplaceName t_objVisitor = new GlycoVisitorReplaceName( t_strSug , t_aCodes[t_iCounter].substring(t_iPos+1).trim() );
		            	try 
		            	{
		            		t_objVisitor.start(this.m_objSugar);
		            	} 
		            	catch (GlycoVisitorException e) 
		            	{
		            		throw new SugarImporterException("BCSDB021", this.m_iPosition);
		            	}
		            }
		        }
		        else
		        {
		            throw new SugarImporterException("BCSDB020", this.m_iPosition);
		        }
		    }
		}
        return this.m_objSugar;
	}

    /**
	 * @param sug
	 * @return
	 */
	private boolean isReplaceString(String a_strString) 
	{
		if ( a_strString.equals("Sug") )
		{
			return true;
		}
		if ( a_strString.equals("Subst") )
		{
			return true;
		}
		if ( a_strString.equals("Subst1") )
		{
			return true;
		}
		if ( a_strString.equals("Subst2") )
		{
			return true;
		}
		if ( a_strString.equals("Subst3") )
		{
			return true;
		}
		if ( a_strString.equals("Subst4") )
		{
			return true;
		}
		if ( a_strString.equals("Subst5") )
		{
			return true;
		}
		if ( a_strString.equals("PEN") )
		{
			return true;
		}
		if ( a_strString.equals("HEX") )
		{
			return true;
		}
		if ( a_strString.equals("HEP") )
		{
			return true;
		}
		if ( a_strString.equals("DDHEP") )
		{
			return true;
		}
		if ( a_strString.equals("LDHEP") )
		{
			return true;
		}
		if ( a_strString.equals("OCT") )
		{
			return true;
		}
		if ( a_strString.equals("NON") )
		{
			return true;
		}
		if ( a_strString.equals("LIP") )
		{
			return true;
		}
		if ( a_strString.equals("CER") )
		{
			return true;
		}
		if ( a_strString.equals("ALK") )
		{
			return true;
		}
		return false;
	}

	protected void start() throws SugarImporterException
    {
        try
        {
        	this.buildUpSugar( this.startparsing() );
        } 
        catch (GlycoconjugateException e)
        {
        	e.printStackTrace(System.out);
            throw new SugarImporterException("COMMON013", this.m_iPosition);            
        }
    }
    
    /**
	 * @param startparsing
     * @throws GlycoconjugateException 
     * @throws SugarImporterException 
	 */
	private void buildUpSugar(BcsdbSubTree3 a_objResidue) throws GlycoconjugateException, SugarImporterException 
	{
		if ( this.m_objInternalLinkage == null )
		{
			// normal sugar
			UnvalidatedGlycoNode t_objNode = new UnvalidatedGlycoNode();
			if ( a_objResidue.m_strResidue.indexOf('%') != -1 )
			{
				throw new SugarImporterException("BCSDB024", this.m_iPosition);
			}
			t_objNode.setName(a_objResidue.m_strResidue);
			a_objResidue.m_objNode = t_objNode;
			this.m_objSugar.addNode(t_objNode);
			for (Iterator<BcsdbSubTree3> t_iterChilds = a_objResidue.m_aSubresidue.iterator(); t_iterChilds.hasNext();) 
			{
				this.addSugarResidue(t_iterChilds.next(),t_objNode,this.m_objSugar);				
			}
		}
		else
		{
			// repeat unit
			SugarUnitRepeat t_objRepeat = new SugarUnitRepeat();
			if ( a_objResidue.m_strResidue.indexOf('%') != -1 )
			{
				throw new SugarImporterException("BCSDB024", this.m_iPosition);
			}
			UnvalidatedGlycoNode t_objNode = new UnvalidatedGlycoNode();
			t_objNode.setName(a_objResidue.m_strResidue);
			a_objResidue.m_objNode = t_objNode;
			t_objRepeat.addNode(t_objNode);
			for (Iterator<BcsdbSubTree3> t_iterChilds = a_objResidue.m_aSubresidue.iterator(); t_iterChilds.hasNext();) 
			{
				this.addRepeatResidue(t_iterChilds.next(),t_objNode,t_objRepeat);				
			}
			
			GlycoEdge t_objInternal = new GlycoEdge();
			t_objInternal.addGlycosidicLinkage(this.m_objInternalLinkage);
			t_objRepeat.setMinRepeatCount(this.m_iMinRepeatCount);
			t_objRepeat.setMaxRepeatCount(this.m_iMaxRepeatCount);
			t_objRepeat.setRepeatLinkage(t_objInternal, this.m_objRepeatEnd.m_objNode, t_objNode);
			
			this.m_objSugar.addNode(t_objRepeat);
		}
	}

	/**
	 * @param next
	 * @param node
	 * @param sugar
	 * @param b
	 * @param c
	 * @throws GlycoconjugateException 
	 * @throws SugarImporterException 
	 */
	private void addSugarResidue(BcsdbSubTree3 a_objResidue, GlycoNode a_objParent, Sugar a_objGraph ) throws GlycoconjugateException, SugarImporterException 
	{
		Sugar t_objGraph = a_objGraph;
		UnvalidatedGlycoNode t_objNode = new UnvalidatedGlycoNode();
		String t_strResidueName = a_objResidue.m_strResidue;
		String[] t_aStrings = t_strResidueName.split("%");
		a_objResidue.m_objNode = t_objNode;
		if ( t_aStrings.length == 2 )
		{
			t_objNode.setName(t_aStrings[1]);
			a_objGraph.addNode(t_objNode);
			a_objGraph.addEdge(a_objParent, t_objNode, a_objResidue.m_objEdge);
			for (Iterator<BcsdbSubTree3> t_iterChilds = a_objResidue.m_aSubresidue.iterator(); t_iterChilds.hasNext();) 
			{
				this.addSugarResidue(t_iterChilds.next(),t_objNode,t_objGraph);				
			}
		}
		else
		{
			t_objNode.setName(t_strResidueName);
			a_objGraph.addNode(t_objNode);
			a_objGraph.addEdge(a_objParent, t_objNode, a_objResidue.m_objEdge);
			for (Iterator<BcsdbSubTree3> t_iterChilds = a_objResidue.m_aSubresidue.iterator(); t_iterChilds.hasNext();) 
			{
				this.addSugarResidue(t_iterChilds.next(),t_objNode,t_objGraph);				
			}
		}
	}

	private void addRepeatResidue(BcsdbSubTree3 a_objResidue, GlycoNode a_objParent, SugarUnitRepeat a_objGraph ) throws GlycoconjugateException, SugarImporterException 
	{
		SugarUnitRepeat t_objGraph = a_objGraph;
		UnvalidatedGlycoNode t_objNode = new UnvalidatedGlycoNode();
		String t_strResidueName = a_objResidue.m_strResidue;
		String[] t_aStrings = t_strResidueName.split("%");
		a_objResidue.m_objNode = t_objNode;
		if ( t_aStrings.length == 2 )
		{
			UnderdeterminedSubTree t_objUND = new UnderdeterminedSubTree();
			t_objUND.setConnection(a_objResidue.m_objEdge);
			
			t_objNode.setName(t_aStrings[1]);
			t_objUND.addNode(t_objNode);
			
			t_objGraph.addUndeterminedSubTree(t_objUND);
			t_objGraph.addUndeterminedSubTreeParent(t_objUND, a_objParent);
			
			double t_dProb = UnderdeterminedSubTree.UNKNOWN;
			if ( !t_aStrings[0].equals("") )
			{
				try 
				{
					t_dProb = Double.parseDouble(t_aStrings[0]);
				} 
				catch (Exception e) 
				{
					throw new SugarImporterException("BCSDB027", this.m_iPosition);
				}
			}

			t_objUND.setProbability(t_dProb);
			for (Iterator<BcsdbSubTree3> t_iterChilds = a_objResidue.m_aSubresidue.iterator(); t_iterChilds.hasNext();) 
			{
				this.addUndResidue(t_iterChilds.next(),t_objNode,t_objUND,t_dProb);				
			}
		}
		else
		{
			t_objNode.setName(t_strResidueName);
			a_objGraph.addNode(t_objNode);
			a_objGraph.addEdge(a_objParent, t_objNode, a_objResidue.m_objEdge);
			for (Iterator<BcsdbSubTree3> t_iterChilds = a_objResidue.m_aSubresidue.iterator(); t_iterChilds.hasNext();) 
			{
				this.addRepeatResidue(t_iterChilds.next(),t_objNode,t_objGraph);				
			}
		}
	}

	private void addUndResidue(BcsdbSubTree3 a_objResidue, GlycoNode a_objParent, UnderdeterminedSubTree a_objGraph, double a_dProb ) throws GlycoconjugateException, SugarImporterException 
	{
		UnderdeterminedSubTree t_objGraph = a_objGraph;
		UnvalidatedGlycoNode t_objNode = new UnvalidatedGlycoNode();
		String t_strResidueName = a_objResidue.m_strResidue;
		String[] t_aStrings = t_strResidueName.split("%");
		a_objResidue.m_objNode = t_objNode;
		if ( t_aStrings.length == 2 )
		{
			throw new SugarImporterException("BCSDB026", this.m_iPosition);
		}
		t_objNode.setName(t_strResidueName);
		a_objGraph.addNode(t_objNode);
		a_objGraph.addEdge(a_objParent, t_objNode, a_objResidue.m_objEdge);
		for (Iterator<BcsdbSubTree3> t_iterChilds = a_objResidue.m_aSubresidue.iterator(); t_iterChilds.hasNext();) 
		{
			this.addUndResidue(t_iterChilds.next(),t_objNode,t_objGraph,a_dProb);				
		}
	}
	/**
     * start       ::= "-" [ "P" "-" ] <linkage_redu> ")" <rsugarchain> "(" <linkage_nonredu> "-" [ "P" "-" ] 
     *                  | "P" "-" ")" [ <sidechain> ] <sugarchain>
     *                  | <sugarchain> 
     * @throws GlycoconjugateException 
       * @see de.glycosciences.glycoconjugate.io.SugarImporterText#start()
     */
    protected BcsdbSubTree3 startparsing() throws SugarImporterException, GlycoconjugateException
    {
    	BcsdbSubTree3 t_objTreeOld = null;
        if ( this.m_cToken == '-' )
        {
            this.m_objInternalLinkage = new Linkage();
            Linkage t_objLinkage = null;
            BcsdbSubTree3 t_objTreeNew = null;
            // Repeatsugar
            // "-" { "P" "-" } <linkage_redu> ")" <rsugarchain> "(" <linkage_nonredu> "-" [ "P" "-" ]
            this.nextToken();
            if ( this.isP() )
            {
            	t_objTreeOld = new BcsdbSubTree3();
            	this.m_objRepeatEnd = t_objTreeOld;
            	t_objTreeOld.m_strResidue = this.getP();
                if ( this.m_cToken != '-' )
        		{
                	throw new SugarImporterException("BCSDB010", this.m_iPosition);
        		}
                this.nextToken();
                this.m_objInternalLinkage.addParentLinkage(1);
                t_objLinkage = new Linkage();
                t_objLinkage.addChildLinkage(1);
                GlycoEdge t_objEdge = new GlycoEdge();
                t_objEdge.addGlycosidicLinkage(t_objLinkage);
                t_objTreeOld.m_objEdge = t_objEdge;
            }
            while ( this.isP() )
            {
            	t_objTreeNew = new BcsdbSubTree3();
            	t_objTreeNew.m_strResidue = this.getP();
                if ( this.m_cToken != '-' )
        		{
                	throw new SugarImporterException("BCSDB010", this.m_iPosition);
        		}
                this.nextToken();
                t_objLinkage = new Linkage();
                t_objLinkage.addChildLinkage(1);
                GlycoEdge t_objEdge = new GlycoEdge();
                t_objEdge.addGlycosidicLinkage(t_objLinkage);
                t_objTreeNew.m_objEdge = t_objEdge;
                // connection to first tree
                for (Iterator<Linkage> t_iterLinkage = t_objTreeOld.m_objEdge.getGlycosidicLinkages().iterator(); t_iterLinkage.hasNext();) 
                {
					t_iterLinkage.next().addParentLinkage(1);					
				}
                t_objTreeNew.m_aSubresidue.add(t_objTreeOld);
                t_objTreeOld = t_objTreeNew;
            }
            int t_iPos = this.linkage_redu(); 
            if ( t_objTreeOld == null )
            {
                this.m_objInternalLinkage.addParentLinkage(t_iPos);
            }
            else
            {
                t_objLinkage.addParentLinkage(t_iPos);
            }
            if ( this.m_cToken != ')' )
            {
                throw new SugarImporterException("BCSDB001", this.m_iPosition);
            }
            this.nextToken();
            t_objTreeOld = this.rsugarchain(t_objTreeOld);
            if ( this.m_cToken != '(' )
            {
                throw new SugarImporterException("BCSDB002", this.m_iPosition);
            }
            this.nextToken();
            t_iPos = this.linkage_nonredu();
            if ( this.m_cToken != '-' )
            {
                throw new SugarImporterException("BCSDB003", this.m_iPosition);
            }
            this.nextToken();
            if ( this.isP() )
            {
            	t_objTreeNew = new BcsdbSubTree3();
            	t_objTreeNew.m_strResidue = this.getP();
                if ( this.m_cToken != '-' )
        		{
                	throw new SugarImporterException("BCSDB010", this.m_iPosition);
        		}
                this.nextToken();
                t_objLinkage = new Linkage();
                t_objLinkage.addParentLinkage(1);
                t_objLinkage.addChildLinkage(t_iPos);
                GlycoEdge t_objEdge = new GlycoEdge();
                t_objEdge.addGlycosidicLinkage(t_objLinkage);
                t_objTreeOld.m_objEdge = t_objEdge; 
                t_objTreeNew.m_aSubresidue.add(t_objTreeOld);
                t_objTreeOld = t_objTreeNew;
                while ( this.isP() )
                {
                	t_objTreeNew = new BcsdbSubTree3();
                	t_objTreeNew.m_strResidue = this.getP();
                    if ( this.m_cToken != '-' )
            		{
                    	throw new SugarImporterException("BCSDB010", this.m_iPosition);
            		}
                    this.nextToken();
                    t_objLinkage = new Linkage();
                    t_objLinkage.addChildLinkage(1);
                    t_objLinkage.addParentLinkage(1);
                    t_objEdge = new GlycoEdge();
                    t_objEdge.addGlycosidicLinkage(t_objLinkage);
                    t_objTreeOld.m_objEdge = t_objEdge;
                    t_objTreeNew.m_aSubresidue.add(t_objTreeOld);
                    t_objTreeOld = t_objTreeNew;
                }
                // finish internal repeat
                this.m_objInternalLinkage.addChildLinkage(1);
            }
            else
            {
                this.m_objInternalLinkage.addChildLinkage(t_iPos);
            }            
        }
        else if ( this.isP() )
        {
        	BcsdbSubTree3 t_objTreeNew = null;
            // "P" "-" ")" [ <sidechain> ] <sugarchain>
        	t_objTreeOld = new BcsdbSubTree3();
        	t_objTreeOld.m_strResidue = this.getP();
            if ( this.m_cToken != '-' )
    		{
            	throw new SugarImporterException("BCSDB010", this.m_iPosition);
    		}
            this.nextToken();
            Linkage t_objLinkage = new Linkage();
            t_objLinkage.addChildLinkage(1);
            GlycoEdge t_objEdge = new GlycoEdge();
            t_objEdge.addGlycosidicLinkage(t_objLinkage);
            t_objTreeOld.m_objEdge = t_objEdge;
            while ( this.isP() )
            {
            	t_objTreeNew = new BcsdbSubTree3();
            	t_objTreeNew.m_strResidue = this.getP();
                if ( this.m_cToken != '-' )
        		{
                	throw new SugarImporterException("BCSDB010", this.m_iPosition);
        		}
                this.nextToken();
                t_objLinkage = new Linkage();
                t_objLinkage.addChildLinkage(1);
                t_objEdge = new GlycoEdge();
                t_objEdge.addGlycosidicLinkage(t_objLinkage);
                t_objTreeNew.m_objEdge = t_objEdge;
                // connection to first tree
                for (Iterator<Linkage> t_iterLinkage = t_objTreeOld.m_objEdge.getGlycosidicLinkages().iterator(); t_iterLinkage.hasNext();) 
                {
					t_iterLinkage.next().addParentLinkage(1);					
				}
                t_objTreeNew.m_aSubresidue.add(t_objTreeOld);
                t_objTreeOld = t_objTreeNew;
            }
            int t_iLink = this.linkage_redu();
            if ( this.m_cToken != ')' )
            {
                throw new SugarImporterException("BCSDB007", this.m_iPosition);
            }
            this.nextToken();
            // subtrees speichern
            ArrayList<BcsdbSubTree3> t_aSubtree= new ArrayList<BcsdbSubTree3>();
            t_objLinkage.addParentLinkage(t_iLink);
            // add residue
            t_aSubtree.add(t_objTreeOld);
            if ( this.m_cToken == '[' )
            {
                // parse sidechain
                this.sidechain(t_aSubtree); 
            }
            t_objTreeOld = this.sugarchain( t_aSubtree );
        }
        else
        {
            // Nonrepeatsugar
            // <sugarchain>
            t_objTreeOld = this.sugarchain( new ArrayList<BcsdbSubTree3>() );
        }
        if ( ! this.finished() )
        {
            throw new SugarImporterException("BCSDB004", this.m_iPosition);
        }
        return t_objTreeOld;
    }

    /**
     * rsugarchain ::= [ <sidechain> ] <residue> { <linkages> [ "[" <sidechain> "]" ] <residue> }  <== spezielles zusatzkriterium ;
     * 
     * @param a_objSubLinkage sublinkage, can be null
     * @return letztes Residuem
     * @throws GlycoconjugateException 
     */
    private BcsdbSubTree3 rsugarchain(BcsdbSubTree3 a_objSubTree) throws SugarImporterException, GlycoconjugateException
    {
        BcsdbSubTree3 t_objResidue = new BcsdbSubTree3();
        ArrayList<BcsdbSubTree3> t_aSubtrees = new ArrayList<BcsdbSubTree3>();
        if ( a_objSubTree != null )
        {
            t_aSubtrees.add(a_objSubTree);
        }
        if ( this.m_cToken == '[' )
        {
            this.sidechain(t_aSubtrees);
        }
        int t_iStartPosition = this.m_iPosition;
        // save Residuename in Monosaccharid Object
        this.residue();
        t_objResidue.m_strResidue = this.m_strText.substring( t_iStartPosition , this.m_iPosition );
        // repeat startpunkt
        if ( this.m_objRepeatEnd == null )
        {
        	this.m_objRepeatEnd = t_objResidue;
        }       
        // add subresidues
        t_objResidue.m_aSubresidue = t_aSubtrees;
        while ( this.m_cToken == '(' && this.m_strText.indexOf(')',this.m_iPosition) != -1 )
        {
            // there is one or more residues left to parse
            t_aSubtrees = new ArrayList<BcsdbSubTree3>();
            // parse
            t_objResidue = this.linkage(t_objResidue);
            t_aSubtrees.add(t_objResidue);
            if ( this.m_cToken == '[' )
            {
                this.sidechain(t_aSubtrees);
            }
            t_objResidue = new BcsdbSubTree3();
            t_iStartPosition = this.m_iPosition;
            this.residue();
            t_objResidue.m_strResidue = this.m_strText.substring( t_iStartPosition , this.m_iPosition );
            t_objResidue.m_aSubresidue = t_aSubtrees;
        }
        return t_objResidue;
    }

    /**
     * sugarchain  ::= <residue> { <linkage> [ <sidechain> ] <residue> } [ "(" <linkage_nonredu> "-" <residue> ] <== spezielles zusatzkriterium ;
     * @param a_aSubLinkages    All linkages "below" this residue  
     * @throws GlycoconjugateException 
     */
    private BcsdbSubTree3 sugarchain(ArrayList<BcsdbSubTree3> a_aSubLinkages) throws SugarImporterException, GlycoconjugateException
    {
    	BcsdbSubTree3 t_objResidue = new BcsdbSubTree3();
    	int t_iStartPosition = this.m_iPosition;
        // save Residuename in Monosaccharid Object
        this.residue();
        t_objResidue.m_strResidue = this.m_strText.substring( t_iStartPosition , this.m_iPosition );
        t_objResidue.m_aSubresidue = a_aSubLinkages;
        // { <linkage> [ <sidechain> ] <residue> } [ "(" <linkage_nonredu> "-" <residue> ] <== spezielles zusatzkriterium ;
        while ( this.m_cToken == '(' && this.m_strText.indexOf(')',this.m_iPosition) != -1 )
        {
            ArrayList<BcsdbSubTree3> t_aSubtrees = new ArrayList<BcsdbSubTree3>();
            // <linkage> [ <sidechain> ] <residue>
            t_objResidue = this.linkage(t_objResidue);
            t_aSubtrees.add(t_objResidue);
            if ( this.m_cToken == '[' )
            {
                this.sidechain( t_aSubtrees ); 
            }
            // parse residue
            t_iStartPosition = this.m_iPosition;
            this.residue();
            t_objResidue = new BcsdbSubTree3();
            t_objResidue.m_strResidue = this.m_strText.substring( t_iStartPosition , this.m_iPosition );
            t_objResidue.m_aSubresidue = t_aSubtrees;
        }        
        // [ "(" <linkage_nonredu> "-" <residue> ]
        if ( this.m_cToken == '(' )
        {
            this.nextToken();
            int t_iPosition = this.linkage_nonredu();
            if ( this.m_cToken != '-' )
            {
                throw new SugarImporterException("BCSDB006", this.m_iPosition);
            }
            while ( this.m_cToken == '-' )
            {
                this.nextToken();
                t_iStartPosition = this.m_iPosition;

                ArrayList<BcsdbSubTree3> t_aSubtrees = new ArrayList<BcsdbSubTree3>();
            	t_aSubtrees.add(t_objResidue);
            	
            	Linkage t_objEndLinkage = new Linkage();
                t_objEndLinkage.addChildLinkage(t_iPosition);
                t_objEndLinkage.addParentLinkage(1);
                t_iPosition = 1;
                GlycoEdge t_objEndEdge = new GlycoEdge();
                t_objEndEdge.addGlycosidicLinkage(t_objEndLinkage);
                t_objResidue.m_objEdge = t_objEndEdge;
                
                t_objResidue = new BcsdbSubTree3();
            	t_objResidue.m_aSubresidue = t_aSubtrees;
                this.residue();
                t_objResidue.m_strResidue =  this.m_strText.substring( t_iStartPosition , this.m_iPosition );                
            }
        }
        return t_objResidue;
    }

    /**
     * linkage      ::= "(" <linkage_nonredu> "-" { "P" "-" } <linkage_redu> ")"
     * 
     * Bastelt das übergebene Residue in die Linkage ein und gibt diese dann zurück. Residuem wird NICHT in den Zucker eingefügt.
     * 
     * @throws SugarImporterException 
     * @throws GlycoconjugateException 
     */
    private BcsdbSubTree3 linkage(BcsdbSubTree3 a_objChild) throws SugarImporterException, GlycoconjugateException
    {
    	boolean t_bP = false;
        Linkage t_objLinkage = new Linkage();
        GlycoEdge t_objEdge = new GlycoEdge();
        BcsdbSubTree3 t_objResidue = a_objChild;
        if ( this.m_cToken != '(' )
        {
            throw new SugarImporterException("BCSDB005", this.m_iPosition);
        }
        this.nextToken();
        int t_iPosi = this.linkage_nonredu();
        if ( t_iPosi == 0 )
        {
        	t_iPosi = 1;
        }
        // fill data in
        t_objLinkage.addChildLinkage(t_iPosi);
        if ( this.m_cToken != '-' )
        {
            throw new SugarImporterException("BCSDB006", this.m_iPosition);
        }
        this.nextToken();
        while ( this.isP() )
        {
        	t_bP = true;
        	BcsdbSubTree3 t_objResidueP = new BcsdbSubTree3();
            t_objResidueP.m_strResidue = this.getP();

            t_objLinkage.addParentLinkage(1);
            t_objEdge.addGlycosidicLinkage(t_objLinkage);
            t_objResidue.m_objEdge = t_objEdge;
            t_objEdge = new GlycoEdge();
            t_objLinkage = new Linkage();
            t_objLinkage.addChildLinkage(1);
            
            t_objResidueP.m_aSubresidue.add(t_objResidue);
            if (this.m_cToken != '-' )
            {
                throw new SugarImporterException("BCSDB010", this.m_iPosition);
            }
            this.nextToken();
            t_objResidue = t_objResidueP;
        }
        t_iPosi = this.linkage_redu();
        if ( t_iPosi == 0 )
        {
        	t_iPosi = 1;
        }
        t_objLinkage.addParentLinkage(t_iPosi);
        t_objEdge.addGlycosidicLinkage(t_objLinkage);
        
        if ( this.m_cToken == ':' )
        {
        	this.nextToken();
        	if ( t_bP )
        	{
        		throw new SugarImporterException("BCSDB023", this.m_iPosition);
        	}
        	t_objLinkage = new Linkage();
            t_iPosi = this.linkage_nonredu();
            if ( t_iPosi == 0 )
            {
            	t_iPosi = 1;
            }
            // fill data in
            t_objLinkage.addChildLinkage(t_iPosi);
            if ( this.m_cToken != '-' )
            {
                throw new SugarImporterException("BCSDB006", this.m_iPosition);
            }
            this.nextToken();
            t_iPosi = this.linkage_redu();
            if ( t_iPosi == 0 )
            {
            	t_iPosi = 1;
            }
            t_objLinkage.addParentLinkage(t_iPosi);
            t_objEdge.addGlycosidicLinkage(t_objLinkage);        	
        }
        
        if ( this.m_cToken != ')' )
        {
            throw new SugarImporterException("BCSDB007", this.m_iPosition);
        }
        this.nextToken();       
        
        t_objResidue.m_objEdge = t_objEdge;
        return t_objResidue;
    }

    /**
	 * @return
	 */
	private String getP() throws SugarImporterException 
	{
		int t_iStartPosition = this.m_iPosition;
		int t_iDigit = (int)this.m_cToken;
		boolean t_bDigit = false;
        while ( ( t_iDigit > 47 && t_iDigit < 58 ) || this.m_cToken == '?' )
        {
        	this.nextToken();            
        	t_iDigit = (int)this.m_cToken;
        	t_bDigit = true;
        }
        if ( this.m_cToken == '%' )
        {
        	this.nextToken();
        }
        else
        {
        	if ( t_bDigit )
        	{
        		throw new SugarImporterException("BCSDB022", this.m_iPosition);
        	}
        }
        if ( this.m_cToken != 'P' && this.m_cToken != 'S' )
		{
        	throw new SugarImporterException("BCSDB012", this.m_iPosition);
		}
        this.nextToken();
        return this.m_strText.substring( t_iStartPosition , this.m_iPosition );
	}

	/**
	 * @return
     * @throws SugarImporterException 
	 */
	private boolean isP() throws SugarImporterException 
	{
		int t_iPos = 0;
		char t_cToken = this.aheadToken(t_iPos);
		int t_iDigit = (int)t_cToken;
		boolean t_bDigit = false;
        while ( ( t_iDigit > 47 && t_iDigit < 58 ) || t_cToken == '?' )
        {
        	t_iPos++;
        	t_cToken = this.aheadToken(t_iPos);
            t_iDigit = (int)t_cToken;
            t_bDigit = true;
        }
        if ( t_cToken == '%' )
        {
        	t_iPos++;
        	t_cToken = this.aheadToken(t_iPos);
        	if ( t_cToken == 'P' || t_cToken == 'S' )
    		{
        		t_iPos++;
            	t_cToken = this.aheadToken(t_iPos);
            	if ( t_cToken == '-' || t_cToken == '$' )
            	{
            		return true;
            	}
    			return false;
    		}
        }
        else
        {
        	if ( t_bDigit )
        	{
        		return false;
        	}
        }
		if ( t_cToken == 'P' || t_cToken == 'S' )
		{
			t_iPos++;
        	t_cToken = this.aheadToken(t_iPos);
        	if ( t_cToken == '-' || t_cToken == '$' )
        	{
        		return true;
        	}			
		}
		return false;
	}

	/**
     * @throws SugarImporterException 
     * 
     */
    private int linkage_nonredu() throws SugarImporterException
    {
        if ( this.m_cToken == '?' )
        {
            this.nextToken();
            return Linkage.UNKNOWN_POSITION;
        }
        else
        {
            return this.number();            
        }
    }

    /**
     * linkage_redu     ::= "?" | <number>
     * @throws SugarImporterException 
     */
    private int linkage_redu() throws SugarImporterException
    {
        if ( this.m_cToken == '?' )
        {
            this.nextToken();
            return Linkage.UNKNOWN_POSITION;
        }
        else
        {
            return this.number();            
        }
    }

    /**
     * residue          ::= <character> | '?'  { <character> | <number> | '?' | ',' | '|' | '-' | '@' | '{' | '}' |'=' }
     * @throws SugarImporterException 
     */
    private void residue() throws SugarImporterException
    {
        int t_iDigit = (int) this.m_cToken;;
        if ( this.m_cToken == '?' )
        {
            this.nextToken();
        }
        else if ( t_iDigit > 47 && t_iDigit < 58 )
        {
            this.number();
        }
        else if ( this.m_cToken == '%' )
        {
        	this.nextToken();
        }
        else
        {
            this.character();
        }
        while ( this.m_cToken != '$' && this.m_cToken != '(' && this.m_cToken != ']' )
        {
            t_iDigit = (int) this.m_cToken;
            if ( t_iDigit > 47 && t_iDigit < 58 )
            {
                this.number();
            }
            else if ( this.m_cToken == '?' )
            {
                this.nextToken();
            } 
            else if ( this.m_cToken == '|' )
            {
                this.nextToken();
            }
            else if ( this.m_cToken == ',' )
            {
                this.nextToken();
            }               
            else if ( this.m_cToken == '-')
            {
                this.nextToken();
            }
            else if ( this.m_cToken == '@')
            {
                this.nextToken();
            }
            else if ( this.m_cToken == '=')
            {
                this.nextToken();
            }
            else if ( this.m_cToken == '%')
            {
                this.nextToken();
            }
            else if ( this.m_cToken == '{')
            {
                this.nextToken();
            }
            else if ( this.m_cToken == '}')
            {
                this.nextToken();
            }
            else
            {
                this.character();
            }
        }
    }
    
    /**
     * muss was global ablegen um multi-valent residues zu vervollständigen 
     * 
     * sidechain        ::= "[" <side_residue> [ <sidechain_follow> ] "]"
     * 
     * @param a_aSubTrees 
     * 
     * @throws GlycoconjugateException 
     */
    private void sidechain(ArrayList<BcsdbSubTree3> a_aSubTrees) throws SugarImporterException, GlycoconjugateException
    {
        BcsdbSubTree3 t_objSubTree = null;
        if ( this.m_cToken != '[' )
        {
            throw new SugarImporterException("BCSDB009", this.m_iPosition);
        }
        this.nextToken();
        t_objSubTree = this.side_residue();
        if( this.m_cToken != ']' )
        {
            this.sidechain_follow(a_aSubTrees,t_objSubTree);
        }
        else
        {
        	a_aSubTrees.add(t_objSubTree);
        }
        if ( this.m_cToken != ']' )
        {
            throw new SugarImporterException("BCSDB008", this.m_iPosition);
        }
        this.nextToken();
    }

    /**
     * 
     * wenn multi = true dann wird das residue nicht im zucker gesetzt
     * 
     * side_residue     ::= <residue> <linkage> | "P" "-" <linkage_redu> ")"  | "S" "-" <linkage_redu> ")"
     * 
     * @throws SugarImporterException 
     * @throws GlycoconjugateException 
     * 
     */
    private BcsdbSubTree3 side_residue() throws SugarImporterException, GlycoconjugateException
    {
        if ( this.isP() )
        {
        	BcsdbSubTree3 t_objResidue = new BcsdbSubTree3();
            // "P" "-" <linkage_redu> ")" 
            t_objResidue.m_strResidue = this.getP();
            if ( this.m_cToken != '-' )
    		{
            	throw new SugarImporterException("BCSDB010", this.m_iPosition);
    		}
            this.nextToken();
            Linkage t_objLinkage = null;
            GlycoEdge t_objEdge = null;
        	while( this.isP() )
            {
        		BcsdbSubTree3 t_objResidue2 = new BcsdbSubTree3();
            	t_objResidue2.m_strResidue = this.getP();
                if ( this.m_cToken != '-' )
        		{
                	throw new SugarImporterException("BCSDB010", this.m_iPosition);
        		}
                this.nextToken();
        		// Edge
                t_objEdge = new GlycoEdge();
                t_objLinkage = new Linkage();
                t_objLinkage.addChildLinkage(1);
                t_objLinkage.addParentLinkage(1);
                t_objEdge.addGlycosidicLinkage(t_objLinkage);
                t_objResidue.m_objEdge = t_objEdge;
                t_objResidue2.m_aSubresidue.add(t_objResidue);
                t_objResidue = t_objResidue2;
            }
            int t_iPos = this.linkage_redu();
            // Edge
            t_objEdge = new GlycoEdge();
            t_objLinkage = new Linkage();
            t_objLinkage.addChildLinkage(1);
            t_objLinkage.addParentLinkage(t_iPos);
            t_objEdge.addGlycosidicLinkage(t_objLinkage);
            if ( this.m_cToken != ')' )
            {
                throw new SugarImporterException("BCSDB007", this.m_iPosition);
            }
            this.nextToken();
            t_objResidue.m_objEdge = t_objEdge;
            return t_objResidue;
        }
        else
        {
        	BcsdbSubTree3 t_objResidue = new BcsdbSubTree3();
            // <residue> <linkage>
            int t_iStartPosition = this.m_iPosition;
            this.residue();
            t_objResidue.m_strResidue = this.m_strText.substring( t_iStartPosition , this.m_iPosition );            
            return this.linkage(t_objResidue);
        }        
    }

    /**
     * sidechain_follow ::= "," <side_residue> [ <sidechain_follow> ] 
     *                  | <sidechain> <residue> <linkage> [ <sidechain_follow> ]
     *                  | <residue> <linkage> [ <sidechain_follow> ]  
     * @param a_objLastLinkage 
     * @param a_aSubLinkages 
     * @throws SugarImporterException 
     * @throws GlycoconjugateException 
     */
    private void sidechain_follow(ArrayList<BcsdbSubTree3> a_aSubTrees, BcsdbSubTree3 a_objCurrentTree) throws SugarImporterException, GlycoconjugateException
    {
        BcsdbSubTree3 t_objResidue = null;
        if ( this.m_cToken == ',' )
        {
            // new Branch
            // "," <side_residue> [ <sidechain_follow> ]
            // add old Linkage to the SubLinkage array
            a_aSubTrees.add(a_objCurrentTree);
            // parsing new branch
            this.nextToken();
            t_objResidue = this.side_residue();
            if ( this.m_cToken != ']' )
            {
                this.sidechain_follow(a_aSubTrees,t_objResidue);
            }
            else
            {
            	a_aSubTrees.add(t_objResidue);
            }
        } 
        else if ( this.m_cToken == '[' )
        {
        	t_objResidue = new BcsdbSubTree3();
            // <sidechain> <residue> <linkage> [ <sidechain_follow> ]
            ArrayList<BcsdbSubTree3> t_aSubLinkages = new ArrayList<BcsdbSubTree3>();
            t_aSubLinkages.add(a_objCurrentTree);
            this.sidechain(t_aSubLinkages);
            int t_iStartPosition = this.m_iPosition;
            this.residue();
            t_objResidue.m_strResidue = this.m_strText.substring( t_iStartPosition , this.m_iPosition );
            // attach all subresidues
            t_objResidue.m_aSubresidue = t_aSubLinkages;            
            t_objResidue = this.linkage(t_objResidue);            
            if ( this.m_cToken != ']' )
            {
                this.sidechain_follow(a_aSubTrees,t_objResidue);
            }
            else
            {
            	a_aSubTrees.add(t_objResidue);
            }
        }
        else 
        {
            // <residue> <linkage> [ <sidechain_follow> ]
            int t_iStartPosition = this.m_iPosition;
            this.residue();
            t_objResidue = new BcsdbSubTree3();
            t_objResidue.m_strResidue = this.m_strText.substring( t_iStartPosition , this.m_iPosition );
            // attach sub residues
            t_objResidue.m_aSubresidue.add(a_objCurrentTree);
            t_objResidue = this.linkage(t_objResidue);
            if ( this.m_cToken != ']' )
            {
                this.sidechain_follow(a_aSubTrees,t_objResidue);
            }
            else
            {
            	a_aSubTrees.add(t_objResidue);
            }
        }
    }
    
    public void setMinRepeatCount(int a_iCount)
    {
    	this.m_iMinRepeatCount = a_iCount;
    }
    
    public void setMaxRepeatCount(int t_iCount)
    {
    	this.m_iMaxRepeatCount = t_iCount;
    }
}
