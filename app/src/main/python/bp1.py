'''
正常运行
'''
import tensorflow as tf
import input_data

def train(params=None):
    num_classes = 10
    input_size = 784
    hidden_units_size = 30
    batch_size = 100
    training_iterations = 10000

    # 加载数据
    mnist = input_data.read_data_sets('/storage/emulated/0/tensor-data/', one_hot=True)
    x_train = mnist.train.images
    y_train = mnist.train.labels
    x_test = mnist.test.images
    y_test = mnist.test.labels

    # 定义变量
    x = tf.placeholder(tf.float32, shape=[None, input_size])
    y_ = tf.placeholder(tf.float32, shape=[None, num_classes])
    w = tf.Variable(tf.truncated_normal([input_size, num_classes]), name='w1')
    b = tf.Variable(tf.constant(0.01, shape=[num_classes]), name='b1')
    y = tf.nn.softmax(tf.matmul(x, w) + b)

    # 定义损失
    loss = -tf.reduce_mean(y_ * tf.log(y))
    optimizer = tf.train.GradientDescentOptimizer(0.2)
    train = optimizer.minimize(loss)

    # 初始化变量
    sess = tf.Session()
    sess.run(tf.global_variables_initializer())

    # 验证正确率
    accuracy_rate = tf.equal(tf.arg_max(y, 1), tf.arg_max(y_, 1))
    accuracy = tf.reduce_mean(tf.cast(accuracy_rate, 'float32'))

    # 训练
    for step in range(training_iterations):
        batch_x, batch_y = mnist.train.next_batch(batch_size)
        sess.run(train, feed_dict={x: batch_x, y_: batch_y})
        if step % 1000 == 0:
            print(step, sess.run(accuracy, feed_dict={x: x_test, y_: y_test}))

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
        params1 = sess.run(fc1)
        print(params1)  # 这样可以输出参数
        fc2 = graph.get_tensor_by_name("b1:0")
        print(fc2)
        params2 = sess.run(fc2)
        print(params2)