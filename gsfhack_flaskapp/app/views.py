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
    if request.values.get("event") == "NewCall":
        r.addPlayText("Welcome to Shine dot com registration service")
        curtime = str(time.time())
        name= curtime+"name"
        qual = curtime+"qual"
        phone_number=request.values.get("called_number")
        r.addPlayText("Please say your name")
        r.addRecord(name)
        r.addPlayText("Please Say your qualification")
        r.addRecord(qual)
        r.addPlayText("Thank You")
        r.addHangup()
    return str(r).replace("<Record","<Record format=\"wav\" silence=\"3\" maxduration=\"5\"")
    
@app.route('/api/upload/', methods=['POST'])
@app.route('/api/upload', methods=['POST'])
def upload():
    if request.method == 'POST':
        upload_folder = "/home/engineer/htdocs/gsfhack/gsfhack_flaskapp/app/uploads/"
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
            'status': 'success'
        }
        files.append(output)
    return jsonify(files=files)


