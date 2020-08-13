DATABASE SIDE
1. Install node JS and Mongo DB.
2. Download the code.
3. Go to the directory of database.
4. Write the Command: node index.js
5. Try accessing localhost:3001/save with json file name via POSTMAN
Example of Json File: 
{
    "MacAddress" : "09:2B:44:51:3A:B7",
	"ArrayOfSessions" : [{
		"StartTime" : "2020-01-01 00:00:01",
		"EndTime" : "2020-01-01 00:00:03",
		"ArrayOfRecords" : [{
					"TimeStamp" : "2020-01-01 00:00:02",
					"Latitude" : 2.0,
					"Longitude" : 2.0,
					"Bearing" : 2.0,
					"Acceleration" : 2.0
				}],
		"ArrayOfCatches" : [{
					"TimeStamp" : "2020-01-01 00:00:02",
					"Latitude" : 2.0,
					"Longitude" : 2.0,
					"RelatedPhoto" : "filename3.jpg"
					}]
		}]
}

Name of the Database: Logs
Port: 3001

