<?php
$con=mysqli_connect("mysql1.000webhost.com","a9359727_brice","fXu*br5q","a9359727_time");
if(!$con){
  die("Connection failed: " . mysqli_connect_error());
}

 $account_owner=$_POST["account_owner"];
    $account_balance=$_POST["account_balance"];   
   $account_uniqueId=$_POST["account_uniqueId"];
  $account_lastchange=$_POST["account_lastchange"];   
   $account_records=$_POST["account_records"];

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
    $statement1= "UPDATE FinanceAccounts SET account_owner ='$account_owner', account_balance = '$account_balance' 
   , account_lastchange ='$account_lastchange', account_records ='$account_records'
    WHERE account_uniqueId='$account_uniqueId'";
   if(mysqli_query($con, $statement1)){
    echo "Account and Grocery list successfully updated";
   }else{ 
        echo "Error updating account" . mysqli_error($con);

   }
   }else{ 
        echo "Error updating grocery list" . mysqli_error($con);

   }
   
   mysqli_close($con);

?>