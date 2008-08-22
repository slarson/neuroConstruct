#
#
#   A file which opens a neuroConstruct project and then manually adds cells and net conns
#   to the generatedCellPositions, etc. instead of using the cell group info and generating the 
#   project in the normal way
#
#   Author: Padraig Gleeson
#
#   This file has been developed as part of the neuroConstruct project
#   This work has been funded by the Medical Research Council
#
#


from java.io import File
from java.lang import System

from ucl.physiol.neuroconstruct.project import ProjectManager, SingleElectricalInput

from math import *


# Load an existing neuroConstruct project

projFile = File("TestPython/TestPython.neuro.xml")

print "Loading project from file: " + projFile.getAbsolutePath()+", exists: "+ str(projFile.exists())

pm = ProjectManager()
myProject = pm.loadProject(projFile)

print "Loaded project: " + myProject.getProjectName() 



# Get the names of the first Cell Group, Network Connection and Electrical Stimulation
cellGroup0 = myProject.cellGroupsInfo.getAllCellGroupNames().get(0)
netConn0 = myProject.morphNetworkConnectionsInfo.getAllSimpleNetConnNames().get(0)
elecInput0 = myProject.elecInputInfo.getAllStimRefs().get(0)



# Add a number of cells to the generatedCellPositions, connections to generatedNetworkConnections
# and electrical inputs to generatedElecInputs
numCells = 6

for i in range(0, numCells) :

    x = 100 * sin(i * 2 *pi / numCells)
    y = 100 * cos(i * 2 *pi / numCells)
    
    myProject.generatedCellPositions.addPosition(cellGroup0, i, x,y,0)
    
    if i != numCells-1 : 
        myProject.generatedNetworkConnections.addSynapticConnection(netConn0, i, i+1)

input = SingleElectricalInput(elecInput0, cellGroup0, 0)

myProject.generatedElecInputs.addSingleInput(elecInput0, input)



print "-----------------------------------"
print "Information on network generated: "
print

print myProject.generatedCellPositions
print myProject.generatedNetworkConnections
print myProject.generatedElecInputs

print "-----------------------------------"
print


System.exit(0)



                                     






