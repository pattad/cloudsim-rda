import numpy as np

class VM:

	def __init__(self, demands = [0], name = 'default'):
		if not all(isinstance(i,(int,float)) for i in demands):
			raise ValueError("First parameter must be a list of integers or floats")
		for i in range(len(demands)):
			demands[i] = demands[i]*1.0
		self.request_vector = list(demands)
		self.receive_vector = [0.0]*len(demands)
		if not isinstance(name,str):
			raise ValueError("Second parameter must be a string")
		self.name = name
		self.greed_user = 0
		self.greed_self = None
		self.request_scalar = None # helper variable for function getTargetAllocation
		self.receive_scalar = 0 # helper variable for function getTargetAllocation
		self.starve_scalar = 0 # helper variable for function getTargetAllocation

	def update_printer(self):
		self.printer = "test = VM()\ntest.greed = %f\ntest.wants = %f\ntest.name = '%s'\nVMs.append(test)\ntest.update_printer()\n"%(self.greed_self,self.request_scalar, self.name)

	def __repr__(self):
		neu = list()
		for i in self.receive_vector:
			neu.append("%.8f"%i)
		neu2 = list()
		for j in self.request_vector:
			neu2.append("%.8f"%j)

		return '\nVM %s, greed: %.3f + %.3f = %.3f\n'%(self.name,self.greed_self,self.greed_user,(self.greed_self + self.greed_user)) + "gets  " + str(neu) + "\nwants " + str(neu2)