 <?php
$con=mysqli_connect("mysql1.000webhost.com","a9359727_brice","fXu*br5q","a9359727_time");
if(!$con){
  die("Connection failed: " . mysqli_connect_error());
}

 $name=$_POST["name"];
   $description=$_POST["description"];
    $price=$_POST["price"];   
   $specification=$_POST["specification"];
  $unique_id=$_POST["unique_id"];   
  
  
   
   $statement= mysqli_prepare($con, "INSERT INTO ItemTable(name ,description , price ,specification,unique_id) VALUES(?,?,?,?,?)");
   mysqli_stmt_bind_param( $statement, "sssss",$name,$description,$price,$specification,$unique_id);
    mysqli_stmt_execute($statement);
	
  $check=mysqli_stmt_affected_rows($statement);

  if($check ==1){
    echo "Item added successfully";
  }else{
    echo "Error adding Item";
  }
   mysqli_stmt_close($statement);
 
    
   
   mysqli_close($con);

?>