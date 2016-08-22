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
   
  
    
   $statement= "UPDATE FinanceAccounts SET account_owner ='$account_owner', account_balance = '$account_balance' 
   , account_lastchange ='$account_lastchange', account_records ='$account_records'
    WHERE account_uniqueId='$account_uniqueId'";
   if(mysqli_query($con, $statement)){
    echo "Account successfully updated";
   }else{ 
        echo "Error updating account" . mysqli_error($con);

   }
   
   mysqli_close($con);

?>