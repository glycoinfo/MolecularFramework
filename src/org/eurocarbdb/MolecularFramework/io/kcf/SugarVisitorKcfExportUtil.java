package org.eurocarbdb.MolecularFramework.io.kcf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eurocarbdb.MolecularFramework.sugar.GlycoEdge;
import org.eurocarbdb.MolecularFramework.sugar.GlycoNode;
import org.eurocarbdb.MolecularFramework.sugar.GlycoconjugateException;
import org.eurocarbdb.MolecularFramework.sugar.Linkage;
import org.eurocarbdb.MolecularFramework.sugar.Monosaccharide;
import org.eurocarbdb.MolecularFramework.sugar.NonMonosaccharide;
import org.eurocarbdb.MolecularFramework.sugar.Substituent;
import org.eurocarbdb.MolecularFramework.sugar.Sugar;
import org.eurocarbdb.MolecularFramework.sugar.SugarUnitAlternative;
import org.eurocarbdb.MolecularFramework.sugar.SugarUnitCyclic;
import org.eurocarbdb.MolecularFramework.sugar.SugarUnitRepeat;
import org.eurocarbdb.MolecularFramework.sugar.UnvalidatedGlycoNode;
import org.eurocarbdb.MolecularFramework.util.traverser.GlycoTraverser;
import org.eurocarbdb.MolecularFramework.util.traverser.GlycoTraverserSimpleForestPostEdge;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitor;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorException;

public class SugarVisitorKcfExportUtil implements GlycoVisitor
{
    private boolean m_reducingEndAddResidue = false;
    private String m_reducingEndName = "molecule";
    private String m_reducingEndLinkage = "1";
    private double m_deltaX = 4.0;
    private double m_deltaY = 4.0;
    private int m_residueCount = 0;
    private int m_linkageCount = 0;
    private HashMap<GlycoNode, ExportResidueKCF> m_hashResidueInformation = new HashMap<GlycoNode, ExportResidueKCF>(); 
    private List<ExportResidueKCF> m_residueInformation = new ArrayList<ExportResidueKCF>();
    private List<ExportLinkageKCF> m_linkageInformation = new ArrayList<ExportLinkageKCF>();
    private HashMap<GlycoNode, ExportAnomer> m_hashAnomer = new HashMap<GlycoNode, ExportAnomer>();
    private HashMap<Double, Boolean> m_hashY = new HashMap<Double, Boolean>();

    public boolean getReducingEndAddResidue()
    {
        return this.m_reducingEndAddResidue;
    }

    public void setReducingEndAddResidue(boolean a_reducingEndAddResidue)
    {
        this.m_reducingEndAddResidue = a_reducingEndAddResidue;
    }

    public String getReducingEndName()
    {
        return this.m_reducingEndName;
    }

    public void setReducingEndName(String a_reducingEndName)
    {
        this.m_reducingEndName = a_reducingEndName;
    }

    public String getReducingEndLinkage()
    {
        return this.m_reducingEndLinkage;
    }

    public void setReducingEndLinkage(String a_reducingEndLinkage)
    {
        this.m_reducingEndLinkage = a_reducingEndLinkage;
    }

    public int getResidueCount() 
    {
        return m_residueCount;
    }

    public void setResidueCount(int a_residueCount) 
    {
        m_residueCount = a_residueCount;
    }

    public int getLinkageCount() 
    {
        return m_linkageCount;
    }

    public void setLinkageCount(int a_linkageCount) 
    {
        m_linkageCount = a_linkageCount;
    }

    public void clear() 
    {
        this.m_hashResidueInformation.clear();
        this.m_residueInformation = new ArrayList<ExportResidueKCF>();
        this.m_linkageInformation = new ArrayList<ExportLinkageKCF>();
        this.m_hashY.clear();
        this.m_residueCount = 0;
        this.m_linkageCount = 0;
    }

    public GlycoTraverser getTraverser(GlycoVisitor a_visitor) throws GlycoVisitorException 
    {
        return new GlycoTraverserSimpleForestPostEdge(a_visitor);
    }

    public void visit(SugarUnitAlternative a_arg0) throws GlycoVisitorException 
    {
        throw new GlycoVisitorException("SugarUnitAlternative are not supported in KCF.");
    }

    public void visit(SugarUnitCyclic a_arg0) throws GlycoVisitorException 
    {
        throw new GlycoVisitorException("SugarUnitCyclic are not supported in KCF.");        
    }

    public void visit(GlycoEdge a_edge) throws GlycoVisitorException 
    {
        // nothing to do
    }

    public void start(Sugar a_sugar) throws GlycoVisitorException 
    {
        this.clear();
        GlycoTraverser t_traverser = this.getTraverser(this);
        try
        {
            if ( a_sugar.getRootNodes().size() != 1 )
            {
                throw new GlycoVisitorException("Unconnected sugars are not supported in KCF.");
            }
        } 
        catch (GlycoconjugateException t_exception)
        {
            throw new GlycoVisitorException(t_exception.getMessage(),t_exception);
        }
        t_traverser.traverseGraph(a_sugar);
        if ( a_sugar.getUndeterminedSubTrees().size() > 0 )
        {
            throw new GlycoVisitorException("Underdeterminded sugars are not supported in KCF.");
        }
    }

    public void visit(SugarUnitRepeat a_repeat) throws GlycoVisitorException 
    {
        throw new GlycoVisitorException("SugarUnitRepeat are not supported in KCF."); 
    }

    public void setResidueInformation(List<ExportResidueKCF> residueInformation)
    {
        this.m_residueInformation = residueInformation;
    }

    public List<ExportResidueKCF> getResidueInformation()
    {
        return m_residueInformation;
    }

    public void setLinkageInformation(List<ExportLinkageKCF> linkageInformation)
    {
        this.m_linkageInformation = linkageInformation;
    }

    public List<ExportLinkageKCF> getLinkageInformation()
    {
        return m_linkageInformation;
    }

    private ExportResidueKCF addResidue(GlycoNode a_node, String a_name, boolean a_handleAnomer)
    {
        if ( a_node.getParentEdge() == null && this.m_reducingEndAddResidue )
        {
            // add the reducing end to maintain anomer
            this.addReducingEnd(a_node,a_name,a_handleAnomer);
        }
        ExportResidueKCF t_residue = this.m_hashResidueInformation.get(a_node); 
        if( t_residue == null )
        {
            this.m_residueCount++;
            t_residue = new ExportResidueKCF();
            t_residue.setId(this.m_residueCount);
            if ( a_handleAnomer )
            {
                t_residue.setName(this.handleAnomer(a_name));
            }
            else
            {
                t_residue.setName(a_name);
            }
            this.m_hashResidueInformation.put(a_node, t_residue);
            this.m_residueInformation.add(t_residue);
        }
        else
        {
            if ( a_handleAnomer )
            {
                t_residue.setName(this.handleAnomer(a_name));
            }
            else
            {
                t_residue.setName(a_name);
            }
        }
        return t_residue;
    }

    private void addReducingEnd(GlycoNode a_node, String a_name, boolean a_handleAnomer)
    {
        // create the parent residue
        this.m_residueCount++;
        ExportResidueKCF t_residue = new ExportResidueKCF();
        t_residue.setId(this.m_residueCount);
        t_residue.setName(this.m_reducingEndName);
        this.m_residueInformation.add(t_residue);
        this.m_hashY.put(Double.valueOf(0), true);
        t_residue.setX(0);
        t_residue.setY(0);
        // create the child residue
        ExportResidueKCF t_residueChild = this.addResidue(a_node, t_residue.getX() - this.m_deltaX, t_residue.getY(),0 );
        // add the linkage
        this.m_linkageCount++;
        ExportLinkageKCF t_linkage = new ExportLinkageKCF();
        t_linkage.setId(this.m_linkageCount);
        String t_parentString = t_residue.getId().toString();
        if ( this.m_reducingEndLinkage != null )
        {
            if ( this.m_reducingEndLinkage.length() > 0 )
            {
                t_parentString += ":" + this.m_reducingEndLinkage;
            }
        }
        String t_childString = t_residueChild.getId().toString();
        ExportAnomer t_anomer = this.m_hashAnomer.get(a_node);
        if ( t_anomer.getAnomer() == null )
        {
            if ( t_anomer.getPosition() != null )
            {
                if ( t_anomer.getPosition() > 0 )
                {
                    t_childString += ":" + t_anomer.getPosition().toString();
                }
            }
        }
        else
        {
            if ( t_anomer.getAnomer().getSymbol().equalsIgnoreCase("x") || t_anomer.getAnomer().getSymbol().equalsIgnoreCase("o") )
            {
                if ( t_anomer.getPosition() != null )
                {
                    if ( t_anomer.getPosition() > 0 )
                    {
                        t_childString += ":" + t_anomer.getPosition().toString();
                    }
                }   
            }
            else
            {
                if ( t_anomer.getPosition() == null )
                {
                    t_childString += ":" + t_anomer.getAnomer().getSymbol();
                }
                else
                {
                    if ( t_anomer.getPosition() > 0 )
                    {
                        t_childString += ":" + t_anomer.getAnomer().getSymbol() + t_anomer.getPosition().toString();
                    }
                    else
                    {
                        t_childString += ":" + t_anomer.getAnomer().getSymbol();
                    }
                }
            }
        }
        t_linkage.setChildString(t_childString);
        t_linkage.setParentString(t_parentString);
        this.m_linkageInformation.add(t_linkage);

    }

    private String handleAnomer(String a_name)
    {
        if ( a_name == null )
        {
            return null;
        }
        if ( a_name.length() > 2 )
        {
            if ( a_name.charAt(1) == '-' && ( a_name.charAt(0) == 'a' || a_name.charAt(0) == 'A' || a_name.charAt(0) == 'b' || a_name.charAt(0) == 'B' ) )
            {
                return a_name.substring(2);
            }
        }
        return a_name;
    }

    private ExportResidueKCF addResidue(GlycoNode a_node, Double a_x, Double a_y, int a_parentRelativ)
    {
        if ( a_parentRelativ != 0 )
        {
            while ( this.m_hashY.get(a_y) != null )
            {
                // collision
                if ( a_parentRelativ < 0 )
                {
                    this.moveUp(a_y);
                    a_y = a_y + this.m_deltaY;
                }
                if ( a_parentRelativ > 0 )
                {
                    this.moveDown(a_y);
                    a_y = a_y - this.m_deltaY;
                }
            }
        }
        this.m_hashY.put(a_y, true);
        this.m_residueCount++;
        ExportResidueKCF t_residue = new ExportResidueKCF();
        t_residue.setId(this.m_residueCount);
        t_residue.setX(a_x);
        t_residue.setY(a_y);
        this.m_hashResidueInformation.put(a_node, t_residue);
        this.m_residueInformation.add(t_residue);
        return t_residue;
    }

    private void moveDown(Double a_y) 
    {
        this.m_hashY = new HashMap<Double, Boolean>();
        for (ExportResidueKCF t_residue : this.m_residueInformation) 
        {
            if ( t_residue.getY() < a_y )
            {
                t_residue.setY(t_residue.getY()-this.m_deltaY);
            }
            this.m_hashY.put(t_residue.getY(), true);
        }      
    }

    private void moveUp(Double a_y) 
    {
        this.m_hashY = new HashMap<Double, Boolean>();
        for (ExportResidueKCF t_residue : this.m_residueInformation) 
        {
            if ( t_residue.getY() > a_y )
            {
                t_residue.setY(t_residue.getY()+this.m_deltaY);
            }
            this.m_hashY.put(t_residue.getY(), true);
        }  
    }

    public void visit(Monosaccharide a_node) throws GlycoVisitorException 
    {
        ExportResidueKCF t_residue = this.addResidue(a_node,a_node.getGlycoCTName(),false);
        this.addChildResidues(a_node,t_residue);
    }

    private void addChildResidues(GlycoNode a_node, ExportResidueKCF a_residue) throws GlycoVisitorException
    {
        int t_childCount = a_node.getChildEdges().size() / 2;
        boolean t_evenNumber = false;
        if ( a_node.getChildEdges().size() % 2 == 0)
        {
            t_evenNumber = true;
        }
        for (GlycoEdge t_edge : a_node.getChildEdges())
        {
            GlycoNode t_node = t_edge.getChild();
            if ( t_evenNumber && t_childCount == 0 )
            {
                t_childCount--;
            }
            ExportResidueKCF t_residueChild = this.addResidue(t_node, a_residue.getX() - this.m_deltaX, a_residue.getY() + (t_childCount * this.m_deltaY),t_childCount );
            t_childCount--;
            this.addEdge(t_edge,a_residue, t_residueChild);
        }
    }

    private void addEdge(GlycoEdge a_edge, ExportResidueKCF a_residue, ExportResidueKCF a_residueChild) throws GlycoVisitorException
    {
        if ( a_edge.getGlycosidicLinkages().size() != 1 )
        {
            throw new GlycoVisitorException("KCf does not support multivalent linked residues.");
        }
        String t_parentInformation = "";
        String t_childInformation = "";
        for (Linkage t_linkage : a_edge.getGlycosidicLinkages()) 
        {
            String t_anomer = null;
            int t_counter = 0;
            // figure out parent linkage position and if anomer is involved
            for (Integer t_position : t_linkage.getParentLinkages())
            {
                if ( t_counter > 1 )
                {
                    t_parentInformation += ",";
                }
                if ( t_position != Linkage.UNKNOWN_POSITION )
                {
                    String t_temp = this.anomerInvolved(a_edge.getParent(),t_position);
                    if ( t_temp != null )
                    {
                        t_anomer = t_temp;
                    }
                    t_parentInformation += t_position.toString();
                }
            }
            if ( t_parentInformation.length() > 0 )
            {
                if ( t_anomer == null )
                {
                    t_parentInformation = ":" + t_parentInformation;
                }
                else if ( !(t_anomer.equalsIgnoreCase("x") || t_anomer.equalsIgnoreCase("o")) )
                {
                    t_parentInformation = ":" +  t_anomer + t_parentInformation;
                }
                else
                {
                    t_parentInformation = ":" + t_parentInformation;
                }
            }
            else
            {
                if ( t_anomer != null )
                {
                    if ( !(t_anomer.equalsIgnoreCase("x") || t_anomer.equalsIgnoreCase("o")) )
                    {
                        t_parentInformation = ":" + t_anomer;
                    }
                }
            }
            t_anomer = null;
            t_counter = 0;
            // child stuff
            for (Integer t_position : t_linkage.getChildLinkages())
            {
                if ( t_counter > 1 )
                {
                    t_childInformation += ",";
                }
                if ( t_position != Linkage.UNKNOWN_POSITION )
                {
                    t_childInformation += t_position.toString();
                }
            }
            if ( t_childInformation.length() > 0 )
            {
                t_childInformation = ":" +  this.findAnomer(a_edge.getChild()) + t_childInformation;
            }
            else
            {
                t_childInformation = ":" + this.findAnomer(a_edge.getChild());
                if ( t_childInformation.length() == 1 )
                {
                    t_childInformation = "";
                }
            }
        }
        this.m_linkageCount++;
        ExportLinkageKCF t_linkage = new ExportLinkageKCF();
        t_linkage.setId(this.m_linkageCount);
        t_linkage.setChildString(a_residueChild.getId().toString() + t_childInformation);
        t_linkage.setParentString(a_residue.getId().toString() + t_parentInformation);
        this.m_linkageInformation.add(t_linkage);
    }

    private String anomerInvolved(GlycoNode a_node, Integer a_position)
    {
        ExportAnomer t_anomer = this.m_hashAnomer.get(a_node);
        if ( t_anomer != null )
        {
            if ( a_position.equals(t_anomer.getPosition()) )
            {
                return t_anomer.getAnomer().getSymbol();
            }
        }
        return null;
    }

    private String findAnomer(GlycoNode a_node)
    {
        ExportAnomer t_anomer = this.m_hashAnomer.get(a_node);
        if ( t_anomer != null )
        {
            String t_string = t_anomer.getAnomer().getSymbol();
            if ( t_string.equalsIgnoreCase("x") || t_string.equalsIgnoreCase("o") )
            {
                return "";
            }
            return t_string; 
        }
        return "";
    }

    public void visit(NonMonosaccharide a_node) throws GlycoVisitorException 
    {
        ExportResidueKCF t_residue = this.addResidue(a_node,a_node.getName(),false);
        this.addChildResidues(a_node,t_residue);
    }

    public void visit(Substituent a_node) throws GlycoVisitorException 
    {
        ExportResidueKCF t_residue = this.addResidue(a_node,a_node.getSubstituentType().getName(),false);
        this.addChildResidues(a_node,t_residue);
    }

    public void visit(UnvalidatedGlycoNode a_node) throws GlycoVisitorException 
    {
        if ( this.m_hashAnomer.get(a_node) != null )
        {
            ExportResidueKCF t_residue = this.addResidue(a_node,a_node.getName(),true);
            this.addChildResidues(a_node,t_residue);
        }
        else
        {
            ExportResidueKCF t_residue = this.addResidue(a_node,a_node.getName(),false);
            this.addChildResidues(a_node,t_residue);
        }
    }

    public void setAnomerHash(HashMap<GlycoNode, ExportAnomer> a_hashAnomer)
    {
        this.m_hashAnomer = a_hashAnomer;
    }
}