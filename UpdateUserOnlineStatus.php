<?php
$con=mysqli_connect("mysql1.000webhost.com","a9359727_brice","fXu*br5q","a9359727_time");
if(!$con){
  die("Connection failed: " . mysqli_connect_error());
}

  $email=$_POST["email"];   
   $password=$_POST["password"];
   $onlinestatus=$_POST["onlinestatus"];
 
    
   
   $statement= "UPDATE User SET onlinestatus ='$onlinestatus' WHERE email='$email' AND password ='$password'";
   if(mysqli_query($con, $statement)){
    
    echo "Status successfully updated";
   }else{ 
        echo "Error updating user status" . mysqli_error($con);

   }
   
   mysqli_close($con);

?>