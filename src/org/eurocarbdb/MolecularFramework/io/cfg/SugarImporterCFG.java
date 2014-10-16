/**
 * 
 */
package org.eurocarbdb.MolecularFramework.io.cfg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.eurocarbdb.MolecularFramework.io.SugarImporterException;
import org.eurocarbdb.MolecularFramework.io.SugarImporterText;
import org.eurocarbdb.MolecularFramework.sugar.GlycoEdge;
import org.eurocarbdb.MolecularFramework.sugar.GlycoGraph;
import org.eurocarbdb.MolecularFramework.sugar.GlycoNode;
import org.eurocarbdb.MolecularFramework.sugar.GlycoconjugateException;
import org.eurocarbdb.MolecularFramework.sugar.Linkage;
import org.eurocarbdb.MolecularFramework.sugar.LinkageType;
import org.eurocarbdb.MolecularFramework.sugar.Sugar;
import org.eurocarbdb.MolecularFramework.sugar.UnderdeterminedSubTree;
import org.eurocarbdb.MolecularFramework.sugar.UnvalidatedGlycoNode;

/**
 * http://www.glycominds.com/index.asp?menu=Research&page=glycoit#
 * 
 * @author Logan
 *
 */
public class SugarImporterCFG extends SugarImporterText
{
    private HashMap<Integer,CFGUnderdeterminedTree> m_hashSubtrees = new HashMap<Integer,CFGUnderdeterminedTree>(); 
    /* (non-Javadoc)
     * @see org.eurocarbdb.MolecularFramework.io.SugarImporterText#start()
     */
    @Override
    protected void start() throws SugarImporterException 
    {
        this.m_hashSubtrees.clear();
        // split up uncertain terminal definitions
        String[] t_aStrings = this.m_strText.split("%\\|");
        int t_iMax = t_aStrings.length - 1;
        for (int t_iCounter = 0; t_iCounter < t_iMax; t_iCounter++) 
        {
            // store uncertain terminal residue
            this.m_strText = t_aStrings[t_iCounter]+ "$";
            this.m_iPosition = -1;
            this.m_iLength = this.m_strText.length();
            this.nextToken();
            CFGUnderdeterminedTree t_objTree = this.parseUnderdetermined();
            this.m_hashSubtrees.put(t_objTree.getId(),t_objTree);
            if ( this.m_cToken != '$' )
            {
                throw new SugarImporterException("CFG007",-2);
            }
            try
            {
                this.m_objSugar.addUndeterminedSubTree(t_objTree.getTree());
            }
            catch (Exception t_e)
            {
                throw new SugarImporterException(t_e.getMessage(),t_e);
            }
        }
        this.m_strText = t_aStrings[t_aStrings.length-1];
        int t_iAmper = this.m_strText.indexOf("&");
        if ( t_iAmper == -1 )
        {
            this.m_iPosition = -1;
            this.m_iLength = this.m_strText.length();
            // start parsing
            this.nextToken();
            this.mainchain(this.m_objSugar);
            // finished ?
            if ( this.m_cToken != '$' )
            {
                throw new SugarImporterException("CFG005",-1);
            }
        }
        else
        {
            // UND is not allowed 
            if ( this.m_hashSubtrees.size() != 0 )
            {
                throw new SugarImporterException("CFG013",t_iAmper);
            }
            this.m_iPosition = -1;
            int t_iAmper2 = this.m_strText.indexOf("&",t_iAmper+1);
            if ( t_iAmper2 < t_iAmper )
            {
                throw new SugarImporterException("CFG014",t_iAmper);
            }
            String t_strFrags = this.m_strText.substring(t_iAmper+1,t_iAmper2).trim();
            if ( this.m_strText.charAt(t_iAmper2+1) != '$' )
            {
                throw new SugarImporterException("CFG015",t_iAmper2);
            }
            this.m_strText = this.m_strText.substring(0,t_iAmper) + "$";
            this.m_iLength = this.m_strText.length();
            // start parsing
            this.nextToken();
            this.mainchain(this.m_objSugar);
            // finished ?
            if ( this.m_cToken != '$' )
            {
                throw new SugarImporterException("CFG005",-1);
            }
            // parse fragments
            t_aStrings = t_strFrags.split(",");
            if ( t_aStrings.length < 2 )
            {
                throw new SugarImporterException("CFG016",t_iAmper2);
            }
            for (int t_iCounter = 0; t_iCounter < t_aStrings.length; t_iCounter++) 
            {
                this.m_strText = t_aStrings[t_iCounter] + "$";
                this.m_iLength = this.m_strText.length();
                this.m_iPosition = -1;
                this.nextToken();
                UnderdeterminedSubTree t_tree = this.fragment();
                try
                {
                    t_tree.setConnection(this.createUnknownEdge());
                    this.m_objSugar.addUndeterminedSubTree(t_tree);
                }
                catch (Exception t_e)
                {
                    throw new SugarImporterException(t_e.getMessage(),t_e);
                }
                // get all residues of the main sugar and add them as parents
                for (GlycoNode t_node : this.m_objSugar.getNodes())
                {
                    try
                    {
                        this.m_objSugar.addUndeterminedSubTreeParent(t_tree, t_node);
                    }
                    catch (Exception t_e)
                    {
                        throw new SugarImporterException(t_e.getMessage(),t_e);
                    }
                }
            }
        }
    }

    private GlycoEdge createUnknownEdge() throws GlycoconjugateException
    {
        GlycoEdge t_edge = new GlycoEdge();
        Linkage t_linkage = new Linkage();
        t_linkage.addChildLinkage(1);
        t_linkage.setChildLinkageType(LinkageType.DEOXY);
        t_linkage.addParentLinkage(Linkage.UNKNOWN_POSITION);
        t_linkage.setParentLinkageType(LinkageType.H_AT_OH);
        t_edge.addGlycosidicLinkage(t_linkage);
        return t_edge;
    }

    private UnderdeterminedSubTree fragment() throws SugarImporterException 
    {
        UnderdeterminedSubTree t_subTree = new UnderdeterminedSubTree();
        try 
        {
            int t_iStartPosition = 0;
            CFGSubTree t_objSubTreeMain = new CFGSubTree();
            ArrayList<CFGSubTree> t_aSubTrees = new ArrayList<CFGSubTree>();
            ArrayList<Integer> t_aPostions;
            UnvalidatedGlycoNode t_objNode;
            t_iStartPosition = this.m_iPosition;
            this.residuename();
            t_objNode = new UnvalidatedGlycoNode();
            t_objNode.setName( this.m_strText.substring( t_iStartPosition , this.m_iPosition ) );
            t_subTree.addNode(t_objNode);
            t_objSubTreeMain.setGlycoNode(t_objNode);
            while ( this.m_cToken != '$' )
            {
                t_aSubTrees.add(t_objSubTreeMain);
                t_aPostions = this.position();
                Linkage t_objLinkage = new Linkage();
                t_objLinkage.addChildLinkage(1);
                t_objLinkage.setParentLinkages(t_aPostions);
                GlycoEdge t_objEdge = new GlycoEdge();
                t_objEdge.addGlycosidicLinkage(t_objLinkage);
                t_objSubTreeMain.setGlycoEdge(t_objEdge);
                while ( this.m_cToken == '(' )
                {
                    this.nextToken();
                    t_aSubTrees.add(this.subbranch(t_subTree));
                    if ( this.m_cToken != ')' )
                    {
                        throw new SugarImporterException("CFG004",this.m_iPosition);
                    }
                    this.nextToken();
                }
                // create parent residue
                t_iStartPosition = this.m_iPosition;
                this.residuename();
                t_objNode= new UnvalidatedGlycoNode();
                t_objNode.setName( this.m_strText.substring( t_iStartPosition , this.m_iPosition ) );
                t_subTree.addNode(t_objNode);
                // add branches
                for (Iterator<CFGSubTree> t_iterSubTrees = t_aSubTrees.iterator(); t_iterSubTrees.hasNext();) 
                {
                    CFGSubTree t_objSubTree = t_iterSubTrees.next();
                    t_subTree.addEdge(t_objNode,t_objSubTree.getGlycoNode(),t_objSubTree.getGlycoEdge());
                }
                t_aSubTrees.clear();
                t_objSubTreeMain.setGlycoNode(t_objNode);
                t_objSubTreeMain.setId(null);
            }
        }
        catch (GlycoconjugateException e) 
        {
            throw new SugarImporterException("COMMON013",this.m_iPosition);
        }
        return t_subTree;
    }

    /**
     * @param subtree
     * @return
     * @throws SugarImporterException 
     * @throws  
     */
    private CFGUnderdeterminedTree parseUnderdetermined() throws SugarImporterException 
    {
        CFGUnderdeterminedTree t_objCFGSubTree = new CFGUnderdeterminedTree();
        UnderdeterminedSubTree t_objSubTree = new UnderdeterminedSubTree();
        // parse 
        CFGSubTree t_objBranch = this.subbranch(t_objSubTree);
        if ( t_objBranch.getId() != null )
        {
            throw new SugarImporterException("CFG012",this.m_iPosition);
        }
        GlycoEdge t_objEdge = t_objBranch.getGlycoEdge();
        for (Iterator<Linkage> t_iterLinkage = t_objEdge.getGlycosidicLinkages().iterator(); t_iterLinkage.hasNext();) 
        {
            Linkage t_objLink = t_iterLinkage.next();
            try 
            {
                t_objLink.setParentLinkageType(LinkageType.H_AT_OH);
                t_objLink.setChildLinkageType(LinkageType.DEOXY);
            } 
            catch (GlycoconjugateException e) 
            {}			
        }
        t_objSubTree.setConnection(t_objEdge);        
        // parse = and number
        if ( this.m_cToken != '=' )
        {
            throw new SugarImporterException("CFG006",this.m_iPosition);
        }
        this.nextToken();
        t_objCFGSubTree.setId(this.number());
        t_objCFGSubTree.setTree(t_objSubTree);
        return t_objCFGSubTree;
    }

    // <residuename> { <position> { <subbranch> } <residuename> }
    private void mainchain(GlycoGraph a_objGraph) throws SugarImporterException 
    {		
        try 
        {
            int t_iStartPosition = 0;
            CFGSubTree t_objSubTreeMain = null;
            ArrayList<CFGSubTree> t_aSubTrees = new ArrayList<CFGSubTree>();
            ArrayList<Integer> t_aPostions;
            UnvalidatedGlycoNode t_objNode;
            int t_iDigit = (int)this.m_cToken;
            if ( t_iDigit > 47 && t_iDigit < 58 )
            {
                while ( t_iDigit > 47 && t_iDigit < 58 )
                {
                    t_objSubTreeMain = new CFGSubTree();
                    // uncertain subtree
                    t_objSubTreeMain.setId(this.number());
                    if ( this.m_cToken != '%' )
                    {
                        throw new SugarImporterException("CFG004",this.m_iPosition);
                    }  
                    this.nextToken();
                    t_aSubTrees.add(t_objSubTreeMain);
                    t_iDigit = (int)this.m_cToken;
                }
            }
            else
            {
                t_objSubTreeMain = new CFGSubTree();
                t_iStartPosition = this.m_iPosition;
                this.residuename();
                t_objNode = new UnvalidatedGlycoNode();
                t_objNode.setName( this.m_strText.substring( t_iStartPosition , this.m_iPosition ) );
                a_objGraph.addNode(t_objNode);
                t_objSubTreeMain.setGlycoNode(t_objNode);
                t_aSubTrees.add(t_objSubTreeMain);
            }
            while ( this.m_cToken != '$' && this.m_cToken != '#' && this.m_cToken != ';' && this.m_cToken != ':' )
            {
                if ( t_objSubTreeMain.getId() == null )
                {
                    t_aPostions = this.position();
                    Linkage t_objLinkage = new Linkage();
                    t_objLinkage.addChildLinkage(1);
                    t_objLinkage.setParentLinkages(t_aPostions);
                    GlycoEdge t_objEdge = new GlycoEdge();
                    t_objEdge.addGlycosidicLinkage(t_objLinkage);
                    t_objSubTreeMain.setGlycoEdge(t_objEdge);
                }
                while ( this.m_cToken == '(' )
                {
                    this.nextToken();
                    t_aSubTrees.add(this.subbranch(a_objGraph));
                    if ( this.m_cToken != ')' )
                    {
                        throw new SugarImporterException("CFG004",this.m_iPosition);
                    }
                    this.nextToken();
                }
                // create parent residue
                t_iStartPosition = this.m_iPosition;
                this.residuename();
                t_objNode= new UnvalidatedGlycoNode();
                t_objNode.setName( this.m_strText.substring( t_iStartPosition , this.m_iPosition ) );
                a_objGraph.addNode(t_objNode);
                // add branches
                for (Iterator<CFGSubTree> t_iterSubTrees = t_aSubTrees.iterator(); t_iterSubTrees.hasNext();) 
                {
                    CFGSubTree t_objSubTree = t_iterSubTrees.next();
                    if ( t_objSubTree.getId() == null )
                    {
                        // normal branch
                        a_objGraph.addEdge(t_objNode,t_objSubTree.getGlycoNode(),t_objSubTree.getGlycoEdge());
                    }
                    else
                    {
                        // uncertain subtree
                        CFGUnderdeterminedTree t_objUTree = this.m_hashSubtrees.get(t_objSubTree.getId());
                        try 
                        {
                            this.addUncertainBranch(a_objGraph,t_objUTree,t_objNode);	
                        } 
                        catch (GlycoconjugateException e) 
                        {
                            throw new SugarImporterException("CFG011",this.m_iPosition);
                        }		                	
                    }
                }
                t_aSubTrees.clear();
                t_objSubTreeMain.setGlycoNode(t_objNode);
                t_objSubTreeMain.setId(null);
                t_aSubTrees.add(t_objSubTreeMain);
            }
            // aglyca ?
            if ( this.m_cToken == '#' || this.m_cToken == ';' || this.m_cToken == ':' )
            {
                t_iStartPosition = this.m_iPosition;
                while ( this.m_cToken != '$' )
                {					
                    this.nextToken();
                }
                t_objNode = new UnvalidatedGlycoNode();
                t_objNode.setName( this.m_strText.substring( t_iStartPosition , this.m_iPosition ) );
                a_objGraph.addNode(t_objNode);
                Linkage t_objLinkage = new Linkage();
                t_objLinkage.addChildLinkage(1);
                t_objLinkage.addParentLinkage(Linkage.UNKNOWN_POSITION);
                GlycoEdge t_objEdge = new GlycoEdge();
                t_objEdge.addGlycosidicLinkage(t_objLinkage);
                a_objGraph.addEdge(t_objNode,t_objSubTreeMain.getGlycoNode(),t_objEdge);
            }
        }
        catch (GlycoconjugateException e) 
        {
            throw new SugarImporterException("COMMON013",this.m_iPosition);
        }
    }

    /**
     * @param graph
     * @return
     * @throws SugarImporterException 
     */
    private ArrayList<Integer> position() throws SugarImporterException 
    {
        ArrayList<Integer> t_aPositions = new ArrayList<Integer>(); 
        if ( this.m_cToken == '?' )
        {
            t_aPositions.add(Linkage.UNKNOWN_POSITION);
            this.nextToken();
        }
        else
        {
            t_aPositions.add(this.number());
            while ( this.m_cToken == '/' )
            {
                this.nextToken();
                t_aPositions.add(this.number());
            }
        }
        return t_aPositions;
    }

    /**
     * @param graph
     * @throws SugarImporterException 
     */
    private void residuename() throws SugarImporterException 	
    {
        if ( this.m_cToken == '?' )
        {
            this.nextToken();
            // modification ?
            if ( this.m_cToken == '[' )
            {
                this.modification();
            }
            // unknown residue
            if ( this.m_cToken == '?' || this.m_cToken == 'a' || this.m_cToken == 'b'  || this.m_cToken == 'o' )
            {
                this.nextToken();
            }
            else
            {
                if ( this.m_cToken != '$' && this.m_cToken != ';' && this.m_cToken != ':' && this.m_cToken != '#' )
                {
                    throw new SugarImporterException("CFG000",this.m_iPosition);
                }
            }
        }
        else
        {
            boolean t_bNameMissing = true;
            while ( (this.m_cToken >= 'A' && this.m_cToken <= 'Z') || this.m_cToken == '\'' || this.m_cToken == '^' || this.m_cToken == '~' )
            {
                this.nextToken();
                t_bNameMissing = false;
            }
            if ( t_bNameMissing )
            {
                throw new SugarImporterException("CFG001",this.m_iPosition);
            }
            if ( this.m_cToken == '[' )
            {
                this.modification();
            }
            if ( this.m_cToken == '?' || this.m_cToken == 'a' || this.m_cToken == 'b' || this.m_cToken == 'o' )
            {
                this.nextToken();
            }
            else
            {
                if ( this.m_cToken != '$' && this.m_cToken != ';' && this.m_cToken != ':' && this.m_cToken != '#' )
                {
                    throw new SugarImporterException("CFG000",this.m_iPosition);
                }
            }
        }
    }

    /**
     * @param node
     * @param graph
     * @throws SugarImporterException 
     */
    private void modification() throws SugarImporterException 
    {
        int t_iDigit;
        if ( this.m_cToken != '[' )
        {
            throw new SugarImporterException("CFG002",this.m_iPosition);
        }
        this.nextToken();
        while ( this.m_cToken != ']' )
        {
            if ( this.m_cToken < 'A' || this.m_cToken > 'Z' )
            {
                if ( this.m_cToken < 'a' || this.m_cToken > 'z' )
                {
                    t_iDigit = (int)this.m_cToken;
                    if ( t_iDigit < 48 && t_iDigit > 57 )
                    {
                        if ( this.m_cToken != '*' )
                        {
                            throw new SugarImporterException("CFG003",this.m_iPosition);
                        }
                    }
                }
            }
            this.nextToken();
        }
        this.nextToken();
    }

    // <residuename> <position> { { <subbranch> } <residuename> <position> }
    private CFGSubTree subbranch(GlycoGraph a_objGraph) throws SugarImporterException 
    {
        CFGSubTree t_objSubTreeMain = null;
        ArrayList<CFGSubTree> t_aSubTrees = new ArrayList<CFGSubTree>();
        UnvalidatedGlycoNode t_objNode;
        ArrayList<Integer> t_aPostions;
        try 
        {
            int t_iDigit = (int)this.m_cToken;
            if ( t_iDigit > 47 && t_iDigit < 58 )
            {
                while ( t_iDigit > 47 && t_iDigit < 58 )
                {
                    t_objSubTreeMain = new CFGSubTree();
                    // uncertain subtree
                    t_objSubTreeMain.setId(this.number());
                    if ( this.m_cToken != '%' )
                    {
                        throw new SugarImporterException("CFG004",this.m_iPosition);
                    }  
                    this.nextToken();
                    t_aSubTrees.add(t_objSubTreeMain);
                    t_iDigit = (int)this.m_cToken;
                }
            }
            else
            {
                t_objSubTreeMain = new CFGSubTree();
                // <residuename> 
                int t_iStartPosition = this.m_iPosition;
                this.residuename();
                t_objNode = new UnvalidatedGlycoNode();
                t_objNode.setName( this.m_strText.substring( t_iStartPosition , this.m_iPosition ) );
                a_objGraph.addNode(t_objNode);
                // <residuename> <position> 
                t_aPostions = this.position();
                Linkage t_objLinkage = new Linkage();
                t_objLinkage.addChildLinkage(1);
                t_objLinkage.setParentLinkages(t_aPostions);
                GlycoEdge t_objEdge = new GlycoEdge();
                t_objEdge.addGlycosidicLinkage(t_objLinkage);
                t_objSubTreeMain.setGlycoEdge(t_objEdge);
                t_objSubTreeMain.setGlycoNode(t_objNode);
                t_aSubTrees.add(t_objSubTreeMain);
            }
            while ( this.m_cToken != ')' && this.m_cToken != '=' )
            {
                // <residuename> <position> {  }
                while ( this.m_cToken == '(' )
                {
                    // <residuename> <position> { { <subbranch> } }
                    this.nextToken();
                    t_aSubTrees.add(this.subbranch(a_objGraph));
                    if ( this.m_cToken != ')' )
                    {
                        throw new SugarImporterException("CFG004",this.m_iPosition);
                    }
                    this.nextToken();
                }
                // <residuename> <position> { { <subbranch> } <residuename> }
                int t_iStartPosition = this.m_iPosition;
                this.residuename();
                t_objNode = new UnvalidatedGlycoNode();
                t_objNode.setName( this.m_strText.substring( t_iStartPosition , this.m_iPosition ) );
                a_objGraph.addNode(t_objNode);
                // add branches
                for (Iterator<CFGSubTree> t_iterSubTrees = t_aSubTrees.iterator(); t_iterSubTrees.hasNext();) 
                {
                    CFGSubTree t_objSubTree = t_iterSubTrees.next();
                    if ( t_objSubTree.getId() == null )
                    {
                        // normal branch
                        a_objGraph.addEdge(t_objNode,t_objSubTree.getGlycoNode(),t_objSubTree.getGlycoEdge());
                    }
                    else
                    {
                        // uncertain subtree
                        CFGUnderdeterminedTree t_objUTree = this.m_hashSubtrees.get(t_objSubTree.getId());
                        try 
                        {
                            this.addUncertainBranch(a_objGraph,t_objUTree,t_objNode);	
                        } 
                        catch (GlycoconjugateException e) 
                        {
                            throw new SugarImporterException("CFG011",this.m_iPosition);
                        }		                	
                    }
                }
                t_aSubTrees.clear();
                // <residuename> <position> { { <subbranch> } <residuename> <position> }
                t_aPostions = this.position();
                t_objSubTreeMain.setId(null);
                t_objSubTreeMain.setGlycoNode(t_objNode);
                Linkage t_objLinkage = new Linkage();
                t_objLinkage.addChildLinkage(1);
                t_objLinkage.setParentLinkages(t_aPostions);
                GlycoEdge t_objEdge = new GlycoEdge();
                t_objEdge.addGlycosidicLinkage(t_objLinkage);
                t_objSubTreeMain.setGlycoEdge(t_objEdge);
                t_aSubTrees.add(t_objSubTreeMain);
            }
        }
        catch (GlycoconjugateException e) 
        {
            throw new SugarImporterException("COMMON013",this.m_iPosition);
        }
        return t_objSubTreeMain;
    }

    private void addUncertainBranch(GlycoGraph a_objGraph, CFGUnderdeterminedTree a_objTree,GlycoNode a_objNode) throws SugarImporterException, GlycoconjugateException 
    {
        if ( a_objTree == null )
        {
            throw new SugarImporterException("CFG009",this.m_iPosition);
        }
        Sugar t_objSugar = (Sugar)a_objGraph;
        t_objSugar.addUndeterminedSubTreeParent(a_objTree.getTree(),a_objNode);
    }
}
