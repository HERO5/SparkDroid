''' 这是一段测试代码，用来解决"n皇后"问题，实际上就相当于主流分布式框架中提交的"任务.jar"，格式需要说明一下：
     任务源码中必须包含一个名为main的函数，需要且仅需要一个数组型参数，返回值格式必须为[complete, intermission]
     "complete"是一个数组，存储了本次处理完成的数据，"intermission"是一个数组，存储了本次处理未完成的中间数据
     (相当于本次任务的子任务的参数)。比如这段代码也是合法的：
     def main(params):
        result=[["ABGX", "IUDL"],["123", "456"]]
        print(result[0], result[1])
        return result
     注意！上面这个任务会让worker一种工作下去，因为他返回的中间数据永远不会为空！
'''

''' 👇这段代码粘贴到"source python"框中 '''

#!/usr/bin/python
# -*- coding: UTF-8 -*-

import queue

#皇后数量，因为app中插入了大量日志输出，所以处理较慢。n<11时的处理时间一般在1s内，n<=12时的处理时间一般在1min左右
n = 12
#本次处理的结束标志: 如果生成的子任务数超过了上限，就终止本次处理
max_qsize = 0x2000
base = 'A'
die_limit = ''
for i in range(n):
    die_limit += '1'
def_limit = ''
for i in range(n):
    def_limit += '0'


def check(state):
    limit = def_limit
    width = len(state)
    for i in range(width):
        ind = ord(state[i]) - ord(base) + 1
        ind_l = ind - (width - i)
        ind_r = ind + (width - i)
        limit = limit[:(ind - 1)] + '1' + limit[ind:]
        if ind_l >= 1:
            limit = limit[:(ind_l - 1)] + '1' + limit[ind_l:]
        if ind_r <= n:
            limit = limit[:(ind_r - 1)] + '1' + limit[ind_r:]
    return limit


def main(params):
    complete = []
    intermission = []
    dict = {}
    q = queue.Queue()
    for param in params:
        print("params", param)
        q.put(param)
        dict[param] = check(param)
    while q.empty() != True:
        state = q.get()
        limit = dict.pop(state)
        for i in range(n):
            if limit[i] == '1':
                continue
            c = chr(i + ord(base))
            new_state = state + c
            # print(len(new_state))
            if len(new_state) == n:
                complete.append(new_state)
                continue
            new_limit = check(new_state)
            if new_limit != die_limit:
                q.put(new_state)
                dict[new_state] = new_limit
        # 队列内存已达上限，终止运行，返回已处理完成数据的和未完成的数据
        if q.qsize() > max_qsize:
            while q.empty() != True:
                intermission.append(q.get())
            break
    # print("complete:", len(complete), "intermission:", len(intermission))
    res = [complete, intermission]
    return res

''' 👆这段代码粘贴到"source python"框中 '''


''' 若想直接在python环境中跑，那么运行👇这段代码即可 '''

global_task = ['']
global_result = []
if __name__ == '__main__':
    while len(global_task) > 0 :
        result = main([global_task.pop()]);
        global_result += result[0]
        global_task += result[1]
        print('global_result:', len(global_result), 'global_task', len(global_task))

''' 若想直接在python环境中跑，那么运行👆这段代码即可 '''