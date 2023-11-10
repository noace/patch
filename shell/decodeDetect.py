#!/usr/bin/env python
#coding:utf-8
import chardet,sys
print(chardet.detect(open(sys.argv[1],'rb').read())['encoding'])