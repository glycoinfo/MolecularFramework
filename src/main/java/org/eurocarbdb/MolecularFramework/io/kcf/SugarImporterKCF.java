package org.eurocarbdb.MolecularFramework.io.kcf;

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
import org.eurocarbdb.MolecularFramework.sugar.Sugar;
import org.eurocarbdb.MolecularFramework.sugar.SugarUnitRepeat;
import org.eurocarbdb.MolecularFramework.sugar.UnvalidatedGlycoNode;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorException;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorNodeType;

/**
 * start		     ::= [ <head> ] <nodes> <edges> [ <bracket> ] "/" "/" "/" [ "\n" ]
 * head			     ::= "E" "N" "T" "R" "Y" " " { " " } <go_number> " " { " " } "G" "l" "y" "c" "a" "n" "\n"
 * go_number	     ::= "G" ( "0" | ... | "9" ) { ( "0" | ... | "9" ) } 
 * nodes		     ::= "N" "O" "D" "E" " " { " " } <number> "\n" <node> { <node> }
 * node			     ::= { " " } <number> " " { " " } <glycan_name> " " { " " } <signed_dec_number> " " { " " } <signed_dec_number> "\n"
 * glycan_name       ::= <symbol> { <symbol> } 
 * edges		     ::= "E" "D" "G" "E" " " { " " } <number> "\n" { <edge> }
 * edge			     ::= { " " } <number> " " { " " } <number> [ ":" <link_information> ] " " { " " } <number> [ ":" <link_information> ] "\n"
 * link_information  ::= ["a" | "b"]  [ ( <number> [ "," <number> ] ) | ( * [ "1" ] ) ]
 * symbol            ::= <character> | "0" | ... | "9"
 * signed_number     ::= [ "-" | "+" ] <number> 
 * bracket           ::= "B" "R" "A" "C" "K" "E" "T" <bracket_line> <bracket_line> <bracket_final>
 * bracket_line      ::= { " " } <number> " " { " " } <signed_dec_number> " " { " " } <signed_dec_number> " " { " " } <signed_dec_number> " " { " " } <signed_dec_number> "\n"
 * bracket_final     ::= { " " } <number> " " { " " } ( <signed_dec_number> " " { " " } <signed_dec_number> " " { " " } ( "n" [ "-" <number> ] | <number> [ "-" <number> ] ) | "n" | "m" ) "\n"
 * signed_dec_number ::= [ "-" | "+" ] <number> [ "." ( 0 | ... | 9 ) { 0 | ... | 9 } ] 
 * @author Logan
 *
 */
public class SugarImporterKCF extends SugarImporterText
{
    private String m_strGONumber                        = "";
    private int m_iResidueCount                         = 0;
    private int m_iEdgeCount                            = 0;
    private int m_iBlockCount                           = 0;
    private HashMap<Integer,KCFResidue> m_hResidues     = new HashMap<Integer,KCFResidue>(); 
    private ArrayList<KCFLinkage> m_aLinkages           = new ArrayList<KCFLinkage>();
    private ArrayList<KCFLinkage> m_aHandledLinkages    = new ArrayList<KCFLinkage>();
    private ArrayList<KCFBlock> m_aBlock				= new ArrayList<KCFBlock>();
    private char m_cAnomer 								= ' ';
    private int m_iLinkagePosition                      = -1;

    /**
     * Parse a string according the gramatic of the language. Uses recursiv decent
     *  
     * @param a_strStream		String that is to parse
     * @throws ImportExeption 
     */
    public Sugar parse(String a_strStream) throws SugarImporterException 
    {
        this.m_objSugar = new Sugar();
        this.m_iPosition = -1;
        // Copie string and add endsymbol		
        this.m_strText = a_strStream.replaceAll("\r", "") + '$';
        this.m_iLength = this.m_strText.length();
        // get first token . Error ? ==> string empty
        this.nextToken();
        this.start();
        return this.m_objSugar;
    }

    // start		::= <head> <nodes> <edges> [ <bracket> ] "/" "/" "/" [ "\n" ]
    protected void start() throws SugarImporterException 
    {
        this.clear();
        // <head>
        if ( this.m_cToken == 'E' )
        {
            this.head();
        }
        // <head> <nodes> 
        this.nodes();
        // <head> <nodes> <edges>
        this.edges();
        // <head> <nodes> <edges> [ <bracket> ]
        if ( this.m_cToken == 'B' )
        {
            this.bracket();
        }
        // <head> <nodes> <edges> [ <bracket> ] "/" 
        if ( this.m_cToken != '/' )
        {
            throw new SugarImporterException("KCF000", this.m_iPosition);
        }
        this.nextToken();
        // <head> <nodes> <edges> [ <bracket> ] "/" "/" 
        if ( this.m_cToken != '/' )
        {
            throw new SugarImporterException("KCF000", this.m_iPosition);
        }
        this.nextToken();
        // <head> <nodes> <edges> [ <bracket> ] "/" "/" "/"
        if ( this.m_cToken != '/' )
        {
            throw new SugarImporterException("KCF000", this.m_iPosition);
        }
        this.nextToken();
        // <head> <nodes> <edges> [ <bracket> ] "/" "/" "/" [ "\n" ]
        if ( this.m_cToken == '\n' )
        {
            this.nextToken();
        }
        // $
        if ( ! this.finished() )
        {
            throw new SugarImporterException("KCF001", this.m_iPosition);
        }
        // bring the pieces together, create sugar
        this.m_iResidueCount = 0;
        this.m_iEdgeCount = 0;
        this.m_iBlockCount = 0;
        this.m_aHandledLinkages.clear();
        this.createSugar();
        if ( this.m_iResidueCount != this.m_hResidues.size() )
        {
            throw new SugarImporterException("KCF016", this.m_iPosition);
        }
        if ( this.m_iEdgeCount != this.m_aLinkages.size() )
        {
            throw new SugarImporterException("KCF017", this.m_iPosition);	
        }
        if ( this.m_iBlockCount != this.m_aBlock.size() )
        {
            throw new SugarImporterException("KCF018", this.m_iPosition);
        }
        for (Iterator<KCFBlock> t_iterBlock = this.m_aBlock.iterator(); t_iterBlock.hasNext();) 
        {
            if ( t_iterBlock.next().getRepeatUnit() != null )
            {
                throw new SugarImporterException("KCF056", this.m_iPosition);
            }			
        }
        if ( this.m_aBlock.size() > 0 )
        {
            try
            {
                GlycoVisitorNodeType t_objNodeType = new GlycoVisitorNodeType();
                UnvalidatedGlycoNode t_objNode;
                ArrayList<GlycoNode> t_aRemove = new ArrayList<GlycoNode>();
                for (Iterator<GlycoNode> t_iterRes = this.m_objSugar.getNodes().iterator(); t_iterRes.hasNext();) 
                {
                    GlycoNode t_objRes = t_iterRes.next();
                    t_objNode = t_objNodeType.getUnvalidatedNode(t_objRes); 
                    if ( t_objNode != null )
                    {
                        if ( t_objNode.getName().trim().equals("*"))
                        {
                            // pruefe linkage
                            if ( t_objNode.getParentEdge() != null && t_objNode.getChildEdges().size() > 0 )
                            {
                                throw new SugarImporterException("KCF057", this.m_iPosition);
                            }
                            if ( t_objNode.getParentEdge() != null )
                            {
                                if ( !t_objNodeType.isSugarUnitRepeat(t_objNode.getParentNode()))
                                {
                                    throw new SugarImporterException("KCF058", this.m_iPosition);
                                }
                            }
                            if ( t_objNode.getChildEdges().size() > 0 )
                            {
                                if ( t_objNode.getChildEdges().size() != 1 )
                                {
                                    throw new SugarImporterException("KCF059", this.m_iPosition);
                                }
                                for (Iterator<GlycoEdge> t_iterChild = t_objNode.getChildEdges().iterator(); t_iterChild.hasNext();) 
                                {
                                    GlycoEdge t_objEdge = t_iterChild.next();
                                    if ( !t_objNodeType.isSugarUnitRepeat(t_objEdge.getChild()))
                                    {
                                        throw new SugarImporterException("KCF058", this.m_iPosition);
                                    }	
                                }
                            }        				
                            t_aRemove.add(t_objNode);
                        }
                    }
                }
                for (Iterator<GlycoNode> t_iterRes = t_aRemove.iterator(); t_iterRes.hasNext();) 
                {
                    this.m_objSugar.removeNode(t_iterRes.next());				
                }
            }
            catch (GlycoVisitorException e) 
            {
                throw new SugarImporterException("COMMON013", this.m_iPosition);
            } 
            catch (GlycoconjugateException e) 
            {
                throw new SugarImporterException("COMMON013", this.m_iPosition);
            }
        }

    }

    private void clear() 
    {
        // cleanup system
        this.m_strGONumber = "";
        this.m_iResidueCount = 0;
        this.m_iEdgeCount = 0;
        this.m_iBlockCount = 0;
        this.m_hResidues.clear();
        this.m_aBlock.clear();
        this.m_aLinkages.clear();
    }

    /**
     * edges        ::= "E" "D" "G" "E" " " { " " } <number> "\n" { <edge> }
     * @throws SugarImporterException 
     */
    private void edges() throws SugarImporterException
    {
        int t_iCounter = 0;
        // "E"
        if ( this.m_cToken != 'E' )
        {
            throw new SugarImporterException("KCF023", this.m_iPosition);
        }
        this.nextToken();
        // "E" "D" 
        if ( this.m_cToken != 'D' )
        {
            throw new SugarImporterException("KCF023", this.m_iPosition);
        }
        this.nextToken();
        // "E" "D" "G" 
        if ( this.m_cToken != 'G' )
        {
            throw new SugarImporterException("KCF023", this.m_iPosition);
        }
        this.nextToken();
        // "E" "D" "G" "E" 
        if ( this.m_cToken != 'E' )
        {
            throw new SugarImporterException("KCF023", this.m_iPosition);
        }
        this.nextToken();
        // "E" "D" "G" "E" " "
        if ( this.m_cToken != ' ' )
        {
            throw new SugarImporterException("KCF024", this.m_iPosition);
        }
        this.nextToken();
        // "E" "D" "G" "E" " " { " " } 
        while ( this.m_cToken == ' ' )
        {
            this.nextToken();
        }
        // "E" "D" "G" "E" " " { " " } <number>
        this.m_iEdgeCount = this.number();
        // "E" "D" "G" "E" " " { " " } <number> "\n"
        if ( this.m_cToken != '\n' )
        {
            throw new SugarImporterException("KCF025", this.m_iPosition);
        }
        this.nextToken();
        // "E" "D" "G" "E" " " { " " } <number> "\n" { <edge> }
        while ( this.m_cToken != '/' && this.m_cToken != 'B' )
        {
            t_iCounter++;
            this.edge();
        }
        if ( this.m_iEdgeCount != t_iCounter )
        {
            throw new SugarImporterException("KCF026");
        }
    }

    /**
     * edge         ::= { " " } <number> " " { " " } <number> [ <link_information> ] " " { " " } <number> [ ":" <link_information> ] { " " } "\n"
     * @throws SugarImporterException 
     */
    private void edge() throws SugarImporterException
    {
        Integer t_iNodeOne = 0;
        Integer t_iNodeTwo = 0;
        int t_iLinkagePositionOne = Linkage.UNKNOWN_POSITION;
        int t_iLinkagePositionTwo = Linkage.UNKNOWN_POSITION;
        // { " " } 
        while ( this.m_cToken == ' ' )
        {
            this.nextToken();
        }
        // { " " } <number>
        this.number();
        // { " " } <number> " " 
        if ( this.m_cToken != ' ' )
        {
            throw new SugarImporterException("KCF020", this.m_iPosition);
        }
        this.nextToken();
        // { " " } <number> " " { " " }
        while ( this.m_cToken == ' ' )
        {
            this.nextToken();
        }
        // { " " } <number> " " { " " } <number>
        t_iNodeOne = this.number();
        if ( !this.m_hResidues.containsKey( t_iNodeOne ) )
        {
            throw new SugarImporterException("KCF027", this.m_iPosition);
        }
        // { " " } <number> " " { " " } <number> [ ":" ]
        if ( this.m_cToken == ':' )
        {
            this.nextToken();
            this.link_information();
            t_iLinkagePositionOne = this.m_iLinkagePosition;
            // add the anomer to the residue name
            if ( this.m_cAnomer != ' ' )
            {
                UnvalidatedGlycoNode t_objResidue = this.m_hResidues.get( t_iNodeOne ).getResidue(); 
                try
                {
                    t_objResidue.setName( String.format("%c",this.m_cAnomer) + "-" + t_objResidue.getName() );
                } 
                catch (GlycoconjugateException e)
                {
                    throw new SugarImporterException("KCF050",this.m_iPosition);
                }
                this.m_cAnomer = ' ';
            }
        }
        // { " " } <number> " " { " " } <number> [ ":" <link_information> ] " " 
        if ( this.m_cToken != ' ' )
        {
            throw new SugarImporterException("KCF020", this.m_iPosition);
        }
        this.nextToken();
        // { " " } <number> " " { " " } <number> [ ":" <link_information> ] " " { " " }
        while ( this.m_cToken == ' ' )
        {
            this.nextToken();
        }
        // { " " } <number> " " { " " } <number> [ ":" <link_information> ] " " { " " } <number>
        t_iNodeTwo = this.number();
        if ( !this.m_hResidues.containsKey( t_iNodeTwo ) )
        {
            throw new SugarImporterException("KCF027", this.m_iPosition);
        }
        // { " " } <number> " " { " " } <number> [ ":" <link_information> ] " " { " " } <number> [ ":" ]
        if ( this.m_cToken == ':' )
        {
            this.nextToken();
            // { " " } <number> " " { " " } <number> [ ":" <link_information> ] " " { " " } <number> [ ":" <link_information> ]
            this.link_information();
            t_iLinkagePositionTwo = this.m_iLinkagePosition;
            // add the anomer to the residue name
            if ( this.m_cAnomer != ' ' )
            {
                UnvalidatedGlycoNode t_objResidue = this.m_hResidues.get( t_iNodeTwo ).getResidue(); 
                try
                {
                    t_objResidue.setName( String.format("%c",this.m_cAnomer) + "-" + t_objResidue.getName() );
                } 
                catch (GlycoconjugateException e)
                {
                    throw new SugarImporterException("KCF050",this.m_iPosition);
                }
                this.m_cAnomer = ' ';
            }
        }             
        // { " " } <number> " " { " " } <number> [ ":" <link_information> ] " " { " " } <number> [ ":" <link_information> ] { " " } 
        while ( this.m_cToken == ' ' )
        {
            this.nextToken();
        }
        // { " " } <number> " " { " " } <number> [ ":" <link_information> ] " " { " " } <number> [ ":" <link_information> ] { " " } "\n"
        if ( this.m_cToken != '\n' )
        {
            throw new SugarImporterException("KCF021", this.m_iPosition);
        }
        this.nextToken();
        // Store edges
        KCFLinkage t_objKcfLinkage = new KCFLinkage(t_iLinkagePositionOne,
                t_iLinkagePositionTwo,  t_iNodeOne, t_iNodeTwo);
        this.m_aLinkages.add(t_objKcfLinkage);
    }

    /**
     * link_information  ::= ["a" | "b"]  [ ( <number> [ "," <number> ] ) | ( * [ "1" ] ) ]
     * @throws SugarImporterException 
     */
    private void link_information() throws SugarImporterException
    {
        int t_iDigit = 0;
        if ( this.m_cToken == 'a' || this.m_cToken == 'b' )
        {
            this.m_cAnomer = this.m_cToken;
            this.nextToken();
        }
        else if ( this.m_cToken == '*' )
        {
            this.m_cAnomer = ' ';
            this.nextToken();
        }
        else
        {
            this.m_cAnomer = ' ';
        }
        t_iDigit = (int)this.m_cToken;
        if ( t_iDigit > 48 && t_iDigit < 58 )
        {
            this.m_iLinkagePosition = this.number();

            //			if ( this.m_cToken == ',' )
            //			{
            //			this.nextToken();
            //			this.number();
            //			}
        }
        else
        {
            this.m_iLinkagePosition = Linkage.UNKNOWN_POSITION;
        }
    }

    // nodes		::= "N" "O" "D" "E" " " { " " } <number> "\n" <node> { <node> }
    private void nodes() throws SugarImporterException 
    {
        // "N" 
        if ( this.m_cToken != 'N' )
        {
            throw new SugarImporterException("KCF008", this.m_iPosition);
        }
        this.nextToken();
        // "N" "O" 
        if ( this.m_cToken != 'O' )
        {
            throw new SugarImporterException("KCF008", this.m_iPosition);
        }
        this.nextToken();
        // "N" "O" "D" 
        if ( this.m_cToken != 'D' )
        {
            throw new SugarImporterException("KCF008", this.m_iPosition);
        }
        this.nextToken();
        // "N" "O" "D" "E" 
        if ( this.m_cToken != 'E' )
        {
            throw new SugarImporterException("KCF008", this.m_iPosition);
        }
        this.nextToken();
        // "N" "O" "D" "E" " " 
        if ( this.m_cToken != ' ' )
        {
            throw new SugarImporterException("KCF032", this.m_iPosition);
        }
        this.nextToken();
        // "N" "O" "D" "E" " " { " " } 
        while ( this.m_cToken == ' ' )
        {
            this.nextToken();
        }
        // "N" "O" "D" "E" " " { " " } <number> 
        this.m_iResidueCount = this.number();
        // "N" "O" "D" "E" " " { " " } <number> "\n" 
        if ( this.m_cToken != '\n' )
        {
            throw new SugarImporterException("KCF033", this.m_iPosition);
        }
        this.nextToken();
        // "N" "O" "D" "E" " " { " " } <number> "\n" <node> 
        this.node();
        // "N" "O" "D" "E" " " { " " } <number> "\n" <node> { <node> } "E" 
        while ( this.m_cToken != 'E' )
        {
            this.node();
        }
        if ( this.m_iResidueCount != this.m_hResidues.size() )
        {
            throw new SugarImporterException("KCF013");
        }
    }

    // node			::= { " " } <number> " " { " " } <glycan_name> " " { " " } <signed_dec_number> " " { " " } <signed_dec_number> "\n"
    private void node() throws SugarImporterException 
    {
        int t_iStart = 0;
        Integer t_iID = 0;
        double t_dX = 0;
        double t_dY = 0;
        UnvalidatedGlycoNode t_objResidue = new UnvalidatedGlycoNode();
        KCFResidue t_objKCFResidue = new KCFResidue(); 
        // { " " }
        while ( this.m_cToken == ' ' )
        {
            this.nextToken();
        }
        // { " " } <number> 
        t_iID = this.number();
        if ( this.m_hResidues.containsKey(t_iID) )
        {
            throw new SugarImporterException("KCF014", this.m_iPosition);
        }
        // { " " } <number> " " 
        if ( this.m_cToken != ' ' )
        {
            throw new SugarImporterException("KCF009", this.m_iPosition);
        }
        this.nextToken();
        // { " " } <number> " " { " " } 
        while ( this.m_cToken == ' ' )
        {
            this.nextToken();
        }
        // { " " } <number> " " { " " } <glycan_name>
        t_iStart = this.m_iPosition;
        this.glycan_name();
        try
        {
            t_objResidue.setName(this.m_strText.substring(t_iStart,this.m_iPosition));
        } 
        catch (GlycoconjugateException e)
        {
            throw new SugarImporterException("KCF050",this.m_iPosition);
        }
        // { " " } <number> " " { " " } <glycan_name> " " 
        if ( this.m_cToken != ' ' )
        {
            throw new SugarImporterException("KCF010", this.m_iPosition);
        }
        this.nextToken();
        // { " " } <number> " " { " " } <glycan_name> " " { " " } 
        while ( this.m_cToken == ' ' )
        {
            this.nextToken();
        }
        // { " " } <number> " " { " " } <glycan_name> " " { " " } <signed_dec_number>
        t_dX = this.float_number_signed();
        // { " " } <number> " " { " " } <glycan_name> " " { " " } <signed_dec_number> " " 
        if ( this.m_cToken != ' ' )
        {
            throw new SugarImporterException("KCF011", this.m_iPosition);
        }
        this.nextToken();
        // { " " } <number> " " { " " } <glycan_name> " " { " " } <signed_dec_number> " " { " " } 
        while ( this.m_cToken == ' ' )
        {
            this.nextToken();
        }
        // { " " } <number> " " { " " } <glycan_name> " " { " " } <signed_dec_number> " " { " " } <signed_dec_number>
        t_dY = this.float_number_signed();
        // { " " } <number> " " { " " } <glycan_name> " " { " " } <signed_dec_number> " " { " " } <signed_dec_number> "\n"
        if ( this.m_cToken != '\n' )
        {
            throw new SugarImporterException("KCF012", this.m_iPosition);
        }
        this.nextToken();
        // store the residue
        t_objKCFResidue.init( t_objResidue , t_dX , t_dY , t_iID );
        this.m_hResidues.put(t_iID,t_objKCFResidue);
    }

    /**
     *  glycan_name  ::= <symbol> { <symbol> }  
     */
    private void glycan_name() throws SugarImporterException
    {
        // <symbol> 
        this.symbol();
        // <symbol> { <symbol> }
        while ( this.m_cToken != ' ' )
        {
            this.symbol();
        }

    }

    /**
     * symbol       ::= <character> | "0" | ... | "9" | "/" | "-" | "*" | "," | "(" | ")"
     * @throws SugarImporterException 
     * 
     */
    private void symbol() throws SugarImporterException
    {
        int t_iDigit = (int) this.m_cToken;
        // "0" | ... | "9"
        if ( t_iDigit > 47 && t_iDigit < 58 )
        {
            this.nextToken();
        }
        else
        {
            // / | "-"
            if ( this.m_cToken == '/' || this.m_cToken == '-' || this.m_cToken == '*'  || this.m_cToken == ',' )
            {
                this.nextToken();
            }
            else
            {
                if ( this.m_cToken == '(' || this.m_cToken == ')' )
                {
                    this.nextToken();
                }
                else
                {
                    this.character();
                }
            }
        }
    }

    // "E" "N" "T" "R" "Y" " " { " " } <go_number> " " { " " } "G" "l" "y" "c" "a" "n" "\n"
    private void head() throws SugarImporterException 
    {
        // "E" 
        if ( this.m_cToken != 'E' )
        {
            throw new SugarImporterException("KCF002", this.m_iPosition);
        }
        this.nextToken();
        // "E" "N" 
        if ( this.m_cToken != 'N' )
        {
            throw new SugarImporterException("KCF002", this.m_iPosition);
        }
        this.nextToken();
        // "E" "N" "T" 
        if ( this.m_cToken != 'T' )
        {
            throw new SugarImporterException("KCF002", this.m_iPosition);
        }
        this.nextToken();
        // "E" "N" "T" "R" 
        if ( this.m_cToken != 'R' )
        {
            throw new SugarImporterException("KCF002", this.m_iPosition);
        }
        this.nextToken();
        // "E" "N" "T" "R" "Y" 
        if ( this.m_cToken != 'Y' )
        {
            throw new SugarImporterException("KCF002", this.m_iPosition);
        }
        this.nextToken();
        // "E" "N" "T" "R" "Y" " "
        if ( this.m_cToken != ' ' )
        {
            throw new SugarImporterException("KCF003", this.m_iPosition);
        }
        this.nextToken();
        // "E" "N" "T" "R" "Y" " " { " " } 
        while ( this.m_cToken == ' ' )
        {
            this.nextToken();
        }
        // "E" "N" "T" "R" "Y" " " { " " } <go_number>
        this.go_number();
        // "E" "N" "T" "R" "Y" " " { " " } <gennumber> " "
        // "E" "N" "T" "R" "Y" " " { " " } <go_number> " " { " " } "G" 
        if ( this.m_cToken != 'G' )
        {
            throw new SugarImporterException("KCF005", this.m_iPosition);
        }
        this.nextToken();
        // "E" "N" "T" "R" "Y" " " { " " } <go_number> " " { " " } "G" "l"
        if ( this.m_cToken != 'l' )
        {
            throw new SugarImporterException("KCF005", this.m_iPosition);
        }
        this.nextToken();
        // "E" "N" "T" "R" "Y" " " { " " } <go_number> " " { " " } "G" "l" "y" 
        if ( this.m_cToken != 'y' )
        {
            throw new SugarImporterException("KCF005", this.m_iPosition);
        }
        this.nextToken();
        // "E" "N" "T" "R" "Y" " " { " " } <go_number> " " { " " } "G" "l" "y" "c" 
        if ( this.m_cToken != 'c' )
        {
            throw new SugarImporterException("KCF005", this.m_iPosition);
        }
        this.nextToken();
        // "E" "N" "T" "R" "Y" " " { " " } <go_number> " " { " " } "G" "l" "y" "c" "a" 
        if ( this.m_cToken != 'a' )
        {
            throw new SugarImporterException("KCF005", this.m_iPosition);
        }
        this.nextToken();
        // "E" "N" "T" "R" "Y" " " { " " } <go_number> " " { " " } "G" "l" "y" "c" "a" "n" 
        if ( this.m_cToken != 'n' )
        {
            throw new SugarImporterException("KCF005", this.m_iPosition);
        }
        this.nextToken();
        // "E" "N" "T" "R" "Y" " " { " " } <go_number> " " { " " } "G" "l" "y" "c" "a" "n" "\n"
        if ( this.m_cToken != '\n' )
        {
            throw new SugarImporterException("KCF006", this.m_iPosition);
        }
        this.nextToken();
    }

    // go_number     ::= "G" ( "0" | ... | "9" ) { ( "0" | ... | "9" ) }
    private void go_number() throws SugarImporterException 
    {
        int t_iStart = this.m_iPosition;

        if ( this.matchesGlycan() )
        {
            return;
        }
        while ( this.m_cToken != ' ' && this.m_cToken != '\t' )
        {
            this.nextToken();
        }
        this.m_strGONumber = this.m_strText.substring(t_iStart,this.m_iPosition);
        if ( this.m_cToken != ' ' )
        {
            throw new SugarImporterException("KCF004", this.m_iPosition);
        }
        this.nextToken();
        // "E" "N" "T" "R" "Y" " " { " " } <go_number> " " { " " } 
        while ( this.m_cToken == ' ' )
        {
            this.nextToken();
        }
    }

    private boolean matchesGlycan() throws SugarImporterException
    {
        if ( this.m_cToken != 'G' )
        {
            return false;
        }
        char t_token = this.aheadToken(1);
        if ( t_token != 'l' &&  t_token != 'L' )
        {
            return false;
        }
        t_token = this.aheadToken(2);
        if ( t_token != 'y' &&  t_token != 'Y' )
        {
            return false;
        }
        t_token = this.aheadToken(3);
        if ( t_token != 'c' &&  t_token != 'C' )
        {
            return false;
        }
        t_token = this.aheadToken(4);
        if ( t_token != 'a' &&  t_token != 'A' )
        {
            return false;
        }
        t_token = this.aheadToken(5);
        if ( t_token != 'n' &&  t_token != 'N' )
        {
            return false;
        }
        return true;
    }

    /**
     * bracket        ::= "B" "R" "A" "C" "K" "E" "T" <bracket_line> <bracket_line> <bracket_final> {<bracket_line> <bracket_line> <bracket_final> }
     */
    private void bracket() throws SugarImporterException
    {
        // "B" 
        if ( this.m_cToken != 'B' )
        {
            throw new SugarImporterException("KCF040", this.m_iPosition);
        }
        this.nextToken();
        // "B" "R" 
        if ( this.m_cToken != 'R' )
        {
            throw new SugarImporterException("KCF040", this.m_iPosition);
        }
        this.nextToken();
        // "B" "R" "A" 
        if ( this.m_cToken != 'A' )
        {
            throw new SugarImporterException("KCF040", this.m_iPosition);
        }
        this.nextToken();
        // "B" "R" "A" "C" 
        if ( this.m_cToken != 'C' )
        {
            throw new SugarImporterException("KCF040", this.m_iPosition);
        }
        this.nextToken();
        // "B" "R" "A" "C" "K" 
        if ( this.m_cToken != 'K' )
        {
            throw new SugarImporterException("KCF040", this.m_iPosition);
        }
        this.nextToken();
        // "B" "R" "A" "C" "K" "E" 
        if ( this.m_cToken != 'E' )
        {
            throw new SugarImporterException("KCF040", this.m_iPosition);
        }
        this.nextToken();
        // "B" "R" "A" "C" "K" "E" "T"
        if ( this.m_cToken != 'T' )
        {
            throw new SugarImporterException("KCF040", this.m_iPosition);
        }
        this.nextToken();        
        // "B" "R" "A" "C" "K" "E" "T" <bracket_line>
        KCFBlock t_objBlock = new KCFBlock();
        this.bracket_line(t_objBlock);
        // "B" "R" "A" "C" "K" "E" "T" <bracket_line> <bracket_line>
        this.bracket_line(t_objBlock);
        // "B" "R" "A" "C" "K" "E" "T" <bracket_line> <bracket_line> <bracket_final>
        this.bracket_final(t_objBlock);
        this.m_aBlock.add(t_objBlock);
        // "B" "R" "A" "C" "K" "E" "T" <bracket_line> <bracket_line> <bracket_final> {<bracket_line> <bracket_line> <bracket_final> }
        while ( this.m_cToken != '/' )
        {
            t_objBlock = new KCFBlock();
            // <bracket_line>
            this.bracket_line(t_objBlock);
            // <bracket_line> <bracket_line>
            this.bracket_line(t_objBlock);
            // <bracket_line> <bracket_line> <bracket_final>
            this.bracket_final(t_objBlock);
            this.m_aBlock.add(t_objBlock);
        }
    }

    /**
     * bracket_line   ::= { " " } <number> " " { " " } <signed_dec_number> " " { " " } <signed_dec_number> " " { " " } <signed_dec_number> " " { " " } <signed_dec_number> "\n"
     * @param a_objBlock 
     * @throws SugarImporterException 
     */
    private void bracket_line(KCFBlock a_objBlock) throws SugarImporterException
    {
        // { " " }
        while ( this.m_cToken == ' ' )
        {
            this.nextToken();
        }
        // { " " } <number>
        this.number();
        // { " " } <number> " " 
        if ( this.m_cToken != ' ' )
        {
            throw new SugarImporterException("KCF043", this.m_iPosition);
        }
        this.nextToken();
        // { " " } <number> " " { " " } 
        while ( this.m_cToken == ' ' )
        {
            this.nextToken();
        }
        // { " " } <number> " " { " " } <signed_dec_number>
        double t_dCoo1 = this.float_number_signed();
        // { " " } <number> " " { " " } <signed_dec_number> " " 
        if ( this.m_cToken != ' ' )
        {
            throw new SugarImporterException("KCF044", this.m_iPosition);
        }
        this.nextToken();
        // { " " } <number> " " { " " } <signed_dec_number> " " { " " } 
        while ( this.m_cToken == ' ' )
        {
            this.nextToken();
        }
        // { " " } <number> " " { " " } <signed_dec_number> " " { " " } <signed_dec_number>
        double t_dCoo2 = this.float_number_signed();
        // { " " } <number> " " { " " } <signed_dec_number> " " { " " } <signed_dec_number> " "
        if ( this.m_cToken != ' ' )
        {
            throw new SugarImporterException("KCF044", this.m_iPosition);
        }
        this.nextToken();
        // { " " } <number> " " { " " } <signed_dec_number> " " { " " } <signed_dec_number> " " { " " } 
        while ( this.m_cToken == ' ' )
        {
            this.nextToken();
        }
        // { " " } <number> " " { " " } <signed_dec_number> " " { " " } <signed_dec_number> " " { " " } <signed_dec_number>
        double t_dCoo3 = this.float_number_signed();        
        // { " " } <number> " " { " " } <signed_dec_number> " " { " " } <signed_dec_number> " " { " " } <signed_dec_number> " " 
        if ( this.m_cToken != ' ' )
        {
            throw new SugarImporterException("KCF044", this.m_iPosition);
        }
        this.nextToken();
        // { " " } <number> " " { " " } <signed_dec_number> " " { " " } <signed_dec_number> " " { " " } <signed_dec_number> " " { " " }
        while ( this.m_cToken == ' ' )
        {
            this.nextToken();
        }
        // { " " } <number> " " { " " } <signed_dec_number> " " { " " } <signed_dec_number> " " { " " } <signed_dec_number> " " { " " } <signed_dec_number>
        double t_dCoo4 = this.float_number_signed();
        // { " " } <number> " " { " " } <signed_dec_number> " " { " " } <signed_dec_number> " " { " " } <signed_dec_number> " " { " " } <signed_dec_number> "\n"
        if ( this.m_cToken != '\n' )
        {
            throw new SugarImporterException("KCF045", this.m_iPosition);
        }
        this.nextToken();
        if ( t_dCoo1 != t_dCoo3 )
        {
            throw new SugarImporterException("KCF015", this.m_iPosition);
        }
        a_objBlock.setBracket(t_dCoo1,t_dCoo2,t_dCoo4);
    }

    /**
     * bracket_final     ::= { " " } <number> " " { " " } ( <signed_dec_number> " " { " " } <signed_dec_number> " " { " " } ( "n" [ "-" <number> ] | <number> ) | "n" | "m" ) "\n"
     * @param a_objBlock 
     * @throws SugarImporterException 
     */
    private void bracket_final(KCFBlock a_objBlock) throws SugarImporterException
    {
        // { " " }
        while ( this.m_cToken == ' ' )
        {
            this.nextToken();
        }
        // { " " } <number>
        this.number();
        // { " " } <number> " "
        if ( this.m_cToken != ' ' )
        {
            throw new SugarImporterException("KCF046", this.m_iPosition);
        }
        this.nextToken();
        // { " " } <number> " " { " " } 
        while ( this.m_cToken == ' ' )
        {
            this.nextToken();
        }
        if ( this.m_cToken == 'n' )
        {
            this.nextToken();
            a_objBlock.setMin( SugarUnitRepeat.UNKNOWN );
            a_objBlock.setMax( SugarUnitRepeat.UNKNOWN );
        }
        else
        {
            if ( this.m_cToken == 'm' )
            {
                this.nextToken();
                a_objBlock.setMin( SugarUnitRepeat.UNKNOWN );
                a_objBlock.setMax( SugarUnitRepeat.UNKNOWN );
            }
            else
            {
                // { " " } <number> " " { " " } <signed_dec_number>
                this.float_number_signed();
                // { " " } <number> " " { " " } <signed_dec_number> " " 
                if ( this.m_cToken != ' ' )
                {
                    throw new SugarImporterException("KCF047", this.m_iPosition);
                }
                this.nextToken();
                // { " " } <number> " " { " " } <signed_dec_number> " " { " " } 
                while ( this.m_cToken == ' ' )
                {
                    this.nextToken();
                }
                // { " " } <number> " " { " " } <signed_dec_number> " " { " " } <signed_dec_number> 
                this.float_number_signed();
                // { " " } <number> " " { " " } <signed_dec_number> " " { " " } <signed_dec_number> " " 
                if ( this.m_cToken != ' ' )
                {
                    throw new SugarImporterException("KCF047", this.m_iPosition);
                }
                this.nextToken();
                // { " " } <number> " " { " " } <signed_dec_number> " " { " " } <signed_dec_number> " " { " " } 
                while ( this.m_cToken == ' ' )
                {
                    this.nextToken();
                }
                // { " " } <number> " " { " " } <signed_dec_number> " " { " " } <signed_dec_number> " " { " " } ( "n" | <number> )
                if ( this.m_cToken == 'n' )
                {
                    a_objBlock.setMin( SugarUnitRepeat.UNKNOWN );
                    a_objBlock.setMax( SugarUnitRepeat.UNKNOWN );
                    this.nextToken();
                    if ( this.m_cToken == '-' )
                    {
                        // "n" [ "-" <number> ]
                        this.nextToken();
                        a_objBlock.setMax( this.number() );
                    }
                }
                else
                {   
                    if ( this.m_cToken == 'm' )
                    {
                        a_objBlock.setMin( SugarUnitRepeat.UNKNOWN );
                        a_objBlock.setMax( SugarUnitRepeat.UNKNOWN );
                        this.nextToken();
                        if ( this.m_cToken == '-' )
                        {
                            // "n" [ "-" <number> ]
                            this.nextToken();
                            a_objBlock.setMax( this.number() );
                        }
                    }
                    else
                    {   
                        // <number>
                        a_objBlock.setMin( this.number() );
                        a_objBlock.setMax( this.number() );
                        // <number> [ "-" <number> ]
                        if ( this.m_cToken == '-' )
                        {
                            // "n" [ "-" <number> ]
                            this.nextToken();
                            a_objBlock.setMax( this.number() );
                        }
                    }
                }
            }
        }
        // { " " } <number> " " { " " } <signed_dec_number> " " { " " } <signed_dec_number> " " { " " } ( "n" | <number> ) "\n"
        if ( this.m_cToken != '\n' )
        {
            throw new SugarImporterException("KCF048", this.m_iPosition);
        }
        this.nextToken();
    }

    public String getGONumber()
    {
        return this.m_strGONumber;
    }

    /**
     * @throws SugarImporterException 
     * 
     */
    private void createSugar() throws SugarImporterException
    {
        this.m_objSugar = new Sugar();
        KCFResidue t_objResidue = this.findRootResidue();
        if ( t_objResidue != null )
        {
            try 
            {
                this.m_objSugar.addNode( t_objResidue.getResidue() );
                this.m_iResidueCount++;
                this.addChildResidue(t_objResidue,this.m_objSugar);
            } 
            catch (GlycoconjugateException e) 
            {
                throw new SugarImporterException("KCF050",this.m_iPosition);
            }
        }
    }

    private KCFResidue findRootResidue() throws SugarImporterException 
    {
        KCFResidue t_objResult = null;
        boolean t_bDouble = false;
        for (Iterator<KCFResidue> t_iterNodes = this.m_hResidues.values().iterator(); t_iterNodes.hasNext();) 
        {
            KCFResidue t_objElement = t_iterNodes.next();
            if ( t_objResult == null )
            {
                t_objResult = t_objElement;
            }
            else
            {
                if ( t_objResult.getX() < t_objElement.getX() )
                {
                    t_objResult = t_objElement;
                    t_bDouble = false;
                }
                else
                {
                    if ( t_objResult.getX() == t_objElement.getX() )
                    {
                        // try to analyse the linkage between this residues
                        KCFLinkage t_objLinkage = this.findLinkage(t_objResult.getId(),t_objElement.getId());
                        if ( t_objLinkage == null )
                        {
                            t_bDouble = true;
                        }
                        else
                        {
                            Integer t_iFound = this.findRootResidueByLinkage(t_objLinkage);
                            if ( t_iFound == null )
                            {
                                t_bDouble = true;
                            }
                            else
                            {
                                if ( t_objElement.getId() == t_iFound )
                                {
                                    t_objResult = t_objElement;
                                }
                                else if ( t_objResult.getId() != t_iFound )
                                {
                                    t_bDouble = true;
                                }
                            }
                        }
                    }
                }
            }
        }
        if ( t_bDouble )
        {
            throw new SugarImporterException("KCF019", this.m_iPosition);
        }
        return t_objResult;
    }

    /**
     * @param linkage
     * @param result
     * @param element
     * @return
     */
    private Integer findRootResidueByLinkage(KCFLinkage a_objLinkage) 
    {
        Integer t_objResult = null;
        int t_iPositionOne = a_objLinkage.getPositionOne();
        int t_iPositionTwo = a_objLinkage.getPositionTwo();
        if ( t_iPositionOne == 1 && t_iPositionTwo == 1)
        {
            // 1-1 Linkage
            boolean t_bOne = this.hasChilds(a_objLinkage.getResidueOne(),a_objLinkage.getResidueTwo());
            boolean t_bTwo = this.hasChilds(a_objLinkage.getResidueTwo(),a_objLinkage.getResidueOne());
            if ( t_bOne && !t_bTwo )
            {
                t_objResult = a_objLinkage.getResidueTwo();
            }
            else if ( t_bTwo && !t_bOne)
            {
                t_objResult = a_objLinkage.getResidueOne();
            }
        }
        else if ( t_iPositionOne == 1 || ( t_iPositionOne == 2 && t_iPositionTwo > 2 ) )
        {
            t_objResult = a_objLinkage.getResidueTwo(); 
        }
        else if ( t_iPositionTwo == 1 || ( t_iPositionTwo == 2 && t_iPositionOne > 2 ) )
        {
            t_objResult = a_objLinkage.getResidueOne();
        }		
        return t_objResult;
    }

    /**
     * @param residueOne
     * @return
     */
    private boolean hasChilds(Integer a_iResidue,Integer a_iResidueExclude) 
    {
        for (Iterator<KCFLinkage> t_iterLinkages = this.m_aLinkages.iterator(); t_iterLinkages.hasNext();) 
        {
            KCFLinkage t_objLinkage = t_iterLinkages.next();
            if ( t_objLinkage.getResidueOne() == a_iResidue )
            {
                if ( t_objLinkage.getResidueTwo() != a_iResidueExclude )
                {
                    return true;
                }
            } 
            else if ( t_objLinkage.getResidueTwo() == a_iResidue )
            {
                if ( t_objLinkage.getResidueOne() != a_iResidueExclude )
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param id
     * @param id2
     * @return
     * @throws SugarImporterException 
     */
    private KCFLinkage findLinkage(int a_iIdOne, int a_iIdTwo) throws SugarImporterException 
    {
        KCFLinkage t_objResult = null;
        for (Iterator<KCFLinkage> t_iterLinkages = this.m_aLinkages.iterator(); t_iterLinkages.hasNext();) 
        {
            KCFLinkage t_objLinkage = t_iterLinkages.next();
            if ( t_objLinkage.getResidueOne() == a_iIdOne && t_objLinkage.getResidueTwo() == a_iIdTwo )
            {
                if ( t_objResult != null )
                {
                    return null;	
                }
                t_objResult = t_objLinkage;
            }
            else if ( t_objLinkage.getResidueOne() == a_iIdTwo && t_objLinkage.getResidueTwo() == a_iIdOne )
            {
                if ( t_objResult != null )
                {
                    return null;
                }
                t_objResult = t_objLinkage;
            }
        }
        return t_objResult;
    }

    private void addChildResidue(KCFResidue a_objResidue,GlycoGraph a_objGraph) throws GlycoconjugateException, SugarImporterException 
    {
        KCFResidue t_objResChild;
        GlycoEdge t_objEdge;
        int t_iId = a_objResidue.getId();
        for (Iterator<KCFLinkage> t_iterLinkages = this.m_aLinkages.iterator(); t_iterLinkages.hasNext();) 
        {
            KCFLinkage t_objLinkage = t_iterLinkages.next();
            if ( t_objLinkage.getResidueOne() == t_iId || t_objLinkage.getResidueTwo() == t_iId )
            {
                if ( !this.m_aHandledLinkages.contains(t_objLinkage) )
                {
                    // linkage not handled before
                    if ( t_objLinkage.getResidueOne() == t_iId )
                    {
                        t_objResChild = this.m_hResidues.get(t_objLinkage.getResidueTwo());
                        t_objEdge = t_objLinkage.getEdge(true);						
                    }
                    else
                    {
                        t_objResChild = this.m_hResidues.get(t_objLinkage.getResidueOne());
                        t_objEdge = t_objLinkage.getEdge(false);
                    }
                    KCFBlock t_objBlockIn = this.crossBlockIn(a_objResidue,t_objResChild);
                    if ( t_objBlockIn == null )
                    {
                        KCFBlock t_objBlockOut = this.crossBlockOut(a_objResidue,t_objResChild); 
                        if ( t_objBlockOut == null )
                        {
                            a_objGraph.addNode(t_objResChild.getResidue());
                            this.m_iResidueCount++;
                            a_objGraph.addEdge(a_objResidue.getResidue(),t_objResChild.getResidue(),t_objEdge);
                            this.m_iEdgeCount++;
                            this.m_aHandledLinkages.add(t_objLinkage);
                            this.addChildResidue(t_objResChild,a_objGraph);
                        }
                        else
                        {
                            // repeat out
                            if ( t_objBlockOut.getRepeatUnit() == null )
                            {
                                throw new SugarImporterException("KCF054", this.m_iPosition);
                            }
                            GlycoGraph t_objParentGraph = t_objBlockOut.getParentGraph();
                            t_objParentGraph.addNode(t_objResChild.getResidue());
                            this.m_iResidueCount++;
                            t_objParentGraph.addEdge(t_objBlockOut.getRepeatUnit(),t_objResChild.getResidue(),t_objEdge);
                            this.m_iEdgeCount++;
                            this.m_aHandledLinkages.add(t_objLinkage);
                            // internal repeat
                            Linkage t_objLinkOut = t_objBlockOut.getRepeatLinkage();
                            for (Iterator<Linkage> t_iter = t_objEdge.getGlycosidicLinkages().iterator(); t_iter.hasNext();) 
                            {
                                Linkage t_objLink = t_iter.next();
                                t_objLinkOut.setParentLinkages(t_objLink.getParentLinkages());
                                t_objLinkOut.setParentLinkageType(t_objLink.getParentLinkageType());
                            }
                            GlycoEdge t_objInternal = new GlycoEdge();
                            t_objInternal.addGlycosidicLinkage(t_objLinkOut);
                            t_objBlockOut.getRepeatUnit().setRepeatLinkage(t_objInternal, a_objResidue.getResidue(), t_objBlockOut.getRepeatChild());
                            this.m_iBlockCount++;
                            t_objBlockOut.setRepeatUnit(null);
                            this.addChildResidue(t_objResChild,t_objParentGraph);
                        }
                    }
                    else
                    {
                        KCFBlock t_objBlockOut = this.crossBlockOut(a_objResidue,t_objResChild); 
                        if ( t_objBlockOut != null )
                        {
                            throw new SugarImporterException("KCF052", this.m_iPosition);
                        }
                        SugarUnitRepeat t_objRepeat = new SugarUnitRepeat();
                        t_objBlockIn.setRepeatUnit(t_objRepeat);
                        a_objGraph.addNode(t_objRepeat);
                        a_objGraph.addEdge(a_objResidue.getResidue(),t_objRepeat,t_objEdge);
                        this.m_iEdgeCount++;
                        // edge teil
                        t_objRepeat.setMaxRepeatCount(t_objBlockIn.getMax());
                        t_objRepeat.setMinRepeatCount(t_objBlockIn.getMin());
                        if ( t_objEdge.getGlycosidicLinkages().size() > 1 )
                        {
                            throw new SugarImporterException("KCF054", this.m_iPosition);
                        }
                        Linkage t_objNew = new Linkage();
                        for (Iterator<Linkage> t_iterLinkage = t_objEdge.getGlycosidicLinkages().iterator(); t_iterLinkage.hasNext();) 
                        {
                            Linkage t_objLink = t_iterLinkage.next();							
                            t_objNew.setChildLinkages(t_objLink.getChildLinkages());
                            t_objNew.setChildLinkageType(t_objLink.getChildLinkageType());
                        }
                        t_objBlockIn.setRepeatLinkage(t_objNew,t_objResChild.getResidue());
                        t_objRepeat.addNode(t_objResChild.getResidue());
                        this.m_iResidueCount++;
                        t_objBlockIn.setParentGraph(a_objGraph);
                        this.m_aHandledLinkages.add(t_objLinkage);
                        this.addChildResidue(t_objResChild, t_objRepeat);
                    }						
                }					
            }
        }		
    }

    /**
     * @param residue
     * @param resChild
     * @return
     * @throws SugarImporterException 
     */
    private KCFBlock crossBlockOut(KCFResidue a_objParent, KCFResidue a_objChild) throws SugarImporterException 
    {
        KCFBlock t_objResult = null;
        if ( this.m_aBlock.size() == 0 )
        {
            return null;
        }
        double t_dDeltaX = a_objParent.getX() - a_objChild.getX();
        double t_dDelteY = a_objParent.getY() - a_objChild.getY();
        double t_dM = 0;
        if ( t_dDeltaX != 0 && t_dDelteY != 0 )
        {
            t_dM = t_dDelteY / t_dDeltaX;
        }
        double t_dY = a_objParent.getY() - ( t_dM * a_objParent.getX() );
        for (Iterator<KCFBlock> t_iterBlock = this.m_aBlock.iterator(); t_iterBlock.hasNext();) 
        {
            KCFBlock t_objBlock = t_iterBlock.next();
            double t_dLeft = t_objBlock.getLeft();
            double t_dRight = t_objBlock.getRight();
            double t_dLeftUp = t_objBlock.getLeftUp();
            double t_dLeftDown = t_objBlock.getLeftDown();
            double t_dWert = 0;
            if ( t_dLeft > t_dRight )
            {
                t_dLeft = t_dRight;
                t_dLeftUp = t_objBlock.getRightUp();
                t_dLeftDown = t_objBlock.getRightDown();
            }
            if ( t_dLeftUp < t_dLeftDown )
            {
                t_dWert = t_dLeftUp;
                t_dLeftUp = t_dLeftDown;
                t_dLeftDown = t_dWert;
            }
            if ( a_objParent.getX() >= t_dLeft && a_objChild.getX() <= t_dLeft )
            {
                t_dWert = (t_dM * t_dLeft) + t_dY;
                if ( t_dLeftUp >= t_dWert && t_dWert >= t_dLeftDown )
                {
                    if ( t_objResult == null )
                    {
                        t_objResult = t_objBlock;
                    }
                    else
                    {
                        throw new SugarImporterException("KCF051", this.m_iPosition); 
                    }
                }
            }
        }
        return t_objResult;
    }

    /**
     * @param residue
     * @param resChild
     * @return
     * @throws SugarImporterException 
     */
    private KCFBlock crossBlockIn(KCFResidue a_objParent, KCFResidue a_objChild) throws SugarImporterException 
    {
        KCFBlock t_objResult = null;
        if ( this.m_aBlock.size() == 0 )
        {
            return null;
        }
        double t_dDeltaX = a_objParent.getX() - a_objChild.getX();
        double t_dDelteY = a_objParent.getY() - a_objChild.getY();
        double t_dM = 0;
        if ( t_dDeltaX != 0 && t_dDelteY != 0 )
        {
            t_dM = t_dDelteY / t_dDeltaX;
        }
        double t_dY = a_objParent.getY() - ( t_dM * a_objParent.getX() );
        for (Iterator<KCFBlock> t_iterBlock = this.m_aBlock.iterator(); t_iterBlock.hasNext();) 
        {
            KCFBlock t_objBlock = t_iterBlock.next();
            double t_dLeft = t_objBlock.getLeft();
            double t_dRight = t_objBlock.getRight();
            double t_dLeftUp = t_objBlock.getLeftUp();
            double t_dLeftDown = t_objBlock.getLeftDown();
            double t_dWert = 0;
            if ( t_dLeft < t_dRight )
            {
                t_dLeft = t_dRight;
                t_dLeftUp = t_objBlock.getRightUp();
                t_dLeftDown = t_objBlock.getRightDown();
            }
            if ( t_dLeftUp < t_dLeftDown )
            {
                t_dWert = t_dLeftUp;
                t_dLeftUp = t_dLeftDown;
                t_dLeftDown = t_dWert;
            }
            if ( a_objParent.getX() >= t_dLeft && a_objChild.getX() <= t_dLeft )
            {
                t_dWert = (t_dM * t_dLeft) + t_dY;
                if ( t_dLeftUp >= t_dWert && t_dWert >= t_dLeftDown )
                {
                    if ( t_objResult == null )
                    {
                        t_objResult = t_objBlock;
                    }
                    else
                    {
                        throw new SugarImporterException("KCF051", this.m_iPosition); 
                    }
                }
            }
        }
        return t_objResult;
    }
}
