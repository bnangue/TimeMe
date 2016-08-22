<?php
$con=mysqli_connect("mysql1.000webhost.com","a9359727_brice","fXu*br5q","a9359727_time");
if(!$con){
  die("Connection failed: " . mysqli_connect_error());
}

  $creator=$_POST["creator"];   
   
   $hashid=$_POST["hashid"];

 $account_owner=$_POST["account_owner"];
    $account_balance=$_POST["account_balance"];   
   $account_uniqueId=$_POST["account_uniqueId"];
  $account_lastchange=$_POST["account_lastchange"];   
   $account_records=$_POST["account_records"];
  

    $list_creator=$_POST["list_creator"];   
   
   $list_uniqueId=$_POST["list_uniqueId"];
  
   
   $statement= "DELETE FROM GroceryLists WHERE list_creator='$list_creator' AND list_uniqueId ='$list_uniqueId'";
   if(mysqli_query($con, $statement)){

   $statement= "UPDATE FinanceAccounts SET account_owner ='$account_owner', account_balance = '$account_balance' 
   , account_lastchange ='$account_lastchange', account_records ='$account_records'
    WHERE account_uniqueId='$account_uniqueId'";
   if(mysqli_query($con, $statement)){

    $statement= "DELETE FROM CalenderEvents WHERE creator='$creator' AND hashid ='$hashid'";
   if(mysqli_query($con, $statement)){
    echo "Grocery list successfully deleted, account updated,Event deleted";
   }else{ 
        echo "Error deleting event" . mysqli_error($con);

   }

      
   }else{ 
        echo "Error updating account" . mysqli_error($con);

   }

  
   }else{ 
        echo "Error deleting grocery list" . mysqli_error($con);

   }
   
   mysqli_close($con);

?>