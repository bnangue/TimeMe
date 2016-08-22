<?php
$con=mysqli_connect("mysql1.000webhost.com","a9359727_brice","fXu*br5q","a9359727_time");
  $email=$_POST["email"];   
   $password=$_POST["password"];
   $firstname=$_POST["firstname"];   
   $lastname=$_POST["lastname"];
    $gcmregid=$_POST["gcmregid"];
  
   
   $statement="INSERT INTO User (email,password,firstname,lastname,gcmregid) 
   VALUES('$email','$password','$firstname','$lastname','$gcmregid')";

  if(mysqli_query($con, $statement)){
    echo "User registered successfully";
  }else{
    echo "Error registring user";
  }
   
   mysqli_close($con);

?>