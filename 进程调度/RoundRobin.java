package com.suncong.os.jincheng;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @author SunCong
 * @date 2022/11/23 17:09
 * 时间片轮转
 */
public class RoundRobin {

    /**
     * 时间片长度
     */
    public static final Integer TIME_SLICE = 2;


    public static void main(String[] args) {

        Queue<Integer> queue = new LinkedList<>();

        int num = 5;

        // 到达时间，服务时间，优先级
        // 这个副本用来扣除已经服务过的时间，原来的serviceTime用来计算最后的一些数值
        int[] arrivalTime = {1, 2, 3, 5, 9};
        int[] serviceTime = {6, 4, 1, 1, 3};
        int[] serviceTimeCopy = {6, 4, 1, 1, 3};

        System.out.println("============================进程的信息 ==================================");
        // 打印各个进程排序后的编号、到达时间、服务时间、优先级
        System.out.println("进程\t\t到达时间\t\t服务时间");
        for (int i = 0; i < num; i++) {
            System.out.println((i + 1) + "\t\t" + arrivalTime[i] + "\t\t\t" + serviceTime[i]);
        }
        System.out.println("=========================================================================");

        System.out.println("============================ 执行过程 ====================================");

        int nowTime = 1;
        boolean isFinished = false;

        while (true) {
            // 1.判断所有进程是否都结束了
            isFinished = true;
            for (int i = 0; i < num; i++) {
                if (serviceTimeCopy[i] > 0) {
                    isFinished = false;
                    break;
                }
            }
            if (isFinished) {
                break;
            }

            // 2.判断当前时间点有没有进程进来
            for (int i = 0; i < num; i++) {
                // 2.1 有进程进来的话，就加到队列的最后。进程进入默认排在就绪的队列里
                if (arrivalTime[i] == nowTime) {
                    queue.add(i);
                }
            }
            // 通过取余判断是否当前的时间片执行完毕

            int index = queue.element();
            serviceTimeCopy[index]--;
            System.out.println("第" + nowTime + "秒在执行" + (index + 1) + "进程");
            // 判断有没有结束
            if (serviceTimeCopy[index] == 0) {
                System.out.println("第" + (index + 1) + "号进程在" + nowTime + "秒执行完毕");
                queue.poll();
            }
            // 当前时间片为2，余数为0的时候，执行完当前时间片就要切换
            if (nowTime % TIME_SLICE == 0) {
                queue.add(queue.poll());
            }
            nowTime++;

        }
        System.out.println("所有进程均执行完毕，耗时" + nowTime);

    }

}
