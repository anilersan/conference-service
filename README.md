conference-service app is remotely deployed on https://conference-service-anil-ersan.herokuapp.com

conference service methods:

POST  /createConference:          scheduling the events and tracks and insert them to postgre db

GET   /conferenceSchedule:        returns generated all tracks

GET   /conferenceSchedule/{id}:   returns track with id

DELETE /conferenceSchedule:       delete all tracks

DELETE /conferenceSchedule/{id}:  delete track by id

PostGreSQL properties are given as System Environment Variable and under in src\main\resources\application.properties path.