import tensorflow.compat.v1 as tf
tf.disable_v2_behavior()

tf.app.flags.DEFINE_string("ps_hosts", "192.168.1.12:2222", "ps hosts")
tf.app.flags.DEFINE_string("worker_hosts", "192.168.1.12:2223,192.168.1.12:2224", "worker hosts")
tf.app.flags.DEFINE_string("job_name", "worker", "'ps' or'worker'")
tf.app.flags.DEFINE_integer("task_index", 0, "Index of task within the job")

FLAGS = tf.app.flags.FLAGS

def main(_):
    ps_hosts = FLAGS.ps_hosts.split(",")
    worker_hosts = FLAGS.worker_hosts.split(",")
    # create cluster
    cluster = tf.train.ClusterSpec({"ps": ps_hosts, "worker": worker_hosts})
    # create the server
    server = tf.train.Server(cluster, job_name=FLAGS.job_name, task_index=FLAGS.task_index)

    server.join()

def train(ps_hosts, worker_hosts, job_name, task_index):
    FLAGS.ps_hosts = ps_hosts
    FLAGS.worker_hosts = worker_hosts
    FLAGS.job_name = job_name
    FLAGS.task_index = task_index
    tf.app.run(main)

'''
python example.py --job_name=ps --task_index=0
python example.py --job_name=worker --task_index=0
python example.py --job_name=worker --task_index=1
'''