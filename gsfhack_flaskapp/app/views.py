import os
from flask import Blueprint, request, jsonify
from werkzeug import secure_filename
from flask.ext.babel import gettext
from app import app

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
