import time
import os
from flask import Blueprint, request, jsonify
from werkzeug import secure_filename
from flask.ext.babel import gettext
from app import app
import kookoo


@app.route('/')
@app.route('/index')
def index():
    return "Hello, World!"



@app.route('/api/', methods=['GET'])
@app.route('/api', methods=['GET'])
def index():
    if request.method == 'GET':
        json_results = []
        output = {'greetings':'Welcome to api'}
        json_results.append(output)
    return jsonify(items=json_results)



@app.route('/api/kookoo', methods=['GET','POST'])
@app.route('/api/kookoo/', methods=['GET','POST'])
def ProcessKooKooResponse():
    r = kookoo.Response()
    curtime = str(time.time())
    dic ={'Please say your name':'name'+curtime,
          'Please Say your qualification':'qual'+curtime}
    if request.values.get("event") == "NewCall":
        r.addPlayText("Welcome to Shine dot com registration service")
        phone_number=request.values.get("called_number")
	for i , j in dic.items():
        	r.addPlayText(i)
        	r.addRecord(j)
        r.addPlayText("Thank You")
        r.addHangup()
    return str(r).replace("<Record","<Record format=\"wav\" silence=\"3\" maxduration=\"5\"")
    
@app.route('/api/upload/', methods=['POST'])
@app.route('/api/upload', methods=['POST'])
def upload():
    if request.method == 'POST':
        upload_folder = os.getcwd()+"/uploads/"
        if not os.path.exists(upload_folder):
            os.mkdir(upload_folder)
            print upload_folder
        files = []
        f = request.files['file']
        if f:
            filename = secure_filename(f.filename)
            f.save(os.path.join(upload_folder, filename))
        output = {
            'name': filename,
            'status': 'success',
	    'url' : 'http://107.161.27.22/gsfhack/gsfhack_flaskapp/app/uploads/'+str(filename)
        }
        files.append(output)
    return jsonify(files=files)

@app.route('/api/test', methods=['GET'])
@app.route('/api/test/', methods=['GET'])
def testform():
	return '<form enctype="multipart/form-data" action="/api/upload" method="POST">Please choose a file: <input name="file" type="file" /><br /><input type="submit" value="Upload" /></form>'

