package org.eurocarbdb.MolecularFramework.io.ncfg;

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
 * Mana1-6(Mana1-3)Manb1-4GlcNAcb1-4GlcNAcb-Sp12
 * or
 * Galb1-4(6OSO3)Glcb-Sp0
 * 
 *  start           ::= residue { [linkageposition] "-" [linkageposition] { subbranch } residue }
 *  resiude         ::= symbol { symbol }
 *  linkageposition ::=	number 
 *  subbranch       ::= "(" fullresidue { { subbranch } fullresidue } ")"
 *  fullresidue     ::= residue [ linkageposition ] "-" linkageposition  
 *  symbol          ::= character | "[" | number | "]"
 * 
 * Galb4(Fuca3)GlcNAcb2Mana-
 * 
 */
public class SugarImporterNCFG extends SugarImporterText
{
    /**
     * start           ::= residue { [linkageposition] "-" [linkageposition] { subbranch } residue }
     */
    protected void start() throws SugarImporterException 
    {
        try 
        {
            this.clear();
            UnvalidatedGlycoNode t_objResiduumChild = new UnvalidatedGlycoNode();
            UnvalidatedGlycoNode t_objResiduumParent = new UnvalidatedGlycoNode();
            GlycoEdge t_objEdge = new GlycoEdge();
            Linkage t_objLinkage = new Linkage();
            int t_iStartPosition = this.m_iPosition;
            // residue 
            this.residue();
            String t_strResidueName = this.m_strText.substring( t_iStartPosition , this.m_iPosition );
            t_objResiduumChild.setName( t_strResidueName );
            this.m_objSugar.addNode(t_objResiduumChild);
            while ( this.m_cToken != '$' )
            {
                // residue [ linkageposition ]
                if ( this.m_cToken != '-' )
                {
                    t_objLinkage.addChildLinkage(this.linkageposition());
                }
                else
                {
                    t_objLinkage.addChildLinkage(Linkage.UNKNOWN_POSITION);
                }
                if ( this.m_cToken == '-' )
                {
                    this.nextToken();
                }
                else
                {
                    throw new SugarImporterException("IUPAC005", this.m_iPosition);
                }
                if ( this.isNumber() )
                {
                    t_objLinkage.addParentLinkage(this.linkageposition());
                }
                else
                {
                    t_objLinkage.addParentLinkage(Linkage.UNKNOWN_POSITION);
                }
                t_objEdge.addGlycosidicLinkage(t_objLinkage);
                ArrayList<IupacSubTree> t_aIupacSubtree = new ArrayList<IupacSubTree>();
                while ( this.m_cToken == '(' )
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
                t_objEdge = new GlycoEdge();
                t_objLinkage = new Linkage();
            }
            if ( !this.finished() )
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
     * subbranch	::= "(" fullresidue { { subbranch } fullresidue } ")"
     * @throws SugarImporterException 
     * @throws GlycoconjugateException 
     */
    private IupacSubTree subbranch() throws SugarImporterException, GlycoconjugateException
    {
        IupacSubTree t_objTreeChild;
        IupacSubTree t_objTreeParent;
        if ( this.m_cToken != '(' )
        {
            throw new SugarImporterException("IUPAC000", this.m_iPosition);
        }
        this.nextToken();
        t_objTreeChild = this.fullresidue();
        this.m_objSugar.addNode(t_objTreeChild.getGlycoNode());
        while ( this.m_cToken != ')' )
        {
            ArrayList<IupacSubTree> t_aIupacSubtree = new ArrayList<IupacSubTree>();
            while ( this.m_cToken == '(' )
            {
                t_aIupacSubtree.add(this.subbranch());				
            }
            t_objTreeParent = this.fullresidue();
            this.m_objSugar.addNode(t_objTreeChild.getGlycoNode());
            this.m_objSugar.addEdge(t_objTreeParent.getGlycoNode(), t_objTreeChild.getGlycoNode(), t_objTreeChild.getGlycoEdge());
            // add subtrees
            for (Iterator<IupacSubTree> t_iterSubtree = t_aIupacSubtree.iterator(); t_iterSubtree.hasNext();) 
            {
                IupacSubTree t_objTree = t_iterSubtree.next();
                this.m_objSugar.addEdge(t_objTreeParent.getGlycoNode(), t_objTree.getGlycoNode(), t_objTree.getGlycoEdge());
            }
            t_objTreeChild = t_objTreeParent;
        }
        this.nextToken();
        return t_objTreeChild;
    }

    /**
     * fullresidue ::= residue [ linkageposition ] "-" linkageposition  
     * @throws SugarImporterException 
     * @throws GlycoconjugateException 
     */
    private IupacSubTree fullresidue() throws SugarImporterException, GlycoconjugateException 
    {
        IupacSubTree t_objTree = new IupacSubTree();
        UnvalidatedGlycoNode t_objNode = new UnvalidatedGlycoNode();
        GlycoEdge t_objEdge = new GlycoEdge();
        Linkage t_objLinkage = new Linkage();
        int t_iStartPosition = this.m_iPosition;
        // residue 
        this.residue();
        String t_strResidueName = this.m_strText.substring( t_iStartPosition , this.m_iPosition );
        t_objNode.setName( t_strResidueName );
        this.m_objSugar.addNode(t_objNode);
        // residue [ linkageposition ]
        if ( this.m_cToken != '-' )
        {
            t_objLinkage.addChildLinkage(this.linkageposition());
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
        t_objLinkage.addParentLinkage(this.linkageposition());
        t_objEdge.addGlycosidicLinkage(t_objLinkage);
        t_objTree.setGlycoEdge(t_objEdge);
        t_objTree.setGlycoNode(t_objNode);
        return t_objTree;
    }

    /**
     * linkageposition ::=	number | "?"
     * @throws SugarImporterException 
     */
    private int linkageposition() throws SugarImporterException 
    {
        return this.number();
    }

    /**
     * resiude		::= symbol { symbol }
     * @throws SugarImporterException 
     */
    private void residue() throws SugarImporterException 
    {
        boolean t_bLoop = true;
        do 
        {
            this.symbol();
            if ( this.m_cToken == '-' )
            {
                t_bLoop = false;
            }
            else if ( this.m_cToken == '$' )
            {
                t_bLoop = false;
            }
            else
            {
                boolean t_bNumber = true;
                int t_iCounter = 0;
                do
                {
                    if ( this.m_iLength > this.m_iPosition + t_iCounter )
                    {
                        int t_iDigit = (int) this.m_strText.charAt( this.m_iPosition + t_iCounter );
                        if ( t_iDigit > 47 && t_iDigit < 58 )
                        {
                            t_bNumber = true;
                        }
                        else if ( this.m_strText.charAt( this.m_iPosition + t_iCounter ) == '-' )
                        {
                            t_bNumber = false;
                            t_bLoop = false;
                        }
                        else
                        {
                            t_bNumber = false;
                        }
                    }
                    else
                    {
                        t_bNumber = false;
                    }
                    t_iCounter++;
                } while ( t_bNumber );
            }
        } while (t_bLoop);
    }

    private boolean isNumber()
    {
        int t_iDigit = (int) this.m_strText.charAt( this.m_iPosition );
        if ( t_iDigit > 47 && t_iDigit < 58 )
        {
            return true;
        }
        return false;
    }

    /**
     * symbol      ::= character | "[" | number | "]" | ","
     * @throws SugarImporterException 
     */
    private void symbol() throws SugarImporterException 
    {
        if ( this.m_cToken == '[' )
        {
            this.nextToken();
        }
        else if ( this.m_cToken == ']' )
        {
            this.nextToken();
        }
        else if ( this.m_cToken == ',' )
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
//
//    private boolean isSubstitution()
//    {
//        int t_nextMinus = this.m_strText.indexOf("-", this.m_iPosition);
//        int t_nextKlammer = this.m_strText.indexOf(")", this.m_iPosition);
//        if ( t_nextKlammer < t_nextMinus )
//        {
//            return true;
//        }
//        return false;
//    }

    private void clear() 
    {}
}
