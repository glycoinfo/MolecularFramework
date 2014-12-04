package org.eurocarbdb.MolecularFramework.io.glycosuite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eurocarbdb.MolecularFramework.io.SugarImporterException;
import org.eurocarbdb.MolecularFramework.io.SugarImporterText;
import org.eurocarbdb.MolecularFramework.io.iupac.IupacSubTree;
import org.eurocarbdb.MolecularFramework.sugar.GlycoEdge;
import org.eurocarbdb.MolecularFramework.sugar.GlycoNode;
import org.eurocarbdb.MolecularFramework.sugar.GlycoconjugateException;
import org.eurocarbdb.MolecularFramework.sugar.Linkage;
import org.eurocarbdb.MolecularFramework.sugar.LinkageType;
import org.eurocarbdb.MolecularFramework.sugar.Sugar;
import org.eurocarbdb.MolecularFramework.sugar.UnderdeterminedSubTree;
import org.eurocarbdb.MolecularFramework.sugar.UnvalidatedGlycoNode;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorException;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorNodeType;

/**
 * condensed form one line
 *
 *  Gal(b1-4)[Fuc(a1-3)]GlcNAc(b1-2)Man(a1-
 *
 *  sequence    ::= residue { "(" anomer linkageposition "-"  linkageposition ")" { subbranch } residue }
 *  anomer      ::= a | b | ? | o
 *  linkageposition ::= number | "?"
 *  resiude     ::= symbol { symbol }
 *  subbranch   ::= "[" fullresidue { [ subbranch { subbranch } ] fullresidue } "]"
 *  fullresidue ::= residue "(" anomer linkageposition "-" linkageposition ")" 
 *  symbol      ::= character | "?" | "-" | number
 */
public class SugarImporterGlycoSuite extends SugarImporterText
{
    private HashMap<String, RepeatCount> m_repeatCounts = new HashMap<String, RepeatCount>();
    private List<String> m_warnings = new ArrayList<String>();
    private List<String> m_uncertain = new ArrayList<String>();
    
    public Sugar parse(String a_strStream) throws SugarImporterException 
    {
        if ( !this.testParenties() )
        {
            throw new SugarImporterException("GLSUITE003", 0);
        }
        this.clear();
        this.m_objSugar = new Sugar();
        int t_index = a_strStream.indexOf("+");
        if ( t_index != -1 )
        {
            String t_part = a_strStream.substring(t_index);
            this.prepareAnnotation(t_part);
            a_strStream = a_strStream.substring(0,t_index);
        }
        this.m_iPosition = -1;
        // Copie string and add endsymbol
        this.m_strText = a_strStream + '$';
        this.m_iLength = this.m_strText.length();
        // get first token . Error ? ==> string empty
        this.nextToken();
        this.start();
        return this.m_objSugar;
    }
    
    private boolean testParenties()
    {
        int t_count = 0;
        for (int i=0; i < this.m_strText.length(); i++)
        {
            if (this.m_strText.charAt(i) == '[')
            {
                 t_count++;
            }
            if (this.m_strText.charAt(i) == ']')
            {
                 t_count--;
            }
        }
        if ( t_count == 0 )
        {
            return true;
        }
        return false;
    }

    private void prepareAnnotation(String a_part)
    {
        String t_annotation = a_part.substring(a_part.indexOf("\"")+1,a_part.lastIndexOf("\""));
        if ( t_annotation.toLowerCase().startsWith("where") )
        {
            if ( t_annotation.equals("where j=30-40 and k=30-40" ) )
            {
                this.m_repeatCounts.put("j", new RepeatCount(30, 40));
                this.m_repeatCounts.put("k", new RepeatCount(30, 40));
            }
            else if ( t_annotation.equals("where j =20"))
            {
                this.m_repeatCounts.put("j", new RepeatCount(20));
            }
            else
            {
                this.m_warnings.add("Unknown repeat count :" + t_annotation);
            }
        }
        else
        {
            t_annotation = t_annotation.substring(1).trim();
            String[] t_annotationPart = t_annotation.split("\\+");
            for (String t_string : t_annotationPart)
            {
                this.m_uncertain.add(t_string.trim());
            }
        }
    }

    protected void start() throws SugarImporterException
    {
        try 
        {
            this.sequence();
            // parse uncertain
            for (String t_uncertain : this.m_uncertain)
            {
                SugarImporterGlycoSuiteUncertain t_importer = new SugarImporterGlycoSuiteUncertain();
                try
                {
                    t_importer.parse(t_uncertain);
                }
                catch (SugarImporterException e)
                {
                    throw new SugarImporterException("GLSUITE001", e.getPosition());
                }
                for (int i = 0; i < t_importer.getNumber(); i++)
                {
                    UnderdeterminedSubTree t_tree = t_importer.getTree().copy();
                    try
                    {
                        this.modifiyUncertainSubstituent(t_tree);
                    } 
                    catch (GlycoVisitorException e)
                    {
                        throw new SugarImporterException("GLSUITE002", -1);
                    }
                    this.m_objSugar.addUndeterminedSubTree(t_tree);
                    for (GlycoNode t_node : this.m_objSugar.getNodes())
                    {
                        this.m_objSugar.addUndeterminedSubTreeParent(t_tree, t_node);
                    }
                }
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

    private void modifiyUncertainSubstituent(UnderdeterminedSubTree a_tree) throws GlycoVisitorException, GlycoconjugateException
    {
        if ( a_tree.getNodes().size() == 1 )
        {
            for (GlycoNode t_node : a_tree.getNodes())
            {
                GlycoVisitorNodeType t_visitor = new GlycoVisitorNodeType();
                UnvalidatedGlycoNode t_unNode = t_visitor.getUnvalidatedNode(t_node);
                if ( t_unNode.getName().equals("HSO3") || t_unNode.getName().equals("H2PO3") || t_unNode.getName().equals("Acetyl") )
                {
                    GlycoEdge t_edge = a_tree.getConnection();
                    for (Linkage t_linkage : t_edge.getGlycosidicLinkages())
                    {
                        t_linkage.setChildLinkageType(LinkageType.NONMONOSACCHARID);
                        t_linkage.setParentLinkageType(LinkageType.H_AT_OH);
                        ArrayList<Integer> t_int = new ArrayList<Integer>();
                        t_int.add(1);
                        t_linkage.setChildLinkages(t_int);
                    }
                }
            }
        }
    }

    /**
     * residue { "(" anomer linkageposition "-" linkageposition ")" { subbranch } residue } 
     */
    private void sequence() throws SugarImporterException, GlycoconjugateException
    {
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
            t_objResiduumParent = new UnvalidatedGlycoNode();
            t_objEdge = new GlycoEdge();
            t_objLinkage = new Linkage();
            if ( this.m_cToken != '(' )
            {
                throw new SugarImporterException("IUPAC000", this.m_iPosition);
            }
            this.nextToken();
            if ( this.m_cToken != '-' )
            {
                t_objResiduumChild.setName( this.anomer() + t_strResidueName );
                // residue "(" anomer linkageposition 
                t_objLinkage.addChildLinkage(this.linkageposition());
            }
            else
            {
                t_objLinkage.addChildLinkage(1);
            }
            if ( this.m_cToken == '-' )
            {
                this.nextToken();
            }
            else
            {
                throw new SugarImporterException("IUPAC005", this.m_iPosition);
            }
            t_objLinkage.addParentLinkage(this.linkageposition());
            t_objEdge.addGlycosidicLinkage(t_objLinkage);
            if ( this.m_cToken != ')' )
            {
                throw new SugarImporterException("IUPAC001", this.m_iPosition);
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
        }
    }

    /**
     * subbranch    ::= "[" fullresidue { { subbranch } fullresidue } "]"
     * @throws SugarImporterException 
     * @throws GlycoconjugateException 
     */
    private IupacSubTree subbranch() throws SugarImporterException, GlycoconjugateException
    {
        IupacSubTree t_objTreeChild;
        IupacSubTree t_objTreeParent;
        if ( this.m_cToken != '[' )
        {
            throw new SugarImporterException("IUPAC004", this.m_iPosition);
        }
        this.nextToken();
        t_objTreeChild = this.fullresidue();
        this.m_objSugar.addNode(t_objTreeChild.getGlycoNode());
        while ( this.m_cToken != ']' )
        {
            ArrayList<IupacSubTree> t_aIupacSubtree = new ArrayList<IupacSubTree>();
            while ( this.m_cToken == '[' )
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
     * fullresidue ::= residue "(" anomer linkageposition "-" linkageposition ")"
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
        if ( this.m_cToken != '(' )
        {
            throw new SugarImporterException("IUPAC000", this.m_iPosition);
        }
        this.nextToken();
        if ( this.m_cToken != '-' )
        {
            t_objNode.setName( this.anomer() + t_strResidueName );
            this.m_objSugar.addNode(t_objNode);
            // residue "(" anomer linkageposition 
            t_objLinkage.addChildLinkage(this.linkageposition());
        }
        else
        {
            t_objLinkage.addChildLinkage(1);
        }
        if ( this.m_cToken != '-' )
        {
            throw new SugarImporterException("IUPAC005", this.m_iPosition);
        }
        this.nextToken();
        t_objLinkage.addParentLinkage(this.linkageposition());
        if ( this.m_cToken != ')' )
        {
            throw new SugarImporterException("IUPAC001", this.m_iPosition);
        }
        this.nextToken();       
        t_objEdge.addGlycosidicLinkage(t_objLinkage);
        t_objTree.setGlycoEdge(t_objEdge);
        t_objTree.setGlycoNode(t_objNode);
        return t_objTree;
    }

    /**
     * linkageposition ::=  number | "?"
     * @throws SugarImporterException 
     */
    private int linkageposition() throws SugarImporterException 
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
     * anomer       ::= a | b | ? | o
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
     * resiude      ::= symbol { symbol }
     * @throws SugarImporterException 
     */
    private void residue() throws SugarImporterException 
    {
        this.symbol();
        while ( this.m_cToken != '(' && this.m_cToken != '$')
        {
            this.symbol();
        }
    }

    /**
     * symbol       ::= character | "?" | "-" | number
     * @throws SugarImporterException 
     */
    private void symbol() throws SugarImporterException 
    {
        if ( this.m_cToken == '?' || this.m_cToken == '-' || this.m_cToken == ',' || this.m_cToken == ' ')
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
    {
        this.m_warnings = new ArrayList<String>();
        this.m_repeatCounts = new HashMap<String,RepeatCount>();
        this.m_uncertain = new ArrayList<String>();
    }

}
