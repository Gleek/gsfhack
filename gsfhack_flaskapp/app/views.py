import time
import os
from flask import Blueprint, request, jsonify
from werkzeug import secure_filename
from app import app
import kookoo
import pickle
import sys, os, json, urllib2, urllib, time
import requests
import requests, json, os.path
import mechanize, cookielib, urllib2
from bs4 import BeautifulSoup

def get_xml_data(filename):
	base_url = 'http://resumeparser.rchilli.com/'
	cookies = 'JSESSIONID=FD1B3917F62E1691D50F96FFF288B0EB; yyy=%20--%20-%20; UName=123; UEmail="sharmapankaj1992@gmail.com"; UPhone=1234567890; UVarified=yes'
	user_agent = 'Mozilla/5.0 (X11; Ubuntu; Linux i686; rv:25.0) Gecko/20100101 Firefox/25.0'
	# Browser
	br = mechanize.Browser()

	# Cookie Jar
	cj = cookielib.LWPCookieJar()
	br.set_cookiejar(cj)

	# Browser options
	br.set_handle_equiv(True)
	br.set_handle_gzip(True)
	br.set_handle_redirect(True)
	br.set_handle_referer(True)
	br.set_handle_robots(False)

	# Follows refresh 0 but not hangs on refresh > 0
	br.set_handle_refresh(mechanize._http.HTTPRefreshProcessor(), max_time=1)
	br.addheaders = [('User-Agent', user_agent), ('Cookie', cookies)]
	br.open('http://resumeparser.rchilli.com/RchilliDemo.jsp?method=2')
	br.select_form(nr=0)
	br.form.find_control(name='fileUpload', type='file').add_file(
	    open(filename, 'rb'), 'application/pdf', os.path.basename(filename))
	br.form.set_all_readonly(False)
	br.addheaders = [('User-Agent', user_agent), ('Cookie', cookies)]
	br.submit()
	out = br.response().read()
	# print out
	soup = BeautifulSoup(out)
	xml_file = base_url + soup.find('span', {'id': 'filePath'}).text.strip()
	# print xml_file
	response = urllib2.urlopen(xml_file)
	return response.read()


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
def getspeechdata(var):
	
	stri = "/home/engineer/htdocs/gsfhack/"+str(var)+".wav"
	stril ="/home/engineer/htdocs/gsfhack/"+str(var)
	ms = 'curl "https://api.att.com/speech/v3/speechToText" \
    --header "Authorization: Bearer WhOv12MXe4Wlduezi7M3hioHUrko6fO9" \
    --header "Accept: application/json" \
    --header "Content-Type: audio/wav" \
    --data-binary @'+stri+' \
    --request POST   > '+stril+'.txt'
	os.system(ms)

	return var
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
			
			dici[k] = getspeechdata(m)
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
    

@app.route('/api/process/pdf', methods=['GET','POST'])
@app.route('/api/process/pdf/', methods=['GET','POST'])

def processPdf():
	stri = "<ul>"
        lis = os.listdir("/home/engineer/htdocs/gsfhack/uploads/")
	for i in lis:
		stri = stri+"<li><a href='/api/process/pdf/?name="+str(i)+"'>"+str(i)+"</a></li>"
		
	if request.values.get("name"):
                name = request.values.get("name")
                stri ="/home/engineer/htdocs/gsfhack/uploads/"
                s=str(stri+name)
                return get_xml_data(s)
		
	else:
		return stri+"</ul>"
	 
        

@app.route('/api/upload/', methods=['POST'])
@app.route('/api/upload', methods=['POST'])
def upload():
    if request.method == 'POST':
        upload_folder = os.path.join(os.getcwd(),'uploads/')
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
	    'url' : 'http://107.161.27.22/gsfhack/uploads/'+str(filename),
	    'path': upload_folder+str(filename)
	}
	if filename.endswith('.png') or filename.endswith('.jpg') or filename.endswith('.gif'):
		print filename 
		os.system("pwd")
		stri = "cd uploads && wget http://107.161.27.22/gsfhack/uploads/process.php?file="+str(filename)+" -O "+str(filename)+".rtf && cd .."
		os.system(stri)
		os.system("pwd")
        files.append(output)
    return jsonify(files=files)
@app.route('/api/test', methods=['GET'])
@app.route('/api/test/', methods=['GET'])
def testform():
	return '<form enctype="multipart/form-data" action="/api/upload" method="POST">Please choose a file: <input name="file" type="file" /><br /><input type="submit" value="Upload" /></form>'

