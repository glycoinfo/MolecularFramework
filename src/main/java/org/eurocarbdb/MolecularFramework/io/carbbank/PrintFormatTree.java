package org.eurocarbdb.MolecularFramework.io.carbbank;

import java.util.ArrayList;
import java.util.List;

public class PrintFormatTree {
    private String rootName = "";
    private List<PrintFormatTree> firstChild = new ArrayList<PrintFormatTree>();
    private List<PrintFormatTree> upperChildren = new ArrayList<PrintFormatTree>();
    private List<PrintFormatTree> downChildren = new ArrayList<PrintFormatTree>();
    private Integer rootLength;
    private Integer firstChildLength;
    private Integer upwardLines;
    private Integer downwardLines;
    private Integer totalLength;
    private Integer maxWidth;

    public String getRootName() {
        return rootName;
    }

    public void setRootName(String rootName) {
        this.rootName = rootName;
    }

    public PrintFormatTree getFirstChild() {
        if(!this.firstChild.isEmpty()) {
            return firstChild.get(0);
        }
        else 
            return null;
    }

    public void setFirstChild(PrintFormatTree firstChild) {
        this.firstChild.add(0, firstChild);
    }

    public List<PrintFormatTree> getUpperChildren() {
        return upperChildren;
    }

    public void setUpperChildren(List<PrintFormatTree> upperChildren) {
        this.upperChildren = upperChildren;
    }

    public void addUpperChildren(PrintFormatTree upperChild) {
        this.upperChildren.add(this.upperChildren.size(), upperChild);
    }

    public List<PrintFormatTree> getDownChildren() {
        return downChildren;
    }

    public void setDownChildren(List<PrintFormatTree> downChildren) {
        this.downChildren = downChildren;
    }

    public void addDownChildren(PrintFormatTree downChild) {
        this.downChildren.add(this.downChildren.size(), downChild);
    }

    public Integer getRootLength() {
        return rootLength;
    }

    public void setRootLength(Integer rootLength) {
        this.rootLength = rootLength;
    }

    public Integer getFirstChildLength() {
        return firstChildLength;
    }

    public void setFirstChildLength(Integer firstChildLength) {
        this.firstChildLength = firstChildLength;
    }

    public Integer getUpwardLines() {
        return upwardLines;
    }

    public void setUpwardLines(Integer upwardLines) {
        this.upwardLines = upwardLines;
    }

    public Integer getDownwardLines() {
        return downwardLines;
    }

    public void setDownwardLines(Integer downwardLines) {
        this.downwardLines = downwardLines;
    }

    public Integer getTotalLength() {
        return totalLength;
    }

    public void setTotalLength(Integer totalLength) {
        this.totalLength = totalLength;
    }

    public Integer getMaxWidth() {
        return maxWidth;
    }

    public void setMaxWidth(Integer maxWidth) {
        this.maxWidth = maxWidth;
    }

}