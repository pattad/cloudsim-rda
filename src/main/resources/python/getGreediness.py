from metrics import getTargetAllocation
from metrics import getGreediness
from metrics import get_allocation
from metrics import get_only_greediness
from VM import VM
import numpy as np
import random
import sys

while True:
	text = input('')
	text = text.split(' ')

	supply = [float(text[0]),float(text[1]),float(text[2]),float(text[3])]

	U1 = VM([float(text[5]),float(text[6]),float(text[7]),float(text[8])],text[4])
	U2 = VM([float(text[10]),float(text[11]),float(text[12]),float(text[13])],text[9])
	VMs = [U1,U2]

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
	