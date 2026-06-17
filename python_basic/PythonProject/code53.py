student = {
    "id": "711512821",
    "name": "张三",
    "age": 15
}

for key in student:
    print(f" k = {key} val = {student[key]}")

print(student.keys())
print(student.values())
print(student.items())

for item in student.items():
    # item 是元组 可以用 [0] [1] 来访问
    print(f" item = {item} item[0] = {item[0]} item[1] = {item[1]}")
