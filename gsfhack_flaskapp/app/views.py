import time
import os
from flask import Blueprint, request, jsonify
from werkzeug import secure_filename
from flask.ext.babel import gettext
from app import app
import kookoo
import pickle
import sys, os, json, urllib2, urllib, time


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



USERNAME = "hammadhaleem@gmail.com"
PASSWORD = "9H398339966MFP6"

def transcribe_audio_file(fi):
    url = 'https://api.nexiwave.com/SpeechIndexing/file/storage/' + USERNAME +'/recording/?authData.passwd=' + PASSWORD + '&auto-redirect=true&response=application/json'
    r = requests.post(url, files={'mediaFileData': fi})
    data = r.json()
    transcript = data['text']
    return  "Transcript for "+filename+"=" + transcript

def getfile(url):
	response = urllib2.urlopen(url)
	html = response.read()
	return html

@app.route('/api/kookoo/process', methods=['GET','POST'])
@app.route('/api/kookoo/process/', methods=['GET','POST'])
def ProcessSong():
	lis = pickle.load( open( "save.p", "rb" ) )
	stri = "<ul>"
	for i,j in lis.items():
		stri = stri + "<li><a href='/api/kookoo/process/?no="+str(i)+"'>"+str(i)+"</a></li>"
	if request.values.get("no"):
		dici = lis[request.values.get("no")]
		for k,m in dici.items():
			var  ="http://recordings.kookoo.in/nayyar_vipul/"+m+".wav"
			dici[k]=transcribe_audio_file(getfile(var))
			
		return jsonify(dici)
	else:
		return stri+"</ul>"
		

@app.route('/api/kookoo', methods=['GET','POST'])
@app.route('/api/kookoo/', methods=['GET','POST'])
def ProcessKooKooResponse():
    try:
	lis = pickle.load( open( "save.p", "rb" ) )
    except :
	lis = {}
    r = kookoo.Response()
    curtime = str(time.time())
    dic ={'Please say your name':'name'+curtime,
          'Please Say your qualification':'qual'+curtime}
    phone_number=request.values.get("called_number")
    if request.values.get("event") == "NewCall":
        r.addPlayText("Welcome to Shine dot com registration service")
        phone_number=request.values.get("called_number")
	for i , j in dic.items():
        	r.addPlayText(i)
        	r.addRecord(j)
        r.addPlayText("Thank You")
        r.addHangup()
    dic['proc'] = 0
    lis[phone_number]=dic
    pickle.dump( lis, open( "save.p", "wb" ) )
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

