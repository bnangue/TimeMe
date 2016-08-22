 <?php
$con=mysqli_connect("mysql1.000webhost.com","a9359727_brice","fXu*br5q","a9359727_time");

if(!$con){
  die("Connection failed: " . mysqli_connect_error());
}

 $account_name=$_POST["account_name"];
   $account_owner=$_POST["account_owner"];
    $account_balance=$_POST["account_balance"];   
   $account_uniqueId=$_POST["account_uniqueId"];
  $account_lastchange=$_POST["account_lastchange"];   
   $account_records=$_POST["account_records"];
   
  
   
   $statement= "INSERT INTO FinanceAccounts(account_name ,account_owner , account_uniqueId ,account_balance,account_lastchange,account_records)
    VALUES('$account_name','$account_owner','$account_uniqueId','$account_balance','$account_lastchange','$account_records') ON DUPLICATE KEY 
    UPDATE account_owner='$account_owner', account_balance='$account_balance',account_lastchange='$account_lastchange', account_records='$account_records'";
  
	  if(mysqli_query($con, $statement)){
    echo "Account added successfully";
  }else{
    echo "Error adding Account";
  }
   
   mysqli_close($con);

?>