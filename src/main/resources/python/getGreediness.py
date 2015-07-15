from metrics import get_Greediness
from metrics import get_allocation
from metrics import get_only_greediness
from metrics import VM
import numpy as np
import random
import sys

while True:
	text = input('') 
	text = text.split(' ')

	# sample input: 
	# 1000 2048.0 1000.0 10000.0 user1 230.0 20.0 22.0 0.0 user2 230.0 20.0 22.0 0.0 
	
	supply = [float(text[0]),float(text[1]),float(text[2]),float(text[3])]

	offset = 4
	
	U1 = VM([float(text[offset+1]),float(text[offset+2]),float(text[offset+3]),float(text[offset+4])],text[offset])
	VMs = [U1]
			
	if len(text) > 10: 
		offset = 9
		U2 = VM([float(text[offset+1]),float(text[offset+2]),float(text[offset+3]),float(text[offset+4])],text[offset])
		VMs = [U1,U2]
	
	if len(text) > 15: 
		offset = 14
		U3 = VM([float(text[offset+1]),float(text[offset+2]),float(text[offset+3]),float(text[offset+4])],text[offset])
		VMs = [U1,U2,U3]

	if len(text) > 20: 
		offset = 19
		U4 = VM([float(text[offset+1]),float(text[offset+2]),float(text[offset+3]),float(text[offset+4])],text[offset])
		VMs = [U1,U2,U3,U4]
		
# calculate a new allocation for the 4th (last) resource
	out = get_only_greediness(VMs,supply)

	for i in out:
		print(i)
		print
	
	
sys.exit(0)
