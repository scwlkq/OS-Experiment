package com.suncong.os.renwu;

import javafx.animation.PauseTransition;
import javafx.util.Pair;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;


/**
 * @author SunCong
 * @date 2022/11/22 16:59
 */
public class Fpf {

    /**
     * 匿名Comparator实现
     */
    public static Comparator<Pair<Integer, Integer>> priorityComparator = new Comparator<Pair<Integer, Integer>>() {

        @Override
        public int compare(Pair<Integer, Integer> c1, Pair<Integer, Integer> c2) {
            return c2.getKey() - c1.getKey() - 1;
        }
    };


    public static void main(String[] args) throws InterruptedException {

        int n = 10;

        // 到达时间，服务时间，优先级
        // 这个副本用来扣除已经服务过的时间，原来的serviceTime用来计算最后的一些数值
        int[] arrivalTime = {1, 2, 3, 5, 9};
        int[] serviceTime = {6, 4, 1, 1, 3};
        int[] serviceTimeCopy = {6, 4, 1, 1, 3};
        int[] priority = {3, 3, 4, 5, 4};


        // 记录周转时间，带权周转时间
        double[] turnAroundTime = new double[n];
        double[] weightedTurnAroundTime = new double[n];


        double avgTurnAroundTime = 0, avgWeightedTurnAroundTime = 0;

        //              按照优先级排序的优先队列
        Queue<Pair<Integer, Integer>> runningProcesses = new PriorityQueue<>(7, priorityComparator);

        // 进程信息
        int num = 5;

        // 用到达的时间排序    当进程到达的时候，判断当前的执行进程的优先级和到达的进程的优先级
        for (int i = 0; i < num; i++) {
            // 双指针算法
            for (int j = i + 1; j < num; j++) {
                if (arrivalTime[i] > arrivalTime[j]) {
                    int a1 = arrivalTime[i], a2 = arrivalTime[j];
                    a1 = a1 + a2;
                    a2 = a1 - a2;
                    a1 = a1 - a2;
                    arrivalTime[i] = a1;
                    arrivalTime[j] = a2;

                    int s1 = serviceTime[i], s2 = serviceTime[j];
                    s1 = s1 + s2;
                    s2 = s1 - s2;
                    s1 = s1 - s2;
                    serviceTime[i] = s1;
                    serviceTime[j] = s2;

                    int p1 = priority[i], p2 = priority[j];
                    p1 = p1 + p2;
                    p2 = p1 - p2;
                    p1 = p1 - p2;
                    priority[i] = p1;
                    priority[j] = p2;
                }
            }
        }

        System.out.println("============================进程的信息 ==================================");
        // 打印各个进程排序后的编号、到达时间、服务时间、优先级
        System.out.println("进程\t\t到达时间\t\t服务时间\t\t优先级");
        for (int i = 0; i < num; i++) {
            System.out.println((i + 1) + "\t\t" + arrivalTime[i] + "\t\t\t" + serviceTime[i] + "\t\t\t" + priority[i]);
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
            //      有的话  放到优先队列里  他会自己排序 按照我们定义的cmp
            for (int i = 0; i < num; i++) {
                if (arrivalTime[i] == nowTime) {
                    Pair<Integer, Integer> pair = new Pair<>(priority[i], i);
                    runningProcesses.add(pair);
                }
            }

            // 3.优先队列最前面的一个进程serviceTimeCopy-1
            if (runningProcesses.size() > 0) {

                // 取出优先级最高的进程
                Pair<Integer, Integer> temp = runningProcesses.peek();
                // 取出进程编号，拿到编号即可索引全部信息
                int firstIndex = temp.getValue();
                serviceTimeCopy[firstIndex]--;
                // 打印当前时间
                System.out.println("第" + nowTime + "秒，正在执行" + (firstIndex + 1) + "号线程，剩余" + serviceTimeCopy[firstIndex] + "服务时间");

                // 3.1 该进程没有执行完，继续

                // 3.2 该进程执行完了，计算他的周转时间和带权周转时间，踢出优先队列
                if (serviceTimeCopy[firstIndex] == 0) {
                    turnAroundTime[firstIndex] = nowTime - arrivalTime[firstIndex] + 1;
                    weightedTurnAroundTime[firstIndex] = turnAroundTime[firstIndex] / serviceTime[firstIndex];
                    System.out.println("进程" + (firstIndex) + "在" + nowTime + "执行完成");
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
