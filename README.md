## Advent of Code Spring Boot

Yeah I got bored and decided to try and tackle this years advent of code with a springboot rest api.

Once ran locally this service will return data including 

{"puzzletext","puzzleinput","answer"}

The below Endpoint path accepts days listed in advent of code.

http://localhost:8081/2024/day/1

### Note

Be aware that to get puzzle input you must pass a session token as advent of code inputs are different per each user.
Once you have and or know you session token you can set it in the application.properties on "session.token"