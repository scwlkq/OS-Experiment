package com.suncong.os.renwu;

import javafx.util.Pair;

import java.util.PriorityQueue;
import java.util.Queue;

/**
 * @author SunCong
 * @date 2022/11/22 22:06
 */
public class Fcfs {

    public static void main(String[] args) {
        int n = 10;

        // 到达时间，服务时间，优先级
        // 这个副本用来扣除已经服务过的时间，原来的serviceTime用来计算最后的一些数值
        int[] arrivalTime = {1, 2, 3, 5, 9};
        int[] serviceTime = {6, 4, 1, 1, 3};
        int[] serviceTimeCopy = {6, 4, 1, 1, 3};

        // 记录周转时间，带权周转时间
        double[] turnAroundTime = new double[n];
        double[] weightedTurnAroundTime = new double[n];


        double avgTurnAroundTime = 0, avgWeightedTurnAroundTime = 0;

        //              按照优先级排序的优先队列
        Queue<Integer> runningProcesses = new PriorityQueue<>(7);

        // 进程信息
        int num = 5;


        System.out.println("============================进程的信息 ==================================");
        // 打印各个进程排序后的编号、到达时间、服务时间、优先级
        System.out.println("进程\t\t到达时间\t\t服务时间");
        for (int i = 0; i < num; i++) {
            System.out.println((i + 1) + "\t\t" + arrivalTime[i] + "\t\t\t" + serviceTime[i] + "\t\t\t");
        }

        System.out.println("=========================================================================");

        System.out.println("============================ 执行过程 ====================================");

        // 一些进程运行时的参数
        // 以s为单位，逐渐递增的，方便统计时间
        int nowTime = 1;
        // int totTime = 0;   // 总时间 这个就不用定义了，就是当所有的进程都执行完了，最后的nowTime  从0s开始
        boolean isFinished = false;

        //运行
        while (true) {

            // 1.判断所有的进程是否结束了
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

            // 2.判断当前是否有线程进来
            //      有的话  放到优先队列里
            for (int i = 0; i < num; i++) {
                if (arrivalTime[i] == nowTime) {
                    runningProcesses.add(i);
                }
            }

            // 3.优先队列最前面的一个进程serviceTimeCopy-1
            if (runningProcesses.size() > 0) {

                // 取出优先级最高的进程
                Integer index = runningProcesses.peek();
                // 取出进程编号，拿到编号即可索引全部信息

                serviceTimeCopy[index]--;
                // 打印当前时间
                System.out.println("第" + nowTime + "秒，正在执行" + (index + 1) + "号线程，剩余" + serviceTimeCopy[index] + "服务时间");

                // 3.1 该进程没有执行完，继续

                // 3.2 该进程执行完了，计算他的周转时间和带权周转时间，踢出优先队列
                if (serviceTimeCopy[index] == 0) {
                    turnAroundTime[index] = nowTime - arrivalTime[index] + 1;
                    weightedTurnAroundTime[index] = turnAroundTime[index] / serviceTime[index];
                    System.out.println("进程" + (index) + "在" + nowTime + "执行完成");
                    // kick
                    runningProcesses.remove();
                }
            }
//            // 模拟时间
//            sleep(1000);
            // 当前结束之后 时间++
            nowTime++;

        }

        System.out.println("============================= 执行总结 =============================");
        // 打印进程执行情况
        System.out.println("进程\t到达时间\t服务时间\t周转时间\t带权周转时间");
        for (int i = 0; i < num; i++) {
            System.out.println((i + 1) + "\t" + arrivalTime[i] + "\t\t" + serviceTime[i] + "\t\t" + turnAroundTime[i] + "\t\t" + weightedTurnAroundTime[i]);
        }

        for (int i = 0; i < num; i++) {
            avgTurnAroundTime += turnAroundTime[i];
            avgWeightedTurnAroundTime += weightedTurnAroundTime[i];
        }

        System.out.println("所有进程均执行完毕，耗时" + nowTime);

        // 计算平均周转时间 和平均带权周转时间
        avgTurnAroundTime /= num;
        avgWeightedTurnAroundTime /= num;
        System.out.println("平均周转时间：" + avgTurnAroundTime);
        System.out.println("平均带权周转时间：" + avgWeightedTurnAroundTime);

    }

}
