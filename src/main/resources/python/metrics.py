import numpy as np
from VM import VM

normalizer = 100.

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
		print ("Input error: first parameter must be np.array")
		return
	if not isinstance(demands, np.ndarray):
		print ("Input error: second parameter must be np.array")
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



def greediness( endowments, demands, factor, resources ):
	if endowments.shape[0] == 1:
		temp = endowments*1.0/demands.shape[0]	# # If endowments is a vector, it depicts the total supply of resource. THerefore the endowment of a consumer to a resource is an nth (demands.shape[0] is the number of consumers) of the supply
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
	ratio = np.divide(np.sum(posDem,axis=0), makeNotZero(np.sum(negDem,axis=0)) )
	return np.sum((posDem-(negDem*(makeAtLeastMinusOne(ratio))))*factor,axis=1) 



def getTargetAllocation(VMs, supplyReal):
	
	set =  [None for _ in range(0)]
	done =  [None for _ in range(0)]
    
	designparameter = 1
	
	normalizer2 = normalizer * designparameter
    
	supply = normalizer2
	for i in VMs:
		i.wants = i.wants*normalizer2/supplyReal
    
	for i in range(len(VMs)):
		VMs[i].number = i
        
	VMs.sort(key=lambda x: x.greed)
	genGreed = VMs[0].greed
    
	while VMs and supply>0:
		current_greed = VMs[0].greed
		set.append(VMs.pop(0))
		while VMs and VMs[0].greed == current_greed:
			set.append(VMs.pop(0))
		set.sort(key=lambda x: x.greed+x.wants)
        
		while supply > 0 and set and (len(VMs)==0 or set[0].greed+set[0].wants <= VMs[0].greed):
			if set and (set[0].greed+set[0].wants-genGreed)*len(set)<=supply:
				set[0].allocated = set[0].wants
				supply -= set[0].greed+set[0].wants - genGreed
				done.append(set.pop(0))
			else:
				genGreed += supply/len(set)
				supply = 0
				while set:
					set[0].allocated = genGreed-set[0].greed
					done.append(set.pop(0))
		if VMs:
			if (VMs[0].greed - genGreed) * len(set) <= supply:
				supply -= (VMs[0].greed - genGreed) * len(set)
				genGreed = VMs[0].greed
			else:
				genGreed += supply/len(set)
				supply = 0
	while set:
		set[0].allocated = genGreed - set[0].greed
		done.append(set.pop(0))
	for i in VMs:
		i.allocated = 0
	done.extend(VMs)
	for i in done:
		i.allocated = i.allocated*(supplyReal/normalizer2)
		#i.wants = i.wants*(supplyReal/normalizer2)
	return done
    






# get_allocation calculates the fairest allocation according to the greediness metric
# Input:
#	- A list of VMs with certain resources requests
#	- A list with the amounts that are available of each resource
# Output:
# 	- The VM objects in the input list are updated and returned
#	- In particular the .gets attribute is set with the values that the VM should receive of each resource
#	- Also the .greed attribute is updated with the greediness this VM has, when it receives the resources as specified by .gets
    
def get_only_greediness( VMs, supply ):

	# Verify first parameter
	
	# Check if first parameter is list
	if not isinstance( VMs, list ):
		raise ValueError("get_allocation's first parameter be list of VMs")
	number_resources = len(VMs[0].request)
	
	# Check if the first parameter only contains instances of VM
	if not all( isinstance( to_test, VM ) for to_test in VMs ):
		raise ValueError("get_allocation's first parameter be list of VMs")
		
	# Check if the demand vector of these VMs have the same length
	if not all( len(to_test.request) == number_resources for to_test in VMs ):
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
		
	supply = np.array(supply)
	
	demands = np.array([VMs[0].request])
	for i in range(len(VMs)-1):	# concatenate the endowments vector
		temp = np.array([VMs[i+1].request])
		demands = np.concatenate((demands, temp), axis=0)

	total_requests = np.sum(demands, axis=0)*1.0
	ratio = np.divide( total_requests, supply )

	greediness = getGreediness(np.array(supply),demands)
	for i in range(len(VMs)):
		VMs[i].greed = greediness[i]		
	return VMs
	
	
def get_allocation( VMs, supply ):

	# Verify first parameter
	
	# Check if first parameter is list
	if not isinstance( VMs, list ):
		raise ValueError("get_allocation's first parameter be list of VMs")
	number_resources = len(VMs[0].request)
	
	# Check if the first parameter only contains instances of VM
	if not all( isinstance( to_test, VM ) for to_test in VMs ):
		raise ValueError("get_allocation's first parameter be list of VMs")
		
	# Check if the demand vector of these VMs have the same length
	if not all( len(to_test.request) == number_resources for to_test in VMs ):
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
		
	supply = np.array(supply)
	
	demands = np.array([VMs[0].request])
	for i in range(len(VMs)-1):	# concatenate the endowments vector
		temp = np.array([VMs[i+1].request])
		demands = np.concatenate((demands, temp), axis=0)

	total_requests = np.sum(demands, axis=0)*1.0
	ratio = np.divide( total_requests, supply )

#	print "total requests"
#	print total_requests
#	print "supply"
#	print supply	
#	print "ratio"
#	print ratio
	
	while np.max(ratio)>1:
#		print '######################################'
#		print
		resource_to_reallocate = np.argmax(ratio)			
		scarce_resources = np.transpose(np.argwhere(ratio > 1))[0]
		
#		print "resource to reallocate %d" %resource_to_reallocate	
#		print "scarce resources %s" %str(scarce_resources)
		
		demands_mod = np.delete(demands, scarce_resources, 1)
		
		supply_mod = np.delete(supply, scarce_resources)
#		print "demands"	
#		print demands_mod
#		print "supply"
#		print supply_mod
		if len(supply_mod)>0:
			greediness = getGreediness(np.array(supply_mod),demands_mod)
		else:
			greediness = np.zeros(number_VMs)
			
		for i in range(len(VMs)):
			VMs[i].greed = greediness[i] + VMs[i].greed_user
			VMs[i].wants = VMs[i].request[resource_to_reallocate]
		VMreturn = getTargetAllocation(list(VMs), supply[resource_to_reallocate])
		
		for i in range(len(VMs)):
			VMs[i].gets[resource_to_reallocate] = VMs[i].allocated
			demands[i,resource_to_reallocate] = VMs[i].allocated
		
#		for i in VMs:
#			print i
#			print

		ratio[resource_to_reallocate] = 0


	greediness = getGreediness(np.array(supply),demands)
	for i in range(len(VMs)):
		VMs[i].greed = greediness[i] + VMs[i].greed_user	
	return VMs
	
	
	
