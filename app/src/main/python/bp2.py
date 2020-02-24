import tensorflow as tf
import input_data

def train(params=None):
    mnist = input_data.read_data_sets('/storage/emulated/0/tensor-data/', one_hot=True)

    num_classes = 10
    input_size = 784
    hidden_units_size = 30
    batch_size = 100
    training_iterations = 10000

    X = tf.placeholder(tf.float32, shape=[None, input_size])
    Y = tf.placeholder(tf.float32, shape=[None, num_classes])

    W1 = tf.Variable(tf.truncated_normal([input_size, hidden_units_size], stddev=0.1, name='w1'))
    B1 = tf.Variable(tf.constant(0.1), [hidden_units_size])
    W2 = tf.Variable(tf.truncated_normal([hidden_units_size, num_classes], stddev=0.1, name='w2'))
    B2 = tf.Variable(tf.constant(0.1), [num_classes])

    hidden_opt = tf.matmul(X, W1) + B1
    hidden_opt = tf.nn.relu(hidden_opt)
    final_opt = tf.matmul(hidden_opt, W2) + B2
    final_opt = tf.nn.relu(final_opt)

    loss = tf.reduce_mean(tf.nn.softmax_cross_entropy_with_logits_v2(labels=Y, logits=final_opt))
    opt = tf.train.GradientDescentOptimizer(0.05).minimize(loss)
    init = tf.global_variables_initializer()
    correct_prediction = tf.equal(tf.argmax(Y, 1), tf.argmax(final_opt, 1))
    accuracy = tf.reduce_mean(tf.cast(correct_prediction, 'float'))

    sess = tf.Session()
    sess.run(init)
    for i in range(training_iterations):
        batch = mnist.train.next_batch(batch_size)
        batch_input = batch[0]
        batch_labels = batch[1]
        training_loss = sess.run([opt, loss], feed_dict={X: batch_input, Y: batch_labels})
        if i % 1000 == 0:
            train_accuracy = accuracy.eval(session=sess, feed_dict={X: batch_input, Y: batch_labels})
            print("step : %d, training accuracy = %g " % (i, train_accuracy))

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
        fc2 = graph.get_tensor_by_name("w1:0")
        print(fc2)  # 得到的是shape
        params2 = sess.run(fc2)
        print(params2)  # 这样可以输出参数
        #return [params1, params2]