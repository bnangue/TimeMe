<?php
$con=mysqli_connect("mysql1.000webhost.com","a9359727_brice","fXu*br5q","a9359727_time");
   if(!$con){
  die("Connection failed: " . mysqli_connect_error());
}
  
  $email=$_POST["email"];   
 
   $query= "SELECT * FROM User  WHERE email = '$email'";

   $user=array();
  $result= mysqli_query($con,$query);
   if($result){

    while($row= mysqli_fetch_assoc($result))
   {
   $user[]=$row;
   }
   
   echo json_encode($user);
      mysqli_free_result($result);
      
   }else{
     echo "Error fetching user" . mysqli_error($con);
   }
   
   mysqli_close($con);

?>