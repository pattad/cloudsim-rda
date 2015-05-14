import numpy as np


class VM:

	def __init__(self, demands, name = 'default'):
		if not all(isinstance(i,(int,float)) for i in demands):
			raise ValueError("First parameter must be a list of integers or floats")
		for i in range(len(demands)):
			demands[i] = demands[i]*1.0
		self.request = list(demands)
		if not isinstance(name,str):
			raise ValueError("Second parameter must be a string")
		self.name = name
		self.wants = -1
		self.gets = list(demands)
		self.greed = -1
		self.greed_user = 0
		self.allocated = 0			

	def __repr__(self):
		neu = list()
		for i in self.gets:
			neu.append("%.8f"%i)
		neu2 = list()
		for j in self.request:
			neu2.append("%.8f"%j)

		return 'VM %s, greed: %.3f\n'%(self.name,self.greed) + "gets  " + str(neu) + "\nwants " + str(neu2)