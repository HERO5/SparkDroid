# python 3

import sys
import os
from java import jclass
from inspect import isfunction
from venv import logger

class CompileMixin:

    @staticmethod
    def _compile(code, funcName):
        ns = {}
        try:
            exec(code, ns)
        except Exception as e:
            logger.error("code: `{0}` 编译时出错，exception: {1}".format(code, e))
            raise e

        func = None
        may_be_function = ns[funcName]
        if isfunction(may_be_function):
            func = may_be_function
        if not func:
            logger.error("code: `{0}` 没有找到可用的函数".format(code))
            raise ValueError("Code Error , function not found")

        return func

def call(source, funcName, params):
    func = CompileMixin._compile(source, funcName)
    print("func: ", func)
    return func(params)

def call1(source, funcName, params):
    #func必须有返回值，且格式为[Object[], Object[]]
    func = CompileMixin._compile(source, funcName)
    #print("func: ", func)
    ResultOfCall = jclass("com.mrl.communicate.middle.ResultOfCall")
    result = ResultOfCall()
    resultRaw = func(params)
    result.setComplete(resultRaw[0])
    result.setIntermission(resultRaw[1])
    return result