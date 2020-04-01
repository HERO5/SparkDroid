import tensorflow.compat.v1 as tf
tf.disable_v2_behavior()

def train(host=None):
    if host==None:
        host = "192.168.1.12:2223"
    with tf.device("/job:ps/task:0"):
        x = tf.Variable(tf.ones([2, 2]))
        y = tf.Variable(tf.ones([2, 2]))

    with tf.device("/job:worker/task:0"):
        z = tf.matmul(x, y) + x

    with tf.device("/job:worker/task:1"):
        z = tf.matmul(z, x) + x

    with tf.Session("grpc://"+host) as sess:
        sess.run(tf.global_variables_initializer())
        val = sess.run(z)
        print(val)

    return val