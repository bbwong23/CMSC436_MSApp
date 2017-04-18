# CMSC436_MSApp
This app is meant to help doctors monitor the progress of Multiple Sclerosis patients testing a drug to minimize the symptoms of MS.

*Note:* User must select their id first before taking the tests to properly submit their trial data/scores 


## Tap Test 
User taps a still button as many times possible within a 10 second time frame 

**_Output:_** Total number of taps


## Spiral Test
User traces a spiral on the screen as accurate and fast as possible

**_Output:_** [score =  %accurate \*(.8) + %time remaining \*(.2)] and a screenshot of the spiral drawn over the original spiral


## Level Test
User holds the device flat on hand and tries to keep it steady for 10 seconds, maintaining the ball on the screen in the center green circle

**_Output:_** [score = sum of points for each ball movement/ total ball movement; where green = 3 points, yellow = 2 points, red = 1 point] and a screenshot of the screen that captures the path of the ball movement during test


## Balloon Test
10 circles will appear randomly on the screen and user will have to pop them as soon as they see the circle

**_Output:_** Average response time (seconds)


## Curl Test
User will hold phone in hand and stretch arm out so it is parallel to floor. From there, user will curl arm 90 degrees until a beep is heard and bring it back down. User will repeat this 10 times as fast as possible.

**_Output:_** Average speed per curl (seconds)


## Sway Test 
User will place phone in head strap and stand up right. Once the phone has been callibrated, user will close his/her eyes and try to stand still for 10 seconds.

**_Output:_** Same output as level test


## Walking Test 
User walks 25 steps indoors as fast as possible

**_Output:_** Average feet walked per second
