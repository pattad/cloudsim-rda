import numpy as np
import math
import time
import sys

#import matplotlib.pyplot as plt
#import pylab as p

class VM:

	nr = 1

	def __init__(self, demands = [0], name = -1):
		if not all(isinstance(i,(int,float)) for i in demands):
			raise ValueError("First parameter must be a list of integers or floats")
		for i in range(len(demands)):
			demands[i] = demands[i]*1.0
		self.request_vector = list(demands)
		self.receive_vector = [0.0]*len(demands)
		print("!!!")
		print(name)
		print(type(name))
		if not (name == -1 or isinstance(name,str)):
			raise ValueError("Second parameter must be a string")
		self.name = name
		self.weight = 1
		self.greed_user = 0
		self.greed_self = float("inf")
		self.request_scalar = None # helper variable for function getTargetAllocation
		self.receive_scalar = 0 # helper variable for function getTargetAllocation
		self.starve_scalar = 0 # helper variable for function getTargetAllocation
		self.starve_vector = [0.0]*len(demands)
		if name == -1:
			self.name = str(VM.nr)
			VM.nr += 1

	def update_printer(self):
		self.printer = "test = VM()\ntest.greed = %f\ntest.wants = %f\ntest.name = '%s'\nVMs.append(test)\ntest.update_printer()\n"%(self.greed_self,self.request_scalar, self.name)

	def __repr__(self):
		neu = list()
		for i in self.receive_vector:
			neu.append("%.2f"%i)
		neu2 = list()
		for j in self.request_vector:
			neu2.append("%.2f"%j)
		neu3 = list()
		for j in self.starve_vector:
			neu3.append("%.2f"%j)

		return '\nVM %s\n\tgreed: %.3f + %.3f = %.3f\n\tweight: %f \n'%(self.name,self.greed_self,self.greed_user,(self.greed_self + self.greed_user),self.weight) + "gets  " + str(neu) + "\nwants " + str(neu2) + "\nstarv " + str(neu3)

starve_design_parameter = 1.0

final_normalizer = 1.

normalizer = final_normalizer

increase_normalizer = True

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
def initialize_raw( endowments, demands, weights = 0):

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
	
	if increase_normalizer:
		global normalizer
		global final_normalizer
		
		if isinstance(weights,int):	
			normalizer = final_normalizer * demands.shape[0]
		else:
			normalizer = final_normalizer * np.sum(weights)

	# the norm vector serves to account for the quantities of different resources, i.e., to normalize resource amounts
#	print "Normalizer %f"%normalizer
	norm = np.divide( normalizer, resources )
	#np.sum(...) calculates the overall request for each resource, which subsequently is divided by the resources supply to arrive at the scarcity of a resource
	norm_w_scarcity = ((np.sum(demands, axis=0)*1.0)/resources) * norm
	
	return { 'resources': resources, 'norm': norm, 'endowments' : endowments, "norm_w_scarcity" : norm_w_scarcity }

# This function calculates the greediness of each customer by simply normalizing and adding up his requests
def get_JustSum( endowments, demands, weights = 0 ):

	init = initialize_raw( endowments, demands, weights )

	# Multiply the resquest of each resource with the normalization vector. The vector's length is equal to the number of columns of the demand matrix and a vector entry is multiplied with each entry in the respective column
	# subsequently each row is added up (np.sum)
	return np.sum(demands*init['norm'],axis=1)

# This function calculates the greediness of each customer by simply normalizing and adding up his requests. However, before the requests are added up, each request is also multiplied by the scarcity of the resource (demand/supply)
def get_JustSumWScarcity( endowments, demands, weights = 0 ):
	init = initialize_raw( endowments, demands, weights )
	return np.sum(demands*init['norm_w_scarcity'],axis=1)

def get_Greediness( endowments, demands, weights = 0):

#	if increase_normalizer:
#		print "[getGreediness] multiplying normalizer with %d" %demands.shape[0]	
#		global normalizer
#		global final_normalizer
#		normalizer = final_normalizer * demands.shape[0]

	init = initialize_raw( endowments, demands, weights )
	return greediness_raw( init['endowments'], demands, init['norm'], init['resources'] )

def get_GreedinessWScarcity( endowments, demands, weights = 0 ):
	init = initialize_raw( endowments, demands, weights )
	# Compared to the "normal" greediness metric, this metric takes the scarcity of resources into account
	# Therefore each resource request is not only weighted by the normalization factor (depending on the overall amounts of resources) but also by the amount of requests divided by the supply, as calculated here
	return greediness_raw( init['endowments'], demands, init['norm_w_scarcity'], init['resources'] )

def get_DRF( endowments, demands, weights = 0 ):

	init = initialize_raw( endowments, demands, weights )

	# Multiply the resquest of each resource with the normalization vector. The vector's length is equal to the number of columns of the demand matrix and a vector entry is multiplied with each entry in the respective column
	# subsequently each row is added up (np.sum)
	
	ret = np.zeros(demands.shape[0])
	
	for i in range(demands.shape[0]):
		ret[i] = np.max( (demands[i,:]*1.0)/endowments )
	return ret

def greediness_raw( endowments, demands, factor, resources ):

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

def get_starvation_factors( VMs , basic_endowment = 0 ):
	
	greediness = np.zeros(len(VMs))
	weight = np.zeros(len(VMs))
		
	for i in range(len(VMs)):
		greediness[i] 	=	VMs[i].greed_user + VMs[i].greed_self
		weight[i] 		=	VMs[i].weight	
	starvation_factors = starvation_factors_raw( greediness , weight )
	
	basic_endowment_ret = np.array(basic_endowment)
	
	if isinstance(basic_endowment, np.ndarray):
		for i in range(len(VMs)):
			VMs[i].starve_vector = basic_endowment * starvation_factors[i]
	return starvation_factors

def starvation_factors_raw( greediness , weight ):
	starvation_factors = np.zeros(len(greediness))
	for i in range(len(greediness)):	
		if greediness[i] <= 0:
			starvation_factor = weight[i]
		else:
			starvation_factor = (weight[i] - greediness[i] - 1)/2+(
				math.sqrt(				
					(
						math.pow(greediness[i],2)
						+
						math.pow(weight[i],2)
						+
						1
					)/4
					+
					(greediness[i] + weight[i] - greediness[i] * weight[i] )/2
				)
			)
		starvation_factors[i] = starvation_factor	
	return starvation_factors

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
	if not isinstance( supply, ( list, np.ndarray )):
		raise ValueError("get_allocation's second parameter be a list")
		
	# Check if second parameter has length of the number of resources
	if not (len(supply) == number_resources or supply.shape[1] == number_resources):
		raise ValueError("get_allocation's second parameter have lenght of the number of resources")
	
	# Check if all item in the second parameters are integers or floats
	if not (all(isinstance(to_test,(int,float)) for to_test in supply) or isinstance(supply , np.ndarray)):
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
	weights = np.zeros(len(VMs))
	weights[0] = VMs[0].weight
	for i in range(len(VMs)-1):	# concatenate the endowments vector
		temp  = np.array([VMs[i+1].request_vector])*1.0
		temp2 = np.array([VMs[i+1].receive_vector])*1.0
		demands =   np.concatenate((demands, temp), axis=0)
		allocates = np.concatenate((allocates, temp2), axis=0)
		weights[i+1] = VMs[i+1].weight
		
	return {'supply': np.array(supply), 'demands': demands, 'allocates': allocates, 'weights': weights}
	  
def get_only_greediness( VMs, supply ):

	init = check_input( VMs, supply )
	supply  = init['supply']
	demands = init['demands']
	allocates = init['allocates']
	weights = init['weights']

	greediness = get_Greediness( np.array(supply), allocates, weights )
	for i in range(len(VMs)):
		VMs[i].greed_self = greediness[i]		
	return VMs

def get_only_greediness_FOR_requests( VMs, supply ):

	init = check_input( VMs, supply )
	supply  = init['supply']
	demands = init['demands']
	allocates = init['allocates']
	weights = init['weights']
	greediness = getGreediness(np.array(supply),demands, weights)

	for i in range(len(VMs)):
		VMs[i].greed_self = greediness[i]		
	return VMs

def get_allocation( VMs, supply ):

	init = check_input( VMs, supply )		
	supply  = init['supply']
	demands = init['demands']
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
			VMs[i].greed_self = greediness[i] + VMs[i].greed_user # kritisch, wie wird greed_self und greed_user berechnet
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
	output = False
	depletion_order = list()

	init = check_input( VMs, supply )		
	supply  = init['supply']
	demands = init['demands']
	nr_VMs = len(VMs)
	nr_res = len(supply)
	
	greed_users = np.zeros(nr_VMs)
	for i in range(nr_VMs):
		greed_users[i] = VMs[i].greed_user
	
	# if there is no scarcity
	if (np.sum( demands, axis=0) <= supply).all():
		for vm in VMs:
			vm.receive_vector = np.array(vm.request_vector)
		get_only_greediness( VMs, supply.tolist() )
		return VMs

	# demands_relative contains VM requests relative to the overall supply
	demands_relative = np.empty(demands.shape)	
	for i in range(nr_res):
		demands_relative[:,i] = 	demands[:,i]/supply[i]
	
	# demands_DRF contains demands_relative scale such that the biggest relative demand for each VM is 1
	demands_DRF = np.empty(demands.shape)
	for i in range(nr_VMs):
		for j in range(nr_res):
			if demands_relative[i,j] == 0:
				demands_DRF[i,j] = 0
			else:
				demands_DRF[i,j] = demands_relative[i,j]/np.max(demands_relative[i,:])

	# demands_DRF_norm contains demands_relative such that the sum of relative demands add up to 1 of each VM
	demands_DRF_norm = np.empty(demands.shape)	
	for i in range(nr_VMs):
		for j in range(nr_res):
			if demands_relative[i,j] == 0:
				demands_DRF_norm[i,j] = 0
			else:
				demands_DRF_norm[i,j] = demands_relative[i,j]/np.sum(demands_relative[i,:])


	greediness = np.zeros(nr_VMs)
	weight = np.zeros(nr_VMs)

	for i in range(nr_VMs):
		greediness[i] 	=	VMs[i].greed_user #+ VMs[i].greed_self
		weight[i] 		=	VMs[i].weight
		
	starvation_factors = starvation_factors_raw( greediness , weight )
	starvation_limits = np.zeros(demands.shape)

#	print "dfsdf"
#	print nr_VMs
#	print "fff"
	for i in range(nr_VMs):
		if np.max(demands_DRF_norm[i,:]) > 0:
			starvation_limits[i,:] = starvation_factors[i] * demands_DRF_norm[i,:]/( np.max(demands_DRF_norm[i,:]) * nr_VMs )
			if (starvation_limits[i,:] >= demands_relative[i,:]).all():
				starvation_limits[i,:] = demands_relative[i,:]
		else:
			starvation_limits[i,:] = np.zeros(demands.shape[1])
			
#		print "greed %d factor %f"%(VMs[i].greed_user, starvation_factors[i])
#		print demands_relative[i,:]
#		print starvation_limits[i,:]
#		print
		
		starvation_limits[i,:] *= starve_design_parameter	
		VMs[i].starve_vector = np.array(starvation_limits[i,:])

	# this allocation matrix is altered in the loop to arrive at the final allocation
	# initially every VMs gets its starvation limit
	allocation = np.array(starvation_limits)#np.zeros(demands.shape)	
#	allocation_bu = np.array(starvation_limits)

	
	x=0
	y=0
	increasing = True
	target_radius = 0.000000001
	approximator_default = 0.05
	factor = 0.9
	approximator =  approximator_default# the fraction of a VM's demand that will be added or removed per loop (change frequently)	
	greediness = getGreediness( np.ones(nr_res), allocation )
	greediness += greed_users
	depleted = np.zeros(nr_res, dtype=bool)

	while True:
		while True:
			if math.fabs(approximator) < target_radius*0.1:
				approximator = approximator_default
				y += 1
				if y == 10:
					print("terminated due to too many iterations")
					sys.exit()		
			if 	(
					(
						approximator < 0
					and
						np.max( np.sum( allocation, axis=0 ) * np.invert(depleted)) < 1
					)
				or
					(
						approximator > 0
					and
						np.max( np.sum( allocation, axis=0 ) * np.invert(depleted)) > 1
					)
				):
				approximator *=- factor
		
			if approximator < 0:
				if output:
					print ("dec "),
				allocate_to_user = np.argmax(greediness)

				for i in range(nr_VMs):
					if (allocation[allocate_to_user,:] == starvation_limits[allocate_to_user,:]).all():
						greediness[allocate_to_user] = float("-inf")
						allocate_to_user = np.argmax(greediness)
					else:
						break
					
			else:
				if output:
					print ("inc "),
				allocate_to_user = np.argmin(greediness)	
				for i in range(nr_VMs):
					if (allocation[allocate_to_user] == demands_relative[allocate_to_user,:]).all():
						greediness[allocate_to_user] = float("inf")
						allocate_to_user = np.argmin(greediness)
					else:
						break
			if output:
				print (allocate_to_user),
				print ("\t"),
				print (approximator)

			allocation[allocate_to_user,:] += approximator * demands_DRF_norm[allocate_to_user,:]

			if (allocation[allocate_to_user,:] <= starvation_limits[allocate_to_user,:]).all():
				allocation[allocate_to_user,:] = starvation_limits[allocate_to_user,:]

			if (allocation[allocate_to_user] >= demands_relative[allocate_to_user,:]).all():
				allocation[allocate_to_user,:] = demands_relative[allocate_to_user,:]

			greediness = getGreediness( np.ones(nr_res), allocation )

			greediness += greed_users
			greed_min = float("inf")
			greed_max = float("-inf")
		
			for i in range(nr_VMs):

				if (allocation[i,:] < demands_relative[i,:]).any():
					greed_min = min(greediness[i],greed_min)

				if (allocation[i,:] > starvation_limits[i,:]).any():
					greed_max = max(greediness[i],greed_max)

			if output and False:
				print ("Max allocated: %.4f\t greed range: %.4f (from %.4f to %.4f)"%(np.max( np.sum( allocation, axis=0)), (greed_max - greed_min), greed_min, greed_max ))
				print("allocation")	
				print(allocation)
				print("________________")
				print(np.sum( allocation, axis=0))
				print("________________")
				print(np.sum( allocation, axis=0) * np.invert(depleted))
				print(depleted)
				if greed_max == float("-inf"):
					print("break1")
				if 	(
								greed_max - greed_min 	< 	target_radius
							and
								np.max( np.sum( allocation, axis=0) * np.invert(depleted)) 	> 	1 - target_radius
							and
								np.max( np.sum( allocation, axis=0) * np.invert(depleted)) 	<= 	1
					):
					print("break2")
				if(	
									greed_min == float("inf")
								and
									np.max( np.sum( allocation, axis=0) * np.invert(depleted)) 	<= 	1
							):
					print("break3")

			if		(
						(
							greed_max == float("-inf")
						)
					or
						(
							greed_max - greed_min 	< 	target_radius
						and
							np.max( np.sum( allocation, axis=0) * np.invert(depleted)) 	> 	1 - target_radius
						and
							np.max( np.sum( allocation, axis=0) * np.invert(depleted)) 	<= 	1
						)
					or
						(	
								greed_min == float("inf")
							and
								np.max( np.sum( allocation, axis=0) * np.invert(depleted)) 	<= 	1
						)
					):
					break
		
		amount_allocated = np.sum( allocation, axis=0 )
		escape = True

#		if (allocation < allocation_bu).any():
#			print "allocations got smaller"
#			sys.exit()
#		allocation_bu = np.array(allocation)

		for j in range(nr_res):
			if amount_allocated[j] 	> 	1 - target_radius:
				if depleted[j] == False:
					depletion_order.append(j)
				depleted[j] = True
		
		for i in range(nr_VMs):
			 starvation_limits[i,:] = np.array(allocation[i,:])

			 if any(	demands_relative[i,j] > 0 and depleted[j]	for j in range(demands.shape[1]) ):
			 	demands_relative[i,:]  = np.array(allocation[i,:])
			 else:
			 	if not (allocation[i] == demands_relative[i,:]).all():
			 		escape = False
		x += 1
		approximator =  approximator_default/x
		if escape:
			break
#		print allocation
#		print("reentering")
	
#	print allocation
	if False:
	#	abfangen, dass alle schon zufrieden sind (muss man vielleicht gar nicht)
		while	np.min(greediness) < float("inf"):
			cont = False
#			print "\n\n\n\n\n"
			print ("npmax : %.20f"%np.max( np.sum( allocation, axis=0)))
#			print allocation
#			print "________________"
#			print np.sum( allocation, axis=0)
			allocate_to_user = np.argmin(greediness)
			print (allocate_to_user),
#			print ("Changing user\t"),
#			print (allocate_to_user)
			greediness [allocate_to_user] = float("inf")
		
			vm_still_wants = demands_relative[allocate_to_user,:]	-	allocation[allocate_to_user,:]
#			print ("vm demand"),
#			print demands_relative[allocate_to_user,:]
#			print ("receives"),
#			print allocation[allocate_to_user,:]
#			print ("still wants"),
#			print vm_still_wants
			if np.max(vm_still_wants) == 0:
				print("VM happy continue")
				continue
		
			still_available = np.ones(allocation.shape[1]) - np.sum( allocation, axis=0)
#			print ("still available"),
#			print still_available		
		
			if (still_available >= vm_still_wants).all():
				allocation[ allocate_to_user , : ] = np.array(demands_relative[ allocate_to_user , : ])
				print("continue 2")
				continue
		
			demand_factors = np.zeros(demands.shape[1])
			for i in range(demands.shape[1]):			
				if still_available[i] > 0:
					demand_factors[i] = vm_still_wants[i]/still_available[i]
				else:
					if vm_still_wants[i] > 0:
						print("continue 1")
						cont = True
			if cont:
				continue				

#			print ("demand factors"),
#			print demand_factors
				
			scarcest_resource = np.argmax(demand_factors)
		
#			print "scarcest resource %d"%scarcest_resource
					
			available_of_scarcest = 1	- np.sum(allocation[:,scarcest_resource]) + allocation[allocate_to_user,scarcest_resource]			#np.ones(allocation.shape[1]) - np.sum( np.delete(allocation,allocate_to_user,0), axis=0)
		
#			print "consumed of scarcest resoure: %f"%np.sum(allocation[:,scarcest_resource])
#			print "consumed by current user: %f"%allocation[allocate_to_user,scarcest_resource]
#			print "available of scarcest resource %d: %f"%(scarcest_resource,available_of_scarcest)
		
#			print ("factor of what is currently received"),
#			print "%.20f"%(allocation[allocate_to_user,0] / demands_relative[ allocate_to_user , 0])
#			print ("factor should be changed to"),
#			print (available_of_scarcest / demands_relative[allocate_to_user,scarcest_resource])
		
			allocation[allocate_to_user,:] = (available_of_scarcest / demands_relative[allocate_to_user,scarcest_resource]) * demands_relative[ allocate_to_user , : ]			
#			print ("change allocation to"),
#			print allocation[allocate_to_user,:]
		


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
