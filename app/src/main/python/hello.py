from bs4 import BeautifulSoup
import requests
import numpy as np
import tensorflow as tf
import sys
import os
from java import jclass

def greet(name):
    print("--- hello,%s ---" % name)

def add(a,b):
    return a + b

def sub(count,a=0,b=0,c=0):
    return count - a - b -c

def get_list(a,b,c,d):
    return [a,b,c,d]

def print_list(data):
    print(type(data))
    # 遍历Java的ArrayList对象
    for i in range(data.size()):
        print(data.get(i))

# python调用Java类
def get_java_bean():
    JavaBean = jclass("com.mrl.sparkdroid.JavaBean")
    jb = JavaBean("python")
    jb.setData("json")
    jb.setData("xml")
    jb.setData("xhtml")
    return jb

# 爬取网页并解析
def http_test():
    r = requests.get("https://www.baidu.com/")
    r.encoding ='utf-8'
    bsObj = BeautifulSoup(r.text,"html.parser")
    for node in bsObj.findAll("a"):
        print("---**--- ", node.text)

#测试numpy
def numpy_test():
    y = np.zeros((5,), dtype = np.int)
    print(y)

def tensor_test():
    a = tf.placeholder(tf.int16)
    b = tf.placeholder(tf.int16)
    add = tf.add(a, b)
    mul = tf.multiply(a, b)
    with tf.Session() as sess:
        # Run every operation with variable input
        print("Addition with variables: %i" % sess.run(add, feed_dict={a: 2, b: 3}))
        print("Multiplication with variables: %i" % sess.run(mul, feed_dict={a: 2, b: 3}))
