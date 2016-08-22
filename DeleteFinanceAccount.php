<?php
$con=mysqli_connect("mysql1.000webhost.com","a9359727_brice","fXu*br5q","a9359727_time");
if(!$con){
  die("Connection failed: " . mysqli_connect_error());
}
      
   
   $account_uniqueId=$_POST["account_uniqueId"];
  
   
   $statement= "DELETE FROM FinanceAccounts WHERE account_uniqueId='$account_uniqueId'";
   if(mysqli_query($con, $statement)){
    echo "Account successfully deleted";
   }else{ 
        echo "Error deleting account" . mysqli_error($con);

   }
   
   mysqli_close($con);

?>