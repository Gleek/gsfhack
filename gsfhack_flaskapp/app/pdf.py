import sys
from pdfminer.pdfinterp import PDFResourceManager, PDFPageInterpreter
from pdfminer.pdfpage import PDFPage
from pdfminer.converter import XMLConverter, HTMLConverter, TextConverter
from pdfminer.layout import LAParams
from cStringIO import StringIO
import re, string

skills = ['Adobe Illustrator', 'Adobe InDesign', 'Adobe Photoshop', 'Analytics', 'Android', 'APIs', 'Art Design', 'AutoCAD', 'Backup Management', 'C', 'C++', 'Certifications', 'Client Server', 'Client Support', 'Configuration', 'Content Management Systems', 'Corel Draw', 'Corel Word Perfect', 'CSS', 'Data Analytics', 'Desktop Publishing', 'Design', 'Diagnostics', 'Documentation', 'End User Support', 'Email', 'Engineering', 'Excel', 'FileMaker Pro', 'Fortran', 'Graphic Design', 'Hardware', 'Help Desk', 'HTML', 'Implementation', 'Installation', 'Internet', 'iOS', 'iPhone', 'Linux', 'Java', 'Javascript', 'Mac', 'Matlab', 'Maya', 'Microsoft Excel', 'Microsoft Office', 'Microsoft Outlook', 'Microsoft Publisher', 'Microsoft Word', 'Microsoft Visual', 'Mobile', 'MySQL', 'Networks', 'Open Source Software', 'Oracle', 'Perl', 'PHP', 'Presentations', 'Processing', 'Programming', 'PT Modeler', 'Python', 'Quick Books Pro', 'Ruby', 'Shade', 'Software', 'Spreadsheet', 'SQL', 'Support', 'Systems Administration', 'Troubleshooting', 'Unix', 'Web Page Design', 'Windows', 'Word Processing', 'XML', 'XHTML','adobe illustrator', 'adobe indesign', 'adobe photoshop', 'analytics', 'android', 'apis', 'art design', 'autocad', 'backup management', 'c', 'c++', 'certifications', 'client server', 'client support', 'configuration', 'content management systems', 'corel draw', 'corel word perfect', 'css', 'data analytics', 'desktop publishing', 'design', 'diagnostics', 'documentation', 'end user support', 'email', 'engineering', 'excel', 'filemaker pro', 'fortran', 'graphic design', 'hardware', 'help desk', 'html', 'implementation', 'installation', 'internet', 'ios', 'iphone', 'linux', 'java', 'javascript', 'mac', 'matlab', 'maya', 'microsoft excel', 'microsoft office', 'microsoft outlook', 'microsoft publisher', 'microsoft word', 'microsoft visual', 'mobile', 'mysql', 'networks', 'open source software', 'oracle', 'perl', 'php', 'presentations', 'processing', 'programming', 'pt modeler', 'python', 'quick books pro', 'ruby', 'shade', 'software', 'spreadsheet', 'sql', 'support', 'systems administration', 'troubleshooting', 'unix', 'web page design', 'windows', 'word processing', 'xml', 'xhtml','C++','C','C/C++','Python','PHP']
def pdfparser(data):

    fp = file(data, 'rb')
    rsrcmgr = PDFResourceManager()
    retstr = StringIO()
    codec = 'utf-8'
    laparams = LAParams()
    device = TextConverter(rsrcmgr, retstr, codec=codec, laparams=laparams)
    interpreter = PDFPageInterpreter(rsrcmgr, device)

    for page in PDFPage.get_pages(fp):
        interpreter.process_page(page)
        data =  retstr.getvalue()

    return data
def get_skills(dic,s):
	exclude = set(string.punctuation)
	table = string.maketrans("","")
	regex = re.compile('[%s]' % re.escape(string.punctuation))
	ch = ''.join(ch for ch in s if ch not in exclude)
   	s = ch.split(" ")
    	print s 
    	if s in skills : 
		print s
		dic['skills'].append(stri)
    	return dic
  


dic = {}
dic['skills']=[]
if __name__ == '__main__':
    s= pdfparser(sys.argv[1]).split("\n")
    l = []
    for i in s : 
	if len(i) > 1 :
		l.append(re.sub(r'\s+',' ',i))
    #print l
    
    for i in l :
	dic = get_skills(dic,i)
    print dic 
    
