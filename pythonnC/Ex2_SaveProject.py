#
#
#   A file which opens a neuroConstruct project, changes the value of the 
#   amplitude in the electrical stimulation to a random value, then saves the project
#
#   Author: Padraig Gleeson
#
#   This file has been developed as part of the neuroConstruct project
#   This work has been funded by the Medical Research Council
#
#

from java.io import File
from java.lang import System

from ucl.physiol.neuroconstruct.project import Project, ProjectManager
from ucl.physiol.neuroconstruct.utils import NumberGenerator

from math import *
from random import *

import time



# Load an existing neuroConstruct project

projFile = File("TestPython/TestPython.neuro.xml")

print "Loading project from file: " + projFile.getAbsolutePath()+", exists: "+ str(projFile.exists())

pm = ProjectManager()

myProject = pm.loadProject(projFile)

print "Loaded project: " + myProject.getProjectName() 



# Get first electrical stimulation & reset it

stim = myProject.elecInputInfo.getStim(0)

print "First stimulation setting: "+ str(stim)

newAmp = random()*0.2

stim.setAmp(NumberGenerator(newAmp))

myProject.elecInputInfo.updateStim(stim)

print "Stimulation now:           "+ str(myProject.elecInputInfo.getStim(0))



# Save project & exit

myProject.markProjectAsEdited()
myProject.saveProject()


System.exit(0)

