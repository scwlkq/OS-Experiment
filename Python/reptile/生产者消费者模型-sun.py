# 202083290053孙聪 操作系统 实践报告1 生产者与消费者问题
import threading
from string import Template
from tkinter import Tk, Label, Canvas, Button
from queue import Queue
from random import random
from time import sleep

# 将缓冲区可视化  最多5个消费者 最多5个生产者

empty = threading.Semaphore(10)
# 互斥信号量，用于保护缓冲区
mutex = threading.Semaphore(1)
# 同步信号量，表示产品的数量，即非空缓冲区的数量
full = threading.Semaphore(0)
# 创建上限为10的缓冲区
queue = Queue(10)


class BufferZone:
    # 实例化TK对象，用于创建窗口
    buffer_zone = Tk()
    # 设置窗口大小
    buffer_zone.geometry('400x600')
    # 禁止窗口缩放
    buffer_zone.resizable(0, 0)
    # 设置窗口标题
    buffer_zone.title('缓冲区')
    buffer = Canvas(buffer_zone, relief='ridge', width=380, height=580, bg='grey')
    # 此时缓冲区的product数量
    button_list = []
    # 缓冲区的product数量
    NUM_PRODUCTS_PRODUCTS = 0

    # 生产者消费者最多十个
    producer1 = Label(buffer_zone)
    producer2 = Label(buffer_zone)
    producer3 = Label(buffer_zone)
    producer4 = Label(buffer_zone)
    producer5 = Label(buffer_zone)
    producer6 = Label(buffer_zone)
    producer7 = Label(buffer_zone)
    producer8 = Label(buffer_zone)
    producer9 = Label(buffer_zone)
    producer10 = Label(buffer_zone)

    consumer1 = Label(buffer_zone)
    consumer2 = Label(buffer_zone)
    consumer3 = Label(buffer_zone)
    consumer4 = Label(buffer_zone)
    consumer5 = Label(buffer_zone)
    consumer6 = Label(buffer_zone)
    consumer7 = Label(buffer_zone)
    consumer8 = Label(buffer_zone)
    consumer9 = Label(buffer_zone)
    consumer10 = Label(buffer_zone)

    def __init__(self):
        # 初始化buffer
        self.buffer.place(x=10, y=10)

    # 缓冲区内生产产品 这里封装一个Canvas的方法供外部调用，改变product的颜色
    # obj_index参数说明的是当前生产的这个是第几个产品
    def add_product_in_buffer(self, obj_index):
        # 实例化按钮  就是生产了一个产品并加上序号
        btn = Button(self.buffer_zone, text='产品' + str(obj_index), bg='red', width=40)
        x = 195
        y = 40 * (self.NUM_PRODUCTS_PRODUCTS + 1)
        self.buffer.create_window(x, y - 20, window=btn)  # 将按钮放入缓冲区
        self.NUM_PRODUCTS_PRODUCTS += 1  # 缓冲区中按钮数量加一
        self.button_list.append({
            'product': btn,
        })  # 将按钮放入缓冲区中

    # 缓冲区内消耗产品
    def reduce_product_in_buffer(self):
        # 如果缓冲区中有按钮
        if self.button_list:
            # 删除最后一个按钮
            last_button = self.button_list.pop()  # 弹出最后一个按钮
            self.NUM_PRODUCTS_PRODUCTS -= 1
            last_button['product'].destroy()  # 销毁按钮

    def run(self):
        self.buffer_zone.mainloop()  # 进入消息循环


# 生产者类
class ProductProducer:
    # 提供给线程使用
    def run(self, producer_index):
        while True:
            # 生产产品
            sleep(random())  # 模拟生产产品的时间
            # 将产品放入缓冲区
            self.product(producer_index)
            template = Template("生产者$a生产了一个product,此时缓冲区剩余$a个product")
            result = template.substitute(a=producer_index, b=buffer_zone.NUM_PRODUCTS_PRODUCTS)
            print(result)

    def product(self, producer_index):
        empty.acquire()  # 实现同步，消耗一个空闲缓冲区
        mutex.acquire()  # 实现互斥，消耗一个互斥信号量

        # 将产品放入缓冲区
        sleep(random())  # 模拟将产品放入缓冲区的时间
        buffer_zone.add_product_in_buffer(buffer_zone.NUM_PRODUCTS_PRODUCTS + 1)  # 模拟将产品放入缓冲区
        queue.put(
            {}  # 将生产的产品放入缓冲区 (其实这里的产品是{}，只是模拟一下）
        )

        mutex.release()  # 释放互斥信号量
        full.release()  # 实现同步，增加一个产品


# 消费者类
class ProductConsumer:
    # 提供给线程使用
    def run(self, consumer_index):
        while True:
            # 从缓冲区取出产品
            self.consumer()
            # 消费产品
            sleep(random())  # 模拟消费产品的时间
            template = Template("消费者$a消费了一个product,此时缓冲区剩余$b个product")
            result = template.substitute(a=consumer_index, b=buffer_zone.NUM_PRODUCTS_PRODUCTS)
            print(result)

    def consumer(self):
        full.acquire()  # 实现同步，消耗一个产品
        mutex.acquire()  # 实现互斥，消耗一个互斥信号量

        # 从缓冲区取出产品
        sleep(random())  # 模拟从缓冲区取出产品的时间
        queue.get()  # 从缓冲区取出产品
        buffer_zone.reduce_product_in_buffer()  # 模拟从缓冲区中删除产品

        mutex.release()  # 释放互斥信号量
        empty.release()  # 实现同步，增加一个空闲缓冲区


if __name__ == "__main__":
    buffer_zone = BufferZone()
    producer = ProductProducer()  # 实例化生产者
    consumer = ProductConsumer()  # 实例化消费者

    # 创建5个生产者线程
    for i in range(10):
        threading.Thread(target=producer.run, args=(i + 1,)).start()

    # 创建3个消费者线程
    for i in range(10):
        threading.Thread(target=consumer.run, args=(i + 1,)).start()

    buffer_zone.run()
