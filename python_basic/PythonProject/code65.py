import random
from threading import Thread

from playsound import playsound
from pynput import keyboard

count = 0

paths = ['./sound/1.mp3', './sound/2.mp3', './sound/3.mp3']

def onRelease(key):
    print(key)
    global count
    count += 1
    if count % 10 == 0:
        index = random.randint(0, len(paths) - 1)
        print(f'index = {index} path = {paths[index]}')
        # 如果单线程的话会卡顿
        # playsound(paths[index])
        t = Thread(target=playsound, args=(paths[index], ))
        t.start()

# 启动监听器
listener = keyboard.Listener(on_release=onRelease)
listener.run()

