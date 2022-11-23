import threading
from queue import Queue
from random import random
from time import sleep


class Monitor:  # 管程相当于一些操作的封装
    forks_ready = [threading.Semaphore(1) for i in range(5)]  # 同步信号量
    forks = [True, True, True, True, True]  # 这个餐具是否正在被使用

    def get_fork(self, p_id):
        # print(p_id)
        left = p_id
        p_id += 1
        right = p_id % 5

        # 抢左边的餐具
        self.forks_ready[left].acquire(timeout=2)
        # print(self.forks[left])
        self.forks[left] = False

        # 抢右边的餐具
        self.forks_ready[right].acquire(timeout=2)
        # print(self.forks[right])
        self.forks[right] = False

    def release_fork(self, p_id):
        left = p_id
        right = (++p_id) % 5

        self.forks[left] = True
        self.forks_ready[left].release()

        self.forks[right] = True
        self.forks_ready[right].release()


class Philosopher:
    def __init__(self, monitor):  # 初始化，传入管程对象
        self.monitor = monitor

    def run(self, p_id):
        while True:
            print(str(p_id) + '号哲学家正在思考')
            sleep(2)

            # print(str(p_id) + '号哲学家拿之前' + str(self.monitor.forks))
            self.monitor.get_fork(p_id)
            # print(str(p_id) + '号哲学家拿之后' + str(self.monitor.forks))
            # print(str(m.forks))
            print(str(p_id) + '号哲学家拿到两双筷子，正在吃饭')
            sleep(random())  # 模拟吃饭时间

            # print(str(p_id) + '号哲学家放回之前' + str(self.monitor.forks))
            self.monitor.release_fork(p_id)
            # print(str(p_id) + '号哲学家放回之后' + str(self.monitor.forks))


if __name__ == '__main__':
    m = Monitor()  # 实例化管程

    philosopher = Philosopher(m)  # 实例化哲学家

    for i in range(5):
        threading.Thread(target=philosopher.run, args=(i,)).start()
