from langchain_core.messages import HumanMessage, SystemMessage
from langchain_core.output_parsers import StrOutputParser
from langchain_openai import ChatOpenAI



# 设置模型
model = ChatOpenAI(
    # api_key='',
    model='deepseek-v4-flash',
    base_url="https://dashscope.aliyuncs.com/compatible-mode/v1",
    # temperature=2,
    # timeout=None,
    max_tokens=20,
    organization='',
    max_retries=None,
)

# 定义消息
messages = [
    SystemMessage("请帮我补充下述句子，50字以内"),
    HumanMessage("我有一条鱼，__")
]

# 发送消息
result = model.invoke(messages)
# print(result)

# 设置输出解析器
parser = StrOutputParser()
print(parser.invoke(result))
