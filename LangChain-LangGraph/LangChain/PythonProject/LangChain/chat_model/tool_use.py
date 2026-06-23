from typing import Annotated

from langchain_core.tools import tool

# # 方案1
# @tool
# def add(a: int, b: int) -> int:
#     '''
#     计算两数之和
#
#     Args:
#         a: 第一个参数
#         b: 第二个参数
#
#     Return: 两数之和
#     '''
#     return a + b

# # 方案2
# class AddInput(BaseModel):
#     '''
#     计算两数之和
#
#     Args:
#         a: 第一个参数
#         b: 第二个参数
#
#     Return: 两数之和
#     '''
#     a: int = Field(..., description="第一个参数")
#     b: int = Field(..., description="第二个参数")
#
# @tool(args_schema=AddInput)
# def add(a: int, b: int):
#     return a + b

# 方案3
@tool()
def add(
        a: Annotated[int, ..., "第一个参数"],
        b: Annotated[int, ..., "第二个参数"],
) -> int:
    '''
    计算两数之和

    Args:
        a: 第一个参数
        b: 第二个参数

    Return: 两数之和
    '''
    return a + b
print(add.invoke({"a": 1, "b": 2}))
print(add.name)
print(add.description)
print(add.args)
