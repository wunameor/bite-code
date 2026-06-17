f = open('./test.txt', 'r', encoding="UTF-8") # 要添加 UTF-8 否则有编码问题

# print(f.read(2)) # 读取两个字符
# print(f.read()) # 读取全部字符

# for-in 遍历
# for line in f:
#     print(f'line = {line}', end='')

# 直接去读全部遍历
lines = f.readlines()
print(lines)

f.close()