<?php
$con=mysqli_connect("mysql1.000webhost.com","a9359727_brice","fXu*br5q","a9359727_time");
if(!$con){
  die("Connection failed: " . mysqli_connect_error());
}
  $email=$_POST["email"];   
   $password=$_POST["password"];
    $currentEmail=$_POST["currentEmail"];

     $currentPassword=$_POST["currentPassword"];
     $picture=$_POST["picture"];
      $firstname=$_POST["firstname"];   
   $lastname=$_POST["lastname"];

  
   
   $statement= "UPDATE User SET email ='$email',password ='$password', picture = '$picture' 
   , firstname ='$firstname', lastname ='$lastname' WHERE email='$currentEmail' AND password ='$currentPassword'";
   if(mysqli_query($con, $statement)){
    echo "User data successfully updated";
   }else{ 
        echo "Error updating user data" . mysqli_error($con);

   }
   
   mysqli_close($con);

?>