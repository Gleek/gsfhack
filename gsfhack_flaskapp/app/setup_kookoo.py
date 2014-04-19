from distutils.core import setup
setup(
    name = "kookoo",
    py_modules = ['kookoo'],
    version = "1.0.0",
    description = "KooKoo ML generator",
    author = "KooKoo",
    author_email = "support@kookoo.in",
    url = "http://www.kookoo.in",
    download_url = "http://kookoo.in/download/python",
    keywords = ["kookoo","kookooml"],
    classifiers = [
        "Programming Language :: Python",
        "Operating System :: OS Independent",
        "License :: OSI Approved :: MIT License",
        "Intended Audience :: Developers",
        "Development Status :: 5 - Production/Stable",
        "Topic :: Software Development :: Libraries :: Python Modules",
        "Topic :: Communications :: Telephony"
        ],
    long_description = """\
    Python KooKoo Helper Library
    ----------------------------
    
    DESCRIPTION
    The KooKoo SDK simplifies the process of makes calls to the KooKoo .
   
    USAGE
    To use the KooKoo library, just 'import KooKoo' in the your current py
    file. As shown in example-kookooml.py, 
    
     LICENSE The KooKoo Python Helper Library is distributed under the MIT
    License """ )
