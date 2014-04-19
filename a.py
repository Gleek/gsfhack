import itertools

for subsequence in itertools.combinations("ABCvxyz",2):
	print "".join(subsequence)


