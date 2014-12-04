package org.eurocarbdb.MolecularFramework.io.glycosuite;

import java.util.ArrayList;
import java.util.List;

import org.eurocarbdb.MolecularFramework.io.SugarImporterException;
import org.eurocarbdb.MolecularFramework.io.SugarImporterText;
import org.eurocarbdb.MolecularFramework.io.iupac.IupacSubTree;
import org.eurocarbdb.MolecularFramework.sugar.GlycoEdge;
import org.eurocarbdb.MolecularFramework.sugar.GlycoconjugateException;
import org.eurocarbdb.MolecularFramework.sugar.Linkage;
import org.eurocarbdb.MolecularFramework.sugar.LinkageType;
import org.eurocarbdb.MolecularFramework.sugar.UnderdeterminedSubTree;
import org.eurocarbdb.MolecularFramework.sugar.UnvalidatedGlycoNode;

public class SugarImporterGlycoSuiteUncertain extends SugarImporterText
{
    private int m_number = 1;
    private UnderdeterminedSubTree m_tree = new UnderdeterminedSubTree();

    protected SugarImporterGlycoSuiteUncertain()
    {
        super();
    }

    protected void start() throws SugarImporterException
    {
        try
        {
            this.clear();
            int t_iDigit = (int) this.m_cToken;
            if ( t_iDigit > 47 && t_iDigit < 58 )
            {
                this.m_number = this.natural_number();
                while ( this.m_cToken == ' ' )
                {
                    this.nextToken();
                }
                if ( this.m_cToken != 'x' )
                {
                    throw new SugarImporterException("Missing x after number.", this.m_iPosition);
                }
                this.nextToken();
                while ( this.m_cToken == ' ' )
                {
                    this.nextToken();
                }
            }
            // residue 
            UnvalidatedGlycoNode t_objNode = this.parseResidue();
            this.m_tree.addNode(t_objNode);
            if ( this.m_cToken == '$')
            {
                return;
            }
            GlycoEdge t_edge = this.parseLinkage(t_objNode);
            List<IupacSubTree> t_children = new ArrayList<IupacSubTree>();
            IupacSubTree t_treeFirst = new IupacSubTree();
            t_treeFirst.setGlycoNode(t_objNode);
            t_treeFirst.setGlycoEdge(t_edge);
            t_children.add(t_treeFirst);
            while ( this.m_cToken != '$' )
            {
                while ( this.m_cToken == '[' )
                {
                    this.nextToken();
                    t_children.add(this.parseSubtree());
                    this.nextToken();
                }
                t_objNode = this.parseResidue();
                this.m_tree.addNode(t_objNode);
                for (IupacSubTree t_tree : t_children)
                {
                    this.m_tree.addEdge(t_objNode, t_tree.getGlycoNode(), t_tree.getGlycoEdge());
                }
                t_edge = this.parseLinkage(t_objNode);
                t_children = new ArrayList<IupacSubTree>();
                IupacSubTree t_tree = new IupacSubTree();
                t_tree.setGlycoNode(t_objNode);
                t_tree.setGlycoEdge(t_edge);
                t_children.add(t_tree);
            }
            for (Linkage t_linkage : t_edge.getGlycosidicLinkages())
            {
                t_linkage.setParentLinkageType(LinkageType.H_AT_OH);
                t_linkage.setChildLinkageType(LinkageType.DEOXY);
            }
            this.m_tree.setConnection(t_edge);
        }
        catch (Exception e)
        {
            throw new SugarImporterException("GLSUITE001",this.m_iPosition);
        }
    }

    private IupacSubTree parseSubtree() throws SugarImporterException, GlycoconjugateException
    {
        UnvalidatedGlycoNode t_objNode = this.parseResidue();
        this.m_tree.addNode(t_objNode);
        GlycoEdge t_edge = this.parseLinkage(t_objNode);
        List<IupacSubTree> t_children = new ArrayList<IupacSubTree>();
        IupacSubTree t_treeFirst = new IupacSubTree();
        t_treeFirst.setGlycoNode(t_objNode);
        t_treeFirst.setGlycoEdge(t_edge);
        t_children.add(t_treeFirst);
        while ( this.m_cToken != '$' )
        {
            while ( this.m_cToken == '[' )
            {
                this.nextToken();
                t_children.add(this.parseSubtree());
                this.nextToken();
            }
            t_objNode = this.parseResidue();
            this.m_tree.addNode(t_objNode);
            for (IupacSubTree t_tree : t_children)
            {
                this.m_tree.addEdge(t_objNode, t_tree.getGlycoNode(), t_tree.getGlycoEdge());
            }
            t_edge = this.parseLinkage(t_objNode);
            t_children = new ArrayList<IupacSubTree>();
            t_treeFirst = new IupacSubTree();
            t_treeFirst.setGlycoNode(t_objNode);
            t_treeFirst.setGlycoEdge(t_edge);
            t_children.add(t_treeFirst);
        }
        return t_treeFirst;
    }

    private GlycoEdge parseLinkage(UnvalidatedGlycoNode t_node) throws SugarImporterException, GlycoconjugateException
    {
        GlycoEdge t_edge = new GlycoEdge();
        Linkage t_linkage = new Linkage();
        if ( this.m_cToken != '(' )
        {
            throw new SugarImporterException("IUPAC000", this.m_iPosition);
        }
        this.nextToken();
        if ( this.m_cToken != '-' )
        {
            t_node.setName( this.anomer() + t_node.getName() );
            // residue "(" anomer linkageposition 
            t_linkage.addChildLinkage(this.linkageposition());
        }
        else
        {
            t_linkage.addChildLinkage(1);
        }
        if ( this.m_cToken == '-' )
        {
            this.nextToken();
        }
        else
        {
            throw new SugarImporterException("IUPAC005", this.m_iPosition);
        }
        t_linkage.addParentLinkage(this.linkageposition());
        t_edge.addGlycosidicLinkage(t_linkage);
        if ( this.m_cToken != ')' )
        {
            throw new SugarImporterException("IUPAC001", this.m_iPosition);
        }
        this.nextToken();
        return t_edge;
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

    private UnvalidatedGlycoNode parseResidue() throws SugarImporterException, GlycoconjugateException
    {
        int t_iStartPosition = this.m_iPosition;
        UnvalidatedGlycoNode t_objNode = new UnvalidatedGlycoNode();
        this.residue();
        String t_strResidueName = this.m_strText.substring( t_iStartPosition , this.m_iPosition );
        t_objNode.setName(t_strResidueName);
        this.m_tree.addNode(t_objNode);
        return t_objNode;
    }

    private void clear() throws GlycoconjugateException
    {
        this.m_tree = new UnderdeterminedSubTree();
        GlycoEdge t_edge = new GlycoEdge();
        Linkage t_linkage = new Linkage();
        t_linkage.setChildLinkageType(LinkageType.DEOXY);
        t_linkage.addChildLinkage(Linkage.UNKNOWN_POSITION);
        t_linkage.addParentLinkage(Linkage.UNKNOWN_POSITION);
        t_linkage.setParentLinkageType(LinkageType.H_AT_OH);
        t_edge.addGlycosidicLinkage(t_linkage);
        this.m_tree.setConnection(t_edge);
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
        if ( this.m_cToken == '?' || this.m_cToken == '-' || this.m_cToken == ',' )
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

    public int getNumber()
    {
        return m_number;
    }

    public void setNumber(int number)
    {
        m_number = number;
    }

    public UnderdeterminedSubTree getTree()
    {
        return m_tree;
    }

    public void setTree(UnderdeterminedSubTree tree)
    {
        m_tree = tree;
    }

}
