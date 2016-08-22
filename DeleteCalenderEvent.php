<?php
$con=mysqli_connect("mysql1.000webhost.com","a9359727_brice","fXu*br5q","a9359727_time");
if(!$con){
  die("Connection failed: " . mysqli_connect_error());
}
    $creator=$_POST["creator"];   
   
   $hashid=$_POST["hashid"];
  
   
   $statement= "DELETE FROM CalenderEvents WHERE creator='$creator' AND hashid ='$hashid'";
   if(mysqli_query($con, $statement)){
    echo "Event successfully deleted";
   }else{ 
        echo "Error deleting event" . mysqli_error($con);

   }
   
   mysqli_close($con);

?>