

<?PHP

	
if( isset($_POST["TAG"])){
	

		$data = json_decode( $_POST["TAG"]);
	
	
		if ($tag = $data->tag != null){
			
				//connect to database			
				if ($db = new mysqli("127.0.0.1","root","ibm7094","myapp")){
					$con_status ["Connected to DB:"] = "true";
				//	echo json_encode($con_status);

				switch ($txreason = $data->tag){
				
				case "login": 
				$username = $data->username;
				$password = $data->password;
				$query_user = "select username,passwd from login where username=\"$username\" and passwd=md5(\"$password\") limit 1";
			
				/* query user and pass from db*/
				if($result = $db->query($query_user) ){
					
					while ($row = $result->fetch_assoc()){
						$result_user = $row["username"];
						$result_pass = $row["passwd"];
					}
				
					//validate user
					if( $data->username == $result_user &&  $result_pass == md5($data->password)) { 
						
						$response ["tag"] = "login";
						$response ["success"] = 1;
						$response ["error"] = 0;
						$response ["username"] = $data->username;	
												
						echo json_encode($response); // login success, send json to android client
					
					} else if ($data->username != $result_user || $result_pass != md5($data->password)){
						
						$response ["tag"] = "login";
						$response ["success"] = 0;
						$response ["error"] = 1; //error code 1 = username or password not found
						$response ["username"] = $data->username;
						
						echo json_encode($response);// login failed, send json to android client
											
								
					}
								
					
				}
								
				break;
				
				
			}
					
					
					
					
					
		} 		
		//close db connection
		 if($db->close()){
			 $close ["DB connection"] = "closed";
			// echo json_encode($close);
		 }
			
	}
			
}

?>
