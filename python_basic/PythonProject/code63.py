import qrcode

ret = qrcode.make("https://gitee.com/wunameor/bite-code/blob/master/python_basic/PythonProject/code63.py")
ret.save('code63-url.png')
