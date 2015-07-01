import numpy as np
import math
import time
import sys

#import matplotlib.pyplot as plt
#import pylab as p


from VM import VM

normalizer = 10.

def gez(a):
	if a > 0:
		return a*1.
	return 0.

def lez(a):
	if a < 0:
		return a*1.
	return 0.

def duc(a):
	if a>-1:
		return a*1.
	return -1.
	
def notZero(a):
	if a!=0:
		return a*1.
	return -1.



# this function calculates the overall quantity of every resource and calculates the normalization factor for each resource accordingly
# this function is called by all of the four metric
def initialize( endowments, demands ):

	# Make sure both input parameters are numpy arrays
	if not isinstance(endowments, np.ndarray):
		print("Input error: first parameter must be np.array")
		return
	if not isinstance(demands, np.ndarray):
		print("Input error: second parameter must be np.array")
		return

	# If endowments are a matrix, it is indeed the enowments per VM
	# If endowments are a vector, it is the overall resource supply and therefore the endowment per VM is 'vector' divided by 'number of VMs'
	if len(endowments.shape)==1: #if endowments is a vector
		endowments=np.array([endowments]) # make endowments two dimensional
	
	# Ensure that the dimensions of the input matrices fit
	if endowments.shape[0] != 1 and endowments.shape[0] != demands.shape[0]:
		print ("Input Error: Endowments height must either be 1 (equal endowments for all VMs) or equal to the demands height")
		return
	if endowments.shape[1] != demands.shape[1]:
		print ("Input Error: Endwoments and demands must have same length")
		return

	# If handed endowments is a matrix, add up the columns to arrive at total supplies
	if endowments.shape[0] != 1:
		resources = np.sum(endowments, axis=0)
	
	# If handed endowments is a vector, this vector already specifies the total supply
	else:
		resources = endowments
	
	# the norm vector serves to account for the quantities of different resources, i.e., to normalize resource amounts
	norm = np.divide( normalizer, resources )
	
	#np.sum(...) calculates the overall request for each resource, which subsequently is divided by the resources supply to arrive at the scarcity of a resource
	norm_w_scarcity = ((np.sum(demands, axis=0)*1.0)/resources) * norm
	
	return { 'resources': resources, 'norm': norm, 'endowments' : endowments, "norm_w_scarcity" : norm_w_scarcity }



# This function calculates the greediness of each customer by simply normalizing and adding up his requests
def getJustSum( endowments, demands ):

	init = initialize( endowments, demands )

	# Multiply the resquest of each resource with the normalization vector. The vector's length is equal to the number of columns of the demand matrix and a vector entry is multiplied with each entry in the respective column
	# subsequently each row is added up (np.sum)
	return np.sum(demands*init['norm'],axis=1)

# This function calculates the greediness of each customer by simply normalizing and adding up his requests. However, before the requests are added up, each request is also multiplied by the scarcity of the resource (demand/supply)
def getJustSumWScarcity( endowments, demands ):
	init = initialize( endowments, demands )
	return np.sum(demands*init['norm_w_scarcity'],axis=1)

def getGreediness( endowments, demands ):
	init = initialize( endowments, demands )
	return greediness( init['endowments'], demands, init['norm'], init['resources'] )

def getGreedinessWScarcity( endowments, demands ):
	init = initialize( endowments, demands )
	# Compared to the "normal" greediness metric, this metric takes the scarcity of resources into account
	# Therefore each resource request is not only weighted by the normalization factor (depending on the overall amounts of resources) but also by the amount of requests divided by the supply, as calculated here
	return greediness( init['endowments'], demands, init['norm_w_scarcity'], init['resources'] )

def getDRF( endowments, demands ):

	init = initialize( endowments, demands )

	# Multiply the resquest of each resource with the normalization vector. The vector's length is equal to the number of columns of the demand matrix and a vector entry is multiplied with each entry in the respective column
	# subsequently each row is added up (np.sum)
	
	ret = np.zeros(demands.shape[0])
	
	for i in range(demands.shape[0]):
		ret[i] = np.max( (demands[i,:]*1.0)/endowments )
	return ret

def greediness( endowments, demands, factor, resources ):
	if endowments.shape[0] == 1:
		temp = endowments*1.0/demands.shape[0]	# # If endowments is a vector, it depicts the total supply of resource. Therefore the endowment of a consumer to a resource is an nth (demands.shape[0] is the number of consumers) of the supply
		endowments = temp
		for i in (range(demands.shape[0]-1)):	# concatenate the endowments vector 
			endowments = np.concatenate((endowments, temp), axis=0)
	diffToEqualShare = demands-endowments
	maxi = np.vectorize(gez)
	mini = np.vectorize(lez)
	makeAtLeastMinusOne = np.vectorize(duc)
	makeNotZero = np.vectorize(notZero)
	posDem = maxi(diffToEqualShare)
	negDem = mini(diffToEqualShare)
	ratio = np.divide( np.sum( posDem,axis=0 ), makeNotZero( np.sum( negDem,axis=0 ) ) )
	return np.sum((posDem-(negDem*(makeAtLeastMinusOne(ratio))))*factor,axis=1)

def getTargetAllocation(VMs_in, supply):

	VMs = list(VMs_in)
#	for VM in VMs:
#		VM.inital_request = VM.request_scalar

#	VMs_bu = list(VMs)
#	supply_bu = supply

	currently_receiving =  [None for _ in range(0)]
	done =  [None for _ in range(0)]

#	for i in range(len(VMs)):
#		VMs[i].number = i
#		VMs[i].greed_bu = VMs[i].greed
#		VMs[i].request_scalar_bu = VMs[i].request_scalar
		
	endow = 1.0*supply/len(VMs) # Equal-share of the resource that is being allocated for every VM
	norm = normalizer/supply # Scale of one unit of the resource to be allocated
#	print "Normation factor is %.3f" %norm
#	print "Endowments are      %.3f" %endow
#	print "Supply: %.2f"%supply
#	for i in VMs:
#		print "\t%s   g: %f, w:%.2f"%(i.name,i.greed,i.request_scalar)
	

	for VM in VMs:# This loop calculates the starvation limit	
		if VM.greed_self <= 0: # if the VM has a non-positive greediness its starvation limit is its endowment
			VM.starve_scalar = endow
		else:
			VM.starve_scalar = (endow - VM.greed_self - 1)/2+(
				math.sqrt(				
					(
						math.pow(VM.greed_self,2)
						+
						math.pow(endow,2)
						+
						1
					)/4
					+
					(VM.greed_self + endow - VM.greed_self*endow )/2
				)
			)
		
		if VM.starve_scalar >= VM.request_scalar:
			supply -= VM.request_scalar
			VM.starve_scalar = VM.request_scalar
			VM.request_scalar = 0
			VM.greed_self = VM.greed_self/norm + VM.starve_scalar # The VMs greediness is its current greediness (normalized by the resource that is allocated) plus the amount it receives of the resource
			done.append(VM)# Because the VM is already happy with receiving its starvation limit or less, it can be moved to list _done_, which contains all VMs that will not receive further resources
		else:
			supply -= VM.starve_scalar
			VM.request_scalar = VM.request_scalar - VM.starve_scalar
			VM.greed_self = VM.greed_self/norm + VM.starve_scalar

	for VM in done:
		VMs.remove(VM)
		
#	print "*\n*\n*"
#	for VM in temp:
#		print "%s's greed is %.2f and its starvation limit %.2f. It still wants  %.2f."%(VM.name,VM.greed,VM.starve_scalar,VM.request_scalar)	
#	print "supply left: %.2f"%(supply)

    # Sort VMs by greediness and set the baseline to the least greedy VM
	VMs.sort(key=lambda x: x.greed_self)
	
	# WE DEFINE THE BASELINE OF A VM AS ITS GREEDINESS + WHAT IT IS ALLOCATED OF THE SCARCE RESOURCE, I.E. THE BASELINE IS THE GREEDINESS OF A VM, WHEN ALSO THE REALLOCATED RESOURCE IS TAKEN INTO ACCOUNT
	
    # baseline of initial greediness + allocationg to which all VM should be raised by allocating them more of the scarce resource
		
    # As long as VMs want resources and there is supply to be allocated

#	print "After starvation, before loop"
#	print "\tSupply: %.2f"%supply
#	for i in VMs:
#		print "\t%s   g: %f, w:%.2f"%(i.name,i.greed,i.request_scalar)
#	print "\n\n\n"
	
	round = 0
	while VMs and supply>0:
		round += 1
#		print "round %d" %round
#		print "\tSupply %.2f"%supply
		baseline = VMs[0].greed_self
#		print "\tbaseline %.2f"%baseline
		currently_receiving.append(VMs.pop(0))
		while VMs and VMs[0].greed_self == baseline:
			currently_receiving.append(VMs.pop(0))
		currently_receiving.sort(key=lambda x: x.greed_self+x.request_scalar)
		
#		print "\tcurrently receiving: "
#		for i in currently_receiving:
#			print "\t\t%s   g: %f, w:%.2f"%(i.name,i.greed_self,i.request_scalar)
		
		while supply > 0 and currently_receiving and ( len(VMs)==0 or currently_receiving[0].greed_self+currently_receiving[0].request_scalar <= VMs[0].greed_self ):
			baseline_inc = currently_receiving[0].greed_self+currently_receiving[0].request_scalar - baseline
#			print "\t\t\tBL: %.2f"%baseline_inc
#			print "\t\t\tLe: %.2f"%len(currently_receiving)
#			print "\t\t\tSu: %.2f"%supply
			if baseline_inc * len(currently_receiving) > supply:
#				print "ja"
				baseline = baseline + supply/len(currently_receiving)
				supply = 0
#				print "\t\tResources depleted"
			else:
				baseline = currently_receiving[0].greed_self+currently_receiving[0].request_scalar
				supply -= baseline_inc * len(currently_receiving)
#			print "\t\tbaseline %.2f (+ %.2f), supply: %.2f"%(baseline, baseline_inc,supply)
			while currently_receiving and currently_receiving[0].greed_self+currently_receiving[0].request_scalar == baseline:				
				currently_receiving[0].receive_scalar = currently_receiving[0].request_scalar
				currently_receiving[0].greed_self += currently_receiving[0].request_scalar
#				print "\t\t\tBaseline reached %s (moved to done)" %(currently_receiving[0].name)
				done.append(currently_receiving.pop(0))
				
		if VMs and currently_receiving:
#			print "reached in round %d" %round
			if (VMs[0].greed_self - baseline) * len(currently_receiving) <= supply:
				supply -= (VMs[0].greed_self - baseline) * len(currently_receiving)
				#baseline = VMs[0].greed_self
				# above not needed, because "baseline" will be updated any at the beginning of outer loop.
			else:
				baseline += supply/len(currently_receiving)		
				supply = 0
	while currently_receiving:#needs to be here (and not in else part of "if VMs and currently_receiving:") because, if part is also fulfilled in case of equality, i.e., supply is then zero and big loop not traversed again
#		print "baseline %f" %baseline
#		print "Current_greed %f" %currently_receiving[0].greed_self
		currently_receiving[0].receive_scalar = baseline - currently_receiving[0].greed_self
#		print currently_receiving[0].receive_scalar
		currently_receiving[0].greed_self += baseline - currently_receiving[0].greed_self
		done.append(currently_receiving.pop(0))
	for i in VMs:
		i.receive_scalar = 0
	done.extend(VMs)
	for i in done:
#		print i.receive_scalar
		i.receive_scalar += i.starve_scalar
		i.request_scalar 	+= i.starve_scalar
		i.greed_self 	*= norm

#	print
#	temp.sort(key=lambda x: x.greed_self)
#	for VM in temp:
#		print "%s's greed is %.2f, when receiving %.2f (%.2f starvation). It wants %.2f." %(VM.name,VM.greed_self,VM.receive_scalar, VM.starve_scalar,VM.request_scalar)
#	sum_supply = supply
#	for VM in VMs_bu:
#		sum_supply+=VM.receive_scalar
#		if VM.starve_scalar > VM.receive_scalar:
			#print "########################################################################################################################%s"%VM.name
			#print
			#print "VMs = list()"
#			print "VM %s ist unter starvation" %VM.name
#			f = open('fehler_fall.py', 'w')			
#			f.write("from metrics import getTargetAllocation\nfrom metrics import get_allocation\nfrom VM import VM\n\nVMs = list()\n")
#			for i in backup:
#				f.write(i.printer)
			#print "getTargetAllocation(VMs,%d)"%backupsupply 
			#return 1
#			f.write("out = getTargetAllocation(VMs,%d)"%backupsupply)
#			print "Fehler"
#			return False
#		if VM.gets < VM.request_scalar:
#			for VMM in VMs_bu:
#				if VMM.greed_self > VM.greed_self and VMM.receive_scalar < VM.receive_scalar:
#					print "VM bekommt weniger als VM mit hoeherer greediness"
#					return False		
#		if VM.request_scalar < VM.request_scalar_bu-0.000001 or VM.request_scalar > VM.request_scalar_bu+0.000001:
#			print "wants      %.20f"%VM.request_scalar
#			print "bu: %.20f"%VM.request_scalar_bu
#			
#		if  (
#			VM.greed_self < VM.greed_self_bu+VM.receive_scalar*norm
#			-0.000001
#			or
#			VM.greed_self > VM.greed_self_bu+VM.receive_scalar*norm
#			+0.000001
#			):	
#			print "greed      %.20f"%VM.greed_self
#			print "bu: %.20f"%(VM.greed_self_bu/norm+VM.receive_scalar)		
#	if sum_supply < supply_bu-0.000001 or sum_supply > supply_bu+0.000001:
#		print "supplies dont sum up"
#		f = open('fehler_fall.py', 'w')			
#		f.write("from metrics import getTargetAllocation\nfrom metrics import get_allocation\nfrom VM import VM\n\nVMs = list()\n")
#		for i in VMs_bu:
#			f.write(i.printer)
#		f.write("out = getTargetAllocation(VMs,%d)"%supply_bu)
#		print "left      %.20f"%sum_supply
#		print "supply_bu: %.20f"%supply_bu
#		return False
	return VMs_in

# get_allocation calculates the fairest allocation according to the greediness metric
# Input:
#	- A list of VMs with certain resources requests
#	- A list with the amounts that are available of each resource
# Output:
# 	- The VM objects in the input list are updated and returned
#	- In particular the .gets attribute is set with the values that the VM should receive of each resource
#	- Also the .greed_self attribute is updated with the greediness this VM has, when it receives the resources as specified by .gets


def check_input( VMs, supply ):
	# Verify first parameter
	
	# Check if first parameter is list
	if not isinstance( VMs, (list,tuple) ):
		raise ValueError("get_allocation's first parameter be list of VMs")
	number_resources = len(VMs[0].request_vector)
	
	# Check if the first parameter only contains instances of VM
	if not all( isinstance( to_test, VM ) for to_test in VMs ):
		raise ValueError("get_allocation's first parameter be list of VMs")
		
	# Check if the demand vector of these VMs have the same length
	if not all( len(to_test.request_vector) == number_resources for to_test in VMs ):
		raise ValueError("the demand vectors of all VM's must have the same length")

	number_VMs = len(VMs)
		
	# Verify second parameter	
		
	# Check if second parameter is list				
	if not isinstance( supply, list ):
		raise ValueError("get_allocation's second parameter be a list")
		
	# Check if second parameter has length of the number of resources
	if not len(supply) == number_resources:
		raise ValueError("get_allocation's second parameter have lenght of the number of resources")
	
	# Check if all item in the second parameters are integers or floats
	if not all(isinstance(to_test,(int,float)) for to_test in supply):
		# Check if all items in the second parameter are lists
		#if not all(isinstance( to_test, list ) for to_test in endowments):
			raise ValueError("get_allocation's second parameter must only contain integers or floats")
		# Check if lists have  length number of VMs and only contain integers or floats
		#if not all( len(to_test2) == number_VMs and all( (isinstance(to_test3, (int,float)) and to_test3 >=0 for to_test3 in to_test2)) for to_test2 in endowments):	

###	if isinstance(which_resource, list):
###		if not all(isinstance(to_test,int) for to_test in which_resource):
###			raise ValueError("get_allocation's third parameter must contain only integers")
###	if not isinstance(which_resource, (int,list)):
###		raise ValueError("get_allocation's third parameter must contain only integers")
	supply = np.array(supply)*1.0
	demands =   np.array([VMs[0].request_vector])*1.0
	allocates = np.array([VMs[0].receive_vector])*1.0
	for i in range(len(VMs)-1):	# concatenate the endowments vector
		temp  = np.array([VMs[i+1].request_vector])*1.0
		temp2 = np.array([VMs[i+1].receive_vector])*1.0
		demands =   np.concatenate((demands, temp), axis=0)
		allocates = np.concatenate((allocates, temp2), axis=0)
	return {'supply': np.array(supply), 'demands': demands, 'allocates': allocates}
	  
def get_only_greediness( VMs, supply ):

	initialize = check_input( VMs, supply )
	supply  = initialize['supply']
	demands = initialize['demands']
	allocates = initialize['allocates']

	greediness = getGreediness(np.array(supply),allocates)
	for i in range(len(VMs)):
		VMs[i].greed_self = greediness[i]		
	return VMs

def get_only_greediness_FOR_requests( VMs, supply ):

	initialize = check_input( VMs, supply )
	supply  = initialize['supply']
	demands = initialize['demands']
	allocates = initialize['allocates']

	greediness = getGreediness(np.array(supply),demands)
	for i in range(len(VMs)):
		VMs[i].greed_self = greediness[i]		
	return VMs

def get_allocation( VMs, supply ):

	initialize = check_input( VMs, supply )		
	supply  = initialize['supply']
	demands = initialize['demands']
	total_requests = np.sum(demands, axis=0)*1.0
	ratio = np.divide( total_requests, supply )
	
	while np.max(ratio)>1:
#		print '######################################'
#		print
		resource_to_reallocate = np.argmax(ratio)			

		# this variable contains the indices with resources where there is more demand than supply.
		# the [0] at the end is necessary because np.argwhere returns a two dimensional array
		scarce_resources = np.transpose(np.argwhere(ratio > 1))[0]

#		remove all demands of not yet allocated resources.
		demands_mod = np.delete(demands, scarce_resources, 1)
		
		supply_mod = np.delete(supply, scarce_resources)
		if len(supply_mod)>0:
			greediness = getGreediness(np.array(supply_mod),demands_mod)
		else:
			greediness = np.zeros(len(VMs))
			
		for i in range(len(VMs)):
			VMs[i].greed_self = greediness[i] + VMs[i].greed_user
			VMs[i].request_scalar = VMs[i].request_vector[resource_to_reallocate]
		VMreturn = getTargetAllocation(list(VMs), supply[resource_to_reallocate])
		
		for i in range(len(VMs)):
			VMs[i].receive_vector[resource_to_reallocate] = VMs[i].receive_scalar
			demands[i,resource_to_reallocate] = VMs[i].receive_scalar
		
		ratio[resource_to_reallocate] = 0

	greediness = getGreediness(np.array(supply),demands)
	for i in range(len(VMs)):
		VMs[i].greed_self = greediness[i] + VMs[i].greed_user
	return VMs


	
def get_allocation_for_leontief( VMs, supply):
#	VMs = list(VMs_in)
	initialize = check_input( VMs, supply )		
	supply  = initialize['supply']
	demands = initialize['demands']
	
	greed_users = np.zeros(len(VMs))
	for i in range(len(VMs)):
		greed_users[i] = VMs[i].greed_user
	
	
	# if there is no scarcity
	if (np.sum( demands, axis=0) <= supply).all():
		for vm in VMs:
			vm.receive_vector = np.array(vm.request_vector)
		get_only_greediness( VMs, supply.tolist() )
#		print ("\t"),
		return VMs

	# demands_relative contains VM requests relative to the overall supply
	demands_relative = np.empty(demands.shape)	
	for i in range(demands.shape[1]):
		demands_relative[:,i] = 	demands[:,i]/supply[i]
	
	# demands_DRF contains demands_relative scale such that the biggest relative demand for each VM is 1
	demands_DRF = np.empty(demands.shape)
	for i in range(demands.shape[0]):
		for j in range(demands.shape[1]):
			if demands_relative[i,j] == 0:
				demands_DRF[i,j] = 0
			else:
				demands_DRF[i,j] = demands_relative[i,j]/np.max(demands_relative[i,:])
#		demands_DRF[i,:] = 	demands_relative[i,:]/np.max(demands_relative[i,:])

	# demands_DRF_norm contains demands_relative such that the sum of relative demands add up to 1 of each VM
	demands_DRF_norm = np.empty(demands.shape)	
	for i in range(demands.shape[0]):
		for j in range(demands.shape[1]):
			if demands_relative[i,j] == 0:
				demands_DRF_norm[i,j] = 0
			else:
				demands_DRF_norm[i,j] = demands_relative[i,j]/np.sum(demands_relative[i,:])
#		demands_DRF_norm[i,:] = 	demands_relative[i,:]/np.sum(demands_relative[i,:])
	
	
	
	
	starvation_limits = np.zeros(demands.shape)

	equalshare = 1.0/demands.shape[0]

	for i in range(demands.shape[0]):
		if np.max(demands_DRF_norm[i,:]) > 0:

			if VMs[i].greed_user <= 0:
				starvation_factor = equalshare
			else:
			
#				print "equal share\t %f"%equalshare
#				print "greed\t\t %f"%VMs[i].greed_user

				starvation_factor = (equalshare - (VMs[i].greed_user/normalizer) - 1)/2+(
					math.sqrt(				
						(
							math.pow((VMs[i].greed_user/normalizer),2)
							+
							math.pow(equalshare,2)
							+
							1
						)/4
						+
						((VMs[i].greed_user/normalizer) + equalshare - (VMs[i].greed_user/normalizer)*equalshare )/2
					)
				)
				
#			print "starv limit:\t %f"%starvation_factor
#			print		
			starvation_limits[i,:] = starvation_factor * demands_DRF_norm[i,:]/( np.max(demands_DRF_norm[i,:]) * demands.shape[0] )
			if (starvation_limits[i,:] >= demands_relative[i,:]).all():
				starvation_limits[i,:] = demands_relative[i,:]
		else:
			starvation_limits[i,:] = np.zeros(demands.shape[1])
		VMs[i].starve_vector = starvation_limits[i,:]
		
#	return VMs	
		
		
		
		
		
		
		
#	print "starvation"
#	print starvation_limits
#	print
	
	# this allocation matrix is altered in the loop to arrive at the final allocation
	# initially no VM gets any resource
	allocation = np.array(starvation_limits)#np.zeros(demands.shape)
	x=0
	y=0
	increasing = True
	target_radius = 0.000000001
#	target_radius = 0.01
	everyone_happy = False

	approximator_default = 0.05
	factor = 0.9
	
	approximator =  approximator_default# the fraction of a VM's demand that will be added or removed per loop (change frequently)
	
	greediness = getGreediness( np.ones(allocation.shape[1]), allocation )
	greediness += greed_users
	

	while True:
		x+=1
		if math.fabs(approximator) < target_radius*0.1:
		
			approximator = approximator_default
			y += 1
		
			if y == 10:
				print("terminated")
				sys.exit()
		
		if 	(
				(
					approximator < 0
				and
					np.max( np.sum( allocation, axis=0 )) < 1
				)
			or
				(
					approximator > 0
				and
					np.max( np.sum( allocation, axis=0 )) > 1
				)
			):
			approximator *=- factor
		
		if approximator < 0:
#			print ("dec "),
#			print greediness
#			print allocation
#			print starvation_limits
			allocate_to_user = np.argmax(greediness)

			for i in range(len(greediness)):
#				print (allocate_to_user),
				if (allocation[allocate_to_user,:] == starvation_limits[allocate_to_user,:]).all():
					greediness[allocate_to_user] = float("-inf")
					allocate_to_user = np.argmax(greediness)
				else:
					break
					
		else:
#			print ("inc "),
			allocate_to_user = np.argmin(greediness)	
			for i in range(len(greediness)):
				if (allocation[allocate_to_user] == demands_relative[allocate_to_user,:]).all():
					greediness[allocate_to_user] = float("inf")
					allocate_to_user = np.argmin(greediness)
				else:
					break
#		print
#		print (allocate_to_user),
#		print ("\t"),
#		print (approximator)
#		print ("starv\t"),
#		print starvation_limits[allocate_to_user,:]
#		print ("before\t"),
#		print allocation[allocate_to_user,:]

		allocation[allocate_to_user,:] += approximator * demands_DRF_norm[allocate_to_user,:]
#		print ("middle\t"),
#		print allocation[allocate_to_user,:]
#		
#		print (allocation[allocate_to_user,:] <= starvation_limits[allocate_to_user,:]),
#		print (allocation[allocate_to_user,:] <= starvation_limits[allocate_to_user,:]).all()
#				
		if (allocation[allocate_to_user,:] <= starvation_limits[allocate_to_user,:]).all():
			allocation[allocate_to_user,:] = starvation_limits[allocate_to_user,:]
#			print ("reset to"),
#			print starvation_limits[allocate_to_user,:]

		if (allocation[allocate_to_user] >= demands_relative[allocate_to_user,:]).all():
			allocation[allocate_to_user,:] = demands_relative[allocate_to_user,:]

#		print ("end\t"),
#		print allocation[allocate_to_user,:]
#		print


		greediness = getGreediness( np.ones(allocation.shape[1]), allocation )

#		print greediness
#		print greed_users
#		print "greed total"
		greediness += greed_users
#		print greediness
		greed_min = float("inf")
		greed_max = float("-inf")
		
		happy = np.zeros(len(greediness))
		gotze = np.zeros(len(greediness))
		
		
		# wenn starvation groesser als demand ist, bekommt jemand nur seine starvation kommt aber trotzdem nach greed max
		
		
		for i in range(len(greediness)):
			if (allocation[i,:] < demands_relative[i,:]).any():
#				print "min %d"%i
				greed_min = min(greediness[i],greed_min)
#				gotze[i] = 1
			if (allocation[i,:] > starvation_limits[i,:]).any():
#				print "max %d"%i
				greed_max = max(greediness[i],greed_max)
#				happy[i] = 1
#			print allocation[i]
#			print starvation_limits[i]
#			print

#		print "Max allocated: %.4f\t greed range: %.4f (from %.4f to %.4f)"%(np.max( np.sum( allocation, axis=0)), (greed_max - greed_min), greed_min, greed_max )

		if		(
					(
						greed_max == float("-inf")
					)
				or
					(
						greed_max - greed_min 	< 	target_radius
					and
						np.max( np.sum( allocation, axis=0)) 	> 	1 - target_radius
					and
						np.max( np.sum( allocation, axis=0)) 	<= 	1
					)
				or
					(	
							greed_min == float("inf")
						and
							np.max( np.sum( allocation, axis=0)) 	<= 	1
					)
				):
				break
#		print
#		print ("happy"),
#		print happy
#		print ("zero "),
#		print gotze				
#		print
#	print allocation
#		print		
		
#		if greed_max - greed_min 	>= 	target_radius:
#			print ("greedniess not close\t"),
#		if np.max( np.sum( allocation, axis=0)) 	< 	1 - target_radius:
#			print ("not enough allocated"),
#		if np.max( np.sum( allocation, axis=0)) 	> 	1:
#			print ("too much allocated"),
#		print
#		print np.max( np.sum( allocation, axis=0))
#		print greed_min
#		print greed_max
#		print greediness
#		print happy
#		print gotze


		
#		if False:
#			if approximator < 0:
#				inc = -0.1
#			else:
#				inc = 0.1
#			plt.plot([allocate_to_user], [inc], 'go',markersize=20)
#			plt.axhline(linewidth=2, y = (np.max( np.sum( allocation, axis=0 ))-0.5), color='y')
#			p.axis([-0.1,(len(VMs)+0.1), -0.51,0.6])
#			ax = p.gca()
#			ax.set_autoscale_on(False)
#			plt.axhline()
#			plt.axhline(y=0.5, color='r')
#			plt.plot(greediness, 'ro')
#			ax.set_title(str(np.max( np.sum( allocation, axis=0))))
#			plt.show()

	greed = getGreediness( np.ones(allocation.shape[1]), allocation )
	allocation_denorm = np.zeros(allocation.shape)

	for i in range(demands.shape[1]):
		allocation_denorm[:,i] = 	allocation[:,i]*supply[i]
	
	for i in range(demands_DRF_norm.shape[0]):
		VMs[i].receive_vector = allocation_denorm[i,:]
		VMs[i].greed_self = greed[i]
		for j in range(demands_DRF_norm.shape[1]):
			VMs[i].starve_vector[j] *= supply[j]
			
	return VMs