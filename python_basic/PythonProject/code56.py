files = []
count = 0

while True:
    f = open('./test.txt', 'r')
    files.append(f)
    f.close()
    count += 1
    print(f'打开文件个数：{count}')