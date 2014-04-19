r = raw_input().split(" ")

a=int(r[0])
b=int(r[1])
n=int(r[2])
c= 0
Trav = 0
c =   n / (a-b)  
Trav =   c * (a-b)
print c 
while 1:
	Trav =Trav +a 
	c=c+1
	if Trav >= n :
		print c
		break
	Trav =Trav-b
