import time
import os
from flask import Blueprint, request, jsonify
from werkzeug import secure_filename
from app import app
import kookoo
import pickle
import sys, os, json, urllib2, urllib, time
import requests
@app.route('/')
@app.route('/index')
@app.route('/api/', methods=['GET'])
@app.route('/api', methods=['GET'])
def index():
    if request.method == 'GET':
        json_results = []
        output = {'greetings':'Welcome to api'}
        json_results.append(output)
    return jsonify(items=json_results)


BASE = "http://recordings.kookoo.in/nayyar_vipul/"
USERNAME = "hammadhaleem@gmail.com"
PASSWORD = "9H398339966MFP6"
def getfileandsave(j):
	for i,m in j.items():
		PATH ="/home/engineer/htdocs/gsfhack/"+str(m)+".wav"
		print PATH
		if os.path.isfile(PATH):
			print "Exist"
		else:
			stri = 'wget '+BASE+m+".wav"
			os.system(stri)
	os.system('pwd')

'''
def transcribe_audio_file(fi):
    try:
	url = 'https://api.nexiwave.com/SpeechIndexing/file/storage/' + USERNAME +'/recording/?authData.passwd=' + PASSWORD + '&auto-redirect=true&response=application/json'
    	r = requests.post(url, files={'mediaFileData': fi})
   	data = r.json()
    	transcript = data['text']
    	return  transcript
    except Exception,e:
	return 0

def getfile(url):
    #url ="http://recordings.kookoo.in/nayyar_vipul/qual1397938878.73.wav"
    print url
    try :
        response = urllib2.urlopen(url)
        html = response.read()
        return html
    except :
        print url
        return 0
'''

@app.route('/api/kookoo/process', methods=['GET','POST'])
@app.route('/api/kookoo/process/', methods=['GET','POST'])
def ProcessSong():
	lis = json.load( open( "save.json", "r" ) )
	stri = "<ul>"
	for i,j in lis.items():
		stri = stri + "<li><a href='/api/kookoo/process/?no="+str(i)+"'>"+str(i)+"</a></li>"
		getfileandsave(j)
	if request.values.get("no"):
		dici = lis[str(request.values.get("no"))]
                print dici
		for k,m in dici.iteritems():
			var  ="http://recordings.kookoo.in/nayyar_vipul/"+str(m)+".wav"
			#return var 
			#print m
			fileh = getfile(var)
                        if fileh == 0:
                            return "an error occured in fetching the file"
                        dici[k]= transcribe_audio_file(fileh)
		return jsonify(dici)
	else:
		return stri+"</ul>"
		

@app.route('/api/kookoo', methods=['GET','POST'])
@app.route('/api/kookoo/', methods=['GET','POST'])
def ProcessKooKooResponse():
    try:
	lis = json.load(open( "save.json", "r" ) )
    except :
	lis = {}
    r = kookoo.Response()
    curtime = int(time.time())
    dic ={'Please say your name':'name'+str(curtime),
          'Please Say your qualification':'qual'+str(curtime)}
    phone_number=""
    if request.values.get("event") == "NewCall":
        r.addPlayText("Welcome to Shine dot com registration service")
        phone_number=request.values.get("cid")
	for i , j in dic.items():
        	r.addPlayText(i)
        	r.addRecord(j)
        r.addPlayText("Thank You")
        r.addHangup()
    print str(r)
    #dic['proc'] = 0
    lis[phone_number]=dic
    json.dump( lis, open( "save.json", "w" ) )
    return str(r).replace("<Record","<Record format=\"wav\" silence=\"3\" maxduration=\"5\"")
    
@app.route('/api/upload/', methods=['POST'])
@app.route('/api/upload', methods=['POST'])
def upload():
    if request.method == 'POST':
        upload_folder = os.path.join(os.getcwd(),'/uploads/')
        if not os.path.exists(upload_folder):
            os.mkdir(upload_folder)
            return upload_folder
        files = []
        f = request.files['file']
        if f:
            filename = secure_filename(f.filename)
            f.save(os.path.join(upload_folder, filename))
        output = {
            'name': filename,
            'status': 'success',
	    'url' : 'http://107.161.27.22/gsfhack/uploads/'+str(filename)
        }
        files.append(output)
    return jsonify(files=files)
@app.route('/api/test', methods=['GET'])
@app.route('/api/test/', methods=['GET'])
def testform():
	return '<form enctype="multipart/form-data" action="/api/upload" method="POST">Please choose a file: <input name="file" type="file" /><br /><input type="submit" value="Upload" /></form>'

