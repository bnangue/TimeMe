<?php
$con=mysqli_connect("mysql1.000webhost.com","a9359727_brice","fXu*br5q","a9359727_time");
if(!$con){
  die("Connection failed: " . mysqli_connect_error());
}
  $email=$_POST["email"];   

    $currentEmail=$_POST["currentEmail"];

     $currentPassword=$_POST["currentPassword"];
    

  
   
   $statement= "UPDATE User SET friendlist ='$email' WHERE email='$currentEmail' AND password ='$currentPassword'";
   if(mysqli_query($con, $statement)){
    $statement= "UPDATE User SET friendlist ='$currentEmail' WHERE email='$email'";
   if(mysqli_query($con, $statement)){
    echo "friendlist updated";
   }else{ 
        echo "Error updating friend user data" . mysqli_error($con);

   }
   
   }else{ 
        echo "Error updating user data" . mysqli_error($con);

   }
   
   mysqli_close($con);

?>