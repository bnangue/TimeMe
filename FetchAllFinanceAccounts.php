<?php
$con=mysqli_connect("mysql1.000webhost.com","a9359727_brice","fXu*br5q","a9359727_time");
if(!$con){
  die("Connection failed: " . mysqli_connect_error());
}

      $account_owner=$_POST["account_owner"];

   $query="SELECT * FROM FinanceAccounts WHERE account_owner='$account_owner'";
   $result= mysqli_query($con,$query);
   
   
   $eventd=array();
   while($row= mysqli_fetch_assoc($result))
   {
   $eventd[]=$row;
   }
   
   echo json_encode($eventd);
      mysqli_free_result($result);

   mysqli_close($con);

?>