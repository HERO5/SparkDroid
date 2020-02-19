# SparkDroid(安卓系统上的分布式)

Hi! 这是一款开源android应用，我想通过它来实现移动端的分布式任务处理，希望做到去中心化、免配置、易使用。因为目前所有的分布式框架都是基于pc的，若要移植到android，需有root权限，且需要复杂的配置。虽然android也是基于Linux的系统，但封印了好多功能，尤其是root权限的限制，导致普通android手机不能直接部署现有分布式框架。总而言之，现有的分布式很好，但太重量级了。

# 使用说明

目前此app的功能非常简单，只是一原型，实现了简单的任务下发和任务提交功能，使用时一定要在**同一局域网下！！！**，因为手机运营商不允许我们直接p2p。下面是使用步骤：

- Step0: "source python"框输入要执行的任务代码(测试代码demo及代码格式说明在**"/doc/demo/main.py"**中)。

- Step1: master端输入master启动端口。master的IP固定为127.0.0.1，修改无效；端口任意，只要是未被占用就行。

- Step2: 点击MASTER按钮以启动“工头”。点击一次master之后会从"source python"复制多份相同的任务，等待Worker的任务请求。

- Step3: worker端输入master的IP和端口。再次申明，IP必须填局域网IP，因为手机运营商不允许我们直接p2p。

- Step4: 点击WORKER按钮以启动一个“工人”。每点击一下WORKER就会生成一个worker，worker的端口是自动分配的。master端当然也可以启动自己的worker，这时IP可以填127.0.0.1，也可以填局域网下的IP。

- Step5: 点击STOP&CLEAN以关闭本地所有master和worker并清理控制台

# 运行流程

- 流程非常简单，就是普通的tcp通信，然后自己写了个处理逻辑。以线程为单位，线程之间的通信流程如下：

![正常工作中···](doc/pic/working.png)

- 如果没有任务了：

![任务都做完了···](doc/pic/waiting.png)

# 其他

目前此应用还不能称得上分布式，因为性能还很低，可以执行的任务很有限，不过这只是个开始，慢慢做吧。
