package org.eurocarbdb.MolecularFramework.io.carbbank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.eurocarbdb.MolecularFramework.io.SugarExporter;
import org.eurocarbdb.MolecularFramework.io.SugarExporterException;
import org.eurocarbdb.MolecularFramework.sugar.GlycoEdge;
import org.eurocarbdb.MolecularFramework.sugar.GlycoNode;
import org.eurocarbdb.MolecularFramework.sugar.GlycoconjugateException;
import org.eurocarbdb.MolecularFramework.sugar.Linkage;
import org.eurocarbdb.MolecularFramework.sugar.Sugar;
import org.eurocarbdb.MolecularFramework.sugar.SugarUnitCyclic;
import org.eurocarbdb.MolecularFramework.sugar.SugarUnitRepeat;
import org.eurocarbdb.MolecularFramework.sugar.UnvalidatedGlycoNode;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorException;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorNodeType;

public class SugarExporterCarbBank extends SugarExporter {
    
    private String sugarName = "";
    private boolean optimizeSpacing = true;
    private GlycoNode rootNode = null;
    private int nextRepeatLetter = 110;     //starting with ascii value of 'n'
    private String cyclicHead = "";
    private GlycoNode repeatHead = null;
    private GlycoEdge repeatEdge = null;
    private HashMap<GlycoNode,GlycoNode> additionalChild = new HashMap<GlycoNode,GlycoNode>();
    private HashMap<GlycoNode, String> repeatStart = new HashMap<GlycoNode,String>();
    private HashMap<GlycoNode, String> repeatEnd = new HashMap<GlycoNode,String>();
    private GlycoVisitorNodeType m_visitor = new GlycoVisitorNodeType();

    private void clear() {
        this.sugarName = "";
        this.rootNode = null;
        this.nextRepeatLetter = 110;
        this.cyclicHead = "";
        this.repeatHead = null;
        this.repeatEdge = null;
        this.additionalChild = new HashMap<GlycoNode,GlycoNode>();
        this.repeatStart = new HashMap<GlycoNode,String>();
        this.repeatEnd = new HashMap<GlycoNode,String>();
        this.m_visitor = new GlycoVisitorNodeType();
    }



    @Override
    public String export(Sugar sugar) throws SugarExporterException {
        this.clear();
        try {
            if(sugar.getRootNodes().size() != 1) {
                throw new SugarExporterException("More than 1 Root Node is not supported.");
            }
            else if(!sugar.getUndeterminedSubTrees().isEmpty()) {
                throw new SugarExporterException("UndeterminedSubtrees are not supported.");
            }
            else {
                Sugar sugarCopy = sugar.copy();
                this.rootNode = sugarCopy.getNodeIterator().next();
                PrintFormatTree sugarPrintFormatTree = this.startBuildingPrintFormatTree(this.rootNode, "");

                List<String> rows = new ArrayList<String>();
                int index = 0;
                while (index < sugarPrintFormatTree.getTotalLength()) {
                    rows.add(index, "");
                    index++;
                }

                String lastLine = sugarPrintFormatTree.getRootName();
                sugarPrintFormatTree.setRootName(lastLine + this.cyclicHead);
                sugarPrintFormatTree.setMaxWidth(sugarPrintFormatTree.getMaxWidth() + this.cyclicHead.length());
                rows = this.finishFormatTree(rows, sugarPrintFormatTree, 0, sugarPrintFormatTree.getMaxWidth());

                index = 0;
                String thisLine;
                while(index < rows.size()) {
                    thisLine = rows.get(index);
                    if(!thisLine.isEmpty())
                        this.sugarName = this.sugarName + thisLine + "\n";
                    index++;
                }
            }
            

        } catch (GlycoconjugateException ex) {
            throw new SugarExporterException(ex.getMessage(), ex);
        } catch (SugarExporterException ex) {
            throw new SugarExporterException(ex.getMessage(), ex);
        } catch (GlycoVisitorException ex) {
            throw new SugarExporterException(ex.getMessage(), ex);
        }
        return this.sugarName;
    }

    private PrintFormatTree startBuildingPrintFormatTree(GlycoNode node, String openingParen) throws SugarExporterException, GlycoVisitorException, GlycoconjugateException {
        PrintFormatTree printFormatForThisNode = new PrintFormatTree();
        UnvalidatedGlycoNode gNode = this.m_visitor.getUnvalidatedNode(node);
        SugarUnitRepeat sugarRepeat = this.m_visitor.getSugarUnitRepeat(node);
        SugarUnitCyclic sugarCyclic = this.m_visitor.getSugarUnitCyclic(node);

        if(gNode == null && sugarRepeat == null && sugarCyclic == null) {
            throw new SugarExporterException("Residue other than UnvalidatedGlycoNode or SugarUnitRepeat or SugarUnitCyclic is not supported.");
        }
        GlycoNodeEquivalent gNEquivalent = this.getNameOfNode(node);
        String nameArray = gNEquivalent.getName()+openingParen;

        if(gNEquivalent.getCanTraverse()) {
            Integer rootLength = 1;
            Integer upwardLines = 0;
            Integer downwardLines = rootLength -1;
            Integer maxWidth = nameArray.length();

            Integer maxChildWidth = 0;

            PrintFormatTree childPrintFormatting;
            boolean firstChild = true;
            if(this.additionalChild.containsKey(node)) {
                childPrintFormatting = this.startBuildingPrintFormatTree(this.additionalChild.get(node), "");
                printFormatForThisNode.setFirstChild(childPrintFormatting);

                upwardLines = upwardLines + childPrintFormatting.getUpwardLines();
                if(downwardLines < childPrintFormatting.getDownwardLines()) {
                    downwardLines = childPrintFormatting.getDownwardLines();
                }

                if(maxChildWidth < childPrintFormatting.getMaxWidth()) {
                    maxChildWidth = childPrintFormatting.getMaxWidth() + 1;
                }

                firstChild = false;
            }

            List<GlycoNode> childNodes = node.getChildNodes();
            Collections.sort(childNodes, new ComparatorChild());
            boolean upwardLine = true;

            for(GlycoNode childNode : childNodes) {
                childPrintFormatting = this.startBuildingPrintFormatTree(childNode, "");

                if(firstChild) {
                    
                    printFormatForThisNode.setFirstChild(childPrintFormatting);
                    
                    upwardLines = upwardLines + childPrintFormatting.getUpwardLines();
                    if(downwardLines < childPrintFormatting.getDownwardLines()) {
                        downwardLines = childPrintFormatting.getDownwardLines();
                    }
                    
                    if(maxChildWidth < childPrintFormatting.getMaxWidth()) {
                        maxChildWidth = childPrintFormatting.getMaxWidth() + 1;
                    }
                    
                    firstChild = false;
                }
                else {
                    if(maxChildWidth < childPrintFormatting.getMaxWidth()) {
                        maxChildWidth = childPrintFormatting.getMaxWidth();
                    }
                    if(upwardLine) {
                        upwardLines = upwardLines + childPrintFormatting.getTotalLength() + 1;
                        printFormatForThisNode.addUpperChildren(childPrintFormatting);
                    }
                    else {
                        downwardLines = downwardLines + childPrintFormatting.getTotalLength() + 1;
                        printFormatForThisNode.addDownChildren(childPrintFormatting);
                    }
                    upwardLine = !upwardLine;
                }
                
            }

            int totalLength = upwardLines + downwardLines + rootLength;
            maxWidth = maxWidth + maxChildWidth;

            printFormatForThisNode.setRootName(nameArray);
            printFormatForThisNode.setRootLength(rootLength);
            printFormatForThisNode.setTotalLength(totalLength);
            printFormatForThisNode.setUpwardLines(upwardLines);
            printFormatForThisNode.setDownwardLines(downwardLines);
            printFormatForThisNode.setMaxWidth(maxWidth);
            return printFormatForThisNode;
        }
        else if(!gNEquivalent.getCanTraverse()) {
            return this.startBuildingPrintFormatTree(sugarRepeat.getRepeatLinkage().getChild(), openingParen);
        }
        
        throw new SugarExporterException("Only UnValidated Glyconode Repeats and Cyclics are supported.");
    }
    
    private GlycoNodeEquivalent getNameOfNode(GlycoNode node) throws SugarExporterException, GlycoVisitorException, GlycoconjugateException {
        UnvalidatedGlycoNode gNode = this.m_visitor.getUnvalidatedNode(node);
        SugarUnitRepeat sugarRepeat = this.m_visitor.getSugarUnitRepeat(node);
        SugarUnitCyclic sugarCyclic = this.m_visitor.getSugarUnitCyclic(node);
        GlycoNodeEquivalent gNEquivalent = new GlycoNodeEquivalent();
        if(gNode != null ) {
            String name = "";

            if(node.getParentEdge() != null || this.repeatStart.containsKey(node)) {
                if(this.repeatStart.containsKey(node)) {
                    name = this.repeatStart.get(node);
                    this.repeatStart.remove(node);
                }
                else {
                    if(this.repeatEnd.containsKey(node)) {
                        name = this.repeatEnd.get(node);
                        this.repeatEnd.remove(node);
                    }
                    name = "-" + name + this.getLinkagesInFormat(node.getParentEdge().getGlycosidicLinkages().iterator().next().getParentLinkages()) + ")";
                    name = "-(" + this.getLinkagesInFormat(node.getParentEdge().getGlycosidicLinkages().iterator().next().getChildLinkages()) + name;
                }
            }
            else if(this.repeatHead != null) {
                if(this.repeatHead.equals(node)) {
                    name = "-(" + this.getLinkagesInFormat(this.repeatEdge.getGlycosidicLinkages().iterator().next().getChildLinkages()) + "-" + name;
                }
            }
            name = gNode.getName() + name;
            
            gNEquivalent.setName(name);
            gNEquivalent.setCanTraverse(true);
        }
        else if(sugarCyclic != null) {
            GlycoNode rootNodeCycle = sugarCyclic.getCyclicStart();
            if(rootNodeCycle.equals(this.rootNode)) {
                this.cyclicHead = "-(" + this.getLinkagesInFormat(sugarCyclic.getParentEdge().getGlycosidicLinkages().iterator().next().getChildLinkages()) + "-";
                String nodeName = "cyclic-" + this.getLinkagesInFormat(sugarCyclic.getParentEdge().getGlycosidicLinkages().iterator().next().getParentLinkages()) + ")";
                gNEquivalent.setName(nodeName);
                gNEquivalent.setCanTraverse(true);
            }
            else {
                throw new SugarExporterException("Only Cyclics connected with root node are supported");
            }
        }
        else if(sugarRepeat != null){
            if(sugarRepeat.getMaxRepeatCount()!=sugarRepeat.getMinRepeatCount()) {
                throw new SugarExporterException("Min. and Max. Repeat Count has to be same.");
            }
            else if(sugarRepeat.getUndeterminedSubTrees().isEmpty()) {
                GlycoEdge parentEdge = node.getParentEdge();
                ArrayList<GlycoNode> children = sugarRepeat.getChildNodes();
                if(children.size() > 1)
                    throw new SugarExporterException("More than one child for Repeat is not supported");
                
                if(parentEdge != null || children.size() == 1) {
                    if(parentEdge == null ){
                        if(!children.isEmpty())
                            throw new SugarExporterException("Repeat has child but no parent.");
                    }
                    if(node.getParentEdge().getGlycosidicLinkages().size() == 1 && children.size()==1) {
                        GlycoNode repeatLinkageParent = sugarRepeat.getRepeatLinkage().getParent();
                        GlycoNode repeatLinkageChild = sugarRepeat.getRepeatLinkage().getChild();
                        
                        String repeatCount;
                        if(sugarRepeat.getMaxRepeatCount() > 0) {
                            repeatCount = Integer.toString(sugarRepeat.getMaxRepeatCount()) + "x";
                        }
                        else {
                            repeatCount = Character.toString((char)this.nextRepeatLetter);
                            this.nextRepeatLetter = this.nextRepeatLetter + 1;
                        }
                        
                        String openingParen = "-(" + this.getLinkagesInFormat(sugarRepeat.getParentEdge().getGlycosidicLinkages().iterator().next().getChildLinkages()) + "]" + repeatCount;
                        
                        if(this.repeatStart.containsKey(node)) {
                            openingParen = openingParen + this.repeatStart.get(node);
                            this.repeatStart.remove(node);
                        }
                        openingParen = openingParen + "-";
                        
                        if(this.repeatEnd.containsKey(node)) {
                            openingParen = openingParen + this.repeatEnd.get(node);
                            this.repeatEnd.remove(node);
                        }
                        
                        openingParen = openingParen + this.getLinkagesInFormat(sugarRepeat.getParentEdge().getGlycosidicLinkages().iterator().next().getParentLinkages()) + ")";
                        
                        this.repeatStart.put(repeatLinkageChild, openingParen);
                        
                        String closingParen = "[";
                        if(this.repeatEnd.containsKey(node)) {
                            closingParen = this.repeatEnd.get(node)+ closingParen;
                        }
                        this.repeatEnd.put(sugarRepeat.getChildNodes().iterator().next(), closingParen);
                        
                        gNEquivalent.setCanTraverse(false);
                        this.additionalChild.put(repeatLinkageParent, children.iterator().next());
                    }
                    else {
                        if(parentEdge != null ){
                            if(children.isEmpty()) {
                                throw new SugarExporterException("Repeat has parent but no child.");
                            }
                        }
                    }
                }
                else if(parentEdge == null && sugarRepeat.getChildEdges().isEmpty()) {
                    this.repeatHead = sugarRepeat.getRepeatLinkage().getChild();
                    this.repeatEdge = sugarRepeat.getRepeatLinkage();
                    UnvalidatedGlycoNode childNode = new UnvalidatedGlycoNode();
                    childNode.setName("Repeat-" + this.getLinkagesInFormat(this.repeatEdge.getGlycosidicLinkages().iterator().next().getParentLinkages()) + ")" );
                    
                    gNEquivalent.setCanTraverse(false);
                    this.additionalChild.put(repeatEdge.getParent(), childNode);
                }
                else {
                    throw new SugarExporterException("Either it should have one parent and one child");
                }
            }
            else
                throw new SugarExporterException("UndeterminedSubtrees are not supported.");
        }
        return gNEquivalent;
    }

    private String getLinkagesInFormat(ArrayList<Integer> parentLinkages) {
        String geName = "";
        boolean first = true;
        for(Integer parentLinkage : parentLinkages) {
            if(!first)
                geName = "/" + geName;
            geName = this.getParentLinkageInFormat(parentLinkage) + geName;
            first = false;
        }
        return geName;
    }

    private String getParentLinkageInFormat(Integer parentLinkage) {
        String parentLinkagePos;
        if (parentLinkage == Linkage.UNKNOWN_POSITION) {
            parentLinkagePos = "?";
        }
        else
            parentLinkagePos = parentLinkage.toString();
        
        return parentLinkagePos;
    }

    private List<String> finishFormatTree(List<String> rows, PrintFormatTree sugarPrintFormatTree, Integer uppermostLine, Integer endpoint) {
        
        int childEndpoint = endpoint - sugarPrintFormatTree.getRootName().length();
        int rootLine = uppermostLine + sugarPrintFormatTree.getUpwardLines();
        String adjustedNameWithSpace = this.adjustNameWithSpaces(sugarPrintFormatTree.getRootName(), endpoint);
        String alreadyPlacedString = rows.get(rootLine);

        if(alreadyPlacedString.length() > 0) {
            if(alreadyPlacedString.length() > endpoint) {
                rows.set(rootLine, alreadyPlacedString.substring(0, endpoint-sugarPrintFormatTree.getRootName().length()) + sugarPrintFormatTree.getRootName() + alreadyPlacedString.substring(endpoint, alreadyPlacedString.length()));
            }
            else {
                rows.set(rootLine, alreadyPlacedString+ this.fillSpaces (endpoint- sugarPrintFormatTree.getRootName().length() - alreadyPlacedString.length()) +adjustedNameWithSpace.substring(endpoint- sugarPrintFormatTree.getRootName().length(), adjustedNameWithSpace.length()));
            }
        }
        else {
            rows.set(rootLine, adjustedNameWithSpace);
        }
        
        if(sugarPrintFormatTree.getFirstChild() != null) {

            PrintFormatTree firstChild = sugarPrintFormatTree.getFirstChild();
            int childBaseLine = uppermostLine + sugarPrintFormatTree.getUpwardLines() - firstChild.getUpwardLines();
            String lastLine = firstChild.getRootName() + "-";
            String rootName = firstChild.getRootName();
            rootName = lastLine;
            firstChild.setRootName(rootName);
            rows = this.finishFormatTree(rows, firstChild, childBaseLine, childEndpoint);
            childEndpoint++;
            int childRootLine;
            int childEndpointUp = childEndpoint;
            int childEndpointDown = childEndpoint;
            int adjustLines = 0;
            //childBaseLine = childBaseLine - firstChild.getUpwardLines()-1;
            for(PrintFormatTree upwardChild : sugarPrintFormatTree.getUpperChildren()) {
                childBaseLine = childBaseLine - upwardChild.getTotalLength() - 1;
                childRootLine = childBaseLine + upwardChild.getTotalLength() - upwardChild.getDownwardLines() -1;
                if(this.optimizeSpacing) {
                    adjustLines = this.getAdjustedBaseLine(rows, rootLine, upwardChild, childRootLine, childEndpointUp);
                    childBaseLine = childBaseLine + adjustLines;
                    childRootLine = childRootLine + adjustLines;
                }
                rootName = upwardChild.getRootName() + "+";
                upwardChild.setRootName(rootName);
                rows = this.finishFormatTree(rows, upwardChild, childBaseLine, childEndpointUp);
                rows = this.fillDashes(rows, childRootLine, rootLine, childEndpointUp);
                childEndpointUp+=2;
            }
 
            childBaseLine = uppermostLine + sugarPrintFormatTree.getUpwardLines() + sugarPrintFormatTree.getRootLength() + firstChild.getDownwardLines() + 1;

            for(PrintFormatTree downwardChild : sugarPrintFormatTree.getDownChildren()) {
                lastLine = downwardChild.getRootName()+"+";
                rootName = downwardChild.getRootName();
                rootName =  lastLine;
                downwardChild.setRootName(rootName);
                childRootLine = childBaseLine + downwardChild.getTotalLength() - downwardChild.getDownwardLines() -1;
                if(this.optimizeSpacing) {
                    adjustLines = this.getAdjustedBaseLine(rows, rootLine, downwardChild, childRootLine, childEndpointDown);
                    childBaseLine = childBaseLine + adjustLines;
                    childRootLine = childRootLine + adjustLines;
                }
                rows = this.finishFormatTree(rows, downwardChild, childBaseLine, childEndpointDown);
                rows = this.fillDashes(rows, rootLine, childRootLine, childEndpointDown);
                childBaseLine = childBaseLine + downwardChild.getTotalLength() + 1;
                childEndpointDown+=2;
                //childBaseLine = childBaseLine + downwardChild.getUpwardLines()+1;
                
            }
        }
        return rows;
    }

    private String adjustNameWithSpaces(String lastLine, Integer maxWidth) {
        int numberOfSpacesInFront = maxWidth - lastLine.length();
        lastLine = this.fillSpaces(numberOfSpacesInFront) + lastLine;
        return lastLine;
    }

    private List<String> fillDashes(List<String> rows, int topRow, int bottomRow, int column) {
        int index = topRow+1;
        int columnIndex;
        while(index < bottomRow) {
            String thisLine = rows.get(index);
            if(thisLine.isEmpty()) {
                thisLine = " ";
            }
            columnIndex = thisLine.length();
            if(columnIndex < column) {
                while(columnIndex < column) {
                    thisLine = thisLine + " ";
                    columnIndex++;
                }
                thisLine = thisLine.substring(0, columnIndex-1)  + "|";
            }
            else {
                thisLine = thisLine.substring(0, column-2) + "|" + thisLine.substring(column, columnIndex);
            }
            rows.set(index, thisLine);
            index ++;
        }
        return rows;
    }

    private int getAdjustedBaseLine(List<String> rows, int rootLine, PrintFormatTree child, int childBaseLine, int childStartPoint) {

        boolean goDown = true;
        int adjustLines = 0;
        if(childBaseLine > rootLine) {
            goDown = false;
        }

        int maxWidth = 0;
        int shiftedChildBaseLine = childBaseLine;
        int shiftWidth = 0;
        if(goDown) {
            shiftWidth = child.getDownwardLines();
        }
        else {
            shiftWidth = -1*child.getUpwardLines();
        }
        
        maxWidth = child.getMaxWidth();
        int childEndPoint = childStartPoint - maxWidth;
        shiftedChildBaseLine = childBaseLine + shiftWidth;
        
        if(goDown) {
            while(shiftedChildBaseLine < rootLine - 2) {
                if(rows.get(shiftedChildBaseLine + 2).length() < childEndPoint -1) {
                    adjustLines = adjustLines + 2;
                    shiftedChildBaseLine = shiftedChildBaseLine + 2;
                }
                else {
                    break;
                }
            }
        }
        else {
            while(shiftedChildBaseLine > rootLine + 2) {
                if(rows.get(shiftedChildBaseLine - 2).length() < childEndPoint -1) {
                    adjustLines = adjustLines - 2;
                    shiftedChildBaseLine = shiftedChildBaseLine - 2;
                }
                else {
                    break;
                }
            }
        }
        return adjustLines;
    }

    private String fillSpaces(int numberOfSpaces) {
        int insertedSpace = 0;
        String lastLine = "";
        while(insertedSpace < numberOfSpaces) {
            lastLine = " " + lastLine;
            insertedSpace++;
        }
        return lastLine;
    }

}
