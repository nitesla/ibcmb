/**
 * Sample Ibanking script hook
 * 
 * request_key exists in all hooks to get service request payload
 * 
 */

function calculator(){
	var response = [];
	var tt = "Tester";
	response['control'] = 99999;
	var rs = 66;
	
	var data =  [{"name":"Card Type","value":"VISA"},{"name":"Database","value":"oracle"},{"name":"Welcome Message","value":"Test"},{"name":"Pickup Date","value":"04/27/2017"}];
	data.forEach(function(el){
		print(el['name']);
		if(el['name'].equals('Database')){
			rs = 34.78;; 
		}
		
	}
	
	);
	
	return rs;
}
