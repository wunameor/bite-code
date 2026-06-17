files = []

def read():
    # 这样不会出现异常
    # with open('./test.txt') as f:
    #     files.append(f)
    #     return ''

    # 这样就会出现异常
    f = open('./test.txt')
    files.append(f)
    return ''

count = 0
while True:
    read()
    count += 1
    print(f"count = {count}")