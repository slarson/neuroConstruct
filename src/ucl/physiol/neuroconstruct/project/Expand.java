/**
 *  neuroConstruct
 *  Software for developing large scale 3D networks of biologically realistic neurons
 * 
 *  Copyright (c) 2009 Padraig Gleeson
 *  UCL Department of Neuroscience, Physiology and Pharmacology
 *
 *  Development of this software was made possible with funding from the
 *  Medical Research Council and the Wellcome Trust
 *  
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.

 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package ucl.physiol.neuroconstruct.project;

import ucl.physiol.neuroconstruct.mechanisms.CellMechanism;
import ucl.physiol.neuroconstruct.mechanisms.ChannelMLCellMechanism;
import ucl.physiol.neuroconstruct.mechanisms.ChannelMLException;
import ucl.physiol.neuroconstruct.utils.*;

import java.io.*;
import java.util.Vector;
import ucl.physiol.neuroconstruct.cell.*;
import ucl.physiol.neuroconstruct.cell.utils.CellTopologyHelper;
import ucl.physiol.neuroconstruct.mechanisms.XMLMechanismException;

/**
 * Class for generating HTML representation of neuroConstruct project
 *
 * @author Padraig Gleeson
 *  
 */

public class Expand
{
    private static ClassLogger logger = new ClassLogger("Expand");
    
    static String CELL_TYPES = "cellTypes";
    static String CELL_MECHANISMS = "cellMechanisms";
    
    public Expand()
    {
    }


    public static void generateProjectView(Project project, File dir)
    {

    }
    
    public static String getItemPage(String origName)
    {
    	return GeneralUtils.replaceAllTokens(origName, " ", "_")+".html";
    }
    

    public static String getCellTypePage(String cellTypeName)
    {
    	return CELL_TYPES+"/"+getItemPage(cellTypeName);
    }
    public static String getCellMechPage(String cellMechName)
    {
    	return CELL_MECHANISMS+"/"+getItemPage(cellMechName);
    }

    public static void generateMainPage(Project project, File mainHtmlFile)
    {
        SimpleHtmlDoc mainPage = new SimpleHtmlDoc();

        mainPage.setMainFontSize(10);

        mainPage.addTaggedElement("neuroConstruct project: "+ project.getProjectName(), "h1");

        mainPage.addTaggedElement(""+ handleWhitespaces(project.getProjectDescription()), "p");

        mainPage.addTaggedElement("Simulation configurations", "h2");

        for (String sc: project.simConfigInfo.getAllSimConfigNames())
        {
            String scFile = getItemPage(sc);
            mainPage.addTaggedElement(mainPage.getLinkedText(sc, scFile), "p");
        }


        mainPage.addTaggedElement("Cell Types present in the project", "h2");

        Vector<Cell> cells = project.cellManager.getAllCells();


    	mainPage.addRawHtml("<table border=\"1\" width ='300'>");
    	
        for (Cell cell: cells)
        {
        	mainPage.addRawHtml("<tr><td>"+cell.getInstanceName());
        	mainPage.addRawHtml("</td><td>"+cell.getCellDescription()+"</td><td>");
        	
        	String cellPageLoc = getCellTypePage(cell.getInstanceName());
        	mainPage.addRawHtml("<a href = "+cellPageLoc	+">Cell details</a></td></tr>");


            SimpleHtmlDoc cellPage = new SimpleHtmlDoc();
            
            cellPage.addRawHtml(CellTopologyHelper.printDetails(cell, project, true, true, true));


            cellPage.saveAsFile(new File(mainHtmlFile.getParentFile(), cellPageLoc));
            
        }
    	mainPage.addRawHtml("</table>");

        mainPage.addTaggedElement("Cell Mechanisms present in the project", "h2");

    	mainPage.addRawHtml("<table>");
        
        Vector<String> cellMechs = project.cellMechanismInfo.getAllCellMechanismNames();

        for (String cmName: cellMechs)
        {
        	CellMechanism cm = project.cellMechanismInfo.getCellMechanism(cmName);
            

        	String cmPageLoc = getCellMechPage(cm.getInstanceName());
        	
        	mainPage.addRawHtml("<tr><td>"+cm.getInstanceName());
        	mainPage.addRawHtml("</td><td>"+cm.getDescription()+"</td><td>");
        	

        	mainPage.addRawHtml("</td><td>"+cm.getMechanismModel()+"</td><td>");
        	mainPage.addRawHtml("</td><td>"+cm.getMechanismType()+"</td><td>");
        	
        	mainPage.addRawHtml("<a href = "+cmPageLoc	+">Full details</a></td></tr>");

            File xslDoc = GeneralProperties.getChannelMLReadableXSL();

        	
        	
            SimpleHtmlDoc cmPage = new SimpleHtmlDoc();
            
            if (cm instanceof ChannelMLCellMechanism)
            {
            	ChannelMLCellMechanism cmlCm = (ChannelMLCellMechanism)cm;
            	

                try 
                {
                	String readable = XMLUtils.transform(cmlCm.getXMLDoc().getXMLString("", false),xslDoc);

                	cmPage.addRawHtml(readable);
                } 
                catch (XMLMechanismException e)
				{
                	cmPage.addTaggedElement("Unable to generate HTML representation of: "+ cm.getInstanceName(), "b");
				}
            	String cmXmlPageLoc = getCellMechPage(cm.getInstanceName()+".channelml");

                SimpleHtmlDoc cmXmlPage = new SimpleHtmlDoc();
                
                
                try 
                {
                	cmXmlPage.addRawHtml(cmlCm.getXMLDoc().getXMLString("", true));
				} 
                catch (XMLMechanismException e)
				{
					cmXmlPage.addTaggedElement("Unable to generate ChannelML representation of: "+ cm.getInstanceName(), "b");
				}

                cmXmlPage.saveAsFile(new File(mainHtmlFile.getParentFile(), cmXmlPageLoc));
                
            	mainPage.addRawHtml("</td><td><a href = "+cmXmlPageLoc	+">ChannelML file</td><td>");
            }
            else
            {
            	
            }
            

        	cmPage.saveAsFile(new File(mainHtmlFile.getParentFile(), cmPageLoc));
        	
        }
        
    	mainPage.addRawHtml("</table>");

        mainPage.saveAsFile(mainHtmlFile);
        
    }
    
    public static String handleWhitespaces(String text)
    {
        return GeneralUtils.replaceAllTokens(text, "\n", "<br/>");
    }

    public static void main(String[] args)
    {
        new Expand();
        
        try
        {
            //File projFile = new File("examples/Ex6-Cerebellum/Ex6-Cerebellum.neuro.xml");
            //File projFile = new File("C:\\copynCmodels\\TraubEtAl2005\\TraubEtAl2005.neuro.xml");
            File projFile = new File("nCmodels/CA1PyramidalCell/CA1PyramidalCell.ncx");
            //File projFile = new File("/bernal/models/Layer23_names/Layer23_names.neuro.xml");
            

            logger.logComment("Going to create docs for project: "+ projFile.getCanonicalPath(), true);
            
            
            Project testProj = Project.loadProject(projFile, new ProjectEventListener()
                {
                    public void tableDataModelUpdated(String tableModelName)
                    {};
                    
                    public void tabUpdated(String tabName)
                    {};
                    public void cellMechanismUpdated()
                    {};
                });
            
            File f = new File("../temp/testExpand/proj.html");
            
            generateMainPage(testProj, f);
            

            logger.logComment("Created doc at: "+ f.getCanonicalPath(), true);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }
}
