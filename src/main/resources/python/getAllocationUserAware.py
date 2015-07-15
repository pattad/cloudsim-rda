from metrics import get_Greediness
from metrics import get_allocation
from metrics import get_allocation_for_leontief

from metrics import VM
import numpy as np
import random
import sys

while True:
	text = input('') 
	text = text.split(' ')

	# sample input: requested resources by <users>, <greediness>, resources 
	# 1000 2048.0 1000.0 10000.0 user1 0.0 230.0 20.0 22.0 0.0 user2 0.0 230.0 20.0 22.0 0.0 
	
	supply = [float(text[0]),float(text[1]),float(text[2]),float(text[3])]


	offset = 4

	U1 = VM([float(text[offset+2]),float(text[offset+3]),float(text[offset+4]),float(text[offset+5])],text[offset])
	U1.greed_user = float(text[offset+1])
	VMs = [U1]
	
	if len(text) > 11: 
		offset = 10
		U2 = VM([float(text[offset+2]),float(text[offset+3]),float(text[offset+4]),float(text[offset+5])],text[offset])
		U2.greed_user = float(text[offset+1])
		VMs = [U1,U2]
			
	if len(text) > 17: 
		offset = 16
		U3 = VM([float(text[offset+2]),float(text[offset+3]),float(text[offset+4]),float(text[offset+5])],text[offset])
		U3.greed_user = float(text[offset+1])
		VMs = [U1,U2,U3]
	
	if len(text) > 23: 
		offset = 22
		U4 = VM([float(text[offset+2]),float(text[offset+3]),float(text[offset+4]),float(text[offset+5])],text[offset])
		U4.greed_user = float(text[offset+1])
		VMs = [U1,U2,U3,U4]

# calculate a new allocation for the 4th (last) resource
	out = get_allocation_for_leontief(VMs,supply)

	for i in out:
		print(i)
		print
	
	
sys.exit(0)
