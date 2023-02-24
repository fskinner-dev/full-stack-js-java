window.onload=function () {          
    const url1='http://localhost:8082/api/greeting';  // static url
    const url2='http://localhost:8082/api/getID';     // this queries the db

 
    const otherParams={
		Headers: {
			Accept: 'application.text',
			'Content-Type' :'application/text; charset=UTF-8'
		},
		Method:'GET',
		Cache: 'default',
  		mode: 'cors'
	};   
	
    const displayResult = document.getElementById("displayResult");
    
    fetchText = function() {
		fetch(url2, otherParams)  // this is the fetch api call
	    .then((response) => response.text())
	    .then((data) => {
		    console.log(data)
		   // return data
		   displayResult.innerHTML = "Result: " + data
		})   
	}
	
    getData = function() {
	    fetchText()
    }  
 }
  
 
