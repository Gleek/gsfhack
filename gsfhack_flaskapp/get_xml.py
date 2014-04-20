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

	# Want debugging messages?
	# br.set_debug_http(True)
	# br.set_debug_redirects(True)
	# br.set_debug_responses(True)

	# User-Agent (this is cheating, ok?)
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

	# print br.response().read()
	# br.form = mechanize.HTMLForm("http://resumeparser.rchilli.com/RchilliDemo.jsp?method=2",
	# 	method='POST', enctype='multipart/form-data')
	# br.form.new_control('file', 'fileUpload', {'id': 'fileUpload'})
	# br.form.new_control('image', 'ImageButton1', {'id': 'ImageButton1'})
	# br.form.add_file(open('Safiyat.pdf'), 'application/pdf', 'Safiyat.pdf', id='fileUpload')
	# br.form.fixup()
	
# print get_xml_data('/home/om/Downloads/Safiyat.pdf')

# browse()
