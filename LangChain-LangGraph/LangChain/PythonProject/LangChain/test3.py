# 设置模型
from langchain.chat_models import init_chat_model
from langchain_core.messages import SystemMessage, HumanMessage

# # 快速入门
# model = init_chat_model('deepseek-v4-flash', model_provider='deepseek')
# print(model.invoke("你是谁"))

# # 自定义配置
# model = init_chat_model(temperature=0.3)
# messages = [
#     SystemMessage("请帮我补充下述句子，50字以内"),
#     HumanMessage("我有一条鱼，__")
# ]
# print(model.invoke(messages, config={'configurable': {"model": 'deepseek-v4-flash'}}))

# 修改默认配置
# 设置模型
model = init_chat_model(
    model='deepseek-v4-flash',
    base_url="https://dashscope.aliyuncs.com/compatible-mode/v1",
    # 这个是起到默认API_KEY 的名称的配置，
    # 比如 openai 就是 OPENAI_API_KEY, deepseek 就是 DEEPSEEK_API_KEY
    model_provider="openai",
    max_tokens=1024,
    configurable_fields=("max_tokens", ), # 元组内必须添加 "," 如果写的是 ("max_tokens") 那么就不会生效
    config_prefix="pre" # 这个可以不加，但是最好加一下，以免区分不了默认的与后修改的
)

messages = [
    SystemMessage("请帮我补充下述句子，100字以内"),
    HumanMessage("一只橘黄色的小猫，__")
]

result = model.invoke(messages, config={
    "configurable": {
        "pre_max_tokens": 20,
    }
})

print(result.content)