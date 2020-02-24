'''
会报如下错误，待解决
W/native: op_kernel.cc:1275 OP_REQUIRES failed at conv_ops.cc:398 : Resource exhausted: OOM when allocating tensor with shape[10000,28,28,32] and type float on /job:localhost/replica:0/task:0/device:CPU:0 by allocator cpu
E/AndroidRuntime: FATAL EXCEPTION: Thread-3
    Process: com.mrl.sparkdroid, PID: 28941
    com.chaquo.python.PyException: ResourceExhaustedError: OOM when allocating tensor with shape[10000,28,28,32] and type float on /job:localhost/replica:0/task:0/device:CPU:0 by allocator cpu
    	 [[Node: Conv2D = Conv2D[T=DT_FLOAT, data_format="NHWC", dilations=[1, 1, 1, 1], padding="SAME", strides=[1, 1, 1, 1], use_cudnn_on_gpu=true, _device="/job:localhost/replica:0/task:0/device:CPU:0"](Reshape, Variable/read)]]
    Hint: If you want to see a list of allocated tensors when OOM happens, add report_tensor_allocations_upon_oom to RunOptions for current allocation info.


    Caused by op 'Conv2D', defined at:
      File "/data/user/0/com.mrl.sparkdroid/files/chaquopy/AssetFinder/app/cnn.py", line 31, in train
        h_con1 = tf.nn.conv2d(x_images,w_con1,[1,1,1,1],padding='SAME')
      File "/data/user/0/com.mrl.sparkdroid/files/chaquopy/AssetFinder/requirements/tensorflow/python/ops/gen_nn_ops.py", line 956, in conv2d
        data_format=data_format, dilations=dilations, name=name)
      File "/data/user/0/com.mrl.sparkdroid/files/chaquopy/AssetFinder/requirements/tensorflow/python/framework/op_def_library.py", line 787, in _apply_op_helper
        op_def=op_def)
      File "/data/user/0/com.mrl.sparkdroid/files/chaquopy/AssetFinder/requirements/tensorflow/python/util/deprecation.py", line 454, in new_func
        return func(*args, **kwargs)
      File "/data/user/0/com.mrl.sparkdroid/files/chaquopy/AssetFinder/requirements/tensorflow/python/framework/ops.py", line 3155, in create_op
        op_def=op_def)
      File "/data/user/0/com.mrl.sparkdroid/files/chaquopy/AssetFinder/requirements/tensorflow/python/framework/ops.py", line 1717, in __init__
        self._traceback = tf_stack.extract_stack()

    ResourceExhaustedError (see above for traceback): OOM when allocating tensor with shape[10000,28,28,32] and type float on /job:localhost/replica:0/task:0/device:CPU:0 by allocator cpu
    	 [[Node: Conv2D = Conv2D[T=DT_FLOAT, data_format="NHWC", dilations=[1, 1, 1, 1], padding="SAME", strides=[1, 1, 1, 1], use_cudnn_on_gpu=true, _device="/job:localhost/replica:0/task:0/device:CPU:0"](Reshape, Variable/read)]]
    Hint: If you want to see a list of allocated tensors when OOM happens, add report_tensor_allocations_upon_oom to RunOptions for current allocation info.


        at <python>.tensorflow.python.client.session._do_call(session.py:1291)
        at <python>.tensorflow.python.client.session._do_run(session.py:1272)
        at <python>.tensorflow.python.client.session._run(session.py:1100)
        at <python>.tensorflow.python.client.session.run(session.py:877)
        at <python>.cnn.train(cnn.py:77)
        at <python>.chaquopy_java.call(chaquopy_java.pyx:283)
        at <python>.chaquopy_java.Java_com_chaquo_python_PyObject_callAttrThrows(chaquopy_java.pyx:255)
        at com.chaquo.python.PyObject.callAttrThrows(Native Method)
        at com.chaquo.python.PyObject.callAttr(PyObject.java:207)
        at com.mrl.sparkdroid.PythonTest.testTensor(PythonTest.java:19)
        at com.mrl.sparkdroid.MainActivity$3.run(MainActivity.java:93)
        at java.lang.Thread.run(Thread.java:784)
'''

import tensorflow as tf
import input_data

#权重函数
def weights(shape, name=None):
    initial = tf.truncated_normal(shape,stddev=0.1,dtype=tf.float32, name=name)
    return tf.Variable(initial)

#偏置项
def bias(shape):
    initial = tf.constant(0.1,shape=shape,dtype=tf.float32)
    return tf.Variable(initial)

def train(params=None):
    mnist = input_data.read_data_sets('/storage/emulated/0/tensor-data/', one_hot=True)
    #加载数据
    x_data = mnist.train.images
    y_data = mnist.train.labels
    x_test = mnist.test.images
    y_test = mnist.test.labels

    #输入值
    xs = tf.placeholder(tf.float32,shape=[None,784])
    ys = tf.placeholder(tf.float32,shape=[None,10])
    x_images = tf.reshape(xs,[-1,28,28,1])

    #第一层卷积
    #con_1
    w_con1 = weights([5,5,1,32], "w1")
    b_con1 = bias([32])
    h_con1 = tf.nn.conv2d(x_images,w_con1,[1,1,1,1],padding='SAME')
    h_relu1 = tf.nn.relu(h_con1 + b_con1)
    #pool1
    h_pool1 = tf.nn.max_pool(h_relu1,ksize=[1,2,2,1],strides=[1,2,2,1],padding='SAME')

    #第二层卷积
    #con2
    w_con2 = weights([5,5,32,64], "w2")
    b_con2 = bias([64])
    h_con2 = tf.nn.conv2d(h_pool1,w_con2,strides=[1,1,1,1],padding='SAME')
    h_relu2 = tf.nn.relu(h_con2)
    #pool2
    h_pool2 = tf.nn.max_pool(h_relu2,ksize=[1,2,2,1],strides=[1,2,2,1],padding='SAME')

    #全连接层
    w_fc1 = weights([7*7*64,1024], "w3")
    b_fc1 = bias([1024])
    h_pool2_flat = tf.reshape(h_pool2,[-1,7*7*64])
    h_fc1 = tf.nn.relu(tf.matmul(h_pool2_flat,w_fc1) + b_fc1)

    #drop_out
    keep_pro = tf.placeholder(dtype=tf.float32)
    h_fc1_drop = tf.nn.dropout(h_fc1,keep_prob=keep_pro)

    #输出层
    w_fc2 = weights([1024,10], "w4")
    b_fc2 = bias([10])
    h_fc2 = tf.nn.softmax(tf.matmul(h_fc1_drop,w_fc2) + b_fc2)

    #损失函数
    loss = -tf.reduce_mean(ys*tf.log(h_fc2))
    train = tf.train.AdamOptimizer(1e-4).minimize(loss)
    #初始化变量
    # 初始化变量
    sess = tf.Session()
    sess.run(tf.global_variables_initializer())

    #计算误差
    accuracy = tf.equal(tf.arg_max(ys,1),tf.arg_max(h_fc2,1))
    accuracy = tf.reduce_mean(tf.cast(accuracy,tf.float32))

    #开始训练
    for step in range(5000):
        batch_x,batch_y = mnist.train.next_batch(100)
        sess.run(train,feed_dict={xs:batch_x,ys:batch_y,keep_pro:0.8})
        if step % 100 == 0 :
            print(step,sess.run(accuracy,feed_dict={xs:mnist.test.images,ys:mnist.test.labels,keep_pro:1}))

    if not tf.gfile.Exists('/storage/emulated/0/tensor-model/'):
        tf.gfile.MakeDirs('/storage/emulated/0/tensor-model/')
    saver = tf.train.Saver()  # 保存模型 实例化
    saver.save(sess, '/storage/emulated/0/tensor-model/my_model.ckpt')

def read_model():
    #打印参数
    with tf.Session() as sess:
        ckpt = tf.train.get_checkpoint_state('/storage/emulated/0/tensor-model/')
        saver = tf.train.import_meta_graph(ckpt.model_checkpoint_path + '.meta')
        saver.restore(sess, ckpt.model_checkpoint_path)
        graph = tf.get_default_graph()
        fc1 = graph.get_tensor_by_name("w1:0")
        print(fc1)  # 得到的是shape
        params = sess.run(fc1)
        print(params)  # 这样可以输出参数