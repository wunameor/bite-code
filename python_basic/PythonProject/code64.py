import xlrd

# 读取到文件
file = xlrd.open_workbook('./code64-test.xlsx')

# 获取到表格
table = file.sheet_by_index(0)

count = 0
total_score = 0
for index in range(1, table.nrows):
    # 第 index 行 第 0 列
    class_id = table.cell_value(index, 0)
    if class_id == 1101:
        total_score += table.cell_value(index, 3)
        count += 1

print(f'平均分为 {total_score / count}')
