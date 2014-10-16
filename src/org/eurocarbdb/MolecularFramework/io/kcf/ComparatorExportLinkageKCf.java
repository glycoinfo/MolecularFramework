package org.eurocarbdb.MolecularFramework.io.kcf;

import java.util.Comparator;

public class ComparatorExportLinkageKCf implements Comparator<ExportLinkageKCF> 
{
    public int compare(ExportLinkageKCF a_linkage1, ExportLinkageKCF a_linkage2) 
    {
        return a_linkage1.getId().compareTo(a_linkage2.getId());
    }
}
