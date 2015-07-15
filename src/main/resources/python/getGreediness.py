from metrics import getTargetAllocation
from metrics import getGreediness
from metrics import get_allocation
from metrics import get_only_greediness
from metrics import VM
import numpy as np
import random
import sys

while True:
	text = input('')  # if using python 2.x change this to raw_input('')
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
		
# define a set of VMs
#U1 = VM([6,5,6,5],'a')
#U2 = VM([6,3,7,3],'b')
#U3 = VM([5,3,2,7],'c')
#U4 = VM([4,4,3,6],'d')

# combine the VMs to a list
#VMs = [U1,U2,U3,U4]
# of each resource are 16 unit available
#supply = [16,16,16,16]

# calculate a new allocation for the 4th (last) resource
	out = get_only_greediness(VMs,supply)

	for i in out:
		print(i)
		print
	
	
sys.exit(0)

# Here the function get_allocation is tested automatically, to find unexpected errors
# 100 times for a random number of VMs and resources the function is called

for i in range (100):	
	number_VMs = random.randrange(2, 6)
	number_resources = random.randrange(2, 6)

	VMs = list()
	for i in range(number_VMs):
		VMs.append(VM([random.randrange(1, 10) for _ in xrange(number_resources)]))
	supply = [(number_VMs*random.randrange(1, 10)) for _ in xrange(number_resources)]

	out = get_allocation(VMs,supply)
	for i in out:
		print(i)
		print
	