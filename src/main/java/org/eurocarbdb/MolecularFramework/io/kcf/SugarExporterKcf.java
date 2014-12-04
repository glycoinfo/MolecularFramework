package org.eurocarbdb.MolecularFramework.io.kcf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.eurocarbdb.MolecularFramework.io.SugarExporter;
import org.eurocarbdb.MolecularFramework.io.SugarExporterException;
import org.eurocarbdb.MolecularFramework.sugar.GlycoNode;
import org.eurocarbdb.MolecularFramework.sugar.Sugar;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorException;

//ENTRY       G00287                      Glycan
//NODE        3
//            1   Man         5     0
//            2   Man        -5     5
//            3   Man        -5    -5
//EDGE        2
//            1     2:a1    1:6  
//            2     3:a1    1:3  
/////

public class SugarExporterKcf extends SugarExporter 
{
    private boolean m_reducingEndAddResidue = false;
    private String m_reducingEndName = "molecule";
    private String m_reducingEndLinkage = "1";
    private String m_id = "G99999";
    private HashMap<GlycoNode, ExportAnomer> m_hashAnomer = new HashMap<GlycoNode, ExportAnomer>();

    public String export(Sugar a_sugar) throws SugarExporterException
    {
        int t_iResidueCount = 0;
        int t_iLinkageCount = 0;
        List<ExportResidueKCF> t_residueInformation = new ArrayList<ExportResidueKCF>(); 
        List<ExportLinkageKCF> t_linkageInformation = new ArrayList<ExportLinkageKCF>();
        
        StringBuffer t_kcf = new StringBuffer("");
        try
        {
            SugarVisitorKcfExportUtil t_visitor = new SugarVisitorKcfExportUtil();
            t_visitor.setReducingEndAddResidue(this.m_reducingEndAddResidue);
            t_visitor.setReducingEndLinkage(this.m_reducingEndLinkage);
            t_visitor.setReducingEndName(this.m_reducingEndName);
            t_visitor.setAnomerHash(this.m_hashAnomer);
            t_visitor.start(a_sugar);
            t_iLinkageCount = t_visitor.getLinkageCount();
            t_iResidueCount = t_visitor.getResidueCount();
            t_residueInformation = t_visitor.getResidueInformation();
            t_linkageInformation = t_visitor.getLinkageInformation();
            Collections.sort(t_residueInformation,new ComparatorExportResidueKCf());
            Collections.sort(t_linkageInformation,new ComparatorExportLinkageKCf());
            t_kcf.append("ENTRY       ");
            t_kcf.append(this.m_id);
            t_kcf.append("                      Glycan\n");
            t_kcf.append("NODE        ");
            t_kcf.append(t_iResidueCount);
            t_kcf.append("\n");
            for (ExportResidueKCF t_residue : t_residueInformation) 
            {
                t_kcf.append("            ");
                t_kcf.append(t_residue.getId());
                t_kcf.append("         ");
                t_kcf.append(t_residue.getName());
                t_kcf.append("   ");
                t_kcf.append(t_residue.getX());
                t_kcf.append("     ");
                t_kcf.append(t_residue.getY());
                t_kcf.append("\n");
            }
            t_kcf.append("EDGE        ");
            t_kcf.append(t_iLinkageCount);
            t_kcf.append("\n");
            for (ExportLinkageKCF t_linkage : t_linkageInformation) 
            {
                t_kcf.append("            ");
                t_kcf.append(t_linkage.getId());
                t_kcf.append("         ");
                t_kcf.append(t_linkage.getChildString());
                t_kcf.append("    ");
                t_kcf.append(t_linkage.getParentString());
                t_kcf.append("\n");
            }
            t_kcf.append("///");
        } 
        catch (GlycoVisitorException t_exception)
        {
            throw new SugarExporterException(t_exception.getMessage(),t_exception);
        }
        return t_kcf.toString();
    }

    public void setHashAnomer(HashMap<GlycoNode, ExportAnomer> hashAnomer)
    {
        this.m_hashAnomer = hashAnomer;
    }

    public HashMap<GlycoNode, ExportAnomer> getHashAnomer()
    {
        return m_hashAnomer;
    }

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

    public String getId()
    {
        return this.m_id;
    }

    public void setId(String a_id)
    {
        this.m_id = a_id;
    }
}