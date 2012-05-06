ADC
===
ADC, Android Development Course, is a series of Android development classes taught by Evan Liu (hmisty) in Beihang University Software College, Beijing, China.

ADC Happy New Year
---
ADC Happy New Year is the 7th homework of the ADC.
The source code here is a partially finished Android application. The goal for the students is to complete the application as required.

Homework
===

Requirements
---
There are five requirements of the homework:

### Optimize the method readContacts in HappyNewYearActivity
Due to nested loops in the method readContacts, it will execute very slowly especially when you have many contacts in your phone book. Please think about how to optimize to improve the execution speed.

### Set the interval to 5 seconds between sending each two SMS
Move the execution of sending SMS code to non-UI thread.
Then set the interval to 5 seconds between sending each two SMS.

### Improve the status bar notification
Implement a progress bar in the status bar notification to show the current SMS sending progress, like below:
+--------------------+
|HappyNewYear        |
|********33/45**=====|
+--------------------+
33/45 means 33 sent, 45 total.

### Use background colors to distinguish the status of each SMS in SendListActivity
Improve SendListActivity and use background colors to distinguish the status of each SMS, for example: to be sent = default (black); sending = light blue; sent = light yellow; delivered = light green; failed = light red; already sent, no need to send again = light gray.

### Save the status of SMS to database to avoid sending more than once
Use the sqlite database to save the status of each SMS. Don't send the SMS more than once otherwise you will be suspected to be a spammer :)

Submission
---
  * Source codes
  * Document
  * Screenshots

HowTo
---
Fork, complete, commit, push and send me a pull request at hmisty/adc_happynewyear.

The MIT License
---
Copyright (c) 2012
Evan (Qingyan) Liu

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
