package org.eurocarbdb.MolecularFramework.io.kcf;

import java.util.Comparator;

public class ComparatorExportResidueKCf implements Comparator<ExportResidueKCF> 
{
    public int compare(ExportResidueKCF a_residue1, ExportResidueKCF a_residue2) 
    {
        return a_residue1.getId().compareTo(a_residue2.getId());
    }

}
