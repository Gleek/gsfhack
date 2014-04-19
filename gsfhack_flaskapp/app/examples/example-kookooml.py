#!/usr/bin/env python

import kookoo

# The same XML can be created above using the convenience methods
r = kookoo.Response()
r.addPlayText("Hello World")
r.addPlayAudio("http://www.mp3.com")
r.addRecord("test.wav",maxDuration="30",silence="3");
print r


""" outputs:
<Response>
	<PlayText>Hello World</PlayText>
	<PlayAudio>http://www.mp3.com</PlayAudio>
	<Record maxduration="30" silence="3">test.wav</Record>
</Response>
"""


# ===========================================================================
# Using Conference
r = kookoo.Response()
r.addPlayText("Welcome to the conference")
r.addConference("12345");
print r

""" Outputs:
<Response>
	<PlayText>Welcome to the conference</PlayText>
	<Conference>12345</Conference>
</Response>
"""


# ===========================================================================
# Collecting user input
r = kookoo.Response()
g = r.append(kookoo.CollectDtmf(maxDigits=1))
g.append(kookoo.PlayText("Press 1"))
print r

""" outputs:

<Response>
	<CollectDtmf l="1">
		<PlayText>Press 1</PlayText>
	</CollectDtmf>
</Response>


"""


