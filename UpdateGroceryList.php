<?php
$con=mysqli_connect("mysql1.000webhost.com","a9359727_brice","fXu*br5q","a9359727_time");
if(!$con){
  die("Connection failed: " . mysqli_connect_error());
}

 $list_name=$_POST["list_name"];
   $list_creator=$_POST["list_creator"];
    $list_status=$_POST["list_status"];
     $list_uniqueId=$_POST["list_uniqueId"];  
    $list_contain=$_POST["list_contain"];   
   $list_isShareStatus=$_POST["list_isShareStatus"];
  $list_note=$_POST["list_note"];   
  
    
   $statement= "UPDATE GroceryLists SET list_name ='$list_name',list_status ='$list_status', list_contain = '$list_contain' 
   , list_isShareStatus ='$list_isShareStatus', list_note ='$list_note'
    WHERE list_creator='$list_creator' AND list_uniqueId ='$list_uniqueId'";
   if(mysqli_query($con, $statement)){
    echo "Grocery list successfully updated";
   }else{ 
        echo "Error updating grocery list" . mysqli_error($con);

   }
   
   mysqli_close($con);

?>