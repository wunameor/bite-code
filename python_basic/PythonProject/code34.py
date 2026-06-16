x = 10

def test():
    #global x = 20 # error
    global x
    x = 20

test()
print(x)